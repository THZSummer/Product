package com.openapp.application.domain;

import com.openapp.application.domain.enums.AppStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用状态转换规则管理器
 * 定义应用程序状态之间的有效转换规则
 */
public class StatusTransitions {
    
    /**
     * 定义从每种状态可以转换到的有效目标状态
     * 例如：DRAFT可以从的状态可以转换到的目标集合
     */
    private static final Map<AppStatus, Set<AppStatus>> VALID_TRANSITIONS;
    
    static {
        Map<AppStatus, Set<AppStatus>> transitions = new HashMap<>();
        
        // 草稿状态可以转换到的目标状态
        transitions.put(AppStatus.DRAFT, Set.of(
            AppStatus.PENDING,    // 可以提交待审核
            AppStatus.ARCHIVED    // 可以直接归档
        ));
        
        // 待审核状态可以转换到的目标状态
        transitions.put(AppStatus.PENDING, Set.of(
            AppStatus.ACTIVE,     // 审核通过变为活跃
            AppStatus.SUSPENDED,  // 审核后被暂停
            AppStatus.DRAFT,      // 审核驳回回到草稿
            AppStatus.ARCHIVED    // 审核拒绝并归档
        ));
        
        // 活跃状态可以转换到的目标状态
        transitions.put(AppStatus.ACTIVE, Set.of(
            AppStatus.SUSPENDED,  // 暂停使用
            AppStatus.ARCHIVED    // 归档
        ));
        
        // 暂停状态可以转换到的目标状态
        transitions.put(AppStatus.SUSPENDED, Set.of(
            AppStatus.ACTIVE,     // 恢复活跃
            AppStatus.ARCHIVED    // 归档
        ));
        
        // 归档状态可以转换到的目标状态（通常只允许还原或保持归档）
        transitions.put(AppStatus.ARCHIVED, Set.of(
            AppStatus.DRAFT       // 从归档恢复到草稿
        ));
        
        VALID_TRANSITIONS = Collections.unmodifiableMap(transitions);
    }
    
    /**
     * 检查从源状态到目标状态的转换是否有效
     *
     * @param from 当前状态
     * @param to 目标状态
     * @return 如果转换有效返回true，否则返回false
     */
    public static boolean isValidTransition(AppStatus from, AppStatus to) {
        if (from == null || to == null) {
            return false;
        }
        
        if (from == to) {
            // 状态不变视为有效的自转换
            return true;
        }
        
        Set<AppStatus> allowedTransitions = VALID_TRANSITIONS.get(from);
        return allowedTransitions != null && allowedTransitions.contains(to);
    }
    
    /**
     * 获取从给定状态可以转换到的所有有效目标状态
     *
     * @param from 当前状态
     * @return 有效的目标状态集，如果from为null则返回空集
     */
    public static Set<AppStatus> getAllowedTransitions(AppStatus from) {
        if (from == null) {
            return Collections.emptySet();
        }
        
        Set<AppStatus> allowed = VALID_TRANSITIONS.get(from);
        return allowed != null ? allowed : Collections.emptySet();
    }
    
    /**
     * 获取所有状态转换规则的不可变映射
     *
     * @return 状态转换规则映射
     */
    public static Map<AppStatus, Set<AppStatus>> getAllValidTransitions() {
        return VALID_TRANSITIONS;
    }
    
    /**
     * 获取从指定状态下可以创建状态日志记录的原因描述
     *
     * @param from 当前状态
     * @param to 目标状态
     * @return 转换原因描述
     */
    public static String getTransitionReason(AppStatus from, AppStatus to) {
        if (!isValidTransition(from, to)) {
            return "无效的状态转换";
        }
        
        if (from == to) {
            return "状态保持不变";
        }
        
        switch (from) {
            case DRAFT:
                switch (to) {
                    case PENDING: return "提交审核";
                    case ARCHIVED: return "直接归档";
                }
                break;
            case PENDING:
                switch (to) {
                    case ACTIVE: return "审核通过";
                    case SUSPENDED: return "审核后暂停";
                    case DRAFT: return "审核驳回";
                    case ARCHIVED: return "审核拒绝并归档";
                }
                break;
            case ACTIVE:
                switch (to) {
                    case SUSPENDED: return "手动暂停";
                    case ARCHIVED: return "归档应用";
                }
                break;
            case SUSPENDED:
                switch (to) {
                    case ACTIVE: return "恢复应用";
                    case ARCHIVED: return "归档应用";
                }
                break;
            case ARCHIVED:
                switch (to) {
                    case DRAFT: return "从归档恢复";
                }
                break;
        }
        
        return String.format("从%s到%s的转换", 
                           from.getDescription(), 
                           to.getDescription());
    }
}