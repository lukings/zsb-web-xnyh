# 权限拦截器与数据加密系统

## 概述

本系统实现了基于Spring Boot的权限拦截器和数据加密功能，用于：
1. **权限控制**：拦截API请求，检查用户功能权限
2. **数据加密**：对返回前端的数据进行加密处理

## 系统架构

```
用户请求 → 权限拦截器 → 权限检查 → 数据加密 → 返回前端
```

## 核心组件

### 1. 权限拦截器 (`PermissionInterceptor`)
- 拦截所有API请求
- 检查用户是否有访问权限
- 无权限时返回403状态码

### 2. 权限服务 (`SimplePermissionService`)
- 从JWT用户信息中获取菜单权限
- 检查请求路径是否在用户权限范围内
- 支持递归检查嵌套菜单结构

### 3. 数据加密处理器 (`DataEncryptAdvice`)
- 拦截所有返回数据
- 对数据进行加密处理
- 支持多种加密算法

### 4. 加密工具 (`EncryptUtil`)
- 提供AES、RSA、Base64、XOR等加密方式
- 可配置密钥和算法参数

## 配置说明

### 配置文件位置
```
src/main/resources/application-permission.yml
```

### 权限拦截器配置
```yaml
permission:
  interceptor:
    enabled: true  # 是否启用权限拦截器
  check:
    enabled: true  # 是否启用权限检查
    cache-enabled: true  # 是否启用缓存
    cache-expire: 300  # 缓存过期时间（秒）
```

### 数据加密配置
```yaml
encrypt:
  enabled: false  # 是否启用数据加密（默认禁用）
  algorithm: aes  # 加密算法：aes, rsa, base64, xor
  aes:
    key: your-secret-key  # AES密钥
    use-custom-key: false
```

## 使用方法

### 1. 启用权限检查
在需要权限检查的Controller或方法上添加注解：
```java
@RestController
@RequiresPermission("用户管理")  // 类级别权限
public class UserController {
    
    @GetMapping("/users")
    @RequiresPermission("查询用户")  // 方法级别权限
    public ResultBody getUserList() {
        // 业务逻辑
    }
}
```

### 2. 启用数据加密
在配置文件中启用加密：
```yaml
encrypt:
  enabled: true
  algorithm: aes
  aes:
    key: your-secret-key
```

### 3. 前端解密
前端需要使用相应的解密逻辑处理加密后的数据。

## 故障排除

### 常见问题

#### 1. `getOutputStream() has already been called for this response`
**原因**：响应流被多次写入
**解决方案**：
- 检查是否有多个过滤器/拦截器同时写入响应
- 使用配置开关禁用有问题的组件
- 在 `application-permission.yml` 中设置：
  ```yaml
  permission:
    interceptor:
      enabled: false  # 临时禁用权限拦截器
  encrypt:
    enabled: false   # 临时禁用数据加密
  ```

#### 2. 权限检查失败
**原因**：用户菜单信息不完整或路径不匹配
**解决方案**：
- 检查JWT用户信息中的菜单结构
- 确认接口路径与菜单中的path/component字段匹配
- 查看日志中的权限检查详情

#### 3. 数据加密失败
**原因**：加密配置错误或密钥不匹配
**解决方案**：
- 检查加密配置是否正确
- 确认密钥与前端解密密钥一致
- 临时禁用加密功能进行测试

### 调试模式

启用详细日志：
```yaml
logging:
  level:
    cn.visolink.common.permission: DEBUG
    cn.visolink.common.security: DEBUG
```

### 临时禁用功能

如果遇到问题，可以临时禁用相关功能：

```yaml
# 禁用权限拦截器
permission:
  interceptor:
    enabled: false

# 禁用数据加密
encrypt:
  enabled: false
```

## 安全注意事项

1. **密钥管理**：不要在代码中硬编码加密密钥
2. **权限验证**：确保权限检查逻辑的完整性
3. **错误处理**：避免在错误响应中泄露敏感信息
4. **日志安全**：不要在日志中记录敏感数据

## 性能优化

1. **缓存策略**：启用权限缓存减少数据库查询
2. **异步处理**：对于复杂权限检查考虑异步处理
3. **批量检查**：批量检查多个权限减少网络开销

## 扩展功能

### 自定义权限检查
可以实现自定义的权限检查逻辑：
```java
@Service
public class CustomPermissionService {
    public boolean hasCustomPermission(String resource, String action) {
        // 自定义权限检查逻辑
    }
}
```

### 自定义加密算法
可以实现自定义的加密算法：
```java
public class CustomEncryptUtil {
    public static String customEncrypt(String data) {
        // 自定义加密逻辑
    }
}
```

## 联系支持

如果遇到问题，请：
1. 查看系统日志
2. 检查配置文件
3. 临时禁用相关功能进行测试
4. 提供详细的错误信息和日志
