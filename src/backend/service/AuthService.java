package backend.service;
import backend.security.Session;
import backend.data.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Сервис авторизации.
 * Выполняет подключение к БД и проверку роли пользователя.
 *
 * Логика ролей:
 * - ADMIN: пользователи PostgreSQL с ролью pg_read_all_data И pg_write_all_data
 *   (или superuser). На практике: если у пользователя есть права на INSERT/UPDATE — он ADMIN.
 * - USER: все остальные аутентифицированные пользователи.
 *
 * Для упрощения используем признак: если логин == "admin" → ADMIN, иначе → USER.
 * В реальном проекте можно хранить роли в отдельной таблице БД.
 */
public class AuthService {

    // Разработчик проекта (отображается на экране входа)
    public static final String DEVELOPER_NAME = "Кучев Ярослав Игоревич";
    public static final String PROJECT_NAME   = "Система управления видеопрокатом";

    /**
     * Попытка аутентификации.
     * Параметры host, port, database позволяют подключаться к разным БД.
     *
     * @return true при успехе
     * @throws SQLException при ошибке соединения
     */
    public boolean authenticate(String host, int port, String database,
                                String username, String password) throws SQLException {

        // Инициализируем пул соединений с переданными credentials
        DatabaseConnection.getInstance().initialize(host, port, database, username, password);

        // Определяем роль
        Session.Role role = resolveRole(username, password, database);

        // Сохраняем сессию
        Session.getInstance().login(username, role, database);

        return true;
    }

    /**
     * Определяем роль пользователя.
     * Логика: проверяем pg_roles — если superuser или createrole → ADMIN.
     */
    private Session.Role resolveRole(String username, String password, String database) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT rolsuper, rolcreaterole, rolcreatedb " +
                             "FROM pg_roles WHERE rolname = ?"
             )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean superuser    = rs.getBoolean("rolsuper");
                    boolean createRole   = rs.getBoolean("rolcreaterole");
                    boolean createDb     = rs.getBoolean("rolcreatedb");
                    if (superuser || createRole || createDb) {
                        return Session.Role.ADMIN;
                    }
                }
            }
        } catch (SQLException e) {
            // Если не можем проверить — назначаем USER
        }
        // Дополнительная проверка по имени (для демо)
        if ("admin".equalsIgnoreCase(username) || "postgres".equalsIgnoreCase(username)) {
            return Session.Role.ADMIN;
        }
        return Session.Role.USER;
    }

    public void logout() {
        Session.getInstance().logout();
        DatabaseConnection.getInstance().close();
    }
}
