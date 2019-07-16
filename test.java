import com.tmax.tibero.jdbc.driver.TbConnection;
import java.sql.*;

import java.sql.ResultSet;
import java.util.ArrayList;



public class test {

    public static void main(String[] args) throws ClassNotFoundException {
        
    Connection conn = null;
        try {
            // mysql ���� tc id ���� -> step ����
            // ����� step ���� �� �迭�� ����
            // tc�� ���� precondition ���̽� �ҽ�/Ÿ�� DB�� �߻�
            // �ҽ�DB ����

            // ���� ���� �о db���� ����
            DBConntion dbinfo = new DBConntion();
            dbinfo.setDbInfo("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink"); 
            conn = dbinfo.getConnection();

            //conn = DBConntion.getConnection("com.mysql.jdbc.Driver","jdbc:mysql://192.168.1.154:3307/bitnami_testlink","root","testlink");
            //conn = DBConntion.getConnection("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@192.168.17.104:1521:orcl","tibero","tmax");
            //conn = DBConntion.getConnection("com.tmax.tibero.jdbc.TbDriver","jdbc:tibero:thin:@192.168.17.104:38629:tbsync1","tibero","tmax");

            Statement stmtDD = conn.createStatement();
            String sql = "SELECT b.id, a.name, b.preconditions FROM bitnami_testlink.nodes_hierarchy a,bitnami_testlink.tcversions b WHERE parent_id in (417990) and b.id=a.id+1";
            ResultSet rs = stmtDD.executeQuery(sql);

            // TestCaseŸ���� ArrayList�� �����ϰڴ�.
            ArrayList<TestCase> tc = new ArrayList<TestCase>();
            
            // ������Ʈ�� testcase ������ ��ȸ
            int cnt=0;
            while(rs.next()){
                // ������ ������ �׽�Ʈ���̽��� id + subject + preconditions ������ ArrayList�� �߰�
                tc.add(new TestCase(rs.getInt("id"), rs.getString("name"), rs.getString("preconditions")));

                sql = "SELECT id FROM bitnami_testlink.nodes_hierarchy WHERE parent_id=" + rs.getInt("id");
                Statement stmtDD1 = conn.createStatement();
                ResultSet rs1 = stmtDD1.executeQuery(sql);

                if(rs1 != null && rs1.isBeforeFirst()){
                    sql = "SELECT actions, expected_results FROM bitnami_testlink.tcsteps WHERE id in (";
                    
                    while(rs1.next()){
                        sql += rs1.getString("id") + ",";
                    }
                    sql=sql.substring(0,sql.length()-1);
                    sql += ") order by step_number";
    
                    System.out.println(sql);
                    rs1 = stmtDD1.executeQuery(sql);
                    while(rs1.next()){
                        tc.get(cnt).setStep(rs1.getString("actions"), rs1.getString("expected_results"));
                    }
                    cnt++;
                }

            }
            closeConnection(conn);
            
            dbinfo.setDbInfo("com.tmax.tibero.jdbc.TbDriver","jdbc:tibero:thin:@192.168.17.104:38629:tbsync1","tibero","tmax"); 
            conn = dbinfo.getConnection();
            stmtDD = conn.createStatement();

            for(int i = 0; i < tc.size(); i++) {
                tc.get(i).PreconditionReplace();

                // DDL�� �������� ���, ';' ������ ������ ����
                String[] precodition_arr = tc.get(i).getPrecondition().split(";");
                for(int n = 0; n < precodition_arr.length; n++) {
                    System.out.println(precodition_arr[n]);
                    stmtDD.executeQuery(precodition_arr[n]);
                }
    
                for(int j = 0; j < tc.get(i).getArraryAction().size(); j++) {
		    tc.get(i).ActionReplace(j);
                    stmtDD.executeQuery(tc.get(i).getArraryAction().get(j));
                    System.out.println(tc.get(i).getArraryAction().get(j));
                }   
            }

        } catch (SQLException e) {
            System.out.println(e.getErrorCode());
            if (e.getErrorCode() == -7102)
            {
                System.out.println("aaa");
            }

            e.printStackTrace();
        } finally {
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
    private String subject, precondition;
    private ArrayList<String> action = new ArrayList<String>();
    private ArrayList<String> expected_result = new ArrayList<String>();

    // �ϳ��� ���̺� ���̽��� id,subject ������ �־, �����ڿ� ������ �Է� ���� �� �ֵ���
    public TestCase(int id, String subject, String precondition) {
        this.id = id;
        this.subject = subject;
        this.precondition = precondition;
    }
    public void setStep(String action, String expected_result) {
        this.action.add(action);
        this.expected_result.add(expected_result);
    }

    // �ӽ�
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
    public void PreconditionReplace() {
        this.precondition = this.precondition.replaceAll("<p>", "");
	this.precondition = this.precondition.replaceAll("</p>", "");
	this.precondition = this.precondition.replaceAll("<br />", "");
	this.precondition = this.precondition.replaceAll("(\r|\n|\r\n|\n\r)", "");
	this.precondition = this.precondition.replaceAll("&#39;", "'");
	this.precondition = this.precondition.replaceAll("&nbsp;", " ");
    }
    public void ActionReplace(int index) {
        this.action.set(index, this.action.get(index).replaceAll("<p>", ""));
	this.action.set(index, this.action.get(index).replaceAll("</p>", ""));
	this.action.set(index, this.action.get(index).replaceAll("<br />", ""));
        this.action.set(index, this.action.get(index).replaceAll("&lt;", "<"));
        this.action.set(index, this.action.get(index).replaceAll("&gt;", ">"));
        this.action.set(index, this.action.get(index).replaceAll("&#39;", "'"));
        this.action.set(index, this.action.get(index).replaceAll("&nbsp;", " "));
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
