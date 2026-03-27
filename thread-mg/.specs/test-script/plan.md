# 技术规划：Phase 2 测试脚本

**Feature ID**: test-script  
**关联文档**: [spec.md](spec.md) | [tasks.md](tasks.md)  
**创建日期**: 2026-03-27  
**状态**: in-progress  

---

## 🏗️ 架构设计

### 整体架构

```
┌─────────────────────────────────────────────────────┐
│                  test-phase2.sh                      │
│  ┌─────────────────────────────────────────────┐    │
│  │              参数解析模块                     │    │
│  │  - 解析命令行选项                            │    │
│  │  - 验证参数组合                              │    │
│  └─────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────┐    │
│  │              Git 变更检测模块                 │    │
│  │  - git diff --name-only                      │    │
│  │  - 匹配 Phase 2 相关文件                       │    │
│  └─────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────┐    │
│  │              Maven 测试执行模块               │    │
│  │  - mvn test -Dtest=...                       │    │
│  │  - mvn test (完整测试)                        │    │
│  │  - mvn test jacoco:report (覆盖率)            │    │
│  └─────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────┐    │
│  │              结果解析与输出模块               │    │
│  │  - 解析 Maven 输出                            │    │
│  │  - 格式化彩色输出                            │    │
│  │  - 生成统计信息                              │    │
│  └─────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

---

## 📝 实现方案

### 1. 参数解析

```bash
#!/bin/bash

# 默认值
MODE="incremental"
COVERAGE=false
REPORT=false
WATCH=false

# 解析参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -full)
            MODE="full"
            shift
            ;;
        -incremental)
            MODE="incremental"
            shift
            ;;
        -coverage)
            COVERAGE=true
            shift
            ;;
        -report)
            REPORT=true
            shift
            ;;
        -watch)
            WATCH=true
            shift
            ;;
        -help)
            show_help
            exit 0
            ;;
        *)
            echo "未知选项：$1"
            show_help
            exit 1
            ;;
    esac
done
```

### 2. Git 变更检测

```bash
# Phase 2 相关文件模式
PHASE2_PATTERNS=(
    "ThreadPoolStrategy"
    "HighConcurrencyScenario"
    "MixedLoadScenario"
    "RecursiveScenario"
)

# 检测最近一次提交的变更
detect_changes() {
    local changed_files=$(git diff --name-only HEAD~1 2>/dev/null)
    
    if [ -z "$changed_files" ]; then
        # 没有提交记录，检测工作区变更
        changed_files=$(git diff --name-only 2>/dev/null)
    fi
    
    local phase2_tests=()
    
    for pattern in "${PHASE2_PATTERNS[@]}"; do
        if echo "$changed_files" | grep -q "$pattern"; then
            # 根据主类名推导测试类名
            phase2_tests+=("${pattern}Test")
        fi
    done
    
    echo "${phase2_tests[@]}"
}
```

### 3. Maven 测试执行

```bash
# 执行增量测试
run_incremental_tests() {
    local test_classes="$1"
    
    if [ -z "$test_classes" ]; then
        echo "⚠️  未检测到 Phase 2 相关变更"
        return 0
    fi
    
    local test_arg=$(IFS=,; echo "${test_classes[*]}")
    
    echo "🧪 运行测试：$test_arg"
    mvn test -Dtest="$test_arg" -q
}

# 执行完整测试
run_full_tests() {
    echo "🧪 运行完整测试套件"
    mvn test
}

