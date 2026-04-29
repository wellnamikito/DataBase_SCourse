package backend.dao;

import backend.model.Owner;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.*;

/**
 * DAD для таблицы Owners
 * Реализует CRUD-операции (создать, чтение, обновить, удалить)
 */
public class OwnerDAO extends BaseDAO {
    // Получить всех владельцев в виде TableModel для JTable
    public DefaultTableModel getAll() throws SQLException{
        return executeQuery("SELECT OwnerID, Familia, Name, Otchestvo" +
                " FROM Owners ORDER BY OwnerID");
    }

    // Получить список всех владельцев как объекты
    public List<Owner> getAllList() throws SQLException{
        List<Owner> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try(PreparedStatement ps = conn.prepareStatement(
                "SELECT OwnerID, Familia, Name, Otchestvo FROM Owners ORDER BY OwnerID"
        );
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
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
    // Добавить нового владельца
    public void insert(Owner o) throws SQLException{
        executeInsertGetKey(
                "INSERT INTO Owners(Familia, Name, Otchestvo) VALUES (?, ?, ?)",
                o.getFamilia(), o.getName(), o.getOtchestvo()
        );
    }

    /** Обновить данные владельца */
    public void update(Owner o) throws SQLException {
        executeUpdate(
                "UPDATE Owners SET Familia=?, Name=?, Otchestvo=? WHERE OwnerID=?",
                o.getFamilia(), o.getName(), o.getOtchestvo(), o.getOwnerID()
        );
    }

    /** Удалить владельца по ID */
    public void delete(int ownerId) throws SQLException {
        executeUpdate("DELETE FROM Owners WHERE OwnerID=?", ownerId);
    }

    /** Поиск по фамилии (для запроса #1) */
    public DefaultTableModel searchByFamilia(String familia) throws SQLException {
        return executeQuery(
                "SELECT V.Caption, V.Address, O.Familia, O.Name, O.Otchestvo " +
                        "FROM Video V INNER JOIN Owners O ON O.OwnerID = V.OwnerId " +
                        "WHERE O.Familia = ?", familia
        );
    }
}
