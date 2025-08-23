#!/bin/bash

# 生成测试覆盖率报告脚本
# 用法: ./scripts/generate-coverage-report.sh [选项]
# 选项:
#   --unit-only     只运行单元测试
#   --integration   包含集成测试
#   --site          生成完整的站点报告
#   --open          生成后自动打开报告

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Maven是否可用
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven 未安装或不在PATH中"
        exit 1
    fi
}

# 清理之前的报告
clean_reports() {
    print_info "清理之前的报告..."
    rm -rf target/site
    rm -rf target/jacoco*.exec
    rm -rf target/surefire-reports
    rm -rf target/failsafe-reports
}

# 运行单元测试
run_unit_tests() {
    print_info "运行单元测试..."
    mvn clean test -Dspring.profiles.active=test
    
    if [ $? -eq 0 ]; then
        print_success "单元测试完成"
    else
        print_error "单元测试失败"
        exit 1
    fi
}

# 运行集成测试
run_integration_tests() {
    print_info "运行集成测试..."
    mvn verify -Dspring.profiles.active=test
    
    if [ $? -eq 0 ]; then
        print_success "集成测试完成"
    else
        print_warning "集成测试失败，但继续生成报告"
    fi
}

# 生成JaCoCo报告
generate_jacoco_report() {
    print_info "生成JaCoCo覆盖率报告..."
    mvn jacoco:report jacoco:report-integration jacoco:merge
    
    if [ $? -eq 0 ]; then
        print_success "JaCoCo报告生成完成"
    else
        print_error "JaCoCo报告生成失败"
        exit 1
    fi
}

# 生成站点报告
generate_site_report() {
    print_info "生成Maven站点报告..."
    mvn site -Dspring.profiles.active=test
    
    if [ $? -eq 0 ]; then
        print_success "站点报告生成完成"
    else
        print_warning "站点报告生成失败"
    fi
}

# 显示报告位置
show_report_locations() {
    echo
    print_info "报告生成完成！报告位置："
    echo "  📊 合并覆盖率报告: target/site/jacoco-merged/index.html"
    echo "  🧪 单元测试覆盖率: target/site/jacoco-unit/index.html"
    echo "  🔧 集成测试覆盖率: target/site/jacoco-integration/index.html"
    echo "  📋 测试结果报告: target/site/surefire-report.html"
    echo "  🌐 完整站点报告: target/site/index.html"
    echo
}

# 打开报告
open_report() {
    local report_file="target/site/jacoco-merged/index.html"
    
    if [ -f "$report_file" ]; then
        print_info "正在打开覆盖率报告..."
        
        # 检测操作系统并使用相应的命令打开文件
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            open "$report_file"
        elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
            # Linux
            xdg-open "$report_file"
        elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
            # Windows
            start "$report_file"
        else
            print_warning "无法自动打开报告，请手动打开: $report_file"
        fi
    else
        print_error "报告文件不存在: $report_file"
    fi
}

# 显示覆盖率摘要
show_coverage_summary() {
    local jacoco_csv="target/site/jacoco-merged/jacoco.csv"
    
    if [ -f "$jacoco_csv" ]; then
        print_info "覆盖率摘要："
        
        # 解析CSV文件获取总体覆盖率
        local instruction_covered=$(tail -n 1 "$jacoco_csv" | cut -d',' -f4)
        local instruction_missed=$(tail -n 1 "$jacoco_csv" | cut -d',' -f3)
        local branch_covered=$(tail -n 1 "$jacoco_csv" | cut -d',' -f6)
        local branch_missed=$(tail -n 1 "$jacoco_csv" | cut -d',' -f5)
        
        if [ -n "$instruction_covered" ] && [ -n "$instruction_missed" ]; then
            local total_instructions=$((instruction_covered + instruction_missed))
            local instruction_percentage=$((instruction_covered * 100 / total_instructions))
            echo "  📈 指令覆盖率: ${instruction_percentage}% (${instruction_covered}/${total_instructions})"
        fi
        
        if [ -n "$branch_covered" ] && [ -n "$branch_missed" ]; then
            local total_branches=$((branch_covered + branch_missed))
            if [ $total_branches -gt 0 ]; then
                local branch_percentage=$((branch_covered * 100 / total_branches))
                echo "  🌿 分支覆盖率: ${branch_percentage}% (${branch_covered}/${total_branches})"
            fi
        fi
    fi
}

# 主函数
main() {
    local unit_only=false
    local include_integration=false
    local generate_site=false
    local open_after=false
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --unit-only)
                unit_only=true
                shift
                ;;
            --integration)
                include_integration=true
                shift
                ;;
            --site)
                generate_site=true
                shift
                ;;
            --open)
                open_after=true
                shift
                ;;
            -h|--help)
                echo "用法: $0 [选项]"
                echo "选项:"
                echo "  --unit-only     只运行单元测试"
                echo "  --integration   包含集成测试"
                echo "  --site          生成完整的站点报告"
                echo "  --open          生成后自动打开报告"
                echo "  -h, --help      显示此帮助信息"
                exit 0
                ;;
            *)
                print_error "未知选项: $1"
                exit 1
                ;;
        esac
    done
    
    print_info "开始生成测试覆盖率报告..."
    
    # 检查环境
    check_maven
    
    # 清理
    clean_reports
    
    # 运行测试
    run_unit_tests
    
    if [ "$include_integration" = true ] && [ "$unit_only" = false ]; then
        run_integration_tests
    fi
    
    # 生成报告
    generate_jacoco_report
    
    if [ "$generate_site" = true ]; then
        generate_site_report
    fi
    
    # 显示结果
    show_report_locations
    show_coverage_summary
    
    # 打开报告
    if [ "$open_after" = true ]; then
        open_report
    fi
    
    print_success "所有任务完成！"
}

# 运行主函数
main "$@"