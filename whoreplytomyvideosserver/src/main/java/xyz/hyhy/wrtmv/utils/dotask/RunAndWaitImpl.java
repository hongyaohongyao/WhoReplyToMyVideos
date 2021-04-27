package xyz.hyhy.wrtmv.utils.dotask;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunAndWaitImpl implements RunAndWait {
    private int[] taskCount;
    @Getter
    @Setter
    private ExecutorService pool;

    public RunAndWaitImpl() {
        this(Executors.newCachedThreadPool());
    }

    public RunAndWaitImpl(ExecutorService pool) {
        this.pool = pool;
        init();
    }

    private void init() {
        taskCount = new int[]{0};
    }

    @Override
    public void add(Runnable task) {
        countUp();
        pool.execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDown();
            }
        });
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
