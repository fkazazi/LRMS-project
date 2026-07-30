package com.lhind.AnnualLeaveApp;

import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.repository.UserRepository;
import com.lhind.AnnualLeaveApp.security.ApplicationRoles;
import com.lhind.AnnualLeaveApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandLineStartupRunner implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    public static final String DEFAULT_ADMIN_EMAIL = "admin@admin.com";
    public static final String DEFAULT_ADMIN_PASSWORD = "adminadmin";

    @Override
    public void run(String... args) {
        User admin = userRepository.findByEmail(DEFAULT_ADMIN_EMAIL).orElseGet(() ->
                new User(DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD,
                        ApplicationRoles.ADMIN.name(), "Admin", "Admin"));
        admin.setEmail(DEFAULT_ADMIN_EMAIL);
        admin.setRole(ApplicationRoles.ADMIN);
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setDepartment(null);
        admin.setEnabled(true);
        admin.setLocked(false);
        admin.setPassword(DEFAULT_ADMIN_PASSWORD);
        userService.save(admin);
        log.info("Default admin ready: {} / {}", DEFAULT_ADMIN_EMAIL, DEFAULT_ADMIN_PASSWORD);
    }
}
