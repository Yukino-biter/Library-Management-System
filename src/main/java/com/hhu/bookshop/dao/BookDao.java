package com.hhu.bookshop.dao;

import com.hhu.bookshop.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 图书数据访问层
 */
@Repository
public interface BookDao extends JpaRepository<Book, Integer> {

    /**
     * 根据图书名称模糊查询
     */
    List<Book> findByBookNameContaining(String bookName);

    /**
     * 根据图书分类精准查询
     */
    List<Book> findByCategory(String category);

    /**
     * 根据图书名称模糊查询 + 分类精准查询组合
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(:bookName IS NULL OR b.bookName LIKE %:bookName%) AND " +
            "(:category IS NULL OR b.category = :category)")
    List<Book> searchBooks(@Param("bookName") String bookName,
                           @Param("category") String category);
}
