-- ============================================================
-- V2__init_application_status_logs.sql
-- 应用管理系统 - 应用状态变更日志表初始化脚本
-- ============================================================
-- 功能：创建 application_status_logs 表，记录应用状态变更历史
-- 数据库：MySQL 5.7
-- 字符集：utf8mb4
-- 引擎：InnoDB
-- 创建时间：2026-03-23
-- ============================================================

-- 删除已存在的表（仅在开发环境使用，生产环境请谨慎）
-- DROP TABLE IF EXISTS `application_status_logs`;

-- 创建 application_status_logs 表
CREATE TABLE IF NOT EXISTS `application_status_logs` (
    -- ============ 主键 ============
    `id`              VARCHAR(64)      NOT NULL                  COMMENT '日志 ID，使用 UUID 或雪花算法生成',
    
    -- ============ 状态变更信息 ============
    `application_id`  VARCHAR(64)      NOT NULL                  COMMENT '应用 ID，关联 applications 表',
    `old_status`      VARCHAR(16)      NOT NULL                  COMMENT '变更前的状态',
    `new_status`      VARCHAR(16)      NOT NULL                  COMMENT '变更后的状态',
    `reason`          VARCHAR(512)                               COMMENT '变更原因说明',
    `changed_by`      VARCHAR(64)      NOT NULL                  COMMENT '变更人 ID',
    `changed_at`      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
    
    -- ============ 约束定义 ============
    PRIMARY KEY (`id`),
    
    -- ============ 索引定义 ============
    -- 按应用 ID 查询（查询某应用的所有状态变更历史）
    INDEX `idx_app_id` (`application_id`),
    
    -- 按时间范围查询（审计日志查询）
    INDEX `idx_changed_at` (`changed_at`),
    
    -- ============ 外键约束 ============
    -- 确保 application_id 必须存在于 applications 表
    CONSTRAINT `fk_status_log_application` 
        FOREIGN KEY (`application_id`) 
        REFERENCES `applications` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用状态变更日志表 - 记录应用状态变更历史';


-- ============================================================
-- 初始化数据说明
-- ============================================================
-- 本脚本仅创建表结构，不插入初始数据
-- 状态变更日志由系统自动记录
-- ============================================================


-- ============================================================
-- 表结构说明
-- ============================================================
-- 1. 主键设计：使用 VARCHAR(64) 支持 UUID 或雪花算法 ID
-- 2. 外键约束：确保数据完整性，应用删除时级联删除日志
-- 3. 时间索引：支持按时间范围查询审计日志
-- 4. 应用索引：支持查询单个应用的状态变更历史
-- ============================================================


-- ============================================================
-- 使用示例
-- ============================================================
-- 1. 查询某应用的所有状态变更历史
--    SELECT * FROM application_status_logs 
--    WHERE application_id = 'xxx' 
--    ORDER BY changed_at DESC;
--
-- 2. 查询某时间段内的所有状态变更
--    SELECT * FROM application_status_logs 
--    WHERE changed_at BETWEEN '2026-03-01' AND '2026-03-31'
--    ORDER BY changed_at DESC;
--
-- 3. 统计某应用的状态变更次数
--    SELECT application_id, COUNT(*) as change_count 
--    FROM application_status_logs 
--    GROUP BY application_id;
-- ============================================================
