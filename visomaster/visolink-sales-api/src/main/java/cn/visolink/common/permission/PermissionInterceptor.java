package cn.visolink.common.permission;

import cn.visolink.exception.ResultBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 权限拦截器
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {

	@Autowired
	private SimplePermissionService permissionService;

	@Autowired
	private ObjectMapper objectMapper;

	// 配置开关，允许禁用权限拦截器
	@Value("${permission.interceptor.enabled}")
	private boolean interceptorEnabled;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 如果禁用了拦截器，直接放行
		if (!interceptorEnabled) {
			return true;
		}
		
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		HandlerMethod handlerMethod = (HandlerMethod) handler;
		RequiresPermission methodPermission = handlerMethod.getMethodAnnotation(RequiresPermission.class);
		RequiresPermission classPermission = handlerMethod.getBeanType().getAnnotation(RequiresPermission.class);
		if (methodPermission == null && classPermission == null) {
			return true;
		}

		String requestPath = getRequestPath(request);

		boolean allowed = permissionService.hasPermission(request, requestPath);
		if (!allowed) {
			// 使用更安全的方式处理权限拒绝
			handleForbiddenSafely(response, "您没有权限访问该接口");
			return false;
		}
		return true;
	}

	private String getRequestPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String requestURI = request.getRequestURI();
		if (StringUtils.hasText(contextPath) && requestURI.startsWith(contextPath)) {
			return requestURI.substring(contextPath.length());
		}
		return requestURI;
	}

	/**
	 * 安全地处理权限拒绝，避免响应流冲突
	 */
	private void handleForbiddenSafely(HttpServletResponse response, String message) {
		try {
			// 检查响应是否已经被提交
			if (response.isCommitted()) {
				return;
			}
			
			// 设置响应状态和内容类型
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json;charset=UTF-8");
			
			// 创建错误响应体
			ResultBody resultBody = ResultBody.error(403, message);
			String jsonResponse = objectMapper.writeValueAsString(resultBody);
			
			// 尝试写入响应，如果失败则只设置状态码
			try {
				response.getWriter().write(jsonResponse);
			} catch (Exception e) {
				// 如果写入失败，只设置状态码，不抛出异常
				// 这样可以避免影响其他过滤器的正常工作
			}
			
		} catch (Exception e) {
			// 如果出现任何异常，只设置状态码，确保不破坏响应流
			try {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			} catch (Exception ignored) {
				// 忽略所有异常，确保拦截器不会破坏系统
			}
		}
	}

	/**
	 * 旧的权限拒绝处理方法（已废弃）
	 */
	@Deprecated
	private void handleForbidden(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json;charset=UTF-8");
		ResultBody resultBody = ResultBody.error(403, message);
		writeResponse(response, resultBody);
	}

	/**
	 * 旧的响应写入方法（已废弃）
	 */
	@Deprecated
	private void writeResponse(HttpServletResponse response, ResultBody resultBody) throws IOException {
		try (PrintWriter writer = response.getWriter()) {
			writer.write(objectMapper.writeValueAsString(resultBody));
			writer.flush();
		}
	}
}
