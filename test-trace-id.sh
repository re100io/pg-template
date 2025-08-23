#!/bin/bash

echo "=== 测试 Trace ID 功能 ==="
echo

# 测试1: 基本请求（自动生成trace ID）
echo "1. 测试自动生成 trace ID:"
echo "请求: GET /api/trace/test"
response=$(curl -s -i http://localhost:7001/api/trace/test)
echo "响应头部:"
echo "$response" | grep -E "X-(Trace|Request)-Id"
echo "响应体:"
echo "$response" | tail -1 | jq .
echo

# 测试2: 传递自定义trace ID
echo "2. 测试传递自定义 trace ID:"
custom_trace_id="custom-trace-123456"
echo "请求: GET /api/trace/test (X-Trace-Id: $custom_trace_id)"
response=$(curl -s -i -H "X-Trace-Id: $custom_trace_id" http://localhost:7001/api/trace/test)
echo "响应头部:"
echo "$response" | grep -E "X-(Trace|Request)-Id"
echo "响应体:"
echo "$response" | tail -1 | jq .
echo

# 测试3: 日志测试
echo "3. 测试日志输出:"
echo "请求: GET /api/trace/log-test"
response=$(curl -s -i http://localhost:7001/api/trace/log-test)
echo "响应头部:"
echo "$response" | grep -E "X-(Trace|Request)-Id"
echo "响应体:"
echo "$response" | tail -1 | jq .
echo

# 测试4: 用户接口测试
echo "4. 测试用户接口 trace ID:"
echo "请求: GET /api/users"
response=$(curl -s -i http://localhost:7001/api/users)
echo "响应头部:"
echo "$response" | grep -E "X-(Trace|Request)-Id"
echo

echo "=== 测试完成 ==="
echo "请检查应用日志文件 logs/application.log 查看 trace ID 是否正确显示"