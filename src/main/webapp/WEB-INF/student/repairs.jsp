<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="dashboard-grid">
    <div class="panel">
        <div class="panel-head">
            <div>
                <h2>报修记录</h2>
                <p>跟踪每一次提交后的处理状态</p>
            </div>
        </div>
        <table class="data-table">
            <thead>
            <tr><th>宿舍</th><th>类型</th><th>描述</th><th>提交时间</th><th>状态</th><th>处理备注</th></tr>
            </thead>
            <tbody>
            <c:forEach items="${pageBean.list}" var="r">
                <tr>
                    <td>${r.buildingName}-${r.roomNumber}</td>
                    <td>${r.repairType}</td>
                    <td>${r.description}</td>
                    <td><fmt:formatDate value="${r.reportTime}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td><span class="badge status-${r.status}">${r.statusName}</span></td>
                    <td>${r.handleRemark}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <%@ include file="/WEB-INF/common/pagination.jsp" %>
    </div>
    <div class="form-panel">
        <div class="panel-head">
            <div>
                <h2>提交报修</h2>
                <p>系统会自动绑定当前宿舍</p>
            </div>
        </div>
        <form class="form-grid single" method="post" action="${ctx}/student/repairs">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <label>
                <span>报修类型</span>
                <select name="repairType" required>
                    <option value="水电">水电</option>
                    <option value="家具">家具</option>
                    <option value="门锁">门锁</option>
                    <option value="网络">网络</option>
                    <option value="其他">其他</option>
                </select>
            </label>
            <label>
                <span>问题描述</span>
                <textarea name="description" placeholder="描述位置、问题、紧急程度" required></textarea>
            </label>
            <button class="primary-btn" type="submit">提交报修</button>
        </form>
    </div>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

