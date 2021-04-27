package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".system-info")
@Getter
@Setter
@AutoConfigureBefore(HBaseConfig.class)
public class SystemProps {

    @Value(ProjectInfoConst.DEFAULT_USER)
    private String user;

    @Value("replies")
    private String replyTable;

    @Value(ProjectInfoConst.ABB_NAME)
    private String hBaseNamespace;

    @Value("users")
    private String usersTable;

    @Value("videos")
    private String videosTable;

    @Value("usersstatistics")
    private String usersStatisticsTable;

    @Value("statisticsinfo")
    private String statisticsInfoTable;

    @Value("systemuser")
    private String systemUserTable;

    @Value(ProjectInfoConst.ABB_NAME)
    private String hiveDataBaseName;

    @Value("false")
    private boolean workOnWindows;

    @PostConstruct
    private void init() {
        if (workOnWindows) {
            System.out.println("使用windows环境测试");
            System.setProperty("hadoop.home.dir", "C:\\hongyao\\templibs\\winutils");
        }
        System.out.println("systemProps ok");
    }
}
