package cn.visolink.system.projectmanager.model.requestmodel;

import cn.visolink.system.projectmanager.model.BuildRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/12/1 13:45
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDishRequest implements Serializable {

    private List<BuildRoom> buildRoomList;

}

