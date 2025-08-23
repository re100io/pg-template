#!/bin/bash

# 简化的测试覆盖率报告生成脚本

echo "🧪 生成测试覆盖率报告..."

# 清理之前的报告
echo "📁 清理之前的报告..."
rm -rf target/site/jacoco*
rm -rf target/jacoco*.exec

# 运行测试并生成覆盖率报告
echo "🏃 运行测试..."
mvn clean test jacoco:report -Dtest="TraceContextTest,TraceInterceptorTest,UserServiceSimpleTest" -Dspring.profiles.active=test

# 检查报告是否生成成功
if [ -f "target/site/jacoco/index.html" ]; then
    echo "✅ 覆盖率报告生成成功！"
    echo "📊 报告位置: target/site/jacoco/index.html"
    
    # 显示覆盖率摘要
    if [ -f "target/site/jacoco/jacoco.csv" ]; then
        echo "📈 覆盖率摘要:"
        # 解析CSV文件获取总体覆盖率
        tail -n 1 target/site/jacoco/jacoco.csv | awk -F',' '
        {
            instruction_covered = $4
            instruction_missed = $3
            branch_covered = $6
            branch_missed = $5
            
            if (instruction_covered + instruction_missed > 0) {
                instruction_percentage = int(instruction_covered * 100 / (instruction_covered + instruction_missed))
                printf "   指令覆盖率: %d%% (%d/%d)\n", instruction_percentage, instruction_covered, instruction_covered + instruction_missed
            }
            
            if (branch_covered + branch_missed > 0) {
                branch_percentage = int(branch_covered * 100 / (branch_covered + branch_missed))
                printf "   分支覆盖率: %d%% (%d/%d)\n", branch_percentage, branch_covered, branch_covered + branch_missed
            }
        }'
    fi
    
    # 尝试打开报告
    if command -v open &> /dev/null; then
        echo "🌐 正在打开报告..."
        open target/site/jacoco/index.html
    elif command -v xdg-open &> /dev/null; then
        echo "🌐 正在打开报告..."
        xdg-open target/site/jacoco/index.html
    else
        echo "💡 请手动打开报告文件: target/site/jacoco/index.html"
    fi
else
    echo "❌ 报告生成失败"
    exit 1
fi