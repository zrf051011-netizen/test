<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/taglibs.jsp" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <c:choose>
        <c:when test="${not empty pageTitle}">
            <c:set var="resolvedPageTitle" value="${pageTitle}"/>
        </c:when>
        <c:otherwise>
            <c:set var="resolvedPageTitle" value="工作台"/>
        </c:otherwise>
    </c:choose>
    <title>${resolvedPageTitle} - 宿舍运营中心</title>
    <link rel="stylesheet" href="${ctx}/css/app.css?v=202607111245">
</head>
<body class="app-body page-${activeMenu}">
<svg class="ui-icon-sprite" aria-hidden="true">
    <symbol id="icon-overview" viewBox="0 0 24 24">
        <rect x="3" y="3" width="7" height="7" rx="2"/>
        <rect x="14" y="3" width="7" height="7" rx="2"/>
        <rect x="3" y="14" width="7" height="7" rx="2"/>
        <rect x="14" y="14" width="7" height="7" rx="2"/>
    </symbol>
    <symbol id="icon-users" viewBox="0 0 24 24">
        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
        <circle cx="9" cy="7" r="4"/>
        <path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
    </symbol>
    <symbol id="icon-building" viewBox="0 0 24 24">
        <path d="M4 21V5a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v16"/>
        <path d="M17 9h2a2 2 0 0 1 2 2v10M2 21h20M8 7h2M13 7h1M8 11h2M13 11h1M8 15h2M13 15h1M9 21v-3h3v3"/>
    </symbol>
    <symbol id="icon-bed" viewBox="0 0 24 24">
        <path d="M3 5v16M21 10v11M3 16h18M3 10h15a3 3 0 0 1 3 3v3"/>
        <path d="M7 10V7h5a3 3 0 0 1 3 3"/>
    </symbol>
    <symbol id="icon-student-card" viewBox="0 0 24 24">
        <rect x="3" y="4" width="18" height="16" rx="3"/>
        <circle cx="9" cy="10" r="2.5"/>
        <path d="M6 16c.7-1.7 1.7-2.5 3-2.5s2.3.8 3 2.5M14.5 9H18M14.5 13H18M14.5 17H17"/>
    </symbol>
    <symbol id="icon-repair" viewBox="0 0 24 24">
        <path d="M14.7 6.3a4 4 0 0 0-5-5L12 3.6 9.6 6 7.3 3.7a4 4 0 0 0 5 5L4 17a2.1 2.1 0 0 0 3 3l7.7-8.3a4 4 0 0 0 5-5L17.4 9 15 6.6l2.3-2.3a4 4 0 0 0-2.6 2z"/>
    </symbol>
    <symbol id="icon-check-in" viewBox="0 0 24 24">
        <path d="M4 21V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v17M4 21h14"/>
        <circle cx="13" cy="12" r=".7" class="icon-dot"/>
        <path d="M20 9v6M17.5 12H22M19 14l3-2-3-2"/>
    </symbol>
    <symbol id="icon-occupancy" viewBox="0 0 24 24">
        <path d="M21 12a9 9 0 1 1-9-9"/>
        <path d="M12 3v9h9A9 9 0 0 0 12 3z"/>
        <path d="m8.5 16 2 2 4-4"/>
    </symbol>
    <symbol id="icon-upload" viewBox="0 0 24 24">
        <path d="M12 16V4M7 9l5-5 5 5"/>
        <path d="M5 14v5a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-5"/>
    </symbol>
    <symbol id="icon-download" viewBox="0 0 24 24">
        <path d="M12 4v12M7 11l5 5 5-5"/>
        <path d="M5 19h14"/>
    </symbol>
    <symbol id="icon-plus" viewBox="0 0 24 24">
        <path d="M12 5v14M5 12h14"/>
    </symbol>
    <symbol id="icon-file-sheet" viewBox="0 0 24 24">
        <path d="M6 2h8l4 4v16H6a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2z"/>
        <path d="M14 2v5h5M8 11h6M8 15h6M8 19h4"/>
    </symbol>
    <symbol id="icon-user-plus" viewBox="0 0 24 24">
        <circle cx="9" cy="7" r="4"/>
        <path d="M2 21v-2a4 4 0 0 1 4-4h6a4 4 0 0 1 4 4v2M19 8v6M16 11h6"/>
    </symbol>
    <symbol id="icon-bed-check" viewBox="0 0 24 24">
        <path d="M3 5v16M21 11v10M3 17h18M3 11h10a3 3 0 0 1 3 3v3M7 11V8h4a3 3 0 0 1 3 3"/>
        <path d="m17 7 2 2 3-4"/>
    </symbol>
    <symbol id="icon-report" viewBox="0 0 24 24">
        <path d="M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/>
        <path d="M7 16v-3M12 16V8M17 16v-6M7 19h10"/>
    </symbol>
    <symbol id="icon-sheet-upload" viewBox="0 0 24 24">
        <path d="M5 2h9l5 5v13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zM14 2v6h6"/>
        <path d="M11 18v-7M8 14l3-3 3 3"/>
    </symbol>
    <symbol id="icon-chevron-right" viewBox="0 0 24 24">
        <path d="m9 18 6-6-6-6"/>
    </symbol>
    <symbol id="icon-bell" viewBox="0 0 24 24">
        <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/>
    </symbol>
    <symbol id="icon-lock" viewBox="0 0 24 24">
        <rect x="4" y="10" width="16" height="11" rx="2"/>
        <path d="M8 10V7a4 4 0 0 1 8 0v3M12 14v3"/>
    </symbol>
    <symbol id="icon-logout" viewBox="0 0 24 24">
        <path d="M10 4H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h5M14 8l4 4-4 4M18 12H8"/>
    </symbol>
    <symbol id="icon-search" viewBox="0 0 24 24">
        <circle cx="11" cy="11" r="7"/>
        <path d="m20 20-4-4"/>
    </symbol>
    <symbol id="icon-edit" viewBox="0 0 24 24">
        <path d="M12 20h9M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4z"/>
    </symbol>
    <symbol id="icon-trash" viewBox="0 0 24 24">
        <path d="M4 7h16M9 7V4h6v3M7 7l1 14h8l1-14M10 11v6M14 11v6"/>
    </symbol>
