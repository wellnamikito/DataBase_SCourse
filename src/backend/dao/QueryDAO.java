package backend.dao;

import backend.util.DatabaseConnection;
import backend.util.I18n;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryDAO {

    // Внутренний ключ -> [SQL, ключи параметров через | ]
    private static final Map<String, String[]> QUERY_SQL = new LinkedHashMap<>();

    static {
        QUERY_SQL.put("query.service_stats",
                new String[]{
                        "SELECT * FROM get_service_stats(?::text, ?::int)",
                        "query.param.service_name|query.param.year"
                });

        QUERY_SQL.put("query.videos_by_owner",
                new String[]{
                        "SELECT * FROM get_videos_by_owner(?::text)",
                        "query.param.last_name"
                });

        QUERY_SQL.put("query.cassettes_by_quality",
                new String[]{
                        "SELECT * FROM get_cassettes_by_quality(?::text)",
                        "query.param.quality"
                });

        QUERY_SQL.put("query.receipts_by_period",
                new String[]{
                        "SELECT * FROM get_receipts_by_service_period(?::text, ?::date, ?::date)",
                        "query.param.service_name|query.param.date_from|query.param.date_to"
                });

        QUERY_SQL.put("query.operations_from_date",
                new String[]{
                        "SELECT * FROM get_operations_from_date(?::text, ?::date)",
                        "query.param.service_name|query.param.date"
                });

        QUERY_SQL.put("query.revenue_over",
                new String[]{
                        "SELECT * FROM get_video_revenue_over(?::numeric)",
                        "query.param.min_revenue"
                });

        QUERY_SQL.put("query.revenue_by_period",
                new String[]{
                        "SELECT * FROM get_revenue_by_period(?::date, ?::date)",
                        "query.param.date_from|query.param.date_to"
                });

        QUERY_SQL.put("query.revenue_by_mask",
                new String[]{
                        "SELECT * FROM get_videos_revenue_by_mask(?)",
                        "query.param.mask"
                });

        QUERY_SQL.put("query.receipts_by_price",
                new String[]{
                        "SELECT * FROM get_receipts_by_price(?::numeric)",
                        "query.param.price"
                });

        QUERY_SQL.put("query.receipts_price_over",
                new String[]{
                        "SELECT * FROM get_receipts_price_over(?::numeric)",
                        "query.param.price"
                });

        QUERY_SQL.put("query.studios_by_year_revenue",
                new String[]{
                        "SELECT * FROM get_studios_by_year_revenue(?::int, ?::numeric)",
                        "query.param.year|query.param.min_revenue"
                });
    }

    // ── Локализованное название → i18n-ключ ──────────────────────────
    public static Map<String, String> getLocalizedQueries() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : QUERY_SQL.keySet()) {
            result.put(I18n.t(key), key);
        }
        return result;
    }

    // ── Локализованные названия параметров по i18n-ключу запроса ─────
    public static String[] getLocalizedParams(String i18nKey) {
        String[] def = QUERY_SQL.get(i18nKey);
        if (def == null || def[1].isEmpty()) return new String[0];
        String[] paramKeys = def[1].split("\\|");
        String[] result = new String[paramKeys.length];
        for (int i = 0; i < paramKeys.length; i++) {
            result[i] = I18n.t(paramKeys[i]);
        }
        return result;
    }

    // ── Выполнение по i18n-ключу ──────────────────────────────────────
    public DefaultTableModel executeByKey(String i18nKey, String[] params) throws SQLException {
        String[] def = QUERY_SQL.get(i18nKey);
        if (def == null) throw new SQLException("Запрос не найден: " + i18nKey);
        return runQuery(def[0], params);
    }

    // ── Обратная совместимость: по локализованному названию ───────────
    public DefaultTableModel executeNamedQuery(String localizedName, String[] params)
            throws SQLException {
        for (Map.Entry<String, String> entry : getLocalizedQueries().entrySet()) {
            if (entry.getKey().equals(localizedName)) {
                return executeByKey(entry.getValue(), params);
            }
        }
        throw new SQLException("Запрос не найден: " + localizedName);
    }

    private DefaultTableModel runQuery(String sql, String[] params) throws SQLException {
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

    private Object smartCast(String value) {
        if (value == null || value.isBlank()) return null;
        if (value.matches("-?\\d+")) return Integer.parseInt(value);
        if (value.matches("-?\\d+(\\.\\d+)?")) return Double.parseDouble(value);
        if (value.matches("\\d{4}-\\d{2}-\\d{2}")) return java.sql.Date.valueOf(value);
        return value;
    }
}