package backend.dao;

import backend.model.User;
import backend.util.DatabaseConnection;

import java.sql.*;

public class AuthDAO {

    public User login(String login, String password) throws SQLException {

        String sql =
                "SELECT UserID, Login, RoleName " +
                        "FROM AppUsers " +
                        "WHERE Login=? AND PasswordHash=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("UserID"),
                            rs.getString("Login"),
                            rs.getString("RoleName")
                    );
                }
            }
        }

        return null;
    }
}