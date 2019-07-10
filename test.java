import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;

import java.sql.ResultSet;
import java.util.ArrayList;



public class test {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
    Connection conn = null;
        try {
            // mysql 에서 tc id 추출 -> step 추출
            // 추출된 step 정제 후 배열에 저장
            // tc에 대한 precondition 케이스 소스/타겟 DB에 발생
            // 소스DB 부하

            // 추후 파일 읽어서 db정보 저장
            DBConntion dbinfo = new DBConntion();
            dbinfo.setDbInfo("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink"); 
            conn = dbinfo.getConnection();

            //conn = DBConntion.getConnection("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink");
            //conn = DBConntion.getConnection("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@192.168.17.104:1521:orcl","tibero","tmax");
            //conn = DBConntion.getConnection("com.tmax.tibero.jdbc.TbDriver","jdbc:tibero:thin:@192.168.17.104:38629:tbsync1","tibero","tmax");

            Statement stmtDD = conn.createStatement();
            String sql = "SELECT b.id, a.name, b.preconditions FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b WHERE parent_id=417990 and b.id=a.id+1";
            ResultSet rs = stmtDD.executeQuery(sql);

            // TestCase타입의 ArrayList를 생성하겠다.
            ArrayList<TestCase> tc = new ArrayList<TestCase>();
            
            // 프로젝트의 testcase 내용을 조회
            int cnt=0;
            while(rs.next()){
                // 내용이 있으면 테스트케이스의 id + subject + preconditions 내용을 ArrayList에 추가
                tc.add(new TestCase(rs.getInt("id"), rs.getString("name"), rs.getString("preconditions")));

                sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + rs.getInt("id");
                Statement stmtDD1 = conn.createStatement();
                ResultSet rs1 = stmtDD1.executeQuery(sql);

                sql = "SELECT actions, expected_results FROM bitnami_testlink.tcsteps WHERE id in (";
                
                rs1.next();
                sql += rs1.getString("id");
                while(rs1.next()){
                    sql += "," + rs1.getString("id");
                }
                sql += ") order by step_number";

                System.out.println(sql);
                rs1 = stmtDD1.executeQuery(sql);
                while(rs1.next()){
                    tc.get(cnt).setStep(rs1.getString("actions"), rs1.getString("expected_results"));
                }
                cnt++;
            }
            closeConnection(conn);
            
            // ArrayList로 생성된 tc의 정보들을 조회
            for(TestCase t : tc){
                //t.getTestCase();
                //t.getStep();
            }
            dbinfo.setDbInfo("com.tmax.tibero.jdbc.TbDriver","jdbc:tibero:thin:@192.168.17.104:38629:tbsync1","tibero","tmax"); 
            conn = dbinfo.getConnection();
            stmtDD = conn.createStatement();

            System.out.println(tc.size());

            for(int i = 0; i < tc.size(); i++) {
                String tmp = tc.get(i).getPrecondition();
                tmp = tmp.replaceAll("<p>", "");
                tmp = tmp.replaceAll("</p>", "");
                tmp = tmp.replaceAll("<br />", "");
                tmp = tmp.replaceAll("/", "");
                tmp = tmp.replaceAll("(\r|\n|\r\n|\n\r)", "");
                System.out.println(tmp);
                stmtDD.executeQuery(tmp);
    
                ArrayList<String> a = tc.get(i).getArraryAction();
                for(int j = 0; j < a.size(); j++) {
                    a.set(j, a.get(j).replaceAll("<p>", ""));
                    a.set(j, a.get(j).replaceAll("</p>", ""));
                    a.set(j, a.get(j).replaceAll("&lt;", "<"));
                    a.set(j, a.get(j).replaceAll("&gt;", ">"));
                    stmtDD.executeQuery(a.get(j));
                    System.out.println(a.get(j));
                }   
            }

        }

        finally {
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

class TestCase
{
    private int id;
    private String subject, precondition;
    private ArrayList<String> action = new ArrayList<String>();
    private ArrayList<String> expected_result = new ArrayList<String>();

    // 하나의 테이블에 케이스의 id,subject 정보가 있어서, 생성자에 정보를 입력 받을 수 있도록
    public TestCase(int id, String subject, String precondition) {
        this.id = id;
        this.subject = subject;
        this.precondition = precondition;
    }
    public void setStep(String action, String expected_result) {
        this.action.add(action);
        this.expected_result.add(expected_result);
    }

    // 임시
    public void getTestCase() {
        System.out.println("tc id : " + this.id + ", subject : " + this.subject);
        System.out.println("precondition : " + this.precondition);
    }
    public void getStep() {
        for(int i = 0; i < this.action.size(); i++) {
            System.out.println(this.action.get(i));
        }
    }
    public String getPrecondition() {
        return this.precondition;
    }
    public ArrayList<String> getArraryAction() {
        return this.action;
    }
}

class DBConntion
{
    private String driver, url, user, pwd;

    public void setDbInfo(String driver, String url, String user, String pwd) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pwd = pwd;
    }
        public Connection getConnection()
        {
            Connection conn = null;
            try {
                Class.forName(this.driver);
                conn = DriverManager.getConnection(this.url, this.user, this.pwd);

                System.out.println("DB 연결");
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("DB 드라이버 로딩 실패 :"+cnfe.toString());
                } catch (SQLException sqle) {
                    System.out.println("DB 접속실패 : "+sqle.toString());
                } catch (Exception e) {
                    System.out.println("Unkonwn error");
                    e.printStackTrace();
                }
                return conn;
        }
}
