import model.*;
import model.enums.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    // -------------------------------------------------------------------------
    // In-memory repositories  (replaced by DB layer later)
    // -------------------------------------------------------------------------
    private static final List<Employee>        employees        = new ArrayList<>();
    private static final List<Approver>        approvers        = new ArrayList<>();
    private static final List<Admin>           admins           = new ArrayList<>();
    private static final List<LeaveRequest>    leaveRequests    = new ArrayList<>();
    private static final List<ApprovalMessage> approvalMessages = new ArrayList<>();

    private static int employeeIdSeq        = 1;
    private static int approverIdSeq        = 1;
    private static int adminIdSeq           = 1;
    private static int leaveRequestIdSeq    = 1;
    private static int approvalMessageIdSeq = 1;

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // -------------------------------------------------------------------------
    // Seed data
    // -------------------------------------------------------------------------
    static {
        Admin defaultAdmin = new Admin("admin", "admin123");
        defaultAdmin.setId(adminIdSeq++);
        admins.add(defaultAdmin);

        Approver senior = new Approver("Alice Senior", "alice", "pass1",
                "alice@company.com", Position.SENIOR);
        senior.setId(approverIdSeq++);
        approvers.add(senior);

        Approver middle = new Approver("Bob Middle", "bob", "pass2",
                "bob@company.com", Position.MIDDLE);
        middle.setId(approverIdSeq++);
        approvers.add(middle);

        Employee intern = new Employee("Carol Intern", "carol", "pass3",
                "carol@company.com", Position.INTERN, Department.IT);
        intern.setId(employeeIdSeq++);
        intern.setEmailVerified(true);
        employees.add(intern);

        Employee junior = new Employee("Dave Junior", "dave", "pass4",
                "dave@company.com", Position.JUNIOR, Department.FINANCE);
        junior.setId(employeeIdSeq++);
        junior.setEmailVerified(true);
        employees.add(junior);
    }

    // =========================================================================
    // Entry point
    // =========================================================================
    public static void main(String[] args) {
        printBanner();
        while (true) {
            printMainMenu();
            String choice = prompt("> ").trim();
            switch (choice) {
                case "1" -> loginFlow();
                case "2" -> signUpFlow();
                case "0" -> { System.out.println("Goodbye."); return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    // =========================================================================
    // Authentication
    // =========================================================================
    private static void loginFlow() {
        System.out.println("\n─── Login ───");
        String login    = prompt("Login    : ");
        String password = prompt("Password : ");

        // Check Admin
        admins.stream()
              .filter(a -> a.getLogin().equals(login) && a.getPassword().equals(password))
              .findFirst()
              .ifPresentOrElse(Main::adminDashboard, () -> {

            // Check Approver
            approvers.stream()
                     .filter(a -> a.getLogin().equals(login) && a.getPassword().equals(password))
                     .findFirst()
                     .ifPresentOrElse(Main::approverDashboard, () -> {

                // Check Employee
                employees.stream()
                         .filter(e -> e.getLogin().equals(login) && e.getPassword().equals(password))
                         .findFirst()
                         .ifPresentOrElse(e -> {
                             if (!e.isEmailVerified()) {
                                 warn("Your email is not verified. Check your inbox.");
                             } else {
                                 employeeDashboard(e);
                             }
                         }, () -> warn("Invalid credentials."));
            });
        });
    }

    private static void signUpFlow() {
        System.out.println("\n─── Sign Up (Employee) ───");
        String fullName = promptRequired("Full name   : ");
        String login    = promptRequired("Login       : ");

        boolean loginTaken = employees.stream().anyMatch(e -> e.getLogin().equals(login))
                          || approvers.stream().anyMatch(a -> a.getLogin().equals(login));
        if (loginTaken) { warn("Login already in use."); return; }

        String password = promptPassword();
        String email    = promptEmail();
        Department dept = pickDepartment();
        Position   pos  = pickPositionFor("employee");

        Employee emp = new Employee(fullName, login, password, email, pos, dept);
        emp.setId(employeeIdSeq++);
        emp.setVerificationToken(UUID.randomUUID().toString());
        employees.add(emp);

        // TODO: send verification email via JavaMail
        System.out.println("\n✓ Account created. A verification link has been sent to " + email);
        System.out.println("  (Development mode — account is auto-verified.)");
        emp.setEmailVerified(true);   // remove when real email is wired up
    }

    // =========================================================================
    // Employee dashboard
    // =========================================================================
    private static void employeeDashboard(Employee emp) {
        System.out.println("\n═══ " + emp.getDashboardTitle() + " ═══");
        while (true) {
            System.out.println("""

                    [1] New leave request
                    [2] My leave requests
                    [0] Log out""");
            switch (prompt("> ").trim()) {
                case "1" -> createLeaveRequest(emp);
                case "2" -> listLeaveRequestsEmployee(emp);
                case "0" -> { System.out.println("Logged out."); return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void createLeaveRequest(Employee emp) {
        System.out.println("\n─── New Leave Request ───");
        RequestType type  = pickRequestType();
        String title      = promptRequired("Title   : ");
        String content    = promptRequired("Details : ");
        LocalDateTime start = promptDateTime("Leave start (yyyy-MM-dd HH:mm) : ");
        LocalDateTime end   = promptDateTimeAfter("Leave end   (yyyy-MM-dd HH:mm) : ", start);
        String docPath      = prompt("Supporting document path (Enter to skip) : ").trim();

        LeaveRequest req = new LeaveRequest(emp.getId(), type, title, content,
                start, end, docPath.isEmpty() ? null : docPath);
        req.setId(leaveRequestIdSeq++);
        leaveRequests.add(req);
        System.out.println("✓ Leave request submitted. Status: " + req.getStatus());
    }

    private static void listLeaveRequestsEmployee(Employee emp) {
        List<LeaveRequest> mine = leaveRequests.stream()
                .filter(r -> r.getEmployeeId() == emp.getId())
                .collect(Collectors.toList());
        if (mine.isEmpty()) { System.out.println("No leave requests found."); return; }

        System.out.println("\n─── Your Leave Requests ───");
        for (int i = 0; i < mine.size(); i++) {
            LeaveRequest r = mine.get(i);
            System.out.printf("[%d] #%d  %-35s  %s%n",
                    i + 1, r.getId(), r.getTitle(), r.getStatus());
        }

        String input = prompt("\nEnter number to view (Enter to go back) : ").trim();
        if (input.isEmpty()) return;
        int idx = parseIndex(input, mine.size());
        if (idx < 0) return;
        viewLeaveRequestEmployee(mine.get(idx), emp);
    }

    private static void viewLeaveRequestEmployee(LeaveRequest req, Employee emp) {
        printLeaveRequestDetails(req);
        System.out.println("""

                [1] Delete this request
                [2] View approval message
                [0] Back""");
        if (req.isPending()) System.out.println("  (No approval yet — status is Pending)");

        switch (prompt("> ").trim()) {
            case "1" -> deleteLeaveRequestEmployee(req, emp);
            case "2" -> {
                if (req.isPending()) warn("No approval message yet.");
                else viewApprovalMessage(req);
            }
            case "0" -> {}
            default  -> warn("Invalid option.");
        }
    }

    private static void deleteLeaveRequestEmployee(LeaveRequest req, Employee emp) {
        if (!confirm("Delete request \"" + req.getTitle() + "\"? [y/n] : ")) return;
        leaveRequests.remove(req);
        approvalMessages.removeIf(m -> m.getLeaveRequestId() == req.getId());
        System.out.println("✓ Deleted.");
    }

    // =========================================================================
    // Approver dashboard
    // =========================================================================
    private static void approverDashboard(Approver approver) {
        System.out.println("\n═══ " + approver.getDashboardTitle() + " ═══");
        while (true) {
            System.out.println("""

                    [1] Leave requests (juniors)
                    [0] Log out""");
            switch (prompt("> ").trim()) {
                case "1" -> listLeaveRequestsApprover(approver);
                case "0" -> { System.out.println("Logged out."); return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void listLeaveRequestsApprover(Approver approver) {
        Set<Integer> juniorIds = employees.stream()
                .filter(approver::canReview)
                .map(Employee::getId)
                .collect(Collectors.toSet());

        List<LeaveRequest> visible = leaveRequests.stream()
                .filter(r -> juniorIds.contains(r.getEmployeeId()))
                .collect(Collectors.toList());

        if (visible.isEmpty()) { System.out.println("No leave requests from junior employees."); return; }

        System.out.println("\n─── Junior Employees' Leave Requests ───");
        for (int i = 0; i < visible.size(); i++) {
            LeaveRequest r = visible.get(i);
            System.out.printf("[%d] #%d  %-35s  %s%n",
                    i + 1, r.getId(), r.getTitle(), r.getStatus());
        }

        String input = prompt("\nEnter number to view (Enter to go back) : ").trim();
        if (input.isEmpty()) return;
        int idx = parseIndex(input, visible.size());
        if (idx < 0) return;
        viewLeaveRequestApprover(visible.get(idx), approver);
    }

    private static void viewLeaveRequestApprover(LeaveRequest req, Approver approver) {
        printLeaveRequestDetails(req);

        if (req.isPending()) {
            System.out.println("""

                    [1] Create approval message
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> createApprovalMessage(req, approver);
                case "0" -> {}
                default  -> warn("Invalid option.");
            }
        } else {
            System.out.println("""

                    [1] View approval message
                    [2] Delete approval message
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> viewApprovalMessage(req);
                case "2" -> deleteApprovalMessage(req);
                case "0" -> {}
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void createApprovalMessage(LeaveRequest req, Approver approver) {
        System.out.println("\n─── Create Approval Message ───");
        System.out.println("[1] Approve   [2] Deny");
        String dec = prompt("Decision : ").trim();
        LeaveStatus decision;
        if ("1".equals(dec))      decision = LeaveStatus.APPROVED;
        else if ("2".equals(dec)) decision = LeaveStatus.DENIED;
        else { warn("Invalid choice."); return; }

        String title   = promptRequired("Title   : ");
        String content = promptRequired("Message : ");

        ApprovalMessage msg = new ApprovalMessage(req.getId(), approver.getId(),
                title, content, decision);
        msg.setId(approvalMessageIdSeq++);
        approvalMessages.add(msg);

        req.setStatus(decision);
        System.out.println("✓ Approval message saved. Request status → " + decision);
    }

    private static void viewApprovalMessage(LeaveRequest req) {
        approvalMessages.stream()
                .filter(m -> m.getLeaveRequestId() == req.getId())
                .findFirst()
                .ifPresentOrElse(m -> {
                    System.out.println("\n─── Approval Message ───");
                    System.out.println("Title    : " + m.getTitle());
                    System.out.println("Decision : " + m.getDecisionMade());
                    System.out.println("Time     : " + m.getApprovalTime().format(DT_FMT));
                    System.out.println("─".repeat(40));
                    System.out.println(m.getContent());
                }, () -> System.out.println("No approval message found for this request."));
    }

    private static void deleteApprovalMessage(LeaveRequest req) {
        Optional<ApprovalMessage> opt = approvalMessages.stream()
                .filter(m -> m.getLeaveRequestId() == req.getId())
                .findFirst();
        if (opt.isEmpty()) { warn("No approval message to delete."); return; }
        if (!confirm("Delete approval message? This resets the request to Pending. [y/n] : ")) return;
        approvalMessages.remove(opt.get());
        req.setStatus(LeaveStatus.PENDING);
        System.out.println("✓ Approval message deleted. Request reset to Pending.");
    }

    // =========================================================================
    // Admin dashboard
    // =========================================================================
    private static void adminDashboard(Admin admin) {
        System.out.println("\n═══ " + admin.getDashboardTitle() + " ═══");
        while (true) {
            System.out.println("""

                    ── Employee actions ──
                    [1] New leave request (as employee)
                    [2] List / manage all leave requests

                    ── Approver actions ──
                    [3] List / manage all leave requests (approver view)

                    ── Admin CRUD ──
                    [4] Manage Employees
                    [5] Manage Approvers
                    [6] Manage Admins
                    [7] Manage Leave Requests
                    [8] Manage Approval Messages

                    [0] Log out""");

            switch (prompt("> ").trim()) {
                case "1" -> { Employee emp = pickEmployee(); if (emp != null) createLeaveRequest(emp); }
                case "2" -> adminListAllLeaveRequests(false);
                case "3" -> adminListAllLeaveRequests(true);
                case "4" -> manageEmployees();
                case "5" -> manageApprovers();
                case "6" -> manageAdmins();
                case "7" -> manageLeaveRequests();
                case "8" -> manageApprovalMessages();
                case "0" -> { System.out.println("Logged out."); return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void adminListAllLeaveRequests(boolean approverView) {
        if (leaveRequests.isEmpty()) { System.out.println("No leave requests."); return; }
        System.out.println("\n─── All Leave Requests ───");
        for (int i = 0; i < leaveRequests.size(); i++) {
            LeaveRequest r = leaveRequests.get(i);
            System.out.printf("[%d] #%d  %-35s  %s%n",
                    i + 1, r.getId(), r.getTitle(), r.getStatus());
        }
        String input = prompt("\nEnter number to view (Enter to go back) : ").trim();
        if (input.isEmpty()) return;
        int idx = parseIndex(input, leaveRequests.size());
        if (idx < 0) return;
        LeaveRequest req = leaveRequests.get(idx);

        if (approverView) {
            printLeaveRequestDetails(req);
            System.out.println("""

                    [1] Create / overwrite approval message
                    [2] View approval message
                    [3] Delete approval message
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> {
                    Approver ap = approvers.isEmpty() ? null : approvers.get(0);
                    if (ap == null) warn("No approver exists to attribute message to.");
                    else createApprovalMessage(req, ap);
                }
                case "2" -> viewApprovalMessage(req);
                case "3" -> deleteApprovalMessage(req);
                case "0" -> {}
                default  -> warn("Invalid option.");
            }
        } else {
            viewLeaveRequestEmployee(req, null);
        }
    }

    // ------ Admin CRUD: Employees -------

    private static void manageEmployees() {
        while (true) {
            System.out.println("""

                    ─── Employees ───
                    [1] List all
                    [2] Create
                    [3] Edit
                    [4] Delete
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> listAll(employees);
                case "2" -> signUpFlow();
                case "3" -> editEmployee();
                case "4" -> deleteEmployee();
                case "0" -> { return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void editEmployee() {
        Employee emp = pickEmployee();
        if (emp == null) return;
        System.out.println("Editing: " + emp);
        System.out.println("[1] Full name  [2] Email  [3] Department  [4] Position  [0] Cancel");
        switch (prompt("> ").trim()) {
            case "1" -> emp.setFullName(promptRequired("New full name : "));
            case "2" -> emp.setEmail(promptEmail());
            case "3" -> emp.setDepartment(pickDepartment());
            case "4" -> emp.setPosition(pickPositionFor("employee"));
            case "0" -> {}
            default  -> warn("Invalid option.");
        }
        System.out.println("✓ Updated: " + emp);
    }

    private static void deleteEmployee() {
        Employee emp = pickEmployee();
        if (emp == null) return;
        if (!confirm("Delete employee \"" + emp.getFullName() + "\"? [y/n] : ")) return;
        leaveRequests.removeIf(r -> r.getEmployeeId() == emp.getId());
        employees.remove(emp);
        System.out.println("✓ Employee and their requests deleted.");
    }

    // ------ Admin CRUD: Approvers -------

    private static void manageApprovers() {
        while (true) {
            System.out.println("""

                    ─── Approvers ───
                    [1] List all
                    [2] Create
                    [3] Edit
                    [4] Delete
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> listAll(approvers);
                case "2" -> createApprover();
                case "3" -> editApprover();
                case "4" -> deleteApprover();
                case "0" -> { return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    private static void createApprover() {
        System.out.println("\n─── New Approver ───");
        String fullName = promptRequired("Full name : ");
        String login    = promptRequired("Login     : ");
        boolean taken = approvers.stream().anyMatch(a -> a.getLogin().equals(login))
                     || employees.stream().anyMatch(e -> e.getLogin().equals(login));
        if (taken) { warn("Login already in use."); return; }
        String password = promptPassword();
        String email    = promptEmail();
        Position pos    = pickPositionFor("approver");

        Approver ap = new Approver(fullName, login, password, email, pos);
        ap.setId(approverIdSeq++);
        approvers.add(ap);
        System.out.println("✓ Approver created: " + ap);
    }

    private static void editApprover() {
        Approver ap = pickApprover();
        if (ap == null) return;
        System.out.println("Editing: " + ap);
        System.out.println("[1] Full name  [2] Email  [3] Position  [0] Cancel");
        switch (prompt("> ").trim()) {
            case "1" -> ap.setFullName(promptRequired("New full name : "));
            case "2" -> ap.setEmail(promptEmail());
            case "3" -> ap.setPosition(pickPositionFor("approver"));
            case "0" -> {}
            default  -> warn("Invalid option.");
        }
        System.out.println("✓ Updated: " + ap);
    }

    private static void deleteApprover() {
        Approver ap = pickApprover();
        if (ap == null) return;
        if (!confirm("Delete approver \"" + ap.getFullName() + "\"? [y/n] : ")) return;
        approvers.remove(ap);
        System.out.println("✓ Approver deleted.");
    }

    // ------ Admin CRUD: Admins -------

    private static void manageAdmins() {
        while (true) {
            System.out.println("""

                    ─── Admins ───
                    [1] List all
                    [2] Create
                    [3] Delete
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> listAll(admins);
                case "2" -> {
                    String login    = promptRequired("Login    : ");
                    String password = promptPassword();
                    Admin a = new Admin(login, password);
                    a.setId(adminIdSeq++);
                    admins.add(a);
                    System.out.println("✓ Admin created.");
                }
                case "3" -> {
                    if (admins.size() == 1) { warn("Cannot delete the last admin."); break; }
                    listAll(admins);
                    int idx = parseIndex(prompt("Select admin to delete : ").trim(), admins.size());
                    if (idx >= 0) { admins.remove(idx); System.out.println("✓ Deleted."); }
                }
                case "0" -> { return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    // ------ Admin CRUD: Leave Requests -------

    private static void manageLeaveRequests() {
        while (true) {
            System.out.println("""

                    ─── Leave Requests ───
                    [1] List all
                    [2] Create (on behalf of employee)
                    [3] Edit status
                    [4] Delete
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> listAll(leaveRequests);
                case "2" -> { Employee emp = pickEmployee(); if (emp != null) createLeaveRequest(emp); }
                case "3" -> {
                    LeaveRequest req = pickLeaveRequest();
                    if (req == null) break;
                    System.out.println("[1] Pending  [2] Approved  [3] Denied");
                    switch (prompt("New status : ").trim()) {
                        case "1" -> req.setStatus(LeaveStatus.PENDING);
                        case "2" -> req.setStatus(LeaveStatus.APPROVED);
                        case "3" -> req.setStatus(LeaveStatus.DENIED);
                        default  -> warn("Invalid.");
                    }
                    System.out.println("✓ Status updated.");
                }
                case "4" -> {
                    LeaveRequest req = pickLeaveRequest();
                    if (req == null) break;
                    if (confirm("Delete request #" + req.getId() + "? [y/n] : ")) {
                        leaveRequests.remove(req);
                        approvalMessages.removeIf(m -> m.getLeaveRequestId() == req.getId());
                        System.out.println("✓ Deleted.");
                    }
                }
                case "0" -> { return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    // ------ Admin CRUD: Approval Messages -------

    private static void manageApprovalMessages() {
        while (true) {
            System.out.println("""

                    ─── Approval Messages ───
                    [1] List all
                    [2] View
                    [3] Delete
                    [0] Back""");
            switch (prompt("> ").trim()) {
                case "1" -> listAll(approvalMessages);
                case "2" -> { LeaveRequest req = pickLeaveRequest(); if (req != null) viewApprovalMessage(req); }
                case "3" -> { LeaveRequest req = pickLeaveRequest(); if (req != null) deleteApprovalMessage(req); }
                case "0" -> { return; }
                default  -> warn("Invalid option.");
            }
        }
    }

    // =========================================================================
    // Shared helpers — display
    // =========================================================================
    private static void printBanner() {
        System.out.println("""
                ╔══════════════════════════════════╗
                ║   Employee Leave Tracker v0.1    ║
                ╚══════════════════════════════════╝""");
    }

    private static void printMainMenu() {
        System.out.println("""

                [1] Log in
                [2] Sign up
                [0] Exit""");
    }

    private static void printLeaveRequestDetails(LeaveRequest req) {
        System.out.println("\n─── Leave Request #" + req.getId() + " ───");
        System.out.println("Title    : " + req.getTitle());
        System.out.println("Type     : " + req.getRequestType());
        System.out.println("From     : " + req.getLeaveStart().format(DT_FMT));
        System.out.println("To       : " + req.getLeaveEnd().format(DT_FMT));
        System.out.println("Duration : " + req.getDurationDays() + " day(s)");
        System.out.println("Status   : " + req.getStatus());
        System.out.println("Submitted: " + req.getSubmissionTime().format(DT_FMT));
        if (req.getSupportingDocumentPath() != null)
            System.out.println("Document : " + req.getSupportingDocumentPath());
        System.out.println("─".repeat(40));
        System.out.println(req.getContent());
    }

    private static <T> void listAll(List<T> items) {
        if (items.isEmpty()) { System.out.println("(empty)"); return; }
        System.out.println();
        items.forEach(System.out::println);
    }

    // =========================================================================
    // Shared helpers — pickers
    // =========================================================================
    private static Employee pickEmployee() {
        if (employees.isEmpty()) { warn("No employees exist."); return null; }
        System.out.println("Employees:");
        for (int i = 0; i < employees.size(); i++)
            System.out.printf("[%d] %s%n", i + 1, employees.get(i));
        int idx = parseIndex(prompt("Select employee : ").trim(), employees.size());
        return idx < 0 ? null : employees.get(idx);
    }

    private static Approver pickApprover() {
        if (approvers.isEmpty()) { warn("No approvers exist."); return null; }
        System.out.println("Approvers:");
        for (int i = 0; i < approvers.size(); i++)
            System.out.printf("[%d] %s%n", i + 1, approvers.get(i));
        int idx = parseIndex(prompt("Select approver : ").trim(), approvers.size());
        return idx < 0 ? null : approvers.get(idx);
    }

    private static LeaveRequest pickLeaveRequest() {
        if (leaveRequests.isEmpty()) { warn("No leave requests exist."); return null; }
        System.out.println("Leave Requests:");
        for (int i = 0; i < leaveRequests.size(); i++)
            System.out.printf("[%d] %s%n", i + 1, leaveRequests.get(i));
        int idx = parseIndex(prompt("Select : ").trim(), leaveRequests.size());
        return idx < 0 ? null : leaveRequests.get(idx);
    }

    private static RequestType pickRequestType() {
        RequestType[] values = RequestType.values();
        System.out.println("Request type:");
        for (int i = 0; i < values.length; i++)
            System.out.printf("[%d] %s%n", i + 1, values[i].getDisplayName());
        int idx = parseIndex(prompt("Select : ").trim(), values.length);
        return idx < 0 ? RequestType.OTHER : values[idx];
    }

    private static Department pickDepartment() {
        Department[] values = Department.values();
        System.out.println("Department:");
        for (int i = 0; i < values.length; i++)
            System.out.printf("[%d] %s%n", i + 1, values[i].getDisplayName());
        int idx = parseIndex(prompt("Select : ").trim(), values.length);
        return idx < 0 ? Department.IT : values[idx];
    }

    private static Position pickPositionFor(String role) {
        Position[] allowed = role.equals("approver")
                ? new Position[]{Position.MIDDLE, Position.SENIOR}
                : new Position[]{Position.INTERN, Position.JUNIOR, Position.MIDDLE};
        System.out.println("Position:");
        for (int i = 0; i < allowed.length; i++)
            System.out.printf("[%d] %s%n", i + 1, allowed[i].getDisplayName());
        int idx = parseIndex(prompt("Select : ").trim(), allowed.length);
        return idx < 0 ? allowed[0] : allowed[idx];
    }

    // =========================================================================
    // Shared helpers — input
    // =========================================================================
    private static String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine();
    }

    private static String promptRequired(String label) {
        while (true) {
            String val = prompt(label).trim();
            if (!val.isEmpty()) return val;
            warn("This field is required.");
        }
    }

    private static String promptPassword() {
        while (true) {
            String pw = prompt("Password (min 6 chars) : ");
            if (pw.length() >= 6) return pw;
            warn("Password too short.");
        }
    }

    private static String promptEmail() {
        while (true) {
            String email = prompt("Email : ").trim();
            if (email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) return email;
            warn("Invalid email format.");
        }
    }

    private static LocalDateTime promptDateTime(String label) {
        while (true) {
            try {
                return LocalDateTime.parse(prompt(label).trim(), DT_FMT);
            } catch (DateTimeParseException e) {
                warn("Use format: yyyy-MM-dd HH:mm");
            }
        }
    }

    private static LocalDateTime promptDateTimeAfter(String label, LocalDateTime after) {
        while (true) {
            LocalDateTime dt = promptDateTime(label);
            if (!dt.isBefore(after)) return dt;
            warn("End date must be after start date.");
        }
    }

    private static boolean confirm(String label) {
        return prompt(label).trim().equalsIgnoreCase("y");
    }

    private static int parseIndex(String input, int size) {
        try {
            int n = Integer.parseInt(input) - 1;
            if (n >= 0 && n < size) return n;
        } catch (NumberFormatException ignored) {}
        warn("Invalid selection.");
        return -1;
    }

    private static void warn(String message) {
        System.out.println("⚠  " + message);
    }
}
