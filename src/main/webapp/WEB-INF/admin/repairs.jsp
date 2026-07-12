<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel repair-oversight-panel">
    <div class="panel-head repair-page-head">
        <div>
            <h2>全校报修记录</h2>
            <p>系统管理员负责全局监管，具体处理由所属楼栋管理员完成。</p>
        </div>
        <span class="student-record-count"><strong>${pageBean.totalCount}</strong><small>条记录</small></span>
    </div>
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/admin/repairs">
            <input type="search" name="keyword" value="${keyword}" placeholder="搜索楼栋、宿舍、学生或类型">
            <select name="buildingId">
                <option value="0">全部楼栋</option>
                <c:forEach items="${buildings}" var="b">
                    <option value="${b.id}" ${buildingId == b.id ? 'selected' : ''}>${b.buildingName}</option>
                </c:forEach>
            </select>
            <select name="status">
                <option value="">全部状态</option>
                <option value="PENDING" ${status eq 'PENDING' ? 'selected' : ''}>待处理</option>
                <option value="DOING" ${status eq 'DOING' ? 'selected' : ''}>处理中</option>
                <option value="DONE" ${status eq 'DONE' ? 'selected' : ''}>已完成</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
    </div>
    <div class="student-table-scroll">
        <table class="data-table repair-oversight-table">
            <thead>
            <tr>
                <th>报修位置</th><th>报修人</th><th>类型与描述</th><th>提交时间</th><th>状态</th><th>处理信息</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${pageBean.list}" var="r">
                <tr>
                    <td><strong><c:out value="${r.buildingName}"/>-<c:out value="${r.roomNumber}"/></strong></td>
                    <td><c:out value="${r.reporterName}"/></td>
                    <td><strong><c:out value="${r.repairType}"/></strong><div class="muted"><c:out value="${r.description}"/></div></td>
                    <td><fmt:formatDate value="${r.reportTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td><span class="badge status-${r.status}">${r.statusName}</span></td>
                    <td>
                        <c:choose>
                            <c:when test="${empty r.handlerName}"><span class="muted">等待楼管处理</span></c:when>
                            <c:otherwise>
                                <strong><c:out value="${r.handlerName}"/></strong>
                                <div class="muted"><c:out value="${r.handleRemark}"/></div>
                                <small><fmt:formatDate value="${r.handleTime}" pattern="yyyy-MM-dd HH:mm"/></small>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty pageBean.list}">
                <tr><td colspan="6"><div class="notice-empty"><strong>没有符合条件的报修记录</strong><span>请调整筛选条件后重试。</span></div></td></tr>
            </c:if>
            </tbody>
        </table>
    </div>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>
