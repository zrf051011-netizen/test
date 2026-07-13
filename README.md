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

## 课程设计交付物

- [`课程设计报告书.docx`](./课程设计报告书.docx)：按课程模板完成的设计报告。
- [`宿舍管理系统课程设计答辩-高级版.html`](./宿舍管理系统课程设计答辩-高级版.html)：推荐使用的动态 H5 答辩稿，支持翻页、全屏、自动播放和动态效果。
- [`宿舍管理系统课程设计答辩-高级版.pptx`](./宿舍管理系统课程设计答辩-高级版.pptx)：与 H5 内容一致的 PowerPoint 兼容版。
- [`宿舍管理系统课程设计答辩.pptx`](./宿舍管理系统课程设计答辩.pptx)：较轻量的普通版答辩稿，可作为备用文件。
- [`new_pic/`](./new_pic/)：当前系统重构后的页面截图，H5 答辩稿通过相对路径引用该目录。

使用 H5 答辩稿时，请保持 HTML 文件和 `new_pic` 目录的相对位置不变。推荐使用最新版 Chrome、Edge 或其他 Chromium 浏览器打开。

## 项目结构

```text
src/                 Java、JSP、CSS 和原生 JavaScript 源码
sql/                 MySQL 初始化脚本
scripts/             Windows 环境辅助脚本
report_assets/       课程报告使用的图文素材
report-tools/        报告生成辅助工具
new_pic/             重构后的系统界面截图
课程设计报告书.docx     最终课程设计报告
宿舍管理系统课程设计答辩-高级版.html
宿舍管理系统课程设计答辩-高级版.pptx
```

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

- 校园背景与液态玻璃登录页
- 深蓝、暖白与金色的登录视觉体系
- 深色侧边栏
- 浅色 SaaS 内容区
- 统计卡片
- 楼栋入住率进度条
- 宿舍房态卡片
- 状态标签
- 统一表格和表单样式

楼栋管理员端的“房态板”是视觉重点，用来区分普通表格式课设。
