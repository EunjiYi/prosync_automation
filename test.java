import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;
import java.util.ArrayList;

public class test {

	public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
		// DatabaseLoader
		//// SRC/TAR DB connection();
		//// SRC DB runSQL(); - getAction
		//// SRC/TAR DB check(); - verify

		DBConnection dbinfo = new DBConnection();
		SqlJob sj = new SqlJob();
		TestCaseRegsiter tr = new TestCaseRegsiter();
		ArrayList<TestCasePrecondition> tcPre = new ArrayList<TestCasePrecondition>();
		ArrayList<TestCaseStep> tcStep = new ArrayList<TestCaseStep>();

		Connection conn = null;
		Connection connTar = null;
		ResultSet rs = null;

		try {
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
			// dbinfo.setDbInfo(3, "oracle.jdbc.driver.OracleDriver",
			// "jdbc:oracle:thin:@192.168.17.104:1521:orcl", "tibero", "tmax");

			// conn.commit(); // 커밋 하기
			// conn.rollback(); // 롤백 하기
			// conn 변수에 값 대입되는 시점에 DB에 접속함
			conn = dbinfo.getConnection(0);
			rs = sj.selectTCversion(conn);
			tr.addPrecondition(tcPre, rs);
			closeConnection(conn);

			// runpre라고 keyword 입력시 해당 구문 수행
			if (args.length > 0 && args[0].equals("runpre")) {
				conn = dbinfo.getConnection(1);
				connTar = dbinfo.getConnection(2);
				for (TestCasePrecondition i : tcPre) {
					sj.executePreQry(conn, i.getPrecondition());
					sj.executePreQry(connTar, i.getPrecondition());
				}
				closeConnection(conn);
				closeConnection(connTar);
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

				conn = dbinfo.getConnection(1);
				for (TestCaseStep i : tcStep) {
					for (String action : i.getAction()) {
						// sj.executeActionQry(conn, action);

						// sync table parsing
						String tmp1 = null;

						if (!action.contains("COMMIT;")) {
							tmp1 = action.substring(action.indexOf("TBL"));
							tmp1 = tmp1.split(" ")[0];
							tmp1 = tmp1.split("\\(")[0];
							tmp1 = tmp1.split("\\,")[0];
							tmp1 = tmp1.split(";")[0];

							if (!syncTable.contains(tmp1)) {
								syncTable.add(tmp1);
							}

						} else {
							// check logic
							for (String tbl : syncTable) {
								System.out.println("sync : " + tbl);
							}
							syncTable.clear();
						}
					}
				}
				closeConnection(conn);
			}

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
			System.out.println("DB 연결 해제");
		} catch (Exception e) {
		}
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

	// db에 접속하여 precondion을 수행한다.
	public void executePreQry(Connection conn, String sql) throws SQLException {
		String[] sqlSplit = sql.split(";");
		for (int n = 0; n < sqlSplit.length; n++) {
			conn.createStatement().execute(sqlSplit[n]);
		}
	}

	// db 에 접속하여 action을 수행한다.
	public void executeActionQry(Connection conn, String sql) {
		try {
			conn.createStatement().execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getErrorCode());
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

	public int getId() {
		return this.id;
	}

	public String getPrecondition() {
		return this.precondition;
	}

	// testlink에서 바로 가져올 경우 HTML tag가 달린 상태로 쿼리가 받아짐. precondition의 HTML tag를 없애는 과정
	public void modifyPrecondition() {
		this.precondition = this.precondition.replaceAll("<p>", "");
		this.precondition = this.precondition.replaceAll("</p>", "");
		this.precondition = this.precondition.replaceAll("<br />", "");
		this.precondition = this.precondition.replaceAll("(\r|\n|\r\n|\n\r)", "");
		this.precondition = this.precondition.replaceAll("&#39;", "'");
		this.precondition = this.precondition.replaceAll("&nbsp;", " ");
	}
}

//step 가져와서 정제
class TestCaseStep {
	public TestCaseStep() {
	}

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
		this.action.set(index, this.action.get(index).replaceAll("<p>", ""));
		this.action.set(index, this.action.get(index).replaceAll("</p>", ""));
		this.action.set(index, this.action.get(index).replaceAll("<br />", ""));
		this.action.set(index, this.action.get(index).replaceAll("&lt;", "<"));
		this.action.set(index, this.action.get(index).replaceAll("&gt;", ">"));
		this.action.set(index, this.action.get(index).replaceAll("&#39;", "'"));
		this.action.set(index, this.action.get(index).replaceAll("&nbsp;", " "));
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
			System.out.println("DB 연결, autoCommit : " + conn.getAutoCommit());
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
