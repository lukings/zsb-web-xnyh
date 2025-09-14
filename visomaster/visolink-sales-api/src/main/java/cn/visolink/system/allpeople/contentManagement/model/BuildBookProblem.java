package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * @Author: FuYong
 * @ProjectName: visolink
 * @Description:
 * @Date: Created in 2020/5/15
 */
@Data
public class BuildBookProblem {
    private Integer rownum;
    private String id;
    private String buildBookID;
    private String buildBookName;
    private String projectId;
    private String projectName;
    private String projectNum;//项目编码
    private String problemDescribe;
    private String problemAnswer;
    private String createUserId;
    private String createUserName;
    private String createTime;
    private String editUserId;
    private String editTime;
    private String statusName;
    private Integer clickNumber;
    private Integer status;
    private Integer listIndex;
    private Integer isDel;

    /**
     * 获取分析数据
     * @param
     * @return
     */
    public Object[] toExproData(){
        return new Object[]{
                getRownum(),getProjectName(),getProjectNum(),getBuildBookName(),getListIndex(),getProblemDescribe(),
                getProblemAnswer(),getClickNumber(),getCreateUserName(),getCreateTime(),
                getStatusName()
        };
    }
}
