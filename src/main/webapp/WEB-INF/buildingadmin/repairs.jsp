<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/buildingadmin/repairs">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索宿舍/学生/类型">
            <select name="status">
                <option value="">全部状态</option>
                <option value="PENDING" ${status eq 'PENDING' ? 'selected' : ''}>待处理</option>
                <option value="DOING" ${status eq 'DOING' ? 'selected' : ''}>处理中</option>
                <option value="DONE" ${status eq 'DONE' ? 'selected' : ''}>已完成</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>报修位置</th><th>类型</th><th>描述</th><th>报修人</th><th>状态</th><th>处理</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="r">
            <tr>
                <td><strong>${r.buildingName}-${r.roomNumber}</strong><div class="muted"><fmt:formatDate value="${r.reportTime}" pattern="yyyy-MM-dd HH:mm"/></div></td>
                <td>${r.repairType}</td>
                <td>${r.description}</td>
                <td>${r.reporterName}</td>
                <td><span class="badge status-${r.status}">${r.statusName}</span></td>
                <td>
                    <c:choose>
                        <c:when test="${r.status eq 'PENDING'}">
                            <form class="filter-form repair-process-form" method="post" action="${ctx}/buildingadmin/repairs">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="id" value="${r.id}">
                                <input type="hidden" name="status" value="DOING">
                                <input type="text" name="handleRemark" value="${r.handleRemark}" placeholder="接单备注（可选）">
                                <button class="primary-btn" type="submit">开始处理</button>
                            </form>
                        </c:when>
                        <c:when test="${r.status eq 'DOING'}">
                            <form class="filter-form repair-process-form" method="post" action="${ctx}/buildingadmin/repairs">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="id" value="${r.id}">
                                <input type="hidden" name="status" value="DONE">
                                <input type="text" name="handleRemark" value="${r.handleRemark}" placeholder="填写完成说明">
                                <button class="primary-btn" type="submit">标记完成</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <strong>${empty r.handlerName ? '楼栋管理员' : r.handlerName}</strong>
                            <div class="muted">${empty r.handleRemark ? '已完成' : r.handleRemark}</div>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

