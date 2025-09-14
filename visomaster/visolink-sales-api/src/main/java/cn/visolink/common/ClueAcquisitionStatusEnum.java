package cn.visolink.common;

public enum ClueAcquisitionStatusEnum {

//  report("1","已报备", "招商地图-已报备客户"),
//  visit("4","已拜访，未完成三个一", "招商地图-已拜访未完成三个一客户"),
//  three_one("3","已完成三个一", "招商地图-已完成三个一客户"),
//  arrive("2","已来访", "招商地图-已来访客户"),
//  deal("8","已成交", "招商地图-已成交客户"),
//  clue("6","线索客户", "招商地图-线索客户客户"),
//  visit_err("7","位置异常", "招商地图-位置异常客户"),
//  public_clue("5","公共池", "招商地图-公共池客户");


//  zsdt_p_1 ("1","已成交", "已成交客户"),
//  zsdt_p_2 ("2","已来访", "已来访客户"),
//  zsdt_p_3 ("3","已完成三个一，未来访", "已完成三个一,未来访客户"),
//  zsdt_p_4 ("4","已拜访，未完成三个一", "已拜访,未完成三个一客户"),
//  zsdt_p_5 ("5","已报备，未拜访", "已报备,未拜访客户"),
//  zsdt_p_6 ("6","未拜访，未报备", "未拜访,未报备客户"),
//  zsdt_p_7 ("7","已拜访，未报备", "已拜访,未报备客户"),
//  zsdt_p_8 ("8","已拜访，被他人报备", "已拜访,但被他人报备客户"),
//  zsdt_p_9("9","已拜访，不存在", "已拜访，不存在客户");
//  zsdt_p_10 ("10","公客池", "公客池客户"),

  tkdt_p_1 ("1","已成交（在本项目）", "已成交（在本项目）客户"),//deal_local
  tkdt_p_2 ("2","已成交（在其他项目）", "已成交（在其他项目）客户"),//deal_other
  tkdt_p_3 ("3","见到老板（未完成三个一）", "见到老板（未完成三个一）客户"),//see_boss_not_done
  tkdt_p_4 ("4","见到老板（完成三个一）", "见到老板（完成三个一）客户"),//see_boss_done
  tkdt_p_5 ("5","未见到老板", "未见到老板客户"),//not_see_boss
  tkdt_p_6 ("6","仅来访本项目一次", "仅来访本项目一次客户"),//visit_once
  tkdt_p_7 ("7","来访本项目两次及以上", "来访本项目两次及以上客户"),//visit_multiple
  tkdt_p_8 ("8","目标企业", "目标企业客户"),//batch_import
  tkdt_p_9("9","企业不存在", "企业不存在客户"),//enterprise_not_exist
  tkdt_p_10 ("10","公客池", "拓客地图公客池客户");

  private String status;

  private String name;

  private String sheetName;

  ClueAcquisitionStatusEnum(String status, String name, String sheetName){
    this.status = status;
    this.name = name;
    this.sheetName = sheetName;
  }

  public static String getNameByStatus(String status) {
    for (ClueAcquisitionStatusEnum item : ClueAcquisitionStatusEnum.values()) {
      if (item.status.equals(status)) {
        return item.name;
      }
    }
    return null;
  }

  public static String getSheetNameByStatus(String status) {
    for (ClueAcquisitionStatusEnum item : ClueAcquisitionStatusEnum.values()) {
      if (item.status.equals(status)) {
        return item.sheetName;
      }
    }
    return null;
  }
}
