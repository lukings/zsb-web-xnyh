package cn.visolink.message.model;

import lombok.Data;

/**
 * 日志类
 * @Auther: wang gang
 * @Date: 2019/10/6 10:40
 */
@Data
public class SysLog {

    private String TaskName;

    private String StartTime;

    private int ResultStatus;

    private String ExecutTime;

    private String Note;

    private String MyUUID;

}
