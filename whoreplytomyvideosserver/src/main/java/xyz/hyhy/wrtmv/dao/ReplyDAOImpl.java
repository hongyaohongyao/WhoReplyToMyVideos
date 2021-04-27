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
import xyz.hyhy.wrtmv.entity.Reply;
import xyz.hyhy.wrtmv.utils.HBaseUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class ReplyDAOImpl implements ReplyDAO {
    @Resource
    private HiveClient hiveClient;
    @Resource
    private SystemProps systemProps;
    @Resource
    private HiveQLConfig hiveQLConfig;

    private String namespace;
    private String tableName;
    private String tableNameInHBase;

    public ReplyDAOImpl() {

    }

    @PostConstruct
    public void prepareUsedTables() {
        namespace = systemProps.getHBaseNamespace();
        tableName = systemProps.getReplyTable();
        tableNameInHBase = namespace + ":" + tableName;
        //hBase部分
        if (!HBaseUtils.existTable(tableNameInHBase)) {
            HBaseUtils.simpleCreateTable(tableNameInHBase, "info");
        }
        //hive部分
        createHiveTableIfNotExist();
    }

    private void createHiveTableIfNotExist() {
        try (Connection conn = hiveClient.getConnection();
             Statement statement = conn.createStatement()) {
            String sql = hiveQLConfig.getCreateRepliesTable();
            statement.execute(sql);
            System.out.println(String.format("success map hbase table:%s->hive table:%s", tableNameInHBase, tableName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    @Override
    public void saveReply(Reply reply) {
        List<Put> puts = new ArrayList<>();
        addPuts(puts, reply);
        HBaseUtils.putRows(tableNameInHBase, puts);
    }

    private Put getOnePut(String rpid, String qualifier, String data) {
        return HBaseUtils.createPut(rpid,
                "info",
                qualifier,
                data);
    }

    private void addPuts(List<Put> puts, Reply reply) {
        String uid = Long.toString(reply.getRpid());
        puts.add(getOnePut(uid, "oid", Long.toString(reply.getOid())));
        puts.add(getOnePut(uid, "mid", Long.toString(reply.getMid())));
        puts.add(getOnePut(uid, "content", reply.getContent()));
        puts.add(getOnePut(uid, "parent", Long.toString(reply.getParent())));
        puts.add(getOnePut(uid, "ctime", Long.toString(reply.getCtime())));
    }

    @Override
    public void removeReply(long rpid) {

    }

    @Override
    public Reply getReply(long rpid) {
        Result result = HBaseUtils.getRow(tableNameInHBase, Long.toString(rpid));
        assert result != null;
        if (!result.getExists())
            return null;
        return getOneFromHBaseResult(result);
    }

    private Reply getOneFromHBaseResult(Result result) {
        long rpid = Long.parseLong(Bytes.toString(result.getRow()));
        long oid = Long.parseLong(getInfoValue(result, "oid"));
        long mid = Long.parseLong(getInfoValue(result, "mid"));
        String content = getInfoValue(result, "content");
        long parent = Long.parseLong(getInfoValue(result, "parent"));
        long ctime = Long.parseLong(getInfoValue(result, "ctime"));

        Reply reply = new Reply();
        reply.setRpid(rpid);
        reply.setOid(oid);
        reply.setMid(mid);
        reply.setContent(content);
        reply.setParent(parent);
        reply.setCtime(ctime);
        return reply;
    }

    private String getInfoValue(Result result, String qualifier) {
        Cell cell = result.getColumnLatestCell("info".getBytes(), qualifier.getBytes());
        return Bytes.toString(CellUtil.cloneValue(cell));
    }

    @Override
    public List<Reply> getReplyByOID(long oid) {
        return null;
    }

    @Override
    public void saveReplies(Set<Reply> replies) {
        List<Put> puts = new ArrayList<>();
        addAllPuts(puts, replies);
        HBaseUtils.putRows(tableNameInHBase, puts);
    }

    /**
     * 批量创建用户put指令
     *
     * @param puts
     * @param replies
     */
    private void addAllPuts(List<Put> puts, Set<Reply> replies) {
        for (Reply reply : replies) {
            addPuts(puts, reply);
        }
    }
}
