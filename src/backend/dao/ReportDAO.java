package backend.dao;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class ReportDAO extends BaseDAO {

    /** Отчёт 1: Однотабличный — владельцы */
    public DefaultTableModel reportOwners() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT Familia, Name, Otchestvo FROM Owners ORDER BY Familia"
        );
        renameColumns(m, new String[]{"Фамилия", "Имя", "Отчество"});
        return m;
    }

    /** Отчёт 2: Многотабличный — кассеты с деталями */
    public DefaultTableModel reportCassettesDetailed() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT C.CasseteID, F.Caption, F.Year, V.Caption, Q.QualityName, C.Demand " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID=C.FilmID " +
                        "INNER JOIN Video V ON V.VideoID=C.VideoID " +
                        "INNER JOIN Quality Q ON Q.QualityID=C.QualityID " +
                        "ORDER BY V.Caption, F.Caption"
        );
        renameColumns(m, new String[]{"ID Кассеты", "Фильм", "Год", "Видеосалон", "Качество", "Спрос"});
        return m;
    }

    /** Отчёт 3: Агрегированный — выручка (использует VIEW vw_total_revenue) */
    public DefaultTableModel reportRevenueSummary() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT Caption, total_amount, rent_revenue FROM vw_total_revenue ORDER BY total_amount DESC"
        );
        renameColumns(m, new String[]{"Видеосалон", "Общая выручка", "Выручка (аренда)"});
        return m;
    }

    /** Функция get_service_stats */
    public DefaultTableModel reportServiceStats(String serviceName, int year) throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT * FROM get_service_stats(?, ?)", serviceName, year
        );
        if (m.getColumnCount() >= 3) {
            renameColumns(m, new String[]{"Услуга", "Кол-во клиентов", "Сумма затрат"});
        }
        return m;
    }
}