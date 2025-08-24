#!/bin/bash

echo "🎨 测试 Spring Boot Banner 功能"
echo

# 测试默认banner
echo "1. 测试默认banner (banner.txt):"
echo "文件内容预览:"
head -10 src/main/resources/banner.txt
echo

# 测试开发环境banner
echo "2. 测试开发环境banner (banner-dev.txt):"
echo "文件内容预览:"
head -10 src/main/resources/banner-dev.txt
echo

# 测试生产环境banner
echo "3. 测试生产环境banner (banner-prod.txt):"
echo "文件内容预览:"
head -10 src/main/resources/banner-prod.txt
echo

# 检查配置文件
echo "4. 检查banner配置:"
echo "application.properties:"
grep -E "banner|spring.application.name" src/main/resources/application.properties
echo

echo "application-dev.properties:"
grep -E "banner" src/main/resources/application-dev.properties
echo

echo "application-prod.properties:"
grep -E "banner" src/main/resources/application-prod.properties
echo

echo "✅ Banner文件和配置检查完成！"
echo "💡 启动应用时将根据环境自动选择对应的banner"