package backend.data.dao;
import backend.data.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO для выполнения VIEW и параметризованных запросов.
 * Возвращает generic List<Map<String,Object>> для отображения в TableView.
 */
public class ViewDAO extends BaseDAO {

    /**
     * Универсальный метод выполнения SELECT-запроса.
     * Возвращает список строк, каждая строка — упорядоченный Map<columnName, value>.
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= colCount; i++) {
                        String colName = meta.getColumnLabel(i);
                        Object val = rs.getObject(i);
                        row.put(colName, val);
                    }
                    result.add(row);
                }
            }
        }
        return result;
    }

    // ════════════════════════════════════════════
    //   VIEWS (без параметров)
    // ════════════════════════════════════════════

    public List<Map<String, Object>> getFilmsFull() throws SQLException {
        return executeQuery("SELECT * FROM vw_films_full");
    }

    public List<Map<String, Object>> getCassettesFull() throws SQLException {
        return executeQuery("SELECT * FROM vw_cassettes_full");
    }

    public List<Map<String, Object>> getReceiptsServices() throws SQLException {
        return executeQuery("SELECT * FROM vw_receipts_services");
    }

    public List<Map<String, Object>> getVideoWithCassettes() throws SQLException {
        return executeQuery("SELECT * FROM vw_video_with_cassettes");
    }

    public List<Map<String, Object>> getTotalRevenue() throws SQLException {
        return executeQuery("SELECT * FROM vw_total_revenue");
    }

    public List<Map<String, Object>> getPeople() throws SQLException {
        return executeQuery("SELECT * FROM vw_people");
    }

    public List<Map<String, Object>> getReceiptCategory() throws SQLException {
        return executeQuery("SELECT * FROM vw_receipt_category");
    }

    public List<Map<String, Object>> getUsedCassettes() throws SQLException {
        return executeQuery("SELECT * FROM vw_used_cassettes");
    }

    public List<Map<String, Object>> getUnusedCassettes() throws SQLException {
        return executeQuery("SELECT * FROM vw_unused_cassettes");
    }

    public List<Map<String, Object>> getVideosWithoutReceipts() throws SQLException {
        return executeQuery("SELECT * FROM vw_videos_without_receipts");
    }

    public List<Map<String, Object>> getFilmsAboveAverage() throws SQLException {
        return executeQuery("SELECT * FROM vw_films_above_average");
    }

    public List<Map<String, Object>> getBestWorstVideos() throws SQLException {
        return executeQuery("SELECT * FROM vw_best_worst_videos");
    }

    public List<Map<String, Object>> getRevenueDifference() throws SQLException {
        return executeQuery("SELECT * FROM vw_revenue_difference");
    }

    public List<Map<String, Object>> getNightVideoPercent() throws SQLException {
        return executeQuery("SELECT * FROM vw_night_video_percent");
    }

    public List<Map<String, Object>> getAvgClients() throws SQLException {
        return executeQuery("SELECT * FROM vw_avg_clients ORDER BY district, video");
    }

    // ════════════════════════════════════════════
    //   FUNCTIONS (параметризованные запросы)
    // ════════════════════════════════════════════

    /** Видеосалоны по фамилии владельца */
    public List<Map<String, Object>> getVideosByOwner(String familia) throws SQLException {
        return executeQuery("SELECT * FROM get_videos_by_owner(?)", familia);
    }

    /** Кассеты по качеству */
    public List<Map<String, Object>> getCassettesByQuality(String qualityName) throws SQLException {
        return executeQuery("SELECT * FROM get_cassettes_by_quality(?)", qualityName);
    }

    /** Квитанции по услуге и периоду */
    public List<Map<String, Object>> getReceiptsByServicePeriod(
            String serviceName, java.time.LocalDate from, java.time.LocalDate to) throws SQLException {
        return executeQuery("SELECT * FROM get_receipts_by_service_period(?,?,?)",
                serviceName, from, to);
    }

    /** Операции начиная с даты */
    public List<Map<String, Object>> getOperationsFromDate(
            String serviceName, java.time.LocalDate from) throws SQLException {
        return executeQuery("SELECT * FROM get_operations_from_date(?,?)", serviceName, from);
    }

    /** Видеосалоны с выручкой выше минимума */
    public List<Map<String, Object>> getVideoRevenueOver(int minRevenue) throws SQLException {
        return executeQuery("SELECT * FROM get_video_revenue_over(?)", minRevenue);
    }

    /** Выручка за период */
    public List<Map<String, Object>> getRevenueByPeriod(
            java.time.LocalDate from, java.time.LocalDate to) throws SQLException {
        return executeQuery("SELECT * FROM get_revenue_by_period(?,?)", from, to);
    }

    /** Выручка по маске */
    public List<Map<String, Object>> getVideosByMask(String mask) throws SQLException {
        return executeQuery("SELECT * FROM get_videos_by_mask(?)", mask);
    }

