package xyz.hyhy.wrtmv.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.data.hadoop.hive.HiveClient;
import org.springframework.stereotype.Repository;
import xyz.hyhy.wrtmv.config.HiveQLConfig;
import xyz.hyhy.wrtmv.config.SystemProps;
import xyz.hyhy.wrtmv.entity.result.StatisticsInfo;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository("statisticsInfoDAO")
public class StatisticsInfoDAOImpl implements StatisticsInfoDAO {
    @Resource
    private HiveClient hiveClient;
    @Resource
    private SystemProps systemProps;
    @Resource
    private HiveQLConfig hiveQLConfig;

    private String tableName;

    public StatisticsInfoDAOImpl() {

    }

    @PostConstruct
    private void prepareUsedTables() {
        tableName = systemProps.getStatisticsInfoTable();
        //hive部分
        createHiveTableIfNotExist();
    }

    private void createHiveTableIfNotExist() {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getCreateStatisticsInfoTable();
            statement.execute(sql);
            System.out.println("成功创建回复区统计信息表");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void analysis(long owner) {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getInsertStatisticsInfo(owner);
            System.out.println(sql);
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public List<StatisticsInfo> getInfos(long owner, String item) {
        List<StatisticsInfo> list = new ArrayList<>();
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getStatisticsInfo(owner, item);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                StatisticsInfo statisticsInfo = new StatisticsInfo();
                statisticsInfo.setItem(resultSet.getString("item"));
                statisticsInfo.setLabel(resultSet.getString("label"));
                statisticsInfo.setValue(resultSet.getString("value"));
                list.add(statisticsInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return list;
    }
}
