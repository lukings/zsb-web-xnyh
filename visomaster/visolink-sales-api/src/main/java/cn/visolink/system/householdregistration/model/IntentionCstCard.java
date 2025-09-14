package cn.visolink.system.householdregistration.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName IntentionCstCard
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/2/24 16:12
 **/
@Data
public class IntentionCstCard implements Serializable{

    private String cardName;

    private String cardId;

    private List<IntentionCst> intentionCsts;
}
