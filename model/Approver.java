package model;

import model.enums.Position;

import java.util.EnumSet;
import java.util.Set;

public class Approver extends User {

    private static final Set<Position> ALLOWED_POSITIONS =
            EnumSet.of(Position.MIDDLE, Position.SENIOR);

    public Approver() {}

    public Approver(String fullName, String login, String password,
                    String email, Position position) {
        super(fullName, login, password, email, position);
        setPosition(position);
    }

    @Override
    public String getRole() {
        return "Approver";
    }

    @Override
    public String getDashboardTitle() {
        return "Approver Dashboard — " + getFullName();
    }

    @Override
    public void setPosition(Position position) {
        if (!ALLOWED_POSITIONS.contains(position))
            throw new IllegalArgumentException(
                    "Approvers may only hold Middle or Senior positions.");
        super.setPosition(position);
    }

    // Returns true if this approver is permitted to act on the given employee's requests
    public boolean canReview(Employee employee) {
        return hasAuthorityOver(employee);
    }

    @Override
    public String toString() {
        return String.format("[Approver] %s | %s", getFullName(), getPosition());
    }
}
