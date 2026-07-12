package com.dorm.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@WebFilter("/*")
public class CsrfFilter implements Filter {
    private static final String SESSION_TOKEN = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isStatic(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = request.getSession(true);
        String expected = (String) session.getAttribute(SESSION_TOKEN);
        if (expected == null) {
            expected = newToken();
            session.setAttribute(SESSION_TOKEN, expected);
        }
        request.setAttribute(SESSION_TOKEN, expected);

        if (isStateChanging(request.getMethod()) && !matches(expected, submittedToken(request))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setHeader("Cache-Control", "no-store");
            if (expectsJson(path)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"请求已失效，请刷新页面后重试\"}");
            } else {
                request.setAttribute("forbiddenMessage", "请求已失效，请返回原页面后重试");
                request.getRequestDispatcher("/403.jsp").forward(request, response);
            }
            return;
        }

        chain.doFilter(req, resp);
    }

    private boolean isStateChanging(String method) {
        return !"GET".equalsIgnoreCase(method)
                && !"HEAD".equalsIgnoreCase(method)
                && !"OPTIONS".equalsIgnoreCase(method);
    }

    private String submittedToken(HttpServletRequest request) {
        String token = request.getHeader("X-CSRF-Token");
        return token == null || token.isEmpty() ? request.getParameter("csrfToken") : token;
    }

    private boolean matches(String expected, String actual) {
        if (actual == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private boolean expectsJson(String path) {
        return "/forgot-password".equals(path) || "/user/changePwd".equals(path);
    }

    private boolean isStatic(String path) {
        return path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/static/")
                || path.startsWith("/favicon");
    }
}
