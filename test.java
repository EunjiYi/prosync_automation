import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class test {
	public static String CommitUnit;
	static String ValidationtUnit;

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		// DatabaseLoader
		//// SRC/TAR DB connection();
		//// SRC DB runSQL(); - getAction
		//// SRC/TAR DB check(); - verify

		// 단위 순 : test step -> test case -> test suite
		// check단위는 commit단위보다 작을 수 없다. ex) commit=case,check=step 이면 안된다.(커밋하지 않았는데,
		// 정합성을 체크함)
		CommitUnit = "step";
		ValidationtUnit = "step";

		DBConnection dbinfo = new DBConnection();
		SqlJob sj = new SqlJob();
		TestCaseRegsiter tr = new TestCaseRegsiter();
		ArrayList<TestCasePrecondition> tcPre = new ArrayList<TestCasePrecondition>();
		ArrayList<TestCaseStep> tcStep = new ArrayList<TestCaseStep>();

		Connection conn = null;

		ResultSet rs = null;

		try {
			long start = System.currentTimeMillis();

			Connection connTarget = null;
			// 전반적인 과정
			// 1) mysql(testlink) 에서 tc(testcase)의 id 추출 및 action 추출
			// 2) 추출된 action을 정제 후(=HTML tag 제거 후) 배열로 저장
			// 3) tc에 대한 precondition쿼리를 소스/타겟 DB에 날려줌
			// 4) action에 있는 쿼리를 날려서 소스DB에 부하를 준다.

			// testlink DB(mysql)에 접속 및 DB정보 저장

			// index 0 : testlink, 1 : srcdb, 2 : tardb
			// srcㅡtar DB 정보 추가 설정 필요
			dbinfo.setDbInfo(0, "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.1.154:3307/bitnami_testlink", "root",
					"testlink");
			dbinfo.setDbInfo(1, "com.tmax.tibero.jdbc.TbDriver", "jdbc:tibero:thin:@192.168.17.104:38629:tbsync1",
					"tibero", "tmax");
			dbinfo.setDbInfo(2, "com.tmax.tibero.jdbc.TbDriver", "jdbc:tibero:thin:@192.168.17.104:48629:tbsync2",
					"tibero", "tmax");

			// conn 변수에 값 대입되는 시점에 DB에 접속함
			conn = dbinfo.getConnection(0);
			rs = sj.selectTCversion(conn);
			tr.addPrecondition(tcPre, rs);
			closeConnection(conn);

			// runpre라고 keyword 입력시 해당 구문 수행
			if (args.length > 0 && args[0].equals("runpre")) {
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
			if (args.length > 0 && args[0].equals("runaction")) {
				conn = dbinfo.getConnection(0);
				// loop를 전부 for each 형태로 작성
				// tcPre에서 id를 추출하여 id에 해당하는 action 수행
				for (TestCasePrecondition i : tcPre) {
					rs = sj.selectTCstep(conn, i.getId());
					tr.addStep(tcStep, rs);
				}
				closeConnection(conn);

				ArrayList<String> syncTable = new ArrayList<String>();
				int xid = 0, tsn = 0;
				boolean colValidation = true, stepValidation = true, caseValidation = true;

				conn = dbinfo.getConnection(1);
				int index = 0;
				for (TestCaseStep i : tcStep) {
					caseValidation = true;
					for (String action : i.getAction()) {
						// sync table parsing
						String syncTableSplit = null;
						if (action.contains("TBL")) {
							// System.out.println("SQL : " + action);
							if (sj.executeActionQry(conn, action) < 0) {
								System.out.println(
										", CASE Name : " + tcPre.get(index).getSubject() + ", SQL : " + action);
								continue;
							}
							rs = conn.createStatement().executeQuery(
									"select dbms_transaction.local_transaction_id as tid, decode(dbms_transaction.local_transaction_id,null,null,current_tsn) as tsn from v$database;\n"
											+ "");
							while (rs.next()) {
								String str = rs.getString("tid");
								tsn = rs.getInt("tsn");
								if (str != null && !str.isEmpty()) {
									xid = Integer.parseInt(str.split("\\.")[0]) * 65536
											+ Integer.parseInt(str.split("\\.")[1]);
								}
							}
							syncTableSplit = action.substring(action.indexOf("TBL"));
							syncTableSplit = syncTableSplit.split(" ")[0].split("\\(")[0].split("\\,")[0].split(";")[0];

							if (!syncTable.contains(syncTableSplit)) {
								syncTable.add(syncTableSplit);
							}
						} else if (action.contains("ROLLBACK")) {
							conn.rollback();
							syncTable.clear();
						} else {
							// check logic
							if (syncTable.size() == 0) {
								break;
							}
							stepValidation = true;
							conn.commit();
							connTarget = dbinfo.getConnection(2);

							// 프로싱크 동기화 여부 확인 (last tsn 조회)
							do {
								// System.out.println("SELECT TSN FROM prosync_t2t.prs_lct_t0 WHERE xid = " +
								// xid + " AND tsn >= " + tsn);
								rs = connTarget.createStatement()
										.executeQuery("SELECT TSN FROM prosync_t2t.prs_lct_t0 WHERE xid = " + xid
												+ " AND tsn >= " + tsn);
								Thread.sleep(200);
							} while (!rs.isBeforeFirst());

							// 마지막에 추가된 테이블부터 조회 하도록 뒤집기
							Collections.reverse(syncTable);
							for (String tbl : syncTable) {
								// 해당 테이블의 총 row count 추출
								int srcRowCount = sj.getRowCount(conn, tbl),
										tarRowCount = sj.getRowCount(connTarget, tbl);

								if (srcRowCount != tarRowCount) {
									stepValidation = false;
								} else if (srcRowCount == 0) {
									// System.out.println(" ROW 없음");
									continue;
								} else {
									// System.out.println(", ROW수 같음");
									ResultSet rs1 = conn.createStatement()
											.executeQuery("select * from " + tbl + " order by 1");
									ResultSet rs2 = connTarget.createStatement()
											.executeQuery("select * from " + tbl + " order by 1");
									// row별 데이터 비교
									ResultSetMetaData metaInfo1 = rs1.getMetaData();
									colValidation = true;
									// 여러 row에 대해서 컬럼값 비교, 하나의 컬럼이라도 정합성이 다를 경우 수행되지 않음
									while (rs1.next() && rs2.next() && colValidation) {
										for (int l = 1; l <= metaInfo1.getColumnCount(); l++) {
											if (rs1.getString(l) == null || rs1.getString(l).equals("") == true) {
												continue;
											} else if (rs1.getString(l).equals(rs2.getString(l))) {
												colValidation &= true;
											} else {
												System.out.println(metaInfo1.getColumnName(l) + "컬럼 데이터 불일치 SRC : "
														+ rs1.getString(l) + ", TAR : " + rs2.getString(l));
												System.out.println(tcPre.get(index).getSubject() + ", TBL : " + tbl);
												colValidation &= false;
												break;
											}
										}
									}
									stepValidation &= colValidation;
								}
								if (!stepValidation) {
									System.out.println("STEP 단위 정합성 불일치 ");
									break;
								}
							}
							// System.out.println("stepValidation : " + stepValidation + ", colValidation :
							// " + colValidation);
							syncTable.clear();
							closeConnection(connTarget);
						}
					}
					caseValidation &= stepValidation;
					if (!caseValidation) {
						System.out.println("CASE 단위 정합성 불일치 ");
					}
					System.out.println(tcPre.get(index++).getSubject() + ", caseValidation : " + caseValidation);
				}
				closeConnection(conn);
			}
			long end = System.currentTimeMillis();
			System.out.println("실행 시간 : " + (end - start) / 1000.0 + "(초)");
		} catch (SQLException e) {
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

	public void runValidation() {
		System.out.println("a");
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
					this.rs.getString("preconditions")));
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
		String sql = "SELECT b.id, a.name, b.preconditions FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b"
				+ " WHERE parent_id in (417990) and b.id=a.id+1";
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
			System.out.print("Error Code : " + e.getErrorCode());
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
	private String subject, precondition;

	public TestCasePrecondition(int id, String subject, String precondition) {
		this.id = id;
		this.subject = subject;
		this.precondition = precondition;
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