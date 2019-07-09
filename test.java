import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;

import java.sql.ResultSet;
import java.util.ArrayList;



public class test {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        
    Connection conn = null;
        try {
//            String sql=args[0];
            // mysql ���� tc id ���� -> step ����
            // ����� step ���� �� �迭�� ����
            // tc�� ���� precondition ���̽� �ҽ�/Ÿ�� DB�� �߻�
            // �ҽ�DB ����
            //ArrayList<TestCase()> list = new ArrayList<TestCase()>();

            // ���� ���� �о db���� ����
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


            // TestCaseŸ���� ArrayList�� �����ϰڴ�.
            ArrayList<TestCase> tc = new ArrayList<TestCase>();
            

            // ������ ���� �� ���� loop
            while(rs.next()){
                // ������ ������ �׽�Ʈ���̽��� id + ������ ������ ArrayList�� �߰�
                tc.add(new TestCase(rs.getInt("id"), rs.getString("name")));
            }

            //stmtDD.executeUpdate(sql);
            //conn.commit();
            

            // ArrayList�� ������ tc�� �������� ��ȸ
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
                System.out.println("DB ���� ����");
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

                System.out.println("DB ����");
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("DB ����̹� �ε� ���� :"+cnfe.toString());
                } catch (SQLException sqle) {
                    System.out.println("DB ���ӽ��� : "+sqle.toString());
                } catch (Exception e) {
                    System.out.println("Unkonwn error");
                    e.printStackTrace();
                }
                return conn;
        }
}
