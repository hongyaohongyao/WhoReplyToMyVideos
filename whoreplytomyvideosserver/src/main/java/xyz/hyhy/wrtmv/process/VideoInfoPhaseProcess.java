package xyz.hyhy.wrtmv.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hyhy.wrtmv.dao.BiliBiliUserDAO;
import xyz.hyhy.wrtmv.dao.VideoInfoDAO;
import xyz.hyhy.wrtmv.entity.VideoInfo;
import xyz.hyhy.wrtmv.utils.AppContextUtils;
import xyz.hyhy.wrtmv.utils.BiliBiliAPIUtils;
import xyz.hyhy.wrtmv.utils.CommonUtils;
import xyz.hyhy.wrtmv.utils.dotask.RunAndWait;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class VideoInfoPhaseProcess extends BaseCrawlProcess {

    private static final Logger logger = LoggerFactory.getLogger(VideoInfoPhaseProcess.class);
    private final BiliBiliUserDAO biliBiliUserDAO;
    private final VideoInfoDAO videoInfoDAO;

    @Getter
    private long uid;
    //task
    private final SortedSet<VideoInfo> videoInfoSet;

    private VideoInfoPhaseProcess(long uid) {
        this(uid, true);
    }

    private VideoInfoPhaseProcess(long uid, boolean startNow) {
        super();
        logger.info("准备爬取用户视频: " + uid);
        this.uid = uid;
        videoInfoSet = new TreeSet<>();
        //初始化DAO
        this.biliBiliUserDAO = AppContextUtils.getBean(BiliBiliUserDAO.class);
        this.videoInfoDAO = AppContextUtils.getBean(VideoInfoDAO.class);
        if (startNow)
            on();
    }

    public static VideoInfoPhaseProcess createProcess(long uid) {
        return new VideoInfoPhaseProcess(uid, true);
    }

    public static VideoInfoPhaseProcess createProcess(long uid, boolean startNow) {
        return new VideoInfoPhaseProcess(uid, startNow);
    }


    @Override
    protected void beforeOn() {
        videoInfoSet.clear();
    }

    @Override
    protected void crawlProcess() {
        try {
            JSONObject result = crawlVideoInfo(1, uid);
            if (result == null) {
                logger.warn("在用户视频列表 " + uid + " 未爬取到数据，退出任务");
                return;
            }
            JSONObject pageInfo = result.getJSONObject("page");
            int size = pageInfo.getInteger("ps");
            int count = pageInfo.getInteger("count");
            int page = count / size + 1;
            result = result.getJSONObject("list");
            if (result == null) {
                logger.warn(String.format("用户%没有视频", uid));
            }
            JSONArray vlist = result.getJSONArray("vlist");
            RunAndWait runAndWait = CommonUtils.getRunAndWaitInstance(getPool());
            for (int i = 2; i <= page; i++) {
                int p = i;
                runAndWait.add(() -> crawlPageProcess(p));
            }
            dealVideoInfos(vlist);
            logger.info(String.format("等待视频列表%d爬取结束", uid));
            runAndWait.waitUntilNoTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject crawlVideoInfo(int page, long uid) {
        return BiliBiliAPIUtils.crawlVideoInfo(page, uid, getSpiderTaskService());
    }

    private void crawlPageProcess(int page) {
        JSONObject result = crawlVideoInfo(page, uid);
        logger.info(String.format("评论区%d: 成功爬取第%d页", uid, page));
        if (result == null)
            return;
        JSONArray vlist = result.getJSONArray("vlist");
        dealVideoInfos(vlist);
    }

    private void dealVideoInfos(JSONArray vlist) {
        if (vlist == null)
            return;
        try {
            for (Object obj : vlist) {
                JSONObject vi = (JSONObject) obj;
                String bvid = vi.getString("bvid");
                long oid = vi.getLong("aid");
                long mid = vi.getLong("mid");
                String title = vi.getString("title");
                String description = vi.getString("description");
                long created = vi.getLong("created");
                VideoInfo videoInfo = new VideoInfo();
                videoInfo.setBvid(bvid);
                videoInfo.setOid(oid);
                videoInfo.setMid(mid);
                videoInfo.setTitle(title);
                videoInfo.setDescription(description);
                videoInfo.setCreated(created);
                synchronized (videoInfoSet) {
                    videoInfoSet.add(videoInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void saveResultToDataBase() {
        logger.info("爬取任务完成，共找到id为" + uid + "的用户的视频，共" + videoInfoSet.size());
        if (videoInfoSet.size() > 0)
            videoInfoDAO.saveVideos(videoInfoSet);
        List<BaseCrawlProcess> processes = new LinkedList<>();
        for (VideoInfo videoInfo : videoInfoSet) {
            processes.add(ReplyPhaseProcess.createProcess(videoInfo.getOid()));
        }
        for (BaseCrawlProcess process : processes) {
            process.waitUntilNoTask();
        }
    }
}
