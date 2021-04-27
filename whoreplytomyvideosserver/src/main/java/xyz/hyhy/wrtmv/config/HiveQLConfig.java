package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;
import xyz.hyhy.wrtmv.utils.YamlPropertySourceFactory;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(ProjectInfoConst.ABB_NAME)
@PropertySource(value = "classpath:hiveql.yml", factory = YamlPropertySourceFactory.class)
@Getter
@Setter
public class HiveQLConfig {
    //建表语句
    private String createUsersTable;
    private String createVideosTable;
    private String createRepliesTable;
    private String createStatisticsUsersTable;
    private String createStatisticsInfoTable;
    //插入语句
    private String insertUsersStatistics;
    private String insertStatisticsInfo;
    //查询语句
    private String selectUsersStatistics;
    private String selectStatisticsInfo;


    public String getInsertStatisticsInfo(long owner) {
        return insertStatisticsInfo.replaceAll("<owner_id>", Long.toString(owner));
    }

    public String getInsertUsersStatistics(long owner) {
        return insertUsersStatistics.replaceAll("<owner_id>", Long.toString(owner));
    }

    public String getSelectUsersStatistics(long owner, long start, long end, String by) {
        return selectUsersStatistics
                .replaceAll("<owner_id>", Long.toString(owner))
                .replaceAll("<rank_by>", by)
                .replaceAll("<start>", Long.toString(start))
                .replaceAll("<end>", Long.toString(end));
    }

    public String getStatisticsInfo(long owner, String item) {
        return selectStatisticsInfo
                .replaceAll("<owner_id>", Long.toString(owner))
                .replaceAll("<item>", item);
    }

    @PostConstruct
    public void created() {
        System.out.println(createUsersTable);
    }
}
