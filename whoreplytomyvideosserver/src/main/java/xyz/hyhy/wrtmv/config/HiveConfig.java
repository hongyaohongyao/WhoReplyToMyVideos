package xyz.hyhy.wrtmv.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hive.HiveClient;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;


@Configuration
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".hive")
@Getter
@Setter
@AutoConfigureBefore(SystemProps.class)
public class HiveConfig {
    private String url;
    private String user;
    private String password;

    @Value("org.apache.hive.jdbc.HiveDriver")
    private String driverClassName;
    @Value("10")
    private int initialSize;
    @Value("10")
    private int minIdle;
    @Value("100")
    private int maxActive;
    @Value("1000")
    private int maxWait;
    @Value("-1")
    private int timeBetweenEvictionRunsMillis;
    @Value("30001")
    private int minEvictableIdleTimeMillis;
    @Value("select 1")
    private String validationQuery;
    @Value("true")
    private boolean testWhileIdle;
    @Value("false")
    private boolean testOnBorrow;
    @Value("false")
    private boolean testOnReturn;
    @Value("true")
    private boolean poolPreparedStatements;
    @Value(value = "20")
    private int maxPoolPreparedStatementPerConnectionSize;

    @Bean(name = "hiveDataSource")
    public DruidDataSource getHiveDataSource() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(url);
        datasource.setUsername(user);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);

        // pool configuration
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        return datasource;
    }

    @Bean("hiveClient")
    public HiveClient getHiveClient(DruidDataSource druidDataSource) {
        return new HiveClient(druidDataSource);
    }
}
