package backend.dao;

import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryDAO {

    private static final Map<String, String[]> QUERY_DEFINITIONS = new LinkedHashMap<>();

    static {

        // ================= FUNCTIONS =================

        QUERY_DEFINITIONS.put(
                "Статистика по услугам",
                new String[]{
                        "SELECT * FROM get_service_stats(?::text, ?::int)",
                        "Название услуги|Год"
                }
        );

        QUERY_DEFINITIONS.put(
                "Видеосалоны по владельцу",
                new String[]{
                        "SELECT * FROM get_videos_by_owner(?::text)",
                        "Фамилия"
                }
        );

        QUERY_DEFINITIONS.put(
                "Кассеты по качеству",
                new String[]{
                        "SELECT * FROM get_cassettes_by_quality(?::text)",
                        "Качество"
                }
        );

        QUERY_DEFINITIONS.put(
                "Квитанции по периоду",
                new String[]{
                        "SELECT * FROM get_receipts_by_service_period(?::text, ?::date, ?::date)",
                        "Услуга|Дата от|Дата до"
                }
        );

        QUERY_DEFINITIONS.put(
                "Операции начиная с даты",
                new String[]{
                        "SELECT * FROM get_operations_from_date(?::text, ?::date)",
                        "Услуга|Дата"
                }
        );

        QUERY_DEFINITIONS.put(
                "Выручка выше суммы",
                new String[]{
                        "SELECT * FROM get_video_revenue_over(?::numeric)",
                        "Минимальная выручка"
                }
        );

        QUERY_DEFINITIONS.put(
                "Выручка за период",
                new String[]{
                        "SELECT * FROM get_revenue_by_period(?::date, ?::date)",
                        "Дата от|Дата до"
                }
        );

        QUERY_DEFINITIONS.put(
                "Выручка видеосалонов по маске",
                new String[]{
                        "SELECT * FROM get_videos_revenue_by_mask(?)",
                        "Маска"
                }
        );

        QUERY_DEFINITIONS.put(
                "Квитанции по точной цене",
                new String[]{
                        "SELECT * FROM get_receipts_by_price(?::numeric)",
                        "Цена"
                }
        );

        QUERY_DEFINITIONS.put(
                "Квитанции выше цены",
                new String[]{
                        "SELECT * FROM get_receipts_price_over(?::numeric)",
                        "Цена"
                }
        );

        QUERY_DEFINITIONS.put(
                "Студии по году и выручке",
                new String[]{
                        "SELECT * FROM get_studios_by_year_revenue(?::int, ?::numeric)",
                        "Год|Мин. выручка"
                }
        );
    }

    public static Map<String, String[]> getQueryDefinitions() {
        return QUERY_DEFINITIONS;
    }

    public DefaultTableModel executeNamedQuery(String name, String[] params)
            throws SQLException {

        String[] def = QUERY_DEFINITIONS.get(name);

        if (def == null)
            throw new SQLException("Запрос не найден: " + name);

        String sql = def[0];

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, smartCast(params[i]));
            }

            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            DefaultTableModel model = new DefaultTableModel();

            for (int i = 1; i <= meta.getColumnCount(); i++) {
                model.addColumn(meta.getColumnName(i));
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

    /**
     * УМНОЕ приведение типов
     */
    private Object smartCast(String value) {

        if (value == null || value.isBlank())
            return null;

        // int
        if (value.matches("-?\\d+"))
            return Integer.parseInt(value);

        // decimal
        if (value.matches("-?\\d+(\\.\\d+)?"))
            return Double.parseDouble(value);

        // date (если формат YYYY-MM-DD)
        if (value.matches("\\d{4}-\\d{2}-\\d{2}"))
            return java.sql.Date.valueOf(value);

        // fallback
        return value;
    }
}