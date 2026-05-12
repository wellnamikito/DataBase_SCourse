package backend.data.dao;
import backend.data.model.Dto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerDAO extends BaseDAO {

    public List<Dto.Owner> findAll() throws SQLException {
        List<Dto.Owner> list = new ArrayList<>();
        String sql = "SELECT OwnerID, Familia, Name, Otchestvo FROM Owners ORDER BY Familia";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Dto.Owner findById(int id) throws SQLException {
        String sql = "SELECT OwnerID, Familia, Name, Otchestvo FROM Owners WHERE OwnerID=?";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public int insert(String familia, String name, String otchestvo) throws SQLException {
        return executeInsertReturnKey(
                "INSERT INTO Owners(Familia, Name, Otchestvo) VALUES (?,?,?)",
                familia, name, otchestvo
        );
    }

    public int update(int id, String familia, String name, String otchestvo) throws SQLException {
        return executeUpdate(
                "UPDATE Owners SET Familia=?, Name=?, Otchestvo=? WHERE OwnerID=?",
                familia, name, otchestvo, id
        );
    }

    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Owners WHERE OwnerID=?", id);
    }

    private Dto.Owner map(ResultSet rs) throws SQLException {
        return new Dto.Owner(
                rs.getInt("OwnerID"),
                rs.getString("Familia"),
                rs.getString("Name"),
                rs.getString("Otchestvo")
        );
    }
}
