package cn.visolink.common.example.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author YHX
 * @date 2021年11月24日 16:28
 */
@Data
public class UserOrg implements Serializable {

    private String id;

    private String OrgName;

    private String ListIndex;

    private String FullPath;

    private String PID;

    private String PName;

    private String ProjectID;

}
