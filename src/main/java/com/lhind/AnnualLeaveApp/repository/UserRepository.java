package com.lhind.AnnualLeaveApp.repository;

import com.lhind.AnnualLeaveApp.model.Department;
import com.lhind.AnnualLeaveApp.model.User;
import com.lhind.AnnualLeaveApp.security.ApplicationRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    List<User> findByRoleAndDepartment(ApplicationRoles role, Department department);
}
