package com.hhu.bookshop.controller;

import com.hhu.bookshop.entity.Book;
import com.hhu.bookshop.entity.BorrowRecord;
import com.hhu.bookshop.service.BookService;
import com.hhu.bookshop.service.BorrowService;
import com.hhu.bookshop.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 借阅控制器 - 处理借阅/归还相关请求
 */
@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 我的借阅记录列表
     */
    @GetMapping("/my")
    public String myBorrows(HttpServletRequest request, Model model) {
        Integer userId = getUserId(request);
        List<BorrowRecord> records = borrowService.findByUserId(userId);
        model.addAttribute("records", records);
        return "borrow/my";
    }

    /**
     * 借阅图书（成功后跳回图书列表，显示库存更新）
     */
    @PostMapping("/do/{bookId}")
    public String doBorrow(@PathVariable Integer bookId,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        Integer userId = getUserId(request);
        String error = borrowService.borrow(userId, bookId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            // 查询图书名称用于提示
            bookService.findById(bookId).ifPresent(book ->
                redirectAttributes.addFlashAttribute("success", "借阅《" + book.getBookName() + "》成功！")
            );
        }
        return "redirect:/books";
    }

    /**
     * 归还图书（成功后跳回图书列表，显示库存更新）
     */
    @PostMapping("/return/{recordId}")
    public String doReturn(@PathVariable Integer recordId,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        Integer userId = getUserId(request);
        String error = borrowService.returnBook(userId, recordId);
        if (error != null) {
            redirectAttributes.addFlashAttribute("error", error);
        } else {
            redirectAttributes.addFlashAttribute("success", "归还成功！");
        }
        return "redirect:/books";
    }

    /**
     * 管理员查看所有借阅记录
     */
    @GetMapping("/all")
    public String allBorrows(Model model) {
        List<BorrowRecord> records = borrowService.findAll();
        model.addAttribute("records", records);
        return "borrow/all";
    }

    /**
     * 从request中获取当前登录用户ID
     */
    private Integer getUserId(HttpServletRequest request) {
        // 优先从request attribute获取（AuthFilter已设置）
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId != null) return userId;
    
        // 兑底：从Cookie中解析
        String token = getTokenFromCookie(request);
        if (token != null) {
            return jwtUtil.getUserIdFromToken(token);
        }
        return 0;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
