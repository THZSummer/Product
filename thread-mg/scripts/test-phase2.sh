#!/bin/bash
#
# Phase 2 测试脚本
# 支持增量测试、完整测试、覆盖率报告和监听模式
#
# 用法：./test-phase2.sh [选项]
#
# 选项:
#   -f, --full         运行完整测试套件
#   -i, --incremental  只测试 Phase 2 新增/修改的类 (默认)
#   -c, --coverage     生成覆盖率报告
#   -r, --report       生成详细测试报告
#   -w, --watch        监听模式，文件变更自动测试
#   -v, --verbose      显示详细输出
#   -h, --help         显示帮助信息
#
# 示例:
#   ./test-phase2.sh -i              # 增量测试
#   ./test-phase2.sh -f -c           # 完整测试 + 覆盖率
#   ./test-phase2.sh -w              # 监听模式
#

# 用法: ./test-phase2.sh [选项]
#
# 选项:
#   -full        运行完整测试套件
#   -incremental 只测试 Phase 2 新增/修改的类 (默认)
#   -coverage    生成覆盖率报告
#   -report      生成详细测试报告
#   -watch       监听模式，文件变更自动测试
#   -help        显示帮助信息
#
# 示例:
#   ./test-phase2.sh -incremental              # 增量测试
#   ./test-phase2.sh -full -coverage           # 完整测试 + 覆盖率
#   ./test-phase2.sh -watch                    # 监听模式
#

set -e

# ============================================================================
# Bash 版本检查
# ============================================================================
if [ -z "$BASH_VERSION" ]; then
  echo "Error: This script requires bash. Run with: bash $0" >&2
  exit 1
fi

# ============================================================================
# 导入公共库
# ============================================================================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
. "$SCRIPT_DIR/lib.sh"

# ============================================================================
# 配置和常量
# ============================================================================

PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PHASE2_PATTERNS=(
    "ThreadPoolStrategy"
    "HighConcurrencyScenario"
    "MixedLoadScenario"
    "RecursiveScenario"
)

# 默认设置
MODE="incremental"
COVERAGE=false
REPORT=false
WATCH=false
VERBOSE=false

# 统计信息
TEST_START_TIME=0
TEST_END_TIME=0

# ============================================================================
# 时间工具函数
# ============================================================================

# 获取当前时间（毫秒）
get_time_ms() {
    echo $(($(date +%s%N)/1000000))
}

# 计算耗时（秒）
calc_duration() {
    local start=$1
    local end=$2
    local diff=$((end - start))
    # 转换为秒，保留一位小数
    echo "scale=1; $diff / 1000" | bc
}

# ============================================================================
# 帮助信息
# ============================================================================

show_help() {
    cat << EOF
${BOLD}Phase 2 测试脚本${NC}

支持增量测试、完整测试、覆盖率报告和监听模式

${BOLD}用法:${NC}
  $0 [选项]

${BOLD}选项:${NC}
  -f, --full       运行完整测试套件
  -i, --incremental 只测试 Phase 2 新增/修改的类 (默认)
  -c, --coverage   生成覆盖率报告
  -r, --report     生成详细测试报告
  -w, --watch      监听模式，文件变更自动测试
  -v, --verbose    显示详细输出
  -h, --help       显示此帮助信息

${BOLD}示例:${NC}
  $0                          # 运行增量测试（默认）
  $0 -i                       # 运行增量测试
  $0 -f                       # 运行完整测试
  $0 -f -c                    # 完整测试 + 生成覆盖率报告
  $0 -i -r                    # 增量测试 + 详细报告
  $0 -w                       # 监听模式，文件变更自动测试

${BOLD}Phase 2 测试类:${NC}
  ThreadPoolStrategyTest      - 线程池策略测试
  HighConcurrencyScenarioTest - 高并发场景测试
  MixedLoadScenarioTest       - 混合负载场景测试
  RecursiveScenarioTest       - 递归分治场景测试

${BOLD}输出目录:${NC}
  测试报告：target/surefire-reports/
  覆盖率报告：target/site/jacoco/

EOF
}

