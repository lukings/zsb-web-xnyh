package cn.visolink.system.commission.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.visolink.system.commission.mapper.CommissionMapper;
import cn.visolink.system.commission.service.CommissionService;
import cn.visolink.utils.PageUtil;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.XGConnectionManager;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * CommissionDetail服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2020-06-22
 */
@Service
public class CommissionServiceImpl implements CommissionService {

    @Autowired
    private CommissionMapper commissionMapper;

    /**
     * 获取佣金列表
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getCommissionList(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        // 查询销管视图获取数据
        List<Map<String, Object>> xgList = selectViewList(param, 1);
        if (xgList == null) {
            return result;
        }
        // 待审核（没有付款单审批通过）
        long dsh = xgList.stream().filter(
                v -> StringUtils.isBlank(MapUtils.getString(v, "paymentCode", ""))
                        || MapUtils.getIntValue(v, "paidAmount", 0) <= 0).count();
        // 现金奖励
        long xj = xgList.stream().filter(v -> StringUtils.isNotBlank(
                MapUtils.getString(v, "paymentCode", ""))
                && StringUtils.isNotBlank(MapUtils.getString(v, "commissionType", ""))
                && (StringUtils.isNotBlank(MapUtils.getString(v, "paidAmount", "")) && MapUtils.getIntValue(v, "paidRadio", 0) > 0)
                &&  ("第三方代付".equals(MapUtils.getString(v, "commissionType", ""))
                || "直接付款".equals(MapUtils.getString(v, "commissionType", "")))).count();
        // 非现金奖励
        long fxj = xgList.stream().filter(v -> StringUtils.isNotBlank(MapUtils.getString(v, "commissionType", ""))
                && (StringUtils.isNotBlank(MapUtils.getString(v, "paidAmount", "")) && MapUtils.getIntValue(v, "paidRadio", 0) > 0)
                && ("购物卡".equals(MapUtils.getString(v, "commissionType", ""))
                || "物业费".equals(MapUtils.getString(v, "commissionType", "")))).count();

        String type = MapUtils.getString(param, "type");
        List<Map<String, Object>> list = new ArrayList<>();
        if (StringUtils.isNotBlank(type)) {
            // 待审核（没有付款单审批通过）
            if (type.contains(",dsh")) {
                List<Map<String, Object>> collect = xgList.stream().filter(v -> StringUtils.isBlank(
                        MapUtils.getString(v, "paymentCode", ""))
                        || MapUtils.getIntValue(v, "paidAmount", 0) <= 0).collect(Collectors.toList());
                list.addAll(collect);
            }
            // 现金奖励（有一笔付款审批单通过后）
            if (type.contains(",xjs")) {
                List<Map<String, Object>> collect = xgList.stream().filter(v -> StringUtils.isNotBlank(
                        MapUtils.getString(v, "paymentCode", ""))
                        && StringUtils.isNotBlank(MapUtils.getString(v, "commissionType", ""))
                        && (StringUtils.isNotBlank(MapUtils.getString(v, "paidAmount", "")) && MapUtils.getIntValue(v, "paidRadio", 0) > 0)
                        &&  ("第三方代付".equals(MapUtils.getString(v, "commissionType", ""))
                        || "直接付款".equals(MapUtils.getString(v, "commissionType", "")))).collect(Collectors.toList());
                list.addAll(collect);
            }
            // 非现金奖励（不结佣数据）
            if (type.contains(",fxj")) {
                List<Map<String, Object>> collect = xgList.stream().filter(
                        v -> StringUtils.isNotBlank(MapUtils.getString(v, "commissionType", ""))
                                && (StringUtils.isNotBlank(MapUtils.getString(v, "paidAmount", "")) && MapUtils.getIntValue(v, "paidRadio", 0) > 0)
                                && ("购物卡".equals(MapUtils.getString(v, "commissionType", ""))
                                || "物业费".equals(MapUtils.getString(v, "commissionType", "")))).collect(Collectors.toList());
                list.addAll(collect);
            }
        } else {
            list.addAll(xgList);
        }

        list.replaceAll(v -> {
            if ("购物卡".equals(MapUtils.getString(v, "commissionType", ""))
                    || "物业费".equals(MapUtils.getString(v, "commissionType", ""))) {
                v.put("type", "非现金奖励");
            } else {
                v.put("type", "现金奖励");
            }
            return v;
        });
        // 审核通过（有一笔付款审批单通过后）
        List<Map<String, Object>> collect = list.stream().filter(
                v -> StringUtils.isNotBlank(MapUtils.getString(v, "paymentCode", ""))).collect(
                Collectors.toList());
        // 佣金总额
        double commissionAmount = collect.stream().filter(v ->
                ("第三方代付".equals(MapUtils.getString(v, "commissionType", ""))
                        || "直接付款".equals(MapUtils.getString(v, "commissionType", "")))).mapToDouble(
                v -> MapUtils.getDoubleValue(v, "commissionAmount", 0d)).sum();
        // 已发总额
        double paidAmount = collect.stream().filter(v ->
                ("第三方代付".equals(MapUtils.getString(v, "commissionType", ""))
                        || "直接付款".equals(MapUtils.getString(v, "commissionType", "")))).mapToDouble(
                v -> MapUtils.getDoubleValue(v, "paidAmount", 0d)).sum();
        int pageNum = MapUtils.getIntValue(param, "pageNum", 0);
        int pageSize = MapUtils.getIntValue(param, "pageSize", 10);
        result.put("total", list.size());
        result.put("list", getPageList(list, pageNum, pageSize));
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("dsh", dsh);
        result.put("xj", xj);
        result.put("fxj", fxj);
        result.put("commissionAmount", commissionAmount);
        result.put("paidAmount", paidAmount);
        return result;
    }

    /**
     * 获取无效数据列表
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getInvalidCommissionList(Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        String brokerId = MapUtils.getString(param, "brokerId");
        int pageNum = MapUtils.getIntValue(param, "pageNum");
        int pageSize = MapUtils.getIntValue(param, "pageSize");
        // 查询销管视图获取数据
        List<Map<String, Object>> xgList = selectViewList(param, 0);
        // 查询本系统失效数据
        List<Map<String, Object>> invalidDataList = commissionMapper.getInvalidDataList(param);
        //copy旭客数据
        List<String> tradeGuIdList = new ArrayList<>();
        for (Map map : invalidDataList){
            tradeGuIdList.add(String.valueOf(map.get("tradeGUID")));
        }
        for (int i = 0; i < xgList.size(); i++) {
            if(!tradeGuIdList.contains(String.valueOf(xgList.get(i).get("transactionId")))){
                invalidDataList.add(xgList.get(i));
            }
        }
        result.put("total", invalidDataList.size());
        result.put("list", PageUtil.toPage(pageNum, pageSize, invalidDataList));
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 查询销管视图获取数据
     *
     * @param param
     * @param status 0无效、1有效
     * @return
     */
    private List<Map<String, Object>> selectViewList(Map<String, Object> param, Integer status) {
        String brokerId = MapUtils.getString(param, "brokerId");
        List<String> projectNameList = (List<String>) param.get("projectNames");

        // 拼接sql
        StringBuilder sql = new StringBuilder("SELECT " +
                "(@num :=@num + 1) num," +
                "project_id as projectId," +
                "project_name as projectName," +
                "room_name as roomName," +
                "intention_id as intentionId," +
                "customer_name as customerName," +
                "concat(left(customer_mobile,3),'****',right(customer_mobile,4)) customerMobile," +
                "contract_date as contractDate," +
                "broker_id as brokerId," +
                "counselor_name as counselorName," +
                "is_commission as isCommission," +
                "no_commission_cause as noCommissionCause," +
                "project_code as projectCode," +
                "project_time as projectTime," +
                "payment_code as paymentCode," +
                "IFNULL(commission_amount,0) as commissionAmount," +
                "IFNULL(paid_amount,0) as paidAmount," +
                "IFNULL(paid_radio,0) as paidRadio," +
                "case status when '激活' then 1 else 0 end status," +
                "failure_cause as failureCause," +
                "update_time as updateTime, commission_type as commissionType," +
                "transaction_id as transactionId" +
                " FROM view_xkj_commission_detail, (SELECT @num := 0) t1  WHERE IFNULL(project_code,'') != '' AND transaction_status = '签约'");
        if (status != null) {
            String str = status == 1 ? "激活" : "关闭";
            sql.append(" AND status = '").append(str).append("'");
        }
        if (StringUtils.isNotBlank(brokerId)) {
            sql.append(" AND broker_id = '").append(brokerId).append("'");
        }
        if (CollectionUtil.isNotEmpty(projectNameList)) {
            String projectNames = String.join("','", projectNameList);
            sql.append(" AND project_name IN ('").append(projectNames).append("')");
        }

        List<Map<String, Object>> list = null;
        Connection connection = XGConnectionManager.getInstance().getConnection();
        try {
            list = new QueryRunner().query(connection, sql.toString(), new MapListHandler());
        } catch (Exception e) {
            list = new ArrayList<>();
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private List<Map<String, Object>> getPageList(List<Map<String, Object>> list, int pageNum, int pageSize) {
        int size = list.size();
        int pageCount = size / pageSize;
        int fromIndex = pageSize * (pageNum - 1);
        int toIndex = fromIndex + pageSize;
        if (toIndex >= size) {
            toIndex = size;
        }
        if (pageNum > pageCount + 1) {
            fromIndex = 0;
            toIndex = 0;
        }
        return list.subList(fromIndex, toIndex);
    }
}
