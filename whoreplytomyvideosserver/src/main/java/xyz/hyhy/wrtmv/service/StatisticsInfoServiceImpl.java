package xyz.hyhy.wrtmv.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.hyhy.wrtmv.dao.StatisticsInfoDAO;
import xyz.hyhy.wrtmv.entity.result.StatisticsInfo;
import xyz.hyhy.wrtmv.process.ProcessMonitor;

import javax.annotation.Resource;
import java.util.List;

@Service("statisticsInfoService")
@Scope("prototype")
public class StatisticsInfoServiceImpl implements StatisticsInfoService {
    @Resource
    private StatisticsInfoDAO statisticsInfoDAO;
    @Resource
    private ProcessMonitor processMonitor;

    @Override
    public List<StatisticsInfo> getSexCount(long owner) {
        if (processMonitor.isProcessing(owner))
            return null;
        return statisticsInfoDAO.getInfos(owner, "sex");
    }

    @Override
    public List<StatisticsInfo> getPeriodCount(long owner) {
        if (processMonitor.isProcessing(owner))
            return null;
        return statisticsInfoDAO.getInfos(owner, "hour");
    }

    @Override
    public List<StatisticsInfo> getLevelCount(long owner) {
        if (processMonitor.isProcessing(owner))
            return null;
        return statisticsInfoDAO.getInfos(owner, "level");
    }
}
