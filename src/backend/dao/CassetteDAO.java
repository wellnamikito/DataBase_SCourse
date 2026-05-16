package backend.dao;

import backend.model.Cassette;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class CassetteDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT C.CasseteID, F.Caption, V.Caption, Q.QualityName, C.Demand, " +
                        "CASE WHEN C.Photo IS NOT NULL THEN '📷' ELSE '—' END AS photo_flag " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID = C.FilmID " +
                        "INNER JOIN Video V ON V.VideoID = C.VideoID " +
                        "INNER JOIN Quality Q ON Q.QualityID = C.QualityID " +
                        "ORDER BY V.Caption, F.Caption"
        );
        renameColumns(m, new String[]{"ID", "Фильм", "Видеосалон", "Качество", "Спрос", "Фото"});
        return m;
    }

    public DefaultTableModel getByVideoId(int videoId) throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT C.CasseteID, F.Caption, Q.QualityName, C.Demand, " +
                        "CASE WHEN C.Photo IS NOT NULL THEN '📷' ELSE '—' END AS photo_flag " +
                        "FROM Cassette C " +
                        "INNER JOIN Film F ON F.FilmID = C.FilmID " +
                        "INNER JOIN Quality Q ON Q.QualityID = C.QualityID " +
                        "WHERE C.VideoID = ? ORDER BY F.Caption",
                videoId
        );
        renameColumns(m, new String[]{"ID", "Фильм", "Качество", "Спрос", "Фото"});
        return m;
    }

    /** Получить полную запись кассеты включая фото */
    public Cassette getById(int cassetteId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM Cassette WHERE CasseteID=?")) {
            ps.setInt(1, cassetteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cassette c = new Cassette();
                    c.setCassetteId(rs.getInt("CasseteID"));
                    c.setFilmId(rs.getInt("FilmID"));
                    c.setVideoId(rs.getInt("VideoID"));
                    c.setQualityId(rs.getInt("QualityID"));
                    c.setDemand(rs.getBoolean("Demand"));
                    c.setPhoto(rs.getBytes("Photo"));
                    return c;
                }
            }
        }
        return null;
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

    /** Обновить только фото */
    public void updatePhoto(int cassetteId, byte[] photo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Cassette SET Photo=? WHERE CasseteID=?")) {
            if (photo != null) {
                ps.setBytes(1, photo);
            } else {
                ps.setNull(1, Types.BINARY);
            }
            ps.setInt(2, cassetteId);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        executeUpdate("DELETE FROM Cassette WHERE CasseteID=?", id);
    }
}