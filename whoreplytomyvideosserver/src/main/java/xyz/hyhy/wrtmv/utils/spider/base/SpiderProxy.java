package xyz.hyhy.wrtmv.utils.spider.base;

public class SpiderProxy implements Comparable<SpiderProxy> {
    public static final int TYPE_LOCALHOST = 1;
    public static final int TYPE_PROXY = 0;

    protected String ip;
    protected int port;
    protected long interval;
    private long when;
    private int score;
    private boolean waiting;
    private int type;

    public void reward() {
        reward(2);
    }

    public void reward(int score) {
        this.score += score;
    }

    public void punish() {
        punish(1);
    }

    public void punish(int score) {
        this.score -= score;
    }

    public SpiderProxy(long interval) {
        this(null, 0, interval, TYPE_LOCALHOST);
    }

    public SpiderProxy(String ip, int port, long interval) {
        this(ip, port, interval, TYPE_PROXY);
    }

    public SpiderProxy(String ip, int port, long interval, int type) {
        init();
        this.ip = ip;
        this.port = port;
        this.interval = interval;
        this.type = type;
    }

    public void init() {
        score = 1;
        used();
    }

    public int getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long restTime() {
        long rest = when - System.currentTimeMillis();
        return rest < 0 ? 0 : rest;
    }

    public boolean isValid() {
        return score > 0;
    }

    public void refreshNextTime(long delay) {
        this.when = System.currentTimeMillis() + delay;
        this.waiting = true;
    }

    public void refreshNextTime() {
        refreshNextTime(interval);
    }

    public void used() {
        refreshNextTime(0);
    }

    public void using() {
        this.waiting = false;
    }

    public boolean isWaiting() {
        return waiting;
    }


    @Override
    public int compareTo(SpiderProxy o) {
        if (type == TYPE_LOCALHOST && restTime() <= 0)
            return -1;
        if (o.type == TYPE_LOCALHOST && o.restTime() <= 0)
            return 1;
        return Long.compare(when, o.when);
    }

}
