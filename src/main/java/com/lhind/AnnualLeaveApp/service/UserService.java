package com.lhind.AnnualLeaveApp.service;

import com.lhind.AnnualLeaveApp.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {

    User save(User user);

    List<User> getUsers();

    void delete(Integer id);

    User getById(Integer id);

    UserDetails loadUserByUsername(String email);

    boolean isEmailTakenByAnotherUser(String email, Integer excludeUserId);
}
