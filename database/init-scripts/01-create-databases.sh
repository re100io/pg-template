#!/bin/bash
set -e

# 创建开发和测试数据库
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- 创建开发数据库
    CREATE DATABASE pg_template_dev;
    GRANT ALL PRIVILEGES ON DATABASE pg_template_dev TO postgres;
    
    -- 创建测试数据库
    CREATE DATABASE pg_template_test;
    GRANT ALL PRIVILEGES ON DATABASE pg_template_test TO postgres;
    
    -- 显示创建的数据库
    \l
EOSQL

echo "✅ 数据库创建完成："
echo "   - pg_template (生产)"
echo "   - pg_template_dev (开发)"
echo "   - pg_template_test (测试)"