package cn.visolink.system.custMap.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GridDistributionResBO {

    @ApiModelProperty("省份总数")
    private Integer provinceCount;

    @ApiModelProperty("地级市总数")
    private Integer cityCount;

    @ApiModelProperty("总网格数量")
    private Integer totalGridCount;

    @ApiModelProperty("省份分布列表")
    private List<ProvinceInfo> provinces;

    @Data
    public static class ProvinceInfo {
        @ApiModelProperty("省份名称")
        private String province;

        @ApiModelProperty("地级市列表")
        private List<CityInfo> cities;

        @ApiModelProperty("该省份绘制区域总数")
        private Integer totalCount;
    }

    @Data
    public static class CityInfo {
        @ApiModelProperty("地级市名称")
        private String cityName;

        @ApiModelProperty("该市绘制区域数量")
        private Integer count;
    }
}
