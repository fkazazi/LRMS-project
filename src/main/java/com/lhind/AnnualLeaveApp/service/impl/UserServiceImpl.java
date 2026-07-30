package com.lhind.AnnualLeaveApp.service.impl;

import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.repository.UserRepository;
import com.lhind.AnnualLeaveApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User save(User user) {
        String password = user.getPassword();
        if (password != null && !password.isBlank() && !isBcryptHash(password)) {
            user.setPassword(bCryptPasswordEncoder.encode(password));
        }
        return userRepository.save(user);
    }

    private boolean isBcryptHash(String password) {
        if (password == null || password.length() < 60) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$");
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getById(Integer id) {
        return userRepository.getById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("Email not provided");
        }
        String normalizedEmail = email.trim().toLowerCase();
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Email %s not found", normalizedEmail)));
    }

    @Override
    public boolean isEmailTakenByAnotherUser(String email, Integer excludeUserId) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.findByEmail(email.trim().toLowerCase())
                .filter(user -> excludeUserId == null || !excludeUserId.equals(user.getId()))
                .isPresent();
    }

}
