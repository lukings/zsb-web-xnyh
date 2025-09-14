package cn.visolink.common;

public enum ClueStatusEnum {

//  report("1","已报备", "招商地图-已报备客户"),
//  visit("4","已拜访，未完成三个一", "招商地图-已拜访未完成三个一客户"),
//  three_one("3","已完成三个一", "招商地图-已完成三个一客户"),
//  arrive("2","已来访", "招商地图-已来访客户"),
//  deal("8","已成交", "招商地图-已成交客户"),
//  clue("6","线索客户", "招商地图-线索客户客户"),
//  visit_err("7","位置异常", "招商地图-位置异常客户"),
//  public_clue("5","公共池", "招商地图-公共池客户");


  zsdt_p_1 ("1","已成交", "已成交客户"),
  zsdt_p_2 ("2","已来访", "已来访客户"),
  zsdt_p_3 ("3","已完成三个一，未来访", "已完成三个一,未来访客户"),
  zsdt_p_4 ("4","已拜访，未完成三个一", "已拜访,未完成三个一客户"),
  zsdt_p_5 ("5","已报备，未拜访", "已报备,未拜访客户"),
  zsdt_p_6 ("6","未拜访，未报备", "未拜访,未报备客户"),
  zsdt_p_7 ("7","已拜访，未报备", "已拜访,未报备客户"),
  zsdt_p_8 ("8","已拜访，被他人报备", "已拜访,但被他人报备客户"),
  zsdt_p_9("9","已拜访，不存在", "已拜访，不存在客户"),
  zsdt_p_10 ("10","公客池", "招商地图公客池客户");

//  khdt_p_1 ("1","已成交", "已成交客户"),
//  khdt_p_2 ("2","已来访", "已来访客户"),
//  khdt_p_3 ("3","已完成三个一", "已完成三个一未来访客户"),
//  khdt_p_4 ("4","已拜访，未完成三个一", "已拜访未完成三个一客户"),
//  khtd_p_5 ("5","已报备", "已报备未拜访客户"),
//  khdt_p_6 ("6","未拜访，未报备", "未拜访未报备客户"),
//  khdt_p_7 ("7","已拜访，未报备", "已拜访未报备客户"),
//  khdt_p_8 ("8","已拜访，被他人报备", "已拜访被他人报备客户"),
//  khdt_p_9("9","已拜访，不存在", "招商地图已拜访不存在客户"),
//  khdt_p_10 ("10","公客池", "招商地图公客池客户");

  private String status;

  private String name;

  private String sheetName;

  ClueStatusEnum (String status, String name, String sheetName){
    this.status = status;
    this.name = name;
    this.sheetName = sheetName;
  }

  public static String getNameByStatus(String status) {
    for (ClueStatusEnum item : ClueStatusEnum.values()) {
      if (item.status.equals(status)) {
        return item.name;
      }
    }
    return null;
  }

  public static String getSheetNameByStatus(String status) {
    for (ClueStatusEnum item : ClueStatusEnum.values()) {
      if (item.status.equals(status)) {
        return item.sheetName;
      }
    }
    return null;
  }
}
