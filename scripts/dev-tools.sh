#!/bin/bash

# 开发工具脚本
# 提供常用的开发任务快捷命令

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# 显示帮助信息
show_help() {
    echo "🛠️  PostgreSQL Template 开发工具"
    echo "================================"
    echo ""
    echo "用法: $0 <命令> [参数]"
    echo ""
    echo "可用命令:"
    echo "  clean          - 清理构建文件和缓存"
    echo "  build          - 构建项目"
    echo "  test           - 运行测试"
    echo "  test-coverage  - 运行测试并生成覆盖率报告"
    echo "  format         - 格式化代码"
    echo "  lint           - 代码质量检查"
    echo "  deps           - 检查依赖更新"
    echo "  db-reset       - 重置数据库"
    echo "  db-migrate     - 运行数据库迁移"
    echo "  logs           - 查看应用日志"
    echo "  api-docs       - 打开API文档"
    echo "  metrics        - 查看应用指标"
    echo "  profile        - 性能分析"
    echo ""
    echo "示例:"
    echo "  $0 clean"
    echo "  $0 test"
    echo "  $0 db-reset"
}

# 清理构建文件
clean() {
    print_info "清理构建文件和缓存..."
    cd "$PROJECT_ROOT"
    
    # 清理Maven构建文件
    mvn clean
    
    # 清理日志文件
    rm -rf logs/
    
    # 清理临时文件
    find . -name "*.tmp" -delete
    find . -name "*.log" -delete
    
    print_success "清理完成"
}

# 构建项目
build() {
    print_info "构建项目..."
    cd "$PROJECT_ROOT"
    
    mvn compile
    
    print_success "构建完成"
}

# 运行测试
test() {
    print_info "运行测试..."
    cd "$PROJECT_ROOT"
    
    mvn test
    
    print_success "测试完成"
}

# 运行测试并生成覆盖率报告
test_coverage() {
    print_info "运行测试并生成覆盖率报告..."
    cd "$PROJECT_ROOT"
    
    mvn clean test jacoco:report
    
    # 打开覆盖率报告
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open target/site/jacoco/index.html
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open target/site/jacoco/index.html
    fi
    
    print_success "测试覆盖率报告已生成"
}

# 格式化代码
format() {
    print_info "格式化代码..."
    cd "$PROJECT_ROOT"
    
    # 使用ktlint格式化Kotlin代码
    if command -v ktlint &> /dev/null; then
        ktlint -F "src/**/*.kt"
        print_success "代码格式化完成"
    else
        print_warning "ktlint未安装，跳过代码格式化"
        print_info "安装ktlint: brew install ktlint"
    fi
}

# 代码质量检查
lint() {
    print_info "代码质量检查..."
    cd "$PROJECT_ROOT"
    
    # 使用ktlint检查代码风格
    if command -v ktlint &> /dev/null; then
        ktlint "src/**/*.kt"
        print_success "代码质量检查完成"
    else
        print_warning "ktlint未安装，跳过代码检查"
        print_info "安装ktlint: brew install ktlint"
    fi
}

# 检查依赖更新
deps() {
    print_info "检查依赖更新..."
    cd "$PROJECT_ROOT"
    
    mvn versions:display-dependency-updates
    mvn versions:display-plugin-updates
    
    print_success "依赖检查完成"
}

# 重置数据库
db_reset() {
    print_info "重置数据库..."
    cd "$PROJECT_ROOT"
    
    if [ -f "database/manage-db.sh" ]; then
        ./database/manage-db.sh reset
        print_success "数据库重置完成"
    else
        print_error "数据库管理脚本不存在"
    fi
}

# 运行数据库迁移
db_migrate() {
    print_info "运行数据库迁移..."
    cd "$PROJECT_ROOT"
    
    # 这里可以添加Flyway或Liquibase迁移命令
    print_warning "数据库迁移功能待实现"
}

# 查看应用日志
logs() {
    print_info "查看应用日志..."
    cd "$PROJECT_ROOT"
    
    if [ -f "logs/app.log" ]; then
        tail -f logs/app.log
    elif [ -f "app.log" ]; then
        tail -f app.log
    else
        print_warning "未找到日志文件"
    fi
}

# 打开API文档
api_docs() {
    print_info "打开API文档..."
    
    local url="http://localhost:8080/api/swagger-ui.html"
    
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open "$url"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open "$url"
    else
        print_info "请在浏览器中打开: $url"
    fi
    
    print_success "API文档已打开"
}

# 查看应用指标
metrics() {
    print_info "查看应用指标..."
    
    local url="http://localhost:8080/api/actuator/metrics"
    
    if command -v curl &> /dev/null; then
        curl -s "$url" | python -m json.tool 2>/dev/null || curl -s "$url"
    else
        print_info "请在浏览器中打开: $url"
    fi
}

# 性能分析
profile() {
    print_info "启动性能分析..."
    cd "$PROJECT_ROOT"
    
    print_warning "性能分析功能待实现"
    print_info "建议使用JProfiler、VisualVM或async-profiler"
}

# 主函数
main() {
    case "${1:-help}" in
        "clean")
            clean
            ;;
        "build")
            build
            ;;
        "test")
            test
            ;;
        "test-coverage")
            test_coverage
            ;;
        "format")
            format
            ;;
        "lint")
            lint
            ;;
        "deps")
            deps
            ;;
        "db-reset")
            db_reset
            ;;
        "db-migrate")
            db_migrate
            ;;
        "logs")
            logs
            ;;
        "api-docs")
            api_docs
            ;;
        "metrics")
            metrics
            ;;
        "profile")
            profile
            ;;
        "help"|*)
            show_help
            ;;
    esac
}

main "$@"