package xyz.hyhy.wrtmv.dao;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.data.hadoop.hive.HiveClient;
import org.springframework.stereotype.Repository;
import xyz.hyhy.wrtmv.config.HiveQLConfig;
import xyz.hyhy.wrtmv.config.SystemProps;
import xyz.hyhy.wrtmv.entity.BiliBiliUser;
import xyz.hyhy.wrtmv.utils.HBaseUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository("biliBiliUserDAO")
public class BiliBiliUserDAOImpl implements BiliBiliUserDAO {

    @Resource
    private HiveClient hiveClient;
    @Resource
    private SystemProps systemProps;
    @Resource
    private HiveQLConfig hiveQLConfig;

    private String namespace;
    private String tableName;
    private String tableNameInHBase;

    public BiliBiliUserDAOImpl() {

    }

    @PostConstruct
    public void prepareUsedTables() {
        namespace = systemProps.getHBaseNamespace();
        tableName = systemProps.getUsersTable();
        tableNameInHBase = namespace + ":" + tableName;
        //hBase部分
        if (!HBaseUtils.existTable(tableNameInHBase)) {
            HBaseUtils.simpleCreateTable(tableNameInHBase, "info", "videos");
        }
        //hive部分
        createHiveTableIfNotExist();
    }

    private void createHiveTableIfNotExist() {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getCreateUsersTable();
            System.out.println(sql);
            statement.execute(sql);
            System.out.println(String.format("success map hbase table:%s->hive table:%s", tableNameInHBase, tableName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public boolean existUser(long uid) {
        return HBaseUtils.existRow(tableNameInHBase, Long.toString(uid));
    }

    @Override
    public void saveUserAsUser(BiliBiliUser user) {


    }

    private Put getOnePut(String uid, String qualifier, String data) {
        return HBaseUtils.createPut(uid,
                "info",
                qualifier,
                data);
    }

    private void addPuts(List<Put> puts, BiliBiliUser user) {
        String uid = Long.toString(user.getUid());
        puts.add(getOnePut(uid, "name", user.getName()));
        puts.add(getOnePut(uid, "sex", user.getSex()));
        puts.add(getOnePut(uid, "face", user.getFace()));
        puts.add(getOnePut(uid, "level", Long.toString(user.getLevel())));
        puts.add(getOnePut(uid, "sign", user.getSign()));
    }

    @Override
    public void saveUser(BiliBiliUser user) {
        List<Put> puts = new ArrayList<>();
        addPuts(puts, user);
        HBaseUtils.putRows(tableNameInHBase, puts);
    }

    @Override
    public BiliBiliUser getUser(long uid) {
        Result result = HBaseUtils.getRow(tableNameInHBase, Long.toString(uid));
        return getOneFromHBaseResult(result);
    }

    private BiliBiliUser getOneFromHBaseResult(Result result) {
        long uid = Long.parseLong(Bytes.toString(result.getRow()));
        String name = getInfoValue(result, "name");
        String sex = getInfoValue(result, "sex");
        String sign = getInfoValue(result, "sign");
        String face = getInfoValue(result, "face");
        int level = Integer.parseInt(getInfoValue(result, "level"));

        BiliBiliUser user = new BiliBiliUser();
        user.setUid(uid);
        user.setName(name);
        user.setSex(sex);
        user.setSign(sign);
        user.setFace(face);
        user.setLevel(level);
        return user;
    }

    private String getInfoValue(Result result, String qualifier) {
        Cell cell = result.getColumnLatestCell("info".getBytes(), qualifier.getBytes());
        return Bytes.toString(CellUtil.cloneValue(cell));
    }

    @Override
    public void saveUsers(Set<BiliBiliUser> users) {
        if (users == null || users.size() <= 0)
            return;
        List<Put> puts = new ArrayList<>();
        addAllPuts(puts, users);
        HBaseUtils.putRows(tableNameInHBase, puts);
    }

    /**
     * 批量创建用户put指令
     *
     * @param puts
     * @param users
     */
    private void addAllPuts(List<Put> puts, Set<BiliBiliUser> users) {
        for (BiliBiliUser user : users) {
            addPuts(puts, user);
        }
    }
}
