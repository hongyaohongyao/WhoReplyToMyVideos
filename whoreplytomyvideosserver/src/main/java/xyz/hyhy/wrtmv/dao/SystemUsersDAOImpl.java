package xyz.hyhy.wrtmv.dao;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import xyz.hyhy.wrtmv.config.SystemProps;
import xyz.hyhy.wrtmv.entity.SystemUser;
import xyz.hyhy.wrtmv.utils.HBaseUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SystemUsersDAOImpl implements SystemUserDAO {
    @Resource
    private SystemProps systemProps;

    private String namespace;
    private String tableName;
    private String tableNameInHBase;

    public SystemUsersDAOImpl() {

    }

    @PostConstruct
    private void prepareUsedTables() {
        namespace = systemProps.getHBaseNamespace();
        tableName = systemProps.getSystemUserTable();
        tableNameInHBase = namespace + ":" + tableName;
        //hBase部分
        if (!HBaseUtils.existTable(tableNameInHBase)) {
            HBaseUtils.simpleCreateTable(tableNameInHBase, "info");
        }
    }


    @Override
    public boolean exist(long uid) {
        return HBaseUtils.existRow(tableNameInHBase, Long.toString(uid));
    }

    @Override
    public void saveSystemUser(SystemUser user) {
        List<Put> puts = new ArrayList<>();
        addPuts(puts, user);
        HBaseUtils.putRows(tableNameInHBase, puts);
    }

    private Put getOnePut(String uid, String qualifier, String data) {
        return HBaseUtils.createPut(uid,
                "info",
                qualifier,
                data);
    }

    private void addPuts(List<Put> puts, SystemUser user) {
        String uid = Long.toString(user.getUid());
        puts.add(getOnePut(uid, "updatetime", Long.toString(user.getUpdateTime())));
    }


    @Override
    public SystemUser getSystemUser(long uid) {
        Result result = HBaseUtils.getRow(tableNameInHBase, Long.toString(uid));
        return getOneFromHBaseResult(result);
    }

    private SystemUser getOneFromHBaseResult(Result result) {
        long uid = Long.parseLong(Bytes.toString(result.getRow()));
        long updateTime = Long.parseLong(getInfoValue(result, "updatetime"));

        SystemUser user = new SystemUser();
        user.setUid(uid);
        user.setUpdateTime(updateTime);
        return user;
    }

    private String getInfoValue(Result result, String qualifier) {
        Cell cell = result.getColumnLatestCell("info".getBytes(), qualifier.getBytes());
        return Bytes.toString(CellUtil.cloneValue(cell));
    }

    @Override
    public List<SystemUser> getSystemUsers() {
        ResultScanner sc = HBaseUtils.getScanner(tableNameInHBase);
        List<SystemUser> list = new ArrayList<>();
        for (Result r : sc) {
            long uid = Long.parseLong(Bytes.toString(r.getRow()));
            long updateTime = Long.parseLong(Bytes.toString(r.getValue("info".getBytes(), "updatetime".getBytes())));
            SystemUser user = new SystemUser();
            user.setUpdateTime(updateTime);
            user.setUid(uid);
            list.add(user);
        }
        return list;
    }


}
