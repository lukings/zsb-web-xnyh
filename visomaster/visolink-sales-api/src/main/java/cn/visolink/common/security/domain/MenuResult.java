package cn.visolink.common.security.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName MenuResult
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/9/26 14:41
 **/
@NoArgsConstructor
@Data
public class MenuResult {

    private int code;
    private String messages;
    private List<String> data;
}
