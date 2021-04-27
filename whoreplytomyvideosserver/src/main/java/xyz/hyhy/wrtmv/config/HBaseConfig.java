package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;

@Configuration("hBaseConfig")
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".hbase")
@Getter
@Setter
public class HBaseConfig {

    @Value("hdfs://hadoophost:9000/hbase")
    private String rootDir;
    @Value("hadoophost")
    private String zookeeperQuorum;

    public HBaseConfig() {
        System.out.println("HBaseConfig 配置完成");
    }

    @Bean
    public org.apache.hadoop.conf.Configuration hBaseConfiguration() {
        System.out.println("HBase配置 加载完成");
        org.apache.hadoop.conf.Configuration configuration = null;
        if (configuration == null) {
            configuration = HBaseConfiguration.create();
            configuration.set("hbase.rootdir", rootDir);
            configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
        }
        return configuration;
    }

}