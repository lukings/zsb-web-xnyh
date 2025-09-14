package cn.visolink.system.job.authorization.model;

/**
 * @author 亮
 * @Description: 外呼权限岗位枚举
 * @date 2025/8/28 15:41
 */
public enum RoleType {
    TYPE_1(1),  // 一级角色
    TYPE_2(2),  // 二级角色
    TYPE_3(3);  // 三级角色

    private final int code;

    RoleType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
