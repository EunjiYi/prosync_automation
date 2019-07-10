import java.io.*;
import java.util.*;
import java.text.*;
import java.sql.*;
import com.tmax.tibero.jdbc.*;
import com.tmax.tibero.jdbc.ext.*;

public class IMS86957 {
    private static String url = "jdbc:tibero:thin:@192.168.1.199:48629:tibero";
    private static String user = "tibero";
    private static String pass = "tmax";

    private static String sender = "sysop@nps.or.kr";
    private static String absolutePath =
        "D:\\_dev\\work\\project\\nps(spring)\\[crinity]backbone\\data\\SPOOL\\incoming\\0\\20140708124850811.0.eml";

    private static String header_data;
    private static String [] recipient_array = {
        "sysop@nps.or.kr",
        "bear@nps.or.kr",
        "ryunia@nps.or.kr",
        "lee@nps.or.kr",
        "foryou@nps.or.kr",
        "npcf333@nps.or.kr",
        "ykno@nps.or.kr",
        "iyang@nps.or.kr",
        "plus21@nps.or.kr",
        "ly4513@nps.or.kr",
        "sh0917@nps.or.kr",
        "uinara@nps.or.kr",
        "ygikim@nps.or.kr",
        "hyuntro@nps.or.kr",
        "moowoong@nps.or.kr",
        "cby1056@nps.or.kr",
        "hongsw@nps.or.kr",
        "khs1273@nps.or.kr",
        "jyn110@nps.or.kr",
        "cbp5576@nps.or.kr",
        "kpman402@nps.or.kr",
        "itl1576@nps.or.kr",
        "p2102884@nps.or.kr",
        "skpark@nps.or.kr",
        "krkim@nps.or.kr",
        "bus123@nps.or.kr",
        "sckwak@nps.or.kr",
        "pierrot@nps.or.kr",
        "yjc359@nps.or.kr",
        "jsrho@nps.or.kr",
        "sunjae@nps.or.kr",
        "chjkhr@nps.or.kr",
        "miseon@nps.or.kr",
        "s881812@nps.or.kr",
        "kjhmoi@nps.or.kr",
        "kscnpc@nps.or.kr",
        "kogurea@nps.or.kr",
        "chw100@nps.or.kr",
        "cbs2000@nps.or.kr",
        "pjw0806@nps.or.kr",
        "ltg1114@nps.or.kr",
        "cgt6209@nps.or.kr",
        "dipark@nps.or.kr",
        "nklee833@nps.or.kr",
        "macklee@nps.or.kr",
        "mkkim@nps.or.kr",
        "kshwan@nps.or.kr",
        "yuksan@nps.or.kr",
        "npc9209@nps.or.kr",
        "ahn3383@nps.or.kr",
        "yug442@nps.or.kr",
        "john5874@nps.or.kr",
        "sslee@nps.or.kr",
        "polybag@nps.or.kr",
        "pys3367@nps.or.kr",
        "mks5007@nps.or.kr",
        "jk88@nps.or.kr",
        "choiyh@nps.or.kr",
        "dongkwan@nps.or.kr",
        "uehy04@nps.or.kr",
        "chhmy@nps.or.kr",
        "kwonds@nps.or.kr",
        "npc02@nps.or.kr",
        "choikang@nps.or.kr",
        "jckim57@nps.or.kr",
        "yhkim@nps.or.kr",
        "hjk22@nps.or.kr",
        "kisstree@nps.or.kr",
        "nwkim88@nps.or.kr",
        "happyiam@nps.or.kr",
        "hcm7197@nps.or.kr",
        "jhshin@nps.or.kr",
        "tariot@nps.or.kr",
        "mia223@nps.or.kr",
        "parkdy@nps.or.kr",
        "woos@nps.or.kr",
        "smlee@nps.or.kr",
        "kschang@nps.or.kr",
        "jschoi@nps.or.kr",
        "sunykim@nps.or.kr",
        "onlychae@nps.or.kr",
        "webadmin@nps.or.kr",
        "open@nps.or.kr",
        "cosmos@nps.or.kr",
        "kimmild@nps.or.kr",
        "garam75@nps.or.kr",
        "master@nps.or.kr",
        "nani@nps.or.kr",
        "mychoi@nps.or.kr",
        "eager4@nps.or.kr",
        "cshyun@nps.or.kr",
        "kes@nps.or.kr",
        "sunnyoon@nps.or.kr",
        "cyber@nps.or.kr",
        "jhpark@nps.or.kr",
        "cadmus@nps.or.kr",
        "lkh5681@nps.or.kr",
        "jgj@nps.or.kr",
        "osa1001@nps.or.kr",
        "fiveline@nps.or.kr",
        "saralee@nps.or.kr"
    };

