package com.openapp.application.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.stream.Stream;

/**
 * 应用类型枚举
 * 定义系统支持的应用程序类型
 * 对应数据库字段 type: WEB/MOBILE/DESKTOP/API/OTHER
 */
public enum AppType {
    /**
     * Web应用程序
     */
    WEB("WEB", "Web应用"),

    /**
     * 移动应用程序 (iOS/Android)
     */
    MOBILE("MOBILE", "移动应用"),

    /**
     * 桌面应用程序
     */
    DESKTOP("DESKTOP", "桌面应用"),

    /**
     * API服务
     */
    API("API", "API服务"),

    /**
     * 其他类型应用
     */
    OTHER("OTHER", "其他");

    private final String code;
    private final String description;

    AppType(String code, String description) {
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
     * 根据代码查找应用类型
     *
     * @param code 类型代码
     * @return 对应的应用类型，未找到返回null
     */
    public static AppType fromCode(String code) {
        return Stream.of(values())
                .filter(type -> type.code.equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
    }
}