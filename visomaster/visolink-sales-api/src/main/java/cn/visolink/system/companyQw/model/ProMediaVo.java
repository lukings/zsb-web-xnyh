package cn.visolink.system.companyQw.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ProMediaVo
 * @Author wanggang
 * @Description //项目素材
 * @Date 2021/12/31 13:41
 **/
@Data
@ApiModel(value = "项目素材", description = "项目素材")
public class ProMediaVo implements Serializable {

    @ApiModelProperty(value = "项目ID")
    private String projectId;

    @ApiModelProperty(value = "新增或修改（1：新增 2：修改）")
    private String addOrEdit;

    @ApiModelProperty(value = "图片集合")
    private List<MediaDetail> photoList;

    @ApiModelProperty(value = "视频集合")
    private List<MediaDetail> videoList;

    @ApiModelProperty(value = "h5链接集合")
    private List<MediaDetail> h5List;

    @ApiModelProperty(value = "小程序集合")
    private List<MediaDetail> appletList;

    @ApiModelProperty(value = "文件集合")
    private List<MediaDetail> fileList;

}
