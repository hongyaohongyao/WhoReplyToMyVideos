package xyz.hyhy.wrtmv.dao;

import xyz.hyhy.wrtmv.entity.result.StatisticsInfo;

import java.util.List;

public interface StatisticsInfoDAO {
    void analysis(long owner);

    List<StatisticsInfo> getInfos(long owner, String item);
}