</svg>
<div class="app-shell">
    <aside class="sidebar app-sidebar">
        <div class="brand sidebar-brand">
            <div class="brand-mark" aria-label="DormFlow logo">
                <svg class="brand-icon" viewBox="0 0 48 48" fill="none" aria-hidden="true">
                    <rect x="8" y="10" width="12" height="30" rx="2" class="icon-shell"/>
                    <rect x="22" y="10" width="18" height="30" rx="2" class="icon-shell"/>
                    <rect x="11" y="16" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="18" y="16" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="11" y="25" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="18" y="25" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="11" y="34" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="27" y="16" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="34" y="16" width="4" height="5" rx="1" class="icon-window"/>
                    <rect x="27" y="24" width="5" height="5" rx="1" class="icon-window"/>
                    <rect x="34" y="24" width="4" height="5" rx="1" class="icon-window"/>
                    <rect x="27" y="32" width="11" height="8" rx="2" class="icon-entry"/>
                </svg>
            </div>
            <div class="brand-copy">
                <strong>DormFlow</strong>
                <span>宿舍运营中心</span>
            </div>
        </div>
        <nav class="nav-list sidebar-nav">
            <c:choose>
                <c:when test="${sessionScope.loginUser.role eq 'ADMIN'}">
                    <a class="nav-item ${activeMenu eq 'dashboard' ? 'active' : ''}" href="${ctx}/admin/dashboard"><span class="nav-icon"><svg><use href="#icon-overview"/></svg></span><span class="nav-label">总览</span></a>
                    <a class="nav-item ${activeMenu eq 'users' ? 'active' : ''}" href="${ctx}/admin/users"><span class="nav-icon"><svg><use href="#icon-users"/></svg></span><span class="nav-label">用户管理</span></a>
                    <a class="nav-item ${activeMenu eq 'buildings' ? 'active' : ''}" href="${ctx}/admin/buildings"><span class="nav-icon"><svg><use href="#icon-building"/></svg></span><span class="nav-label">楼栋管理</span></a>
                    <a class="nav-item ${activeMenu eq 'dormitories' ? 'active' : ''}" href="${ctx}/admin/dormitories"><span class="nav-icon"><svg><use href="#icon-bed"/></svg></span><span class="nav-label">宿舍管理</span></a>
                    <a class="nav-item ${activeMenu eq 'students' ? 'active' : ''}" href="${ctx}/admin/students"><span class="nav-icon"><svg><use href="#icon-student-card"/></svg></span><span class="nav-label">学生管理</span></a>
                    <a class="nav-item ${activeMenu eq 'repairs' ? 'active' : ''}" href="${ctx}/admin/repairs"><span class="nav-icon"><svg><use href="#icon-repair"/></svg></span><span class="nav-label">报修监管</span></a>
                </c:when>
                <c:when test="${sessionScope.loginUser.role eq 'BUILDING_ADMIN'}">
                    <a class="nav-item ${activeMenu eq 'dashboard' ? 'active' : ''}" href="${ctx}/buildingadmin/dashboard"><span class="nav-icon"><svg><use href="#icon-overview"/></svg></span><span class="nav-label">楼栋总览</span></a>
                    <a class="nav-item ${activeMenu eq 'dormitories' ? 'active' : ''}" href="${ctx}/buildingadmin/dormitories"><span class="nav-icon"><svg><use href="#icon-bed"/></svg></span><span class="nav-label">房态板</span></a>
                    <a class="nav-item ${activeMenu eq 'students' ? 'active' : ''}" href="${ctx}/buildingadmin/students"><span class="nav-icon"><svg><use href="#icon-student-card"/></svg></span><span class="nav-label">学生列表</span></a>
                    <a class="nav-item ${activeMenu eq 'repairs' ? 'active' : ''}" href="${ctx}/buildingadmin/repairs"><span class="nav-icon"><svg><use href="#icon-repair"/></svg></span><span class="nav-label">报修处理</span></a>
                    <a class="nav-item ${activeMenu eq 'check' ? 'active' : ''}" href="${ctx}/buildingadmin/check"><span class="nav-icon"><svg><use href="#icon-check-in"/></svg></span><span class="nav-label">入住办理</span></a>
                </c:when>
                <c:otherwise>
                    <a class="nav-item ${activeMenu eq 'dashboard' ? 'active' : ''}" href="${ctx}/student/dashboard"><span class="nav-icon"><svg><use href="#icon-bed"/></svg></span><span class="nav-label">我的宿舍</span></a>
                    <a class="nav-item ${activeMenu eq 'repairs' ? 'active' : ''}" href="${ctx}/student/repairs"><span class="nav-icon"><svg><use href="#icon-repair"/></svg></span><span class="nav-label">我的报修</span></a>
                </c:otherwise>
            </c:choose>
        </nav>
        <div class="sidebar-foot">
            <span class="sidebar-user-avatar" aria-hidden="true"><svg><use href="#icon-users"/></svg></span>
            <div class="sidebar-user-meta">
                <strong>${sessionScope.loginUser.realName}</strong>
                <small>${sessionScope.loginUser.username}</small>
            </div>
            <svg class="sidebar-user-chevron" aria-hidden="true"><use href="#icon-chevron-right"/></svg>
            <span class="role-pill">${sessionScope.loginUser.roleName}</span>
        </div>
    </aside>
    <section class="workspace">
        <header class="topbar">
            <div class="topbar-title">
                <p class="eyebrow"><span>宿舍运营中心</span><i>/</i><strong>${resolvedPageTitle}</strong></p>
                <h1>${resolvedPageTitle}</h1>
                <span>您好，${sessionScope.loginUser.realName}，以下是最新数据面板</span>
            </div>
            <div class="top-actions">
                <div class="notice-center" data-notice-root>
                    <button class="notice-indicator" type="button" data-notice-toggle
                            aria-label="${noticeCount} 条待处理事项" aria-expanded="false" aria-controls="noticePanel">
                        <svg aria-hidden="true"><use href="#icon-bell"/></svg>
                        <c:if test="${noticeCount gt 0}">
                            <span class="notice-badge">
                                <c:choose>
                                    <c:when test="${noticeCount gt 99}">99+</c:when>
                                    <c:otherwise>${noticeCount}</c:otherwise>
                                </c:choose>
                            </span>
                        </c:if>
                    </button>
                    <div class="notice-panel" id="noticePanel" data-notice-panel hidden>
                        <div class="notice-panel-head">
                            <div>
                                <strong>待办提醒</strong>
                                <span>${noticeTitle}</span>
                            </div>
                            <c:if test="${noticeCount gt 0}"><b>${noticeCount}</b></c:if>
                        </div>
                        <div class="notice-list">
                            <c:choose>
                                <c:when test="${empty noticeRepairs}">
                                    <div class="notice-empty">
                                        <svg aria-hidden="true"><use href="#icon-bell"/></svg>
                                        <strong>暂无待处理事项</strong>
                                        <span>当前没有你需要处理的报修。</span>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${noticeRepairs}" var="notice">
                                        <a class="notice-item" href="${ctx}${noticeLink}">
                                            <i aria-hidden="true"><svg><use href="#icon-repair"/></svg></i>
                                            <span>
                                                <strong><c:out value="${notice.repairType}"/></strong>
                                                <small><c:out value="${notice.buildingName}"/>-<c:out value="${notice.roomNumber}"/> 路 <c:out value="${notice.reporterName}"/></small>
                                                <time><fmt:formatDate value="${notice.reportTime}" pattern="MM-dd HH:mm"/></time>
                                            </span>
                                            <em class="status-${notice.status}">${notice.statusName}</em>
                                        </a>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <a class="notice-panel-foot" href="${ctx}${noticeLink}">查看全部待办</a>
                    </div>
                </div>
                <div class="user-chip">
                    <span class="user-avatar" aria-hidden="true"><svg><use href="#icon-users"/></svg></span>
                    <div>
                        <strong>${sessionScope.loginUser.realName}</strong>
                        <small>${sessionScope.loginUser.roleName} / ${sessionScope.loginUser.username}</small>
                    </div>
                </div>
                <button class="ghost-btn top-action-btn" type="button" data-open-password>
                    <svg aria-hidden="true"><use href="#icon-lock"/></svg>
                    <span>修改密码</span>
                </button>
                <form class="logout-form" method="post" action="${ctx}/logout">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    <button class="logout-btn top-action-btn" type="submit">
                        <svg aria-hidden="true"><use href="#icon-logout"/></svg>
                        <span>退出</span>
                    </button>
                </form>
            </div>
        </header>
        <main class="content">
            <c:if test="${not empty param.success}">
                <div class="alert success">${param.success}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert error">${param.error}</div>
            </c:if>


