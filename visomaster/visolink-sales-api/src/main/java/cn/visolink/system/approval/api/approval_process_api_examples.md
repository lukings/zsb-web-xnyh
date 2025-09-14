# 审批流程配置系统接口使用示例

## 1. 审批流程配置管理

### 1.1 保存审批流程配置
**接口地址：** `POST /approval/process/save`

**请求示例：**
```json
{
  "processName": "客户信息变更审批",
  "processType": "2",
  "levelType": "3",
  "regionId": "region123",
  "projectId": "project123",
  "isEnabled": 1,
  "isForce": 0,
  "nodes": [
    {
      "nodeName": "部门经理审批",
      "nodeType": "1",
      "approvalJobList": ["部门经理", "副经理"],
      "ccJobList": ["部门主管"],
      "timeoutDays": 3,
      "isRequired": 1
    },
    {
      "nodeName": "总经理审批",
      "nodeType": "1",
      "approvalJobList": ["总经理"],
      "ccJobList": ["副总经理"],
      "timeoutDays": 5,
      "isRequired": 1
    },
    {
      "nodeName": "抄送财务",
      "nodeType": "2",
      "ccJobList": ["财务经理", "财务主管"],
      "timeoutDays": 0,
      "isRequired": 0
    }
  ]
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "保存成功",
  "data": null
}
```

### 1.2 更新审批流程配置
**接口地址：** `POST /approval/process/update`

**请求示例：**
```json
{
  "id": "config123",
  "processName": "客户信息变更审批-更新版",
  "processType": "2",
  "levelType": "3",
  "regionId": "region123",
  "projectId": "project123",
  "isEnabled": 1,
  "isForce": 0,
  "nodes": [
    {
      "nodeName": "部门经理审批",
      "nodeType": "1",
      "approvalJobList": ["部门经理"],
      "ccJobList": ["部门主管"],
      "timeoutDays": 3,
      "isRequired": 1
    }
  ]
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

### 1.3 根据ID查询审批流程配置
**接口地址：** `POST /approval/process/getById`

**请求参数：**
```
id=config123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "id": "config123",
    "processName": "客户信息变更审批",
    "processType": "2",
    "levelType": "3",
    "regionId": "region123",
    "projectId": "project123",
    "isEnabled": 1,
    "isForce": 0,
    "createBy": "admin",
    "createTime": "2024-01-01 10:00:00",
    "updateBy": "admin",
    "updateTime": "2024-01-01 10:00:00",
    "isDel": 0,
    "regionName": "华东区域",
    "projectName": "上海项目",
    "processTypeName": "客户信息变更审批",
    "levelTypeName": "项目级",
    "nodes": [
      {
        "id": "node123",
        "processConfigId": "config123",
        "nodeName": "部门经理审批",
        "nodeType": "1",
        "nodeOrder": 1,
        "approvalJobs": "[\"部门经理\",\"副经理\"]",
        "ccJobs": "[\"部门主管\"]",
        "timeoutDays": 3,
        "isRequired": 1,
        "createTime": "2024-01-01 10:00:00",
        "updateTime": "2024-01-01 10:00:00",
        "isDel": 0,
        "approvalJobList": ["部门经理", "副经理"],
        "ccJobList": ["部门主管"],
        "nodeTypeName": "审批节点"
      }
    ]
  }
}
```

### 1.4 查询审批流程配置列表
**接口地址：** `POST /approval/process/select`

**请求示例：**
```json
{
  "processType": "2",
  "levelType": "3",
  "regionId": "region123",
  "projectId": "project123",
  "isEnabled": 1,
  "processName": "客户信息",
  "pageNum": 1,
  "pageSize": 10
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "查询成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "size": 2,
    "total": 2,
    "pages": 1,
    "list": [
      {
        "id": "config123",
        "processName": "客户信息变更审批",
        "processType": "2",
        "levelType": "3",
        "regionId": "region123",
        "projectId": "project123",
        "isEnabled": 1,
        "isForce": 0,
        "createBy": "admin",
        "createTime": "2024-01-01 10:00:00",
        "updateBy": "admin",
        "updateTime": "2024-01-01 10:00:00",
        "isDel": 0,
        "regionName": "华东区域",
        "projectName": "上海项目",
        "processTypeName": "客户信息变更审批",
        "levelTypeName": "项目级"
      },
      {
        "id": "config124",
        "processName": "电话/微信跟进审批",
        "processType": "3",
        "levelType": "3",
        "regionId": "region123",
        "projectId": "project123",
        "isEnabled": 0,
        "isForce": 0,
        "createBy": "admin",
        "createTime": "2024-01-01 11:00:00",
        "updateBy": "admin",
        "updateTime": "2024-01-01 11:00:00",
        "isDel": 0,
        "regionName": "华东区域",
        "projectName": "上海项目",
        "processTypeName": "电话/微信跟进审批",
        "levelTypeName": "项目级"
      }
    ]
  }
}
```

### 1.5 删除审批流程配置
**接口地址：** `POST /approval/process/delete`

**请求参数：**
```
id=config123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

