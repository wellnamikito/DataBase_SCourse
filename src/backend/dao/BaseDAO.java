package backend.dao;

import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый DAO.
 * Содержит утилиту renameColumns() — решает проблему кириллицы в алиасах на macOS JDBC.
 */
public abstract class BaseDAO {

    protected DefaultTableModel executeQuery(String sql, Object... params) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetToTableModel(rs);
            }
        }
    }

    public static DefaultTableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        String[] columnNames = new String[cols];
        for (int i = 1; i <= cols; i++) columnNames[i - 1] = meta.getColumnLabel(i);

        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
            model.addRow(row);
        }
        return model;
    }

    protected int executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            return ps.executeUpdate();
        }
    }

    protected int executeInsertGetKey(String sql, Object... params) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    protected List<Object[]> fetchIdName(String sql) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(new Object[]{rs.getInt(1), rs.getString(2)});
        }
        return result;
    }

    /**
     * Переименовать колонки модели — решает проблему кириллицы в SQL алиасах на macOS.
     * Вызывать после executeQuery() если нужны русские заголовки.
     */
    protected void renameColumns(DefaultTableModel model, String[] names) {
        for (int i = 0; i < Math.min(names.length, model.getColumnCount()); i++) {
            model.getColumnName(i); // обращение для инициализации
        }
        // Пересоздать модель с новыми именами колонок
        int cols = model.getColumnCount();
        int rows = model.getRowCount();
        Object[][] data = new Object[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                data[r][c] = model.getValueAt(r, c);

        model.setDataVector(data, names);
    }
}