package model.enums;

public enum LeaveStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    DENIED("Denied");

    private final String displayName;

    LeaveStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
