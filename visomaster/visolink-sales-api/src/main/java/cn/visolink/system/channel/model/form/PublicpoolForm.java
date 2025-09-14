package cn.visolink.system.channel.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * PublicpoolForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-09-02
 */
@Data
@ApiModel(value = "Publicpool对象", description = "")
public class PublicpoolForm {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private String id;

    private String isAll;//1：隐号 2：全号

    private String pageNum;

    private String pageSize;

    private String addType;//1：跟进逾期 2：主动放弃 3：强制丢失 4:   导入数据无归属

    private String poolType;//公共池类型（1：项目 2：区域 3：全国 4: 招商 5：总招商）

    private String search;//客户姓名或手机号

    private String searchTime;//查询时间类型（1：加入公共池时间 2：报备时间 3：首访时间）

    private String beginTime;//开始时间

    private String endTime;//结束时间

    private String clueId;

    private String name;

    private Boolean gender;

    private String level;

    private String operationTime;

    private String theFirstVisitDate;

    private String clueSource;

    private String reportUserName;

    private String reportTime;

    private String saleId;

    private String saleName;

    private String expireTag;

    private String catchTime;

    private String catchWay;

    private String clueStatus;

    private String mobile;

    private String projectId;

    private String sourceType;

    @ApiModelProperty(value = "1 报备过期2 跟进过期3 到访过期4 丢弃5 顾问离职6 报备人离职")
    private String reason;

    private String customerMobile;

    private String customerName;

    private String redistributionId;

    private List<String> projectList;

    private List<String> areaList;

    private List<String> redistributionIdList;

    private List<String> clueStatusList;

    private List<String> addTypeList;//进入公池原因

    private List<Map> fileds;

    private List<List<String>> customerIndustryArr;
    private List<String> belongIndustriseList;
    private List<String> belongIndustriseTwoList;
    private List<String> belongIndustriseThreeList;
    private List<String> belongIndustriseFourList;

    private List<String> source;

    private String customerAddress;

    @ApiModelProperty(value = "客户逾期标签")
    private String customerDateLabel;

    @ApiModelProperty(value = "到访数")
    private String visitCount;
    private String visitStartCount;
    private String visitEndCount;

    @ApiModelProperty(value = "三个一前拜访数")
    private String threeOnesBeforeCount;
    private String threeOnesBeforeStartCount;
    private String threeOnesBeforeEndCount;

    @ApiModelProperty(value = "三个一后拜访数")
    private String threeOnesAfterCount;
    private String threeOnesAfterStartCount;
    private String threeOnesAfterEndCount;

    @ApiModelProperty(value = "拜访数")
    private String comeVisitCount;
    private String comeVisitStartCount;
    private String comeVisitEndCount;

    private List<String> mainProductsList;


}
