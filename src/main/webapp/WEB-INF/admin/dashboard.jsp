<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>

<section class="stats-grid admin-dashboard-stats">
    <article class="stat-card admin-dashboard-stat-card accent-blue">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-student-card"/></svg></span>
        <span>学生总数</span>
        <strong>${stats.studentCount}</strong>
        <small>全部在册学生</small>
    </article>
    <article class="stat-card admin-dashboard-stat-card accent-cyan">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-bed"/></svg></span>
        <span>空闲床位</span>
        <strong>${stats.bedCount - stats.usedBedCount}</strong>
        <small>${stats.usedBedCount}/${stats.bedCount} 床位已使用</small>
    </article>
    <article class="stat-card admin-dashboard-stat-card accent-green">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-occupancy"/></svg></span>
        <span>入住率</span>
        <strong>${stats.occupancyRate}%</strong>
        <small>${stats.buildingCount} 栋楼参与统计</small>
    </article>
    <article class="stat-card admin-dashboard-stat-card accent-amber">
        <span class="stat-icon" aria-hidden="true"><svg><use href="#icon-repair"/></svg></span>
        <span>待处理报修</span>
        <strong>${stats.repairPendingCount}</strong>
        <small>处理中 ${stats.repairDoingCount} 条</small>
    </article>
</section>

<section class="dashboard-grid admin-dashboard-grid">
    <div class="panel dashboard-panel dashboard-panel--occupancy">
        <div class="panel-head">
            <div>
                <h2>楼栋入住率</h2>
                <p>按楼栋统计入住情况</p>
            </div>
            <a class="link-btn" href="${ctx}/admin/buildings">全部楼栋</a>
        </div>
        <div class="building-bars">
            <c:forEach items="${buildingStats}" var="b">
                <div class="bar-row">
                    <div class="bar-meta">
                        <strong>${b.buildingName}</strong>
                    <span class="bar-data">${b.usedCount}/${b.bedCount}</span>
                    <b class="bar-percent">${b.occupancyRate}%</b>
                    </div>
                    <div class="bar"><span style="width:${b.occupancyRate}%"></span></div>
                </div>
            </c:forEach>
        </div>
    </div>

    <div class="panel dashboard-panel dashboard-panel--repairs" id="latest-repairs">
        <div class="panel-head">
            <div>
                <h2>最新报修</h2>
                <p>优先关注待处理和处理中问题</p>
            </div>
            <a class="link-btn" href="${ctx}/admin/repairs">查看全部</a>
        </div>
        <div class="dashboard-repairs-list">
            <table class="data-table compact-table dashboard-repairs-table">
                <tbody>
                <c:forEach items="${latestRepairs}" var="r">
                    <tr>
                        <td>
                            <strong>${r.buildingName}-${r.roomNumber}</strong>
                            <div class="muted">${r.repairType} · ${r.reporterName}</div>
                        </td>
                        <td><span class="badge status-${r.status}">${r.statusName}</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="panel dashboard-panel dashboard-panel--records">
        <div class="panel-head">
            <div>
                <h2>最近入住记录</h2>
                <p>入住、退宿、换宿流水</p>
            </div>
        </div>
        <div class="dashboard-records-scroll">
            <table class="data-table record-table">
                <thead>
                <tr><th>学生姓名</th><th>楼栋-房间</th><th>入住时间</th><th>操作人</th></tr>
                </thead>
                <tbody>
                <c:forEach items="${latestRecords}" var="rec">
                    <tr>
                        <td><strong>${rec.studentName}</strong></td>
                        <td>${empty rec.toRoom ? rec.fromRoom : rec.toRoom}</td>
                        <td><fmt:formatDate value="${rec.checkDate}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>${rec.operatorName}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="panel dashboard-panel dashboard-panel--quick-actions">
        <div class="panel-head">
            <div>
                <h2>快捷操作</h2>
                <p>常用管理入口</p>
            </div>
        </div>
        <div class="quick-actions">
            <a class="tone-blue" href="${ctx}/admin/students/edit"><i aria-hidden="true"><svg><use href="#icon-user-plus"/></svg></i><strong>添加学生</strong><span>新增学生信息</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-teal" href="${ctx}/admin/dormitories"><i aria-hidden="true"><svg><use href="#icon-bed-check"/></svg></i><strong>分配宿舍</strong><span>为学生分配床位</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-amber" href="${ctx}/admin/buildings"><i aria-hidden="true"><svg><use href="#icon-building"/></svg></i><strong>楼栋管理</strong><span>管理楼栋信息</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-indigo" href="${ctx}/admin/dormitories/edit"><i aria-hidden="true"><svg><use href="#icon-bed"/></svg></i><strong>宿舍管理</strong><span>管理宿舍及床位</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-green" href="${ctx}/admin/users/edit"><i aria-hidden="true"><svg><use href="#icon-users"/></svg></i><strong>用户管理</strong><span>创建系统账号</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-slate" href="${ctx}/admin/reports/operations.xlsx"><i aria-hidden="true"><svg><use href="#icon-report"/></svg></i><strong>导出报表</strong><span>下载运营 Excel</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-gold" href="${ctx}/admin/students?import=1#studentImportForm"><i aria-hidden="true"><svg><use href="#icon-sheet-upload"/></svg></i><strong>Excel 导入</strong><span>批量创建学生档案</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
            <a class="tone-red" href="${ctx}/admin/repairs"><i aria-hidden="true"><svg><use href="#icon-repair"/></svg></i><strong>报修管理</strong><span>处理宿舍报修工单</span><svg class="quick-action-arrow" aria-hidden="true"><use href="#icon-chevron-right"/></svg></a>
        </div>
    </div>
</section>

<%@ include file="/WEB-INF/common/footer.jsp" %>
