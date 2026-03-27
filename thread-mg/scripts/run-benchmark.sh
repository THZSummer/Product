#!/bin/bash
# ============================================================================
# Thread Management Benchmark 运行脚本
# ============================================================================
# 用法：./scripts/run-benchmark.sh [选项]
# 
# 选项:
#   -s, --scenario <name>    指定测试场景 (默认：全部)
#   -t, --strategy <name>    指定线程策略 (默认：全部)
#   -c, --config <path>      指定配置文件路径 (默认：config/benchmark/benchmark.yaml)
#   -o, --output <formats>   指定输出格式，逗号分隔 (默认：console,markdown)
#                            可选值：console, markdown, html, json
#   -v, --verbose            启用详细输出
#   -h, --help               显示帮助信息
#
# 示例:
#   ./scripts/run-benchmark.sh
#   ./scripts/run-benchmark.sh --scenario cpu-bound --strategy virtual
#   ./scripts/run-benchmark.sh --config config/benchmark/benchmark.yaml --output html,json
# ============================================================================

set -e  # 遇到错误立即退出

# ============================================================================
# 导入公共库
# ============================================================================
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
. "$SCRIPT_DIR/lib.sh"

# 脚本目录和项目根目录
# 使用 POSIX 兼容方式获取脚本路径
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 默认配置
DEFAULT_CONFIG="$PROJECT_ROOT/config/benchmark/benchmark.yaml"
DEFAULT_OUTPUT="console,markdown,html,json"
SCENARIO=""
STRATEGY=""
CONFIG_FILE="$DEFAULT_CONFIG"
OUTPUT_FORMATS="$DEFAULT_OUTPUT"
VERBOSE=false

# ----------------------------------------------------------------------------
# 函数：显示帮助信息
# ----------------------------------------------------------------------------
show_help() {
    cat << EOF
${BOLD}Thread Management Benchmark 运行脚本${NC}

${BOLD}用法:${NC}
  $0 [选项]

${BOLD}选项:${NC}
  -s, --scenario <name>    指定测试场景名称
                           可选：cpu-bound, io-bound, high-concurrency, 
                                mixed-load, recursive, async-pipeline
                           默认：运行所有场景
                           
  -t, --strategy <name>    指定线程策略名称
                           可选：platform, virtual, pool, 
                                forkjoin, completable, reactive
                           默认：运行所有策略
                           
  -c, --config <path>      指定配置文件路径
                           默认：config/benchmark/benchmark.yaml
                           
  -o, --output <formats>   指定输出格式 (逗号分隔)
                           可选：console, markdown, html, json
                           默认：console,markdown
                           
  -v, --verbose            启用详细输出模式
  
  -h, --help               显示此帮助信息

${BOLD}示例:${NC}
  # 运行所有测试
  $0
  
  # 只运行 CPU 密集型场景，使用虚拟线程
  $0 --scenario cpu-bound --strategy virtual
  
  # 使用自定义配置文件，生成 HTML 和 JSON 报告
  $0 --config my-config.yaml --output html,json
  
  # 运行特定场景并启用详细输出
  $0 -s io-bound -t platform -v

${BOLD}输出目录:${NC}
  原始数据：results/raw/
  测试报告：results/reports/

EOF
}

# ----------------------------------------------------------------------------
# 函数：解析命令行参数
# ----------------------------------------------------------------------------
parse_args() {
    while [ $# -gt 0 ]; do
        case $1 in
            -s|--scenario)
                SCENARIO="$2"
                shift 2
                ;;
            -t|--strategy)
                STRATEGY="$2"
                shift 2
                ;;
            -c|--config)
                CONFIG_FILE="$2"
                shift 2
                ;;
            -o|--output)
                OUTPUT_FORMATS="$2"
                shift 2
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
                echo "使用 '$0 --help' 查看帮助信息"
                exit 1
                ;;
        esac
    done
}

# ----------------------------------------------------------------------------
# 函数：验证环境和依赖
# ----------------------------------------------------------------------------
validate_environment() {
    log_info "验证运行环境..."
    
    # 检查 Java
    if ! command -v java > /dev/null 2>&1; then
        log_error "Java 未安装，请先安装 Java 21+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 21 ]; then
        log_error "需要 Java 21 或更高版本，当前版本：$JAVA_VERSION"
        exit 1
    fi
    log_info "Java 版本：$JAVA_VERSION ✓"
    
    # 检查 Maven
    if ! command -v mvn > /dev/null 2>&1; then
        log_error "Maven 未安装，请先安装 Maven 3.9+"
        exit 1
    fi
    log_info "Maven 已安装 ✓"
    
    # 检查配置文件
    if [ ! -f "$CONFIG_FILE" ]; then
        log_warning "配置文件不存在：$CONFIG_FILE"
        if [ "$CONFIG_FILE" = "$DEFAULT_CONFIG" ]; then
            log_info "将使用默认配置运行"
        else
            log_error "请检查配置文件路径"
            exit 1
        fi
    else
        log_info "配置文件：$CONFIG_FILE ✓"
    fi
    
    # 检查项目根目录
    if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
        log_error "未找到 pom.xml，请确认在项目根目录运行"
        exit 1
    fi
}

