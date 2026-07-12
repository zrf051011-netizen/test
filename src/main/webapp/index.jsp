<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/taglibs.jsp" %>
<c:choose>
    <c:when test="${empty sessionScope.loginUser}">
        <c:redirect url="/login.jsp"/>
    </c:when>
    <c:when test="${sessionScope.loginUser.role eq 'ADMIN'}">
        <c:redirect url="/admin/dashboard"/>
    </c:when>
    <c:when test="${sessionScope.loginUser.role eq 'BUILDING_ADMIN'}">
        <c:redirect url="/buildingadmin/dashboard"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="/student/dashboard"/>
    </c:otherwise>
</c:choose>

