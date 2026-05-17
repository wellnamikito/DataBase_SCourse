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

    // ── Получить всех пользователей ─────────────────────────────
    public DefaultTableModel getAll() throws SQLException {

        String sql = "SELECT UserID, Login, RoleName FROM AppUsers ORDER BY UserID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Логин", "Роль"}, 0
            ) {
                @Override public boolean isCellEditable(int r, int c) {
                    return false;
                }
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

    // ── List версия ──────────────────────────────────────────────
    public List<User> getAllList() throws SQLException {

        List<User> list = new ArrayList<>();

        String sql = "SELECT UserID, Login, RoleName FROM AppUsers ORDER BY UserID";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
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

    // ── INSERT USER ───────────────────────────────────────────────
    public void insert(String login, String password, String roleName) throws SQLException {

        String cleanLogin = login.trim();

        // защита от пустых значений
        if (cleanLogin.isEmpty() || password == null || password.isEmpty()) {
            throw new SQLException("Login или password пустые");
        }

        String sql = "INSERT INTO AppUsers (Login, PasswordHash, RoleName) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cleanLogin);
            ps.setString(2, hashPassword(password));
            ps.setString(3, roleName);

            ps.executeUpdate();
        }
    }

    // ── UPDATE ROLE ──────────────────────────────────────────────
    public void updateRole(int userId, String roleName) throws SQLException {

        String sql = "UPDATE AppUsers SET RoleName=? WHERE UserID=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roleName);
            ps.setInt(2, userId);

            ps.executeUpdate();
        }
    }

    // ── UPDATE PASSWORD ──────────────────────────────────────────
    public void updatePassword(int userId, String newPassword) throws SQLException {

        if (newPassword == null || newPassword.isEmpty()) {
            throw new SQLException("Password пустой");
        }

        String sql = "UPDATE AppUsers SET PasswordHash=? WHERE UserID=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hashPassword(newPassword));
            ps.setInt(2, userId);

            ps.executeUpdate();
        }
    }

    // ── DELETE USER ──────────────────────────────────────────────
    public void delete(int userId) throws SQLException {

        String sql = "DELETE FROM AppUsers WHERE UserID=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ── EXISTS LOGIN ──────────────────────────────────────────────
    public boolean loginExists(String login) throws SQLException {

        String sql = "SELECT 1 FROM AppUsers WHERE Login=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // ── SHA-256 HASH ─────────────────────────────────────────────
    public static String hashPassword(String password) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }
}