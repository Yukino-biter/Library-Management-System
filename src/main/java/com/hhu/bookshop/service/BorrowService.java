package com.hhu.bookshop.service;

import com.hhu.bookshop.dao.BookDao;
import com.hhu.bookshop.dao.BorrowDao;
import com.hhu.bookshop.dao.UserDao;
import com.hhu.bookshop.entity.Book;
import com.hhu.bookshop.entity.BorrowRecord;
import com.hhu.bookshop.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 借阅业务逻辑层
 */
@Service
public class BorrowService {

    @Autowired
    private BorrowDao borrowDao;

    @Autowired
    private BookDao bookDao;

    @Autowired
    private UserDao userDao;

    /**
     * 借阅图书
     * @return 成功返回null，失败返回错误信息
     */
    @Transactional
    public String borrow(Integer userId, Integer bookId) {
        // 检查图书是否存在
        Optional<Book> optBook = bookDao.findById(bookId);
        if (optBook.isEmpty()) {
            return "图书不存在";
        }
        Book book = optBook.get();

        // 检查库存
        if (book.getStock() <= 0) {
            return "该图书库存不足，无法借阅";
        }

        // 检查是否已借阅未归还
        Optional<BorrowRecord> existing = borrowDao.findByUserIdAndBookIdAndStatus(userId, bookId, "borrowed");
        if (existing.isPresent()) {
            return "您已借阅该图书且尚未归还，请勿重复借阅";
        }

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        borrowDao.save(record);

        // 库存减1
        book.setStock(book.getStock() - 1);
        bookDao.save(book);

        return null; // 成功
    }

    /**
     * 归还图书
     * @return 成功返回null，失败返回错误信息
     */
    @Transactional
    public String returnBook(Integer userId, Integer recordId) {
        Optional<BorrowRecord> optRecord = borrowDao.findById(recordId);
        if (optRecord.isEmpty()) {
            return "借阅记录不存在";
        }
        BorrowRecord record = optRecord.get();

        // 验证是否属于当前用户
        if (!record.getUserId().equals(userId)) {
            return "无权操作此借阅记录";
        }

        if ("returned".equals(record.getStatus())) {
            return "该图书已归还，请勿重复操作";
        }

        // 更新借阅记录
        record.setReturnTime(LocalDateTime.now());
        record.setStatus("returned");
        borrowDao.save(record);

        // 库存加1
        Optional<Book> optBook = bookDao.findById(record.getBookId());
        if (optBook.isPresent()) {
            Book book = optBook.get();
            book.setStock(book.getStock() + 1);
            bookDao.save(book);
        }

        return null; // 成功
    }

    /**
     * 查询用户借阅记录（填充图书和用户信息）
     */
    public List<BorrowRecord> findByUserId(Integer userId) {
        List<BorrowRecord> records = borrowDao.findByUserIdOrderByBorrowTimeDesc(userId);
        for (BorrowRecord record : records) {
            bookDao.findById(record.getBookId()).ifPresent(record::setBook);
            userDao.findById(record.getUserId()).ifPresent(u -> record.setUserNickname(u.getNickname()));
        }
        return records;
    }

    /**
     * 查询所有借阅记录（管理员用）
     */
    public List<BorrowRecord> findAll() {
        List<BorrowRecord> records = borrowDao.findAll();
        for (BorrowRecord record : records) {
            bookDao.findById(record.getBookId()).ifPresent(record::setBook);
            userDao.findById(record.getUserId()).ifPresent(u -> record.setUserNickname(u.getNickname()));
        }
        // 按借阅时间倒序
        records.sort((a, b) -> b.getBorrowTime().compareTo(a.getBorrowTime()));
        return records;
    }
}
