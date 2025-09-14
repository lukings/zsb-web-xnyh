package cn.visolink.system.card.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * c_电子名片信息
 * </p>
 *
 * @author autoJob
 * @since 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("c_user_card")
@ApiModel(value = "UserCard对象", description = "c_电子名片信息")
public class UserCard implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "用户ID")
    private String accountId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @ApiModelProperty(value = "签名")
    private String signature;

    @ApiModelProperty(value = "标签")
    private String tag;

    @ApiModelProperty(value = "头像")
    private String imgUrl;

    @ApiModelProperty(value = "性别1、男2、女")
    private Integer gender;

    @ApiModelProperty(value = "浏览次数")
    private int viewNum;

    @ApiModelProperty(value = "浏览人数")
    private int viewUserNum;

    @ApiModelProperty(value = "关注数")
    private int attentionUserNum;

    @ApiModelProperty(value = "点赞数")
    private int likeUserNum;

    @ApiModelProperty(value = "分享图片数")
    private int sharePhotoNum;

    @ApiModelProperty(value = "分享好友数")
    private int shareFriendNum;

    @ApiModelProperty(value = "电话咨询次数")
    private int callNum;

    @ApiModelProperty(value = "电话咨询人数")
    private int callUserNum;

    @ApiModelProperty(value = "在线咨询次数")
    private int imNum;

    @ApiModelProperty(value = "在线咨询人数")
    private int imUserNum;

    @ApiModelProperty(value = "名片类型")
    private String cardType;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "编辑人")
    private String editor;

    @ApiModelProperty(value = "编辑时间")
    private Date editTime;

    @ApiModelProperty(value = "状态")
    @TableLogic
    private Integer status;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel;


}
