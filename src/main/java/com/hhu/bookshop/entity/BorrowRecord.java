package com.hhu.bookshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Data
@Entity
@Table(name = "borrow_record")
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    /** 借阅时间 */
    @Column(name = "borrow_time", nullable = false)
    private LocalDateTime borrowTime;

    /** 归还时间（null表示未归还） */
    @Column(name = "return_time")
    private LocalDateTime returnTime;

    /** 状态：borrowed-借阅中, returned-已归还 */
    @Column(nullable = false, length = 20)
    private String status = "borrowed";

    @PrePersist
    public void prePersist() {
        if (borrowTime == null) borrowTime = LocalDateTime.now();
        if (status == null) status = "borrowed";
    }

    /** 关联图书（非数据库字段） */
    @Transient
    private Book book;

    /** 关联用户昵称（非数据库字段） */
    @Transient
    private String userNickname;
}
