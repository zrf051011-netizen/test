<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="form-panel">
    <div class="panel-head">
        <div>
            <h2>${empty item ? '新增楼栋' : '编辑楼栋'}</h2>
            <p>楼栋管理员决定楼管端可见数据范围</p>
        </div>
    </div>
    <form class="form-grid" method="post" action="${ctx}/admin/buildings">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <input type="hidden" name="id" value="${item.id}">
        <label>
            <span>楼栋名称</span>
            <input type="text" name="buildingName" value="${item.buildingName}" required>
        </label>
        <label>
            <span>楼层数</span>
            <input type="number" name="floorCount" value="${empty item ? 6 : item.floorCount}" min="1" required>
        </label>
        <label>
            <span>地址</span>
            <input type="text" name="address" value="${item.address}">
        </label>
        <label>
            <span>楼栋管理员</span>
            <select name="buildingAdminId">
                <option value="0">暂不分配</option>
                <c:forEach items="${admins}" var="a">
                    <option value="${a.id}" ${item.buildingAdminId == a.id ? 'selected' : ''}>${a.realName}</option>
                </c:forEach>
            </select>
        </label>
        <label>
            <span>状态</span>
            <select name="status">
                <option value="1" ${empty item || item.status == 1 ? 'selected' : ''}>正常</option>
                <option value="0" ${item.status == 0 ? 'selected' : ''}>停用</option>
            </select>
        </label>
        <div class="form-actions">
            <a class="ghost-btn" href="${ctx}/admin/buildings">返回</a>
            <button class="primary-btn" type="submit">保存</button>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

