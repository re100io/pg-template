#!/bin/bash

echo "🐘 本地 PostgreSQL 设置脚本"
echo "============================"

# 检测操作系统
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    echo "检测到 macOS 系统"
    
    # 检查是否安装了 Homebrew
    if ! command -v brew &> /dev/null; then
        echo "❌ 需要安装 Homebrew"
        echo "请访问: https://brew.sh/"
        exit 1
    fi
    
    # 检查是否安装了 PostgreSQL
    if ! command -v psql &> /dev/null; then
        echo "📦 安装 PostgreSQL..."
        brew install postgresql@16
        brew services start postgresql@16
    else
        echo "✅ PostgreSQL 已安装"
    fi
    
    # 启动 PostgreSQL 服务
    echo "🚀 启动 PostgreSQL 服务..."
    brew services start postgresql@16
    
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    echo "检测到 Linux 系统"
    
    # Ubuntu/Debian
    if command -v apt-get &> /dev/null; then
        echo "📦 安装 PostgreSQL (Ubuntu/Debian)..."
        sudo apt-get update
        sudo apt-get install -y postgresql postgresql-contrib
        sudo systemctl start postgresql
        sudo systemctl enable postgresql
    
    # CentOS/RHEL
    elif command -v yum &> /dev/null; then
        echo "📦 安装 PostgreSQL (CentOS/RHEL)..."
        sudo yum install -y postgresql-server postgresql-contrib
        sudo postgresql-setup initdb
        sudo systemctl start postgresql
        sudo systemctl enable postgresql
    fi
    
else
    echo "❌ 不支持的操作系统: $OSTYPE"
    exit 1
fi

# 等待 PostgreSQL 启动
echo "⏳ 等待 PostgreSQL 启动..."
sleep 3

# 创建数据库和用户
echo "🗄️ 创建数据库..."

# 在 macOS 上，默认用户是当前用户
if [[ "$OSTYPE" == "darwin"* ]]; then
    DB_USER=$(whoami)
    
    # 创建 postgres 用户（如果不存在）
    createuser -s postgres 2>/dev/null || true
    
    # 使用 postgres 用户创建数据库
    sudo -u $DB_USER psql -c "ALTER USER postgres PASSWORD 'password';" 2>/dev/null || \
    psql -U $DB_USER -c "ALTER USER postgres PASSWORD 'password';" 2>/dev/null || \
    psql -c "ALTER USER postgres PASSWORD 'password';" 2>/dev/null || true
    
    # 创建数据库
    sudo -u $DB_USER createdb -O postgres pg_template 2>/dev/null || \
    createdb -O postgres pg_template 2>/dev/null || \
    psql -c "CREATE DATABASE pg_template OWNER postgres;" 2>/dev/null || true
    
    sudo -u $DB_USER createdb -O postgres pg_template_dev 2>/dev/null || \
    createdb -O postgres pg_template_dev 2>/dev/null || \
    psql -c "CREATE DATABASE pg_template_dev OWNER postgres;" 2>/dev/null || true
    
    sudo -u $DB_USER createdb -O postgres pg_template_test 2>/dev/null || \
    createdb -O postgres pg_template_test 2>/dev/null || \
    psql -c "CREATE DATABASE pg_template_test OWNER postgres;" 2>/dev/null || true

else
    # Linux
    sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'password';"
    sudo -u postgres createdb pg_template
    sudo -u postgres createdb pg_template_dev  
    sudo -u postgres createdb pg_template_test
fi

echo "✅ 数据库设置完成！"
echo ""
echo "📋 创建的数据库："
echo "   - pg_template (生产环境)"
echo "   - pg_template_dev (开发环境)"  
echo "   - pg_template_test (测试环境)"
echo ""
echo "🔗 连接信息："
echo "   主机: localhost"
echo "   端口: 5432"
echo "   用户: postgres"
echo "   密码: password"
echo ""
echo "🧪 测试连接："
echo "   psql -h localhost -U postgres -d pg_template_dev"