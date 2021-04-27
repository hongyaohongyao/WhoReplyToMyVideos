package xyz.hyhy.wrtmv.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.hyhy.wrtmv.dao.ActiveUserDAO;
import xyz.hyhy.wrtmv.dao.StatisticsInfoDAO;
import xyz.hyhy.wrtmv.dao.SystemUserDAO;
import xyz.hyhy.wrtmv.entity.SystemUser;
import xyz.hyhy.wrtmv.process.BaseCrawlProcess;
import xyz.hyhy.wrtmv.process.ProcessMonitor;
import xyz.hyhy.wrtmv.process.VideoInfoPhaseProcess;
import xyz.hyhy.wrtmv.utils.CommonUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.ExecutorService;

@Service("systemUserService")
@Scope("prototype")
public class SystemUserServiceImpl implements SystemUserService {
    @Resource
    private ProcessMonitor processMonitor;
    @Resource
    private ExecutorService publicThreadPool;
    @Resource
    private ActiveUserDAO activeUserDAO;
    @Resource
    private StatisticsInfoDAO statisticsInfoDAO;
    @Resource
    private SystemUserDAO systemUserDAO;

    @Override
    public void update(long uid) {
        SystemUser systemUser = systemUserDAO.getSystemUser(uid);
        if (systemUser == null)
            return;
        if (processMonitor.isProcessing(uid))
            return;
        processMonitor.setProcess(uid, String.format("准备爬取用户id%d的评论区数据", uid));
        publicThreadPool.execute(() -> {
            //爬虫阶段
            BaseCrawlProcess p0 = VideoInfoPhaseProcess.createProcess(uid);
            p0.waitUntilNoTask();
            //分析阶段
            processMonitor.setProcess(uid, String.format("分析阶段1: 正在分析用户id%d的评论区数据", uid));
            analysis(uid);
            processMonitor.complete(uid);
            systemUser.setUpdateTime(new Date().getTime());
            systemUserDAO.saveSystemUser(systemUser);
        });
    }

    private void analysis(long owner) {
        processMonitor.setProcess(owner, String.format("分析阶段1: 正在分析用户id%d的评论区用户统计数据", owner));
        activeUserDAO.analysis(owner);
        processMonitor.setProcess(owner, String.format("分析阶段2: 正在分析用户id%d的评论区综合统计数据", owner));
        statisticsInfoDAO.analysis(owner);
        processMonitor.setProcess(owner, String.format("分析用户id%d完成", owner));
    }

    @Override
    public String process(long uid) {
        return processMonitor.getProcess(uid);
    }

    @Override
    public boolean exist(long uid) {
        return systemUserDAO.exist(uid);
    }

    @Override
    public boolean canUpdate(long uid) {
        SystemUser systemUser = systemUserDAO.getSystemUser(uid);
        if (systemUser == null) {
            return false;
        }
        return CommonUtils.getToDay() > systemUser.getUpdateTime();
    }

    @Override
    public void registerSystemUser(long uid) {
        if (systemUserDAO.exist(uid))
            return;
        SystemUser systemUser = new SystemUser();
        systemUser.setUid(uid);
        systemUser.setUpdateTime(0);
        systemUserDAO.saveSystemUser(systemUser);
    }


}