# 生成覆盖率报告
generate_coverage() {
    echo "📊 生成覆盖率报告..."
    mvn test jacoco:report -q
    echo "✅ 报告生成：target/site/jacoco/index.html"
}
```

### 4. 结果解析与输出

```bash
# 解析 Maven 测试结果
parse_test_results() {
    local output="$1"
    
    # 提取测试结果统计
    local total=$(echo "$output" | grep -oP 'Tests run: \K\d+' | head -1)
    local failures=$(echo "$output" | grep -oP 'Failures: \K\d+' | head -1)
    local errors=$(echo "$output" | grep -oP 'Errors: \K\d+' | head -1)
    local skipped=$(echo "$output" | grep -oP 'Skipped: \K\d+' | head -1)
    
    # 计算通过率
    local passed=$((total - failures - errors))
    local pass_rate=0
    if [ "$total" -gt 0 ]; then
        pass_rate=$((passed * 100 / total))
    fi
    
    # 彩色输出
    if [ "$failures" -eq 0 ] && [ "$errors" -eq 0 ]; then
        echo -e "\033[0;32m✅ 测试结果：$passed/$total 通过 ($pass_rate%)\033[0m"
    else
        echo -e "\033[0;31m❌ 测试结果：$passed/$total 通过 ($pass_rate%)\033[0m"
    fi
}
```

### 5. 监听模式实现

```bash
# 使用 inotify (Linux) 或 fswatch (macOS)
run_watch_mode() {
    echo "👀 监听文件变更..."
    
    if command -v inotifywait &> /dev/null; then
        # Linux
        while inotifywait -r -e modify --include '.*\.java$' src/; do
            clear
            run_tests
        done
    elif command -v fswatch &> /dev/null; then
        # macOS
        fswatch -o --include '.*\.java$' src/ | while read; do
            clear
            run_tests
        done
    else
        # 降级方案：轮询
        echo "⚠️  未找到 inotifywait 或 fswatch，使用轮询模式"
        local last_hash=$(find src -name "*.java" -exec md5sum {} \; | md5sum)
        while true; do
            sleep 2
            local current_hash=$(find src -name "*.java" -exec md5sum {} \; | md5sum)
            if [ "$last_hash" != "$current_hash" ]; then
                clear
                run_tests
                last_hash="$current_hash"
            fi
        done
    fi
}
```

---

## 🎨 颜色方案

```bash
# 颜色定义
COLOR_RESET="\033[0m"
COLOR_RED="\033[0;31m"
COLOR_GREEN="\033[0;32m"
COLOR_YELLOW="\033[0;33m"
COLOR_BLUE="\033[0;34m"
COLOR_PURPLE="\033[0;35m"
COLOR_CYAN="\033[0;36m"
COLOR_BOLD="\033[1m"

# 使用示例
echo -e "${COLOR_GREEN}✅ 测试通过${COLOR_RESET}"
echo -e "${COLOR_RED}❌ 测试失败${COLOR_RESET}"
echo -e "${COLOR_YELLOW}⚠️  警告${COLOR_RESET}"
```

---

## 🔧 依赖要求

### 必需依赖
- Bash 4.0+
- Git
- Maven 3.6+
- Java 21+

### 可选依赖（用于增强功能）
- `inotify-tools` (Linux 监听模式)
- `fswatch` (macOS 监听模式)
- `jq` (JSON 报告解析)

---

## 📁 文件结构

```
thread-mg/
├── scripts/
│   ├── test-phase2.sh       # 主测试脚本
│   ├── test-full.sh         # 完整测试快捷脚本
│   └── test-watch.sh        # 监听模式快捷脚本
├── .specs/test-script/
│   ├── spec.md              # 规范文档
│   ├── plan.md              # 技术规划
│   └── tasks.md             # 任务分解
├── target/
│   └── site/
│       └── jacoco/          # 覆盖率报告输出
└── README.md                # 更新使用说明
```

---

## 🚀 执行流程

### 增量测试流程
```
开始
  │
  ▼
解析参数
  │
  ▼
检测 Git 变更 ──(无变更)──► 提示并退出
  │
  ▼ (有变更)
匹配 Phase 2 类
  │
  ▼
构建 Maven 测试命令
  │
  ▼
执行测试
  │
  ▼
解析结果
  │
  ▼
输出报告
  │
  ▼
结束
```

### 完整测试流程
```
开始
  │
  ▼
解析参数
  │
  ▼
执行 mvn test
  │
  ▼
(可选) 生成覆盖率报告
  │
  ▼
解析结果
  │
  ▼
输出报告
  │
  ▼
结束
```

---

## ⚠️ 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Git 无提交历史 | 无法检测增量 | 降级为完整测试或工作区检测 |
| Maven 测试超时 | 测试卡住 | 添加超时参数 `-Dsurefire.timeout=300` |
| 颜色不支持 | 输出混乱 | 检测终端支持，自动降级 |
| 跨平台差异 | 脚本不兼容 | 使用 POSIX 兼容语法，避免 Bash 特有功能 |

---

*文档版本：1.0 | 创建日期：2026-03-27*
