package cn.visolink.system.householdregistration.model.form;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CardOppID
 * @Author wanggang
 * @Description //查询机会数据辅助类
 * @Date 2020/8/11 11:14
 **/
@Data
public class CardOppID {

    private String projectId;//项目ID

    private List<String> intentionIDs;//明源机会ID集合

}
