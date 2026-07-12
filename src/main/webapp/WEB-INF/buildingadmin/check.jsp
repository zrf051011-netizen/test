<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="check-grid">
    <article class="check-card">
        <h2>办理入住</h2>
        <form class="form-grid single" method="post" action="${ctx}/buildingadmin/check">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="in">
            <label>
                <span>未入住学生</span>
                <select name="studentId" required>
                    <c:forEach items="${outStudents}" var="s">
                        <option value="${s.id}">${s.realName} · ${s.studentNo}</option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>可入住宿舍</span>
                <select name="dormitoryId" required>
                    <c:forEach items="${availableDorms}" var="d">
                        <option value="${d.id}">${d.buildingName}-${d.roomNumber}（${d.currentCount}/${d.capacity}）</option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>备注</span>
                <textarea name="remark" placeholder="例如：新生入住"></textarea>
            </label>
            <button class="primary-btn" type="submit">确认入住</button>
        </form>
    </article>
    <article class="check-card">
        <h2>办理退宿</h2>
        <form class="form-grid single" method="post" action="${ctx}/buildingadmin/check">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="out">
            <label>
                <span>已入住学生</span>
                <select name="studentId" required>
                    <c:forEach items="${inStudents}" var="s">
                        <option value="${s.id}">${s.realName} · ${s.buildingName}-${s.roomNumber}</option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>备注</span>
                <textarea name="remark" placeholder="例如：毕业退宿"></textarea>
            </label>
            <button class="primary-btn" type="submit">确认退宿</button>
        </form>
    </article>
    <article class="check-card">
        <h2>办理换宿</h2>
        <form class="form-grid single" method="post" action="${ctx}/buildingadmin/check">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="change">
            <label>
                <span>已入住学生</span>
                <select name="studentId" required>
                    <c:forEach items="${inStudents}" var="s">
                        <option value="${s.id}">${s.realName} · 当前 ${s.buildingName}-${s.roomNumber}</option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>目标宿舍</span>
                <select name="dormitoryId" required>
                    <c:forEach items="${availableDorms}" var="d">
                        <option value="${d.id}">${d.buildingName}-${d.roomNumber}（${d.currentCount}/${d.capacity}）</option>
                    </c:forEach>
                </select>
            </label>
            <label>
                <span>备注</span>
                <textarea name="remark" placeholder="例如：同专业调整"></textarea>
            </label>
            <button class="primary-btn" type="submit">确认换宿</button>
        </form>
    </article>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

