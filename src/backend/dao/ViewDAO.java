package backend.dao;

import backend.util.DatabaseConnection;
import backend.util.I18n;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewDAO {

    // Внутренние ключи -> SQL. Названия берём из I18n по тому же ключу.
    private static final Map<String, String> VIEW_SQL = new LinkedHashMap<>();

    static {
        VIEW_SQL.put("view.films_directors",   "SELECT * FROM vw_films_full");
        VIEW_SQL.put("view.cassettes_films",   "SELECT * FROM vw_cassettes_full");
        VIEW_SQL.put("view.receipts_services", "SELECT * FROM vw_receipts_services");
        VIEW_SQL.put("view.video_cassettes",   "SELECT * FROM vw_video_with_cassettes");
        VIEW_SQL.put("view.total_revenue",     "SELECT * FROM vw_total_revenue");
        VIEW_SQL.put("view.people",            "SELECT * FROM vw_people");
        VIEW_SQL.put("view.receipt_category",  "SELECT * FROM vw_receipt_category");
        VIEW_SQL.put("view.used_cassettes",    "SELECT * FROM vw_used_cassettes");
        VIEW_SQL.put("view.unused_cassettes",  "SELECT * FROM vw_unused_cassettes");
        VIEW_SQL.put("view.without_receipts",  "SELECT * FROM vw_videos_without_receipts");
        VIEW_SQL.put("view.above_average",     "SELECT * FROM vw_films_above_average");
        VIEW_SQL.put("view.best_worst",        "SELECT * FROM vw_best_worst_videos");
        VIEW_SQL.put("view.revenue_diff",      "SELECT * FROM vw_revenue_difference");
        VIEW_SQL.put("view.night_video",       "SELECT * FROM vw_night_video_percent");
        VIEW_SQL.put("view.avg_clients",       "SELECT * FROM vw_avg_clients");
        VIEW_SQL.put("view.editable",          "SELECT * FROM vw_video_edit");
    }

    /**
     * Возвращает карту: локализованное название -> i18n-ключ.
     * ViewPanel использует это для заполнения combo.
     */
    public static Map<String, String> getLocalizedViews() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : VIEW_SQL.keySet()) {
            result.put(I18n.t(key), key);
        }
        return result;
    }

    /**
     * Выполнить VIEW по i18n-ключу (например "view.films_directors").
     */
    public DefaultTableModel executeViewByKey(String i18nKey) throws SQLException {
        String sql = VIEW_SQL.get(i18nKey);
        if (sql == null)
            throw new SQLException("VIEW не найден: " + i18nKey);
        return runQuery(sql);
    }

    /**
     * Выполнить VIEW по локализованному названию (обратная совместимость).
     */
    public DefaultTableModel executeView(String localizedName) throws SQLException {
        // Ищем ключ по локализованному названию
        for (Map.Entry<String, String> entry : getLocalizedViews().entrySet()) {
            if (entry.getKey().equals(localizedName)) {
                return executeViewByKey(entry.getValue());
            }
        }
        throw new SQLException("VIEW не найден: " + localizedName);
    }

    private DefaultTableModel runQuery(String sql) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                model.addColumn(meta.getColumnLabel(i));
            }

            while (rs.next()) {
                Object[] row = new Object[meta.getColumnCount()];
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            return model;
        }
    }
}