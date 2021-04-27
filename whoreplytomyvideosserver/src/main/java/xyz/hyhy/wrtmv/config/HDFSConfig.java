package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;

@Configuration
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".hdfs")
@Getter
@Setter
@AutoConfigureBefore(SystemProps.class)
public class HDFSConfig {

    @Value("hdfs://hadoophost:9000")
    private String defaultFs;

    @Value("/wrtmv")
    private String hdfsBasePath;

    @Value("hadoop")
    private String user;

    @Bean("hadoopConfiguration")
    public org.apache.hadoop.conf.Configuration hadoopConfiguration() {
        System.setProperty("HADOOP_USER_NAME", user);
        org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
        configuration.set("fs.defaultFS", defaultFs);
        configuration.setBoolean("dfs.support.append", true);
        configuration.setBoolean("dfs.client.block.write.replace-datanode-on-failure.policy", true);
        configuration.set("dfs.client.block.write.replace-datanode-on-failure.policy", "NEVER");
        configuration.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        System.out.println("FsIOUtils加载完毕。");
        return configuration;
    }

    public String finalHdfsBasePath() {
        return defaultFs + hdfsBasePath;
    }


}
