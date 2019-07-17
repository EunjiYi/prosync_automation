import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;
import java.util.ArrayList;

public class test {

	public static void main(String[] args) throws ClassNotFoundException {

		// argument non
		// TestCaseCollection + DatabaseTableCreate + 프로싱크 설치 및 기동 + DatabaseLoader

		// argument CreateTable
		// TestCaseCollection(setPrecondition)+DatabaseTableCreate(SRC/TAR TableCreate)

		// argument Loader
		// TestCaseCollection(setAction)+DatabaseLoader(SRC DML execute)

		// TestCaseCollection
		//// setPrecondition(); - TestCase->id, precondition value setting)
		//// setAction(); - TestCase->action value setting)

		// DatabaseTableCreate
		//// SRC/TAR DB connection();
		//// SRC/TAR DB runSQL(); - getPrecondition

		// DatabaseLoader
		//// SRC/TAR DB connection();
		//// SRC DB runSQL(); - getAction
		//// SRC/TAR DB check(); - verify

		Connection conn = null;

		try {
			// 전반적인 과정
			// 1) mysql(testlink) 에서 tc(testcase)의 id 추출 및 action 추출
			// 2) 추출된 action을 정제 후(=HTML tag 제거 후) 배열로 저장
			// 3) tc에 대한 precondition쿼리를 소스/타겟 DB에 날려줌
			// 4) action에 있는 쿼리를 날려서 소스DB에 부하를 준다.

			// testlink DB(mysql)에 접속 및 DB정보 저장
			DBConnection dbinfo = new DBConnection();
			SqlJob sj = new SqlJob();

			// index 0 : testlink, 1 : srcdb, 2 : tardb
			// srcㅡtar DB 정보 추가 설정 필요
			dbinfo.setDbInfo(0, "com.mysql.jdbc.Driver", "jdbc:mysql://192.168.1.154:3307/bitnami_testlink", "root",
					"testlink");
			dbinfo.setDbInfo(1, "com.tmax.tibero.jdbc.TbDriver", "jdbc:tibero:thin:@192.168.17.104:38629:tbsync1",
					"tibero", "tmax");
			dbinfo.setDbInfo(2, "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@192.168.17.104:1521:orcl",
					"tibero", "tmax");
			conn = dbinfo.getConnection(0);

			// conn.commit(); // 커밋 하기
			// conn.rollback(); // 롤백 하기

			// TestCaseCollection Precondition 가져오기
			// DoGetTestCase(conn);

			Statement stmtDD = null;
			String sql = null;
			ResultSet rs = sj.selectTCversion(conn);

			// TestCase타입의 ArrayList를 생성하겠다.
			ArrayList<TestCase> tc = new ArrayList<TestCase>();
			ArrayList<TestCasePrecondition> tcPre = new ArrayList<TestCasePrecondition>();
			ArrayList<TestCaseStep> tcStep = new ArrayList<TestCaseStep>();

			// TestCaseRegsiter
			TestCaseRegsiter tr = new TestCaseRegsiter();
			tr.addPrecondition(tcPre, rs);

			for (int i = 0; i < tcPre.size(); i++) {
				rs = sj.selectTCstep(conn, tcPre.get(i).getId());
				tr.addStep(tcStep, rs);
			}

			// to do list
			// SqlJob -> executeQry()에 인자값을 precondition/action으로 줘서 쿼리 수행하기
			for (int i = 0; i < tcPre.size(); i++) {
				System.out.println(tcPre.get(i).getPrecondition());
				// SqlJob -> executeQry(tcPre.get(i).getPrecondition())
				for (int j = 0; j < tcStep.get(i).getActionSize(); j++) {
					System.out.println(tcStep.get(i).getAction(j));
					// SqlJob -> executeQry(tcStep.get(i).getAction(j))
				}
			}

			System.exit(1);
			// testcase 내용을 조회
			int cnt = 0;
			while (rs.next()) {
				// 내용이 있으면 테스트케이스의 id + subject + preconditions 내용을 ArrayList에 추가
				// tc.add(new TestCase(rs.getInt("id"), rs.getString("name"),
				// rs.getString("preconditions")));

				sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + rs.getInt("id");
				Statement stmtDD1 = conn.createStatement();
				ResultSet rs1 = stmtDD1.executeQuery(sql);

				if (rs1 != null && rs1.isBeforeFirst()) {
					sql = "SELECT actions, expected_results FROM bitnami_testlink.tcsteps WHERE id in (";

					while (rs1.next()) {
						sql += rs1.getString("id") + ",";
					}
					sql = sql.substring(0, sql.length() - 1);
					sql += ") order by step_number";

					System.out.println(sql);
					rs1 = stmtDD1.executeQuery(sql);
					while (rs1.next()) {
						tc.get(cnt).setStep(rs1.getString("actions"), rs1.getString("expected_results"));
					}
				}
				cnt++;
			}
			closeConnection(conn);

			// 소스 DB 접속

			conn = dbinfo.getConnection(1);
			stmtDD = conn.createStatement();

			for (int i = 0; i < tc.size(); i++) {
				// precondition/action의 tag 제거 작업
				tc.get(i).modifyPrecondition();
				tc.get(i).modifyActions();

				// precondition의 DDL이 복수개일 경우도 고려, 쿼리를 ';' 단위로 나눠서 수행한다.
				String[] precoditions = tc.get(i).getPrecondition().split(";");
				for (int n = 0; n < precoditions.length; n++) {
					System.out.println(precoditions[n]);
					stmtDD.executeQuery(precoditions[n]);
				}

				// action(DML)을 SRC DB에 수행 - DML을 의도적으로 실패하는 케이스가 있으므로 try문 안에서 수행(안할 경우 java수행이
				// 멈춤)
				for (int j = 0; j < tc.get(i).getArraryAction().size(); j++) {
					// tc.get(i).modifyAction(j);
					try {
						System.out.println(tc.get(i).getArraryAction().get(j));
						stmtDD.executeQuery(tc.get(i).getArraryAction().get(j));
						// conn.commit(); // Step action 단위 커밋 하기
						// check();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				// conn.commit(); // Test Case 단위 커밋 하기
				// check();
			}
			// conn.commit(); // Test Suite 단위 커밋 하기
			// check();

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

class TestCaseRegsiter {
	private ResultSet rs = null;

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

	public ArrayList<TestCaseStep> addStep(ArrayList<TestCaseStep> tcStep, ResultSet rs) throws SQLException {
		tcStep.add(new TestCaseStep());
		while (rs.next()) {
			tcStep.get(tcStep.size() - 1).setStep(rs.getString("actions"), rs.getString("expected_results"));
			tcStep.get(tcStep.size() - 1).modifyAction(tcStep.get(tcStep.size() - 1).getActionSize() - 1);
		}
		return tcStep;
	}
}

class SqlJob {
	private Statement stmt = null;
	private ResultSet rs, rs1 = null;

	public ResultSet selectTCversion(Connection conn) throws SQLException {
		String sql = "SELECT b.id, a.name, b.preconditions FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b WHERE parent_id in (417990) and b.id=a.id+1";
		this.stmt = conn.createStatement();
		this.rs = this.stmt.executeQuery(sql);
		return this.rs;
	}

	public ResultSet selectTCstep(Connection conn, int id) throws SQLException {
		String sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + id;
		String sql1 = "SELECT actions, expected_results FROM bitnami_testlink.tcsteps WHERE id in (";
		stmt = conn.createStatement();
		this.rs = stmt.executeQuery(sql);
		if (this.rs != null && this.rs.isBeforeFirst()) {
			while (this.rs.next()) {
				sql1 += this.rs.getString("id") + ",";
			}
			sql1 = sql1.substring(0, sql1.length() - 1);
			sql1 += ") order by step_number";
			this.rs1 = stmt.executeQuery(sql1);
		}
		return this.rs1;
	}

	public void executeQry() {

	}
}

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

class TestCaseStep {
	public TestCaseStep() {
	}

	private ArrayList<String> action = new ArrayList<String>();
	private ArrayList<String> expected_result = new ArrayList<String>();

	public void setStep(String action, String expected_result) {
		this.action.add(action);
		this.expected_result.add(expected_result);
	}

	public String getAction(int index) {
		return this.action.get(index);
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

class TestCase {
	private int id;
	private String subject, precondition;
	private ArrayList<String> action = new ArrayList<String>();
	private ArrayList<String> expected_result = new ArrayList<String>();

	// 생성자에서 tc를 생성하면서 id, subject, precondition 정보를 입력 받음
	public TestCase(int id, String subject, String precondition) {
		this.id = id;
		this.subject = subject;
		this.precondition = precondition;
	}

	// Step = action + expected_result
	public void setStep(String action, String expected_result) {
		this.action.add(action);
		this.expected_result.add(expected_result);
	}

	// 임시
	public void getTestCase() {
		System.out.println("tc id : " + this.id + ", subject : " + this.subject);
		System.out.println("precondition : " + this.precondition);
	}

	// Step = action 과 동일한 의미
	public void getStep() {
		for (int i = 0; i < this.action.size(); i++) {
			System.out.println(this.action.get(i));
		}
	}

	// testlink의 precondition을 가져오는 메소드
	public String getPrecondition() {
		return this.precondition;
	}

	// testlink의 action을 가져오는 메소드, action은 한 개의 test case 당 여러개이므로 arraylist로 선언
	public ArrayList<String> getArraryAction() {
		return this.action;
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

	// testlink에서 바로 가져올 경우 HTML tag가 달린 상태로 쿼리가 받아짐. action의 HTML tag를 없애는 과정
	public void modifyActions() {
		for (int i = 0; i < this.action.size(); i++) {
			this.action.set(i, this.action.get(i).replaceAll("<p>", ""));
			this.action.set(i, this.action.get(i).replaceAll("</p>", ""));
			this.action.set(i, this.action.get(i).replaceAll("<br />", ""));
			this.action.set(i, this.action.get(i).replaceAll("&lt;", "<"));
			this.action.set(i, this.action.get(i).replaceAll("&gt;", ">"));
			this.action.set(i, this.action.get(i).replaceAll("&#39;", "'"));
			this.action.set(i, this.action.get(i).replaceAll("&nbsp;", " "));
		}
	}
}

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
