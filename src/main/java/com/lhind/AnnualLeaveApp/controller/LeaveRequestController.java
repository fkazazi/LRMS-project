package com.lhind.AnnualLeaveApp.controller;

import com.lhind.AnnualLeaveApp.model.LeaveRequest;
import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.service.LeaveRequestService;
import com.lhind.AnnualLeaveApp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;

@AllArgsConstructor
@Controller
@RequestMapping("/api")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final UserService userService;

    @GetMapping("user/my-leaves")
    public String userLeaves(Model model){
        User user = (User) userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("myLeaves", leaveRequestService.getAllLeavesOfUser(user.getId()));
        return "user/userLeaves";
    }

    @GetMapping("supervisor/home")
    public String allLeaves(Model model) throws ParseException {
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        model.addAttribute("allLeaves", leaveRequestService.displayAllLeaves(supervisor.getDepartment()));
        return "supervisor/supervisorHome";
    }

    @GetMapping("supervisor/accepted-leaves")
    public String acceptedLeaves(Model model){
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        model.addAttribute("activeLeaves", leaveRequestService.getAllLeavesOnStatus(true, supervisor.getDepartment()));
        return "supervisor/activeLeaves";
    }

    @GetMapping("supervisor/rejected-leaves")
    public String rejectedLeaves(Model model){
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        model.addAttribute("rejectedLeaves", leaveRequestService.getAllLeavesOnStatus(false, supervisor.getDepartment()));
        return "supervisor/rejectedLeaves";
    }

    @GetMapping("supervisor/pending-leaves")
    public String pendingLeaves(Model model){
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        model.addAttribute("pendingLeaves", leaveRequestService.getAllPendingLeaves(supervisor.getDepartment()));
        return "supervisor/pendingLeaves";
    }

    @PostMapping("supervisor/pending-leaves/confirm/{id}")
    public String confirmRequest (@PathVariable("id") Integer id){
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        LeaveRequest leaveRequest = leaveRequestService.getLeaveById(id);
        if (!leaveRequestService.isLeaveInDepartment(leaveRequest, supervisor.getDepartment())) {
            return "redirect:/api/access-denied";
        }
        leaveRequestService.confirmRejectLeave(leaveRequest, true, "");
        return "supervisor/confirmed";
    }

    @PostMapping("supervisor/pending-leaves/reject/{id}")
    public String rejectRequest (@PathVariable("id") Integer id, @RequestParam("message") String message){
        User supervisor = currentUser();
        String denied = supervisorDepartmentGuard(supervisor);
        if (denied != null) {
            return denied;
        }
        LeaveRequest leaveRequest = leaveRequestService.getLeaveById(id);
        if (!leaveRequestService.isLeaveInDepartment(leaveRequest, supervisor.getDepartment())) {
            return "redirect:/api/access-denied";
        }
        leaveRequestService.confirmRejectLeave(leaveRequest, false,  message);
        return "supervisor/rejected";
    }

    private User currentUser() {
        return (User) userService.loadUserByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private String supervisorDepartmentGuard(User supervisor) {
        if (supervisor.getDepartment() == null) {
            return "redirect:/api/access-denied";
        }
        return null;
    }

    @RequestMapping(path = "user/new-request")
    public String newRequest(Model model){
        LeaveRequest leaveRequest = new LeaveRequest();
        model.addAttribute("leave_details", leaveRequest);
        model.addAttribute("minDate", LocalDate.now().toString());
        return "/user/newRequest";
    }

    @PostMapping("user/new-request/save")
    public String saveRequest(@ModelAttribute("leave_details") LeaveRequest leaveRequest, Model model) throws ParseException {
            User user = (User) userService.loadUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            try {
                leaveRequestService.saveLeave(leaveRequest, user);
            } catch (IllegalStateException ex) {
                if (ex.getMessage().startsWith("You must be assigned")) {
                    return "user/newRequestError";
                }
                model.addAttribute("leave_details", leaveRequest);
                model.addAttribute("minDate", LocalDate.now().toString());
                model.addAttribute("dateError", ex.getMessage());
                return "/user/newRequest";
            }
        return "user/newRequestSuccess";
    }
}
