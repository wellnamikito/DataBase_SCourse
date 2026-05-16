package backend.dao;

import backend.model.User;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── Получить всех пользователей для таблицы ───────────────────
    public DefaultTableModel getAll() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT UserID, Login, RoleName FROM AppUsers ORDER BY UserID");
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Логин", "Роль"}, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("UserID"),
                        rs.getString("Login"),
                        rs.getString("RoleName")
                });
            }
            return model;
        }
    }

    // ── Получить список пользователей ─────────────────────────────
    public List<User> getAllList() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT UserID, Login, RoleName FROM AppUsers ORDER BY UserID");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(
                        rs.getInt("UserID"),
                        rs.getString("Login"),
                        rs.getString("RoleName")
                ));
            }
        }
        return list;
    }

    // ── Добавить пользователя ─────────────────────────────────────
    public void insert(String login, String password, String roleName) throws SQLException {
        String hash = hashPassword(password);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO AppUsers(Login, PasswordHash, RoleName) VALUES(?,?,?)")) {
            ps.setString(1, login.trim());
            ps.setString(2, hash);
            ps.setString(3, roleName);
            ps.executeUpdate();
        }
    }

    // ── Изменить роль пользователя ────────────────────────────────
    public void updateRole(int userId, String roleName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE AppUsers SET RoleName=? WHERE UserID=?")) {
            ps.setString(1, roleName);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ── Сменить пароль ────────────────────────────────────────────
    public void updatePassword(int userId, String newPassword) throws SQLException {
        String hash = hashPassword(newPassword);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE AppUsers SET PasswordHash=? WHERE UserID=?")) {
            ps.setString(1, hash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    // ── Удалить пользователя ──────────────────────────────────────
    public void delete(int userId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM AppUsers WHERE UserID=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ── Проверить существование логина ────────────────────────────
    public boolean loginExists(String login) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM AppUsers WHERE Login=?")) {
            ps.setString(1, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ── SHA-256 хэш пароля ────────────────────────────────────────
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }
}