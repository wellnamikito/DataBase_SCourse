package backend.dao;

import backend.model.Video;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VideoDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        DefaultTableModel model = executeQuery(
                "SELECT V.Caption, D.DistrictName, V.Address, V.Type, " +
                        "V.Phone, V.Licence, V.TimeStart, V.TimeEnd, V.Amount, " +
                        "O.Familia || ' ' || O.Name AS OwnerName " +
                        "FROM Video V " +
                        "LEFT JOIN Districts D ON D.DistrictID = V.DistrictID " +
                        "LEFT JOIN Owners O ON O.OwnerID = V.OwnerId " +
                        "ORDER BY V.Caption"
        );
        renameColumns(model, new String[]{
                "Название", "Район", "Адрес", "Тип",
                "Телефон", "Лицензия", "Откр.", "Закр.",
                "Клиентов", "Владелец"
        });
        return model;
    }

    public List<Video> getAllList() throws SQLException {
        List<Video> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Video ORDER BY VideoID");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Video getById(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Video WHERE VideoID=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public void insert(Video v) throws SQLException {
        executeInsertGetKey(
                "INSERT INTO Video(Caption, DistrictID, Address, Type, Phone, Licence, " +
                        "TimeStart, TimeEnd, Amount, OwnerId) VALUES(?,?,?,?,?,?,?,?,?,?)",
                v.getCaption(), v.getDistrictId(), v.getAddress(), v.getType(),
                v.getPhone(), v.getLicence(), v.getTimeStart(), v.getTimeEnd(),
                v.getAmount(), v.getOwnerId()
        );
    }

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

    public void updateViaView(int videoId, String caption, String address) throws SQLException {
        executeUpdate(
                "UPDATE vw_video_simple SET Caption=?, Address=? WHERE VideoID=?",
                caption, address, videoId
        );
    }

    public DefaultTableModel getSimpleView() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT Caption, Address FROM vw_video_simple ORDER BY Caption"
        );
        renameColumns(m, new String[]{"Название", "Адрес"});
        return m;
    }

    public DefaultTableModel getRevenueChart() throws SQLException {
        return executeQuery(
                "SELECT V.Caption, COALESCE(SUM(R.Price),0) AS Total " +
                        "FROM Video V LEFT JOIN Receipt R ON V.VideoID = R.VideoID " +
                        "GROUP BY V.VideoID, V.Caption ORDER BY Total DESC"
        );
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
}