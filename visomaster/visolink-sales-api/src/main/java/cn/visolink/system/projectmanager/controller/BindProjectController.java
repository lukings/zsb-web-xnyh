package cn.visolink.system.projectmanager.controller;

import cn.visolink.exception.ResultBody;
import cn.visolink.system.projectmanager.service.BindProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

/**
 * @Author: Mr.Yu
 * @Date: 2021/10/12 10:48
 * @description
 * @Version 1.0
 */
@RestController
@RequestMapping("/bindProject")
public class BindProjectController {

    @Autowired
    private BindProjectService bindProjectService;

    /*@Autowired
    private ProjectMapper projectMapper;*/

    private final SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 实时查询失效的项目列表并修改失效项目改为禁止
     *
     * @return
     */
    @PostMapping(value = "/selectInvalidProject")
    public ResultBody selectInvalidProject() {
        String result = "OK";
        try {
            bindProjectService.selectInvalidProject(null);
        } catch (Exception e) {
            e.printStackTrace();
            result = "实时查询失效的项目列表异常！！！";
        }
        return ResultBody.success(result);
    }
}