package xyz.hyhy.wrtmv.utils.spider.base;

import java.util.PriorityQueue;
import java.util.Queue;

public class SpiderProxyProvider {
    private Queue<SpiderProxy> usableProxy;

    public SpiderProxyProvider() {
        this.usableProxy = new PriorityQueue<>();
    }


    public void addProxy(SpiderProxy spiderProxy) {
        this.usableProxy.add(spiderProxy);
    }

    public boolean hasProxy() {
        return usableProxy.peek() != null;
    }

    public SpiderProxy getProxy() {
        return usableProxy.poll();
    }

    public int size() {
        synchronized (usableProxy) {
            return usableProxy.size();
        }
    }
}
