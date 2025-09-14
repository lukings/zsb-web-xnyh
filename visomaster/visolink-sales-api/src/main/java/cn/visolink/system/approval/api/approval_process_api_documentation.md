# 审批流程配置系统API文档

## 概述

审批流程配置系统提供了完整的审批流程管理功能，支持集团级、区域级、项目级的多级审批配置，以及审批节点的灵活配置。

## 基础信息

- **基础路径：** `/approval/process`
- **请求格式：** JSON
- **响应格式：** JSON
- **字符编码：** UTF-8

## 通用响应格式

```json
{
  "code": 0,           // 响应码，0表示成功，非0表示失败
  "message": "成功",    // 响应消息
  "data": null         // 响应数据
}
```

## 接口列表

### 1. 审批流程配置管理

#### 1.1 保存审批流程配置

**接口地址：** `POST /approval/process/save`

**功能描述：** 创建新的审批流程配置

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| processName | String | 是 | 流程名称 |
| processType | String | 是 | 流程类型（1-12） |
| levelType | String | 是 | 级别类型（1-3） |
| regionId | String | 否 | 区域ID（区域级和项目级必填） |
| projectId | String | 否 | 项目ID（项目级必填） |
| isEnabled | Integer | 否 | 是否启用（0-禁用，1-启用） |
| isForce | Integer | 否 | 是否强制（0-否，1-是） |
| nodes | Array | 否 | 节点列表 |

**节点参数说明：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| nodeName | String | 是 | 节点名称 |
| nodeType | String | 是 | 节点类型（1-审批节点，2-抄送节点） |
| approvalJobList | Array | 否 | 审批岗位列表 |
| ccJobList | Array | 否 | 抄送岗位列表 |
| timeoutDays | Integer | 否 | 超时天数（0-不超时） |
| isRequired | Integer | 否 | 是否必填（0-否，1-是） |

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

#### 1.2 更新审批流程配置

**接口地址：** `POST /approval/process/update`

**功能描述：** 更新现有的审批流程配置

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 配置ID |
| processName | String | 是 | 流程名称 |
| processType | String | 是 | 流程类型 |
| levelType | String | 是 | 级别类型 |
| regionId | String | 否 | 区域ID |
| projectId | String | 否 | 项目ID |
| isEnabled | Integer | 否 | 是否启用 |
| isForce | Integer | 否 | 是否强制 |
| nodes | Array | 否 | 节点列表 |

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

#### 1.3 根据ID查询审批流程配置

**接口地址：** `POST /approval/process/getById`

**功能描述：** 根据ID查询审批流程配置详情

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 配置ID |

**请求示例：**
```
POST /approval/process/getById?id=config123
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

#### 1.4 查询审批流程配置列表

**接口地址：** `POST /approval/process/select`

**功能描述：** 分页查询审批流程配置列表

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| processType | String | 否 | 流程类型 |
| levelType | String | 否 | 级别类型 |
| regionId | String | 否 | 区域ID |
| projectId | String | 否 | 项目ID |
| isEnabled | Integer | 否 | 是否启用 |
| processName | String | 否 | 流程名称（模糊查询） |
| pageNum | Integer | 否 | 页码（默认1） |
| pageSize | Integer | 否 | 每页大小（默认10） |

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
      }
    ]
  }
}
```

#### 1.5 删除审批流程配置

**接口地址：** `POST /approval/process/delete`

**功能描述：** 删除指定的审批流程配置

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 配置ID |

