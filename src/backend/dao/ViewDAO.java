package backend.dao;

import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewDAO {

    private static final Map<String, String> VIEW_DEFINITIONS = new LinkedHashMap<>();

    static {

        VIEW_DEFINITIONS.put("Фильмы + режиссеры", "SELECT * FROM vw_films_full");
        VIEW_DEFINITIONS.put("Кассеты + фильмы", "SELECT * FROM vw_cassettes_full");
        VIEW_DEFINITIONS.put("Квитанции + услуги", "SELECT * FROM vw_receipts_services");
        VIEW_DEFINITIONS.put("Видео + кассеты", "SELECT * FROM vw_video_with_cassettes");
        VIEW_DEFINITIONS.put("Общая выручка", "SELECT * FROM vw_total_revenue");
        VIEW_DEFINITIONS.put("Все люди", "SELECT * FROM vw_people");
        VIEW_DEFINITIONS.put("Категории квитанций", "SELECT * FROM vw_receipt_category");
        VIEW_DEFINITIONS.put("Использованные кассеты", "SELECT * FROM vw_used_cassettes");
        VIEW_DEFINITIONS.put("Неиспользованные кассеты", "SELECT * FROM vw_unused_cassettes");
        VIEW_DEFINITIONS.put("Видео без квитанций", "SELECT * FROM vw_videos_without_receipts");
        VIEW_DEFINITIONS.put("Фильмы выше среднего", "SELECT * FROM vw_films_above_average");
        VIEW_DEFINITIONS.put("Лучшие/худшие видеосалоны", "SELECT * FROM vw_best_worst_videos");
        VIEW_DEFINITIONS.put("Разница выручки", "SELECT * FROM vw_revenue_difference");
        VIEW_DEFINITIONS.put("Ночные видеотеки", "SELECT * FROM vw_night_video_percent");
        VIEW_DEFINITIONS.put("Среднее число клиентов", "SELECT * FROM vw_avg_clients");
        VIEW_DEFINITIONS.put("Редактируемое VIEW", "SELECT * FROM vw_video_edit");
    }

    public static Map<String, String> getViews() {
        return VIEW_DEFINITIONS;
    }

    public DefaultTableModel executeView(String name) throws SQLException {

        String sql = VIEW_DEFINITIONS.get(name);

        if (sql == null)
            throw new SQLException("VIEW не найден: " + name);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();

            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                model.addColumn(meta.getColumnLabel(i)); // 👈 лучше чем getColumnName
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