package com.lhind.AnnualLeaveApp.service;

import com.lhind.AnnualLeaveApp.model.Department;
import com.lhind.AnnualLeaveApp.model.LeaveRequest;
import com.lhind.AnnualLeaveApp.model.User;

import java.util.List;

public interface LeaveRequestService {

    List<LeaveRequest> displayAllLeaves (Department department);

    List<LeaveRequest> getAllLeavesOnStatus(boolean flag, Department department);

    List<LeaveRequest> getAllLeavesOfUser(Integer userId);

    List<LeaveRequest> getAllPendingLeaves (Department department);

    LeaveRequest getLeaveById (Integer id);

    void deleteLeave (Integer id);

    LeaveRequest saveLeave(LeaveRequest leaveRequest, User user);

    void confirmRejectLeave(LeaveRequest leaveRequest, boolean flag, String message);

    boolean isLeaveInDepartment(LeaveRequest leaveRequest, Department department);
}
