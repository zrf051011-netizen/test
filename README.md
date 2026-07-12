# 宿舍管理系统

基于 Servlet + JSP + MySQL 的 JavaWeb 期末课设项目。

## 技术栈

- JDK 8+
- Maven
- Tomcat 9
- MySQL 8
- Servlet 4.0
- JSP / JSTL
- Commons DBUtils
- C3P0

注意：不建议使用 Tomcat 10，因为本项目使用 `javax.servlet` 包。

## IDEA 运行步骤

1. 用 IDEA 打开当前目录。
2. 等待 Maven 下载依赖。
3. 在 MySQL 中执行：

   ```sql
   source sql/dormitory_system.sql;
   ```

   或者直接复制 `sql/dormitory_system.sql` 到数据库客户端执行。

4. 修改数据库账号密码：

   ```text
   src/main/resources/c3p0-config.xml
   ```

   默认配置：

   ```text
   user=root
   password=123456
   database=dormitory_system
   ```

5. IDEA 配置 Tomcat 9，Deployment 选择当前 Maven Web 项目，Application context 建议：

   ```text
   /dormitory-system
   ```

6. 启动后访问：

   ```text
   http://localhost:8080/dormitory-system/
   ```

## 测试账号

所有初始账号密码都是：

```text
admin123
```

| 角色 | 用户名 |
| --- | --- |
| 超级管理员 | admin |
| 楼栋管理员 | zhanglou |
| 楼栋管理员 | wanglou |
| 学生 | lisi |
| 学生 | wangwu |

## 页面风格

项目采用现代后台风格：

- 深色侧边栏
- 浅色 SaaS 内容区
- 统计卡片
- 楼栋入住率进度条
- 宿舍房态卡片
- 状态标签
- 统一表格和表单样式

楼栋管理员端的“房态板”是视觉重点，用来区分普通表格式课设。
