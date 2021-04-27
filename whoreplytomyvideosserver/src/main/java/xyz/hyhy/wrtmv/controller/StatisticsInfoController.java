package xyz.hyhy.wrtmv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.hyhy.wrtmv.entity.result.ResultDTO;
import xyz.hyhy.wrtmv.entity.result.StatisticsInfo;
import xyz.hyhy.wrtmv.service.StatisticsInfoService;

import javax.annotation.Resource;
import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/api/statistics_info")
public class StatisticsInfoController {
    @Resource
    private StatisticsInfoService statisticsInfoService;

    @GetMapping("/sex")
    @ResponseBody
    public ResultDTO getSexCount(long uid) {
        List<StatisticsInfo> list = statisticsInfoService.getSexCount(uid);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }

    @GetMapping("/hour")
    @ResponseBody
    public ResultDTO getHourCount(long uid) {
        List<StatisticsInfo> list = statisticsInfoService.getPeriodCount(uid);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }

    @GetMapping("/level")
    @ResponseBody
    public ResultDTO getLevelCount(long uid) {
        List<StatisticsInfo> list = statisticsInfoService.getLevelCount(uid);
        if (list == null)
            return ResultDTO.noResult();
        else return ResultDTO.success(list);
    }
}
