package com.hhu.bookshop.service;

import com.hhu.bookshop.dao.BookDao;
import com.hhu.bookshop.entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 图书业务逻辑层
 */
@Service
public class BookService {

    @Autowired
    private BookDao bookDao;

    /**
     * 查询所有图书
     */
    public List<Book> findAll() {
        return bookDao.findAll();
    }

    /**
     * 根据ID查询图书
     */
    public Optional<Book> findById(Integer id) {
        return bookDao.findById(id);
    }

    /**
     * 新增图书
     */
    public Book save(Book book) {
        return bookDao.save(book);
    }

    /**
     * 更新图书
     */
    public Book update(Book book) {
        return bookDao.save(book);
    }

    /**
     * 删除图书
     */
    public void deleteById(Integer id) {
        bookDao.deleteById(id);
    }

    /**
     * 多条件查询图书（书名模糊 + 分类精准）
     */
    public List<Book> search(String bookName, String category) {
        // 空字符串转null
        String name = (bookName != null && !bookName.trim().isEmpty()) ? bookName.trim() : null;
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        return bookDao.searchBooks(name, cat);
    }

    /**
     * 获取所有图书分类（去重）
     */
    public List<String> findAllCategories() {
        return bookDao.findAll().stream()
                .map(Book::getCategory)
                .filter(c -> c != null && !c.isEmpty())
                .distinct()
                .sorted()
                .toList();
    }
}
