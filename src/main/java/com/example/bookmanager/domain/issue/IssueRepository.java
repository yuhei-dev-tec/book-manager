package com.example.bookmanager.domain.issue;

import com.example.bookmanager.domain.user.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<IssueEntity, Long>{

    List<IssueEntity> findByAuthorNameContainingAndRating(String authorName, Integer rating);

    List<IssueEntity> findByAuthorNameContaining(String authorName);

    List<IssueEntity> findByRating(Integer rating);

    List<IssueEntity> findByUser(UserAccount user);
    List<IssueEntity> findByUserId(Long userId);

}
