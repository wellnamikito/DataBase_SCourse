package backend.dao;

import backend.model.Video;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        return executeQuery(
                "SELECT V.VideoID, V.Caption, D.DistrictName, V.Address, V.Type, " +
                        "V.Phone, V.Licence, V.TimeStart, V.TimeEnd, V.Amount, " +
                        "O.Familia || ' ' || O.Name AS OwnerName " +
                        "FROM Video V " +
                        "LEFT JOIN Districts D ON D.DistrictID = V.DistrictID " +
                        "LEFT JOIN Owners O ON O.OwnerID = V.OwnerId " +
                        "ORDER BY V.VideoID"
        );
    }

    // ✅ ВАЖНО: этот метод у тебя ломал MainFrame
    public List<Video> getAllList() throws SQLException {
        List<Video> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Video ORDER BY VideoID");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
        }

        return list;
    }

    public Video getById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Video WHERE VideoID=?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ✅ INSERT
    public void insert(Video v) throws SQLException {
        executeUpdate(
                "INSERT INTO Video(Caption, DistrictID, Address, Type, Phone, Licence, " +
                        "TimeStart, TimeEnd, Amount, OwnerId) VALUES(?,?,?,?,?,?,?,?,?,?)",
                v.getCaption(), v.getDistrictId(), v.getAddress(), v.getType(),
                v.getPhone(), v.getLicence(), v.getTimeStart(), v.getTimeEnd(),
                v.getAmount(), v.getOwnerId()
        );
    }

    // ✅ UPDATE
    public void update(Video v) throws SQLException {
        executeUpdate(
                "UPDATE Video SET Caption=?, DistrictID=?, Address=?, Type=?, Phone=?, Licence=?, " +
                        "TimeStart=?, TimeEnd=?, Amount=?, OwnerId=? WHERE VideoID=?",
                v.getCaption(), v.getDistrictId(), v.getAddress(), v.getType(),
                v.getPhone(), v.getLicence(), v.getTimeStart(), v.getTimeEnd(),
                v.getAmount(), v.getOwnerId(), v.getVideoId()
        );
    }

    public void delete(int id) throws SQLException {
        executeUpdate("DELETE FROM Video WHERE VideoID=?", id);
    }

    private Video mapRow(ResultSet rs) throws SQLException {
        Video v = new Video();
        v.setVideoId(rs.getInt("VideoID"));
        v.setCaption(rs.getString("Caption"));
        v.setDistrictId(rs.getInt("DistrictID"));
        v.setAddress(rs.getString("Address"));
        v.setType(rs.getString("Type"));
        v.setPhone(rs.getString("Phone"));
        v.setLicence(rs.getString("Licence"));
        v.setTimeStart(rs.getInt("TimeStart"));
        v.setTimeEnd(rs.getInt("TimeEnd"));
        v.setAmount(rs.getInt("Amount"));
        v.setOwnerId(rs.getInt("OwnerId"));
        return v;
    }

    public DefaultTableModel getAllWithId() throws SQLException {
        return executeQuery(
                "SELECT V.VideoID, V.Caption, D.DistrictName, V.Address, V.Type, " +
                        "V.Phone, V.Licence, V.TimeStart, V.TimeEnd, V.Amount, " +
                        "O.Familia || ' ' || O.Name AS OwnerName " +
                        "FROM Video V " +
                        "LEFT JOIN Districts D ON D.DistrictID = V.DistrictID " +
                        "LEFT JOIN Owners O ON O.OwnerID = V.OwnerId " +
                        "ORDER BY V.VideoID"
        );
    }

    public DefaultTableModel getSimpleView() throws SQLException {
        return executeQuery("SELECT Caption, Address FROM vw_video_simple");
    }

    public void updateViaView(int id, String caption, String address) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE vw_video_edit SET Caption=?, Address=? WHERE VideoID=?")) {

            conn.setAutoCommit(false);

            ps.setString(1, caption);
            ps.setString(2, address);
            ps.setInt(3, id);

            ps.executeUpdate();

            conn.commit();
        }
    }
}