package xyz.hyhy.wrtmv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import xyz.hyhy.wrtmv.utils.BaseUtils;

import javax.annotation.Resource;

@SpringBootApplication
public class WRTMVServerApplication {

    @Resource
    private BaseUtils baseUtils;

    public static void main(String[] args) {
        SpringApplication.run(WRTMVServerApplication.class, args);
    }

}
