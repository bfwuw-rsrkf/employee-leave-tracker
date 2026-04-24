package model.enums;

public enum RequestType {
    VACATION("Vacation"),
    SICK_LEAVE("Sick Leave"),
    PERSONAL("Personal Leave"),
    MATERNITY("Maternity Leave"),
    PATERNITY("Paternity Leave"),
    UNPAID("Unpaid Leave"),
    OTHER("Other");

    private final String displayName;

    RequestType(String displayName) {
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
