package xyz.hyhy.wrtmv.process;

import java.util.concurrent.ConcurrentHashMap;

public class ProcessMonitor {
    private ConcurrentHashMap<Long, String> map;

    public ProcessMonitor() {
        map = new ConcurrentHashMap<>();
    }

    public void setProcess(long uid, String state) {
        map.put(uid, state);
    }

    public String getProcess(long uid) {
        return map.get(uid);
    }

    public void complete(long uid) {
        map.remove(uid);
    }

    public boolean isProcessing(long uid) {
        return map.get(uid) != null;
    }
}
