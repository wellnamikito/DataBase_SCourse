package backend.dao;

import backend.model.Film;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO для таблицы Film */
public class FilmDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        return executeQuery(
                "SELECT F.FilmID, F.Caption, F.Year, F.Duration, " +
                        "D.Familia || ' ' || D.Name AS Director, S.StudioName " +
                        "FROM Film F " +
                        "LEFT JOIN Director D ON D.DirectorID = F.DirectorID " +
                        "LEFT JOIN Studio S ON S.StudioID = F.StudioID " +
                        "ORDER BY F.FilmID"
        );
    }

    public List<Film> getAllList() throws SQLException {
        List<Film> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Film ORDER BY FilmID");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Film f = new Film();
                f.setFilmId(rs.getInt("FilmID"));
                f.setCaption(rs.getString("Caption"));
                f.setYear(rs.getString("Year"));
                f.setDuration(rs.getInt("Duration"));
                f.setInformation(rs.getString("Information"));
                f.setDirectorId(rs.getInt("DirectorID"));
                f.setStudioId(rs.getInt("StudioID"));
                list.add(f);
            }
        }
        return list;
    }

    public void insert(Film f) throws SQLException {
        executeInsertGetKey(
                "INSERT INTO Film(Caption, Year, Duration, Information, DirectorID, StudioID) VALUES(?,?,?,?,?,?)",
                f.getCaption(), f.getYear(), f.getDuration(), f.getInformation(),
                f.getDirectorId(), f.getStudioId()
        );
    }

    public void update(Film f) throws SQLException {
        executeUpdate(
                "UPDATE Film SET Caption=?, Year=?, Duration=?, Information=?, DirectorID=?, StudioID=? WHERE FilmID=?",
                f.getCaption(), f.getYear(), f.getDuration(), f.getInformation(),
                f.getDirectorId(), f.getStudioId(), f.getFilmId()
        );
    }

    public void delete(int id) throws SQLException {
        executeUpdate("DELETE FROM Film WHERE FilmID=?", id);
    }

    /** VIEW: фильмы с режиссерами и студиями */
    public DefaultTableModel getFilmsFullView() throws SQLException {
        return executeQuery("SELECT * FROM vw_films_full ORDER BY 1");
    }
}