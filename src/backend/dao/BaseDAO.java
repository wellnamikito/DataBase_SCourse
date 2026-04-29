package backend.dao;

import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;

/**
 * Базовый DAO с общими утилитами работы с БД.
 * Все конкретные DAO наследуются от этого клсса
*/
public abstract class BaseDAO {
    /**
     * Выполнить произвольный SELECT и вернуть DefaultTableModel для JTable
     */
    protected DefaultTableModel executeQuery(String sql, Object... params) throws SQLException{
        Connection conn = DatabaseConnection.getConnection();
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try(ResultSet rs = ps.executeQuery()){
                return resultSetToTableModel(rs);
            }
        }
    }

    /**
     * Конвертировать Result Set в DefaultTableModel
     */
    public static DefaultTableModel resultSetToTableModel(ResultSet rs) throws SQLException{
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();

        String[] columnNames = new String[cols];
        for (int i = 1; i <cols ; i++) {
            columnNames[i-1] = meta.getColumnName(i);

        }
        DefaultTableModel model = new DefaultTableModel(columnNames,0){
            @Override public boolean isCellEditable(int r, int c) {return false;}
        };

        while(rs.next()){
            Object[] row = new Object[cols];
            for (int i = 1; i <cols ; i++) {
                row[i-1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        return model;
    }

    /**
     * Выполнить INSERT/UPDATE/DELETE
     */
    protected int executeUpdate (String sql, Object... params) throws SQLException{
        Connection conn = DatabaseConnection.getConnection();
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    /**
     * Выполнить INSERT и вернуть сгенерированный ключ
     */
    protected int executeInsertGetKey(String sql, Object... params) throws SQLException{
        Connection conn = DatabaseConnection.getConnection();
        try(PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            for(int i = 0; i < params.length; i++){
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
            try(ResultSet keys = ps.getGeneratedKeys()){
                if(keys.next()) return keys.getInt(1);
            }
        }
        return  -1;
    }
    /**
     * Получить список пар (id, name) для ComboBox-ов.
     */
    protected List<Object[]> fetchIdName(String sql) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<Object[]> result = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{rs.getInt(1), rs.getString(2)});
            }
        }
        return result;
    }
}
