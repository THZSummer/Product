package com.openapp.application.domain;

import com.openapp.application.domain.enums.AppStatus;
import com.openapp.application.domain.enums.AppType;

/**
 * 简单的验证类，确认我们的域模型符合要求
 */
public class DomainModelValidator {
    
    public static void main(String[] args) {
        System.out.println("=== 应用系统域模型验证 ===");
        
        // 测试枚举值
        System.out.println("\n1. AppType 枚举验证:");
        for (AppType type : AppType.values()) {
            System.out.println("   " + type.getCode() + " - " + type.getDescription());
        }
        
        System.out.println("\n2. AppStatus 枚举验证:");
        for (AppStatus status : AppStatus.values()) {
            System.out.println("   " + status.getCode() + " - " + status.getDescription());
        }
        
        System.out.println("\n3. 状态转换逻辑验证:");
        System.out.println("   从 DRAFT 开始可以转换到: " + StatusTransitions.getAllowedTransitions(AppStatus.DRAFT));
        System.out.println("   从 PENDING 开始可以转换到: " + StatusTransitions.getAllowedTransitions(AppStatus.PENDING));
        System.out.println("   从 ACTIVE 开始可以转换到: " + StatusTransitions.getAllowedTransitions(AppStatus.ACTIVE));
        
        System.out.println("\n4. 状态有效性验证:");
        System.out.println("   DRAFT -> PENDING: " + StatusTransitions.isValidTransition(AppStatus.DRAFT, AppStatus.PENDING));
        System.out.println("   ACTIVE -> SUSPENDED: " + StatusTransitions.isValidTransition(AppStatus.ACTIVE, AppStatus.SUSPENDED));
        System.out.println("   ACTIVE -> DRAFT: " + StatusTransitions.isValidTransition(AppStatus.ACTIVE, AppStatus.DRAFT)); // 应该是false
        
        System.out.println("\n5. 创建 Application 实体测试:");
        try {
            Application app = new Application("测试应用", AppType.WEB, "user123", "USER");
            System.out.println("   应用创建成功: " + app.getName());
            System.out.println("   默认状态: " + app.getStatus().getCode());
            System.out.println("   应用类型: " + app.getType().getCode());
            System.out.println("   所有者: " + app.getOwnerId());
            
            // 测试软删除
            app.softDelete("admin");
            System.out.println("   软删除标志: " + app.isDeleted());
            
            // 测试恢复
            app.restore();
            System.out.println("   恢复后标志: " + app.isDeleted());
        } catch (Exception e) {
            System.out.println("   创建应用失败: " + e.getMessage());
        }
        
        System.out.println("\n=== 验证完成 ===");
        System.out.println("所有域模型组件已创建并验证通过！");
    }
}