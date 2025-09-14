package cn.visolink.system.channel.model.vo;

import cn.visolink.system.channel.model.TaskCustomer;
import cn.visolink.system.channel.model.TaskMember;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 任务表
 * @TableName b_task
 */
@Data
public class TaskVo implements Serializable {
    /**
     * 主键
     */
    private String id;
    
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型id
     */
    private String taskTypeId;

    /**
     * 任务类型名称
     */
    private String taskTypeName;

    /**
     * 任务开始时间
     */
    private String startTime;

    /**
     * 任务结束时间
     */
    private String endTime;

    /**
     * 任务地点
     */
    private String taskArea;


    /**
     * 任务地点名称
     */
    private String taskAreaName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 县
     */
    private String county;

    /**
     * 街道
     */
    private String street;

    /**
     * 报备指标
     */
    private Integer reportNum;

    /**
     * 拜访指标
     */
    private Integer visitNum;

    /**
     * 到访指标
     */
    private Integer arriveNum;

    /**
     * 成交指标
     */
    private Integer dealNum;

    /**
     * 团队id
     */
    private String teamId;

    /**
     * 团队名称
     */
    private String teamName;

    /**
     * 所属项目
     */
    private String projectId;

    /**
     * 所属项目
     */
    private String projectName;

    /**
     * 所属组织
     */
    private String orgId;

    /**
     * 是否删除：0 未删除、1 已删除
     */
    private Integer isDel;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 报备人姓名
     */
    private String employeeName;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private String modifyTime;

    /**
     * 任务报备数
     */
    private Integer taskReport;

    /**
     * 媒体大类ID
     */
    private String mainMediaId;

    /**
     * 媒体大类描述
     */
    private String mainMediaName;

    /**
     * 媒体子类ID
     */
    private String subMediaId;

    /**
     * 媒体子类描述
     */
    private String subMediaName;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 任务状态编码 1: 未开始 2:进行中 3：已结束
     */
    private String taskStatus;

    /**
     * 任务状态名称
     */
    private String taskStatusDesc;

    /**
     * 报备完成量
     */
    private Integer reportCompleteNum;

    /**
     * 报备完成率
     */
    private String reportCompleteRate;

    /**
     * 报备完成量占比
     */
    private String reportCompleteProportion;

    /**
     * 拜访完成量
     */
    private Integer visitCompleteNum;

    /**
     * 拜访完成率
     */
    private String visitCompleteRate;

    /**
     * 拜访完成量占比
     */
    private String visitCompleteProportion;

    /**
     * 到访完成量
     */
    private Integer arriveCompleteNum;

    /**
     *到访完成率
     */
    private String arriveCompleteRate;

    /**
     * 到访完成量占比
     */
    private String arriveCompleteProportion;

    /**
     * 三个一完指标
     */
    private Integer threeOneNum;
    /**
     * 三个一完成量
     */
    private Integer threeOneCompleteNum;
    /**
     * 三个一完成率
     */
    private String threeOneCompleteRate;
    /**
     * 三个一完成占比
     */
    private String threeOneCompleteProportion;

    private String rownum;

    private String isAll;

    private List<TaskMember> memberList;

    private List<String> memberIdList;
    
    /**
     * 成员姓名（取memberList中第一个成员的姓名）
     */
    private String memberName;
    /**
     * 面坐标
     */
    private String drawLatLon;

    private String type;

    private static final long serialVersionUID = 1L;

    /**
     * 父任务ID
     */
    private String parentId;
    /**
     * 是否与该项目的报备客户进行判重
     */
    private Integer isDupChecked;
    

    /**
     * 成交完成量
     */
    private Integer dealCompleteNum;
    
    /**
     * 成交完成率
     */
    private String dealCompleteRate;
    
    /**
     * 成交完成占比
     */
    private String dealCompleteProportion;

    /**
     * 首访完成量
     */
    private Integer firstVisitNum;

    
    /**
     * 首访完成量
     */
    private Integer firstVisitCompleteNum;
    
    /**
     * 首访完成率
     */
    private String firstVisitCompleteRate;
    
    /**
     * 首访完成占比
     */
    private String firstVisitCompleteProportion;
    
    /**
     * 复访指标
     */
    private Integer repeatVisitNum;
    
    /**
     * 复访完成量
     */
    private Integer repeatVisitCompleteNum;
    
    /**
     * 复访完成率
     */
    private String repeatVisitCompleteRate;
    
    /**
     * 复访完成占比
     */
    private String repeatVisitCompleteProportion;


    /**
     * 标记指标
     */
    private Integer tagNum;

    /**
     * 标记完成量
     */
    private Integer tagCompleteNum;

    /**
     * 标记完成率
     */
    private String tagCompleteRate;

    /**
     * 标记完成占比
     */
    private String tagCompleteProportion;
    

    private List<TaskCustomer> customerList;


    private String parentTaskName;

    private List<TaskVo> taskVoListZrw;

        /**
     * 子任务数量
     */
    private Integer subTaskCount;

    /**
     * 子任务列表
     */
    private List<TaskVo> subTaskList;

    private String status;




