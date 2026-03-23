package com.openapp.application.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

/**
 * 应用状态枚举
 * 定义应用程序在其生命周期中可能处于的各种状态
 * 对应数据库字段 status: draft/pending/active/suspended/archived
 */
public enum AppStatus {
    /**
     * 草稿 - 初始状态，应用详情尚未完全填写或审核
     */
    DRAFT("draft", "草稿"),

    /**
     * 待审核 - 应用已提交审核，等待系统或管理员审批
     */
    PENDING("pending", "待审核"),

    /**
     * 已激活 - 应用已获得授权，可在系统中正常使用
     */
    ACTIVE("active", "活跃"),

    /**
     * 已暂停 - 应用暂时停止使用，可能是由于维护或策略原因
     */
    SUSPENDED("suspended", "暂停"),

    /**
     * 已归档 - 应用已归档，不再使用但保留历史数据
     */
    ARCHIVED("archived", "归档");

    private final String code;
    private final String description;

    AppStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取应用状态
     *
     * @param code 状态代码
     * @return 对应的应用状态，未找到返回null
     */
    public static AppStatus fromCode(String code) {
        return Stream.of(values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}