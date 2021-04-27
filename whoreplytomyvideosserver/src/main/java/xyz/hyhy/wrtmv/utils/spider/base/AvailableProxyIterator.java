package xyz.hyhy.wrtmv.utils.spider.base;

import java.util.Iterator;

public interface AvailableProxyIterator extends Iterator<ProxyIp> {
    void startGetProxy();
}
