package xyz.hyhy.wrtmv.utils.spider.addition;

import xyz.hyhy.wrtmv.utils.spider.base.AvailableProxyIterator;
import xyz.hyhy.wrtmv.utils.spider.utils.ProxyIPUtils;

public class SpiderAdditionTools {
    private SpiderAdditionTools() {

    }

    public static void getProxyIpFromKuaidaili(String file) {
        AvailableProxyIterator[] allItr = new AvailableProxyIterator[2];
        allItr[0] = new KuaidailiProxyItr(1, 3753, true);
        allItr[1] = new KuaidailiProxyItr(1, 3753, false);
        ProxyIPUtils.saveUniqueIPToFile(allItr, file);
    }
}
