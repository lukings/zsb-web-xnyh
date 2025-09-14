package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ParamDesc
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/10 13:43
 **/
@Data
public class ParamDesc {

    private String orgLevel;

    private List<String> proIds;

    private List<String> orgIds;

    private List<String> toUrls;

    private List<String> extenTypes;

    private String extenActivityName;

    private String jumpToName;

    private String creator;

    private String pageIndex;

    private String pageSize;

    private String userName;

    private String userId;
}
