-- noinspection SqlNoDataSourceInspectionForFile

-- noinspection SqlDialectInspectionForFile



-- 添加房间任务表
CREATE TABLE `t_mm_room_task` (
  `task_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '任务id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目名称',
  `project_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目id',
  `apply_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '申请名称',
  `apply_time` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '申请日期',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态 0：草稿 1：已执行',
  `adjust_num` int(11) DEFAULT NULL COMMENT '调整套数',
  `before_ys_bld_area` decimal(10,2) DEFAULT NULL COMMENT '调整前预售建筑面积',
  `before_ys_tn_area` decimal(10,2) DEFAULT NULL COMMENT '调整前预售套内面积',
  `before_sc_bld_area` decimal(10,2) DEFAULT NULL COMMENT '调整前实测建筑面积',
  `before_sc_tn_area` decimal(10,2) DEFAULT NULL COMMENT '调整前实测套内面积',
  `after_ys_bld_area` decimal(10,2) DEFAULT NULL COMMENT '调整后预售建筑面积',
  `after_ys_tn_area` decimal(10,2) DEFAULT NULL COMMENT '调整后预售套内面积',
  `after_sc_bld_area` decimal(10,2) DEFAULT NULL COMMENT '调整后实测建筑面积',
  `after_sc_tn_area` decimal(10,2) DEFAULT NULL COMMENT '调整后实测套内面积',
  `agent` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '经办人',
  `create_time` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `update_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT '0' COMMENT '是否删除 0：正常；1：删除',
  `remark` text COLLATE utf8_unicode_ci COMMENT '备注',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='房间任务表';

-- 添加价格任务表
CREATE TABLE `t_mm_price_task` (
  `task_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '任务id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目名称',
  `project_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目id',
  `apply_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '申请名称',
  `apply_time` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '申请日期',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态 0：草稿 1：已执行',
  `adjust_num` int(11) DEFAULT NULL COMMENT '调整套数',
  `before_bz_total_price` decimal(10,2) DEFAULT NULL COMMENT '调整前标准总价',
  `after_bz_total_price` decimal(10,2) DEFAULT NULL COMMENT '调整后标准总价',
  `before_bz_bld_price` decimal(10,2) DEFAULT NULL COMMENT '调整前建筑标准单价',
  `before_bz_tn_price` decimal(10,2) DEFAULT NULL COMMENT '调整前套内标准单价',
  `after_bz_bld_price` decimal(10,2) DEFAULT NULL COMMENT '调整后建筑标准单价',
  `after_bz_tn_price` decimal(10,2) DEFAULT NULL COMMENT '调整后套内标准单价',
  `before_dj_total` decimal(10,2) DEFAULT NULL COMMENT '调整前低价总价',
  `after_dj_total` decimal(10,2) DEFAULT NULL COMMENT '调整后低价总价',
  `before_bld_dj_price` decimal(10,2) DEFAULT NULL COMMENT '调整前建筑低价单价',
  `before_tn_dj_price` decimal(10,2) DEFAULT NULL COMMENT '调整前套内低价单价',
  `after_dj_price` decimal(10,2) DEFAULT NULL COMMENT '调整后套内低价单价',
  `after_bld_dj_price` decimal(10,2) DEFAULT NULL COMMENT '调整后建筑低价单价',
  `valuation_type` tinyint(2) DEFAULT NULL COMMENT '计价方式 1:建筑面积 2:套内面积 3:套',
  `price_standard` tinyint(2) DEFAULT NULL COMMENT '价格标准 1:以面价总价为准 2:以建筑单价为准 3:以套内单价为准 4:以底价总价为准',
  `agent` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '经办人',
  `create_time` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `update_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT '0' COMMENT '是否删除 0：正常；1：删除',
  `remark` text COLLATE utf8_unicode_ci COMMENT '备注',
  `type` tinyint(2) DEFAULT NULL COMMENT '0:标准价录入 1:低价录入',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='价格任务表';

-- 添加 楼栋房间辅助表
CREATE TABLE `t_mm_build_room_plus` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '主键id',
  `room_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '房间ID',
  `room_info` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间全称',
  `room_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间简称',
  `room_code` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间编码',
  `room_no` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房号',
  `no` int(11) DEFAULT NULL COMMENT '房间序号',
  `status_enum` tinyint(4) DEFAULT NULL COMMENT '房间状态',
  `floor_name` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼层名称',
  `floor_no` int(11) DEFAULT NULL COMMENT '楼层编号',
  `unit_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单元ID',
  `unit_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单元名称',
  `unit_no` int(9) DEFAULT NULL COMMENT '单元编号',
  `build_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(计划系统楼栋ID)',
  `product_build_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼栋名称',
  `kingdee_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID（金蝶）',
  `my_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(明源)',
  `stage_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目分期ID',
  `stage_code` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期编号',
  `group_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团ID',
  `group_name` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `update_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT NULL COMMENT '是否删除 0：正常；1：删除',
  `project_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目名称',
  `stage_name` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期名称',
  `opening_lock` tinyint(4) DEFAULT NULL COMMENT '开盘锁',
  `bottom_price_return_house_lock` tinyint(4) DEFAULT NULL COMMENT '底价退房锁',
  `dj_bld_price` decimal(10,2) DEFAULT NULL COMMENT '底价建筑单价',
  `dj_tn_price` decimal(10,2) DEFAULT NULL COMMENT '底价套内单价',
  `bld_price` decimal(10,2) DEFAULT NULL COMMENT '建筑单价',
  `dj_total` decimal(10,2) DEFAULT NULL COMMENT '底价总价',
  `bld_area` decimal(10,2) DEFAULT NULL COMMENT '建筑面积',
  `is_area_modify` tinyint(4) DEFAULT NULL COMMENT '定价后面积发生变更',
  `trade_locker` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '锁定人',
  `is_trade_lock` tinyint(4) DEFAULT NULL COMMENT '是否交易锁',
  `is_tf_lock` tinyint(4) DEFAULT NULL COMMENT '标准价退房锁',
  `trade_lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `tf_date` datetime DEFAULT NULL COMMENT '退房日期',
  `hx_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型Id',
  `hx_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型名称',
  `tn_price` decimal(10,2) DEFAULT NULL COMMENT '套内单价',
  `tn_area` decimal(10,2) DEFAULT NULL COMMENT '套内面积',
  `total` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `dsp_bld_area` decimal(10,2) DEFAULT NULL COMMENT '待审批建筑面积',
  `dsp_tn_area` decimal(10,2) DEFAULT NULL COMMENT '待审批套内面积',
  `ys_bld_area` decimal(10,2) DEFAULT NULL COMMENT '预售建筑面积',
  `ys_tn_area` decimal(10,2) DEFAULT NULL COMMENT '预售套内面积',
  `sc_bld_area` decimal(10,2) DEFAULT NULL COMMENT '实测建筑面积',
  `sc_tn_area` decimal(10,2) DEFAULT NULL COMMENT '实测套内面积',
  `room_stru` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间结构',
  `dsp_area_status_enum` tinyint(4) DEFAULT NULL COMMENT '待审批面积状态',
  `cal_mode_enum` tinyint(4) DEFAULT NULL COMMENT '计价方式code',
  `cal_mode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '计价方式',
  `is_hf_lock` tinyint(4) DEFAULT NULL COMMENT '是否换房锁',
  `x_is_zc` tinyint(4) DEFAULT NULL COMMENT '是否自持',
  `area_status_enum` tinyint(4) DEFAULT NULL COMMENT '面积状态code',
  `area_status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面积状态',
  `status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间状态',
  `dsp_area_status` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '待审批面积状态',
  `is_annexe` tinyint(4) DEFAULT NULL COMMENT '是否附属房产',
  `main_room_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主房间GUID',
  `is_virtual_room` tinyint(4) DEFAULT NULL COMMENT '是否虚拟房间',
  `area_chg_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面积变更ID',
  `product_type_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型Id',
  `choose_room_lock_time` datetime DEFAULT NULL COMMENT '选房锁定时间',
  `choose_room_lock_end_time` datetime DEFAULT NULL COMMENT '选房锁定结束时间',
  `choose_room_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '选房唯一标识',
  `choose_room_lock_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '选房锁唯一标识',
  `choose_room_time` datetime DEFAULT NULL COMMENT '选房时间',
  `lsxk_time` datetime DEFAULT NULL COMMENT '临时销控时间',
  `jbr_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '经办人GUID',
  `ba_total` decimal(10,2) DEFAULT NULL COMMENT '备案总价',
  `ba_bld_price` decimal(10,2) DEFAULT NULL COMMENT '备案建筑单价',
  `ba_tn_price` decimal(10,2) DEFAULT NULL COMMENT '备案套内单价',
  `jf_date` datetime DEFAULT NULL COMMENT '交房日期',
  `opening_batch` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '开盘批次',
  `mj_bld_price` decimal(10,2) DEFAULT NULL COMMENT '面价建筑单价',
  `mj_tn_price` decimal(10,2) DEFAULT NULL COMMENT '面价套内单价',
  `mj_total` decimal(10,2) DEFAULT NULL COMMENT '面价总价',
  `task_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '任务id',
  `right_cell` int(9) DEFAULT '0' COMMENT '向右合并数量',
  `down_cell` int(9) DEFAULT '0' COMMENT '向下合并数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='楼栋房间辅助表';

-- 添加 楼层表
CREATE TABLE `t_mm_build_floor` (
  `floor_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '楼层ID',
  `floor_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼层名称',
  `floor_no` int(9) DEFAULT NULL COMMENT '楼层序号',
  `build_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `editor_time` datetime DEFAULT NULL COMMENT '更新时间',
  `creator` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `editor` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT NULL COMMENT '是否删除 0：正常；1：删除',
  `status` tinyint(2) DEFAULT NULL COMMENT '状态 0:禁用；1：启用',
  PRIMARY KEY (`floor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='楼层表';

-- 添加 楼栋户型表
CREATE TABLE `t_mm_build_hx` (
  `id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '户型主键',
  `hx_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型',
  `room_stru_code` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间类型代码',
  `room_stru` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间类型',
  `project_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '项目ID',
  `project_fid` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '分期项目ID',
  `x_hx_img` text COLLATE utf8_unicode_ci COMMENT '户型图',
  `x_hx_img_url` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型图地址',
  `x_hx_img_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型图名称',
  `plan_url` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平面图地址',
  `remark` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型描述',
  `plan` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '平面图',
  `tn_area` decimal(12,4) DEFAULT NULL COMMENT '套内面积',
  `bld_area` decimal(12,4) DEFAULT NULL COMMENT '建筑面积',
  `x_hx_other_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型别名',
  `x_area` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '区域',
  `x_hx_product_type` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型产品类型',
  `x_area_section` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面积段',
  `x_staircase_proportion` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '梯户比',
  `x_wide_number` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面宽数',
  `x_scopeenum` tinyint(4) DEFAULT NULL COMMENT '使用范围code',
  `x_scope` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '使用范围',
  `x_rowId` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '设计户型Id',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `modified_time` datetime DEFAULT NULL COMMENT '修改时间',
  `modified` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '修改人',
  `is_del` tinyint(4) DEFAULT '0' COMMENT '是否删除',
  `alias` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型别名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_hxid` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='楼栋户型表';

-- 添加 楼栋房间表
CREATE TABLE `t_mm_build_room` (
  `room_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '房间ID',
  `room_info` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间全称',
  `room_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间简称',
  `room_code` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间编码',
  `room_no` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房号',
  `no` int(11) DEFAULT NULL COMMENT '房间序号',
  `status_enum` tinyint(4) DEFAULT NULL COMMENT '房间状态(1:待售2:认购3:销控4:签约5：预留)',
  `floor_name` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼层名称',
  `floor_no` int(11) DEFAULT NULL COMMENT '楼层编号',
  `unit_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单元ID',
  `unit_name` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单元名称',
  `unit_no` int(9) DEFAULT NULL COMMENT '单元编号',
  `build_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(计划系统楼栋ID)',
  `product_build_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼栋名称',
  `kingdee_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID（金蝶）',
  `my_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(明源)',
  `stage_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目分期ID',
  `stage_code` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期编号',
  `group_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团ID',
  `group_name` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `update_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT '0' COMMENT '是否删除 0：正常；1：删除',
  `project_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目名称',
  `stage_name` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期名称',
  `opening_lock` tinyint(4) DEFAULT NULL COMMENT '开盘锁',
  `bottom_price_return_house_lock` tinyint(4) DEFAULT NULL COMMENT '底价退房锁',
  `dj_bld_price` decimal(10,2) DEFAULT NULL COMMENT '底价建筑单价',
  `dj_tn_price` decimal(10,2) DEFAULT NULL COMMENT '底价套内单价',
  `bld_price` decimal(10,2) DEFAULT NULL COMMENT '建筑单价',
  `dj_total` decimal(10,2) DEFAULT NULL COMMENT '底价总价',
  `bld_area` decimal(10,2) DEFAULT NULL COMMENT '建筑面积',
  `is_area_modify` tinyint(4) DEFAULT NULL COMMENT '定价后面积发生变更',
  `trade_locker` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '锁定人',
  `is_trade_lock` tinyint(4) DEFAULT NULL COMMENT '是否交易锁',
  `is_tf_lock` tinyint(4) DEFAULT NULL COMMENT '标准价退房锁',
  `trade_lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `tf_date` datetime DEFAULT NULL COMMENT '退房日期',
  `hx_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型Id',
  `hx_name` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '户型名称',
  `tn_price` decimal(10,2) DEFAULT NULL COMMENT '套内单价',
  `tn_area` decimal(10,2) DEFAULT NULL COMMENT '套内面积',
  `total` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `dsp_bld_area` decimal(10,2) DEFAULT NULL COMMENT '待审批建筑面积',
  `dsp_tn_area` decimal(10,2) DEFAULT NULL COMMENT '待审批套内面积',
  `ys_bld_area` decimal(10,2) DEFAULT NULL COMMENT '预售建筑面积',
  `ys_tn_area` decimal(10,2) DEFAULT NULL COMMENT '预售套内面积',
  `sc_bld_area` decimal(10,2) DEFAULT NULL COMMENT '实测建筑面积',
  `sc_tn_area` decimal(10,2) DEFAULT NULL COMMENT '实测套内面积',
  `room_stru` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间结构',
  `dsp_area_status_enum` tinyint(4) DEFAULT NULL COMMENT '待审批面积状态',
  `cal_mode_enum` tinyint(4) DEFAULT NULL COMMENT '计价方式code',
  `cal_mode` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '计价方式',
  `is_hf_lock` tinyint(4) DEFAULT NULL COMMENT '是否换房锁',
  `x_is_zc` tinyint(4) DEFAULT NULL COMMENT '是否自持',
  `area_status_enum` tinyint(4) DEFAULT NULL COMMENT '面积状态code',
  `area_status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面积状态',
  `status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '房间状态',
  `dsp_area_status` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '待审批面积状态',
  `is_annexe` tinyint(4) DEFAULT NULL COMMENT '是否附属房产',
  `main_room_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '主房间GUID',
  `is_virtual_room` tinyint(4) DEFAULT NULL COMMENT '是否虚拟房间',
  `area_chg_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '面积变更ID',
  `product_type_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型Id',
  `choose_room_lock_time` datetime DEFAULT NULL COMMENT '选房锁定时间',
  `choose_room_lock_end_time` datetime DEFAULT NULL COMMENT '选房锁定结束时间',
  `choose_room_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '选房唯一标识',
  `choose_room_lock_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '选房锁唯一标识',
  `choose_room_time` datetime DEFAULT NULL COMMENT '选房时间',
  `lsxk_time` datetime DEFAULT NULL COMMENT '临时销控时间',
  `jbr_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '经办人GUID',
  `ba_total` decimal(10,2) DEFAULT NULL COMMENT '备案总价',
  `ba_bld_price` decimal(10,2) DEFAULT NULL COMMENT '备案建筑单价',
  `ba_tn_price` decimal(10,2) DEFAULT NULL COMMENT '备案套内单价',
  `jf_date` datetime DEFAULT NULL COMMENT '交房日期',
  `opening_batch` varchar(128) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '开盘批次',
  `mj_bld_price` decimal(10,2) DEFAULT NULL COMMENT '面价建筑单价',
  `mj_tn_price` decimal(10,2) DEFAULT NULL COMMENT '面价套内单价',
  `mj_total` decimal(10,2) DEFAULT NULL COMMENT '面价总价',
  `task_id` varchar(36) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '任务id',
  `right_cell` int(9) DEFAULT '0' COMMENT '向右合并数量',
  `down_cell` int(9) DEFAULT '0' COMMENT '向下合并数量',
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='楼栋房间表';

-- 添加 楼栋单元表
CREATE TABLE `t_mm_build_unit` (
  `unit_id` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '单元ID',
  `unit_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '单元名称',
  `unit_no` int(9) DEFAULT NULL COMMENT '单元编号',
  `build_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(计划系统楼栋ID)',
  `kingdee_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID（金蝶）',
  `my_build_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '楼栋ID(明源)',
  `stage_id` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目分期ID',
  `stage_code` varchar(40) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期编号',
  `group_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团ID',
  `group_name` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '组团名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '创建人账号',
  `update_user` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '修改人账号',
  `is_delete` tinyint(2) DEFAULT '0' COMMENT '是否删除 0：正常；1：删除',
  `status` tinyint(2) DEFAULT '1' COMMENT '状态 0:禁用；1：启用',
  `project_id` varchar(36) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目id',
  `project_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '项目名称',
  `stage_name` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分期名称',
  `households_num` int(11) DEFAULT '0' COMMENT '户数',
  `floors_name` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '楼层名称',
  `total_households_num` int(11) DEFAULT NULL COMMENT '总户数',
  PRIMARY KEY (`unit_id`),
  KEY `idx_unitid` (`unit_id`),
  KEY `idx_unitno` (`unit_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='楼栋单元表';

-- 修改 项目表
ALTER TABLE `b_project` ADD COLUMN `stage_id` varchar(36) DEFAULT NULL COMMENT '分期id';
ALTER TABLE `b_project` ADD COLUMN `build_id` varchar(36) DEFAULT NULL COMMENT '楼栋id';
ALTER TABLE `b_project` ADD COLUMN `group_id` varchar(36) DEFAULT NULL COMMENT '组团id';
ALTER TABLE `b_project` ADD COLUMN `project_pic` varchar(255) DEFAULT NULL COMMENT '项目图片';
ALTER TABLE `b_project` ADD COLUMN `buildland_area` decimal(18,4) DEFAULT NULL COMMENT '建筑用地面积';
ALTER TABLE `b_project` ADD COLUMN `totalland_area` decimal(18,4) DEFAULT NULL COMMENT '总用地面积';
ALTER TABLE `b_project` ADD COLUMN `up_build_area` decimal(18,4) DEFAULT NULL COMMENT '地下建筑面积';
ALTER TABLE `b_project` ADD COLUMN `totalbuild_area` decimal(18,4) DEFAULT NULL COMMENT '总建筑面积';
ALTER TABLE `b_project` ADD COLUMN `on_build_area` decimal(18,4) DEFAULT NULL COMMENT '地上建筑面积';
ALTER TABLE `b_project` ADD COLUMN `get_time` datetime DEFAULT NULL COMMENT '项目获取日期';
ALTER TABLE `b_project` ADD COLUMN `build_volume_area` decimal(18,4) DEFAULT NULL COMMENT '计容建筑面积';
ALTER TABLE `b_project` ADD COLUMN `plot_ratio` decimal(18,4) DEFAULT NULL COMMENT '容积率';
ALTER TABLE `b_project` ADD COLUMN `total_sale_area` decimal(18,4) DEFAULT NULL COMMENT '总可售面积';
ALTER TABLE `b_project` ADD COLUMN `full_path` varchar(1000) DEFAULT NULL COMMENT '全路径';

-- 2021-12-06 修改 s_menus
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('00e9d7c1-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '分期新增', 'stage_add', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:00', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:00', 1, 0, 'stage_add', NULL, 2, NULL, NULL, 'stage_add', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('0c920a18-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '分期编辑', 'stage_edit', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:20', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:20', 1, 0, 'stage_edit', NULL, 2, NULL, NULL, 'stage_edit', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('0d867460-534f-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '编辑楼栋', 'build_edit', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:05:53', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:05:53', 1, 0, 'build_edit', NULL, 2, NULL, NULL, 'build_edit', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('16c47e6c-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '分期删除', 'stage_del', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:37', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:27:37', 1, 0, 'stage_del', NULL, 2, NULL, NULL, 'stage_del', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('24266ceb-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '组团新增', 'group_add', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:00', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:00', 1, 0, 'group_add', NULL, 2, NULL, NULL, 'group_add', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('34aaa127-534f-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '删除楼栋', 'build_del', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:06:59', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:06:59', 1, 0, 'build_del', NULL, 2, NULL, NULL, 'build_del', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('35703184-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '组团编辑', 'group_edit', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:29', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:29', 1, 0, 'group_edit', NULL, 2, NULL, NULL, 'group_edit', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('3e5e8939-5352-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '组团删除', 'group_del', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:44', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:28:44', 1, 0, 'group_del', NULL, 2, NULL, NULL, 'group_del', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('46f5992c-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '生成房源', 'rooms_creat', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:00:20', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:00:20', 1, 0, 'rooms_creat', NULL, 2, NULL, NULL, 'rooms_creat', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('55a481ac-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '调整房源', 'rooms_edit', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:00:45', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:00:45', 1, 0, 'rooms_edit', NULL, 2, NULL, NULL, 'rooms_edit', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('6c8cf3c6-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '楼栋复制粘贴', 'build_copy_paste', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:01:23', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:01:23', 1, 0, 'build_copy_paste', NULL, 2, NULL, NULL, 'build_copy_paste', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('8052c1a0-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '插入整列', 'room_insert_cols', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:01:56', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:01:56', 1, 0, 'room_insert_cols', NULL, 2, NULL, NULL, 'room_insert_cols', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('8afd04fa-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '删除整列', 'room_del_cols', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:14', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:14', 1, 0, 'room_del_cols', NULL, 2, NULL, NULL, 'room_del_cols', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('9800b1d6-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '向右合并', 'room_merge_right', NULL, 0, 1, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:36', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:36', 1, 0, 'room_merge_right', NULL, 2, NULL, NULL, 'room_merge_right', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('a02e40e9-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '向下合并', 'room_merge_down', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:50', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:02:50', 1, 0, 'room_merge_down', NULL, 2, NULL, NULL, 'room_merge_down', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('ad4fcda2-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '房间拆分', 'room_splice', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:12', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:12', 1, 0, 'room_splice', NULL, 2, NULL, NULL, 'room_splice', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('b5fc06be-5350-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '新增楼栋', 'build_add', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:17:45', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:17:45', 1, 0, 'build_add', NULL, 2, NULL, NULL, 'build_add', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('b63321f1-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '新增房间', 'room_insert_room', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:27', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:27', 1, 0, 'room_insert_room', NULL, 2, NULL, NULL, 'room_insert_room', 1, NULL, NULL);
INSERT INTO `s_menus`(`ID`, `PID`, `MenuName`, `Url`, `ImageUrl`, `IsHomePage`, `IsShow`, `Levels`, `ListIndex`, `FullPath`, `IsLast`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `MenuSysName`, `IconClass`, `menusType`, `Remarks`, `redirect`, `component`, `alwaysShow`, `meta`, `ProductID`) VALUES ('bd7c143c-534e-11ec-9e8e-00163e082b08', 'bacc9fb7-4d98-11ec-9e8e-00163e082b08', '删除房间', 'room_del_room', NULL, 0, 0, 4, 1, NULL, 1, 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:39', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-02 17:03:39', 1, 0, 'room_del_room', NULL, 2, NULL, NULL, 'room_del_room', 1, NULL, NULL);

-- 修改 房源管理 s_dictionary (集团级, 项目级)
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('6fdf5235-d0e5-4536-8b55-a4e2b5c92498', '-1', '1', 'fygl001', '房源管理', 1, 0, '', 0, '房源管理', NULL, NULL, NULL, NULL, 'ede1b679-3546-11e7-a3f8-5254007b6f02', 'ee3b2466-3546-11e7-a3f8-5254007b6f02', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-06 10:32:55', NULL, NULL, 1, 0, '0003DCA4-B01F-EA11-80BB-005056A37AFA', 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('ebc283b1-e242-458f-82ee-626329b116d1', '-1', '2', 'c250a771-5225-4813-a40f-ba5b53a7f83a', '房源管理', 1, 0, '', 0, '房源管理', NULL, NULL, NULL, NULL, 'ede1b679-3546-11e7-a3f8-5254007b6f02', 'ee3b2466-3546-11e7-a3f8-5254007b6f02', 'e7cf453b-214e-4500-bf99-58ea0cc9b4e5', '2021-12-06 10:43:35', NULL, NULL, 1, 0, '0003DCA4-B01F-EA11-80BB-005056A37AFA', 2, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 添加计价方式, 价格标准, 业态
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('47f4e363-7bd7-4668-b194-f19d7254ab77', '6fdf5235-d0e5-4536-8b55-a4e2b5c92498', '1', 'jjfs1002', '计价方式', 2, 1, '', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-06 10:34:21', NULL, '2021-12-06 10:36:11', 1, 0, NULL, 1, 2, 8, '', 0, '', '0', '0', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('9fadc7c3-6541-4911-829a-cc7d94458010', '6fdf5235-d0e5-4536-8b55-a4e2b5c92498', '3', 'jgbz1003', '价格标准', 2, 1, '', 0, '房源管理/价格标准', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-06 10:35:15', NULL, '2021-12-06 10:37:45', 1, 0, NULL, 1, 2, 8, '', 0, '', '0', '0', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('d68c6053-72ea-48d4-b747-47c43ee576e7', '6fdf5235-d0e5-4536-8b55-a4e2b5c92498', '2', 'cpyt1001', '业态', 2, 1, '', 0, '房源管理/业态', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-06 10:34:42', NULL, NULL, 1, 0, NULL, 1, 2, 8, '', 0, '', '0', '0', 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 添加计价方式
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('267e03de-df1a-46f9-bb7d-a44996f707d4', '47f4e363-7bd7-4668-b194-f19d7254ab77', '0', '1', '建筑面积', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:23:17', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '建筑面积', '1', 0, 0, NULL, NULL, '49389128-9baf-4c5c-ba0f-2ab61dfff926', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('39458fa7-95f4-4443-946f-372cd4bbf8bd', '47f4e363-7bd7-4668-b194-f19d7254ab77', '2', '3', '套', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:23:17', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '套', '3', 0, 0, NULL, NULL, '49389128-9baf-4c5c-ba0f-2ab61dfff926', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('3fca6e55-b8b5-4409-90d8-b73f3de2ce34', '47f4e363-7bd7-4668-b194-f19d7254ab77', '1', '2', '套内面积', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:23:17', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '套内面积', '2', 0, 0, NULL, NULL, '49389128-9baf-4c5c-ba0f-2ab61dfff926', NULL);

-- 添加价格标准
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('2893a059-79f4-4a5c-8cff-140115fd431f', '9fadc7c3-6541-4911-829a-cc7d94458010', '3', '4', '以底价总价为准', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:26:26', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '以底价总价为准', '4', 0, 0, NULL, NULL, 'd4919fcf-70d9-463c-ba9b-d5d33d77905d', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('781654f4-8d07-4ff2-ac3a-dbeed953f3ac', '9fadc7c3-6541-4911-829a-cc7d94458010', '1', '2', '以建筑单价为准', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:26:26', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '以建筑单价为准', '2', 0, 0, NULL, NULL, 'd4919fcf-70d9-463c-ba9b-d5d33d77905d', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('dd585f77-dc8f-4e35-8343-04219f6db592', '9fadc7c3-6541-4911-829a-cc7d94458010', '0', '1', '以面价总价为准', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:26:26', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '以面价总价为准', '1', 0, 0, NULL, NULL, 'd4919fcf-70d9-463c-ba9b-d5d33d77905d', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('e39c13c6-62e7-4497-9e1a-549b90db1cf7', '9fadc7c3-6541-4911-829a-cc7d94458010', '2', '3', '以套内单价为准', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 11:26:26', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '以套内单价为准', '3', 0, 0, NULL, NULL, 'd4919fcf-70d9-463c-ba9b-d5d33d77905d', NULL);

-- 2021-12-03 添加业态
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('04c897e5-af72-4774-9115-57fd62529215', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '45', '66', '窗井', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '窗井', '66', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('07791ad8-05cc-4699-b2de-6cf0c0b46814', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '6', '6', '底商', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '底商', '6', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('081606f2-f11d-4db0-836d-4520e3a1b219', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '11', '32', '文化活动站', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '文化活动站', '32', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('09496e2d-fb09-4734-b45c-bd7f2fb22568', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '31', '52', '上跃', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '上跃', '52', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('0b7f3c90-c485-4dd6-9a92-e07322cde347', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '9', '9', 'loft公寓', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, 'loft公寓', '9', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('0d3957e6-8ee0-40c5-95bf-d141a37725bd', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '32', '53', '下跃', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '下跃', '53', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('1a118240-fe83-4b7a-b80a-5bc07c7c6124', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '43', '64', '跃层', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '跃层', '64', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('20308494-1b36-4bd5-9b52-790f91700c67', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '42', '63', '管井', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '管井', '63', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('2b328490-cd0c-47ad-8e59-69a17477bc34', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '20', '41', '菜市场', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '菜市场', '41', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('2e604758-110b-402a-8aa4-3dc63cf9fed8', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '39', '60', '加建小间', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '加建小间', '60', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('387855dc-481c-46f5-9ece-722c909af69c', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '35', '56', '起居室', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '起居室', '56', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('3ee9afb1-7544-4b3a-9b5f-623576788e52', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '36', '57', '首层花园', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '首层花园', '57', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('40c0158e-377c-459d-b178-2b31c92a287c', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '33', '54', '景桥', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '景桥', '54', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('40f1364c-d9ad-4bf5-bd8c-d12986aa9b57', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '51', '72', '凸窗', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '凸窗', '72', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('41ffabd1-94c4-4b46-b6ee-42f7c9469648', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '0', '0', '住宅', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '住宅', '0', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('4225906e-7ebf-44c0-939f-0ff81d1c15c0', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '10', '31', '社区卫生服务站', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '社区卫生服务站', '31', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('42f216a9-04b9-42d3-88e8-81b31e04563b', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '12', '33', '物业管理用房', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '物业管理用房', '33', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('46cf3f91-bd59-4aea-8a83-5a6d91c505e8', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '49', '70', '过道储藏间', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '过道储藏间', '70', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('52dd8468-6da0-4be8-959c-70c9c8ebe60f', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '2', '2', '办公', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '办公', '2', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('5484daef-0055-4369-b8ba-971b404ada3e', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '34', '55', '挑空', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '挑空', '55', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('631a695b-daa2-4bcf-a4c6-532c440d35b9', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '24', '45', '避难间', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '避难间', '45', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('68f3f4eb-13b6-4db9-b9c2-e08408df78b6', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '16', '37', '居委会', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '居委会', '37', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('69a302eb-e9e4-404c-9924-f8f2eed11870', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '46', '67', '平台', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '平台', '67', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('69b562f3-c532-49bc-b6ec-01e5661a7ffc', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '40', '61', '首层庭院', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '首层庭院', '61', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('6f72057a-831f-4f5b-9a20-5d495d3e8dca', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '19', '40', '公厕', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '公厕', '40', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('75c284ea-bc26-49f3-a102-2d2cf92881f7', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '37', '58', '下沉庭院', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '下沉庭院', '58', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('76533579-13e9-445b-80b2-ab7c97faf9c1', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '25', '46', '变配电室', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '变配电室', '46', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('7d4b8a81-c34b-40c0-a3dc-b72c86021947', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '4', '4', '集中商业', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '集中商业', '4', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('8786f539-d506-4986-97fb-dd795cf9242a', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '44', '65', '小院', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '小院', '65', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('8a4ad052-bac7-4de0-86fc-89ccff4e2c9c', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '30', '51', '阳台', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '阳台', '51', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('8b754ba8-c01f-43e0-8e98-e0cb5ad667ab', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '17', '38', '社区服务中心', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '社区服务中心', '38', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('8cf40565-82ac-4901-9bd2-e9a0eb9d66d3', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '21', '42', '门厅', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '门厅', '42', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('8fc79bef-855d-4e39-b7b0-da1360164011', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '18', '39', '社区综合服务', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '社区综合服务', '39', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('93b29589-6233-4687-89b3-93ddbeff9a16', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '13', '34', '邮政所', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '邮政所', '34', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('97a58684-7a59-42ca-9ec2-84092d65e4cd', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '1', '1', '商业', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '商业', '1', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('9e620a30-de58-4692-9198-b0f6685c1dc8', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '5', '5', '街区商业', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '街区商业', '5', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('ad402da7-70eb-4d0a-a1fd-cfa5f336b8d0', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '15', '36', '设备夹层', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '设备夹层', '36', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('b6feb4df-d77e-4c94-805f-49b47c24c489', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '38', '59', '阁楼', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '阁楼', '59', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('bdb33351-0a9a-4080-a2a6-fd92d3831c57', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '48', '69', '公共卫生间', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '公共卫生间', '69', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('bf0d3d2e-a7f0-443a-b2f4-22e0e0fd8aae', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '27', '48', '赠送产品', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '赠送产品', '48', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('c17e9a65-868d-417e-a7ab-a5f7d84a84ca', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '50', '71', '首层廊桥', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '首层廊桥', '71', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('c59e0f28-2f9d-4049-81f3-6f2d1743c5ed', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '28', '49', '露台', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '露台', '49', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('cffae70d-af13-4bc5-ab6e-444f5d3618d2', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '8', '8', '平层公寓', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '平层公寓', '8', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('d1ec35f8-a032-4202-b7eb-f5c3403d64fd', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '26', '47', '配套', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '配套', '47', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('d4f05c24-4542-42d2-8059-d543a4d7cdbc', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '29', '50', '飘窗', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '飘窗', '50', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('e003f811-765e-4c7b-86e6-988d6bafa476', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '22', '43', '其他', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '其他', '43', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('e77de541-8d7f-47f6-8f2c-7f0015a69801', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '3', '3', '酒店', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '酒店', '3', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('e7b6e07d-7c2f-4f97-92e7-8a91caff5d4b', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '47', '68', '保洁室', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '保洁室', '68', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('f11a0416-7e7c-4188-b86b-259bd81bb8f3', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '23', '44', '托老所', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '托老所', '44', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('f6a6ebcb-ffc8-45f5-94d1-0ce44cf2b166', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '7', '7', '独立商业', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '独立商业', '7', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('face71aa-2973-4c3e-b17f-e5d92dec6e23', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '14', '35', '市政府', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '市政府', '35', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('fcd1817a-335b-4a0c-80db-b1428d6378ab', 'd68c6053-72ea-48d4-b747-47c43ee576e7', '41', '62', '设备平台', 2, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-03 12:18:05', NULL, NULL, 1, 0, NULL, 1, 2, 1, NULL, NULL, NULL, NULL, NULL, 2, '设备平台', '62', 0, 0, NULL, NULL, '8d3962cf-ccd0-4a17-827b-7b05cc43d17e', NULL);

-- 添加户型
INSERT INTO `s_dictionary`(`ID`, `PID`, `ListIndex`, `DictCode`, `DictName`, `DictType`, `Levels`, `Remark`, `IsReadOnly`, `FullPath`, `Ext1`, `Ext2`, `Ext3`, `Ext4`, `AuthCompanyID`, `ProductID`, `Creator`, `CreateTime`, `Editor`, `EditTime`, `Status`, `IsDel`, `ProjectID`, `DictionaryLevel`, `ParamMode`, `DictTypeMode`, `ParamDefaults`, `DecimalPlaces`, `Unit`, `MinVal`, `MaxVal`, `StorageMode`, `SelectText`, `NumVal`, `IsAllowDel`, `IsDefault`, `customArrayOne`, `customArrayTwo`, `PublicPid`, `CityId`) VALUES ('e29560db-ea4b-4796-a42e-69c8d2374fbb', 'ebc283b1-e242-458f-82ee-626329b116d1', '1', 'hx001', '户型', 2, 1, '', 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2021-12-06 10:45:19', NULL, NULL, 1, 0, '0003DCA4-B01F-EA11-80BB-005056A37AFA', 2, 3, 0, '/#/custom', 0, '', '0', '0', 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 2021-12-07 添加索引
alter table b_project add index full_path_ind(`full_path`);

ALTER TABLE `s_dictionary`
ADD COLUMN `global_flag` varchar(36) NULL DEFAULT NULL COMMENT '全局标识 1：全局';

-- 2021-12-20 添加线下支付审核表 zhyx数据库已经执行
CREATE TABLE `b_opportunity_offline_certificate` (
  `id` varchar(36) NOT NULL COMMENT '主键id',
  `region` varchar(255) DEFAULT NULL COMMENT '区域',
  `project_id` varchar(36) DEFAULT NULL COMMENT '项目ID',
  `project_name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `activity_id` varchar(255) DEFAULT NULL COMMENT '认购活动id',
  `activity_name` varchar(255) DEFAULT NULL COMMENT '认购活动名称',
  `room_id` varchar(36) DEFAULT NULL COMMENT '房间id',
  `room_name` varchar(255) DEFAULT NULL COMMENT '房间名称',
  `client_name` varchar(255) DEFAULT NULL COMMENT '认购人名称',
  `client_mobile` varchar(255) DEFAULT NULL COMMENT '认购人手机号',
  `submit` varchar(255) DEFAULT NULL COMMENT '提交人',
  `card_on_pic` text COMMENT '身份证正面',
  `card_dn_pic` text COMMENT '身份证反面',
  `certificate_pic` text COMMENT '支付凭证图片',
  `status` tinyint(4) DEFAULT NULL COMMENT '审批状态（1：待审核 2：审批通过, 3: 驳回）',
  `status_name` varchar(36) DEFAULT NULL COMMENT '审批状态名称',
  `create_time` datetime DEFAULT NULL COMMENT '提交审核时间',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_id` varchar(36) DEFAULT NULL COMMENT '审批人ID',
  `approve_name` varchar(36) DEFAULT NULL COMMENT '审批人名称',
  `trade_guid` varchar(36) DEFAULT NULL COMMENT '交易ID',
  `rejection_reason` text COMMENT '驳回原因',
  `is_del` tinyint(4) DEFAULT '0' COMMENT '是否删除（1：是 0：否）',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_r_name` (`room_name`),
  KEY `idx_name` (`client_name`),
  KEY `sub_index` (`submit`),
  KEY `tra_index` (`trade_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='线下支付审核表';

ALTER TABLE `t_mm_designbuild` ADD COLUMN `deposit` decimal(20,2) DEFAULT NULL COMMENT '楼栋定金';


-- 2022-01-05

CREATE TABLE `b_qw_mass_texting` (
  `id` varchar(36) NOT NULL COMMENT '主键id',
  `task_name` varchar(36) DEFAULT NULL COMMENT '任务名称',
  `project_id` varchar(255) DEFAULT NULL COMMENT '项目id',
  `content` text DEFAULT NULL COMMENT '内容',
  `img_url` text DEFAULT NULL COMMENT '图片路径',
  `video_url` text DEFAULT NULL COMMENT '视频路径',
  `file_url` text DEFAULT NULL COMMENT '文件路径',
  `send_time` datetime DEFAULT NULL COMMENT '群发时间',
  `senders` text DEFAULT NULL COMMENT '群发成员',
  `send_type` tinyint(4) DEFAULT 0 COMMENT '0：群发到客户；1：群发到客户群',
  `flag_type` tinyint(4) DEFAULT NULL COMMENT '客户标签',
  `flag_type_name` varchar(255) DEFAULT NULL COMMENT '客户标签名称',
  `applet_name` varchar(255) DEFAULT NULL COMMENT '群发小程序',
  `app_id` varchar(255) DEFAULT NULL COMMENT '小程序appid',
  `app_url` varchar(255) DEFAULT NULL COMMENT '小程序页面路径',
  `params` varchar(255) DEFAULT NULL COMMENT '参数',
  `scheme` varchar(255) DEFAULT NULL COMMENT '群发h5',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `creater` varchar(36) DEFAULT NULL COMMENT '创建人员',
  `updater` varchar(36) DEFAULT NULL COMMENT '修改人员',
  `is_del` tinyint(4) DEFAULT 0 COMMENT '0：正常；1：删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='群发任务表';






