package com.hhu.bookshop.filter;

import com.hhu.bookshop.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 认证与非法字符过滤器
 * 1. 登录拦截：未登录用户禁止访问图书管理页面，跳转至登录页
 * 2. 非法字符过滤：拦截包含SQL注入、XSS等非法内容的请求
 */
@Component
public class AuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    private static final List<String> WHITE_LIST = List.of(
            "/login", "/register", "/resetPassword", "/checkUsername",
            "/css/", "/js/", "/images/", "/static/", "/uploads/",
            "/favicon.ico", "/error"
    );

    private static final Pattern ILLEGAL_PATTERN = Pattern.compile(
            "(?i)(<script|javascript:|eval\\s*\\(|" +
            "union\\s+select|drop\\s+table|insert\\s+into|" +
            "delete\\s+from|update\\s+.*set|" +
            "exec\\s*\\(|execute\\s*\\()"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());

        // ========== 非法字符检测 ==========
        if (containsIllegalContent(request)) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('请求包含非法字符，请检查输入！');history.back();</script>");
            return;
        }

        // ========== 白名单路径放行 ==========
        for (String white : WHITE_LIST) {
            if (path.startsWith(white) || path.equals("/")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ========== JWT Token验证 ==========
        String token = getTokenFromCookie(request);
        if (token != null && jwtUtil.validateToken(token)) {
            // Token有效，将用户信息存入request属性，供后续使用
            request.setAttribute("username", jwtUtil.getUsernameFromToken(token));
            request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
            request.setAttribute("role", jwtUtil.getRoleFromToken(token));
            chain.doFilter(request, response);
        } else {
            // 未登录或Token失效，重定向到登录页
            response.sendRedirect(contextPath + "/login");
        }
    }

    /**
     * 检测请求参数中是否包含非法字符
     */
    private boolean containsIllegalContent(HttpServletRequest request) {
        // 检查请求参数
        var paramMap = request.getParameterMap();
        for (String[] values : paramMap.values()) {
            for (String value : values) {
                if (value != null && ILLEGAL_PATTERN.matcher(value).find()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 从Cookie中获取JWT Token
     */
    private String getTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
