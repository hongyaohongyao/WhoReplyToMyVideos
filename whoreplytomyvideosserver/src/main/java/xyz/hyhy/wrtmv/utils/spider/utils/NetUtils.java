package xyz.hyhy.wrtmv.utils.spider.utils;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import xyz.hyhy.wrtmv.utils.spider.base.ProxyIp;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class NetUtils {
    private NetUtils() {

    }

    public static boolean checkIp(ProxyIp proxyIp) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp.getIp(), proxyIp.getPort()));
        try {
            URLConnection httpCon = new URL("https://www.baidu.com/").openConnection(proxy);
            httpCon.setConnectTimeout(2000);
            httpCon.setReadTimeout(2000);
            int code = ((HttpURLConnection) httpCon).getResponseCode();
            if (code != 200)
                return false;
            InputStream inputStream = httpCon.getInputStream();
            byte[] bytes;
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String str = new String(bytes);

            ((HttpURLConnection) httpCon).disconnect();
            return str.contains("baidu");
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 根据SpiderProxy提供的代理信息生成代理客户端
     *
     * @param proxyInfo
     * @return 代理http客户端
     */
    public static CloseableHttpClient createProxyClient(SpiderProxy proxyInfo) {
        if (proxyInfo.getType() == SpiderProxy.TYPE_LOCALHOST) {
            return HttpClientBuilder.create().build();
        } else {
            HttpHost proxy = new HttpHost(proxyInfo.getIp(), proxyInfo.getPort());
            return HttpClientBuilder.create().disableAutomaticRetries().setProxy(proxy).build();
        }
    }

    public static CloseableHttpResponse getRequest(CloseableHttpClient httpClient, String url, int timeout) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
        return httpClient.execute(httpGet);
    }

    public static void closeQuietly(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        HttpClientUtils.closeQuietly(response);
        HttpClientUtils.closeQuietly(httpClient);
    }


}
