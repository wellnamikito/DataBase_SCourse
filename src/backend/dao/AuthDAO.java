package backend.dao;

import backend.model.User;
import backend.util.DatabaseConnection;

import java.sql.*;

public class AuthDAO {

    public User login(String login, String password) throws SQLException {

        String sql =
                "SELECT UserID, Login, RoleName, PasswordHash " +
                        "FROM AppUsers " +
                        "WHERE Login=?";

        String inputHash = UserDAO.hashPassword(password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                int id = rs.getInt("UserID");
                String dbHash = rs.getString("PasswordHash");
                String role = rs.getString("RoleName");
                String dbLogin = rs.getString("Login");

                // ✔ 1. новый формат (SHA-256)
                if (dbHash != null && dbHash.equals(inputHash)) {
                    return new User(id, dbLogin, role);
                }

                // ✔ 2. старый формат (plain text) — временная поддержка
                if (dbHash != null && dbHash.equals(password)) {

                    // 🔥 автоматически мигрируем старый пароль в hash
                    migratePassword(conn, id, inputHash);

                    return new User(id, dbLogin, role);
                }
            }
        }

        return null;
    }

    // ── авто-миграция старых паролей ─────────────────────────────
    private void migratePassword(Connection conn, int userId, String newHash) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE AppUsers SET PasswordHash=? WHERE UserID=?")) {

            ps.setString(1, newHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}