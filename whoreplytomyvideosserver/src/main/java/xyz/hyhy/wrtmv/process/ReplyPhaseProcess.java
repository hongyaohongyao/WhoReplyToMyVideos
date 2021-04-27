package xyz.hyhy.wrtmv.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import xyz.hyhy.wrtmv.dao.BiliBiliUserDAO;
import xyz.hyhy.wrtmv.dao.ReplyDAO;
import xyz.hyhy.wrtmv.entity.BiliBiliUser;
import xyz.hyhy.wrtmv.entity.Reply;
import xyz.hyhy.wrtmv.utils.AppContextUtils;
import xyz.hyhy.wrtmv.utils.BiliBiliAPIUtils;
import xyz.hyhy.wrtmv.utils.CommonUtils;
import xyz.hyhy.wrtmv.utils.dotask.RunAndWait;

import java.util.SortedSet;
import java.util.TreeSet;

@Scope("prototype")
public class ReplyPhaseProcess extends BaseCrawlProcess {

    private static final Logger logger = LoggerFactory.getLogger(ReplyPhaseProcess.class);
    private final BiliBiliUserDAO biliBiliUserDAO;
    private final ReplyDAO replyDAO;

    @Getter
    private long oid;
    //task
    private final SortedSet<Reply> replySet;
    private final SortedSet<BiliBiliUser> userSet;

    private ReplyPhaseProcess(long oid) {
        this(oid, true);
    }

    private ReplyPhaseProcess(long oid, boolean startNow) {
        logger.info("准备爬取评论区: " + oid);
        this.oid = oid;
        replySet = new TreeSet<>();
        userSet = new TreeSet<>();
        this.biliBiliUserDAO = AppContextUtils.getBean(BiliBiliUserDAO.class);
        this.replyDAO = AppContextUtils.getBean(ReplyDAO.class);
        if (startNow)
            on();
    }

    public static ReplyPhaseProcess createProcess(long oid) {
        return new ReplyPhaseProcess(oid);
    }

    public static ReplyPhaseProcess createProcess(long oid, boolean startNow) {
        return new ReplyPhaseProcess(oid, startNow);
    }


    private void clearSet() {
        replySet.clear();
        userSet.clear();
    }

    private JSONObject crawlReply(int page, long oid) {
        return BiliBiliAPIUtils.crawlReply(page, oid, getSpiderTaskService());
    }

    @Override
    protected void beforeOn() {
        clearSet();
    }

    @Override
    protected void crawlProcess() {
        try {
            JSONObject result = crawlReply(1, oid);
            if (result == null) {
                logger.warn("在评论区" + oid + " 未爬取到数据，退出任务");
                return;
            }
            JSONObject pageInfo = result.getJSONObject("page");
            int size = pageInfo.getInteger("size");
            int count = pageInfo.getInteger("count");
            int page = count / size + 1;
            JSONArray replies = result.getJSONArray("replies");
            RunAndWait runAndWait = CommonUtils.getRunAndWaitInstance(getPool());
            for (int i = 2; i <= page; i++) {
                int p = i;
                runAndWait.add(() -> crawlPageProcess(p));
            }
            dealReplies(replies);
            logger.info(String.format("等待评论区%d爬取结束", oid));
            runAndWait.waitUntilNoTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void crawlPageProcess(int page) {
        JSONObject result = crawlReply(page, oid);
        logger.info(String.format("评论区%d: 成功爬取第%d页", oid, page));
        if (result == null)
            return;
        JSONArray replies = result.getJSONArray("replies");
        dealReplies(replies);
    }

    private void dealReplies(JSONArray replies) {
        if (replies == null)
            return;
        try {
            for (Object obj : replies) {
                JSONObject reply0 = (JSONObject) obj;
                long rpid = reply0.getLong("rpid");
                long parent = reply0.getLong("parent");
                long mid = reply0.getLong("mid");
                long ctime = reply0.getLong("ctime");
                String content = reply0.getJSONObject("content").getString("message");
                JSONObject member = reply0.getJSONObject("member");
                dealMember(member);
                Reply reply = new Reply();
                reply.setRpid(rpid);
                reply.setOid(oid);
                reply.setParent(parent);
                reply.setContent(content);
                reply.setMid(mid);
                reply.setCtime(ctime);
                synchronized (replySet) {
                    replySet.add(reply);
                }
                //处理楼中楼回复
                JSONArray subReplies = reply0.getJSONArray("replies");
                dealReplies(subReplies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理回复中的member数据
     *
     * @param member
     */
    private void dealMember(JSONObject member) {
        if (member == null)
            return;
        long mid = member.getLong("mid");
        String name = member.getString("uname");
        String face = member.getString("avatar");
        String sex = member.getString("sex");
        String sign = member.getString("sign");
        JSONObject levelInfo = member.getJSONObject("level_info");
        int level = levelInfo.getInteger("current_level");
        BiliBiliUser user = new BiliBiliUser();
        user.setUid(mid);
        user.setFace(face);
        user.setLevel(level);
        user.setName(name);
        user.setSex(sex);
        user.setSign(sign);
        synchronized (userSet) {
            userSet.add(user);
        }
    }

    @Override
    protected void saveResultToDataBase() {
        logger.info("爬取任务完成，共找到用户" + userSet.size() + ", 回复" + replySet.size());
        if (userSet.size() > 0)
            biliBiliUserDAO.saveUsers(userSet);
        if (replySet.size() > 0)
            replyDAO.saveReplies(replySet);
    }
}
