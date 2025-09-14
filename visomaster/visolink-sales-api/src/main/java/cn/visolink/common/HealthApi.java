package cn.visolink.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@Slf4j
public class HealthApi {
    /**
     * 健康检查
     * @return
     */
    @GetMapping("/health")
    @ResponseBody
    private String getStandardResult(
    ) {
        return "ok";
    }
}
