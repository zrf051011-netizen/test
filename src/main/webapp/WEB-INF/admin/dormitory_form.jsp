<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="form-panel">
    <div class="panel-head">
        <div>
            <h2>${empty item ? '新增宿舍' : '编辑宿舍'}</h2>
            <p>宿舍状态会影响入住办理</p>
        </div>
    </div>
    <form class="form-grid" method="post" action="${ctx}/admin/dormitories">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <input type="hidden" name="id" value="${item.id}">
        <label>
            <span>楼栋</span>
            <select name="buildingId" required>
                <c:forEach items="${buildings}" var="b">
                    <option value="${b.id}" ${item.buildingId == b.id ? 'selected' : ''}>${b.buildingName}</option>
                </c:forEach>
            </select>
        </label>
        <label>
            <span>房间号</span>
            <input type="text" name="roomNumber" value="${item.roomNumber}" required>
        </label>
        <label>
            <span>楼层</span>
            <input type="number" name="floor" value="${empty item ? 1 : item.floor}" min="1" required>
        </label>
        <label>
            <span>床位数</span>
            <input type="number" name="capacity" value="${empty item ? 4 : item.capacity}" min="1" required>
        </label>
        <label>
            <span>状态</span>
            <select name="status">
                <option value="AVAILABLE" ${empty item || item.status eq 'AVAILABLE' ? 'selected' : ''}>可入住</option>
                <option value="FULL" ${item.status eq 'FULL' ? 'selected' : ''}>已满</option>
                <option value="REPAIR" ${item.status eq 'REPAIR' ? 'selected' : ''}>维修中</option>
            </select>
        </label>
        <div class="form-actions">
            <a class="ghost-btn" href="${ctx}/admin/dormitories">返回</a>
            <button class="primary-btn" type="submit">保存</button>
        </div>
    </form>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

