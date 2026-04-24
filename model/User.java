package model;

import model.enums.Position;

public abstract class User {

    private int id;
    private String fullName;
    private String login;
    private String password;
    private String email;
    private Position position;

    protected User() {}

    protected User(String fullName, String login, String password, String email, Position position) {
        this.fullName = fullName;
        this.login = login;
        this.password = password;
        this.email = email;
        this.position = position;
    }

    // Polymorphic methods — each subclass defines its own role and dashboard label
    public abstract String getRole();
    public abstract String getDashboardTitle();

    // Determines whether this user has authority over another user by position rank
    public boolean hasAuthorityOver(User other) {
        return other.getPosition().isJuniorTo(this.getPosition());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name must not be empty.");
        this.fullName = fullName;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) {
        if (login == null || login.isBlank())
            throw new IllegalArgumentException("Login must not be empty.");
        this.login = login;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.isEmpty())
            throw new IllegalArgumentException("Password must not be empty.");
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Invalid email format.");
        this.email = email;
    }

    public Position getPosition() { return position; }
    public void setPosition(Position position) {
        if (position == null)
            throw new IllegalArgumentException("Position must not be null.");
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) — %s", getRole(), fullName, login, position);
    }
}
