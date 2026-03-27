# 脚本重构规格说明

## 📋 概述

修复测试脚本设计不一致问题，建立统一的脚本开发标准。

## 🎯 目标

1. **统一代码风格**：所有脚本使用相同的颜色定义、日志函数
2. **减少代码重复**：通过公共库 `lib.sh` 提供共享功能
3. **提高可维护性**：单一位置维护公共功能
4. **简化脚本**：删除冗余快捷脚本

## 📁 范围

### 影响文件

| 文件 | 操作 | 原因 |
|------|------|------|
| `scripts/lib.sh` | 新建 | 公共库 |
| `scripts/test-phase2.sh` | 重构 | 使用 lib.sh |
| `scripts/run-benchmark.sh` | 重构 | 使用 lib.sh |
| `scripts/test-full.sh` | 删除 | 冗余 |
| `scripts/test-watch.sh` | 删除 | 冗余 |
| `README.md` | 更新 | 文档同步 |
| `docs/development/SCRIPT-STANDARDS.md` | 新建 | 规范文档 |

## 🏗️ 架构设计

```
scripts/
├── lib.sh              # 公共库（新增）
├── test-phase2.sh      # 主测试脚本（重构）
├── run-benchmark.sh    # 基准测试脚本（重构）
├── test-full.sh        # ❌ 删除
└── test-watch.sh       # ❌ 删除
```

## 🔧 技术规范

### 公共库 API

```bash
# 颜色变量
RED, GREEN, YELLOW, BLUE, CYAN, BOLD, NC

# 日志函数
log_info()      # [INFO] 蓝色
log_success()   # [SUCCESS] 绿色
log_warning()   # [WARNING] 黄色
log_error()     # [ERROR] 红色，输出到 stderr

# 工具函数
supports_color()           # 检查是否支持颜色
print_separator()          # 打印分隔线
exit_with_error()          # 错误退出
```

### 参数规范

- 支持短参数：`-f`, `-i`, `-c`
- 支持长参数：`--full`, `--incremental`, `--coverage`
- 帮助参数：`-h`, `--help`

## ✅ 验收标准

1. [ ] `lib.sh` 可被正确加载
2. [ ] 所有脚本语法正确（`bash -n` 检查通过）
3. [ ] 无旧的颜色定义（`COLOR_*`）
4. [ ] 无旧的日志函数（`print_*`）
5. [ ] 冗余脚本已删除
6. [ ] 所有功能测试通过
