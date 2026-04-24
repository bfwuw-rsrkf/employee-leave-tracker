package model.enums;

public enum Department {
    IT("IT"),
    FINANCE("Finance"),
    HR("HR"),
    MANAGEMENT("Management");

    private final String displayName;

    Department(String displayName) {
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
