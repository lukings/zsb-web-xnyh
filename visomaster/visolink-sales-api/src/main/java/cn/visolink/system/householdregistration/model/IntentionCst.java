package cn.visolink.system.householdregistration.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName IntentionCst
 * @Author wanggang
 * @Description //TODO
 * @Date 2021/2/23 21:22
 **/
@Data
public class IntentionCst implements Serializable{

    private String id;

    private String activityId;

    private String activityName;

    private String projectid;

    private String projectname;

    private String projectidFq;

    private String projectnameFq;

    private String buildguid;

    private String buildname;

    private String opportunityClueId;

    private String projectClueId;

    private String intentionId;

    private String customerName;

    private String customerMobile;

    private String salesId;

    private String salesName;

    private String batchNo;

    private String batchNoName;

    private String cardGrouping;

    private String cardGroupingName;

    private String cardId;

    private String cardName;

    private String cardType;

    private String placeNo;

    private String intentionLevelEdit;

    private String intentionLevelEditDesc;

    private String roomguidEdit;

    private String roomnameEdit;

    private String intentionLevelOld;

    private String intentionLevelOldDesc;

    private String roomguidOld;

    private String roomnameOld;

    private String placeCount;

    private String needPlaceCount;

    private List<IntentionCstCard> cards;

    private List<Map> intentionLevels;
}
