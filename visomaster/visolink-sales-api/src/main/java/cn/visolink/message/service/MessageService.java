package cn.visolink.message.service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Message服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-09-03
 */
public interface MessageService{

        /**
         * 定时解决报备相关数据
         *
         * @return
         */
        void selectMessage();
        void deleteHD();

        /**
         * 定时解决其他数据
         */
//        void timerOther();

        void startUpRuleExpired();

        void startUpRuleEnable();

        /**
         * @author liang
         * @date 2025/8/27 14:52
         * @description: 获取外呼超时未消费项目预警
         */
        void getTimeoutWarning();
        /**
         * @Author wanggang
         * @Description //跟进逾期任务
         * @Date 11:06 2022/4/21
         * @Param []
         * @return void
         **/
        void addProPool();
        /**
         * @Author wanggang
         * @Description //掉入区域池
         * @Date 11:06 2022/4/21
         * @Param []
         * @return void
         **/
        String addAreaPool();
        /**
         * @Author wanggang
         * @Description //掉入全国池
         * @Date 11:06 2022/4/21
         * @Param []
         * @return void
         **/
        String addNationalPool();
        /**
         * @Author wanggang
         * @Description //每月强制丢失客户
         * @Date 11:06 2022/4/21
         * @Param []
         * @return void
         **/
        void monthlyDelCst();
        /**
         * @Author wanggang
         * @Description //转介超时自动驳回
         * @Date 18:14 2022/4/23
         * @Param []
         * @return void
         **/
        void automaticRejection();

        /**
         * @Author luqianqian
         * @Description //设置公客池客户逾期标签
         * @Date 18:14 2023/9/12
         * @Param []
         * @return void
         **/
        void saveCustomerPoolDateLabel();

        /**
         * @Author luqianqian
         * @Description //公客池获取客户待办逾期
         * @Date 18:14 2023/9/12
         * @Param []
         * @return void
         **/
        void automaticObtainApprove();

        /**
         * @Author luqianqian
         * @Description //客户跟进超时自动驳回
         * @Date 18:14 2023/9/12
         * @Param []
         * @return void
         **/
        void automaticFollowUpRejection();

        /**
         * @Author luqianqian
         * @Description //相似客户审批超时自动驳回
         * @Date 18:14 2023/9/12
         * @Param []
         * @return void
         **/
        void automaticSimilarCustomerReportRejection();

        /**
         * @Author luqianqian
         * @Description //报备客户客户统计
         * @Date 18:14 2024/3/8
         * @Param []
         * @return void
         **/
        void saveCustomerOppStatistics();

        /**
         * @Author luqianqian
         * @Description //公客池客户统计
         * @Date 18:14 2024/3/8
         * @Param []
         * @return void
         **/
        void saveCustomerPoolStatistics();

        /**
         * @Author luqianqian
         * @Description //同步报备链路
         * @Date 18:14 2024/4/24
         * @Param []
         * @return void
         **/
        void synCustomerReportRecord();
        /**
         * @Author luqianqian
         * @Description //导出任务执行
         * @Date 18:14 2024/5/6
         * @Param []
         * @return void
         **/
        String createExcelDownLoad();
        /**
         * @Author luqianqian
         * @Description //删除导出附件
         * @Date 18:14 2024/5/6
         * @Param []
         * @return void
         **/
        void delExcelFile();

        /**
         * @Author luqianqian
         * @Description //初始化数据1130版本
         * @Date 18:14 2024/5/6
         * @Param []
         * @return void
         **/
        String initHistoryDate1130();

//
//        /**
//         * @Author luqianqian
//         * @Description //初始化项目转化率数据
//         * @Date 14:41 2025/1/5
//         * @Param []
//         * @return void
//         **/
//        String initJtproConversionRateDate();
}
