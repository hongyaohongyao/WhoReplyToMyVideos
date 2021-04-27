package xyz.hyhy.wrtmv.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.hyhy.wrtmv.entity.result.ResultDTO;
import xyz.hyhy.wrtmv.service.SystemUserService;

import javax.annotation.Resource;

@Controller
@CrossOrigin
@RequestMapping("/api/user")
public class SystemUserController {
    @Resource
    private SystemUserService systemUserService;

    @GetMapping("/init")
    @ResponseBody
    public ResultDTO init(long uid) {
        if (systemUserService.process(uid) != null) {
            return ResultDTO.accept("is processing");
        }
        if (!systemUserService.exist(uid)) {
            systemUserService.registerSystemUser(uid);
            systemUserService.update(uid);
            return ResultDTO.accept("new user, initializing");
        }
        return ResultDTO.accept("exist user");
    }


    @GetMapping("/process")
    @ResponseBody
    public ResultDTO getProcess(long uid) {
        String process = systemUserService.process(uid);
        if (process != null)
            return ResultDTO.accept(process);
        return ResultDTO.success("完成");
    }

    @GetMapping("/update")
    @ResponseBody
    public ResultDTO update(long uid) {
        if (!systemUserService.exist(uid)) {
            return ResultDTO.noResult();
        }
        if (systemUserService.process(uid) != null) {
            return ResultDTO.accept("is processing");
        }
        if (systemUserService.canUpdate(uid)) {
            systemUserService.update(uid);
            return ResultDTO.success("updating user");
        }
        return ResultDTO.accept("has updated today");
    }
}
