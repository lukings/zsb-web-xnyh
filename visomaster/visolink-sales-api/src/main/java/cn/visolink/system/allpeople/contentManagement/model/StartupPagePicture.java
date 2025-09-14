package cn.visolink.system.allpeople.contentManagement.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("startup_page_picture")
@ApiModel(value = "app pad端启动页图片对象", description = "app pad端启动页图片表")
public class StartupPagePicture {


    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "id")
    @TableField("id")
    private String id;

    /**
     * 图片路径
     */
    @ApiModelProperty(value = "图片路径")
    private String imgUrl;

    /**
     * 是否启用 0不启用 1 启用
     */
    @ApiModelProperty(value = "是否启用 0不启用 1 启用")
    private Integer status;

    /**
     * 用于app或pad端 1 app 2 pad
     */
    @ApiModelProperty(value = "用于app或pad端 1 app 2 pad")
    private Integer port;

    /**
     * 是否删除 0不删除 1 删除
     */
    @ApiModelProperty(value = "是否删除 0不删除 1 删除")
    private Integer isDel;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 修改人
     */
    @ApiModelProperty(value = "修改人")
    private String editor;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private String editTime;

    /**
     * 图片名称
     */
    @ApiModelProperty(value = "图片名称")
    private String imgName;

    /**
     * 当前登陆人id
     */
    @ApiModelProperty(value = "当前登陆人id")
    private String userName;

    public StartupPagePicture() {}
}
