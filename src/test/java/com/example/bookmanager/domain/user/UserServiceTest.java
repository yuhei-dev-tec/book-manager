package com.example.bookmanager.domain.user;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    class registerUserTest{

        @Test
        @DisplayName("まだ登録されていないメールアドレスが入力された場合、新しいユーザーとして登録されること")
        void registerNewUserTest(){
            String email = "test@example.com";
            String rawPassword = "testPassword";
            String encodedPassword = "encoded_password";

            when(userAccountRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

            userService.registerUser(email, rawPassword);

            verify(userAccountRepository, times(1)).findByEmail(email);
            verify(passwordEncoder, times(1)).encode(rawPassword);

            ArgumentCaptor<UserAccount> userCaptor = ArgumentCaptor.forClass(UserAccount.class);
            verify(userAccountRepository, times(1)).save(userCaptor.capture());

            UserAccount savedUser = userCaptor.getValue();
            assertEquals(email, savedUser.getEmail());
            assertEquals(encodedPassword, savedUser.getPassword());
            assertEquals("ROLE_USER", savedUser.getRole());
        }

        @Test
        @DisplayName("メールアドレスが既に登録されている場合、IllegalArgumentExceptionが発生すること")
        void registerUser_AlreadyExists_ThrowsException(){
            String email = "test@example.com";
            String rawPassword = "testPassword";
            UserAccount existingUser = new UserAccount();

            when(userAccountRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> userService.registerUser(email, rawPassword)
            );

            assertEquals("このアドレスは既に登録されています", exception.getMessage());

            verify(passwordEncoder, never()).encode(any());
            verify(userAccountRepository, never()).save(any());

        }

    }
}