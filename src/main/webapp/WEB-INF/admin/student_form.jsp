<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="form-panel">
    <div class="panel-head">
        <div>
            <h2>${empty item ? '新增学生' : '编辑学生'}</h2>
            <p>新增学生会同步创建登录账号，默认密码 admin123</p>
        </div>
    </div>
    <form class="form-grid" method="post" action="${ctx}/admin/students">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <input type="hidden" name="id" value="${item.id}">
        <label>
            <span>登录用户名</span>
            <input type="text" name="username" value="${item.username}" ${not empty item ? 'readonly' : ''} required>
        </label>
        <label>
            <span>姓名</span>
            <input type="text" name="realName" value="${item.realName}" required>
        </label>
        <label>
            <span>学号</span>
            <input type="text" name="studentNo" value="${item.studentNo}" required>
        </label>
        <label>
            <span>性别</span>
            <select name="gender">
                <option value="男" ${empty item || item.gender eq '男' ? 'selected' : ''}>男</option>
                <option value="女" ${item.gender eq '女' ? 'selected' : ''}>女</option>
            </select>
        </label>
        <label>
            <span>班级</span>
            <input type="text" name="className" value="${item.className}">
        </label>
        <label>
            <span>专业</span>
            <input type="text" name="major" value="${item.major}">
        </label>
        <label>
            <span>手机号</span>
            <input type="text" name="phone" value="${item.phone}">
        </label>
        <label>
            <span>紧急联系人</span>
            <input type="text" name="emergencyContact" value="${item.emergencyContact}">
        </label>
        <label>
            <span>紧急联系电话</span>
            <input type="text" name="emergencyPhone" value="${item.emergencyPhone}">
        </label>
        <div class="form-actions">
            <a class="ghost-btn" href="${ctx}/admin/students">返回</a>
            <button class="primary-btn" type="submit">保存</button>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

