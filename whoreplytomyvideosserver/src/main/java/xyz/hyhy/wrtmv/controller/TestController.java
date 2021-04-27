package xyz.hyhy.wrtmv.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.hyhy.wrtmv.utils.AppContextUtils;

@Controller
@CrossOrigin
public class TestController {

    @GetMapping("exit")
    public void test1() {
        AppContextUtils.exit();
    }
}
