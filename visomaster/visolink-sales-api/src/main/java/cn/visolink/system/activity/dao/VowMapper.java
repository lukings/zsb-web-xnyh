package cn.visolink.system.activity.dao;

import cn.visolink.system.activity.model.vo.ActivityVowDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: 杨航行
 * @Description:
 * @Date: create in 2020/10/21 17:50
 */
@Mapper
public interface VowMapper {

    /**
    * 查询许愿明细列表
    * */
    List<Map> getVowDetail(Map map);

    /**
     * 导出许愿明细列表
    * */
    List<ActivityVowDetailVo> getVowDetailExport(Map map);
    /**
     * 修改弹幕状态
     * */
   void updateBarrageStatus (Map map);

   /**
   * 获取开奖状态
   * */
   int checkIsOpenAward(String id);

   /**
    * 查询奖品信息
    * */
   List<Map> getAwardInfo(String activity_id);

   /**
   * 更新中奖状态
   * */
   int updateAwardStatus(Map map);

   /**
   * 操作记录
   * */
   int saveOperateRecord(Map map);


   /**
    * 获取优惠券图片
    * */
   Map getCouponImageUrl(String awardId);
   /**
   * 根据活动ID获取是否支持分区
   * */
   int getPartition(String activity_id);

   /*
   * 获取中奖ID
   * */
   List <Map> getAwardWinners(Map map);

  /*
  * 批量更新中奖
  * */
  int updateBrokerAwardStatus(@Param("award_id") String award_id,@Param("award_name") String award_name,@Param("list") List<Map> list);
}
