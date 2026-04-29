package backend.dao;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

/**
 * DAO для отчётов:
 * 1. Однотабличный: список владельцев
 * 2. Многотабличный: кассеты с фильмами и видеосалонами
 * 3. Агрегированный: выручка и аренда по видеосалонам (через VIEW)
 */
public class ReportDAO extends BaseDAO {

    /** Отчёт 1: однотабличный — все владельцы */
    public DefaultTableModel reportOwners() throws SQLException {
        return executeQuery("SELECT OwnerID, Familia, Name, Otchestvo FROM Owners ORDER BY Familia");
    }

    /** Отчёт 2: многотабличный — кассеты с полной информацией */
    public DefaultTableModel reportCassettesDetailed() throws SQLException {
        return executeQuery(
                "SELECT C.CasseteID, F.Caption AS Фильм, F.Year AS Год, " +
                        "V.Caption AS Видеосалон, Q.QualityName AS Качество, C.Demand AS Спрос " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID=C.FilmID " +
                        "INNER JOIN Video V ON V.VideoID=C.VideoID " +
                        "INNER JOIN Quality Q ON Q.QualityID=C.QualityID " +
                        "ORDER BY V.Caption, F.Caption"
        );
    }

    /** Отчёт 3: агрегированный — выручка по видеосалонам (использует VIEW vw_total_revenue) */
    public DefaultTableModel reportRevenueSummary() throws SQLException {
        return executeQuery(
                "SELECT Caption AS Видеосалон, " +
                        "total_amount AS Общая_выручка, " +
                        "rent_revenue AS Выручка_аренда " +
                        "FROM vw_total_revenue ORDER BY total_amount DESC"
        );
    }

    /** Агрегированный отчёт с вызовом функции get_service_stats */
    public DefaultTableModel reportServiceStats(String serviceName, int year) throws SQLException {
        return executeQuery("SELECT * FROM get_service_stats(?, ?)", serviceName, year);
    }
}