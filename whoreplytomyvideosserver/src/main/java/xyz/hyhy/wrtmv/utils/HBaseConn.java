package xyz.hyhy.wrtmv.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;

public class HBaseConn extends BaseUtils {

    private static Configuration hBaseConfiguration; //hbase配置
    private static Connection connection; //hbase connection

    static {
        hBaseConfiguration = (Configuration) context.getBean("hBaseConfiguration");
    }

    private HBaseConn() {

    }

    /**
     * 获取内部连接
     *
     * @return Hbase连接
     */
    private static Connection getConnection() {
        if (connection == null || connection.isClosed()) {
            try {
                connection = ConnectionFactory.createConnection(hBaseConfiguration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * 获取HBase连接
     *
     * @return
     */
    public static Connection getHBaseConn() {
        return getConnection();
    }

    /**
     * 根据获取HBase的table对象
     *
     * @param tableName
     * @return
     * @throws IOException
     */
    public static Table getTable(String tableName) throws IOException {
        return getConnection().getTable(TableName.valueOf(tableName));
    }

    /**
     * 关闭连接
     */
    public static void closeConn() {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
