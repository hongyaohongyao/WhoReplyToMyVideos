package xyz.hyhy.wrtmv.service;

public interface SystemUserService {
    /**
     * 开启一次爬虫分析任务,更新分析数据
     *
     * @param uid
     */
    void update(long uid);

    /**
     * 查看任务进度
     *
     * @param uid
     * @return 当前任务进度消息, 无任务返回空值
     */
    String process(long uid);

    boolean exist(long uid);

    boolean canUpdate(long uid);

    void registerSystemUser(long uid);
}
