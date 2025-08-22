#!/bin/bash

echo "🚀 启动 PostgreSQL Template Backend"
echo "=================================="

# 设置环境变量
PROFILE=${1:-dev}
PORT=${2:-8080}

# 检查是否有 Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查是否有 Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven 未安装，请先安装 Maven"
    exit 1
fi

echo "📦 启动 PostgreSQL 数据库..."

# 检查是否有本地 PostgreSQL
if command -v psql &> /dev/null && pg_isready -h localhost -U postgres > /dev/null 2>&1; then
    echo "✅ 使用本地 PostgreSQL"
    DB_TYPE="local"
elif command -v docker &> /dev/null && docker info > /dev/null 2>&1; then
    echo "🐳 使用 Docker PostgreSQL"
    docker-compose up -d postgres
    DB_TYPE="docker"
    
    echo "⏳ 等待数据库启动..."
    for i in {1..30}; do
        if docker exec pg-template-db pg_isready -U postgres > /dev/null 2>&1; then
            echo "✅ 数据库已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            echo "❌ 数据库启动超时"
            echo "💡 提示: 尝试运行 './database/setup-local-postgres.sh' 安装本地 PostgreSQL"
            exit 1
        fi
        echo "等待数据库启动... ($i/30)"
        sleep 2
    done
else
    echo "❌ 未找到可用的 PostgreSQL"
    echo "💡 请选择以下方式之一："
    echo "   1. 安装并启动 Docker，然后重新运行此脚本"
    echo "   2. 运行 './database/setup-local-postgres.sh' 安装本地 PostgreSQL"
    exit 1
fi

# 设置正确的 Java 环境
if [ -d "/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home"
elif [ -d "/usr/lib/jvm/java-21-openjdk" ]; then
    export JAVA_HOME="/usr/lib/jvm/java-21-openjdk"
fi

echo "🔨 构建项目..."
echo "使用 Java: $(java -version 2>&1 | head -n 1)"

# 直接使用 Maven 运行，避免打包问题
echo "🎯 启动应用程序 (环境: $PROFILE)..."
case $PROFILE in
    "dev")
        echo "开发环境 - 访问地址: http://localhost:$PORT/api"
        echo "数据库: pg_template_dev"
        ;;
    "prod")
        echo "生产环境 - 访问地址: http://localhost:$PORT/api"
        echo "数据库: pg_template"
        ;;
    "test")
        echo "测试环境 - 访问地址: http://localhost:$PORT/api"
        echo "数据库: pg_template_test"
        ;;
esac

echo "按 Ctrl+C 停止应用"
echo ""
echo "🚀 启动中..."

mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE -Dspring-boot.run.arguments="--server.port=$PORT"