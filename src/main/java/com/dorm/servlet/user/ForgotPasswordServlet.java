package com.dorm.servlet.user;

import com.dorm.exception.BusinessException;
import com.dorm.service.UserService;
import com.dorm.util.WebUtil;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MILLIS = 10 * 60 * 1000L;
    private static final ConcurrentHashMap<String, AttemptWindow> ATTEMPTS = new ConcurrentHashMap<String, AttemptWindow>();

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");

        Map<String, Object> result = new HashMap<String, Object>();
        String username = WebUtil.getString(request, "username");
        String attemptKey = request.getRemoteAddr() + ":" + (username == null ? "" : username.trim().toLowerCase());

        if (isBlocked(attemptKey)) {
            result.put("success", false);
            result.put("message", "尝试次数过多，请10分钟后再试");
            response.getWriter().write(gson.toJson(result));
            return;
        }

        try {
            userService.resetPasswordByPhone(
                    username,
                    WebUtil.getString(request, "phone"),
                    WebUtil.getString(request, "newPassword"),
                    WebUtil.getString(request, "confirmPassword")
            );
            ATTEMPTS.remove(attemptKey);
            result.put("success", true);
            result.put("message", "密码重置成功，请使用新密码登录");
        } catch (BusinessException e) {
            recordFailure(attemptKey);
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        response.getWriter().write(gson.toJson(result));
    }

    private boolean isBlocked(String key) {
        AttemptWindow current = ATTEMPTS.get(key);
        if (current == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - current.startedAt >= WINDOW_MILLIS) {
            ATTEMPTS.remove(key, current);
            return false;
        }
        return current.failures >= MAX_FAILURES;
    }

    private void recordFailure(String key) {
        final long now = System.currentTimeMillis();
        ATTEMPTS.compute(key, (ignored, current) -> {
            if (current == null || now - current.startedAt >= WINDOW_MILLIS) {
                return new AttemptWindow(now, 1);
            }
            return new AttemptWindow(current.startedAt, current.failures + 1);
        });
    }

    private static class AttemptWindow {
        private final long startedAt;
        private final int failures;

        private AttemptWindow(long startedAt, int failures) {
            this.startedAt = startedAt;
            this.failures = failures;
        }
    }
}
