import com.tmax.tibero.jdbc.driver.TbConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class test {
	public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IOException {

		DBConnection dbinfo = new DBConnection();
		SqlJob sj = new SqlJob();
		TestCaseRegsiter tr = new TestCaseRegsiter();
		ArrayList<TestCasePrecondition> tcPre = new ArrayList<TestCasePrecondition>();
		ArrayList<TestCaseStep> tcStep = new ArrayList<TestCaseStep>();
		Validation vd = new Validation();

		Connection conn = null;
		ResultSet rs = null;
		if (args.length <= 0 || args.length > 3) {
			System.out.println("java " + Thread.currentThread().getStackTrace()[1].getClassName()
					+ " [cfg file] [createTable|runTestcase] [4]");
			System.exit(0);
		}

		Path path = Paths.get(args[0]);
		List<String> list = new ArrayList<String>();
		list = Files.readAllLines(path);

		/*
		 * java 또는 shell 을 수행해야 할 때 Process theProcess = null; BufferedReader inStream =
		 * null;
		 * 
		 * theProcess = Runtime.getRuntime().exec("java IMS159927_Tibero"); inStream =
		 * new BufferedReader(new InputStreamReader(theProcess.getInputStream()));
		 * System.out.println(inStream.readLine()); theProcess.destroy();
		 * System.exit(0);
		 */

		String src_url = null, src_driver = null, src_id = null, src_passwd = null, tar_url = null, tar_driver = null,
				tar_id = null, tar_passwd = null;

		for (String readLine : list) {
			if (readLine.contains("SRC_URL"))
				src_url = readLine.split("=")[1];
			else if (readLine.contains("SRC_DRIVER"))
				src_driver = readLine.split("=")[1];
			else if (readLine.contains("SRC_ID"))
				src_id = readLine.split("=")[1];
			else if (readLine.contains("SRC_PASSWD"))
				src_passwd = readLine.split("=")[1];
			else if (readLine.contains("TAR_URL"))
				tar_url = readLine.split("=")[1];
			else if (readLine.contains("TAR_DRIVER"))
				tar_driver = readLine.split("=")[1];
			else if (readLine.contains("TAR_ID"))
				tar_id = readLine.split("=")[1];
			else if (readLine.contains("TAR_PASSWD"))
				tar_passwd = readLine.split("=")[1];
		}

		try {
			long start = System.currentTimeMillis();
			Connection connTarget = null;
			// index 0 : testlink, 1 : srcdb, 2 : tardb
			dbinfo.setDbInfo(0, "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.2.128:3307/bitnami_testlink", "root",
					"testlink");
			dbinfo.setDbInfo(1, src_driver, src_url, src_id, src_passwd);
			dbinfo.setDbInfo(2, tar_driver, tar_url, tar_id, tar_passwd);

			// conn 변수에 값 대입되는 시점에 DB에 접속함
			conn = dbinfo.getConnection(0);
			rs = sj.selectTCversion(conn);
			tr.addPrecondition(tcPre, rs);
			closeConnection(conn);

			// runpre라고 keyword 입력시 해당 구문 수행
			if (args.length > 0 && args[1].equals("createTable")) {
				conn = dbinfo.getConnection(1);
				connTarget = dbinfo.getConnection(2);
				for (TestCasePrecondition i : tcPre) {
					sj.executePreQry(conn, i.getPrecondition());
					sj.executePreQry(connTarget, i.getPrecondition());
				}
				closeConnection(conn);
				closeConnection(connTarget);
			}
			// runaction이라고 keyword 입력시 해당 구문 수행
			if (args.length > 0 && args[1].equals("runTestcase")) {
				int xid = 0, tsn = 0;

				conn = dbinfo.getConnection(0);
				// loop를 전부 for each 형태로 작성
				// tcPre에서 id를 추출하여 id에 해당하는 action 수행
				for (TestCasePrecondition i : tcPre) {
					rs = sj.selectTCstep(conn, i.getId());
					tr.addStep(tcStep, rs);
				}
				closeConnection(conn);

				conn = dbinfo.getConnection(1);
				int actionIndex = 0;

				for (int i = 0; i < tcStep.size(); i++) {
					System.out.print(tcPre.get(i).getTcName());
					for (int j = 0; j < tcStep.get(i).getActionSize(); j++) {
						boolean stepValidation = true;
						// System.out.println(tcStep.get(i).getAction().get(j));
						if (tcStep.get(i).getAction().get(j).contains("TBL")) {
							actionIndex++;
							// 쿼리 수행 시, 에러(음수값)로 인해서 적용되지 않았다면 TX정보 조회하지 않기 위한 구문
							if (sj.executeActionQry(conn, tcStep.get(i).getAction().get(j)) < 0) {
								// System.out.print(", SQL : "+ tcStep.get(i).getAction().get(j));
								continue;
							}
							String txInfo = sj.getTxInfo(conn);
							xid = Integer.parseInt(txInfo.split("/")[0]);
							tsn = Integer.parseInt(txInfo.split("/")[1]);
							// 동기화 테이블 추가();
							vd.registerSyncTable(tcStep.get(i).getAction().get(j));

						} else if (tcStep.get(i).getAction().get(j).contains("ROLLBACK")) {
							conn.rollback();
							vd.clearSyncTable();
						} else {
							if (vd.getSyncTableList().size() == 0) {
								// System.out.println("* 확인할 동기화 테이블 없음 *");
								continue;
							}
							conn.commit();
							connTarget = dbinfo.getConnection(2);
							// Thread.sleep(5000);
							// 프로싱크 동기화 여부 확인 (last TSN 조회될 때가지)
							int sleepCnt = 0;
							do {
								// System.out.println("SELECT TSN FROM prosync_t2t.prs_lct_t0 WHERE xid = " +
								// xid + " AND tsn >= " + tsn);
								if (args[2].equals("4")) {
									rs = connTarget.createStatement()
											.executeQuery("SELECT tsn FROM prosync_t2t.prs_lct WHERE tsn >= " + tsn);
								} else if (args[2].equals("3")) {
									rs = connTarget.createStatement().executeQuery(
											"SELECT last_commit_tsn FROM tbsync_t2t.tbsync_last_commit_tsn WHERE last_commit_tsn >= "
													+ tsn);
								} else {
									rs = connTarget.createStatement()
											.executeQuery("SELECT tsn FROM prosync_t2t.prs_lct_t0 WHERE xid = " + xid
													+ " AND tsn >= " + tsn);
								}
								Thread.sleep(500);
								sleepCnt++;
								if (sleepCnt == 1200) {
									System.out.println("\nPRS_LCT 테이블 동기화 체크가 되지 않음, 동기화 되지 않는 상태로 판단하여 프로그램 종료");
									System.exit(0);
								}
							} while (!rs.isBeforeFirst());

							for (String tbl : vd.getSyncTableList()) {
								stepValidation &= vd.validateSyncTable(conn, connTarget, tbl);
							}
							// commit 까지 step 단위로 case pass/fail 입력
							for (int k = 0; k <= actionIndex; k++) {
								tcStep.get(i).addStepValidation(stepValidation);
							}
							actionIndex = 0;
							vd.clearSyncTable();
							// System.out.println("해당 stepValidation : " + stepValidation);
						}
						closeConnection(connTarget);
						tcStep.get(i).setCaseValidation(stepValidation);
					}
					System.out.print(", caseValidation : " + tcStep.get(i).getCaseValidation());
					if (!tcStep.get(i).getCaseValidation()) {
						System.out.print(" index :");
						for (int l = 0; l < tcStep.get(i).getStepValidation().size(); l++) {
							if (!tcStep.get(i).getStepValidation().get(l)) {
								System.out.print(" " + l);
							}
						}

					}
					System.out.println("");
				}
				closeConnection(conn);
			}
			long end = System.currentTimeMillis();
			System.out.println("실행 시간 : " + (end - start) / 1000.0 + "(초)");
		} catch (

		SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
	}

	private static void closeConnection(final Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}
}

class Validation {
	// current tsn 조회
	// lct 테이블에 동기화 되었는지 여부 확인
	// src/tar 정합성 비교
	private ArrayList<String> syncTable = new ArrayList<String>();
	private ResultSet rs = null;

	public void clearSyncTable() {
		this.syncTable.clear();
	}

	public void runValidation() {
		System.out.println("a");
	}

	public ArrayList<String> getSyncTableList() {
		return this.syncTable;
	}

	public void registerSyncTable(String action) {
		String syncTableSplit = action.substring(action.indexOf("TBL"));
		syncTableSplit = syncTableSplit.split(" ")[0].split("\\(")[0].split("\\,")[0].split(";")[0];
		if (!syncTable.contains(syncTableSplit)) {
			syncTable.add(syncTableSplit);
		}
	}

	public int getRowCount(Connection conn, String tbl) throws SQLException {
		rs = conn.createStatement().executeQuery("select count(*) as count from " + tbl);
		rs.next();
		return rs.getInt("count");
	}

	public boolean validateSyncTable(Connection conn, Connection connTarget, String tbl) throws SQLException {
		int srcRowCount = this.getRowCount(conn, tbl);
		int tarRowCount = this.getRowCount(connTarget, tbl);
		boolean flag = true;

		if (srcRowCount != tarRowCount) {
			System.out.println(tbl + " 소스/타겟 ROW 불일치!!");
			return false;
		} else if (srcRowCount == 0) {
		} else {
			ResultSet rs1 = conn.createStatement().executeQuery("select * from " + tbl + " order by 1");
			ResultSet rs2 = connTarget.createStatement().executeQuery("select * from " + tbl + " order by 1");
			ResultSetMetaData metaInfo1 = rs1.getMetaData();
			ResultSetMetaData metaInfo2 = rs2.getMetaData();

			if (metaInfo1.getColumnCount() != metaInfo2.getColumnCount()) {
				System.out.println("테이블의 컬럼 개수 정보가 일치하지 않음 : " + tbl);
				return false;
			}
			// 여러 row에 대해서 컬럼값 비교, 하나의 컬럼이라도 정합성이 다를 경우 나머지 row는 확인하지 않음
			while (rs1.next() && rs2.next() && flag) {
				for (int l = 1; l <= metaInfo1.getColumnCount(); l++) {
					if (rs1.getString(l) == null || rs1.getString(l).equals("") == true) {
					} else if (rs1.getString(l).equals(rs2.getString(l))) {
					} else {
						System.out.println(metaInfo1.getColumnName(l) + "컬럼 데이터 불일치 SRC : " + rs1.getString(l)
								+ ", TAR : " + rs2.getString(l));
						return false;
					}
				}
			}
		}
		return true;
	}

}

//testlink에서 precondition과 step을 가져오는 클래스
//step = actions + expected results를 합친 의미
//actions = testlink에서 step actions
class TestCaseRegsiter {
	private ResultSet rs = null;

	// testlink에서 precondition을 가져온다.
	public ArrayList<TestCasePrecondition> addPrecondition(ArrayList<TestCasePrecondition> tcPre, ResultSet rs)
			throws SQLException {
		this.rs = rs;
		while (this.rs.next()) {
			// 내용이 있으면 테스트케이스의 id + subject + preconditions 내용을 ArrayList에 추가
			tcPre.add(new TestCasePrecondition(this.rs.getInt("id"), this.rs.getString("name"),
					this.rs.getString("preconditions"), this.rs.getString("value")));
			tcPre.get(tcPre.size() - 1).modifyPrecondition();
		}
		return tcPre;
	}

	// testlink에서 step을 가져온다.
	public ArrayList<TestCaseStep> addStep(ArrayList<TestCaseStep> tcStep, ResultSet rs) throws SQLException {
		tcStep.add(new TestCaseStep());
		while (rs.next()) {
			tcStep.get(tcStep.size() - 1).setStep(rs.getString("actions"), rs.getString("expected_results"));
			tcStep.get(tcStep.size() - 1).modifyAction(tcStep.get(tcStep.size() - 1).getActionSize() - 1);
		}
		return tcStep;
	}
}

//자동화를 위해 수행하는 sql문을 모아놓은 class
class SqlJob {
	private ResultSet rs, rs1 = null;

	public ResultSet selectTCversion(Connection conn) throws SQLException {
		// sql : testlink에서 prosync 테스트케이스들이 저장된 '디렉토리'를 조회하는 쿼리
		String sql = "SELECT b.id, a.name, b.preconditions, c.value FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b, bitnami_testlink.cfield_design_values c"
				+ " WHERE parent_id in (417990) and b.id=a.id+1 and b.id=c.node_id and c.field_id=1;";
		this.rs = conn.createStatement().executeQuery(sql);
		return this.rs;
	}

	public ResultSet selectTCstep(Connection conn, int id) throws SQLException {
		// sql : 해당 디렉토리 속 '테스트케이스들의 id'를 조회하는 쿼리
		String sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + id;
		// sql1 : 해당 테스트케이스의 '액션들'을 조회하는 쿼리
		String sql1 = "SELECT actions, expected_results FROM bitnami_testlink.tcsteps WHERE id in (";
		this.rs = conn.createStatement().executeQuery(sql);
		if (this.rs != null && this.rs.isBeforeFirst()) {
			while (this.rs.next()) {
				sql1 += this.rs.getString("id") + ",";
			}
			sql1 = sql1.substring(0, sql1.length() - 1);
			sql1 += ") order by step_number";
			this.rs1 = conn.createStatement().executeQuery(sql1);
		}
		return this.rs1;
	}

	public String getTxInfo(Connection conn) throws SQLException {
		int xid, tsn;
		rs = conn.createStatement().executeQuery(
				"select dbms_transaction.local_transaction_id as tid, decode(dbms_transaction.local_transaction_id,null,null,current_tsn) as tsn from v$database");
		rs.next();
		xid = Integer.parseInt(rs.getString("tid").split("\\.")[0]) * 65536
				+ Integer.parseInt(rs.getString("tid").split("\\.")[1]);
		tsn = rs.getInt("tsn");
		return xid + "/" + tsn;
	}

	public int getRowCount(Connection conn, String tbl) throws SQLException {
		rs = conn.createStatement().executeQuery("select count(*) as count from " + tbl);
		rs.next();
		return rs.getInt("count");
	}

	// db에 접속하여 precondion을 수행한다.
	public void executePreQry(Connection conn, String sql) throws SQLException {
		String[] sqlSplit = sql.split(";");
		for (int n = 0; n < sqlSplit.length; n++) {
			conn.createStatement().execute(sqlSplit[n]);
		}
	}

	// db 에 접속하여 action을 수행한다.
	public int executeActionQry(Connection conn, String sql) {
		try {
			return conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			// System.out.print("Error Code : " + e.getErrorCode());
			return e.getErrorCode();
		}
	}

	public void executeCommit(Connection conn, String CommitUnit, String unit) throws SQLException {
		if (CommitUnit.equals(unit)) {
			System.out.println("COMMIT;");
			conn.commit();
		}
	}
}

//precondition 가져와서 정제
class TestCasePrecondition {
	private int id;
	private String subject, precondition, tcName;

	public TestCasePrecondition(int id, String subject, String precondition, String tcName) {
		this.id = id;
		this.subject = subject;
		this.precondition = precondition;
		this.tcName = tcName;
	}

	public String getTcName() {
		return this.tcName;
	}

	public String getSubject() {
		return this.subject;
	}

	public int getId() {
		return this.id;
	}

	public String getPrecondition() {
		return this.precondition;
	}

	// testlink에서 바로 가져올 경우 HTML tag가 달린 상태로 쿼리가 받아짐. precondition의 HTML tag를 없애는 과정
	public void modifyPrecondition() {
		this.precondition = this.precondition.replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("<br />", "")
				.replaceAll("(\r|\n|\r\n|\n\r)", "").replaceAll("&#39;", "'").replaceAll("&nbsp;", " ");
	}
}

//step 가져와서 정제
class TestCaseStep {
	private ArrayList<String> action = new ArrayList<String>();
	private ArrayList<String> expected_result = new ArrayList<String>();
	private ArrayList<Boolean> stepValidation = new ArrayList<Boolean>();
	private boolean caseValidation = true;

	public void addStepValidation(Boolean flag) {
		this.stepValidation.add(flag);
	}

	public void setCaseValidation(Boolean flag) {
		this.caseValidation &= flag;
	}

	public ArrayList<Boolean> getStepValidation() {
		return this.stepValidation;
	}

	public Boolean getCaseValidation() {
		return this.caseValidation;
	}

	public void setStep(String action, String expected_result) {
		this.action.add(action);
		this.expected_result.add(expected_result);
	}

	public ArrayList<String> getAction() {
		return this.action;
	}

	public int getActionSize() {
		return this.action.size();
	}

	// testlink에서 바로 가져올 경우 HTML tag가 달린 상태로 쿼리가 받아짐. action의 HTML tag를 없애는 과정
	public void modifyAction(int index) {
		this.action.set(index,
				this.action.get(index).replaceAll("<p>", "").replaceAll("</p>", "").replaceAll("<br />", "")
						.replaceAll("(\r|\n|\r\n|\n\r)", "").replaceAll("&lt;", "<").replaceAll("&gt;", ">")
						.replaceAll("&#39;", "'").replaceAll("&nbsp;", " "));
	}
}

//db에 접속과 관련된 클래스
class DBConnection {
	private String[] driver = new String[3];
	private String[] url = new String[3];
	private String[] user = new String[3];
	private String[] pwd = new String[3];

	// DB에 접속하기 위한 정보(driver, url, user, passwd)를 받아 정보를 세팅
	public void setDbInfo(int index, String driver, String url, String user, String pwd) {
		this.driver[index] = driver;
		this.url[index] = url;
		this.user[index] = user;
		this.pwd[index] = pwd;
	}

	// DB연결상태 출력
	public Connection getConnection(int index) {
		Connection conn = null;
		try {
			Class.forName(this.driver[index]);
			conn = DriverManager.getConnection(this.url[index], this.user[index], this.pwd[index]);
			conn.setAutoCommit(false); // 자동 커밋 해제
		} catch (ClassNotFoundException cnfe) {
			System.out.println("DB 드라이버 로딩 실패 :" + cnfe.toString());
		} catch (SQLException sqle) {
			System.out.println("DB 접속실패 : " + sqle.toString());
		} catch (Exception e) {
			System.out.println("Unkonwn error");
			e.printStackTrace();
		}
		return conn;
	}
}