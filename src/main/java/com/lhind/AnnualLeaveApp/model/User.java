package com.lhind.AnnualLeaveApp.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

import com.lhind.AnnualLeaveApp.security.ApplicationRoles;
import lombok.*;
import org.hibernate.validator.constraints.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "please provide an email")
//    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    @Column(name = "email", unique = true)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Length(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private ApplicationRoles role;

    @Column(name = "department")
    @Enumerated(EnumType.STRING)
    private Department department;

    @NotEmpty(message = "Please provide first name!")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "Please provide last name!")
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "locked")
    private Boolean locked = false;

    @Column(name = "enabled")
    private Boolean enabled = true;

    //    @JsonManagedReference
    @OneToMany(targetEntity = LeaveRequest.class, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private List<LeaveRequest> leaves = new ArrayList<LeaveRequest>();

    public User(String email, String password, String role, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.role = ApplicationRoles.valueOf(role);
        this.firstName = firstName;
        this.lastName = lastName;
        this.locked = false;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);

    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<LeaveRequest> getDetails() {
        return leaves;
    }

    public void setRequests(List<LeaveRequest> leaves) {
        this.leaves = leaves;
    }

}
