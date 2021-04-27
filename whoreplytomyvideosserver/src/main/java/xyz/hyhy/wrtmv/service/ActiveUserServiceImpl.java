package xyz.hyhy.wrtmv.service;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.hyhy.wrtmv.dao.ActiveUserDAO;
import xyz.hyhy.wrtmv.entity.result.ActiveUser;
import xyz.hyhy.wrtmv.process.ProcessMonitor;

import javax.annotation.Resource;
import java.util.List;

@Service("activeUserService")
@Scope("prototype")
public class ActiveUserServiceImpl implements ActiveUserService {
    @Resource
    private ActiveUserDAO activeUserDAO;
    @Resource
    private ProcessMonitor processMonitor;

    @Override
    public List<ActiveUser> getNewVisitor(long owner, long start, long end) {
        if (processMonitor.isProcessing(owner))
            return null;
        return activeUserDAO.getUsersByRank(owner, start, end, ActiveUserDAO.BY_FIRST_TIME);
    }

    @Override
    public List<ActiveUser> getLastActiveUser(long owner, long start, long end) {
        if (processMonitor.isProcessing(owner))
            return null;
        return activeUserDAO.getUsersByRank(owner, start, end, ActiveUserDAO.BY_LAST_TIME);
    }

    @Override
    public List<ActiveUser> getMostReplies(long owner, long start, long end) {
        if (processMonitor.isProcessing(owner))
            return null;
        return activeUserDAO.getUsersByRank(owner, start, end, ActiveUserDAO.BY_REPLY_TIMES);
    }

    @Override
    public List<ActiveUser> getMostVideos(long owner, long start, long end) {
        if (processMonitor.isProcessing(owner))
            return null;
        return activeUserDAO.getUsersByRank(owner, start, end, ActiveUserDAO.BY_REPLY_VIDEOS);
    }
}
