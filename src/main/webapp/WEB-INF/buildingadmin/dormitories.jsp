<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel">
    <div class="toolbar">
        <form class="filter-form" method="get" action="${ctx}/buildingadmin/dormitories">
            <input type="text" name="keyword" value="${keyword}" placeholder="搜索房间/楼栋">
            <select name="status">
                <option value="">全部状态</option>
                <option value="AVAILABLE" ${status eq 'AVAILABLE' ? 'selected' : ''}>可入住</option>
                <option value="FULL" ${status eq 'FULL' ? 'selected' : ''}>已满</option>
                <option value="REPAIR" ${status eq 'REPAIR' ? 'selected' : ''}>维修中</option>
            </select>
            <button class="ghost-btn" type="submit">筛选</button>
        </form>
    </div>
    <div class="room-grid">
        <c:forEach items="${roomCards}" var="d">
            <article class="room-card status-${d.status}">
                <div class="room-top">
                    <div>
                        <div class="room-name">${d.roomNumber}</div>
                        <div class="room-building">${d.buildingName} · ${d.floor}层</div>
                    </div>
                    <span class="badge status-${d.status}">${d.statusName}</span>
                </div>
                <div class="room-capacity">
                    <strong>${d.currentCount}/${d.capacity}</strong>
                    <div class="bar"><span style="width:${d.useRate}%"></span></div>
                </div>
            </article>
        </c:forEach>
    </div>
</section>

<section class="panel" style="margin-top:18px;">
    <div class="panel-head">
        <div>
            <h2>宿舍明细</h2>
            <p>与房态板对应的表格数据</p>
        </div>
    </div>
    <table class="data-table">
        <thead>
        <tr><th>宿舍</th><th>楼栋</th><th>入住</th><th>空床</th><th>状态</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="d">
            <tr>
                <td><strong>${d.roomNumber}</strong></td>
                <td>${d.buildingName} · ${d.floor}层</td>
                <td>${d.currentCount}/${d.capacity}</td>
                <td>${d.vacancy}</td>
                <td><span class="badge status-${d.status}">${d.statusName}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

