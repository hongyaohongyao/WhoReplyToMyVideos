package xyz.hyhy.wrtmv.service;

import xyz.hyhy.wrtmv.entity.result.StatisticsInfo;

import java.util.List;

public interface StatisticsInfoService {
    /**
     * @param owner
     * @return 性别统计
     */
    List<StatisticsInfo> getSexCount(long owner);

    /**
     * @return 每天每个小时内的回复数
     */
    List<StatisticsInfo> getPeriodCount(long owner);

    /**
     * @param owner
     * @return 等级统计
     */
    List<StatisticsInfo> getLevelCount(long owner);
}
