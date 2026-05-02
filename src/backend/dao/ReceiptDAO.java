package backend.dao;

import backend.model.Receipt;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT R.ReceiptID, R.Date, R.Price, S.ServiceName, V.Caption, C.CasseteID " +
                        "FROM Receipt R " +
                        "LEFT JOIN Service S ON S.ServiceID = R.ServiceID " +
                        "LEFT JOIN Video V ON V.VideoID = R.VideoID " +
                        "LEFT JOIN Cassette C ON C.CasseteID = R.CassetteID " +
                        "ORDER BY R.Date DESC, R.ReceiptID DESC"
        );
        renameColumns(m, new String[]{"№ Чека", "Дата", "Цена (руб.)", "Услуга", "Видеосалон", "ID Кассеты"});
        return m;
    }

    public void insert(Receipt r) throws SQLException {
        executeInsertGetKey(
                "INSERT INTO Receipt(CassetteID, VideoID, ServiceID, Date, Price) VALUES(?,?,?,?,?)",
                r.getCassetteId(), r.getVideoId(), r.getServiceId(),
                java.sql.Date.valueOf(r.getDate()), r.getPrice()
        );
    }

    public void update(Receipt r) throws SQLException {
        executeUpdate(
                "UPDATE Receipt SET CassetteID=?, VideoID=?, ServiceID=?, Date=?, Price=? WHERE ReceiptID=?",
                r.getCassetteId(), r.getVideoId(), r.getServiceId(),
                java.sql.Date.valueOf(r.getDate()), r.getPrice(), r.getReceiptId()
        );
    }

    public void delete(int id) throws SQLException {
        executeUpdate("DELETE FROM Receipt WHERE ReceiptID=?", id);
    }

    public DefaultTableModel getTotalRevenueView() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT Caption, total_amount, rent_revenue FROM vw_total_revenue ORDER BY total_amount DESC"
        );
        renameColumns(m, new String[]{"Видеосалон", "Общая выручка", "Выручка (аренда)"});
        return m;
    }

    public DefaultTableModel getReceiptCategoryView() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT ReceiptID, price_category FROM vw_receipt_category");
        renameColumns(m, new String[]{"№ Чека", "Категория цены"});
        return m;
    }

    public List<Object[]> getPriceCategoryData() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<Object[]> result = new ArrayList<>();
        String sql =
                "SELECT CASE WHEN Price<=100 THEN 'Дешево' " +
                        "WHEN Price<=500 THEN 'Средне' ELSE 'Дорого' END AS cat, COUNT(*) " +
                        "FROM Receipt GROUP BY cat";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(new Object[]{rs.getString(1), rs.getLong(2)});
        }
        return result;
    }

    public List<Object[]> getRevenueByVideo() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<Object[]> result = new ArrayList<>();
        String sql =
                "SELECT V.Caption, COALESCE(SUM(R.Price),0) " +
                        "FROM Video V LEFT JOIN Receipt R ON V.VideoID=R.VideoID " +
                        "GROUP BY V.VideoID, V.Caption ORDER BY 2 DESC LIMIT 10";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) result.add(new Object[]{rs.getString(1), rs.getLong(2)});
        }
        return result;
    }
}