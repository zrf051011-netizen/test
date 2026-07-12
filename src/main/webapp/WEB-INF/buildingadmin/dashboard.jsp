<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>

<section class="stats-grid">
    <article class="stat-card accent-blue">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-building"/></svg></span>
        <span>负责楼栋</span>
        <strong>${stats.buildingCount}</strong>
        <small>按当前楼管权限统计</small>
    </article>
    <article class="stat-card accent-cyan">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-bed"/></svg></span>
        <span>空闲床位</span>
        <strong>${stats.bedCount - stats.usedBedCount}</strong>
        <small>${stats.usedBedCount}/${stats.bedCount} 床位已使用</small>
    </article>
    <article class="stat-card accent-green">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-occupancy"/></svg></span>
        <span>入住率</span>
        <strong>${stats.occupancyRate}%</strong>
        <small>${stats.dormitoryCount} 间宿舍纳入统计</small>
    </article>
    <article class="stat-card accent-amber">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-repair"/></svg></span>
        <span>待处理报修</span>
        <strong>${stats.repairPendingCount}</strong>
        <small>处理中 ${stats.repairDoingCount} 条</small>
    </article>
</section>

<section class="dashboard-grid">
    <div class="panel">
        <div class="panel-head">
            <div>
                <h2>房态速览</h2>
                <p>颜色区分空闲、已满和维修中</p>
            </div>
            <a class="link-btn" href="${ctx}/buildingadmin/dormitories">查看全部</a>
        </div>
        <div class="room-grid dense-room-grid">
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
    </div>

    <div class="side-stack">
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2>最近报修</h2>
                    <p>需要关注的维修问题</p>
                </div>
            </div>
            <table class="data-table compact-table">
                <tbody>
                <c:forEach items="${latestRepairs}" var="r">
                    <tr>
                        <td>
                            <strong>${r.roomNumber} · ${r.repairType}</strong>
                            <div class="muted">${r.reporterName}</div>
                        </td>
                        <td><span class="badge status-${r.status}">${r.statusName}</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2>最近入住记录</h2>
                    <p>本楼栋办理流水</p>
                </div>
            </div>
            <table class="data-table record-table">
                <thead>
                <tr><th>学生姓名</th><th>房间</th><th>时间</th><th>操作人</th></tr>
                </thead>
                <tbody>
                <c:forEach items="${latestRecords}" var="rec">
                    <tr>
                        <td><strong>${rec.studentName}</strong></td>
                        <td>${empty rec.toRoom ? rec.fromRoom : rec.toRoom}</td>
                        <td><fmt:formatDate value="${rec.checkDate}" pattern="MM-dd HH:mm"/></td>
                        <td>${rec.operatorName}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/common/footer.jsp" %>
