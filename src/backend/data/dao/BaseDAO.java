package backend.data.dao;
import backend.data.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый DAO — общие утилиты для работы с JDBC.
 */
public abstract class BaseDAO {

    protected Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    /**
     * Безопасное закрытие ресурсов
     */
    protected void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) {
                try { r.close(); } catch (Exception ignored) {}
            }
        }
    }

    /**
     * Выполняет UPDATE/DELETE/INSERT и возвращает количество изменённых строк
     */
    protected int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            return ps.executeUpdate();
        }
    }

    /**
     * Выполняет INSERT и возвращает сгенерированный ключ
     */
    protected int executeInsertReturnKey(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(ps, params);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    protected void setParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else if (params[i] instanceof Integer v) {
                ps.setInt(i + 1, v);
            } else if (params[i] instanceof String v) {
                ps.setString(i + 1, v);
            } else if (params[i] instanceof Boolean v) {
                ps.setBoolean(i + 1, v);
            } else if (params[i] instanceof java.time.LocalDate v) {
                ps.setDate(i + 1, Date.valueOf(v));
            } else if (params[i] instanceof byte[] v) {
                ps.setBytes(i + 1, v);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}