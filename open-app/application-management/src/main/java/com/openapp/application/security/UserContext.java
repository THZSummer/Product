package com.openapp.application.security;

/**
 * 用户上下文持有者
 * 使用 ThreadLocal 存储当前请求的用户信息
 * 
 * @author open-app
 * @since 1.0.0
 */
public class UserContext {

    private static final ThreadLocal<String> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    private UserContext() {
        // 私有构造函数，防止实例化
    }

    /**
     * 获取当前用户 ID
     */
    public static String getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    /**
     * 设置当前用户 ID
     */
    public static void setCurrentUserId(String userId) {
        CURRENT_USER_ID.set(userId);
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        return USERNAME.get();
    }

    /**
     * 设置用户名
     */
    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    /**
     * 获取用户角色
     */
    public static String getRole() {
        return ROLE.get();
    }

    /**
     * 设置用户角色
     */
    public static void setRole(String role) {
        ROLE.set(role);
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }
}
