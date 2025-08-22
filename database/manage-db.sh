#!/bin/bash

# PostgreSQL 数据库管理脚本

DB_CONTAINER="pg-template-db"
DB_USER="postgres"
DB_PASSWORD="password"

function show_help() {
    echo "PostgreSQL 数据库管理工具"
    echo "========================"
    echo ""
    echo "用法: $0 [命令] [参数]"
    echo ""
    echo "命令:"
    echo "  start           启动数据库容器"
    echo "  stop            停止数据库容器"
    echo "  restart         重启数据库容器"
    echo "  status          查看数据库状态"
    echo "  logs            查看数据库日志"
    echo "  connect [db]    连接到数据库 (默认: pg_template)"
    echo "  list-dbs        列出所有数据库"
    echo "  backup [db]     备份数据库"
    echo "  restore [file]  恢复数据库"
    echo "  reset           重置所有数据库"
    echo "  help            显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 start                    # 启动数据库"
    echo "  $0 connect pg_template_dev  # 连接开发数据库"
    echo "  $0 backup pg_template       # 备份生产数据库"
}

function start_db() {
    echo "🚀 启动 PostgreSQL 数据库..."
    docker-compose up -d postgres
    
    echo "⏳ 等待数据库启动..."
    for i in {1..30}; do
        if docker exec $DB_CONTAINER pg_isready -U $DB_USER > /dev/null 2>&1; then
            echo "✅ 数据库已启动"
            return 0
        fi
        echo "等待中... ($i/30)"
        sleep 2
    done
    echo "❌ 数据库启动失败"
    return 1
}

function stop_db() {
    echo "🛑 停止 PostgreSQL 数据库..."
    docker-compose stop postgres
    echo "✅ 数据库已停止"
}

function restart_db() {
    echo "🔄 重启 PostgreSQL 数据库..."
    stop_db
    start_db
}

function show_status() {
    echo "📊 数据库状态:"
    docker-compose ps postgres
    echo ""
    if docker exec $DB_CONTAINER pg_isready -U $DB_USER > /dev/null 2>&1; then
        echo "✅ 数据库运行正常"
        echo ""
        echo "📋 数据库列表:"
        list_databases
    else
        echo "❌ 数据库未运行"
    fi
}

function show_logs() {
    echo "📝 数据库日志:"
    docker-compose logs -f postgres
}

function connect_db() {
    local db_name=${1:-pg_template}
    echo "🔗 连接到数据库: $db_name"
    docker exec -it $DB_CONTAINER psql -U $DB_USER -d $db_name
}

function list_databases() {
    docker exec $DB_CONTAINER psql -U $DB_USER -c "\l"
}

function backup_db() {
    local db_name=${1:-pg_template}
    local backup_file="backup_${db_name}_$(date +%Y%m%d_%H%M%S).sql"
    
    echo "💾 备份数据库: $db_name"
    docker exec $DB_CONTAINER pg_dump -U $DB_USER $db_name > $backup_file
    echo "✅ 备份完成: $backup_file"
}

function restore_db() {
    local backup_file=$1
    if [ -z "$backup_file" ]; then
        echo "❌ 请指定备份文件"
        return 1
    fi
    
    if [ ! -f "$backup_file" ]; then
        echo "❌ 备份文件不存在: $backup_file"
        return 1
    fi
    
    echo "📥 恢复数据库从: $backup_file"
    docker exec -i $DB_CONTAINER psql -U $DB_USER < $backup_file
    echo "✅ 恢复完成"
}

function reset_databases() {
    echo "⚠️  警告: 这将删除所有数据库数据!"
    read -p "确定要继续吗? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🗑️  重置数据库..."
        docker-compose down -v
        docker-compose up -d postgres
        echo "✅ 数据库已重置"
    else
        echo "❌ 操作已取消"
    fi
}

# 主逻辑
case "${1:-help}" in
    "start")
        start_db
        ;;
    "stop")
        stop_db
        ;;
    "restart")
        restart_db
        ;;
    "status")
        show_status
        ;;
    "logs")
        show_logs
        ;;
    "connect")
        connect_db $2
        ;;
    "list-dbs")
        list_databases
        ;;
    "backup")
        backup_db $2
        ;;
    "restore")
        restore_db $2
        ;;
    "reset")
        reset_databases
        ;;
    "help"|*)
        show_help
        ;;
esac