**请求示例：**
```
POST /approval/process/delete?id=config123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

#### 1.6 批量删除审批流程配置

**接口地址：** `POST /approval/process/batchDelete`

**功能描述：** 批量删除审批流程配置

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| - | Array | 是 | 配置ID列表 |

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

#### 1.7 启用/禁用审批流程

**接口地址：** `POST /approval/process/toggleEnabled`

**功能描述：** 启用或禁用审批流程

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 配置ID |
| isEnabled | Integer | 是 | 启用状态（0-禁用，1-启用） |

**请求示例：**
```
POST /approval/process/toggleEnabled?id=config123&isEnabled=1
```

**响应示例：**
```json
{
  "code": 0,
  "message": "操作成功",
  "data": null
}
```

#### 1.8 复制审批流程配置

**接口地址：** `POST /approval/process/copy`

**功能描述：** 复制现有的审批流程配置到新的流程类型或级别

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sourceId | String | 是 | 源配置ID |
| targetProcessType | String | 是 | 目标流程类型 |
| targetLevelType | String | 是 | 目标级别类型 |
| targetRegionId | String | 否 | 目标区域ID |
| targetProjectId | String | 否 | 目标项目ID |

**请求示例：**
```
POST /approval/process/copy?sourceId=config123&targetProcessType=3&targetLevelType=2&targetRegionId=region456
```

**响应示例：**
```json
{
  "code": 0,
  "message": "复制成功",
  "data": null
}
```

#### 1.9 清空所有流程配置

**接口地址：** `POST /approval/process/clearAll`

**功能描述：** 清空所有审批流程配置（需要二次确认）

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

### 2. 审批流程节点管理

#### 2.1 保存审批流程节点

**接口地址：** `POST /approval/process/saveNode`

**功能描述：** 创建新的审批流程节点

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| processConfigId | String | 是 | 流程配置ID |
| nodeName | String | 是 | 节点名称 |
| nodeType | String | 是 | 节点类型（1-审批节点，2-抄送节点） |
| nodeOrder | Integer | 否 | 节点顺序 |
| approvalJobList | Array | 否 | 审批岗位列表 |
| ccJobList | Array | 否 | 抄送岗位列表 |
| timeoutDays | Integer | 否 | 超时天数 |
| isRequired | Integer | 否 | 是否必填 |

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

#### 2.2 更新审批流程节点

**接口地址：** `POST /approval/process/updateNode`

**功能描述：** 更新现有的审批流程节点

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 节点ID |
| processConfigId | String | 是 | 流程配置ID |
| nodeName | String | 是 | 节点名称 |
| nodeType | String | 是 | 节点类型 |
| nodeOrder | Integer | 否 | 节点顺序 |
| approvalJobList | Array | 否 | 审批岗位列表 |
| ccJobList | Array | 否 | 抄送岗位列表 |
| timeoutDays | Integer | 否 | 超时天数 |
| isRequired | Integer | 否 | 是否必填 |

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

#### 2.3 删除审批流程节点

**接口地址：** `POST /approval/process/deleteNode`

**功能描述：** 删除指定的审批流程节点

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | String | 是 | 节点ID |

**请求示例：**
```
POST /approval/process/deleteNode?id=node123
```

**响应示例：**
```json
{
  "code": 0,
  "message": "删除成功",
  "data": null
}
```

#### 2.4 根据流程配置ID查询节点列表

**接口地址：** `POST /approval/process/getNodesByConfigId`

**功能描述：** 根据流程配置ID查询所有节点

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| processConfigId | String | 是 | 流程配置ID |

**请求示例：**
```
POST /approval/process/getNodesByConfigId?processConfigId=config123
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
    }
  ]
}
```

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| -10000 | 系统错误 |
| -10001 | 参数错误 |
| -10002 | 流程类型不能为空 |
| -10003 | 级别类型不能为空 |
| -10004 | 该流程配置已存在 |

## 数据字典

### 流程类型 (processType)

| 值 | 说明 |
|----|------|
| 1 | 相似客户审批 |
| 2 | 客户信息变更审批 |
| 3 | 电话/微信跟进审批 |
| 4 | 电话/微信且满足三个一的跟进审批 |
| 5 | 上门拜访跟进审批 |
| 6 | 上门拜访且满足三个一的跟进审批 |
| 7 | 邀约到访跟进审批 |
| 8 | 邀约到访且满足三个一的跟进审批 |
| 9 | 自然来访跟进审批 |
| 10 | 自然来访且满足三个一的跟进审批 |
| 11 | 其他跟进审批 |
| 12 | 其他跟进且满足三个一的审批 |

### 级别类型 (levelType)

| 值 | 说明 |
|----|------|
| 1 | 集团级 |
| 2 | 区域级 |
| 3 | 项目级 |

### 节点类型 (nodeType)

| 值 | 说明 |
|----|------|
| 1 | 审批节点 |
| 2 | 抄送节点 |

### 启用状态 (isEnabled)

| 值 | 说明 |
|----|------|
| 0 | 禁用 |
| 1 | 启用 |

### 强制状态 (isForce)

| 值 | 说明 |
|----|------|
| 0 | 否 |
| 1 | 是（集团级专用） |

## 注意事项

1. **权限控制：** 所有接口都需要进行权限验证
2. **数据校验：** 所有输入参数都会进行格式和业务规则校验
3. **事务管理：** 涉及多表操作的接口都使用了事务管理
4. **日志记录：** 所有操作都会记录操作日志
5. **软删除：** 删除操作采用软删除方式，不会物理删除数据
6. **分页查询：** 列表查询接口都支持分页功能
7. **错误处理：** 所有接口都有完善的错误处理机制
