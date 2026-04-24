package model.enums;

public enum Position {
    INTERN("Intern"),
    JUNIOR("Junior Employee"),
    MIDDLE("Middle Employee"),
    SENIOR("Senior Employee");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isJuniorTo(Position other) {
        return this.ordinal() < other.ordinal();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
