package xyz.hyhy.wrtmv.dao;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.hadoop.hive.HiveClient;
import org.springframework.stereotype.Repository;
import xyz.hyhy.wrtmv.config.HiveQLConfig;
import xyz.hyhy.wrtmv.config.SystemProps;
import xyz.hyhy.wrtmv.entity.VideoInfo;
import xyz.hyhy.wrtmv.utils.HBaseUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository("videoInfoDAO")
@DependsOn({"biliBiliUserDAO"})
public class VideoInfoDAOImpl implements VideoInfoDAO {
    private static final Logger logger = LoggerFactory.getLogger(VideoInfoDAOImpl.class);
    @Resource
    private HiveClient hiveClient;
    @Resource
    private SystemProps systemProps;
    @Resource
    private HiveQLConfig hiveQLConfig;

    private String namespace;
    private String tableName;
    private String tableNameInHBase;
    private String bilibiliUserHBaseTableName;

    @PostConstruct
    public void prepareUsedTables() {
        namespace = systemProps.getHBaseNamespace();
        tableName = systemProps.getVideosTable();
        tableNameInHBase = namespace + ":" + tableName;
        bilibiliUserHBaseTableName = namespace + ":" + systemProps.getUsersTable();
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
            String sql = hiveQLConfig.getCreateVideosTable();
            statement.execute(sql);
            System.out.println(String.format("success map hbase table:%s->hive table:%s", tableNameInHBase, tableName));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Override
    public void saveVideo(VideoInfo v) {
        List<Put> puts = new ArrayList<>();
        addPuts(puts, v);
        HBaseUtils.putRows(tableNameInHBase, puts);
        Put put = getOwnerPut(v.getMid(), v.getBvid());
        HBaseUtils.putRow(bilibiliUserHBaseTableName, put);
    }

    private Put getOnePut(String bvid, String qualifier, String data) {
        return HBaseUtils.createPut(bvid,
                "info",
                qualifier,
                data);
    }

    private Put getOwnerPut(long uid, String bvid) {
        return HBaseUtils.createPut(Long.toString(uid),
                "videos",
                bvid,
                "0");
    }

    private void addPuts(List<Put> puts, VideoInfo v) {
        String bvid = v.getBvid();
        puts.add(getOnePut(bvid, "oid", Long.toString(v.getOid())));
        puts.add(getOnePut(bvid, "mid", Long.toString(v.getMid())));
        puts.add(getOnePut(bvid, "title", v.getTitle()));
        puts.add(getOnePut(bvid, "created", Long.toString(v.getCreated())));
        puts.add(getOnePut(bvid, "description", v.getDescription()));
    }

    @Override
    public void saveVideos(Set<VideoInfo> vSet) {
        if (vSet == null || vSet.size() <= 0)
            return;
        List<Put> puts = new ArrayList<>();
        addAllPuts(puts, vSet);
        HBaseUtils.putRows(tableNameInHBase, puts);
        HBaseUtils.putRows(bilibiliUserHBaseTableName, getOwnerPuts(vSet));
    }

    private void addAllPuts(List<Put> puts, Set<VideoInfo> videoInfos) {
        for (VideoInfo v : videoInfos) {
            addPuts(puts, v);
        }
    }

    private List<Put> getOwnerPuts(Set<VideoInfo> videoInfos) {
        List<Put> puts = new ArrayList<>();
        for (VideoInfo v : videoInfos) {
            puts.add(getOwnerPut(v.getMid(), v.getBvid()));
        }
        return puts;
    }

    @Override
    public VideoInfo getVideoInfo(String bvid) {
        Result result = HBaseUtils.getRow(tableNameInHBase, bvid);
        if (!result.getExists())
            return null;
        return getOneFromHBaseResult(result);
    }

    private VideoInfo getOneFromHBaseResult(Result result) {
        String bvid = Bytes.toString(result.getRow());
        long oid = Long.parseLong(getInfoValue(result, "oid"));
        long mid = Long.parseLong(getInfoValue(result, "mid"));
        String title = getInfoValue(result, "title");
        String description = getInfoValue(result, "description");
        long created = Long.parseLong(getInfoValue(result, "created"));

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setBvid(bvid);
        videoInfo.setOid(oid);
        videoInfo.setMid(mid);
        videoInfo.setTitle(title);
        videoInfo.setDescription(description);
        videoInfo.setCreated(created);

        return videoInfo;
    }

    private String getInfoValue(Result result, String qualifier) {
        Cell cell = result.getColumnLatestCell("info".getBytes(), qualifier.getBytes());
        return Bytes.toString(CellUtil.cloneValue(cell));
    }

    @Override
    public List<VideoInfo> getVideosByUID(long uid) {
        Result result = HBaseUtils.getRow(bilibiliUserHBaseTableName, Long.toString(uid), "videos");
        List<Get> gets = new ArrayList<>();
        for (Cell cell : result.rawCells()) {
            String bvid = Bytes.toString(CellUtil.cloneQualifier(cell));
            gets.add(HBaseUtils.createGet(bvid));
        }
        Result[] results = HBaseUtils.getRows(tableName, gets);
        if (results == null)
            return null;
        List<VideoInfo> videoInfos = new ArrayList<>();
        for (Result r : results) {
            videoInfos.add(getOneFromHBaseResult(r));
        }
        return videoInfos;
    }
}
