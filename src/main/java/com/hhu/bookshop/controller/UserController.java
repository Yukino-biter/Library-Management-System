package com.hhu.bookshop.controller;

import com.hhu.bookshop.entity.User;
import com.hhu.bookshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 处理登录和注册相关请求
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 根路径重定向到登录页
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * 显示登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * 处理登录请求
     * 后端校验用户名密码，成功后生成JWT Token存入Cookie，跳转至图书管理主页
     */
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpServletResponse response) {
        // 后端非空校验
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "账号不能为空");
            return "login";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "密码不能为空");
            return "login";
        }

        // 调用Service层验证
        User user = userService.login(username.trim(), password);
        if (user != null) {
            // 登录成功，生成JWT Token
            String token = userService.generateToken(user);

            // 将Token存入Cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); // 24小时
            response.addCookie(cookie);

            // 同时存入用户昵称Cookie用于页面展示
            Cookie nickCookie = new Cookie("nickname", user.getNickname());
            nickCookie.setPath("/");
            nickCookie.setMaxAge(24 * 60 * 60);
            response.addCookie(nickCookie);

            return "redirect:/books";
        } else {
            // 登录失败
            model.addAttribute("error", "账号或密码错误");
            model.addAttribute("username", username);
            return "login";
        }
    }

    /**
     * 显示注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * 处理注册请求
     * 后端校验、密码BCrypt加密、数据入库
     */
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String nickname,
                           Model model) {
        // 后端非空校验
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "账号不能为空");
            return "register";
        }
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", "密码不能为空");
            return "register";
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            model.addAttribute("error", "昵称不能为空");
            return "register";
        }

        // 构建用户对象
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(password);
        user.setNickname(nickname.trim());

        // 调用Service层注册（含BCrypt加密）
        User saved = userService.register(user);
        if (saved != null) {
            // 注册成功，跳转登录页
            model.addAttribute("success", "注册成功，请登录");
            return "login";
        } else {
            // 用户名已存在
            model.addAttribute("error", "该账号已被注册");
            model.addAttribute("username", username);
            model.addAttribute("nickname", nickname);
            return "register";
        }
    }

    /**
     * 检查用户名是否可用（AJAX请求）
     */
    @GetMapping("/checkUsername")
    @ResponseBody
    public String checkUsername(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return "empty";
        }
        boolean exists = userService.findByUsername(username.trim()).isPresent();
        return exists ? "exists" : "available";
    }

    /**
     * 显示重置密码页面
     */
    @GetMapping("/resetPassword")
    public String resetPasswordPage() {
        return "resetPassword";
    }

    /**
     * 处理重置密码请求
     * 根据账号+昵称验证身份，通过后重置密码为123456
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String username,
                                @RequestParam String nickname,
                                Model model) {
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", "账号不能为空");
            return "resetPassword";
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            model.addAttribute("error", "昵称不能为空");
            return "resetPassword";
        }

        boolean success = userService.resetPassword(username.trim(), nickname.trim());
        if (success) {
            model.addAttribute("success", "密码已重置为 123456，请重新登录");
            return "login";
        } else {
            model.addAttribute("error", "账号或昵称不匹配，重置失败");
            model.addAttribute("username", username);
            return "resetPassword";
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 清除Cookie
        Cookie tokenCookie = new Cookie("token", "");
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);

        Cookie nickCookie = new Cookie("nickname", "");
        nickCookie.setPath("/");
        nickCookie.setMaxAge(0);
        response.addCookie(nickCookie);

        return "redirect:/login";
    }
}
