package xyz.hyhy.wrtmv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.hyhy.wrtmv.constant.ProjectInfoConst;


@Configuration
@ConfigurationProperties(ProjectInfoConst.ABB_NAME + ".my")
@Getter
@Setter
public class MyConfig {
    private String auth;
    @Value("hyhy2")
    private String nickname;

    @Bean("auth")
    public String auth() {
        return auth;
    }

    @Bean("nickname")
    public String nickName() {
        return nickname;
    }
}
