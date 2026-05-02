package backend.dao;

import backend.model.Owner;
import backend.util.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OwnerDAO extends BaseDAO {

    public DefaultTableModel getAll() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT Familia, Name, Otchestvo FROM Owners ORDER BY Familia"
        );
        renameColumns(m, new String[]{
                I18n.t("f.familia"), I18n.t("f.name"), I18n.t("f.otchestvo")
        });
        return m;
    }

    public List<Owner> getAllList() throws SQLException {
        List<Owner> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT OwnerID, Familia, Name, Otchestvo FROM Owners ORDER BY OwnerID");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Owner o = new Owner();
                o.setOwnerID(rs.getInt("OwnerID"));
                o.setFamilia(rs.getString("Familia"));
                o.setName(rs.getString("Name"));
                o.setOtchestvo(rs.getString("Otchestvo"));
                list.add(o);
            }
        }
        return list;
    }

    public void insert(Owner o) throws SQLException {
        executeInsertGetKey("INSERT INTO Owners(Familia, Name, Otchestvo) VALUES (?, ?, ?)",
                o.getFamilia(), o.getName(), o.getOtchestvo());
    }

    public void update(Owner o) throws SQLException {
        executeUpdate("UPDATE Owners SET Familia=?, Name=?, Otchestvo=? WHERE OwnerID=?",
                o.getFamilia(), o.getName(), o.getOtchestvo(), o.getOwnerID());
    }

    public void delete(int ownerId) throws SQLException {
        executeUpdate("DELETE FROM Owners WHERE OwnerID=?", ownerId);
    }
}