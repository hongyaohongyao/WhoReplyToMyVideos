package xyz.hyhy.wrtmv.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.hyhy.wrtmv.utils.AppContextUtils;
import xyz.hyhy.wrtmv.utils.dotask.WaitForTasks;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderTaskService;

import java.util.concurrent.ExecutorService;

public abstract class BaseCrawlProcess implements WaitForTasks {

    private static final Logger logger = LoggerFactory.getLogger(BaseCrawlProcess.class);
    private final ExecutorService pool;
    private SpiderTaskService spiderTaskService;
    private final int[] taskCount;
    protected final ProcessMonitor processMonitor;

    protected BaseCrawlProcess() {
        this.taskCount = new int[]{0};
        this.pool = (ExecutorService) AppContextUtils.getBean("publicThreadPool");
        this.spiderTaskService = AppContextUtils.getBean(SpiderTaskService.class);
        this.processMonitor = AppContextUtils.getBean(ProcessMonitor.class);
    }


    protected abstract void beforeOn();

    protected abstract void crawlProcess();

    protected abstract void saveResultToDataBase();

    protected ExecutorService getPool() {
        return pool;
    }

    protected SpiderTaskService getSpiderTaskService() {
        return spiderTaskService;
    }

    protected void countUp() {
        synchronized (taskCount) {
            taskCount[0]++;
        }
    }

    protected void countDown() {
        synchronized (taskCount) {
            if (--taskCount[0] <= 0)
                taskCount.notifyAll();
        }
    }

    public boolean on() {
        synchronized (taskCount) {
            if (taskCount[0] > 0)
                return false;
        }
        beforeOn();
        countUp();
        pool.execute(() -> {
            try {
                crawlProcess();
                saveResultToDataBase();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDown();
            }
        });
        return true;
    }

    @Override
    public void waitUntilNoTask() {
        synchronized (taskCount) {
            while (taskCount[0] > 0) {
                try {
                    taskCount.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
