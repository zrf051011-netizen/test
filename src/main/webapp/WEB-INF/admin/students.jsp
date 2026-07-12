<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/header.jsp" %>
<section class="panel student-management-panel">
    <div class="student-page-head">
        <div>
            <h2>学生档案</h2>
            <p>当前筛选结果 <strong>${pageBean.totalCount}</strong> 条</p>
        </div>
        <span class="student-record-count"><strong>${pageBean.totalCount}</strong><small>条记录</small></span>
    </div>
    <div class="toolbar">
        <form class="filter-form student-filter-form" method="get" action="${ctx}/admin/students">
            <label class="student-search-field">
                <svg aria-hidden="true"><use href="#icon-search"/></svg>
                <input type="search" name="keyword" value="${keyword}" placeholder="搜索学号、姓名、班级或手机" aria-label="搜索学生">
            </label>
            <input type="hidden" name="status" value="${status}" data-student-status-input>
            <div class="student-status-tabs" aria-label="入住状态筛选">
                <button class="${empty status ? 'active' : ''}" type="button" data-student-status="">全部</button>
                <button class="${status eq 'IN' ? 'active' : ''}" type="button" data-student-status="IN">已入住</button>
                <button class="${status eq 'OUT' ? 'active' : ''}" type="button" data-student-status="OUT">未入住</button>
            </div>
            <button class="student-search-btn" type="submit" aria-label="执行搜索" title="搜索">
                <svg aria-hidden="true"><use href="#icon-search"/></svg>
            </button>
        </form>
        <div class="toolbar-actions">
            <a class="ghost-btn" href="${ctx}/admin/students/template">
                <svg class="action-icon" aria-hidden="true"><use href="#icon-download"/></svg>
                下载模板
            </a>
            <button class="ghost-btn" type="button" data-open-student-import aria-controls="studentImportForm" aria-expanded="false">
                <svg class="action-icon" aria-hidden="true"><use href="#icon-upload"/></svg>
                导入 Excel
            </button>
            <a class="primary-btn" href="${ctx}/admin/students/edit">
                <svg class="action-icon" aria-hidden="true"><use href="#icon-plus"/></svg>
                新增学生
            </a>
        </div>
    </div>
    <form class="excel-import-form" id="studentImportForm" method="post" action="${ctx}/admin/students/import" enctype="multipart/form-data" hidden>
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <label class="excel-file-picker" data-import-picker>
            <input type="file" name="excelFile" accept=".xlsx,.xls" data-import-file required>
            <svg aria-hidden="true"><use href="#icon-file-sheet"/></svg>
            <span>
                <strong data-import-file-name>选择 Excel 文件</strong>
                <small>.xlsx / .xls，最大 5MB</small>
            </span>
        </label>
        <div class="excel-import-actions">
            <button class="primary-btn" type="submit" data-import-submit disabled>开始导入</button>
            <button class="ghost-btn" type="button" data-close-student-import>取消</button>
        </div>
    </form>
    <div class="student-table-scroll">
    <table class="data-table student-table">
        <thead>
        <tr><th scope="col">学生</th><th scope="col">学号</th><th scope="col">班级/专业</th><th scope="col">宿舍</th><th scope="col">手机</th><th scope="col">状态</th><th scope="col">操作</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${pageBean.list}" var="s">
            <tr>
                <td><strong>${s.realName}</strong><div class="muted">${s.gender}</div></td>
                <td><span class="student-number">${s.studentNo}</span></td>
                <td>${s.className}<div class="muted">${s.major}</div></td>
                <td>
                    <c:choose>
                        <c:when test="${empty s.roomNumber}">未分配</c:when>
                        <c:otherwise>${s.buildingName}-${s.roomNumber}</c:otherwise>
                    </c:choose>
                </td>
                <td><span class="student-phone">${s.phone}</span></td>
                <td><span class="badge status-${s.status}">${s.statusName}</span></td>
                <td class="row-actions">
                    <a class="row-icon-btn edit" href="${ctx}/admin/students/edit?id=${s.id}" aria-label="编辑 ${s.realName}" title="编辑">
                        <svg aria-hidden="true"><use href="#icon-edit"/></svg>
                    </a>
                    <form class="inline-action-form" method="post" action="${ctx}/admin/students/delete">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="id" value="${s.id}">
                        <button class="row-icon-btn delete" type="submit" data-confirm="确定删除该学生？" aria-label="删除 ${s.realName}" title="删除">
                            <svg aria-hidden="true"><use href="#icon-trash"/></svg>
                        </button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty pageBean.list}">
            <tr class="student-empty-row">
                <td colspan="7"><strong>没有找到学生记录</strong><span>请调整搜索关键词或入住状态</span></td>
            </tr>
        </c:if>
        </tbody>
    </table>
    </div>
    <%@ include file="/WEB-INF/common/pagination.jsp" %>
</section>
<%@ include file="/WEB-INF/common/footer.jsp" %>

