<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="form-panel">
    <div class="panel-head">
        <div>
            <h2>${empty item ? '新增用户' : '编辑用户'}</h2>
            <p>新增账号默认密码为 admin123</p>
        </div>
    </div>
    <form class="form-grid" method="post" action="${ctx}/admin/users">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <input type="hidden" name="id" value="${item.id}">
        <label>
            <span>用户名</span>
            <input type="text" name="username" value="${item.username}" ${not empty item ? 'readonly' : ''} required>
        </label>
        <label>
            <span>真实姓名</span>
            <input type="text" name="realName" value="${item.realName}" required>
        </label>
        <label>
            <span>手机号</span>
            <input type="text" name="phone" value="${item.phone}">
        </label>
        <label>
            <span>角色</span>
            <select name="role">
                <option value="ADMIN" ${item.role eq 'ADMIN' ? 'selected' : ''}>超级管理员</option>
                <option value="BUILDING_ADMIN" ${item.role eq 'BUILDING_ADMIN' ? 'selected' : ''}>楼栋管理员</option>
                <option value="STUDENT" ${empty item || item.role eq 'STUDENT' ? 'selected' : ''}>学生</option>
            </select>
        </label>
        <label>
            <span>状态</span>
            <select name="status">
                <option value="1" ${empty item || item.status == 1 ? 'selected' : ''}>启用</option>
                <option value="0" ${item.status == 0 ? 'selected' : ''}>禁用</option>
            </select>
        </label>
        <div class="form-actions">
            <a class="ghost-btn" href="${ctx}/admin/users">返回</a>
            <button class="primary-btn" type="submit">保存</button>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

