-- ============================================================
-- V1__init_applications.sql
-- 应用管理系统 - 应用表初始化脚本
-- ============================================================
-- 功能：创建 applications 表，用于存储应用的基本信息
-- 数据库：MySQL 5.7
-- 字符集：utf8mb4
-- 引擎：InnoDB
-- 创建时间：2026-03-23
-- ============================================================

-- 删除已存在的表（仅在开发环境使用，生产环境请谨慎）
-- DROP TABLE IF EXISTS `applications`;

-- 创建 applications 表
CREATE TABLE IF NOT EXISTS `applications` (
    -- ============ 主键 ============
    `id`              VARCHAR(64)      NOT NULL                  COMMENT '应用 ID，使用 UUID 或雪花算法生成',
    
    -- ============ 基本信息 ============
    `name`            VARCHAR(128)     NOT NULL                  COMMENT '应用名称，同一 owner 下唯一',
    `description`     TEXT                                       COMMENT '应用描述，详细介绍应用功能',
    `icon_url`        VARCHAR(512)                               COMMENT '应用图标 URL，支持 http/https 路径',
    `type`            VARCHAR(32)      NOT NULL                  COMMENT '应用类型：WEB/MOBILE/DESKTOP/API/OTHER',
    `status`          VARCHAR(16)      NOT NULL DEFAULT 'draft'  COMMENT '应用状态：draft/pending/active/suspended/archived',
    `owner_id`        VARCHAR(64)      NOT NULL                  COMMENT '所有者 ID，用户或组织 ID',
    `owner_type`      VARCHAR(16)      NOT NULL                  COMMENT '所有者类型：USER/ORGANIZATION',
    `callback_url`    VARCHAR(512)                               COMMENT '回调 URL，用于状态变更通知',
    
    -- ============ 软删除 ============
    `deleted_at`      TIMESTAMP          NULL                    COMMENT '删除时间，NULL 表示未删除',
    `deleted_by`      VARCHAR(64)        NULL                    COMMENT '删除人 ID',
    
    -- ============ 审计字段 ============
    `version`         INTEGER          NOT NULL DEFAULT 1        COMMENT '版本号，用于乐观锁',
    `created_at`      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by`      VARCHAR(64)      NOT NULL                  COMMENT '创建人 ID',
    `updated_at`      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by`      VARCHAR(64)      NOT NULL                  COMMENT '更新人 ID',
    
    -- ============ 约束定义 ============
    PRIMARY KEY (`id`),
    
    -- 唯一约束：同一所有者下应用名称唯一
    UNIQUE KEY `uk_owner_name` (`owner_id`, `name`),
    
    -- ============ 索引定义 ============
    -- 按所有者查询（常用查询条件）
    INDEX `idx_owner_id` (`owner_id`),
    
    -- 按状态筛选（列表页常用）
    INDEX `idx_status` (`status`),
    
    -- 软删除查询（回收站功能）
    INDEX `idx_deleted_at` (`deleted_at`),
    
    -- 联合索引：查询某用户的所有应用及其状态
    INDEX `idx_owner_status` (`owner_id`, `status`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用表 - 存储应用的基本信息';


-- ============================================================
-- 初始化数据说明
-- ============================================================
-- 本脚本仅创建表结构，不插入初始数据
-- 应用数据由用户通过 API 动态创建
-- ============================================================


-- ============================================================
-- 表结构说明
-- ============================================================
-- 1. 主键设计：使用 VARCHAR(64) 支持 UUID 或雪花算法 ID
-- 2. 软删除：通过 deleted_at 字段实现，便于数据恢复和审计
-- 3. 乐观锁：version 字段用于并发控制
-- 4. 审计追踪：created_by/updated_by 记录操作人
-- 5. 索引优化：覆盖常用查询场景
-- ============================================================
