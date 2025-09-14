package cn.visolink.common;

public enum TaskTypeEnum {

  report("1","月度考核任务"),
  visit("2","地图拓客任务");

  private String type;

  private String name;

  TaskTypeEnum (String type, String name){
    this.type = type;
    this.name = name;
  }
  public static String getNameByType(String type) {
    for (TaskTypeEnum item : TaskTypeEnum.values()) {
      if (item.type.equals(type)) {
        return item.name;
      }
    }
    return null;
  }

}
