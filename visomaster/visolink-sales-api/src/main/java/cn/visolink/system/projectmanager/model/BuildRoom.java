package cn.visolink.system.projectmanager.model;

import cn.visolink.system.projectmanager.model.requestmodel.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 孙林
 * @date:2019-9-10
 * */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BuildRoom extends BaseModel implements Serializable {

    @ApiModelProperty(name = "roomId", value = "房间ID")
    private String roomId;

    @ApiModelProperty(name = "roomInfo", value = "房间全称")
    private String roomInfo;

    @ApiModelProperty(name = "roomName", value = "房间简称")
    private String roomName;

    @ApiModelProperty(name = "roomCode", value = "房间编码")
    private String roomCode;

    @ApiModelProperty(name = "roomNo", value = "房号")
    private String roomNo;

    @ApiModelProperty(name = "no", value = "房间序号")
    private Integer no;

    @ApiModelProperty(name = "statusEnum", value = "房间状态")
    private Integer statusEnum;

    @ApiModelProperty(name = "floorName", value = "楼层名称")
    private String floorName;

    @ApiModelProperty(name = "floorNo", value = "楼层编号")
    private Integer floorNo;

    @ApiModelProperty(name = "unitId", value = "单元ID")
    private String unitId;

    @ApiModelProperty(name = "unitName", value = "单元名称")
    private String unitName;

    @ApiModelProperty(name = "unitNo", value = "单元编号")
    private Integer unitNo;

    @ApiModelProperty(name = "buildId", value = "楼栋ID(计划系统楼栋ID)")
    private String buildId;

    @ApiModelProperty(name = "kingdeeBuildId", value = "楼栋ID（金蝶）")
    private String kingdeeBuildId;

    @ApiModelProperty(name = "myBuildId", value = "楼栋ID(明源)")
    private String myBuildId;

    @ApiModelProperty(name = "stageId", value = "项目分期ID")
    private String stageId;

    @ApiModelProperty(name = "stageCode", value = "分期编号")
    private String stageCode;

    @ApiModelProperty(name = "groupId", value = "组团ID")
    private String groupId;

    @ApiModelProperty(name = "groupName", value = "组团名称")
    private String groupName;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private String createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private String updateTime;

    @ApiModelProperty(name = "createUser", value = "创建人账号")
    private String createUser;

    @ApiModelProperty(name = "updateUser", value = "修改人账号")
    private String updateUser;

    @ApiModelProperty(name = "isDelete", value = "是否删除 0：正常；1：删除")
    private Integer isDelete;

    @ApiModelProperty(name = "projectId", value = "项目id")
    private String projectId;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    private String projectName;

    @ApiModelProperty(name = "stageName", value = "分期名称")
    private String stageName;

    @ApiModelProperty(name = "openingLock", value = "开盘锁")
    private Integer openingLock;

    @ApiModelProperty(name = "bottomPriceReturnHouseLock", value = "底价退房锁")
    private Integer bottomPriceReturnHouseLock;

    @ApiModelProperty(name = "djBldPrice", value = "底价建筑单价")
    private BigDecimal djBldPrice;

    @ApiModelProperty(name = "djTnPrice", value = "底价套内单价")
    private BigDecimal djTnPrice;

    @ApiModelProperty(name = "bldPrice", value = "建筑单价")
    private BigDecimal bldPrice;

    @ApiModelProperty(name = "djTotal", value = "底价总价")
    private BigDecimal djTotal;

    @ApiModelProperty(name = "bldArea", value = "建筑面积")
    private BigDecimal bldArea;

    @ApiModelProperty(name = "isAreaModify", value = "定价后面积发生变更")
    private Integer isAreaModify;

    @ApiModelProperty(name = "tradeLocker", value = "锁定人")
    private String tradeLocker;

    @ApiModelProperty(name = "isTradeLock", value = "是否交易锁")
    private Integer isTradeLock;

    @ApiModelProperty(name = "isTfLock", value = "标准价退房锁")
    private Integer isTfLock;

    @ApiModelProperty(name = "tradeLockTime", value = "锁定时间")
    private String tradeLockTime;

    @ApiModelProperty(name = "tfDate", value = "退房日期")
    private String tfDate;

    @ApiModelProperty(name = "hxId", value = "户型Id")
    private String hxId;

    @ApiModelProperty(name = "hxName", value = "户型名称")
    private String hxName;

    @ApiModelProperty(name = "tnPrice", value = "套内单价")
    private BigDecimal tnPrice;

    @ApiModelProperty(name = "tnArea", value = "套内面积")
    private BigDecimal tnArea;

    @ApiModelProperty(name = "total", value = "总价")
    private BigDecimal total;

    @ApiModelProperty(name = "dspBldArea", value = "待审批建筑面积")
    private BigDecimal dspBldArea;

    @ApiModelProperty(name = "dspTnArea", value = "待审批套内面积")
    private BigDecimal dspTnArea;

    @ApiModelProperty(name = "ysBldArea", value = "预售建筑面积")
    private BigDecimal ysBldArea;

    @ApiModelProperty(name = "ysTnArea", value = "预售套内面积")
    private BigDecimal ysTnArea;

    @ApiModelProperty(name = "scBldArea", value = "实测建筑面积")
    private BigDecimal scBldArea;

    @ApiModelProperty(name = "scTnArea", value = "实测套内面积")
    private BigDecimal scTnArea;

    @ApiModelProperty(name = "roomStru", value = "房间结构")
    private String roomStru;

    @ApiModelProperty(name = "dspAreaStatusEnum", value = "待审批面积状态")
    private Integer dspAreaStatusEnum;

    @ApiModelProperty(name = "calModeEnum", value = "计价方式code")
    private Integer calModeEnum;

    @ApiModelProperty(name = "calMode", value = "计价方式")
    private String calMode;

    @ApiModelProperty(name = "isHfLock", value = "是否换房锁")
    private Integer isHfLock;

    @ApiModelProperty(name = "xIsZc", value = "是否自持")
    private Integer xIsZc;

    @ApiModelProperty(name = "areaStatusEnum", value = "面积状态code")
    private Integer areaStatusEnum;

    @ApiModelProperty(name = "areaStatus", value = "面积状态")
    private String areaStatus;

    @ApiModelProperty(name = "status", value = "房间状态")
    private String status;

    @ApiModelProperty(name = "dspAreaStatus", value = "待审批面积状态")
    private String dspAreaStatus;

    @ApiModelProperty(name = "isAnnexe", value = "是否附属房产")
    private Integer isAnnexe;

    @ApiModelProperty(name = "mainRoomId", value = "主房间GUID")
    private String mainRoomId;

    @ApiModelProperty(name = "isVirtualRoom", value = "是否虚拟房间")
    private Integer isVirtualRoom;

    @ApiModelProperty(name = "areaChgId", value = "面积变更ID")
    private String areaChgId;

    @ApiModelProperty(name = "productTypeId", value = "产品类型Id")
    private String productTypeId;

    @ApiModelProperty(name = "chooseRoomLockTime", value = "选房锁定时间")
    private String chooseRoomLockTime;

    @ApiModelProperty(name = "chooseRoomLockEndTime", value = "选房锁定结束时间")
    private String chooseRoomLockEndTime;

    @ApiModelProperty(name = "chooseRoomId", value = "选房唯一标识")
    private String chooseRoomId;

    @ApiModelProperty(name = "chooseRoomLockId", value = "选房锁唯一标识")
    private String chooseRoomLockId;

    @ApiModelProperty(name = "floors", value = "选房时间")
    private String chooseRoomTime;

    @ApiModelProperty(name = "lsxkTime", value = "临时销控时间")
    private String lsxkTime;

    @ApiModelProperty(name = "jbrId", value = "经办人GUID")
    private String jbrId;

    @ApiModelProperty(name = "baTotal", value = "备案总价")
    private BigDecimal baTotal;

    @ApiModelProperty(name = "baBldPrice", value = "备案建筑单价")
    private BigDecimal baBldPrice;

    @ApiModelProperty(name = "baTnPrice", value = "备案套内单价")
    private BigDecimal baTnPrice;

    @ApiModelProperty(name = "jfDate", value = "交房日期")
    private String jfDate;

    @ApiModelProperty(name = "openingBatch", value = "开盘批次")
    private String openingBatch;

    @ApiModelProperty(name = "productBuildName", value = "楼栋名称")
    private String productBuildName;

    @ApiModelProperty(name = "bldType", value = "类型")
    private String bldType;

    /**
     *   面价建筑单价
     */
    private BigDecimal mjBldPrice;

    /**
     *   面价套内单价
     */
    private BigDecimal mjTnPrice;

    /**
     *   面价总价
     */
    private BigDecimal mjTotal;

    /**
     *   任务id
     */
    private String taskId;

    /**
     *   type: {0: 录入预售 1: 录入实测}
     */
    private Integer type;

    /**
     *   t_mm_build_room_plus 表的id
     */
    private String id;

    /**
     *   计价方式 1:建筑面积 2:套内面积 3:套
     */
    private Integer valuationType;

    /**
     * 价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准
     */
    private Integer priceStandard;

    /**
     *   低价建筑单价
     */
    private BigDecimal oldDjBldPrice;

    /**
     *   低价套内单价
     */
    private BigDecimal oldDjTnPrice;

    /**
     *   低价总价
     */
    private BigDecimal oldDjTotal;

    /**
     * 向右合并数量
     */
    private Integer rightCell;

    /**
     * 向下合并数量
     */
    private Integer downCell;

    /**
     * 删除标识 废弃
     */
    private String delFlag;

    /**
     * 产品构成名称
     */
    private String productName;

}