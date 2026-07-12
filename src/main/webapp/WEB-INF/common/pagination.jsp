<c:if test="${pageBean.totalPage > 1}">
    <c:url var="previousPageUrl" value="${pageContext.request.servletPath}">
        <c:param name="page" value="${pageBean.page - 1}"/>
        <c:param name="keyword" value="${param.keyword}"/>
        <c:param name="status" value="${param.status}"/>
        <c:param name="buildingId" value="${param.buildingId}"/>
        <c:param name="role" value="${param.role}"/>
    </c:url>
    <c:url var="nextPageUrl" value="${pageContext.request.servletPath}">
        <c:param name="page" value="${pageBean.page + 1}"/>
        <c:param name="keyword" value="${param.keyword}"/>
        <c:param name="status" value="${param.status}"/>
        <c:param name="buildingId" value="${param.buildingId}"/>
        <c:param name="role" value="${param.role}"/>
    </c:url>
    <div class="pagination">
        <a class="${pageBean.hasPreviousPage ? '' : 'disabled'}" href="${previousPageUrl}">上一页</a>
        <span>第 ${pageBean.page} / ${pageBean.totalPage} 页，共 ${pageBean.totalCount} 条</span>
        <a class="${pageBean.hasNextPage ? '' : 'disabled'}" href="${nextPageUrl}">下一页</a>
    </div>
</c:if>
