<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/admin/users">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索用户名/姓名/手机">
            <select name="role">
                <option value="">全部角色</option>
                <option value="ADMIN" ${role eq 'ADMIN' ? 'selected' : ''}>超级管理员</option>
                <option value="BUILDING_ADMIN" ${role eq 'BUILDING_ADMIN' ? 'selected' : ''}>楼栋管理员</option>
                <option value="STUDENT" ${role eq 'STUDENT' ? 'selected' : ''}>学生</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
        <a class="primary-btn" href="${ctx}/admin/users/edit">新增用户</a>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>账号</th><th>姓名</th><th>手机</th><th>角色</th><th>状态</th><th>最后登录</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="u">
            <tr>
                <td><strong>${u.username}</strong></td>
                <td>${u.realName}</td>
                <td>${u.phone}</td>
                <td><span class="badge role-${u.role}">${u.roleName}</span></td>
                <td><span class="badge status-${u.status}">${u.status == 1 ? '启用' : '禁用'}</span></td>
                <td><fmt:formatDate value="${u.lastLoginTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td class="row-actions">
                    <a class="link-btn" href="${ctx}/admin/users/edit?id=${u.id}">编辑</a>
                    <form class="inline-action-form" method="post" action="${ctx}/admin/users/delete">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="id" value="${u.id}">
                        <button class="danger-btn" type="submit" data-confirm="确定删除该用户？">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

