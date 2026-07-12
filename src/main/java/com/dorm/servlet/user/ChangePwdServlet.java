package com.dorm.servlet.user;

import com.dorm.entity.User;
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

@WebServlet("/user/changePwd")
public class ChangePwdServlet extends HttpServlet {
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> result = new HashMap<String, Object>();
        User user = (User) request.getSession().getAttribute("loginUser");
        try {
            userService.changePassword(
                    user.getId(),
                    WebUtil.getString(request, "oldPassword"),
                    WebUtil.getString(request, "newPassword"),
                    WebUtil.getString(request, "confirmPassword")
            );
            result.put("success", true);
            result.put("message", "密码修改成功");
            result.put("redirectUrl", request.getContextPath() + userService.homePath(user));
        } catch (BusinessException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        response.getWriter().write(gson.toJson(result));
    }
}
