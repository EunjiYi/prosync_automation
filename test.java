import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;

import java.sql.ResultSet;
import java.util.ArrayList;



public class test {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
    Connection conn = null;
        try {
//            String sql=args[0];
            // mysql 에서 tc id 추출 -> step 추출
            // 추출된 step 정제 후 배열에 저장
            // tc에 대한 precondition 케이스 소스/타겟 DB에 발생
            // 소스DB 부하
            //ArrayList<TestCase()> list = new ArrayList<TestCase()>();

            // 추후 파일 읽어서 db정보 저장
            DBConntion dbinfoTestlink = new DBConntion();
            //DBConntion dbinfoSource = new DBConntion();
            //DBConntion dbinfoTarget = new DBConntion();
            dbinfoTestlink.setDbInfo("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink"); 
            //dbinfoSource.setDbInfo("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink"); 
            //dbinfoTarget.setDbInfo("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink"); 

            // mysql
            //conn = DBConntion.getConnection("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink");
            conn = dbinfoTestlink.getConnection();
            // oracle
            //conn = DBConntion.getConnection("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@192.168.17.104:1521:orcl","tibero","tmax");
            // tibero
            //conn = DBConntion.getConnection("com.tmax.tibero.jdbc.TbDriver","jdbc:tibero:thin:@192.168.17.104:38629:tbsync1","tibero","tmax");

            Statement stmtDD = conn.createStatement();
            StringBuilder sb = new StringBuilder();
            String sql = sb.append("SELECT * FROM bitnami_testlink.nodes_hierarchy where parent_id =409849").toString();
            ResultSet rs = stmtDD.executeQuery(sql);


            // TestCase타입의 ArrayList를 생성하겠다.
            ArrayList<TestCase> tc = new ArrayList<TestCase>();
            

            // 쿼리이 있을 때 까지 loop
            while(rs.next()){
                // 내용이 있으면 테스트케이스의 id + 제목의 내용을 ArrayList에 추가
                tc.add(new TestCase(rs.getInt("id"), rs.getString("name")));
            }

            //stmtDD.executeUpdate(sql);
            //conn.commit();
            

            // ArrayList로 생성된 tc의 정보들을 조회
            for(TestCase t : tc){
                t.getTestCase();
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
    private String subject;
    private String precondition;
    private String step[];

    public TestCase(int id) {
        this.id = id;
    }
    public TestCase(int id, String subject) {
        this.id = id;
        this.subject = subject;
    }

    public void setTestCaseId(int id) {
        this.id = id;
    }

    public void getTestCase() {
        System.out.println("tc id : " + this.id + ", subject : " + this.subject);
    }

}

class DBConntion
{
    String driver, url, user, pwd;

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
