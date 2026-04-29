package backend.dao;

import backend.model.Cassette;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

/**
 * DAO для таблицы Cassette.
 * Поддерживает фильтрацию по VideoID для Master-Detail.
 */
public class CassetteDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        return executeQuery(
                "SELECT C.CasseteID, F.Caption AS Film, V.Caption AS Video, " +
                        "Q.QualityName, C.Demand " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID = C.FilmID " +
                        "INNER JOIN Video V ON V.VideoID = C.VideoID " +
                        "INNER JOIN Quality Q ON Q.QualityID = C.QualityID " +
                        "ORDER BY C.CasseteID"
        );
    }

    /** Фильтрация кассет по VideoID — для Master-Detail панели */
    public DefaultTableModel getByVideoId(int videoId) throws SQLException {
        return executeQuery(
                "SELECT C.CasseteID, F.Caption AS Film, Q.QualityName, C.Demand " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID = C.FilmID " +
                        "INNER JOIN Quality Q ON Q.QualityID = C.QualityID " +
                        "WHERE C.VideoID = ? ORDER BY C.CasseteID",
                videoId
        );
    }

    public void insert(Cassette c) throws SQLException {
        executeInsertGetKey(
                "INSERT INTO Cassette(FilmID, VideoID, QualityID, Demand) VALUES(?,?,?,?)",
                c.getFilmId(), c.getVideoId(), c.getQualityId(), c.isDemand()
        );
    }

    public void update(Cassette c) throws SQLException {
        executeUpdate(
                "UPDATE Cassette SET FilmID=?, VideoID=?, QualityID=?, Demand=? WHERE CasseteID=?",
                c.getFilmId(), c.getVideoId(), c.getQualityId(), c.isDemand(), c.getCassetteId()
        );
    }

    public void delete(int id) throws SQLException {
        executeUpdate("DELETE FROM Cassette WHERE CasseteID=?", id);
    }
}