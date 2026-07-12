<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/buildingadmin/students">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索学号/姓名/班级">
            <select name="status">
                <option value="">全部</option>
                <option value="IN" ${status eq 'IN' ? 'selected' : ''}>已入住</option>
                <option value="OUT" ${status eq 'OUT' ? 'selected' : ''}>未入住</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>学生</th><th>学号</th><th>班级/专业</th><th>宿舍</th><th>电话</th><th>状态</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="s">
            <tr>
                <td><strong>${s.realName}</strong><div class="muted">${s.gender}</div></td>
                <td>${s.studentNo}</td>
                <td>${s.className}<div class="muted">${s.major}</div></td>
                <td>${s.buildingName}-${s.roomNumber}</td>
                <td>${s.phone}</td>
                <td><span class="badge status-${s.status}">${s.statusName}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

