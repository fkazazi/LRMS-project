package com.lhind.AnnualLeaveApp.service.impl;

import com.lhind.AnnualLeaveApp.model.Department;
import com.lhind.AnnualLeaveApp.model.LeaveRequest;
import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.repository.LeaveRequestRepository;
import com.lhind.AnnualLeaveApp.service.LeaveRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;


@AllArgsConstructor
@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;


    public List<LeaveRequest> displayAllLeaves (Department department){
        return leaveRequestRepository.findAllByDepartment(department);
    }

    public List<LeaveRequest> getAllLeavesOnStatus(boolean flag, Department department) {
        return leaveRequestRepository.findAllByStatusAndDepartment(flag, department);
    }

    public List<LeaveRequest> getAllLeavesOfUser(Integer userId){
        return leaveRequestRepository.getAllLeavesOfUser(userId);
    }

    public List<LeaveRequest> getAllPendingLeaves (Department department){
        return leaveRequestRepository.findAllPendingByDepartment(department);
    }

    public LeaveRequest getLeaveById (Integer id){
        return leaveRequestRepository.getById(id);
    }

    public void deleteLeave (Integer id) {
        if (!leaveRequestRepository.getById(id).isFlag())
            throw new IllegalStateException("You cannot delete this request!");
        else
            leaveRequestRepository.deleteById(id);
    }

    public LeaveRequest saveLeave(LeaveRequest leaveRequest, User user){
        if (user.getDepartment() == null) {
            throw new IllegalStateException("You must be assigned to a department before submitting a leave request.");
        }
        validateLeaveDatesAndSetDuration(leaveRequest);
        leaveRequest.setUser(user);
        leaveRequestRepository.save(leaveRequest);
        return leaveRequest;
    }

    private void validateLeaveDatesAndSetDuration(LeaveRequest leaveRequest) {
        LocalDate today = LocalDate.now();
        LocalDate from;
        LocalDate to;
        try {
            from = LocalDate.parse(leaveRequest.getFromDate());
            to = LocalDate.parse(leaveRequest.getToDate());
        } catch (DateTimeParseException ex) {
            throw new IllegalStateException("Please enter valid start and end dates.");
        }
        if (from.isBefore(today)) {
            throw new IllegalStateException("Start date cannot be before today.");
        }
        if (to.isBefore(today)) {
            throw new IllegalStateException("End date cannot be before today.");
        }
        if (to.isBefore(from)) {
            throw new IllegalStateException("End date cannot be before start date.");
        }
        int duration = (int) ChronoUnit.DAYS.between(from, to) + 1;
        leaveRequest.setDuration(duration);
    }

    public boolean isLeaveInDepartment(LeaveRequest leaveRequest, Department department) {
        if (department == null || leaveRequest.getUser() == null || leaveRequest.getUser().getDepartment() == null) {
            return false;
        }
        return department.equals(leaveRequest.getUser().getDepartment());
    }


    public void confirmRejectLeave(LeaveRequest leaveRequest, boolean flag, String message){
        leaveRequest.setFlag(flag);
        leaveRequest.setPending(false);
        leaveRequestRepository.save(leaveRequest);
    }

}
