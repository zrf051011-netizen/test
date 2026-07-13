\# 宿舍管理系统项目规则



\## 技术栈



本项目固定使用：



\- JDK 8+

\- Maven

\- Tomcat 9

\- Servlet 4.0

\- javax.servlet

\- JSP / JSTL

\- MySQL 8

\- Commons DBUtils

\- C3P0

\- HTML

\- CSS

\- 少量原生 JavaScript



不得替换或迁移现有核心技术栈为：



\- Spring Boot

\- Tomcat 10

\- jakarta.servlet

允许在不替换 JSP / Servlet / Tomcat / Maven 主体、不改变现有业务骨架与部署方式的前提下，附加使用前端增强技术或工具链。



\## 当前任务



当前登录页面已经实现：



\- 校园背景

\- 左右双栏

\- 左侧系统介绍

\- 右侧登录表单

\- 基础玻璃拟态

\- 金色强调色



本次任务不是重新做一个登录页。



本次任务是在当前页面基础上进行渐进式视觉升级：



\- 深蓝色液态玻璃左侧面板

\- 暖白色液态玻璃登录卡片

\- 半透明玻璃输入框

\- 金色液态玻璃登录按钮

\- 边缘高光

\- 多层渐变

\- 内外阴影

\- 轻微动态高光

\- 响应式手机适配



液态玻璃效果允许使用：



\- CSS 半透明渐变

\- backdrop-filter

\- CSS 边框和阴影

\- CSS 伪元素

\- 少量原生 JavaScript

\- CSS 自定义属性

\- WebGL

\- Canvas

\- SVG displacement filter

\- 复杂光学折射

\- 原生浏览器 Shader / GLSL

\- 为视觉增强所需的本地前端资源与第三方实现思路



新增技术只能作为当前页面的渐进增强层；加载失败时必须保留可用的 CSS 毛玻璃降级，且不得接管登录业务逻辑。

\- 远程图片

\- 远程字体

\- CDN

\- 第三方前端依赖



\## 修改范围



默认只允许修改：



\- 当前登录 JSP

\- 登录页专用 CSS

\- 登录页专用原生 JavaScript

\- 必要的本地 SVG 图标

\- 登录页专用 Canvas / WebGL Shader

\- 必要的本地位移贴图或光学纹理



未经明确批准，不得修改：



\- LoginServlet

\- Service

\- DAO

\- Filter

\- web.xml

\- pom.xml

\- 数据库结构

\- 数据库数据

\- 登录 URL

\- form action

\- form method

\- 用户名参数名

\- 密码参数名

\- Session 属性名

\- 登录成功跳转地址

\- 其他业务页面



\## JSP 规则



\- JSP 只负责页面显示

\- 禁止在 JSP 中访问数据库

\- 禁止在 JSP 中编写 SQL

\- 禁止新增 Java scriptlet

\- 禁止新增 <% ... %>

\- 优先使用 JSTL 和 EL

\- 正确处理 contextPath

\- 不回显密码

\- 不向页面输出数据库异常或 Java 堆栈



\## 登录安全规则



\- 页面不得公开测试账号和密码

\- 用户名不得默认填入 admin

\- 密码不得设置 value

\- JavaScript 不得校验账号密码

\- JavaScript 不得保存账号密码

\- 登录失败页面统一显示“用户名或密码错误”

\- 不得在 Session 中保存密码或 passwordHash

\- 所有 SQL 应使用 DBUtils 参数化查询

\- 前端隐藏按钮不能替代服务端权限检查



\## 工作方式



开始修改前必须：



1\. 先阅读现有代码

2\. 找到实际登录 JSP、CSS、JavaScript 和后端流程

3\. 输出分析和修改计划

4\. 等待实施指令



用户要求“只分析”时，禁止修改文件。



只修改当前任务直接相关的文件。



禁止：



\- 删除用户文件

\- 进行无关重构

\- 自动修改数据库

\- 执行 DROP 或 TRUNCATE

\- 自动安装依赖

\- 自动提交 Git

\- 执行 git push

\- 执行 git reset --hard

\- 执行 git clean -fd



\## 验证



修改完成后执行：



mvn clean package



完成后必须报告：



1\. 修改文件列表

2\. 每个文件的修改内容

3\. 是否修改后端 Java 文件

4\. 是否修改 pom.xml

5\. 是否新增依赖

6\. 是否修改数据库

7\. Maven 构建结果

8\. Tomcat 9 人工验证步骤

9\. 尚未验证的事项



所有回复使用中文。

