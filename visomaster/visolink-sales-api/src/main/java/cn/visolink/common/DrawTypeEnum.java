package cn.visolink.common;


public enum DrawTypeEnum {

  spot(1,"点"),
  line(2,"线"),
  face(3,"面"),
  xzqy(4,"行政区域");

  private Integer code;

  private String name;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  DrawTypeEnum(Integer code, String name){
    this.code = code;
    this.name = name;
  }

  public static String getNameByCode(Integer code) {
    for (DrawTypeEnum drawTypeEnum : DrawTypeEnum.values()) {
      if (drawTypeEnum.getCode().equals(code)) {
        return drawTypeEnum.getName();
      }
    }
    return null;
  }
}
