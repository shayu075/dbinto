package org.l.dbinto.helper;

import org.l.dbinto.object.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Created by liao on 2017/8/1.
 */
public class JDBCHelper {
    protected static Logger log = LoggerFactory.getLogger(JDBCHelper.class);
    private final static int DB_INFO = 0;
    private final static int EXECUTE_SQL = 1;

    public static Connection getConnection(DB db) throws Exception {
        log.info("正在连接数据库");
        // 设置可获取REMARK备注信息
        Properties props =new Properties();
        props.setProperty("remarksReporting", "true");
        props.setProperty("useInformationSchema", "true");//mysql设置可以获取tables remarks信息
        props.put("user", db.getUser());
        props.put("password", db.getPassword());
        return DriverManager.getConnection(db.getUrl(), props);
    }

    public static boolean testConnection(DB db){
        boolean re = true;
        try {
            Connection con = getConnection(db);
            con.close();
            log.info("数据库连接已关闭！");
        } catch (Exception e) {
            e.printStackTrace();
            re = false;
        }

        return re;
    }

    public static List executeSql(DB db, String sql) {
        List<Map<String, Object>> re = dbMetaData(db, sql, EXECUTE_SQL);

        return re;
    }

    public static List dbInfo(DB db) {
        List<Map<String, Object>> re = dbMetaData(db, null, DB_INFO);

        return re;
    }

    public static List dbMetaData (DB db, String sql, int type) {
        List<Map<String, Object>> re = new ArrayList<Map<String, Object>>();
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection(db);
            if (DB_INFO == type){
                log.info("数据库连接成功，准备返回库表信息！");
                DatabaseMetaData metaData = conn.getMetaData();
                String catalog = conn.getCatalog(); // catalog 其实也就是数据库名
                ResultSet rs = metaData.getTables(catalog, metaData.getUserName(), "%", new String[] { "TABLE" });
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String remarks = rs.getString("REMARKS");       //表备注
                    String pkName = "";
                    // 获取被引用的表，它的主键就是当前表的外键
                    Map fkTableNamesAndPk = new HashMap();
                    ResultSet foreignKeyResultSet = metaData.getPrimaryKeys(catalog, metaData.getUserName(), tableName);
                    while (foreignKeyResultSet.next()) {
                        if ("".equals(pkName))
                            pkName = foreignKeyResultSet.getString("COLUMN_NAME");
                        else
                            pkName = pkName + "," +foreignKeyResultSet.getString("COLUMN_NAME");
                    }
                    fkTableNamesAndPk.put("table_name", tableName);
                    fkTableNamesAndPk.put("remarks", remarks);
                    fkTableNamesAndPk.put("pkName", pkName);
                    re.add(fkTableNamesAndPk);
                    foreignKeyResultSet.close();
                }
                rs.close();
            }
            if (EXECUTE_SQL == type){
                log.info("数据库连接成功，准备执行：" + sql);
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
                int columnCount = md.getColumnCount();   //获得列数
                while (rs.next()) {
                    Map<String, Object> rowData = new HashMap<String, Object>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowData.put(md.getColumnName(i), rs.getObject(i));
                    }
                    re.add(rowData);
                }
                rs.close();
                stmt.close();
            }
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
                log.info("数据库连接已关闭！");
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

        return re;
    }
}
