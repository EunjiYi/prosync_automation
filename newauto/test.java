import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class test {
	public static void main(String[] args)
			throws ClassNotFoundException, InterruptedException, IOException, JSchException {

		DBConnection dbinfo = new DBConnection();
		SqlJob sj = new SqlJob();
		TestCaseRegsiter tr = new TestCaseRegsiter();
		ArrayList<TestCasePrecondition> tcPre = new ArrayList<TestCasePrecondition>();
		ArrayList<TestCaseStep> tcStep = new ArrayList<TestCaseStep>();
		Validation vd = new Validation();

		Connection conn = null;
		ResultSet rs = null;
	
		File file = new File(args[0]);		
		BufferedReader br = new BufferedReader(new FileReader(file));

		String temp, Val = "";
		
		//rule db 셋팅을 위한 임시 변수
		String temp2 = "";
		
		while ((temp = br.readLine()) != null) {
			if ( temp.contains("TOP_ID")) {
				Val += temp + "\n";				
				Val += temp.replace("TOP_ID=", "PRS_USER=prosync_") + "\n";
				Val += "PRS_PWD=tibero\n";
			}	
			else if ( temp.contains("SRC_DB_TYPE=tibero") ||temp.contains("SRC_DB_TYPE=TIBERO") ) {
				Val += temp + "\n";				
				Val += "SRC_INSTALL_USER=sys\n";
				Val += "SRC_INSTALL_PWD=tibero\n";
			}
			else if ( temp.contains("SRC_DB_TYPE=oracle") || temp.contains("SRC_DB_TYPE=ORACLE")) {
				Val += temp + "\n";		
				Val += "SRC_INSTALL_USER=sys\n";
				Val += "SRC_INSTALL_PWD=oracle\n";
			}
			else if ( temp.contains("SRC_DB_NAME") ) {
				Val += temp + "\n";		
			}			
			else if ( temp.contains("TAR_DB_TYPE[0]=tibero") || temp.contains("TAR_DB_TYPE[0]=TIBERO")) {
				temp2 += temp + "\n";
				Val += temp + "\n";			
				Val += "TAR_INSTALL_USER[0]=sys\n";
				Val += "TAR_INSTALL_PWD[0]=tibero\n";	
				Val += "RULE_INSTALL_PWD=tibero\n";
			}
			else if ( temp.contains("TAR_DB_TYPE[0]=oracle") || temp.contains("TAR_DB_TYPE[0]=ORACLE")) {
				temp2 += temp + "\n";
				Val += temp + "\n";			
				Val += "TAR_INSTALL_USER[0]=sys\n";
				Val += "TAR_INSTALL_PWD[0]=oracle\n";
				Val += "RULE_INSTALL_PWD=oracle\n";
			}
			else if ( temp.contains("TAR_DB_NAME[0]") ) {
				temp2 += temp + "\n";
				Val += temp + "\n";		
			}	
		
		}
				
		//RULE_DB SETTING		
		Val += "RULE_INSTALL_USER=sys\n";
		temp2 = temp2.replace("TAR_DB_TYPE[0]", "RULE_DB_TYPE") + "\n";
		temp2 = temp2.replace("TAR_DB_NAME[0]", "RULE_DB_NAME") + "\n";
		Val += temp2 + "\n";	
		
		
		// ssh 접속 후 명령어 수행 예제
		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		// String binaryName = args[4].split("/")[2];
		// System.out.println(binaryName);

		String installCmd = "source .*profile;" + "tar -xzf " + args[4] + ";" // args[4] = binaryName
				+ "mv prosync4 prosync4_" + args[3] + ";" // args[3] = IMS NUMBER
				+ "cd prosync4_" + args[3] + ";" + "source prs_env `pwd`;" + "cd $PRS_HOME/install;"
				// + "cp templates/prs_install.cfg.template prs_install.cfg;"
				+ "cp templates/prs_obj_group1.list.template prs_obj_group1.list;" 
				+ "echo \"" + Val + "\" >> prs_install.cfg;";

		System.out.println(installCmd);
		session = jsch.getSession("tmax", "192.168.17.105");
		session.setPassword("tmaxdata");
		session.setConfig("StrictHostKeyChecking", "no");
		session.connect();
		System.out.println("Connected");

		channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(installCmd);
		channel.setInputStream(null);
		((ChannelExec) channel).setErrStream(System.err);
		// BufferedReader buffer = new BufferedReader(new InputStreamReader(channel.getInputStream(), "UTF-8"));

		channel.connect();
		channel.disconnect();

		session.disconnect();
		System.out.println("DONE");
		System.exit(0);

		if (args.length > 1 && args[1].equals("prsInstall")) {
			String prsCmd = "sh $HOME/prosync4_" + args[3] + "/install/prs_install.sh";
			// args[3] = IMS NUMBER
			// channelExec.setCommand(prsCmd);

		} else if (args.length > 1 && args[1].equals("prsAdm")) {
			String admCmd = "prs_adm -c \"start " + args[2] + "\"";
			// args[2] = T2T or O2T
			// channelExec.setCommand(admCmd);
		}

		if (args.length <= 0 || args.length > 3) {
			System.out.println("java " + Thread.currentThread().getStackTrace()[1].getClassName()
					+ " [cfg file] [createTable|runTestcase] [3|4]");
			System.exit(0);
		}

		Path path = Paths.get(args[0]);
		List<String> list = new ArrayList<String>();
		list = Files.readAllLines(path);

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
			dbinfo.setDbInfo(0, "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.2.128:3307/bitnami_testlink", "root",
					"testlink");
			dbinfo.setDbInfo(1, src_driver, src_url, src_id, src_passwd);
			dbinfo.setDbInfo(2, tar_driver, tar_url, tar_id, tar_passwd);
			conn = dbinfo.getConnection(0);
			rs = sj.selectTCversion(conn);
			tr.addPrecondition(tcPre, rs);
			closeConnection(conn);

			if (args.length > 1 && args[1].equals("createTable")) {
				conn = dbinfo.getConnection(1);
				connTarget = dbinfo.getConnection(2);
				for (TestCasePrecondition i : tcPre) {
					sj.executePreQry(conn, i.getPrecondition());
					sj.executePreQry(connTarget, i.getPrecondition());
				}
				closeConnection(conn);
				closeConnection(connTarget);
			} else if (args.length > 1 && args[1].equals("runTestcase")) {
				int xid = 0, tsn = 0;

				conn = dbinfo.getConnection(0);
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
						actionIndex++;
						// System.out.println(tcStep.get(i).getAction().get(j));
						if (tcStep.get(i).getAction().get(j).contains("TBL")) {

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
								Thread.sleep(3000);
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
							for (int k = 0; k < actionIndex; k++) {
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
			} else {
				System.out.println("java " + Thread.currentThread().getStackTrace()[1].getClassName()
						+ " [cfg file] [createTable|runTestcase] [3|4]");
				System.exit(0);
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

class Validation {
	private ArrayList<String> syncTable = new ArrayList<String>();
	private ResultSet rs = null;

	public void registerSyncTable(String action) {
		String syncTableSplit = action.substring(action.indexOf("TBL"));
		syncTableSplit = syncTableSplit.split(" ")[0].split("\\(")[0].split("\\,")[0].split(";")[0];
		if (!syncTable.contains(syncTableSplit)) {
			syncTable.add(syncTableSplit);
		}
	}

	public ArrayList<String> getSyncTableList() {
		return this.syncTable;
	}

	public void clearSyncTable() {
		this.syncTable.clear();
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
			System.out.print(" " + tbl + " 소스/타겟 ROW 불일치!!");
			return false;
		} else if (srcRowCount == 0) {
		} else {
			ResultSet rs1 = conn.createStatement().executeQuery("select * from " + tbl + " order by 1");
			ResultSet rs2 = connTarget.createStatement().executeQuery("select * from " + tbl + " order by 1");
			ResultSetMetaData metaInfoSrc = rs1.getMetaData();
			ResultSetMetaData metaInfoTar = rs2.getMetaData();
			if (metaInfoSrc.getColumnCount() != metaInfoTar.getColumnCount()) {
				System.out.println("테이블의 컬럼 개수 정보가 일치하지 않음 : " + tbl);
				return false;
			}
			while (rs1.next() && rs2.next() && flag) {
				for (int l = 1; l <= metaInfoSrc.getColumnCount(); l++) {
					if (rs1.getString(l) == null || rs1.getString(l).equals("") == true) {
					} else if (rs1.getString(l).equals(rs2.getString(l))) {
					} else {
						System.out.println(metaInfoSrc.getColumnName(l) + "컬럼 데이터 불일치 SRC : " + rs1.getString(l)
								+ ", TAR : " + rs2.getString(l));
						return false;
					}
				}
			}
		}
		return true;
	}

}

class SqlJob {
	private ResultSet rs, rs1 = null;

	public ResultSet selectTCversion(Connection conn) throws SQLException {
		String sql = "SELECT b.id, a.name, b.preconditions, c.value FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b, bitnami_testlink.cfield_design_values c"
				+ " WHERE parent_id in (417990) and b.id=a.id+1 and b.id=c.node_id and c.field_id=1;";
		this.rs = conn.createStatement().executeQuery(sql);
		return this.rs;
	}

	public ResultSet selectTCstep(Connection conn, int id) throws SQLException {
		String sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + id;
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

	public void executePreQry(Connection conn, String sql) throws SQLException {
		String[] sqlSplit = sql.split(";");
		for (int n = 0; n < sqlSplit.length; n++) {
			conn.createStatement().execute(sqlSplit[n]);
		}
	}

	public int executeActionQry(Connection conn, String sql) {
		try {
			return conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			// System.out.print("Error Code : " + e.getErrorCode());
			return e.getErrorCode();
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

	public void setCaseValidation(Boolean flag) {
		this.caseValidation &= flag;
	}

	public void addStepValidation(Boolean flag) {
		this.stepValidation.add(flag);
	}

	public ArrayList<Boolean> getStepValidation() {
		return this.stepValidation;
	}

	public Boolean getCaseValidation() {
		return this.caseValidation;
	}

}

class TestCaseRegsiter {
	private ResultSet rs = null;

	public ArrayList<TestCasePrecondition> addPrecondition(ArrayList<TestCasePrecondition> tcPre, ResultSet rs)
			throws SQLException {
		this.rs = rs;
		while (this.rs.next()) {
			tcPre.add(new TestCasePrecondition(this.rs.getInt("id"), this.rs.getString("name"),
					this.rs.getString("preconditions"), this.rs.getString("value")));
			tcPre.get(tcPre.size() - 1).modifyPrecondition();
		}
		return tcPre;
	}

	public ArrayList<TestCaseStep> addStep(ArrayList<TestCaseStep> tcStep, ResultSet rs) throws SQLException {
		tcStep.add(new TestCaseStep());
		while (rs.next()) {
			tcStep.get(tcStep.size() - 1).setStep(rs.getString("actions"), rs.getString("expected_results"));
			tcStep.get(tcStep.size() - 1).modifyAction(tcStep.get(tcStep.size() - 1).getActionSize() - 1);
		}
		return tcStep;
	}
}
