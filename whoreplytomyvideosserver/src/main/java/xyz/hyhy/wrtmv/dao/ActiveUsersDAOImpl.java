package xyz.hyhy.wrtmv.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.data.hadoop.hive.HiveClient;
import org.springframework.stereotype.Repository;
import xyz.hyhy.wrtmv.config.HiveQLConfig;
import xyz.hyhy.wrtmv.config.SystemProps;
import xyz.hyhy.wrtmv.entity.result.ActiveUser;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Repository("activeUsersDAO")
public class ActiveUsersDAOImpl implements ActiveUserDAO {
    @Resource
    private HiveClient hiveClient;
    @Resource
    private SystemProps systemProps;
    @Resource
    private HiveQLConfig hiveQLConfig;

    private String tableName;

    public ActiveUsersDAOImpl() {

    }

    @PostConstruct
    private void prepareUsedTables() {
        tableName = systemProps.getUsersStatisticsTable();
        //hive部分
        createHiveTableIfNotExist();
    }

    private void createHiveTableIfNotExist() {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getCreateStatisticsUsersTable();
            statement.execute(sql);
            System.out.println("成功创建用户回复统计信息表");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void analysis(long owner) {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getInsertUsersStatistics(owner);
            System.out.println(sql);
            statement.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public List<ActiveUser> getUsersByRank(long owner, long start, long end, String by) {
        List<ActiveUser> list = new ArrayList<>();
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getSelectUsersStatistics(owner, start, end, by);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                ActiveUser user = new ActiveUser();
                user.setOwner(owner);
                user.setName(resultSet.getString("name"));
                user.setFace(resultSet.getString("face"));
                user.setSex(resultSet.getString("sex"));
                user.setMid(resultSet.getLong("mid"));
                user.setLevel(resultSet.getInt("level"));
                user.setTimesR(resultSet.getLong("times_r"));
                user.setTimesV(resultSet.getLong("times_v"));
                user.setFirstTime(resultSet.getLong("firsttime"));
                user.setLastTime(resultSet.getLong("lasttime"));

                user.setRankR(resultSet.getLong("rank_r"));
                user.setRankV(resultSet.getLong("rank_v"));
                user.setRankF(resultSet.getLong("rank_f"));
                user.setRankL(resultSet.getLong("rank_l"));

                user.setRankRBySex(resultSet.getLong("rank_r_by_sex"));
                user.setRankVBySex(resultSet.getLong("rank_v_by_sex"));
                user.setRankFBySex(resultSet.getLong("rank_f_by_sex"));
                user.setRankLBySex(resultSet.getLong("rank_l_by_sex"));
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return list;
    }

}