# ============================================================================
# Git 变更检测
# ============================================================================

# 检测 Phase 2 相关的变更文件
detect_phase2_changes() {
    local changed_files=""
    
    # 首先尝试检测最近一次提交的变更
    if git rev-parse HEAD~1 >/dev/null 2>&1; then
        changed_files=$(git diff --name-only HEAD~1 2>/dev/null || true)
    fi
    
    # 如果没有提交历史，检测工作区变更
    if [ -z "$changed_files" ]; then
        changed_files=$(git diff --name-only 2>/dev/null || true)
    fi
    
    # 如果还没有，检测暂存区变更
    if [ -z "$changed_files" ]; then
        changed_files=$(git diff --cached --name-only 2>/dev/null || true)
    fi
    
    # 过滤 Phase 2 相关文件
    local phase2_tests=()
    
    for pattern in "${PHASE2_PATTERNS[@]}"; do
        if echo "$changed_files" | grep -q "$pattern"; then
            phase2_tests+=("${pattern}Test")
        fi
    done
    
    # 输出变更文件
    if [ ${#phase2_tests[@]} -gt 0 ]; then
        echo "${phase2_tests[@]}"
    fi
}

# 显示检测到的变更文件
show_changed_files() {
    local changed_files=""
    
    if git rev-parse HEAD~1 >/dev/null 2>&1; then
        changed_files=$(git diff --name-only HEAD~1 2>/dev/null || true)
    fi
    
    if [ -z "$changed_files" ]; then
        changed_files=$(git diff --name-only 2>/dev/null || true)
    fi
    
    if [ -n "$changed_files" ]; then
        print_subheader "检测到变更文件"
        echo "$changed_files" | while read -r file; do
            # 只关注 Phase 2 相关文件
            for pattern in "${PHASE2_PATTERNS[@]}"; do
                if echo "$file" | grep -q "$pattern"; then
                    echo "  - $file"
                    break
                fi
            done
        done
    fi
}

# ============================================================================
# Maven 测试执行
# ============================================================================

# 执行增量测试
run_incremental_tests() {
    local test_classes="$1"
    
    if [ -z "$test_classes" ]; then
        log_warning "未检测到 Phase 2 相关变更"
        log_info "运行完整测试套件以验证所有功能..."
        run_full_tests
        return $?
    fi
    
    local test_arg=$(IFS=,; echo "${test_classes[*]}")
    
    print_subheader "运行 Phase 2 增量测试"
    log_info "测试类：$test_arg"
    echo ""
    
    TEST_START_TIME=$(get_time_ms)
    
    # 执行 Maven 测试
    local mvn_output
    local mvn_exit_code=0
    
    if [ "$VERBOSE" = true ]; then
        mvn test -Dtest="$test_arg"
        mvn_exit_code=$?
    else
        mvn_output=$(mvn test -Dtest="$test_arg" 2>&1) || mvn_exit_code=$?
    fi
    
    TEST_END_TIME=$(get_time_ms)
    
    # 解析并显示结果
    parse_and_show_results "$mvn_output" "$mvn_exit_code"
    
    return $mvn_exit_code
}

# 执行完整测试
run_full_tests() {
    print_subheader "运行完整测试套件"
    echo ""
    
    TEST_START_TIME=$(get_time_ms)
    
    local mvn_output
    local mvn_exit_code=0
    
    if [ "$VERBOSE" = true ]; then
        mvn test
        mvn_exit_code=$?
    else
        mvn_output=$(mvn test 2>&1) || mvn_exit_code=$?
    fi
    
    TEST_END_TIME=$(get_time_ms)
    
    # 解析并显示结果
    parse_and_show_results "$mvn_output" "$mvn_exit_code"
    
    return $mvn_exit_code
}

# 解析并显示测试结果
parse_and_show_results() {
    local output="$1"
    local exit_code="$2"
    
    # 尝试从输出中提取统计信息
    local total=0
    local failures=0
    local errors=0
    local skipped=0
    local passed=0
    
    # 解析 Maven Surefire 输出
    if [ -n "$output" ]; then
        # 尝试多种输出格式
        total=$(echo "$output" | grep -oP 'Tests run:\s*\K\d+' | tail -1 || echo "0")
        failures=$(echo "$output" | grep -oP 'Failures:\s*\K\d+' | tail -1 || echo "0")
        errors=$(echo "$output" | grep -oP 'Errors:\s*\K\d+' | tail -1 || echo "0")
        skipped=$(echo "$output" | grep -oP 'Skipped:\s*\K\d+' | tail -1 || echo "0")
        
        # 处理空值
        total=${total:-0}
        failures=${failures:-0}
        errors=${errors:-0}
        skipped=${skipped:-0}
        
        passed=$((total - failures - errors))
    fi
    
    # 计算耗时
    local duration=$(calc_duration $TEST_START_TIME $TEST_END_TIME)
    
    # 计算通过率
    local pass_rate=0
    if [ "$total" -gt 0 ]; then
        pass_rate=$((passed * 100 / total))
    fi
    
    echo ""
    print_header "测试结果"
    
    if [ "$exit_code" -eq 0 ]; then
        log_success "所有测试通过!"
    else
        log_error "测试失败"
    fi
    
    echo ""
    echo "📊 统计信息:"
    echo "  总计：$total"
    echo "  通过：$passed"
    echo "  失败：$failures"
    echo "  错误：$errors"
    echo "  跳过：$skipped"
    echo "  通过率：${pass_rate}%"
    echo "  耗时：${duration}s"
    
    # 显示失败详情（如果有）
    if [ "$failures" -gt 0 ] || [ "$errors" -gt 0 ]; then
        echo ""
        print_subheader "失败详情"
        echo "$output" | grep -A 5 "FAILURE\|ERROR" | head -20
    fi
    
    # 显示报告位置
    echo ""
    log_info "详细报告：target/surefire-reports/"
}

# 生成覆盖率报告
generate_coverage_report() {
    print_subheader "生成覆盖率报告"
    echo ""
    
    local mvn_exit_code=0
    
    if [ "$VERBOSE" = true ]; then
        mvn test jacoco:report
        mvn_exit_code=$?
    else
        mvn test jacoco:report -q
        mvn_exit_code=$?
    fi
    
    if [ $mvn_exit_code -eq 0 ]; then
        log_success "覆盖率报告已生成"
        echo ""
        echo "📄 报告位置:"
        echo "  HTML: target/site/jacoco/index.html"
        echo "  XML:  target/site/jacoco/jacoco.xml"
        
        # 尝试提取覆盖率数据
        if [ -f "target/site/jacoco/jacoco.xml" ]; then
            local coverage=$(grep -oP 'coverage="[^"]*"' target/site/jacoco/jacoco.xml | head -1 | cut -d'"' -f2)
            if [ -n "$coverage" ]; then
                local coverage_percent=$(echo "scale=0; $coverage * 100 / 1" | bc)
                echo ""
                log_info "整体覆盖率：${coverage_percent}%"
            fi
        fi
    else
        log_error "生成覆盖率报告失败"
    fi
    
    return $mvn_exit_code
}

# ============================================================================
# 监听模式
# ============================================================================

run_watch_mode() {
    print_header "监听模式"
    log_info "监听 Java 文件变更，自动运行测试..."
    log_info "按 Ctrl+C 退出"
    echo ""
    
    # 检测可用的监听工具
    local watch_tool=""
    
    if command -v inotifywait &> /dev/null; then
        watch_tool="inotifywait"
    elif command -v fswatch &> /dev/null; then
        watch_tool="fswatch"
    fi
    
    if [ -n "$watch_tool" ]; then
        run_watch_with_tool "$watch_tool"
    else
        run_watch_polling
    fi
}

# 使用专业工具监听
run_watch_with_tool() {
    local tool="$1"
    
    if [ "$tool" = "inotifywait" ]; then
        # Linux inotifywait
        inotifywait -m -r -e modify --include '.*\.java$' src/ 2>/dev/null | while read -r path event file; do
            clear
            log_info "检测到变更：$file"
            echo ""
            run_tests_internal
        done
    elif [ "$tool" = "fswatch" ]; then
        # macOS fswatch
        fswatch -o --include '.*\.java$' src/ 2>/dev/null | while read -r event; do
            clear
            log_info "检测到文件变更"
            echo ""
            run_tests_internal
        done
    fi
}

# 轮询模式监听
run_watch_polling() {
    log_warning "未找到 inotifywait 或 fswatch，使用轮询模式（每 5 秒检查一次）"
    echo ""
    
    local last_hash=$(find src -name "*.java" 2>/dev/null | xargs md5sum 2>/dev/null | md5sum | awk '{print $1}')
    
    while true; do
        sleep 5
        local current_hash=$(find src -name "*.java" 2>/dev/null | xargs md5sum 2>/dev/null | md5sum | awk '{print $1}')
        
        if [ "$last_hash" != "$current_hash" ]; then
            clear
            log_info "检测到文件变更"
            echo ""
            run_tests_internal
            last_hash="$current_hash"
        fi
    done
}

# 监听模式内部测试执行
run_tests_internal() {
    if [ "$MODE" = "incremental" ]; then
        local tests=$(detect_phase2_changes)
        if [ -n "$tests" ]; then
            run_incremental_tests "$tests"
        else
            run_full_tests
        fi
    else
        run_full_tests
    fi
    
    if [ "$COVERAGE" = true ]; then
        generate_coverage_report
    fi
    
    echo ""
    log_info "继续监听..."
}

# ============================================================================
# 主入口
# ============================================================================

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -f|--full)
                MODE="full"
                shift
                ;;
            -i|--incremental)
                MODE="incremental"
                shift
                ;;
            -c|--coverage)
                COVERAGE=true
                shift
                ;;
            -r|--report)
                REPORT=true
                shift
                ;;
            -w|--watch)
                WATCH=true
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                log_error "未知选项：$1"
                echo ""
                echo "使用 '$0 --help' 查看帮助信息"
                exit 1
                ;;
        esac
    done
}

