package xyz.hyhy.wrtmv.utils.spider.utils;

import xyz.hyhy.wrtmv.utils.spider.base.AvailableProxyIterator;
import xyz.hyhy.wrtmv.utils.spider.base.ProxyIp;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderProxy;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderProxyProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProxyIPUtils {

    public static final long DEFAULT_INTERVAL = 51;

    public static void register(SpiderProxyProvider spiderProxyProvider, AvailableProxyIterator itr) {
        register(spiderProxyProvider, itr, DEFAULT_INTERVAL);
    }

    public static void register(SpiderProxyProvider spiderProxyProvider, AvailableProxyIterator itr, long defaultInterval) {
        itr.startGetProxy();
        while (itr.hasNext()) {
            registerOne(spiderProxyProvider, itr.next(), defaultInterval);
        }
    }

    public static void registerOne(SpiderProxyProvider spiderProxyProvider, ProxyIp proxyIp, long defaultInterval) {
        SpiderProxy spiderProxy = new SpiderProxy(proxyIp.getIp(), proxyIp.getPort(), defaultInterval);
        spiderProxyProvider.addProxy(spiderProxy);
    }

    public static void registerOne(SpiderProxyProvider spiderProxyProvider, ProxyIp proxyIp) {
        registerOne(spiderProxyProvider, proxyIp, DEFAULT_INTERVAL);
    }

    public static void saveUniqueIPToFile(AvailableProxyIterator[] allItr, String file) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (out == null)
            return;
        Set<String> set = new LinkedHashSet<>();
        for (AvailableProxyIterator itr : allItr) {
            itr.startGetProxy();
            while (itr.hasNext()) {
                ProxyIp proxyIp = itr.next();
                String ip = proxyIp.getIp();
                int port = proxyIp.getPort();
                set.add(ip + " " + port);
                System.out.println(ip + " " + port + " ok");
            }
        }
        for (String line : set) {
            out.println(line);
        }
        out.close();
    }

}
