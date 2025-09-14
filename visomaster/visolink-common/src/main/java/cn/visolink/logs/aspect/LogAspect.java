package cn.visolink.logs.aspect;

import cn.visolink.logs.domain.Log;
import cn.visolink.logs.service.LogService;
import cn.visolink.utils.RequestHolder;
import cn.visolink.utils.SecurityUtils;
import cn.visolink.utils.StringUtils;
import cn.visolink.utils.ThrowableUtil;
import java.util.Date;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author WCL
 * @date 2018-11-24
 */
@Component
@Aspect
@Slf4j
public class LogAspect {

    @Autowired
    private LogService logService;

    private long currentTime = 0L;

    @Value("${param.interface.limit}")
    private Integer limit;
    @Value("${param.interface.redisKey}")
    private String redisKey;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 配置切入点
     */
    @Pointcut("@annotation(cn.visolink.logs.aop.log.Log)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        currentTime = System.currentTimeMillis();
        //校验接口访问次数
        String username = getUsername();
        if (StringUtils.isNotBlank(username)) {
            checkLimit(username);
        }
        result = joinPoint.proceed();
        Log log = new Log("INFO",System.currentTimeMillis() - currentTime);
        logService.save(getUsername(), StringUtils.getIp(RequestHolder.getHttpServletRequest()),joinPoint, log);
        return result;
    }

    /**
     * 校验接口访问次数
     */
    private void checkLimit(String username) {
        Object object = redisTemplate.opsForValue().get("param.interface.limit");
        if (object != null) {
            limit = (Integer) object;
        }
        //限制用户访问数量，默认200
        String key = redisKey + ":" + username;
        String date = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
        Object value = redisTemplate.opsForHash().get(key, date);
        if (value == null) {
            value = 0;
        } else {
            if (Integer.parseInt(value.toString()) >= limit) {
                throw new RuntimeException("当日访问接口次数超过【" + limit + "次】限制，请明日再试，如有特殊需求，可联系管理员");
            } else {
                value = Integer.parseInt(value.toString()) + 1;
            }
        }
        redisTemplate.opsForHash().put(key, date, value);
    }
    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR",System.currentTimeMillis() - currentTime);
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        logService.save(getUsername(), StringUtils.getIp(RequestHolder.getHttpServletRequest()), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {
        try {
            return SecurityUtils.getUsername();
        }catch (Exception e){
            return "";
        }
    }
}
