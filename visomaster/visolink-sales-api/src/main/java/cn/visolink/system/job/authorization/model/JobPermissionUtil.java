package cn.visolink.system.job.authorization.model;

import java.util.HashSet;
import java.util.Set;

/**
 * @author 亮
 * @Description: 外呼权限枚举值
 * @date 2025/8/28 15:40
 */
public class JobPermissionUtil {
    // 定义二级角色对应的岗位编码
    private static final Set<String> SECONDARY_ROLE_JOBS = new HashSet<>();

    static {
        SECONDARY_ROLE_JOBS.add("qyfz");
        SECONDARY_ROLE_JOBS.add("yxjl");
        SECONDARY_ROLE_JOBS.add("zszj");
        SECONDARY_ROLE_JOBS.add("qyyxjl");
        SECONDARY_ROLE_JOBS.add("qyxsjl");
        SECONDARY_ROLE_JOBS.add("xsjl");
        SECONDARY_ROLE_JOBS.add("qyzszj");
        SECONDARY_ROLE_JOBS.add("xmz");
        SECONDARY_ROLE_JOBS.add("qyz");
        SECONDARY_ROLE_JOBS.add("qfsj");
        SECONDARY_ROLE_JOBS.add("qyqfsj");
    }

    // 根据岗位编码获取角色类型
    public static RoleType getRoleType(String jobCode) {
        if ("10001".equals(jobCode)) {
            return RoleType.TYPE_1;
        } else if (SECONDARY_ROLE_JOBS.contains(jobCode)) {
            return RoleType.TYPE_2;
        } else {
            return RoleType.TYPE_3;
        }
    }
}
