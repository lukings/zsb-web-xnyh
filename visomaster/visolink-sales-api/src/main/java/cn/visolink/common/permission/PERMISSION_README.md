# 权限拦截器使用说明

## 概述

这是一个基于Spring Boot的权限拦截器，用于拦截所有接口请求并检查用户是否有访问权限。权限检查基于用户的功能菜单配置，通过Redis缓存提高性能。

## 功能特性

- 拦截所有接口请求
- 基于用户功能菜单进行权限检查
- 支持Redis缓存，提高性能
- 支持方法级和类级权限控制
- 自动处理未授权和禁止访问的情况
- 可配置的路径排除规则

## 核心组件

### 1. 权限注解 (@RequiresPermission)

用于标记需要权限检查的接口：

```java
@RequiresPermission(value = "user:view", description = "查看用户信息")
@GetMapping("/user/{id}")
public ResultBody getUserInfo(@PathVariable String id) {
    // 业务逻辑
}
```

### 2. 权限拦截器 (PermissionInterceptor)

拦截所有请求，检查用户权限：

- 检查请求路径是否在用户功能菜单中
- 自动处理权限不足的情况
- 返回标准的错误响应

### 3. 权限检查服务 (PermissionService)

负责具体的权限检查逻辑：

- 从Redis获取用户权限缓存
- 缓存未命中时从数据库加载
- 检查请求路径是否匹配用户权限

### 4. 用户菜单服务 (UserMenuService)

管理用户功能菜单：

- 从数据库加载用户权限
- 管理Redis缓存
- 支持权限刷新

## 配置说明

### 1. 注册拦截器

在 `WebMvcConfig` 中注册权限拦截器：

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private PermissionInterceptor permissionInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(    // 排除不需要权限检查的路径
                        "/login",
                        "/logout",
                        "/error",
                        "/swagger-ui/**"
                );
    }
}
```

### 2. 排除路径配置

可以配置不需要权限检查的路径：

- 登录相关接口
- 错误页面
- 静态资源
- API文档
- 其他公开接口

## 使用方法

### 1. 基本使用

在Controller方法上添加权限注解：

```java
@RequiresPermission(value = "user:view")
@GetMapping("/user/{id}")
public ResultBody getUserInfo(@PathVariable String id) {
    return ResultBody.success("用户信息");
}
```

### 2. 类级权限控制

在整个Controller类上添加权限注解：

```java
@RequiresPermission(value = "admin")
@RestController
@RequestMapping("/admin")
public class AdminController {
    // 所有方法都需要admin权限
}
```

### 3. 组合使用

方法级注解会覆盖类级注解：

```java
@RequiresPermission(value = "admin")  // 类级权限
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @RequiresPermission(value = "user:view")  // 方法级权限，覆盖类级权限
    @GetMapping("/user/{id}")
    public ResultBody getUserInfo(@PathVariable String id) {
        return ResultBody.success("用户信息");
    }
}
```

## 数据库配置

### 1. 功能菜单表结构

需要创建功能菜单表，包含以下字段：

```sql
CREATE TABLE sys_menu (
    id VARCHAR(32) PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL COMMENT '菜单名称',
    api_path VARCHAR(200) COMMENT '接口路径',
    permission_code VARCHAR(100) COMMENT '权限标识',
    parent_id VARCHAR(32) COMMENT '父菜单ID',
    company_code VARCHAR(50) COMMENT '公司代码',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用'
);
```

### 2. 用户菜单关联表

```sql
CREATE TABLE sys_user_menu (
    id VARCHAR(32) PRIMARY KEY,
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    menu_id VARCHAR(32) NOT NULL COMMENT '菜单ID',
    company_code VARCHAR(50) COMMENT '公司代码'
);
```

## 自定义配置

### 1. 修改权限检查逻辑

在 `PermissionService` 中修改权限检查逻辑：

```java
@Service
public class PermissionService {
    
    public boolean hasPermission(String userId, String companyCode, String requestPath) {
        // 自定义权限检查逻辑
        return customPermissionCheck(userId, companyCode, requestPath);
    }
}
```

### 2. 修改缓存策略

在 `UserMenuService` 中修改缓存策略：

```java
@Service
public class UserMenuService {
    
    public void cacheUserMenus(String userId, String companyCode, List<Map<String, Object>> menus) {
        // 自定义缓存逻辑
        String redisKey = generateCustomKey(userId, companyCode);
        redisUtil.setEx(redisKey, menus, customExpireTime, TimeUnit.MINUTES);
    }
}
```

## 注意事项

1. **性能考虑**：权限检查会拦截所有请求，建议合理使用Redis缓存
2. **异常处理**：权限检查失败时会抛出异常，确保有合适的异常处理机制
3. **日志记录**：建议在权限检查失败时记录详细日志，便于问题排查
4. **测试覆盖**：确保权限拦截器的各种场景都有测试覆盖

## 常见问题

### 1. 权限检查失败

- 检查用户是否已登录
- 检查用户功能菜单是否正确配置
- 检查Redis缓存是否正常

### 2. 性能问题

- 检查Redis缓存命中率
- 优化数据库查询
- 考虑使用本地缓存

### 3. 权限配置问题

- 检查菜单表数据是否正确
- 检查用户菜单关联是否正确
- 检查权限标识是否匹配

## 扩展功能

### 1. 动态权限配置

支持运行时修改权限配置，无需重启应用。

### 2. 权限审计

记录权限检查的详细日志，支持权限使用分析。

### 3. 多租户支持

支持不同租户的权限隔离和配置。

### 4. 权限继承

支持基于角色的权限继承机制。
