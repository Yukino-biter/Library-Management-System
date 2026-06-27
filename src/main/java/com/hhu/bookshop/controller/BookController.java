package com.hhu.bookshop.controller;

import com.hhu.bookshop.entity.Book;
import com.hhu.bookshop.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 图书控制器 - 处理图书CRUD相关请求
 */
@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Value("${upload.path:src/main/resources/static/uploads/}")
    private String uploadPath;

    /**
     * 图书列表（支持多条件查询，传递角色信息用于权限控制）
     */
    @GetMapping
    public String list(@RequestParam(required = false) String bookName,
                       @RequestParam(required = false) String category,
                       HttpServletRequest request,
                       Model model) {
        List<Book> books = bookService.search(bookName, category);
        List<String> categories = bookService.findAllCategories();

        model.addAttribute("books", books);
        model.addAttribute("categories", categories);
        model.addAttribute("bookName", bookName);
        model.addAttribute("category", category);
        // 传递用户角色信息（由AuthFilter设置）
        model.addAttribute("role", request.getAttribute("role"));
        model.addAttribute("userId", request.getAttribute("userId"));
        return "books/list";
    }

    /**
     * 跳转新增图书页面
     */
    @GetMapping("/add")
    public String addPage(Model model) {
        model.addAttribute("book", new Book());
        return "books/add";
    }

    /**
     * 新增图书（前端+后端双重校验，支持图片上传）
     */
    @PostMapping("/add")
    public String add(@ModelAttribute Book book,
                      @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                      Model model) {
        // 后端数据校验
        String error = validateBook(book);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("book", book);
            return "books/add";
        }

        // 处理图片上传
        String imagePath = handleUpload(coverFile);
        if (imagePath != null) {
            book.setImage(imagePath);
        }

        bookService.save(book);
        return "redirect:/books";
    }

    /**
     * 跳转编辑图书页面（回显原有数据）
     */
    @GetMapping("/edit/{id}")
    public String editPage(@PathVariable Integer id, Model model) {
        Optional<Book> optBook = bookService.findById(id);
        if (optBook.isPresent()) {
            model.addAttribute("book", optBook.get());
            return "books/edit";
        }
        return "redirect:/books";
    }

    /**
     * 更新图书（先查询原数据再合并更新，避免覆盖时间字段）
     */
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Integer id,
                       @ModelAttribute Book book,
                       @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                       Model model) {
        // 后端数据校验
        String error = validateBook(book);
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("book", book);
            return "books/edit";
        }

        // 从数据库查出原数据，合并表单字段
        Optional<Book> optExisting = bookService.findById(id);
        if (optExisting.isEmpty()) {
            return "redirect:/books";
        }
        Book existing = optExisting.get();
        existing.setBookName(book.getBookName());
        existing.setAuthor(book.getAuthor());
        existing.setPublisher(book.getPublisher());
        existing.setIsbn(book.getIsbn());
        existing.setPrice(book.getPrice());
        existing.setCategory(book.getCategory());
        existing.setStock(book.getStock());
        existing.setDescription(book.getDescription());

        // 处理图片上传（有新文件则替换，否则保留原图）
        String imagePath = handleUpload(coverFile);
        if (imagePath != null) {
            existing.setImage(imagePath);
        } else if (book.getImage() != null && !book.getImage().isEmpty()) {
            existing.setImage(book.getImage());
        }

        bookService.update(existing);
        return "redirect:/books";
    }

    /**
     * 删除图书
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    /**
     * 图书详情页面
     */
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Integer id, HttpServletRequest request, Model model) {
        Optional<Book> optBook = bookService.findById(id);
        if (optBook.isPresent()) {
            model.addAttribute("book", optBook.get());
            model.addAttribute("role", request.getAttribute("role"));
            model.addAttribute("userId", request.getAttribute("userId"));
            return "books/detail";
        }
        return "redirect:/books";
    }

    /**
     * 图书数据校验
     */
    private String validateBook(Book book) {
        if (book.getBookName() == null || book.getBookName().trim().isEmpty()) {
            return "图书名称不能为空";
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return "作者不能为空";
        }
        if (book.getPrice() == null) {
            return "价格不能为空";
        }
        if (book.getPrice().doubleValue() < 0) {
            return "价格不能为负数";
        }
        if (book.getStock() != null && book.getStock() < 0) {
            return "库存不能为负数";
        }
        return null;
    }

    /**
     * 处理图片上传，返回存储路径
     */
    private String handleUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            File dir = new File(uploadPath).getAbsoluteFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 必须使用绝对路径，否则transferTo会解析到Tomcat临时目录
            file.transferTo(new File(dir, fileName).getAbsoluteFile());
            return "/uploads/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
