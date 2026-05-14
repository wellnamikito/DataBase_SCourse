package backend.model;

public class User {

    private int userId;
    private String login;
    private String roleName;

    public User(int userId, String login, String roleName) {
        this.userId = userId;
        this.login = login;
        this.roleName = roleName;
    }

    public int getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public String getRoleName() {
        return roleName;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(roleName);
    }
}