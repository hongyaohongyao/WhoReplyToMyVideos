package xyz.hyhy.wrtmv.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderProxy;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderTaskService;
import xyz.hyhy.wrtmv.utils.spider.utils.NetUtils;

import java.io.IOException;

public class BiliBiliAPIUtils {
    private static Logger logger = LoggerFactory.getLogger(BiliBiliAPIUtils.class);
    public static final String VIDEO_API = "https://api.bilibili.com/x/space/arc/search?mid=%d&ps=%d&tid=0&pn=%d&keyword=&order=pubdate&jsonp=jsonp";
    public static final String REPLY_API = "https://api.bilibili.com/x/v2/reply?pn=%d&type=1&oid=%d&sort=0";

    private BiliBiliAPIUtils() {

    }

    public static String getReplyAPI(int page, long oid) {
        return String.format(REPLY_API, page, oid);
    }

    public static String getVideoAPI(long mid, int pageSize, int page) {
        return String.format(VIDEO_API, mid, pageSize, page);
    }

    public static JSONObject crawlReply(int page, long oid, SpiderTaskService spiderTaskService) {
        final JSONObject[] msg = {null};
        final Integer[] tryTime = {10};
        spiderTaskService.executeTask((proxyInfo) -> {
            String log = "评论区%d: 开始爬取第%d页, 使用";
            if (proxyInfo.getType() == SpiderProxy.TYPE_LOCALHOST)
                log += "localhost";
            else
                log += proxyInfo.getIp() + ":" + proxyInfo.getPort();
            logger.info(String.format(log, oid, page));
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            String url = getReplyAPI(page, oid);
            try {
                httpClient = NetUtils.createProxyClient(proxyInfo);
                response = NetUtils.getRequest(httpClient, url, 1000);
                HttpEntity entity = response.getEntity();
                Header contentType = entity.getContentType();
                if (contentType == null || !contentType.toString().contains("json")) {
                    throw new IOException();
                }
                msg[0] = JSONObject.parseObject(EntityUtils.toString(entity));
                proxyInfo.reward();
                return true;
            } catch (IOException ioe) {
                proxyInfo.punish();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return tryTime[0]-- <= 0;
            } finally {
                proxyInfo.refreshNextTime();
                NetUtils.closeQuietly(httpClient, response);
            }
        });
        JSONObject result = msg[0];
        if (result == null)
            return null;
        int code = result.getInteger("code");
        if (code != 0)
            return null;
        return result.getJSONObject("data");
    }

    public static JSONObject crawlVideoInfo(int page, long uid, SpiderTaskService spiderTaskService) {
        final JSONObject[] msg = {null};
        final Integer[] tryTime = {10};
        spiderTaskService.executeTask((proxyInfo) -> {
            String log = "用户视频列表%d: 开始爬取第%d页, 使用";
            if (proxyInfo.getType() == SpiderProxy.TYPE_LOCALHOST)
                log += "localhost";
            else
                log += proxyInfo.getIp() + ":" + proxyInfo.getPort();
            logger.info(String.format(log, uid, page));
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            String url = getVideoAPI(uid, 30, page);
            try {
                httpClient = NetUtils.createProxyClient(proxyInfo);
                response = NetUtils.getRequest(httpClient, url, 2001);
                HttpEntity entity = response.getEntity();
                Header contentType = entity.getContentType();
                if (contentType == null || !contentType.toString().contains("json")) {
                    throw new IOException();
                }
                msg[0] = JSONObject.parseObject(EntityUtils.toString(entity));
                proxyInfo.reward();
                return true;
            } catch (IOException ioe) {
                proxyInfo.punish();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return tryTime[0]-- <= 0;
            } finally {
                proxyInfo.refreshNextTime();
                NetUtils.closeQuietly(httpClient, response);
            }
        });
        JSONObject result = msg[0];
        if (result == null)
            return null;
        int code = result.getInteger("code");
        if (code != 0)
            return null;
        return result.getJSONObject("data");
    }
}
