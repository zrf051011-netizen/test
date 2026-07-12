<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/admin/dormitories">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索宿舍/楼栋">
            <select name="buildingId">
                <option value="0">全部楼栋</option>
                <c:forEach items="${buildings}" var="b">
                    <option value="${b.id}" ${buildingId == b.id ? 'selected' : ''}>${b.buildingName}</option>
                </c:forEach>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="AVAILABLE" ${status eq 'AVAILABLE' ? 'selected' : ''}>可入住</option>
                <option value="FULL" ${status eq 'FULL' ? 'selected' : ''}>已满</option>
                <option value="REPAIR" ${status eq 'REPAIR' ? 'selected' : ''}>维修中</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
        <a class="primary-btn" href="${ctx}/admin/dormitories/edit">新增宿舍</a>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>宿舍</th><th>楼栋</th><th>楼层</th><th>入住</th><th>空床</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="d">
            <tr>
                <td><strong>${d.roomNumber}</strong></td>
                <td>${d.buildingName}</td>
                <td>${d.floor} 层</td>
                <td>${d.currentCount}/${d.capacity}</td>
                <td>${d.vacancy}</td>
                <td><span class="badge status-${d.status}">${d.statusName}</span></td>
                <td class="row-actions">
                    <a class="link-btn" href="${ctx}/admin/dormitories/edit?id=${d.id}">编辑</a>
                    <form class="inline-action-form" method="post" action="${ctx}/admin/dormitories/delete">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="id" value="${d.id}">
                        <button class="danger-btn" type="submit" data-confirm="确定删除该宿舍？">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

