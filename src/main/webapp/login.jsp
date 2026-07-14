<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/common/taglibs.jsp" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 宿舍管理系统</title>
    <link rel="icon" href="${ctx}/static/images/favicon.svg" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/css/app.css?v=202607101620">
    <link rel="stylesheet" href="${ctx}/css/login.css?v=202607130160">
</head>
<body class="login-body login-page" style="--login-bg-image: url('${pageContext.request.contextPath}/static/images/login-bg.png');">
<main class="login-shell">
    <section class="login-visual login-visual--liquid"
             data-liquid-glass
             data-liquid-variant="dark"
             data-liquid-background="${ctx}/static/images/login-bg.png">
        <canvas class="login-liquid-canvas" aria-hidden="true"></canvas>
        <div class="brand large">
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
            <div>
                <strong>DormFlow</strong>
                <span>宿舍运营中心</span>
            </div>
        </div>

        <div class="visual-copy">
            <h1>让宿舍管理更高效，<br>让校园生活<span>更美好</span></h1>
            <p>一体化宿舍运营管理平台，覆盖入住、房态、报修、学生管理，助力校园数字化与精细化管理。</p>
            <div class="feature-list">
                <div>
                    <i class="feature-icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" role="img" aria-hidden="true">
                            <path d="M4 19h16a1 1 0 0 0 1-1V7.5A1.5 1.5 0 0 0 19.5 6H16V4a1 1 0 0 0-1-1h-6a1 1 0 0 0-1 1v2H4.5A1.5 1.5 0 0 0 3 7.5V18a1 1 0 0 0 1 1Z"/>
                            <path d="M9 8h5"/>
                            <path d="M9 10.5h4"/>
                            <path d="M9 13h6"/>
                            <path d="M16 20h-1"/>
                            <path d="M8 19v1"/>
                            <path d="M20 13.2V17a1 1 0 0 1-.7.95M17 20h6"/>
                            <path d="M17 13.5l3 3-3 3" />
                        </svg>
                    </i>
                    <strong>入住管理</strong><span>流程化办理</span>
                </div>
                <div>
                    <i class="feature-icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" role="img" aria-hidden="true">
                            <path d="M4 20V8a1 1 0 0 1 1-1h2"/>
                            <path d="M7 7h10a2 2 0 0 1 2 2v12"/>
                            <path d="M7 11h10"/>
                            <path d="M7 15h7"/>
                            <path d="M11 20V11"/>
                            <path d="M17 20V8.5"/>
                            <path d="M21 10h-3"/>
                            <path d="M20 13h-2"/>
                        </svg>
                    </i>
                    <strong>房态管理</strong><span>实时掌控</span>
                </div>
                <div>
                    <i class="feature-icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" role="img" aria-hidden="true">
                            <path d="M11 7.2a3.5 3.5 0 1 1-6.15 3.28"/>
                            <path d="M4.85 9.4 2 14.8l3 3 5.4-2.85"/>
                            <path d="M17 8h3"/>
                            <path d="M18.5 6.5v3"/>
                            <path d="M16 7.5a3.5 3.5 0 0 1 2 3"/>
                            <path d="M14 15v2"/>
                            <path d="M14 21h5"/>
                        </svg>
                    </i>
                    <strong>报修管理</strong><span>高效响应</span>
                </div>
                <div>
                    <i class="feature-icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" fill="none" role="img" aria-hidden="true">
                            <rect x="3" y="5" width="18" height="14" rx="2"/>
                            <path d="M7 9h3"/>
                            <path d="M7 12h6"/>
                            <path d="M7 15h5"/>
                            <circle cx="16" cy="10.5" r="2"/>
                            <path d="M14 16h4"/>
                            <path d="M14 19h4"/>
                        </svg>
                    </i>
                    <strong>学生管理</strong><span>信息全面</span>
                </div>
            </div>
        </div>

        <div class="login-visual-bottom">
            <p>平台能力概览</p>
            <div class="visual-metrics">
                <span><strong>4</strong>核心模块<small>统一业务入口</small></span>
                <span><strong>3</strong>用户角色<small>分级权限管理</small></span>
                <span><strong>统一</strong>管理平台<small>集中处理事务</small></span>
                <span><strong>全程</strong>操作留痕<small>便于查询追踪</small></span>
            </div>
        </div>
    </section>

    <section class="login-card login-card--liquid"
             data-liquid-glass
             data-liquid-background="${ctx}/static/images/login-bg.png">
        <canvas class="login-liquid-canvas" aria-hidden="true"></canvas>
        <div class="login-heading">
            <p class="eyebrow">欢迎回来</p>
            <h2>登录系统</h2>
            <span>使用分配的账号进入宿舍管理后台</span>
        </div>

        <c:if test="${not empty error}">
            <div class="login-alert" role="alert" aria-live="polite">用户名或密码错误</div>
        </c:if>

        <form action="${ctx}/login" method="post" class="login-form">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <label>
                <span>用户名</span>
                <input class="login-input user-input" type="text" name="username" value="${fn:escapeXml(username)}" placeholder="请输入用户名" autocomplete="username" required autofocus>
            </label>
            <label>
                <span>密码</span>
                <input class="login-input password-input" type="password" name="password" placeholder="请输入密码" autocomplete="current-password" required>
            </label>
            <div class="login-options login-options-end">
                <button class="forgot-link" type="button" data-open-forgot>忘记密码?</button>
            </div>
            <button type="submit" class="primary-btn wide">进入工作台</button>
        </form>

        <aside class="login-security" aria-label="安全提示">
            <strong>安全提示</strong>
            <ul>
                <li>请使用学校分配的账号登录</li>
                <li>请勿将密码提供给他人</li>
                <li>离开设备前请退出系统</li>
            </ul>
        </aside>
    </section>
