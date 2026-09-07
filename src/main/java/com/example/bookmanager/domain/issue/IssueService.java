package com.example.bookmanager.domain.issue;

import com.example.bookmanager.domain.user.UserAccount;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;

    public List<IssueEntity> findAll(){
        return issueRepository.findAll();
    }

    @Transactional
    public void create(String bookTitle, String authorName, Integer rating, UserAccount user) {
        IssueEntity entity = new IssueEntity();
        entity.setBookTitle(bookTitle);
        entity.setAuthorName(authorName);
        entity.setRating(rating);
        entity.setUser(user);

        issueRepository.save(entity);
    }

    public @Nullable IssueEntity findById(long issueId) {
        return issueRepository.findById(issueId).orElse(null);
    }

    public List<IssueEntity> search(String authorName, Integer rating, UserAccount user){
        boolean hasAuthor = authorName != null && !authorName.isBlank();
        boolean hasRating = rating != null;

        if (hasAuthor && hasRating){
            return issueRepository.findByAuthorNameContainingAndRating(authorName, rating);
        } else if (hasAuthor) {
            return issueRepository.findByAuthorNameContaining(authorName);
        } else if (hasRating) {
            return issueRepository.findByRating(rating);
        } else {
            return issueRepository.findAll();
        }
    }

    public List<IssueEntity> findByUser(UserAccount user){
        return issueRepository.findByUser(user);
    }

    public void createUser(IssueEntity issue, UserAccount user){
        issue.setUser(user);
        issueRepository.save(issue);
    }

}
