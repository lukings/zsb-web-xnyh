package cn.visolink.common;

/**
 * 1是未开始，2是进行中，3是已结束 0是未分配
 */
public enum TaskStatusEnum {

  un_org("0","未分配"),
  no_start("1","未开始"),
  progress("2","进行中"),
  ended("3","已结束"),
  stoped("4","已终止");
  private String status;

  private String name;

  TaskStatusEnum(String status, String name){
    this.status = status;
    this.name = name;
  }
  public static String getNameByType(String type) {
    for (TaskStatusEnum item : TaskStatusEnum.values()) {
      if (item.status.equals(type)) {
        return item.name;
      }
    }
    return null;
  }

}
