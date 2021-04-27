package xyz.hyhy.wrtmv.utils;

import org.springframework.data.hadoop.hive.HiveClient;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class HiveUtils {

    @Resource
    private static HiveClient hiveClient;

    private HiveUtils() {

    }

    @Deprecated
    public static void createTableIfNotExist(String tableName) {
        String sql = "create  table if not exists ?";
        try (Connection conn = hiveClient.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, tableName);
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createExternalTable() {

    }

}