    /** Квитанции по точной цене */
    public List<Map<String, Object>> getReceiptsByPrice(int price) throws SQLException {
        return executeQuery("SELECT * FROM get_receipts_by_price(?)", price);
    }

    /** Квитанции выше цены */
    public List<Map<String, Object>> getReceiptsPriceOver(int price) throws SQLException {
        return executeQuery("SELECT * FROM get_receipts_price_over(?)", price);
    }

    /** Студии по году и минимальной выручке */
    public List<Map<String, Object>> getStudiosByYearRevenue(String year, int minRevenue) throws SQLException {
        return executeQuery("SELECT * FROM get_studios_by_year_revenue(?,?)", year, minRevenue);
    }

    /** Статистика по услугам */
    public List<Map<String, Object>> getServiceStats(String serviceName, int year) throws SQLException {
        return executeQuery("SELECT * FROM get_service_stats(?,?)", serviceName, year);
    }

    // ════════════════════════════════════════════
    //   CHART DATA
    // ════════════════════════════════════════════

    /** Данные для PieChart: выручка по видеосалонам */
    public List<Map<String, Object>> getRevenueByVideo() throws SQLException {
        return executeQuery(
                "SELECT V.Caption, COALESCE(SUM(R.Price),0) AS total " +
                        "FROM Video V LEFT JOIN Receipt R ON V.VideoID=R.VideoID " +
                        "GROUP BY V.Caption ORDER BY total DESC LIMIT 10"
        );
    }

    /** Данные для BarChart: количество кассет по качеству */
    public List<Map<String, Object>> getCassettesByQualityChart() throws SQLException {
        return executeQuery(
                "SELECT Q.QualityName, COUNT(*) AS cnt " +
                        "FROM Cassette C LEFT JOIN Quality Q ON Q.QualityID=C.QualityID " +
                        "GROUP BY Q.QualityName ORDER BY cnt DESC"
        );
    }

    /** Данные для BarChart: выручка по месяцам */
    public List<Map<String, Object>> getRevenueByMonth() throws SQLException {
        return executeQuery(
                "SELECT TO_CHAR(Date,'YYYY-MM') AS month, SUM(Price) AS total " +
                        "FROM Receipt WHERE Date IS NOT NULL " +
                        "GROUP BY month ORDER BY month DESC LIMIT 12"
        );
    }

    /** Данные для PieChart: кассеты по спросу */
    public List<Map<String, Object>> getCassettesByDemand() throws SQLException {
        return executeQuery(
                "SELECT CASE WHEN Demand THEN 'В спросе' ELSE 'Не в спросе' END AS label, " +
                        "COUNT(*) AS cnt FROM Cassette GROUP BY Demand"
        );
    }

    // ════════════════════════════════════════════
    //   REPORT DATA
    // ════════════════════════════════════════════

    /** Однотабличный отчёт: список владельцев */
    public List<Map<String, Object>> reportOwners() throws SQLException {
        return executeQuery(
                "SELECT Familia AS \"Фамилия\", Name AS \"Имя\", Otchestvo AS \"Отчество\" " +
                        "FROM Owners ORDER BY Familia"
        );
    }

    /** Многотабличный отчёт: фильмы с режиссёрами и студиями */
    public List<Map<String, Object>> reportFilmsFullInfo() throws SQLException {
        return executeQuery(
                "SELECT F.Caption AS \"Название фильма\", F.Year AS \"Год\", " +
                        "F.Duration AS \"Длительность\", " +
                        "D.Familia||' '||D.Name AS \"Режиссёр\", " +
                        "S.StudioName AS \"Студия\", C.CountryName AS \"Страна\" " +
                        "FROM Film F " +
                        "LEFT JOIN Director D ON D.DirectorID=F.DirectorID " +
                        "LEFT JOIN Studio S ON S.StudioID=F.StudioID " +
                        "LEFT JOIN Country C ON C.CountryID=S.CountryID " +
                        "ORDER BY F.Caption"
        );
    }

    /** Агрегированный отчёт: выручка по видеосалонам с разбивкой */
    public List<Map<String, Object>> reportRevenueAggregated() throws SQLException {
        return executeQuery(
                "SELECT V.Caption AS \"Видеосалон\", D.DistrictName AS \"Район\", " +
                        "COALESCE(SUM(R.Price),0) AS \"Общая выручка\", " +
                        "COUNT(R.ReceiptID) AS \"Кол-во квитанций\", " +
                        "COALESCE(MAX(R.Price),0) AS \"Макс. цена\", " +
                        "COALESCE(MIN(R.Price),0) AS \"Мин. цена\" " +
                        "FROM Video V " +
                        "LEFT JOIN Districts D ON D.DistrictID=V.DistrictID " +
                        "LEFT JOIN Receipt R ON R.VideoID=V.VideoID " +
                        "GROUP BY V.VideoID, V.Caption, D.DistrictName " +
                        "ORDER BY \"Общая выручка\" DESC"
        );
    }
}
