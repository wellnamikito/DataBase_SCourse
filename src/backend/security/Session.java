package backend.security;


/**
 * Singleton — сессия текущего пользователя.
 * Хранит роль и имя вошедшего пользователя.
 */
public class Session {

    public enum Role {
        ADMIN, USER
    }

    private static Session instance;

    private String username;
    private Role role;
    private String dbName;
    private boolean authenticated = false;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void login(String username, Role role, String dbName) {
        this.username = username;
        this.role = role;
        this.dbName = dbName;
        this.authenticated = true;
    }

    public void logout() {
        this.username = null;
        this.role = null;
        this.authenticated = false;
    }

    public boolean isAuthenticated() { return authenticated; }
    public boolean isAdmin() { return Role.ADMIN.equals(role); }
    public boolean isUser() { return Role.USER.equals(role); }

    public String getUsername() { return username; }
    public Role getRole() { return role; }
    public String getRoleDisplay() {
        return role == Role.ADMIN ? "Администратор" : "Пользователь";
    }
    public String getDbName() { return dbName; }
}