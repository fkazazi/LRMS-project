package com.lhind.AnnualLeaveApp.controller;

import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.security.ApplicationRoles;
import com.lhind.AnnualLeaveApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.Validator;

@AllArgsConstructor
@Controller
@RequestMapping(path = "/api")
public class UserController {

    private final UserService userService;
    private final Validator validator;

    @RequestMapping(path = "/admin/home")
    public String adminhomeView(){
        return "admin/adminHome";
    }

    @RequestMapping(path = "/user/home")
    public String userHomeView(){
        return "user/userHome";
    }

    @GetMapping("/admin/manage-users")
    public String getAllUsers(Model model){
        model.addAttribute("userList", userService.getUsers());
        return "admin/manageUsers";
    }

    @RequestMapping("/change-password")
    public String changePassword(){
        return "changePassword";
    }

    @PostMapping("/change-password/save")
    public String savePassword(@RequestParam("currentPassword") String currentPassword,
                               @Valid @RequestParam("newPassword") String newPassword){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = (User) userService.loadUserByUsername(username);
        String encodedPassword = user.getPassword();
        if (!bCryptPasswordEncoder.matches(currentPassword,encodedPassword)){
            return "errorInvalidPassword";
        }
        user.setPassword(newPassword);
        userService.save(user);
        if (user.getRole().equals(ApplicationRoles.USER))
            return "user/userHome";
        else if (user.getRole().equals(ApplicationRoles.SUPERVISOR))
            return "supervisor/supervisorHome";
        return "admin/adminHome";
    }
    @GetMapping("admin/manage-users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id){
        userService.delete(id);
        return "redirect:/api/admin/manage-users";
    }

    @GetMapping("admin/manage-users/edit/{id}")
    public String editUserForm (@PathVariable("id") Integer id, Model model){
        User user = userService.getById(id);
        user.setPassword(null);
        model.addAttribute("user", user);
        return "admin/editUserForm";
    }

    @RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
    public String loginView(){
        return "login";
    }

    @RequestMapping(path = "/login-error")
    public String loginError(){
        return "login-error";
    }

    @GetMapping("/logged-out")
    public String loggedOut(){
        return "logout";
    }

    @RequestMapping(path = "/access-denied")
    public String accessDenied(){
        return "accessDenied";
    }

    @GetMapping("/admin/add-user")
    public String newUser(Model model){
        User user = new User();
        model.addAttribute("user", user);
        return "admin/newUserForm";
    }
    @PostMapping("/admin/save-user")
    public String saveUser(@ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam(value = "newPassword", required = false) String newPassword){
         boolean isEdit = user.getId() != null;
         User existing = null;
         if (isEdit) {
             existing = userService.getById(user.getId());
             if (newPassword != null && !newPassword.isBlank()) {
                 if (newPassword.length() < 8) {
                     bindingResult.rejectValue("newPassword", "password.length",
                             "Password must be at least 8 characters long.");
                 } else {
                     user.setPassword(newPassword);
                 }
             } else {
                 user.setPassword(existing.getPassword());
             }
         }

         if (user.getRole() == null) {
             bindingResult.rejectValue("role", "role.required", "Please select a role.");
         }
         if (requiresDepartment(user.getRole()) && user.getDepartment() == null) {
             bindingResult.rejectValue("department", "department.required",
                     "Department is required for users and supervisors.");
         }
         if (user.getEmail() != null && userService.isEmailTakenByAnotherUser(user.getEmail().trim(), user.getId())) {
             bindingResult.rejectValue("email", "email.duplicate",
                     "An account with this email already exists.");
         }

         if (!bindingResult.hasErrors()) {
             validator.validate(user).forEach(violation ->
                     bindingResult.rejectValue(
                             violation.getPropertyPath().toString(),
                             violation.getMessage()));
         }

         if (bindingResult.hasErrors()){
            if (isEdit) {
                user.setPassword(null);
            }
            return isEdit ? "admin/editUserForm" : "admin/newUserForm";
        }
         if (user.getRole().equals(ApplicationRoles.ADMIN)) {
             user.setDepartment(null);
         }
         if (user.getEmail() != null) {
             user.setEmail(user.getEmail().trim().toLowerCase());
         }
         userService.save(user);
        return "admin/saveUserSuccess";
    }

    private boolean requiresDepartment(ApplicationRoles role) {
        return ApplicationRoles.USER.equals(role) || ApplicationRoles.SUPERVISOR.equals(role);
    }

}