# ----------------------------------------------------------------------------
# 函数：创建输出目录
# ----------------------------------------------------------------------------
setup_directories() {
    log_info "创建输出目录..."
    
    mkdir -p "$PROJECT_ROOT/results/raw"
    mkdir -p "$PROJECT_ROOT/results/reports"
    
    log_success "输出目录准备完成"
}

# ----------------------------------------------------------------------------
# 函数：显示配置信息
# ----------------------------------------------------------------------------
show_configuration() {
    echo ""
    echo "========================================"
    echo "  基准测试配置"
    echo "========================================"
    echo "  场景：    ${SCENARIO:-全部}"
    echo "  策略：    ${STRATEGY:-全部}"
    echo "  配置：    $CONFIG_FILE"
    echo "  输出：    $OUTPUT_FORMATS"
    echo "  详细：    $VERBOSE"
    echo "========================================"
    echo ""
}

# ----------------------------------------------------------------------------
# 函数：运行基准测试
# ----------------------------------------------------------------------------
run_benchmark() {
    log_info "开始执行基准测试..."
    
    # 切换到项目根目录执行
    cd "$PROJECT_ROOT"
    
    # 先编译项目
    log_info "编译项目..."
    if ! mvn clean compile -q; then
        log_error "编译失败"
        return 1
    fi
    
    # 构建执行 Maven 命令
    MVN_CMD="mvn exec:java"
    
    # 添加输出格式参数
    MVN_CMD="$MVN_CMD -Dtest.output=$OUTPUT_FORMATS"
    
    # 添加配置文件参数
    MVN_CMD="$MVN_CMD -Dtest.config=$CONFIG_FILE"
    
    # 添加详细输出参数
    if [ "$VERBOSE" = true ]; then
        MVN_CMD="$MVN_CMD -X"
    fi
    
    echo ""
    log_info "执行命令：$MVN_CMD"
    echo ""
    
    # 执行 Maven exec
    if eval "$MVN_CMD"; then
        log_success "基准测试执行完成"
        return 0
    else
        log_error "基准测试执行失败"
        return 1
    fi
}

# ----------------------------------------------------------------------------
# 函数：显示结果摘要
# ----------------------------------------------------------------------------
show_summary() {
    echo ""
    echo "========================================"
    echo "  测试完成"
    echo "========================================"
    echo ""
    
    # 检查生成的报告
    REPORT_DIR="$PROJECT_ROOT/results/reports"
    
    if [ -d "$REPORT_DIR" ]; then
        MD_COUNT=$(find "$REPORT_DIR" -name "*.md" 2>/dev/null | wc -l)
        HTML_COUNT=$(find "$REPORT_DIR" -name "*.html" 2>/dev/null | wc -l)
        JSON_COUNT=$(find "$REPORT_DIR" -name "*.json" 2>/dev/null | wc -l)
        
        if [ $MD_COUNT -gt 0 ]; then
            log_info "生成 Markdown 报告：$MD_COUNT 个"
        fi
        if [ $HTML_COUNT -gt 0 ]; then
            log_info "生成 HTML 报告：$HTML_COUNT 个"
        fi
        if [ $JSON_COUNT -gt 0 ]; then
            log_info "生成 JSON 报告：$JSON_COUNT 个"
        fi
        
        if [ $MD_COUNT -eq 0 ] && [ $HTML_COUNT -eq 0 ] && [ $JSON_COUNT -eq 0 ]; then
            log_warning "未生成报告文件，请检查测试输出"
        fi
    fi
    
    echo ""
    echo "报告目录：$REPORT_DIR"
    echo "========================================"
    echo ""
}

# ----------------------------------------------------------------------------
# 主程序
# ----------------------------------------------------------------------------
main() {
    echo ""
    echo "========================================"
    echo "  Thread Management Benchmark"
    echo "========================================"
    echo ""
    
    # 解析参数
    parse_args "$@"
    
    # 验证环境
    validate_environment
    
    # 设置目录
    setup_directories
    
    # 显示配置
    show_configuration
    
    # 运行测试
    if run_benchmark; then
        # 显示摘要
        show_summary
        log_success "所有任务完成!"
        exit 0
    else
        log_error "测试执行失败，请查看上方错误信息"
        exit 1
    fi
}

# 执行主程序
main "$@"
