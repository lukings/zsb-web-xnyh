package cn.visolink.system.projectmanager.model.requestmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/22 13:31
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoomTaskDto extends BaseModel implements Serializable {

    /**
     * 申请名称
     */
    private String applyName;
    
    /**
     *   开始时间
     */
    private String startTime;
    
    /**
     *   结束时间
     */
    private String endTime;
}

