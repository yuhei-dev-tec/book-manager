package com.example.bookmanager.domain.user;

import com.example.bookmanager.domain.issue.IssueRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserAccountRepository mockUserAccountRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Nested
    @DisplayName("loadUserByUserNameメソッドに対するテスト")
    class LoadUserByUserNameTest{

        @Test
        @DisplayName("存在するメールアドレスを入力すると、対応するUserDetailsが返されること")
        void loadUserByUserName_success(){
            String email = "test@example.com";

            UserAccount mockUser = new UserAccount();
            mockUser.setEmail(email);
            mockUser.setPassword("testPassword");
            mockUser.setRole("ROLE_USER");

            when(mockUserAccountRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

            UserDetails result = customUserDetailService.loadUserByUsername(email);

            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo(email);
            assertThat(result.getPassword()).isEqualTo("testPassword");
            assertThat(result.getAuthorities())
                    .extracting("authority")
                    .containsExactly("ROLE_USER");

            verify(mockUserAccountRepository).findByEmail(email);
        }

        @Test
        @DisplayName("存在しないメールアドレスが指定された場合、UsernameNotFoundExceptionが返されること")
        void loadUserByUserName_notFound(){
            String email = "test@example.com";

            UserAccount mockUser = new UserAccount();
            mockUser.setEmail(email);

            when(mockUserAccountRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> customUserDetailService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessageContaining("ユーザーが見つかりません" + email);

            verify(mockUserAccountRepository).findByEmail(email);
        }
    }


}