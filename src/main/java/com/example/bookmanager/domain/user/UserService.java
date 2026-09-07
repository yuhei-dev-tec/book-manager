package com.example.bookmanager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(String email, String rawPassword){
        if (userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("このアドレスは既に登録されています");
        }

        UserAccount user = new UserAccount();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }

}
