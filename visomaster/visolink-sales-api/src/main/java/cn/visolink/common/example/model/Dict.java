package cn.visolink.common.example.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author YHX
 * @date 2021年11月23日 16:02
 */
@Data
public class Dict  implements Serializable {
    
    /**
     *   主键ID
     */
    private String ID;

    /**
     *   排序号
     */
    private String ListIndex;
    
    /**
     *   字典编码
     */ 
    private String DictCode;
    
    /**
     *   字典名称
     */
    private String DictName;
    
    /**
     *   字典类型
     */
    private String DictType;
    
    private int  DictTypeMode;

    private int ParamMode;

    private String Unit;
}
