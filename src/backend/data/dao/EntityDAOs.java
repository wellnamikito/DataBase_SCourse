package backend.data.dao;

import backend.data.model.Dto;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ─────────────────────── FILM ───────────────────────
class FilmDAO extends BaseDAO {

    public List<Dto.Film> findAll() throws SQLException {
        List<Dto.Film> list = new ArrayList<>();
        String sql = """
            SELECT F.FilmID, F.Caption, F.Year, F.Duration, F.Information,
                   F.DirectorID, D.Familia||' '||D.Name AS DirectorName,
                   F.StudioID,  S.StudioName
            FROM Film F
            LEFT JOIN Director D ON D.DirectorID = F.DirectorID
            LEFT JOIN Studio   S ON S.StudioID   = F.StudioID
            ORDER BY F.Caption
            """;
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapFilm(rs));
        }
        return list;
    }

    public int insert(String caption, String year, int duration, String info,
                      int directorId, int studioId) throws SQLException {
        return executeInsertReturnKey(
                "INSERT INTO Film(Caption,Year,Duration,Information,DirectorID,StudioID) VALUES(?,?,?,?,?,?)",
                caption, year, duration, info, directorId, studioId
        );
    }

    public int update(int id, String caption, String year, int duration, String info,
                      int directorId, int studioId) throws SQLException {
        return executeUpdate(
                "UPDATE Film SET Caption=?,Year=?,Duration=?,Information=?,DirectorID=?,StudioID=? WHERE FilmID=?",
                caption, year, duration, info, directorId, studioId, id
        );
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Film WHERE FilmID=?", id);
    }

    private Dto.Film mapFilm(ResultSet rs) throws SQLException {
        return new Dto.Film(
                rs.getInt("FilmID"), rs.getString("Caption"), rs.getString("Year"),
                rs.getInt("Duration"), rs.getString("Information"),
                rs.getInt("DirectorID"), rs.getString("DirectorName"),
                rs.getInt("StudioID"),  rs.getString("StudioName")
        );
    }
}

// ─────────────────────── VIDEO ───────────────────────
class VideoDAO extends BaseDAO {

    public List<Dto.Video> findAll() throws SQLException {
        List<Dto.Video> list = new ArrayList<>();
        String sql = """
            SELECT V.VideoID, V.Caption, V.DistrictID, D.DistrictName,
                   V.Address, V.Type, V.Phone, V.Licence,
                   V.TimeStart, V.TimeEnd, V.Amount,
                   V.OwnerId, O.Familia||' '||O.Name AS OwnerName
            FROM Video V
            LEFT JOIN Districts D ON D.DistrictID = V.DistrictID
            LEFT JOIN Owners    O ON O.OwnerID    = V.OwnerId
            ORDER BY V.Caption
            """;
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapVideo(rs));
        }
        return list;
    }

    public int insert(String caption, int districtId, String address, String type,
                      String phone, String licence, int timeStart, int timeEnd,
                      int amount, int ownerId) throws SQLException {
        return executeInsertReturnKey(
                "INSERT INTO Video(Caption,DistrictID,Address,Type,Phone,Licence,TimeStart,TimeEnd,Amount,OwnerId) VALUES(?,?,?,?,?,?,?,?,?,?)",
                caption, districtId, address, type, phone, licence, timeStart, timeEnd, amount, ownerId
        );
    }

    public int update(int id, String caption, int districtId, String address, String type,
                      String phone, String licence, int timeStart, int timeEnd,
                      int amount, int ownerId) throws SQLException {
        return executeUpdate(
                "UPDATE Video SET Caption=?,DistrictID=?,Address=?,Type=?,Phone=?,Licence=?,TimeStart=?,TimeEnd=?,Amount=?,OwnerId=? WHERE VideoID=?",
                caption, districtId, address, type, phone, licence, timeStart, timeEnd, amount, ownerId, id
        );
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Video WHERE VideoID=?", id);
    }

    private Dto.Video mapVideo(ResultSet rs) throws SQLException {
        return new Dto.Video(
                rs.getInt("VideoID"), rs.getString("Caption"),
                rs.getInt("DistrictID"), rs.getString("DistrictName"),
                rs.getString("Address"), rs.getString("Type"),
                rs.getString("Phone"), rs.getString("Licence"),
                rs.getInt("TimeStart"), rs.getInt("TimeEnd"),
                rs.getInt("Amount"),
                rs.getInt("OwnerId"), rs.getString("OwnerName")
        );
    }
}

