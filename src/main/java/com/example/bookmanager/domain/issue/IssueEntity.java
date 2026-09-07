package com.example.bookmanager.domain.issue;

import com.example.bookmanager.domain.user.UserAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "book_manager")
@AllArgsConstructor
@Data
public class IssueEntity {
    @Id
    private long id;
    private String bookTitle;
    private String authorName;
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;
}
