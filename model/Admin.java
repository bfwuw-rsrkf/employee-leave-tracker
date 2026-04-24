package model;

public class Admin {

    private int id;
    private String login;
    private String password;

    public Admin() {}

    public Admin(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    public String getRole() {
        return "Admin";
    }

    public String getDashboardTitle() {
        return "Admin Control Panel";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

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

    @Override
    public String toString() {
        return String.format("[Admin] %s", login);
    }
}
