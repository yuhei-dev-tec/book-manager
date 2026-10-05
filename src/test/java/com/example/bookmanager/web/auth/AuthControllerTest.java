package com.example.bookmanager.web.auth;

import com.example.bookmanager.config.SecurityConfig;
import com.example.bookmanager.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void ログイン画面を表示できること () throws Exception{
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void 新規登録画面を表示できること () throws Exception{
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("signupForm"));

    }

    @Test
    void フォームが正しいときユーザー登録されログイン画面にリダイレクトすること() throws Exception{
        mockMvc.perform(post("/signup")
                        .with(csrf())
                .param("email", "test@example.com")
                .param("password", "testPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?signupSuccess"));

        verify(userService, times(1)).registerUser("test@example.com", "testPassword");
    }

    @Test
    void バリデーションエラーがあるとき＿signup画面に戻りServiceが呼ばれないこと() throws Exception{
        mockMvc.perform(post("/signup")
                        .with(csrf())
                .param("email", "")
                .param("password", "testPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().hasErrors());

        verify(userService, never()).registerUser(anyString(), anyString());
    }

    @Test
    void 登録処理でIllegalArgumentExceptionが発生したとき＿signup画面に戻ること() throws Exception{
        doThrow(new IllegalArgumentException("メールアドレスが重複しています"))
                .when(userService).registerUser("exists@example.com", "testPassword");

        mockMvc.perform(post("/signup")
                        .with(csrf())
                .param("email", "exists@example.com")
                .param("password", "testPassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));

        verify(userService, times(1)).registerUser("exists@example.com", "testPassword");
    }

}