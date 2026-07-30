# Leave Requests Management System

A web application for submitting, reviewing, and tracking employee leave requests inside an organization. Employees request time off; supervisors in the same department approve or reject those requests; administrators manage user accounts and roles.

## Main idea

The system replaces informal leave handling (email or spreadsheets) with a single place where:

- **Employees** submit leave with dates, type, and reason.
- **Supervisors** see only requests from their **department** and decide approve or reject.
- **Admins** create and maintain users (role and department assignment).

Access is controlled by **role** (Admin, Supervisor, User) and, for supervisors and employees, by **department** (IT, Finance, Legal).

## Core functionalities

### Admin
- Log in and access the admin dashboard.
- View all users, add new users, edit users, delete users.
- Assign **role** (Admin, Supervisor, User) and **department** (required for User and Supervisor).
- Enforce unique email addresses and password rules (minimum 8 characters on create).

### User (employee)
- Log in with email and password.
- Submit a **new leave request** (start/end date, leave type, reason).
- Date rules: cannot choose dates before today; end date cannot be before start date; **one-day** leave is allowed (same start and end date); duration is calculated automatically.
- Leave types: **Medical**, **Vacation**, **Annual**.
- View **my requests** with status: Pending, Accepted, or Rejected.
- Change password.

### Supervisor
- Log in with email and password (must have a department).
- **Home / All leaves**: all requests from employees in the **same department** (pending and decided).
- **Pending leaves**: confirm or reject requests (reject requires a short reason).
- Filtered views for accepted and rejected requests (department only).
- Change password.

### General
- Spring Security login; each role is redirected to the correct home page after sign in.
- Log out ends the session and returns to the login flow.

## Technology stack

| Layer | Technology |
|--------|------------|
| Backend | Java 11, **Spring Boot** 2.5 |
| Web | **Spring MVC**, **Thymeleaf** (server-rendered HTML) |
| Security | **Spring Security** (form login, role-based URLs) |
| Data | **Spring Data JPA**, **Hibernate** |
| Querying | **SQL** (native queries)  
| Database (local) | **H2** in-memory (`spring.profiles.active=h2`) |
| Build | **Maven** (wrapper: `mvnw.cmd`) |
| Passwords | **BCrypt** hashing |

## How to run

### Requirements
- **JDK 11** (the project’s `run-local.ps1` expects Temurin 11 under `%LOCALAPPDATA%\jdk-11\` if you use that script’s path).

### Option 1 — Double-click launcher (opens browser)
1. Double-click **`Open Leave App.bat`** in the project folder.
2. A **server** window opens — keep it open while you use the app.
3. The browser should open **http://localhost:8080/api/login** when the server is ready (first start can take 1–2 minutes while Maven downloads dependencies).

### Option 2 — PowerShell
1. Run **`run-local.ps1`** from the project folder.
2. When the app has started, open **http://localhost:8080/api/login** in your browser.

### Stop the app
Close the server terminal window (or press Ctrl+C in that window).

### Data note (H2)
With the default H2 in-memory database, **all users and leave requests are cleared when the app stops**. On the next start, the default admin account is created again; you will need to recreate other users unless you switch to a persistent database.

## Default admin credentials

| Field | Value |
|--------|--------|
| Email | `admin@admin.com` |
| Password | `adminadmin` |

Use this account to log in as admin, then create supervisors and employees (assign each User and Supervisor to a department).

## URL

- Login: **http://localhost:8080/api/login**