// ─────────────────────── CASSETTE ───────────────────────
class CassetteDAO extends BaseDAO {

    public List<Dto.Cassette> findAll() throws SQLException {
        List<Dto.Cassette> list = new ArrayList<>();
        String sql = """
            SELECT C.CasseteID, C.FilmID, F.Caption AS FilmCaption,
                   C.VideoID, V.Caption AS VideoCaption,
                   C.QualityID, Q.QualityName, C.Photo, C.Demand
            FROM Cassette C
            LEFT JOIN Film    F ON F.FilmID    = C.FilmID
            LEFT JOIN Video   V ON V.VideoID   = C.VideoID
            LEFT JOIN Quality Q ON Q.QualityID = C.QualityID
            ORDER BY C.CasseteID
            """;
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapCassette(rs));
        }
        return list;
    }

    public int insert(int filmId, int videoId, int qualityId,
                      byte[] photo, boolean demand) throws SQLException {
        return executeInsertReturnKey(
                "INSERT INTO Cassette(FilmID,VideoID,QualityID,Photo,Demand) VALUES(?,?,?,?,?)",
                filmId, videoId, qualityId, photo, demand
        );
    }

    public int update(int id, int filmId, int videoId, int qualityId,
                      byte[] photo, boolean demand) throws SQLException {
        return executeUpdate(
                "UPDATE Cassette SET FilmID=?,VideoID=?,QualityID=?,Photo=?,Demand=? WHERE CasseteID=?",
                filmId, videoId, qualityId, photo, demand, id
        );
    }

    public int updatePhoto(int id, byte[] photo) throws SQLException {
        return executeUpdate(
                "UPDATE Cassette SET Photo=? WHERE CasseteID=?", photo, id
        );
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Cassette WHERE CasseteID=?", id);
    }

    private Dto.Cassette mapCassette(ResultSet rs) throws SQLException {
        byte[] photo = rs.getBytes("Photo");
        return new Dto.Cassette(
                rs.getInt("CasseteID"),
                rs.getInt("FilmID"), rs.getString("FilmCaption"),
                rs.getInt("VideoID"), rs.getString("VideoCaption"),
                rs.getInt("QualityID"), rs.getString("QualityName"),
                photo, rs.getBoolean("Demand")
        );
    }
}

// ─────────────────────── RECEIPT ───────────────────────
class ReceiptDAO extends BaseDAO {

    public List<Dto.Receipt> findAll() throws SQLException {
        List<Dto.Receipt> list = new ArrayList<>();
        String sql = """
            SELECT R.ReceiptID, R.CassetteID, R.VideoID,
                   V.Caption AS VideoCaption,
                   R.ServiceID, S.ServiceName, R.Date, R.Price
            FROM Receipt R
            LEFT JOIN Video   V ON V.VideoID   = R.VideoID
            LEFT JOIN Service S ON S.ServiceID = R.ServiceID
            ORDER BY R.Date DESC
            """;
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapReceipt(rs));
        }
        return list;
    }

    public int insert(int cassetteId, int videoId, int serviceId,
                      LocalDate date, int price) throws SQLException {
        return executeInsertReturnKey(
                "INSERT INTO Receipt(CassetteID,VideoID,ServiceID,Date,Price) VALUES(?,?,?,?,?)",
                cassetteId, videoId, serviceId, date, price
        );
    }

    public int update(int id, int cassetteId, int videoId, int serviceId,
                      LocalDate date, int price) throws SQLException {
        return executeUpdate(
                "UPDATE Receipt SET CassetteID=?,VideoID=?,ServiceID=?,Date=?,Price=? WHERE ReceiptID=?",
                cassetteId, videoId, serviceId, date, price, id
        );
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Receipt WHERE ReceiptID=?", id);
    }

    private Dto.Receipt mapReceipt(ResultSet rs) throws SQLException {
        Date d = rs.getDate("Date");
        return new Dto.Receipt(
                rs.getInt("ReceiptID"), rs.getInt("CassetteID"),
                rs.getInt("VideoID"), rs.getString("VideoCaption"),
                rs.getInt("ServiceID"), rs.getString("ServiceName"),
                d != null ? d.toLocalDate() : null,
                rs.getInt("Price")
        );
    }
}
