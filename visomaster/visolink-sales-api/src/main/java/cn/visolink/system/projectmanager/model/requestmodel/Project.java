package cn.visolink.system.projectmanager.model.requestmodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Mr.Yu
 * @Date: 2021/11/11 15:24
 * @description
 * @Version 1.0
 */
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class Project implements Serializable {
    /**
     *   项目对象
     */
    private ProjectModel projectModel;

    /**
     *   分期对象
     */
    private List<StageModel> stageModelList;

}

