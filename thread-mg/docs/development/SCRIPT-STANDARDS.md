# 脚本开发规范

## 📋 概述

本文档定义 Thread-MG 项目的脚本开发标准，确保所有脚本保持一致的代码风格和使用体验。

## 🏗️ 架构设计

### 目录结构

```
scripts/
├── lib.sh              # 公共库（必须第一个加载）
├── test-phase2.sh      # 主测试脚本
├── run-benchmark.sh    # 基准测试脚本
└── ...                 # 其他脚本
```

### 公共库

所有脚本必须在开头导入公共库：

```bash
#!/bin/bash
set -e

# 导入公共库
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib.sh"
```

## 🎨 颜色和日志标准

### 颜色变量

| 变量 | 颜色代码 | 用途 |
|------|----------|------|
| `RED` | `\033[0;31m` | 错误、失败 |
| `GREEN` | `\033[0;32m` | 成功、通过 |
| `YELLOW` | `\033[1;33m` | 警告、注意 |
| `BLUE` | `\033[0;34m` | 信息、提示 |
| `CYAN` | `\033[0;36m` | 子标题、强调 |
| `BOLD` | `\033[1m` | 粗体、标题 |
| `NC` | `\033[0m` | 重置颜色 |

### 日志函数

| 函数 | 输出位置 | 格式 | 用途 |
|------|----------|------|------|
| `log_info()` | stdout | `[INFO] 消息` | 一般信息 |
| `log_success()` | stdout | `[SUCCESS] 消息` | 成功操作 |
| `log_warning()` | stdout | `[WARNING] 消息` | 警告信息 |
| `log_error()` | stderr | `[ERROR] 消息` | 错误信息 |

### 使用示例

```bash
log_info "正在处理..."
log_success "操作完成"
log_warning "注意：某些功能受限"
log_error "发生错误"
```

## 📝 脚本结构规范

### 标准模板

```bash
#!/bin/bash
# ============================================================================
# 脚本名称
# ============================================================================
# 描述：脚本功能说明
#
# 用法：./script.sh [选项]
#
# 选项:
#   -h, --help     显示帮助信息
#
# 示例:
#   ./script.sh -h
# ============================================================================

set -e

# ============================================================================
# 导入公共库
# ============================================================================
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib.sh"

# ============================================================================
# 配置和常量
# ============================================================================
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
DEFAULT_VALUE="default"

# ============================================================================
# 全局变量
# ============================================================================
VERBOSE=false
OUTPUT_DIR=""

# ============================================================================
# 函数定义
# ============================================================================

# 显示帮助信息
show_help() {
    cat << EOF
${BOLD}脚本名称${NC}

${BOLD}用法:${NC}
  $0 [选项]

${BOLD}选项:${NC}
  -h, --help    显示此帮助信息

${BOLD}示例:${NC}
  $0
  $0 --help

EOF
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            *)
                log_error "未知选项：$1"
                echo "使用 '$0 --help' 查看帮助信息"
                exit 1
                ;;
        esac
    done
}

# 验证环境
validate_environment() {
    log_info "验证运行环境..."
    
    if ! command_exists java; then
        exit_with_error "Java 未安装"
    fi
}

# 主函数
main() {
    parse_args "$@"
    validate_environment
    
    log_info "开始执行..."
    log_success "执行完成"
}

# 执行主函数
main "$@"
```

## 🔧 参数解析规范

### 短参数和长参数

所有参数必须同时支持短参数和长参数：

```bash
# 推荐格式
-f, --full
-i, --incremental
-c, --coverage
-h, --help
-v, --verbose
```

### 参数处理模式

```bash
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            # 布尔标志
            -f|--full)
                MODE="full"
                shift
                ;;
            
            # 带值的参数
            -o|--output)
                OUTPUT="$2"
                shift 2
                ;;
            
            # 帮助
            -h|--help)
                show_help
                exit 0
                ;;
            
            # 未知参数
            *)
                log_error "未知选项：$1"
                exit 1
                ;;
        esac
    done
}
```

## ✅ 工具函数

### 内置工具函数

| 函数 | 说明 |
|------|------|
| `supports_color()` | 检查终端是否支持颜色 |
| `print_separator()` | 打印分隔线 |
| `print_header()` | 打印带标题的分隔线 |
| `print_subheader()` | 打印子标题 |
| `exit_with_error()` | 显示错误并退出 |
| `command_exists()` | 检查命令是否存在 |
| `is_project_root()` | 检查是否在项目根目录 |
| `cd_to_project_root()` | 切换到项目根目录 |

### 使用示例

```bash
# 检查命令
if ! command_exists mvn; then
    exit_with_error "Maven 未安装" 1
fi

# 打印标题
print_header "测试报告"

# 打印子标题
print_subheader "详细信息"
```

## 📋 检查清单

在提交脚本前，请确认：

- [ ] 脚本开头有完整的注释头
- [ ] 导入了 `lib.sh` 公共库
- [ ] 没有重复定义颜色变量
- [ ] 没有重复定义日志函数
- [ ] 使用 `log_*` 函数进行输出
- [ ] 参数支持短参数和长参数
- [ ] 有帮助信息和示例
- [ ] 使用 `bash -n` 检查语法
- [ ] 脚本有执行权限 (`chmod +x`)

## 🚫 禁止事项

1. **不要**在脚本中重新定义颜色变量
2. **不要**在脚本中重新定义日志函数
3. **不要**使用 `echo -e` 直接输出彩色文本（使用日志函数）
4. **不要**使用硬编码路径（使用 `SCRIPT_DIR` 和 `PROJECT_ROOT`）
5. **不要**忽略错误（使用 `set -e`）

## 📚 参考

- [Bash 脚本最佳实践](https://google.github.io/styleguide/shellguide.html)
- [ANSI 转义码颜色参考](https://en.wikipedia.org/wiki/ANSI_escape_code#Colors)