### 1.6 批量删除审批流程配置
**接口地址：** `POST /approval/process/batchDelete`

**请求示例：**
```json
["config123", "config124", "config125"]
```

**响应示例：**
```json
{
  "code": 0,
  "message": "批量删除成功",
  "data": null
}
```

### 1.7 启用/禁用审批流程
**接口地址：** `POST /approval/process/toggleEnabled`

**请求参数：**
```
id=config123&isEnabled=1
```

**响应示例：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

### 1.8 复制审批流程配置
**接口地址：** `POST /approval/process/copy`

**请求参数：**
```
sourceId=config123&targetProcessType=3&targetLevelType=2&targetRegionId=region456
```

**响应示例：**
```json
{
  "code": 0,
  "message": "复制成功",
  "data": null
}
```

### 1.9 清空所有流程配置
**接口地址：** `POST /approval/process/clearAll`

**请求示例：**
```json
{}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "清空成功",
  "data": null
}
```

## 2. 审批流程节点管理

### 2.1 保存审批流程节点
**接口地址：** `POST /approval/process/saveNode`

**请求示例：**
```json
{
  "processConfigId": "config123",
  "nodeName": "部门经理审批",
  "nodeType": "1",
  "nodeOrder": 1,
  "approvalJobList": ["部门经理", "副经理"],
  "ccJobList": ["部门主管"],
  "timeoutDays": 3,
  "isRequired": 1
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "保存成功",
  "data": null
}
```

### 2.2 更新审批流程节点
**接口地址：** `POST /approval/process/updateNode`

**请求示例：**
```json
{
  "id": "node123",
  "processConfigId": "config123",
  "nodeName": "部门经理审批-更新版",
  "nodeType": "1",
  "nodeOrder": 1,
  "approvalJobList": ["部门经理"],
  "ccJobList": ["部门主管"],
  "timeoutDays": 5,
  "isRequired": 1
}
```

**响应示例：**
```json
{
  "code": 0,
  "message": "更新成功",
  "data": null
}
```

### 2.3 删除审批流程节点
**接口地址：** `POST /approval/process/deleteNode`

