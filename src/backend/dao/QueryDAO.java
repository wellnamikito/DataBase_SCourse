package backend.dao;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DAO для выполнения именованных SQL-запросов из ТЗ.
 * Все 29 запросов реализованы с поддержкой параметров.
 */
public class QueryDAO extends BaseDAO {

    /** Описание запросов: имя -> SQL (параметры через ?) */
    public static Map<String, String[]> getQueryDefinitions() {
        // LinkedHashMap сохраняет порядок вставки
        Map<String, String[]> map = new LinkedHashMap<>();

        map.put("1. Видеопрокаты и владельцы (по фамилии)",
                new String[]{
                        "SELECT V.Caption, V.Address, O.Familia, O.Name, O.Otchestvo " +
                                "FROM Video V INNER JOIN Owners O ON O.OwnerID = V.OwnerId WHERE O.Familia = ?",
                        "Фамилия владельца"
                });

        map.put("2. Кассеты с фильмами (по качеству)",
                new String[]{
                        "SELECT F.Caption, Q.QualityName FROM Cassette C " +
                                "INNER JOIN Film F ON F.FilmID=C.FilmID INNER JOIN Quality Q ON Q.QualityID=C.QualityID " +
                                "WHERE Q.QualityName = ?",
                        "Название качества (напр. 4K)"
                });

        map.put("3. Квитанции по услуге и периоду (дата С)",
                new String[]{
                        "SELECT R.Date, R.Price, S.ServiceName FROM Receipt R " +
                                "INNER JOIN Service S ON S.ServiceID=R.ServiceID " +
                                "WHERE S.ServiceName=? AND R.Date BETWEEN ?::DATE AND ?::DATE",
                        "Услуга|Дата С (YYYY-MM-DD)|Дата По (YYYY-MM-DD)"
                });

        map.put("4. Все операции аренды с даты",
                new String[]{
                        "SELECT R.Date, R.Price, S.ServiceName FROM Receipt R " +
                                "INNER JOIN Service S ON S.ServiceID=R.ServiceID " +
                                "WHERE S.ServiceName='Аренда' AND R.Date >= ?::DATE",
                        "Дата начала (YYYY-MM-DD)"
                });

        map.put("5. Все фильмы с режиссерами и студиями",
                new String[]{
                        "SELECT F.Caption, D.Familia, D.Name, D.Otchestvo, S.StudioName FROM Film F " +
                                "INNER JOIN Director D ON F.DirectorID=D.DirectorID " +
                                "INNER JOIN Studio S ON S.StudioID=F.StudioID",
                        ""
                });

        map.put("6. Все кассеты с фильмами и видеопрокатами",
                new String[]{
                        "SELECT C.CasseteID, F.Caption, V.Caption AS Video FROM Cassette C " +
                                "INNER JOIN Film F ON F.FilmID=C.FilmID INNER JOIN Video V ON V.VideoID=C.VideoID",
                        ""
                });

        map.put("7. Квитанции с услугами и видеопрокатами",
                new String[]{
                        "SELECT R.ReceiptID, S.ServiceName, V.Caption, R.Date FROM Receipt R " +
                                "INNER JOIN Video V ON V.VideoID=R.VideoID INNER JOIN Service S ON S.ServiceID=R.ServiceID",
                        ""
                });

        map.put("8. LEFT JOIN: Видеопрокаты и их кассеты",
                new String[]{
                        "SELECT V.Caption, C.CasseteID FROM Video V LEFT JOIN Cassette C ON V.VideoID=C.VideoID",
                        ""
                });

        map.put("9. RIGHT JOIN: Кассеты и видеопрокаты",
                new String[]{
                        "SELECT V.Caption, C.CasseteId FROM Video V RIGHT JOIN Cassette C ON V.VideoID=C.VideoID",
                        ""
                });

        map.put("10. Видеопрокаты без квитанций (NOT EXISTS)",
                new String[]{
                        "SELECT V.Caption FROM Video V WHERE NOT EXISTS(SELECT 1 FROM Receipt WHERE VideoID=V.VideoID)",
                        ""
                });

        map.put("11. Кассеты в операциях (IN)",
                new String[]{
                        "SELECT * FROM Cassette C WHERE C.CasseteID IN(SELECT DISTINCT R.CassetteID FROM Receipt R)",
                        ""
                });

        map.put("12. Кассеты без операций (NOT IN)",
                new String[]{
                        "SELECT * FROM Cassette C WHERE C.CasseteID NOT IN(SELECT DISTINCT R.CassetteID FROM Receipt R WHERE R.CassetteID IS NOT NULL)",
                        ""
                });

        map.put("13. Классификация квитанций по цене (CASE)",
                new String[]{
                        "SELECT t.ReceiptID, CASE WHEN t.Price<=100 THEN 'дешево' WHEN t.Price<=500 THEN 'средне' ELSE 'дорого' END AS price_category " +
                                "FROM (SELECT R.ReceiptID, R.Price FROM Receipt R WHERE R.Price IS NOT NULL) t ORDER BY t.Price ASC",
                        ""
                });

        map.put("14. Итого: кассеты и выручка по салонам",
                new String[]{
                        "SELECT COUNT(DISTINCT C.CasseteID) AS cassettes, V.Caption, " +
                                "COALESCE((SELECT SUM(R.Price)::TEXT FROM Receipt R WHERE V.VideoID=R.VideoID),'нет чеков') AS revenue " +
                                "FROM Video V INNER JOIN Cassette C ON V.VideoID=C.VideoID GROUP BY V.VideoID, V.Caption",
                        ""
                });

        map.put("15. Выручка: всего и аренда по видеосалонам",
                new String[]{
                        "SELECT V.Caption, SUM(COALESCE(r.price,0)) AS total_amount, " +
                                "SUM(CASE WHEN S.ServiceName='Аренда' THEN r.price ELSE 0 END) AS rent_revenue " +
                                "FROM Receipt r LEFT JOIN Video V ON V.VideoID=r.VideoID " +
                                "LEFT JOIN Service S ON r.ServiceID=S.ServiceID GROUP BY V.VideoID, V.Caption",
                        ""
                });

        map.put("16. Видеосалоны с выручкой > порога",
                new String[]{
                        "SELECT V.Caption, SUM(R.Price) FROM Video V INNER JOIN Receipt R ON V.VideoID=R.VideoID " +
                                "GROUP BY V.VideoID, V.Caption HAVING SUM(R.Price) > ?::INT",
                        "Минимальная выручка"
                });

        map.put("17. Выручка за период (параметры: С и По)",
                new String[]{
                        "SELECT V.Caption, COALESCE((SELECT SUM(R.Price) FROM Receipt R WHERE R.VideoID=V.VideoID AND R.Date BETWEEN ?::DATE AND ?::DATE),0) AS revenue " +
                                "FROM Video V",
                        "Дата С (YYYY-MM-DD)|Дата По (YYYY-MM-DD)"
                });

        map.put("18. Видеосалоны по маске названия (LIKE)",
                new String[]{
                        "SELECT V.Caption FROM Video V WHERE V.Caption LIKE ?",
                        "Маска (напр. Видео%)"
                });

        map.put("19. Видеосалон с максимальной выручкой",
                new String[]{
                        "SELECT V.Caption, COALESCE(SUM(R.Price),0) AS Total FROM Video V " +
                                "LEFT JOIN Receipt R ON V.VideoID=R.VideoID GROUP BY V.VideoID, V.Caption ORDER BY Total DESC LIMIT 1",
                        ""
                });

        map.put("20. Студии с выручкой > min (за год)",
                new String[]{
                        "SELECT S.StudioName, COALESCE(SUM(R.Price),0) FROM Studio S " +
                                "LEFT JOIN Film F ON S.StudioID=F.StudioID LEFT JOIN Cassette C ON F.FilmID=C.FilmID " +
                                "LEFT JOIN Receipt R ON C.CasseteID=R.CassetteID WHERE F.Year=? " +
                                "GROUP BY S.StudioID, S.StudioName HAVING SUM(R.Price) >= ?::INT",
                        "Год (напр. 2023)|Минимальная выручка"
                });

        map.put("21. Фильмы с кол-вом кассет > среднего",
                new String[]{
                        "SELECT F.Caption FROM Film F LEFT JOIN Cassette C ON F.FilmID=C.FilmID " +
                                "GROUP BY F.FilmID, F.Caption HAVING COUNT(*) > (SELECT AVG(cnt) FROM (SELECT COUNT(*) AS cnt FROM Cassette C GROUP BY C.FilmID) T)",
                        ""
                });

        map.put("22. Объединение: владельцы + режиссеры",
                new String[]{
                        "SELECT O.Familia, O.Name, O.Otchestvo FROM Owners O UNION SELECT D.Familia, D.Name, D.Otchestvo FROM Director D ORDER BY 1",
                        ""
                });

        map.put("23. % ночных видеотек по районам",
                new String[]{
                        "SELECT D.DistrictName, ROUND(COUNT(CASE WHEN (V.TimeStart>=22 OR V.TimeEnd<=6) THEN 1 END)*100/COUNT(*)) AS NIGHT_PERCENT " +
                                "FROM Video V INNER JOIN Districts D ON D.DistrictID=V.DistrictID GROUP BY D.DistrictName",
                        ""
                });

        map.put("24. Среднее количество клиентов по районам",
                new String[]{
                        "WITH video_clients AS (" +
                                "  SELECT V.VideoID, V.Caption, V.DistrictID, COUNT(*) AS client_count " +
                                "  FROM Receipt R JOIN Video V ON V.VideoID=R.VideoID GROUP BY V.VideoID, V.Caption, V.DistrictID) " +
                                "SELECT CASE WHEN GROUPING(D.DistrictName)=1 THEN 'ГОРОД' ELSE D.DistrictName END AS district, " +
                                "CASE WHEN GROUPING(VC.Caption)=1 THEN 'ВСЕ ВИДЕОТЕКИ' ELSE VC.Caption END AS video, " +
                                "ROUND(AVG(VC.client_count)) AS avg_clients " +
                                "FROM video_clients VC JOIN Districts D ON D.DistrictID=VC.DistrictID " +
                                "GROUP BY ROLLUP(D.DistrictName, VC.Caption) ORDER BY GROUPING(D.DistrictName), D.DistrictName, GROUPING(VC.Caption), VC.Caption",
                        ""
                });

        map.put("25. Кол-во клиентов и затраты по услуге за год (функция)",
                new String[]{
                        "SELECT * FROM get_service_stats(?, ?::INT)",
                        "Название услуги|Год (напр. 2023)"
                });

        return map;
    }

    /** Выполнить запрос по имени с массивом строковых параметров */
    public DefaultTableModel executeNamedQuery(String queryName, String[] params) throws SQLException {
        Map<String, String[]> defs = getQueryDefinitions();
        String[] def = defs.get(queryName);
        if (def == null) throw new SQLException("Запрос не найден: " + queryName);

        String sql = def[0];
        // Считаем кол-во ? в SQL
        int expectedParams = (int) sql.chars().filter(c -> c == '?').count();

        Connection conn = backend.util.DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < Math.min(params.length, expectedParams); i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return resultSetToTableModel(rs);
            }
        }
    }
}