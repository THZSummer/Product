package com.openapp.application.security;

import com.openapp.application.exception.ApplicationErrorCode;
import com.openapp.application.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 应用认证 AOP 切面
 * 拦截标注了 @RequireOwnerOrAdmin 的方法，进行权限校验
 * 
 * @author open-app
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class ApplicationAuthAspect {

    @Around("@annotation(com.openapp.application.security.RequireOwnerOrAdmin)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        RequireOwnerOrAdmin annotation = method.getAnnotation(RequireOwnerOrAdmin.class);
        String resourceIdParam = annotation.resourceIdParam();
        
        // 获取当前用户 ID
        String currentUserId = UserContext.getCurrentUserId();
        if (!StringUtils.hasText(currentUserId)) {
            throw new ApplicationException(ApplicationErrorCode.PERMISSION_DENIED,
                "未认证的用户");
        }
        
        // 获取资源 ID 参数
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String resourceId = null;
        
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(resourceIdParam)) {
                if (args[i] instanceof String) {
                    resourceId = (String) args[i];
                }
                break;
            }
        }
        
        // 如果没有找到资源 ID，检查是否是 admin
        if (resourceId == null) {
            log.warn("Resource ID parameter '{}' not found in method {}", resourceIdParam, method.getName());
        }
        
        // 管理员直接放行
        if (isAdmin(currentUserId)) {
            log.debug("Admin user {} accessing resource {}", currentUserId, resourceId);
            return joinPoint.proceed();
        }
        
        // 非管理员需要校验是否为资源所有者
        // 实际项目中应查询资源的所有者 ID 进行比对
        // 这里简化处理，假设 resourceId 就是所有者 ID
        if (resourceId != null && !resourceId.equals(currentUserId)) {
            throw new ApplicationException(ApplicationErrorCode.PERMISSION_DENIED,
                "您没有权限访问此资源");
        }
        
        log.debug("User {} authorized to access resource {}", currentUserId, resourceId);
        
        return joinPoint.proceed();
    }

    /**
     * 判断用户是否为管理员
     */
    private boolean isAdmin(String userId) {
        String role = UserContext.getRole();
        return "ADMIN".equals(role) || "admin".equals(userId) || 
               (userId != null && userId.startsWith("admin_"));
    }
}
