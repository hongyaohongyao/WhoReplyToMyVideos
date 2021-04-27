package xyz.hyhy.wrtmv.utils.spider.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpiderTaskService {
    private static Logger logger = LoggerFactory.getLogger(SpiderTaskService.class);
    private SpiderProxyProvider proxyProvider;
    private boolean toRun;
    private ExecutorService pool;
    private int[] taskCount;

    public SpiderTaskService() {
        this(Executors.newCachedThreadPool());
    }

    public SpiderTaskService(ExecutorService pool) {
        this.pool = pool;
        init();
    }

    private void init() {
        this.taskCount = new int[]{0};
        this.toRun = true;
        this.proxyProvider = new SpiderProxyProvider();
    }

    public void addTask(SpiderTask task) {
        pool.execute(() -> {
            executeTask(task);
        });
    }

    private SpiderProxy getProxy() {
        SpiderProxy spiderProxy;
        synchronized (proxyProvider) {
            while (toRun && !proxyProvider.hasProxy()) {
                try {
                    proxyProvider.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            spiderProxy = this.proxyProvider.getProxy();
        }
        if (spiderProxy == null)
            return null;
        spiderProxy.using();
        long rest = spiderProxy.restTime();
        if (rest > 0) {
            try {
                Thread.sleep(rest);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return spiderProxy;
    }

    private void reuseProxy(SpiderProxy proxyInfo) {
        if (proxyInfo == null)
            return;
        if (proxyInfo.isValid()) {
            logger.info("重用ip " + proxyInfo.getIp() + ":" + proxyInfo.getPort());
            if (!proxyInfo.isWaiting())
                proxyInfo.refreshNextTime();
            synchronized (proxyProvider) {
                proxyProvider.addProxy(proxyInfo);
                proxyProvider.notifyAll();
            }
        } else {
            logger.warn("舍弃代理ip " + proxyInfo.getIp() + ":" + proxyInfo.getPort());
        }
    }

    public boolean hasTask() {
        synchronized (taskCount) {
            return taskCount[0] > 0;
        }
    }

    /**
     * 非多线程方式执行
     *
     * @param task
     * @return
     */
    public int executeTask(SpiderTask task) {
        if (!toRun)
            return 1;
        countUp();
        SpiderProxy proxyInfo = null;
        try {
            proxyInfo = getProxy();
            while (proxyInfo != null && !task.doTask(proxyInfo)) {
                reuseProxy(proxyInfo);
                proxyInfo = getProxy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            reuseProxy(proxyInfo);
            countDown();
        }
        return 0;
    }

    private void countUp() {
        synchronized (taskCount) {
            taskCount[0]++;
        }
    }

    private void countDown() {
        synchronized (taskCount) {
            if (--taskCount[0] <= 0)
                taskCount.notifyAll();
        }
    }

    public void on() {
        if (toRun)
            return;
        toRun = true;
    }

    public void stop() {
        toRun = false;
    }

    public SpiderProxyProvider getProxyProvider() {
        return this.proxyProvider;
    }

}
