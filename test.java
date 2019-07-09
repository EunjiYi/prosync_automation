import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;

import java.sql.ResultSet;
import java.util.ArrayList;



public class test {
//    private static String url = "jdbc:tibero:thin:@192.168.17.104:38629:tbsync1";
//    private static String user = "tibero";
//    private static String pwd = "tmax";
//    private static String driver = "com.tmax.tibero.jdbc.TbDriver";
//

    public static Connection conn = null;

    public void select(int id) {
        StringBuilder sb = new StringBuilder();
        String sql = sb.append("SELECT * FROM bitnami_testlink.nodes_hierarchy where parent_id = ").append(id).toString();

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
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
            
            while(rs.next()){
                System.out.println(rs.getInt("id"));
            }

            //stmtDD.executeUpdate(sql);
            //conn.commit();
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
    int tcid;
    String subject = null;
    String precondition = null;
    String step[] = null;
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
