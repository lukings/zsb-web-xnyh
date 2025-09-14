package cn.visolink.common.permission;

import cn.visolink.common.security.security.JwtUser;
import cn.visolink.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 简化的权限检查服务
 */
@Service
public class SimplePermissionService {

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 单一入口：根据当前请求直接判断是否有权限
	 */
	public boolean hasPermission(HttpServletRequest request, String requestPath) {
		try {
			HttpServletRequest req = request != null ? request
					: ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

			String userName = req.getHeader("username");
			if (StringUtils.isEmpty(userName)) {
				userName = SecurityUtils.getUsername();
			}

			// 检查用户名是否为空
			if (StringUtils.isEmpty(userName)) {
				return false;
			}

			JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);
			if (jwtUser == null || jwtUser.getMenus() == null) {
				return false;
			}

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> featuresMenus = (List<Map<String, Object>>) jwtUser.getMenus().get("featuresMenus");
			return matchPathInMenus(featuresMenus, requestPath);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 递归检查路径是否出现在菜单树中（支持 path/component 两种字段）
	 */
	private boolean matchPathInMenus(List<Map<String, Object>> menus, String requestPath) {
		if (menus == null || menus.isEmpty() || !StringUtils.hasText(requestPath)) {
			return false;
		}
		for (Map<String, Object> menu : menus) {
			Object path = menu.get("path");
			if (path != null && requestPath.equals(path.toString())) {
				return true;
			}
			Object component = menu.get("component");
			if (component != null && requestPath.equals(component.toString())) {
				return true;
			}
			Object apiAddress = menu.get("ApiAddress");
			if (apiAddress != null && requestPath.equals(apiAddress.toString())) {
				return true;
			}
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> children = (List<Map<String, Object>>) menu.get("children");
			if (matchPathInMenus(children, requestPath)) {
				return true;
			}
		}
		return false;
	}
}
