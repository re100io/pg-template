-- 创建应用用户（可选，用于生产环境）
-- CREATE USER app_user WITH PASSWORD 'app_password';
-- GRANT CONNECT ON DATABASE pg_template TO app_user;
-- GRANT USAGE ON SCHEMA public TO app_user;
-- GRANT CREATE ON SCHEMA public TO app_user;

-- 为开发环境插入一些示例数据
\c pg_template_dev;

-- 这里可以添加开发环境的初始数据
-- 注意：由于使用了 create-drop，这些数据会在应用启动时被重新创建