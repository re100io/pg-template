#!/bin/bash

echo "🧪 测试驱动开发 (TDD) 工具"
echo "=========================="

# 设置颜色
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 设置 Java 环境
if [ -d "/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home"
elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
fi

function show_help() {
    echo "用法: $0 [命令] [选项]"
    echo ""
    echo "命令:"
    echo "  unit              运行单元测试"
    echo "  integration       运行集成测试"
    echo "  all               运行所有测试"
    echo "  coverage          生成测试覆盖率报告"
    echo "  watch             监视模式 - 文件变化时自动运行测试"
    echo "  clean             清理测试缓存"
    echo "  tdd [class]       TDD 模式 - 持续运行指定测试类"
    echo "  help              显示此帮助信息"
    echo ""
    echo "选项:"
    echo "  --verbose         显示详细输出"
    echo "  --parallel        并行运行测试"
    echo "  --fail-fast       遇到失败立即停止"
    echo ""
    echo "示例:"
    echo "  $0 unit                           # 运行单元测试"
    echo "  $0 integration --verbose          # 运行集成测试（详细输出）"
    echo "  $0 tdd UserServiceTest           # TDD模式运行UserServiceTest"
    echo "  $0 coverage                      # 生成覆盖率报告"
}

function run_unit_tests() {
    echo -e "${BLUE}🔬 运行单元测试...${NC}"
    
    local args=""
    if [[ "$*" == *"--verbose"* ]]; then
        args="$args -X"
    fi
    if [[ "$*" == *"--parallel"* ]]; then
        args="$args -T 1C"
    fi
    if [[ "$*" == *"--fail-fast"* ]]; then
        args="$args -Dmaven.test.failure.ignore=false"
    fi

    JAVA_HOME=$JAVA_HOME mvn $args test -Dtest="**/*Test,**/*Spec" -DexcludedGroups="integration"
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 单元测试通过${NC}"
    else
        echo -e "${RED}❌ 单元测试失败${NC}"
        return 1
    fi
}

function run_integration_tests() {
    echo -e "${BLUE}🔗 运行集成测试...${NC}"
    
    # 检查 Docker 是否运行（TestContainers 需要）
    if ! docker info > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  Docker 未运行，尝试启动本地数据库...${NC}"
        ./database/manage-db.sh start > /dev/null 2>&1
    fi
    
    local args=""
    if [[ "$*" == *"--verbose"* ]]; then
        args="$args -X"
    fi

    mvn $args verify -Dtest="**/*IT,**/*IntegrationTest" -DfailIfNoTests=false
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ 集成测试通过${NC}"
    else
        echo -e "${RED}❌ 集成测试失败${NC}"
        return 1
    fi
}

function run_all_tests() {
    echo -e "${BLUE}🧪 运行所有测试...${NC}"
    
    run_unit_tests "$@"
    local unit_result=$?
    
    run_integration_tests "$@"
    local integration_result=$?
    
    if [ $unit_result -eq 0 ] && [ $integration_result -eq 0 ]; then
        echo -e "${GREEN}🎉 所有测试通过！${NC}"
    else
        echo -e "${RED}💥 部分测试失败${NC}"
        return 1
    fi
}

function generate_coverage() {
    echo -e "${BLUE}📊 生成测试覆盖率报告...${NC}"
    
    mvn clean test jacoco:report
    
    if [ -f "target/site/jacoco/index.html" ]; then
        echo -e "${GREEN}✅ 覆盖率报告已生成${NC}"
        echo "📂 报告位置: target/site/jacoco/index.html"
        
        # 尝试在浏览器中打开报告
        if command -v open > /dev/null 2>&1; then
            open target/site/jacoco/index.html
        elif command -v xdg-open > /dev/null 2>&1; then
            xdg-open target/site/jacoco/index.html
        fi
    else
        echo -e "${RED}❌ 覆盖率报告生成失败${NC}"
        return 1
    fi
}

function watch_tests() {
    echo -e "${BLUE}👀 监视模式启动...${NC}"
    echo "文件变化时将自动运行测试，按 Ctrl+C 退出"
    
    if ! command -v fswatch > /dev/null 2>&1; then
        echo -e "${YELLOW}⚠️  fswatch 未安装，使用轮询模式${NC}"
        
        while true; do
            sleep 5
            if find src -name "*.kt" -newer .last_test_run 2>/dev/null | grep -q .; then
                touch .last_test_run
                echo -e "${YELLOW}🔄 检测到文件变化，重新运行测试...${NC}"
                run_unit_tests
            fi
        done
    else
        touch .last_test_run
        fswatch -o src | while read f; do
            echo -e "${YELLOW}🔄 检测到文件变化，重新运行测试...${NC}"
            run_unit_tests
        done
    fi
}

function tdd_mode() {
    local test_class=$1
    
    if [ -z "$test_class" ]; then
        echo -e "${RED}❌ 请指定测试类名${NC}"
        echo "示例: $0 tdd UserServiceTest"
        return 1
    fi
    
    echo -e "${BLUE}🔄 TDD 模式: $test_class${NC}"
    echo "按 Ctrl+C 退出"
    
    while true; do
        echo -e "${YELLOW}🧪 运行测试: $test_class${NC}"
        mvn test -Dtest="**/*$test_class*" -q
        
        echo ""
        echo -e "${BLUE}按 Enter 重新运行测试，或 Ctrl+C 退出...${NC}"
        read -r
    done
}

function clean_tests() {
    echo -e "${BLUE}🧹 清理测试缓存...${NC}"
    
    mvn clean
    rm -f .last_test_run
    
    echo -e "${GREEN}✅ 清理完成${NC}"
}

# 主逻辑
case "${1:-help}" in
    "unit")
        run_unit_tests "${@:2}"
        ;;
    "integration")
        run_integration_tests "${@:2}"
        ;;
    "all")
        run_all_tests "${@:2}"
        ;;
    "coverage")
        generate_coverage
        ;;
    "watch")
        watch_tests
        ;;
    "tdd")
        tdd_mode "$2"
        ;;
    "clean")
        clean_tests
        ;;
    "help"|*)
        show_help
        ;;
esac