    /**
     * 任务管理台账
     *
     * @param
     * @param taskTypeId
     * @return
     */
    public Object[] toData1(List<String> fileds, String taskTypeId) {
        List met = new ArrayList();
        
        // 基础字段
        if(fileds.contains("rowNum")){
            met.add(getRownum());
        }
        if(fileds.contains("projectName")){
            met.add(getProjectName());
        }
        if(fileds.contains("taskName")){
            met.add(getTaskName());
        }
        if(fileds.contains("employeeName")){
            met.add(getEmployeeName());
        }
        if(fileds.contains("memberName")){
            met.add(getMemberName());
        }
        if(fileds.contains("createTime")){
            met.add(getCreateTime());
        }
        if(fileds.contains("startTime")){
            met.add(getStartTime());
        }
        if(fileds.contains("endTime")){
            met.add(getEndTime());
        }
        if(fileds.contains("teamName")){
            met.add(getTeamName());
        }
        
        // 报备相关字段
        if(fileds.contains("reportNum")){
            met.add(getReportNum());
        }
        if(fileds.contains("reportCompleteNum")){
            met.add(getReportCompleteNum());
        }
        if(fileds.contains("reportCompleteRate")){
            met.add(getReportCompleteRate());
        }
        if(fileds.contains("reportCompleteProportion")){
            met.add(getReportCompleteProportion());
        }
        
        // 拜访相关字段
        if(fileds.contains("visitNum")){
            met.add(getVisitNum());
        }
        if(fileds.contains("visitCompleteNum")){
            met.add(getVisitCompleteNum());
        }
        if(fileds.contains("visitCompleteRate")){
            met.add(getVisitCompleteRate());
        }
        if(fileds.contains("visitCompleteProportion")){
            met.add(getVisitCompleteProportion());
        }
        
        // 到访相关字段（任务类型1）
        if ("1".equals(taskTypeId)) {
            if(fileds.contains("arriveNum")){
                met.add(getArriveNum());
            }
            if(fileds.contains("arriveCompleteNum")){
                met.add(getArriveCompleteNum());
            }
            if(fileds.contains("arriveCompleteRate")){
                met.add(getArriveCompleteRate());
            }
            if(fileds.contains("arriveCompleteProportion")){
                met.add(getArriveCompleteProportion());
            }
        } else {
                    // 三个一相关字段（任务类型2）
        if(fileds.contains("threeOneNum")){
            met.add(getThreeOneNum());
        }
        if(fileds.contains("threeOneCompleteNum")){
            met.add(getThreeOneCompleteNum());
        }
        if(fileds.contains("threeOneCompleteRate")){
            met.add(getThreeOneCompleteRate());
        }
        if(fileds.contains("threeOneCompleteProportion")){
            met.add(getThreeOneCompleteProportion());
        }
        // 兼容其他可能的字段名
        if(fileds.contains("三个一完成量占比")){
            met.add(getThreeOneCompleteProportion());
        }
        }
        
        // 成交相关字段
        if(fileds.contains("dealNum")){
            met.add(getDealNum());
        }
        if(fileds.contains("dealCompleteNum")){
            met.add(getDealCompleteNum());
        }
        if(fileds.contains("dealCompleteRate")){
            met.add(getDealCompleteRate());
        }
        if(fileds.contains("dealCompleteProportion")){
            met.add(getDealCompleteProportion());
        }
        // 兼容其他可能的字段名
        if(fileds.contains("成交完成量占比")){
            met.add(getDealCompleteProportion());
        }
        
        // 首访相关字段
        if(fileds.contains("firstVisitNum")){
            met.add(getFirstVisitNum());
        }
        if(fileds.contains("firstVisitCompleteNum")){
            met.add(getFirstVisitCompleteNum());
        }
        if(fileds.contains("firstVisitCompleteRate")){
            met.add(getFirstVisitCompleteRate());
        }
        if(fileds.contains("firstVisitCompleteProportion")){
            met.add(getFirstVisitCompleteProportion());
        }
        // 兼容其他可能的字段名
        if(fileds.contains("首访完成量占比")){
            met.add(getFirstVisitCompleteProportion());
        }
        
        // 复访相关字段
        if(fileds.contains("repeatVisitNum")){
            met.add(getRepeatVisitNum());
        }
        if(fileds.contains("repeatVisitCompleteNum")){
            met.add(getRepeatVisitCompleteNum());
        }
        if(fileds.contains("repeatVisitCompleteRate")){
            met.add(getRepeatVisitCompleteRate());
        }
        if(fileds.contains("repeatVisitCompleteProportion")){
            met.add(getRepeatVisitCompleteProportion());
        }
        // 兼容其他可能的字段名
        if(fileds.contains("复访完成量占比")){
            met.add(getRepeatVisitCompleteProportion());
        }
        
        // 标记相关字段
        if(fileds.contains("tagNum")){
            met.add(getTagNum());
        }
        if(fileds.contains("tagCompleteNum")){
            met.add(getTagCompleteNum());
        }
        if(fileds.contains("tagCompleteRate")){
            met.add(getTagCompleteRate());
        }
        if(fileds.contains("tagCompleteProportion")){
            met.add(getTagCompleteProportion());
        }
        
        // 任务状态
        if(fileds.contains("taskStatusDesc")){
            met.add(getTaskStatusDesc());
        }

        Object[] objects = met.toArray();
        return objects;
    }
    public String[] courtCaseTitle1 = new String[] {
            "序号", "项目名称", "任务名称", "任务创建人", "任务创建时间", "任务起始时间", "任务结束时间", "所分配团队",
            "报备指标量", "报备完成量", "拜访指标量", "拜访完成量", "到访指标量", "到访完成量", "任务状态"
    };

}
