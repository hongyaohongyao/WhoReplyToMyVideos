package xyz.hyhy.wrtmv.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import xyz.hyhy.wrtmv.dao.ActiveUserDAO;
import xyz.hyhy.wrtmv.dao.BiliBiliUserDAO;
import xyz.hyhy.wrtmv.entity.BiliBiliUser;
import xyz.hyhy.wrtmv.process.BaseCrawlProcess;
import xyz.hyhy.wrtmv.process.ProcessMonitor;
import xyz.hyhy.wrtmv.process.VideoInfoPhaseProcess;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

@Service("biliBiliUserService")
@Scope("prototype")
public class BiliBiliUserServiceImpl implements BiliBiliUserService {

    @Resource
    private BiliBiliUserDAO biliBiliUserDAO;


    @Override
    public BiliBiliUser queryUser(long mid) {
        return biliBiliUserDAO.getUser(mid);
    }
}
