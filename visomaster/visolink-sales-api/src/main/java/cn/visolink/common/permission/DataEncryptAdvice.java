package cn.visolink.common.permission;

import cn.visolink.exception.ResultBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * 数据加密处理器
 * 对所有返回前端的数据进行加密处理
 * 使用@Order注解确保优先级
 */
@RestControllerAdvice
@Order(1) // 确保数据加密拦截器优先执行
public class DataEncryptAdvice implements ResponseBodyAdvice<Object> {

	private static final Logger logger = LoggerFactory.getLogger(DataEncryptAdvice.class);

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EncryptConfig encryptConfig;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 增加更详细的日志记录
        String methodName = returnType.getMethod() != null ? returnType.getMethod().getName() : "unknown";
        String className = returnType.getContainingClass() != null ? returnType.getContainingClass().getSimpleName() : "unknown";
        String fullClassName = returnType.getContainingClass() != null ? returnType.getContainingClass().getName() : "unknown";
        String returnTypeName = returnType.getParameterType().getSimpleName();
        
        logger.info("数据加密拦截器检查 - 类: {}.{}, 返回类型: {}, 加密启用状态: {}", 
            className, methodName, returnTypeName, encryptConfig.isEnabled());
        
        // 如果禁用了加密，直接返回false
        if (!encryptConfig.isEnabled()) {
            logger.info("数据加密已禁用，跳过加密处理 - 类: {}.{}", className, methodName);
            return false;
        }
        
        // 检查是否在白名单类中
        if (isInWhitelistClass(fullClassName)) {
            logger.info("类在白名单中，跳过加密处理 - 类: {}.{}", className, methodName);
            return false;
        }
        
        // 检查是否在白名单方法中
        if (isInWhitelistMethod(fullClassName, methodName)) {
            logger.info("方法在白名单中，跳过加密处理 - 类: {}.{}", className, methodName);
            return false;
        }
        
        // 对所有返回数据进行加密
        logger.info("数据加密已启用，将进行加密处理 - 类: {}.{}", className, methodName);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        
        String methodName = returnType.getMethod() != null ? returnType.getMethod().getName() : "unknown";
        String className = returnType.getContainingClass() != null ? returnType.getContainingClass().getSimpleName() : "unknown";
        
        logger.info("开始数据加密处理 - 类: {}.{}, 原始数据类型: {}, 内容类型: {}", 
            className, methodName, body != null ? body.getClass().getSimpleName() : "null", selectedContentType);
        
