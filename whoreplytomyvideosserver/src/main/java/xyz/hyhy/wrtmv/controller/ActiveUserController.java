package xyz.hyhy.wrtmv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.hyhy.wrtmv.entity.result.ActiveUser;
import xyz.hyhy.wrtmv.entity.result.ResultDTO;
import xyz.hyhy.wrtmv.service.ActiveUserService;

import javax.annotation.Resource;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/api/active_user")
public class ActiveUserController {
    @Resource
    private ActiveUserService activeUserService;

    @GetMapping("/new")
    @ResponseBody
    public ResultDTO getNewVisitor(long uid) {
        List<ActiveUser> list = activeUserService.getNewVisitor(uid, 1, 100);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }

    @GetMapping("/last")
    @ResponseBody
    public ResultDTO getLastActiveUser(long uid) {
        List<ActiveUser> list = activeUserService.getLastActiveUser(uid, 1, 100);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }

    @GetMapping("/replies")
    @ResponseBody
    public ResultDTO getMostReplies(long uid) {
        List<ActiveUser> list = activeUserService.getMostReplies(uid, 1, 100);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }

    @GetMapping("/videos")
    @ResponseBody
    public ResultDTO getMostVideos(long uid) {
        List<ActiveUser> list = activeUserService.getMostVideos(uid, 1, 100);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }
}
