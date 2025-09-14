package cn.visolink.system.allpeople.contentManagement.model;

import lombok.Data;

/**
 * 新闻
 * @Auther: wang gang
 * @Date: 2020/1/31 11:55
 * @Description: Pointing to the breeze, the procedure is self-contained
 */
@Data
public class News {

    private int num;

    private String ID;

    private String BelongArea;

    private String ProjectID;

    private String ProjectName;

    private String CityId;

    private String CityName;

    private String Title;

    private String Content;

    private String Author;

    private String Type;

    private String Summary;

    private String HeadImgUrl;

    private String HeadImgName;

    private String JumpUrl;

    private String IsPublish;

    private String IsGuessLike;

    private String IsTop;

    private int ListIndex;

    private int ClickNum;

    private String Creator;

    private String CreateTime;

    private String Editor;

    private String EditTime;

    private String Status;

    private String IsDel;

    private String PublishTime;

    private String addOrEdit;//1:新增 2：修改

//    private MultipartFile file;//图片文件

    private String NewsType;//新闻类型

    //private String ProjectShowName;//项目展示名称

    private String BuildID;//楼盘主键ID

    private String BuildBookName;//关联项目

    private String ReadCount; //阅读次数

    private int WaterNum; // 虚拟浏览次数
    /**
     *导出excel标题头
     */
    public String[]  toNewsTitle =  new String[]{
            "序号","新闻标题","创建人","新闻类型","城市","关联项目",
            "创建时间","发布时间","实际阅读次数","是否发布"
            };
    /**
     * 获取反馈的数据
     * @param
     * @return
     */
    public Object[] toNewsData() {
        return new Object[]{
                getNum(), getTitle(), getAuthor(), getNewsType(), getCityName(), getBuildBookName(), getCreateTime(),
                getPublishTime(), getReadCount(), getIsPublish()
        };
    }
}
