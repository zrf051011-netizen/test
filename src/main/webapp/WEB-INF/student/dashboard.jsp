<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="student-hero">
    <div>
        <p class="eyebrow">Student Residence</p>
        <h2>${student.realName}</h2>
        <div class="info-list">
            <span>学号：${student.studentNo}</span>
            <span>班级：${student.className}</span>
            <span>专业：${student.major}</span>
            <span>联系电话：${student.phone}</span>
        </div>
    </div>
    <div class="panel">
        <div class="panel-head">
            <div>
                <h2>当前宿舍</h2>
                <p>我的住宿状态</p>
            </div>
        </div>
        <c:choose>
            <c:when test="${student.status eq 'IN'}">
                <div class="room-card status-AVAILABLE">
                    <div class="room-name">${student.buildingName}-${student.roomNumber}</div>
                    <div class="room-building">入住日期：<fmt:formatDate value="${student.checkInDate}" pattern="yyyy-MM-dd"/></div>
                    <span class="badge status-IN">已入住</span>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state" style="margin:0; box-shadow:none;">
                    <h1>暂未入住</h1>
                    <p class="muted">请联系楼栋管理员办理入住。</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<section class="dashboard-grid">
    <div class="panel">
        <div class="panel-head">
            <div>
                <h2>室友</h2>
                <p>同宿舍学生信息</p>
            </div>
        </div>
        <table class="data-table">
            <thead><tr><th>姓名</th><th>学号</th><th>班级</th><th>电话</th></tr></thead>
            <tbody>
            <c:forEach items="${roommates}" var="r">
                <tr><td><strong>${r.realName}</strong></td><td>${r.studentNo}</td><td>${r.className}</td><td>${r.phone}</td></tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="panel">
        <div class="panel-head">
            <div>
                <h2>入住流水</h2>
                <p>入住、退宿、换宿记录</p>
            </div>
        </div>
        <div class="timeline">
            <c:forEach items="${records}" var="rec">
                <div class="timeline-item">
                    <strong>${rec.typeName}</strong>
                    <span class="muted"><fmt:formatDate value="${rec.checkDate}" pattern="yyyy-MM-dd HH:mm"/> · ${rec.operatorName}</span>
                    <div class="muted">${rec.remark}</div>
                </div>
            </c:forEach>
        </div>
    </div>
</section>

<section class="panel" style="margin-top:18px;">
    <div class="panel-head">
        <div>
            <h2>最近报修</h2>
            <p>查看维修状态，也可以提交新报修</p>
        </div>
        <a class="primary-btn" href="${ctx}/student/repairs">报修管理</a>
    </div>
    <table class="data-table">
        <thead><tr><th>类型</th><th>描述</th><th>时间</th><th>状态</th></tr></thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="r">
            <tr>
                <td>${r.repairType}</td>
                <td>${r.description}</td>
                <td><fmt:formatDate value="${r.reportTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                <td><span class="badge status-${r.status}">${r.statusName}</span></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

