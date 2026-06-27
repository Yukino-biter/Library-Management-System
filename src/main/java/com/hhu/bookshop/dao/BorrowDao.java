package com.hhu.bookshop.dao;

import com.hhu.bookshop.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 借阅记录数据访问层
 */
@Repository
public interface BorrowDao extends JpaRepository<BorrowRecord, Integer> {

    /**
     * 根据用户ID查询借阅记录
     */
    List<BorrowRecord> findByUserIdOrderByBorrowTimeDesc(Integer userId);

    /**
     * 查询某用户某本书未归还的记录
     */
    Optional<BorrowRecord> findByUserIdAndBookIdAndStatus(Integer userId, Integer bookId, String status);

    /**
     * 查询某本书所有未归还的记录（检查库存）
     */
    List<BorrowRecord> findByBookIdAndStatus(Integer bookId, String status);
}
