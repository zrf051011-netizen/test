<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/admin/buildings">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索楼栋/地址/楼管">
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
        <a class="primary-btn" href="${ctx}/admin/buildings/edit">新增楼栋</a>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>楼栋</th><th>楼层</th><th>地址</th><th>楼管</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="b">
            <tr>
                <td><strong>${b.buildingName}</strong></td>
                <td>${b.floorCount} 层</td>
                <td>${b.address}</td>
                <td>${empty b.adminName ? '未分配' : b.adminName}</td>
                <td><span class="badge status-${b.status}">${b.status == 1 ? '正常' : '停用'}</span></td>
                <td class="row-actions">
                    <a class="link-btn" href="${ctx}/admin/buildings/edit?id=${b.id}">编辑</a>
                    <form class="inline-action-form" method="post" action="${ctx}/admin/buildings/delete">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="id" value="${b.id}">
                        <button class="danger-btn" type="submit" data-confirm="确定删除该楼栋？">删除</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

