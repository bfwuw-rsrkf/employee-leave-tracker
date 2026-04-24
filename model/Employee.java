package model;

import model.enums.Department;
import model.enums.Position;

import java.util.EnumSet;
import java.util.Set;

public class Employee extends User {

    private static final Set<Position> ALLOWED_POSITIONS =
            EnumSet.of(Position.INTERN, Position.JUNIOR, Position.MIDDLE);

    private Department department;
    private boolean emailVerified;
    private String verificationToken;

    public Employee() {}

    public Employee(String fullName, String login, String password,
                    String email, Position position, Department department) {
        super(fullName, login, password, email, position);
        setPosition(position);
        this.department = department;
        this.emailVerified = false;
    }

    @Override
    public String getRole() {
        return "Employee";
    }

    @Override
    public String getDashboardTitle() {
        return "Employee Dashboard — " + getFullName();
    }

    @Override
    public void setPosition(Position position) {
        if (!ALLOWED_POSITIONS.contains(position))
            throw new IllegalArgumentException(
                    "Employees may only hold Intern, Junior, or Middle positions.");
        super.setPosition(position);
    }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) {
        if (department == null)
            throw new IllegalArgumentException("Department must not be null.");
        this.department = department;
    }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    @Override
    public String toString() {
        return String.format("[Employee] %s | %s | %s | Verified: %b",
                getFullName(), department, getPosition(), emailVerified);
    }
}
