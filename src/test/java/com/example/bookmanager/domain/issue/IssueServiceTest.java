package com.example.bookmanager.domain.issue;

import com.example.bookmanager.domain.user.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IssueRepository mockIssueRepository;

    @InjectMocks
    private IssueService mockIssueService;

    @Test
    @DisplayName("createメソッド実行時、入力値が正しくセットされたIssueEntityがsaveされること")
    void create_success(){
        String bookTittle = "テスト書籍";
        String authorName = "テスト著者";
        Integer rating = 5;
        UserAccount user = new UserAccount();

        mockIssueService.create(bookTittle, authorName, rating, user);

        ArgumentCaptor<IssueEntity> captor = ArgumentCaptor.forClass(IssueEntity.class);
        verify(mockIssueRepository).save(captor.capture());

        IssueEntity savedEntity = captor.getValue();

        assertThat(savedEntity.getBookTitle()).isEqualTo(bookTittle);
        assertThat(savedEntity.getAuthorName()).isEqualTo(authorName);
        assertThat(savedEntity.getRating()).isEqualTo(rating);
        assertThat(savedEntity.getUser()).isEqualTo(user);

    }

    @Nested
    @DisplayName("findByIdメソッドのテスト")
    class FindByIdTest{

        @Test
        @DisplayName("指定したIDのIssueEntityが存在する場合、そのEntityを返すこと")
        void findById_found(){
            long issueId = 1L;
            IssueEntity expectedEntity = new IssueEntity();
            expectedEntity.setBookTitle("テスト本");

            when(mockIssueRepository.findById(issueId)).thenReturn(Optional.of(expectedEntity));

            IssueEntity actual = mockIssueService.findById(issueId);

            assertThat(actual).isNotNull();
            assertThat(actual).isEqualTo(expectedEntity);
            assertThat(actual.getBookTitle()).isEqualTo("テスト本");
        }

        @Test
        @DisplayName("指定したIdのIssueEntityが存在しないとき、Nullを返すこと")
        void findById_notFound(){
            long issueId = 999L;

            when(mockIssueRepository.findById(issueId)).thenReturn(Optional.empty());

            IssueEntity actual = mockIssueService.findById(issueId);

            assertThat(actual).isNull();
        }
    }

    @Nested
    @DisplayName("searchメソッドのテストクラス")
    class SearchTest{

        @Test
        @DisplayName("authorNameとratingがどちらも空ではないとき、findByAuthorNameContainingAndRatingが呼ばれること")
        void search_authorName_rating_exist(){
            String authorName = "テスト著者";
            Integer rating = 5;
            UserAccount userAccount = new UserAccount();
            List<IssueEntity> expectedList = List.of(new IssueEntity());

            when(mockIssueRepository.findByAuthorNameContainingAndRating(authorName, rating)).
                    thenReturn(expectedList);

            List<IssueEntity> actual = mockIssueService.search(authorName, rating, userAccount);

            assertThat(actual).isEqualTo(expectedList);
            verify(mockIssueRepository).findByAuthorNameContainingAndRating(authorName, rating);
        }

        @Test
        @DisplayName("著者名のみ入力されている場合、findByAuthorNameContainingが呼ばれること")
        void search_onlyAuthorName(){
            String authorName = "テスト著者";
            Integer rating = null;
            UserAccount user = new UserAccount();
            List<IssueEntity> expectedList = List.of(new IssueEntity());

            when(mockIssueRepository.findByAuthorNameContaining(authorName))
                    .thenReturn(expectedList);

            List<IssueEntity> actual = mockIssueService.search(authorName, rating, user);

            assertThat(actual).isEqualTo(expectedList);
            verify(mockIssueRepository).findByAuthorNameContaining(authorName);
        }

        @Test
        @DisplayName("評価のみ入力されている場合、findByRatingが呼ばれること")
        void search_onlyRating(){
            String authorName = " ";
            Integer rating = 5;
            UserAccount user = new UserAccount();
            List<IssueEntity> expectedList = List.of(new IssueEntity());

            when(mockIssueRepository.findByRating(rating))
                    .thenReturn(expectedList);

            List<IssueEntity> actual = mockIssueService.search(authorName, rating, user);

            assertThat(actual).isEqualTo(expectedList);
            verify(mockIssueRepository).findByRating(rating);
        }

        @Test
        @DisplayName("著者名も評価も指定されていないとき、findAllが呼ばれること")
        void search_neitherAuthorNorRating(){
            String authorName = null;
            Integer rating = null;
            UserAccount user = new UserAccount();
            List<IssueEntity> expectedList = List.of(new IssueEntity());

            when(mockIssueRepository.findAll()).thenReturn(expectedList);

            List<IssueEntity> actual = mockIssueService.search(authorName, rating, user);

            assertThat(actual).isEqualTo(expectedList);
            verify(mockIssueRepository).findAll();
        }

    }

}