</main>

<div class="modal-backdrop forgot-backdrop" id="forgotPasswordModal" hidden>
    <section class="modal forgot-modal" role="dialog" aria-modal="true" aria-labelledby="forgotPasswordTitle">
        <div class="modal-head">
            <div>
                <p class="eyebrow">账号验证</p>
                <h2 id="forgotPasswordTitle">重置密码</h2>
            </div>
            <button class="icon-btn" type="button" data-close-forgot aria-label="关闭">×</button>
        </div>
        <p class="forgot-help">输入用户名和系统中登记的手机号，验证通过后即可设置新密码。</p>
        <form id="forgotPasswordForm" class="form-grid single" autocomplete="off">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <label>
                <span>用户名</span>
                <input type="text" name="username" autocomplete="username" placeholder="请输入用户名" required>
            </label>
            <label>
                <span>绑定手机号</span>
                <input type="tel" name="phone" inputmode="numeric" autocomplete="tel" maxlength="20" placeholder="请输入登记手机号" required>
            </label>
            <label>
                <span>新密码</span>
                <div class="password-field">
                    <input type="password" name="newPassword" autocomplete="new-password" minlength="8" placeholder="至少 8 位" required>
                    <button type="button" data-toggle-password>显示</button>
                </div>
            </label>
            <label>
                <span>确认新密码</span>
                <div class="password-field">
                    <input type="password" name="confirmPassword" autocomplete="new-password" minlength="8" placeholder="再次输入新密码" required>
                    <button type="button" data-toggle-password>显示</button>
                </div>
            </label>
            <p class="form-message" id="forgotPasswordMessage" aria-live="polite"></p>
            <div class="form-actions">
                <button type="button" class="ghost-btn" data-close-forgot>取消</button>
                <button type="submit" class="primary-btn" data-forgot-submit>确认重置</button>
            </div>
        </form>
    </section>
</div>

<script>window.APP_CONTEXT = '${ctx}';</script>
<script src="${ctx}/js/app.js?v=202607101620"></script>
    <script src="${ctx}/js/login-liquid-glass.js?v=202607130160"></script>
</body>
</html>
