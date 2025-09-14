package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

import java.util.List;

/**
 * @ClassName BuildBookPhoto
 * @Author wanggang
 * @Description //TODO
 * @Date 2020/12/23 15:27
 **/
@Data
public class BuildBookPhoto {

    private String code;

    private String name;

    private String isClear;

    private List<BuildingPhoto> buildingPhoto;
}