# 主函数
main() {
    # 解析参数
    parse_args "$@"
    
    # 切换到项目根目录
    cd "$PROJECT_ROOT"
    
    # 检查 Maven 是否可用
    if ! command -v mvn &> /dev/null; then
        log_error "Maven 未安装或不在 PATH 中"
        exit 1
    fi
    
    # 检查 Git 是否可用
    if ! command -v git &> /dev/null; then
        log_error "Git 未安装或不在 PATH 中"
        exit 1
    fi
    
    # 显示启动信息
    print_header "Phase 2 测试"
    echo ""
    echo "模式：$MODE"
    [ "$COVERAGE" = true ] && echo "覆盖率：启用"
    [ "$REPORT" = true ] && echo "详细报告：启用"
    [ "$WATCH" = true ] && echo "监听模式：启用"
    
    # 根据模式执行
    if [ "$WATCH" = true ]; then
        run_watch_mode
    else
        case $MODE in
            incremental)
                local tests=$(detect_phase2_changes)
                show_changed_files
                run_incremental_tests "$tests"
                local exit_code=$?
                ;;
            full)
                run_full_tests
                local exit_code=$?
                ;;
            *)
                log_error "未知模式：$MODE"
                exit 1
                ;;
        esac
        
        # 生成覆盖率报告（如果请求）
        if [ "$COVERAGE" = true ]; then
            generate_coverage_report
        fi
        
        # 显示最终状态
        echo ""
        if [ $exit_code -eq 0 ]; then
            log_success "测试完成"
        else
            log_error "测试失败"
        fi
        echo ""
        
        exit $exit_code
    fi
}

# 执行主函数
main "$@"