        try {
            // 如果body为null，直接返回
            if (body == null) {
                logger.info("响应体为null，跳过加密 - 类: {}.{}", className, methodName);
                return null;
            }
            
            // 检查是否是错误响应，如果是则不加密
            if (isErrorResponse(body)) {
                logger.info("检测到错误响应，跳过加密 - 类: {}.{}", className, methodName);
                return body;
            }
            
            // 如果返回的是ResultBody，直接加密
            if (body instanceof ResultBody) {
                logger.info("检测到ResultBody，直接加密 - 类: {}.{}", className, methodName);
                Object result = encryptResultBody((ResultBody) body);
                logger.info("加密完成 - 类: {}.{}, 加密后数据类型: {}", className, methodName, 
                    result != null ? result.getClass().getSimpleName() : "null");
                return result;
            }
            
            // 如果是其他类型，包装成ResultBody再加密
            logger.info("检测到其他类型数据，包装后加密 - 类: {}.{}", className, methodName);
            ResultBody resultBody = ResultBody.success(body);
            Object result = encryptResultBody(resultBody);
            logger.info("包装加密完成 - 类: {}.{}, 加密后数据类型: {}", className, methodName, 
                result != null ? result.getClass().getSimpleName() : "null");
            return result;
            
        } catch (Exception e) {
            logger.error("数据加密处理失败 - 类: {}.{}", className, methodName, e);
            // 加密失败时返回原始数据，避免影响系统正常运行
            return body;
        }
    }

    /**
     * 检查是否是错误响应
     */
    private boolean isErrorResponse(Object body) {
        if (body instanceof ResultBody) {
            ResultBody resultBody = (ResultBody) body;
            // 如果状态码不是200，说明是错误响应，不加密
            // 包括权限不足(403)、未授权(401)、参数错误(400)等
            boolean isError = resultBody.getCode() != 200;
            if (isError) {
                logger.debug("检测到错误响应，状态码: {}", resultBody.getCode());
            }
            return isError;
        }
        
        // 如果是字符串且包含错误信息，也不加密
        if (body instanceof String) {
            String bodyStr = (String) body;
            boolean isError = bodyStr.contains("error") || bodyStr.contains("Error") || 
                   bodyStr.contains("exception") || bodyStr.contains("Exception");
            if (isError) {
                logger.debug("检测到错误字符串响应: {}", bodyStr);
            }
            return isError;
        }
        
        return false;
    }

    /**
     * 加密ResultBody数据
     */
    private Object encryptResultBody(ResultBody resultBody) {
        try {
            // 获取原始数据
            Object originalData = resultBody.getData();
            
            if (originalData != null) {
                // 将数据转换为JSON字符串
                String jsonData = objectMapper.writeValueAsString(originalData);
                logger.debug("原始数据JSON长度: {}", jsonData.length());
                
                // 对JSON数据进行加密
                String encryptedData = encryptData(jsonData);
                logger.debug("加密后数据长度: {}", encryptedData.length());
                
                // 创建新的ResultBody，包含加密后的数据
                ResultBody encryptedResult = new ResultBody();
                encryptedResult.setCode(resultBody.getCode());
                encryptedResult.setMessages(resultBody.getMessages());
                encryptedResult.setData(encryptedData);
                
                return encryptedResult;
            }
            
            return resultBody;
            
        } catch (Exception e) {
            logger.error("加密ResultBody数据失败", e);
            // 加密失败时返回原始数据
            return resultBody;
        }
    }

    /**
     * 加密数据
     * 使用EncryptUtil提供的加密方法
     */
    private String encryptData(String data) {
        try {
            // 选择你想要的加密方式：
            
            // 方式1：AES加密（推荐，性能好，安全性高）
            String encrypted = EncryptUtil.aesEncrypt(data);
            logger.debug("AES加密成功，数据长度: {} -> {}", data.length(), encrypted.length());
            return encrypted;
            
            // 方式2：RSA加密（安全性最高，但性能较差）
            // return EncryptUtil.rsaEncrypt(data);
            
            // 方式3：Base64编码（仅编码，不加密）
            // return EncryptUtil.base64Encode(data);
            
            // 方式4：异或加密（简单但安全性较低）
            // return EncryptUtil.xorEncrypt(data, "your-key");
            
        } catch (Exception e) {
            logger.error("数据加密失败，使用原始数据", e);
            return data;
        }
    }
    
    /**
     * 检查类是否在白名单中
     */
    private boolean isInWhitelistClass(String fullClassName) {
        List<String> whitelistClasses = encryptConfig.getExclude().getWhitelist().getClasses();
        if (whitelistClasses == null || whitelistClasses.isEmpty()) {
            return false;
        }
        
        for (String whitelistClass : whitelistClasses) {
            if (fullClassName.equals(whitelistClass)) {
                logger.debug("类在白名单中: {}", fullClassName);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 检查方法是否在白名单中
     */
    private boolean isInWhitelistMethod(String fullClassName, String methodName) {
        List<String> whitelistMethods = encryptConfig.getExclude().getWhitelist().getMethods();
        if (whitelistMethods == null || whitelistMethods.isEmpty()) {
            return false;
        }
        
        String methodSignature = fullClassName + "#" + methodName;
        
        for (String whitelistMethod : whitelistMethods) {
            if (methodSignature.equals(whitelistMethod)) {
                logger.debug("方法在白名单中: {}", methodSignature);
                return true;
            }
        }
        
        return false;
    }
}

/**
 * 加密配置类
 */
@Component
@ConfigurationProperties(prefix = "encrypt")
class EncryptConfig {
    private boolean enabled;
    private String algorithm;
    private AesConfig aes;
    private ExcludeConfig exclude;
    
    // getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public AesConfig getAes() { return aes; }
    public void setAes(AesConfig aes) { this.aes = aes; }
    
    public ExcludeConfig getExclude() { return exclude; }
    public void setExclude(ExcludeConfig exclude) { this.exclude = exclude; }
    
    public static class AesConfig {
        private String key;
        private boolean useCustomKey;
        
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        
        public boolean isUseCustomKey() { return useCustomKey; }
        public void setUseCustomKey(boolean useCustomKey) { this.useCustomKey = useCustomKey; }
    }
    
    public static class ExcludeConfig {
        private List<String> paths;
        private WhitelistConfig whitelist;
        
        public List<String> getPaths() { return paths; }
        public void setPaths(List<String> paths) { this.paths = paths; }
        
        public WhitelistConfig getWhitelist() { return whitelist; }
        public void setWhitelist(WhitelistConfig whitelist) { this.whitelist = whitelist; }
    }
    
    public static class WhitelistConfig {
        private List<String> classes;
        private List<String> methods;
        
        public List<String> getClasses() { return classes; }
        public void setClasses(List<String> classes) { this.classes = classes; }
        
        public List<String> getMethods() { return methods; }
        public void setMethods(List<String> methods) { this.methods = methods; }
    }
}
