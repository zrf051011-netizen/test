<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>无权限</title>
    <link rel="stylesheet" href="${ctx}/css/app.css?v=202607101620">
</head>
<body class="login-body">
<section class="empty-state">
    <strong>403</strong>
    <h1>${empty forbiddenMessage ? '当前账号没有访问这个页面的权限' : forbiddenMessage}</h1>
    <a class="primary-btn" href="${ctx}/login">返回入口</a>
</section>
</body>
</html>

