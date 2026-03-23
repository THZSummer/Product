package com.openapp.application.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 要求所有者或管理员权限注解
 * 用于标记需要权限校验的方法
 * 
 * @author open-app
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireOwnerOrAdmin {

    /**
     * 资源 ID 参数名，用于获取资源进行权限校验
     * 默认为 "id"，即从方法参数中获取名为 "id" 的参数
     */
    String resourceIdParam() default "id";
}
