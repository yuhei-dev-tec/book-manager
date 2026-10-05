package com.example.bookmanager.web.book;

import com.example.bookmanager.config.SecurityConfig;
import com.example.bookmanager.web.dto.RakutenBookDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(BookSearchController.class)
@Import(SecurityConfig.class)
class BookSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RakutenBookService rakutenBookService;

    @Test
    @DisplayName("クエリパラメータisbnを指定し正常に書籍情報を取得できること")
    @WithMockUser
    void searchBook_Success() throws Exception{
        String testIsbn = "1234567891234";
        RakutenBookDto.BookItem mockBookItem = new RakutenBookDto.BookItem();

        Mockito.when(rakutenBookService.searchByIsbn(testIsbn))
                        .thenReturn(mockBookItem);

        mockMvc.perform(get("/api/search")
                        .with(csrf())
                .param("isbn", testIsbn))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("クエリパラメータisbnが存在しない場合400エラー（Bad Request）を返すこと")
    @WithMockUser
    void searchBook_MissingParam_ReturnsBadRequest() throws Exception{
        mockMvc.perform(get("/api/search"))
                .andExpect(status().isBadRequest());
    }

}