**请求参数：**
```
id=node123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

### 2.4 根据流程配置ID查询节点列表
**接口地址：** `POST /approval/process/getNodesByConfigId`

**请求参数：**
```
processConfigId=config123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "查询成功",
  "data": [
    {
      "id": "node123",
      "processConfigId": "config123",
      "nodeName": "部门经理审批",
      "nodeType": "1",
      "nodeOrder": 1,
      "approvalJobs": "[\"部门经理\",\"副经理\"]",
      "ccJobs": "[\"部门主管\"]",
      "timeoutDays": 3,
      "isRequired": 1,
      "createTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:00:00",
      "isDel": 0,
      "approvalJobList": ["部门经理", "副经理"],
      "ccJobList": ["部门主管"],
      "nodeTypeName": "审批节点"
    },
    {
      "id": "node124",
      "processConfigId": "config123",
      "nodeName": "总经理审批",
      "nodeType": "1",
      "nodeOrder": 2,
      "approvalJobs": "[\"总经理\"]",
      "ccJobs": "[\"副总经理\"]",
      "timeoutDays": 5,
      "isRequired": 1,
      "createTime": "2024-01-01 10:00:00",
      "updateTime": "2024-01-01 10:00:00",
      "isDel": 0,
      "approvalJobList": ["总经理"],
      "ccJobList": ["副总经理"],
      "nodeTypeName": "审批节点"
    }
  ]
}
```

## 3. 完整业务流程示例

### 3.1 创建集团级审批流程配置
```json
{
  "processName": "相似客户审批",
  "processType": "1",
  "levelType": "1",
  "isEnabled": 1,
  "isForce": 1,
  "nodes": [
    {
      "nodeName": "区域经理审批",
      "nodeType": "1",
      "approvalJobList": ["区域经理"],
      "timeoutDays": 3,
      "isRequired": 1
    },
    {
      "nodeName": "集团总监审批",
      "nodeType": "1",
      "approvalJobList": ["集团总监"],
      "timeoutDays": 5,
      "isRequired": 1
    }
  ]
}
```

### 3.2 创建区域级审批流程配置
```json
{
  "processName": "客户信息变更审批",
  "processType": "2",
  "levelType": "2",
  "regionId": "region123",
  "isEnabled": 1,
  "isForce": 0,
  "nodes": [
    {
      "nodeName": "部门经理审批",
      "nodeType": "1",
      "approvalJobList": ["部门经理"],
      "timeoutDays": 3,
      "isRequired": 1
    }
  ]
}
```

### 3.3 创建项目级审批流程配置
```json
{
  "processName": "电话/微信跟进审批",
  "processType": "3",
  "levelType": "3",
  "regionId": "region123",
  "projectId": "project123",
  "isEnabled": 1,
  "isForce": 0,
  "nodes": [
    {
      "nodeName": "项目主管审批",
      "nodeType": "1",
      "approvalJobList": ["项目主管"],
      "timeoutDays": 2,
      "isRequired": 1
    },
    {
      "nodeName": "抄送财务",
      "nodeType": "2",
      "ccJobList": ["财务经理"],
      "timeoutDays": 0,
      "isRequired": 0
    }
  ]
}
```

## 4. 错误响应示例

### 4.1 参数错误
```json
{
  "code": -10001,
  "message": "流程名称不能为空",
  "data": null
}
```

### 4.2 重复配置错误
```json
{
  "code": -10004,
  "message": "该流程配置已存在",
  "data": null
}
```

### 4.3 系统错误
```json
{
  "code": -10000,
  "message": "保存失败：数据库连接异常",
  "data": null
}
```

## 5. 字段说明

### 5.1 流程类型 (processType)
- `1` - 相似客户审批
- `2` - 客户信息变更审批
- `3` - 电话/微信跟进审批
- `4` - 电话/微信且满足三个一的跟进审批
- `5` - 上门拜访跟进审批
- `6` - 上门拜访且满足三个一的跟进审批
- `7` - 邀约到访跟进审批
- `8` - 邀约到访且满足三个一的跟进审批
- `9` - 自然来访跟进审批
- `10` - 自然来访且满足三个一的跟进审批
- `11` - 其他跟进审批
- `12` - 其他跟进且满足三个一的审批

### 5.2 级别类型 (levelType)
- `1` - 集团级
- `2` - 区域级
- `3` - 项目级

### 5.3 节点类型 (nodeType)
- `1` - 审批节点
- `2` - 抄送节点

### 5.4 启用状态 (isEnabled)
- `0` - 禁用
- `1` - 启用

### 5.5 强制状态 (isForce)
- `0` - 否
- `1` - 是（集团级专用）