    public static void main(String[] args) throws Exception {
        System.out.println("IMS86957 regen program!!!");

        Class.forName("com.tmax.tibero.jdbc.TbDriver");

        IMS86957 instance = new IMS86957();
        initialize();

        if (args.length > 0 && args[0].equals("insert")) {
            SimpleDateFormat formatter = 
                new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
            String today = formatter.format(new java.util.Date());

            for (int i = 0; i < 1000000; i++) {
                int count = (int)(Math.random() * 32) + 1;

                if (checkEndCondition())
                    break;

                for (int j = 0; j < count; j++) {
                    String msg_id = "Mail_" + today + "_" + i + "_" + j;
                    instance.insert(msg_id);
                }

                System.out.println("insert loop: " + i);
                Thread.sleep(300);
            }
        } else if (args.length > 0 && args[0].equals("delete")) {
            for (int i = 0; i < 1000000; i++) {  
                int count = (int)(Math.random() * 32) + 64;

                if (checkEndCondition())
                    break;

                List list = instance.getSpoolList(count);

                /* for (int j = 0; j < list.size(); j++) {
                    String msg_id = (String)list.get(j);
                    instance.update(msg_id);
                } */

                if (list.size() < count) {
                    Thread.sleep(100);
                    continue;
                }

                for (int j = 0; j < list.size(); j++) {
                    String msg_id = (String)list.get(j);
                    instance.delete(msg_id);
                }

                System.out.println("delete loop: " + i);
                Thread.sleep(100);
            }
        } else {
            for (int i = 0; i < 1000000; i++) {  
                if (checkEndCondition())
                    break;

                List list = instance.getSpoolList(1000);

                for (int j = 0; j < list.size(); j++) {
                    String msg_id = (String)list.get(j);
                    instance.update(msg_id);
                }

                System.out.println("update loop: " + i);
                /* Thread.sleep(100); */
            }
        }
    }

    public static void initialize() throws Exception {
        StringBuffer buffer = new StringBuffer();
        FileInputStream in = new FileInputStream("header.txt");

        int c;
        while ((c = in.read()) != -1)
            buffer.append((char)c);

        buffer.append(buffer.toString());
        buffer.append(buffer.toString());
        buffer.append(buffer.toString());
        buffer.append(buffer.toString());
        buffer.append(buffer.toString());
        // buffer.append(buffer.toString());

        header_data = buffer.toString();
    }

    public static boolean checkEndCondition() throws Exception {
        File dir = new File(System.getenv("TB_HOME") + "/instance/" + 
                            System.getenv("TB_SID"));
        String list[] = dir.list();
        System.out.println("::" + dir.getAbsolutePath());
        for (int i = 0; i < list.length; i++) {
            if (!list[i].startsWith("tbsvr.out."))
                continue;

            File file = new File(dir, list[i]);
            if (file.length() > 0)
                return true;
        }

        return false;
    }

    protected void insert(String msg_id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String header = header_data.toString();

            String sender2 = sender;
            if ((int)(Math.random() * 3000) == 0)
                sender2 = null;

            StringBuffer recipients = new StringBuffer();
            for (int i = 0; i < recipient_array.length; i++) {
                recipients.append(recipient_array[i]);
                if (i < recipient_array.length)
                    recipients.append("\r\n");
            }

            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO crspool ( ");
            sql.append(" seq, message_id, repository_name, state, priority, name_path, ");
            sql.append(" sender, recipients, remote_addr, remote_host, ");
            sql.append(" error_count, last_updated, header, error_message ) ");
            sql.append(" VALUES(seq_crspool.NEXTVAL,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, empty_clob(), empty_clob())" );

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, msg_id);
            pstmt.setString(2, "repositoryName");
            pstmt.setString(3, "state");
            pstmt.setInt(4, 3);
            pstmt.setString(5, absolutePath);
            pstmt.setString(6, sender2);
            pstmt.setString(7, recipients.toString());
            pstmt.setString(8, "127.0.0.1");
            pstmt.setString(9, "127.0.0.1");
            pstmt.setInt(10, 0);
            pstmt.setString(11, "20140708124850");

            pstmt.executeUpdate();
            release(pstmt);

            sql.setLength(0);
            sql.append("SELECT header, error_message FROM crspool where  message_id=? FOR UPDATE");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, msg_id);

