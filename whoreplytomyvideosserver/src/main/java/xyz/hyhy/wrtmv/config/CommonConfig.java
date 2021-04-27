package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;
import xyz.hyhy.wrtmv.process.ProcessMonitor;
import xyz.hyhy.wrtmv.utils.spider.addition.UniqueProxyItr;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderProxy;
import xyz.hyhy.wrtmv.utils.spider.base.SpiderTaskService;
import xyz.hyhy.wrtmv.utils.spider.utils.ProxyIPUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".common")
@Getter
@Setter
@Lazy(false)
public class CommonConfig {
    private static final Logger logger = LoggerFactory.getLogger(CommonConfig.class);

    @Value("60")
    private long spiderInterval;

    @Value("proxy_ip.ipsrc")
    private String proxyIpFile;

    @Bean("publicThreadPool")
    public ExecutorService getPublicThreadPool() {
        return Executors.newCachedThreadPool();
    }

    @Bean("spiderTaskService")
    public SpiderTaskService getSpiderTaskService() {
        SpiderTaskService spiderTaskService = new SpiderTaskService();
        spiderTaskService.getProxyProvider().addProxy(new SpiderProxy(spiderInterval));
        File file = new File(proxyIpFile);
        if (file.exists()) {
            logger.info("找到可用代理ip文件" + proxyIpFile);
            UniqueProxyItr itr = new UniqueProxyItr(proxyIpFile);
            ProxyIPUtils.register(spiderTaskService.getProxyProvider(), itr, spiderInterval);
            logger.info("载入可用ip " + spiderTaskService.getProxyProvider().size() + " 个");
        }
        spiderTaskService.on();
        return spiderTaskService;
    }

    @Bean("processMonitor")
    public ProcessMonitor getProcessMonitor() {
        return new ProcessMonitor();
    }

}
