package com.example.bookmanager.web.issue;

import com.example.bookmanager.domain.issue.IssueEntity;
import com.example.bookmanager.domain.issue.IssueService;
import com.example.bookmanager.domain.user.UserAccount;
import com.example.bookmanager.domain.user.UserAccountRepository;
import com.example.bookmanager.web.book.RakutenBookService;
import com.example.bookmanager.web.dto.RakutenBookDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IssueController.class)
class IssueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserAccountRepository mockUserAccountRepository;

    @MockitoBean
    private IssueService issueService;

    @MockitoBean
    private RakutenBookService rakutenBookService;

    @Nested
    @DisplayName("showListメソッドのクラス")
    class ShowListTest{

        @Test
        @DisplayName("showList:クエリパラメータとログイン情報に基づき正常に一覧画面が表示される")
        @WithMockUser(username = "test@example.com")
        void showList_Success() throws Exception{

            UserAccount mockUser = new UserAccount();

            List<IssueEntity>mockIssueList = List.of(new IssueEntity());

            when(mockUserAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
            when(issueService.search(eq("testAuthor"), eq(5), any(UserAccount.class)))
                    .thenReturn(mockIssueList);

            mockMvc.perform(get("/issues")
                    .param("authorName", "testAuthor")
                    .param("rating", "5"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("issues/list"))
                    .andExpect(model().attribute("issueList", mockIssueList))
                    .andExpect(model().attribute("searchAuthorName", "testAuthor"))
                    .andExpect(model().attribute("searchRating", 5));

        }

        @Test
        @DisplayName("showList:パラメータ指定がない場合でも正常に検索される")
        @WithMockUser(username = "test@example.com")
        void showList_WithNullParams() throws Exception{
             UserAccount userAccount = new UserAccount();

             when(mockUserAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userAccount));
             when(issueService.search(null, null, userAccount)).thenReturn(List.of());

             mockMvc.perform(get("/issues"))
                     .andExpect(status().isOk())
                     .andExpect(view().name("issues/list"))
                     .andExpect(model().attribute("issueList", List.of()))
                     .andExpect(model().attributeDoesNotExist("searchAuthorName"))
                     .andExpect(model().attributeDoesNotExist("searchRating"));
        }

    }

    @Test
    @DisplayName("書籍登録画面が表示できること")
    @WithMockUser(username = "test@example.com")
    void showCreationFormTest () throws Exception{
        mockMvc.perform(get("/issues/creationForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/creationForm"))
                .andExpect(model().attributeExists("issueForm"));
    }

    @Nested
    @DisplayName("createメソッドのテスト")
    class CreateTest{

        @Test
        @WithMockUser(username = "test@example.com")
        @DisplayName("create:クエリパラメータとログイン情報に基づき書籍情報が登録され,一覧画面にリダイレクトする")
        void create_Success() throws Exception{
            UserAccount mockUser = new UserAccount();
            when(mockUserAccountRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

            mockMvc.perform(post("/issues")
                            .with(csrf())
                    .param("bookTitle", "テスト書籍")
                    .param("authorName", "テスト一郎")
                    .param("rating", "5"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/issues"));

            verify(issueService).create(eq("テスト書籍"), eq("テスト一郎"), eq(5), any(UserAccount.class));

        }

        @Test
        @WithMockUser(username = "test@example.com")
        @DisplayName("create:バリデーションエラーがある場合、登録処理をせず、入力画面を表示")
        void create_ValidationError() throws Exception{
            mockMvc.perform(post("/issues")
                            .with(csrf())
                    .param("bookTitle","")
                    .param("authorName", "テスト一郎")
                    .param("rating", "5"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("issues/creationForm"));
        }

    }

    @Test
    @DisplayName("詳細画面を表示できること")
    @WithMockUser(username = "test@example.com")
    void showDetail_Success() throws Exception{
        Long issueId = 1L;

        IssueEntity mockIssue = new IssueEntity();
        mockIssue.setId(issueId);
        mockIssue.setBookTitle("テスト書籍");
        mockIssue.setAuthorName("テスト一郎");

        RakutenBookDto.BookItem mockBookItem = new RakutenBookDto.BookItem();

        when(issueService.findById(issueId)).thenReturn(mockIssue);
        when(rakutenBookService.searchByKeyword("テスト書籍", "テスト一郎")).thenReturn(mockBookItem);

        mockMvc.perform(get("/issues/{issueId}", issueId))
                .andExpect(status().isOk())
                .andExpect(view().name("issues/detail"))
                .andExpect(model().attributeExists("issue"))
                .andExpect(model().attribute("issue", mockIssue))
                .andExpect(model().attributeExists("rakutenBook"))
                .andExpect(model().attribute("rakutenBook", mockBookItem));

        verify(issueService).findById(issueId);
        verify(rakutenBookService).searchByKeyword("テスト書籍", "テスト一郎");




    }

}