            rs = pstmt.executeQuery();
            rs.next();
            
            writeClob(rs.getClob("header"), header);
            writeClob(rs.getClob("error_message"), "");
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            release(rs);
            release(pstmt);
            closeConnection(conn);
        }
    }

    protected void update(String msg_id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(true);

            String header = header_data.toString();

            String sender2 = sender;
            if ((int)(Math.random() * 3000) == 0)
                sender2 = null;

            StringBuffer recipients = new StringBuffer();
            for (int i = 0; i < recipient_array.length; i++) {
                recipients.append(recipient_array[i]);
                if (i < recipient_array.length)
                    recipients.append("\r\n");
            }

            StringBuffer sql = new StringBuffer();
            sql.append(" UPDATE crspool SET state=?, name_path=?, ");
            sql.append(" sender=?, recipients=?, error_count=?, last_updated=?  ");
            sql.append(" WHERE message_id=? ");

            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, "state");
            pstmt.setString(2, absolutePath);
            pstmt.setString(3, sender2);
            pstmt.setString(4, recipients.toString());
            pstmt.setInt(5, 0);
            pstmt.setString(6, "20140708124850");
            pstmt.setString(7, msg_id);

            pstmt.executeUpdate();
            release(pstmt);
                   
            sql.setLength(0);
            sql.append("SELECT header, error_message FROM crspool where message_id = ? FOR UPDATE");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, msg_id);

            rs = pstmt.executeQuery();
            boolean exist = rs.next();

            if (exist) {
                /* 대상 ROW를 마구 삭제하면 AUTO COMMIT 모드에서,
                 * SELECT FOR UPDATE 하기 전에 이미 삭제 되었을 수 있다 */
                writeClob(rs.getClob("header"), header);
                writeClob(rs.getClob("error_message"), "");
            }

            release(rs);
            release(pstmt);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            release(pstmt);
            closeConnection(conn);
        }
    }

    protected void delete(String msg_id) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(true);

            String sql = "DELETE FROM crspool WHERE message_id=?";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, msg_id);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            release(pstmt);
            closeConnection(conn);
        }
    }

    protected String fetchResultSet(final ResultSet rs) throws SQLException, IOException {
        /* System.out.println("message_id=" + rs.getString("message_id")); */
       
        String recipients = readClob(rs.getClob("recipients"));
        String header = readClob(rs.getClob("header"));
        String errorMessage = readClob(rs.getClob("error_message")); 

        return rs.getString("message_id");
    }

    protected List getSpoolList(int count) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;

        List list = new LinkedList();
        try {
            conn = DriverManager.getConnection(url, user, pass);
            conn.setAutoCommit(true);

            String sql = "SELECT *  FROM ( "+
                    " SELECT * FROM crspool " +
                    " WHERE repository_name=? AND last_updated <= ? " +
                    " ORDER BY priority DESC, last_updated ASC) "
                    + " WHERE ROWNUM <= ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "repositoryName");
            pstmt.setString(2, "99991231235959");
            pstmt.setInt(3, count);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String entity = fetchResultSet(rs);
                list.add(entity);
            }
        } finally {
            release(pstmt);
            closeConnection(conn);
        }

        System.out.println("getSpoolList: " + list.size() + "/" + count);
        return list;
    }

    protected void writeClob(Clob clob, String content) throws SQLException, IOException {
        Writer writer = null;
        try {
            writer = new BufferedWriter(clob.setCharacterStream(0));
            writer.write(content);
            /* writer.close(); */
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static String readClob(Clob clob)throws SQLException, IOException {
        if (clob == null) {
            return "";
        }

        StringBuffer sbuffer = null;
        Reader in = null;
        try {
            in = clob.getCharacterStream();
            sbuffer = new StringBuffer();

            char[] buffer = new char[4096];
            int len = 0;

            while ((len=in.read(buffer)) != -1) {
                sbuffer.append(buffer, 0, len);
            }

            return sbuffer.toString();

        } finally {
            try {
                in.close();
            } catch (Exception ignored) {}
        }
    } 

    private void release(PreparedStatement pstmt) {
        try {
            if (pstmt != null) pstmt.close();
        } catch (Exception e) {
            // ignored.
        }
    }

    private void release(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (Exception e) {
            // ignored.
        }
    }

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, pass);
    }

    private void closeConnection(final Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (Exception e) {
            // ignored
        } 
    }
}

