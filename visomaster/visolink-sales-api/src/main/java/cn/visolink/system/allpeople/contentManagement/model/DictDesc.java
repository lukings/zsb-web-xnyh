package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DictDesc
 * @Author wanggang
 * @Description //字典
 * @Date 2020/10/20 15:46
 **/
@Data
public class DictDesc {

    private String ID;//id

    private String DictCode;//code

    private String DictName;//描述

    private List<DictDesc> children;//子集

    private List<String> childrenDictCodes;//子集code
}
