package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * 推广活动
 * @Auther: wang gang
 * @Date: 2020/2/5 13:27
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class Extension {

    private String Num;//序号
    private String ID;//推广活动ID
    private String ProjectID;//项目ID
    private String ExtenOrgId;//区域id
    private String OrgName;//区域名称
    private String BuildBookID;//楼盘ID
    private String NewsId;//新闻ID
    private String ActivityId;//活动ID
    private String CityId;//城市ID
    private String WareHouseId;//户型ID
    private String HouseBuildBookId;//户型所属楼盘ID
    private String ProjectName;//项目名称
    private int RegisterCnt;//注册人数
    private int LookNum;//浏览次数
    private String ExtenActivityName;//推广活动名称
    private String IconName;//推广小程序码名称
    private String IconUrl;//推广小程序码图片路径
    private String ToUrl;//扫码跳转路径
    private String ToUrlDesc;//跳转路径说明
    private String JumpToName;//落地页名称
    private String ExtenTypeDesc;//推广码级别
    private String ExtenType;//推广码级别CODE
    private String PhotoWidth;//图片宽度
    private String PhotoName;//图片名称
    private String Creator;//创建人
    private String Name;//创建人名称
    private String CreateTime;//创建时间
    private String IsDel;//是否删除 0：否 1：是
    private String addOrEdit;//是否修改  1：添加 2：修改
    private String mainMediaGuId;
    private String mainMediaName;
    private String subMediaGuId;
    private String subMediaName;
    private String type;
    private String days;
    private String IsNewAdd = "0";

    private String getMainMedia(){
        if(getMainMediaName()==null || "".equals(getMainMediaName())
        || getSubMediaName()==null || "".equals(getSubMediaName())){
            return "";
        }else{
            return getMainMediaName()+"/"+getSubMediaName();
        }
    }
    /**
     * 获取数据
     * @param
     * @return
     */
    public Object[] toPublicData(){
        return new Object[]{
                getNum(),getOrgName(),getProjectName(),getToUrlDesc(),getJumpToName(),getExtenActivityName(),
                getExtenTypeDesc(),getLookNum(),getRegisterCnt(),getMainMedia(),getCreator(),
                getCreateTime()
        };
    }
    public String[]  courtCaseTitle =  new String[]{
            "序号","区域","项目","扫码跳转",
            "落地页名称","码名称","码属性",
            "浏览次数","注册人数","认知途径","创建人",
            "创建时间"};
}
