package backend.data.dao;
import backend.data.model.Dto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// ─────────────────────── DISTRICT ───────────────────────
class DistrictDAO extends BaseDAO {
    public List<Dto.District> findAll() throws SQLException {
        List<Dto.District> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DistrictID, DistrictName FROM Districts ORDER BY DistrictName")) {
            while (rs.next())
                list.add(new Dto.District(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public int insert(String name) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Districts(DistrictName) VALUES(?)", name);
    }
    public int update(int id, String name) throws SQLException {
        return executeUpdate("UPDATE Districts SET DistrictName=? WHERE DistrictID=?", name, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Districts WHERE DistrictID=?", id);
    }
}

// ─────────────────────── QUALITY ───────────────────────
class QualityDAO extends BaseDAO {
    public List<Dto.Quality> findAll() throws SQLException {
        List<Dto.Quality> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT QualityID, QualityName FROM Quality ORDER BY QualityName")) {
            while (rs.next())
                list.add(new Dto.Quality(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public int insert(String name) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Quality(QualityName) VALUES(?)", name);
    }
    public int update(int id, String name) throws SQLException {
        return executeUpdate("UPDATE Quality SET QualityName=? WHERE QualityID=?", name, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Quality WHERE QualityID=?", id);
    }
}

// ─────────────────────── SERVICE ───────────────────────
class ServiceDAO extends BaseDAO {
    public List<Dto.Service> findAll() throws SQLException {
        List<Dto.Service> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT ServiceID, ServiceName FROM Service ORDER BY ServiceName")) {
            while (rs.next())
                list.add(new Dto.Service(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public int insert(String name) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Service(ServiceName) VALUES(?)", name);
    }
    public int update(int id, String name) throws SQLException {
        return executeUpdate("UPDATE Service SET ServiceName=? WHERE ServiceID=?", name, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Service WHERE ServiceID=?", id);
    }
}

// ─────────────────────── DIRECTOR ───────────────────────
class DirectorDAO extends BaseDAO {
    public List<Dto.Director> findAll() throws SQLException {
        List<Dto.Director> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT DirectorID, Familia, Name, Otchestvo FROM Director ORDER BY Familia")) {
            while (rs.next())
                list.add(new Dto.Director(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        }
        return list;
    }
    public int insert(String f, String n, String o) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Director(Familia,Name,Otchestvo) VALUES(?,?,?)", f, n, o);
    }
    public int update(int id, String f, String n, String o) throws SQLException {
        return executeUpdate("UPDATE Director SET Familia=?,Name=?,Otchestvo=? WHERE DirectorID=?", f, n, o, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Director WHERE DirectorID=?", id);
    }
}

// ─────────────────────── COUNTRY ───────────────────────
class CountryDAO extends BaseDAO {
    public List<Dto.Country> findAll() throws SQLException {
        List<Dto.Country> list = new ArrayList<>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT CountryID, CountryName FROM Country ORDER BY CountryName")) {
            while (rs.next())
                list.add(new Dto.Country(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public int insert(String name) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Country(CountryName) VALUES(?)", name);
    }
    public int update(int id, String name) throws SQLException {
        return executeUpdate("UPDATE Country SET CountryName=? WHERE CountryID=?", name, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Country WHERE CountryID=?", id);
    }
}

// ─────────────────────── STUDIO ───────────────────────
class StudioDAO extends BaseDAO {
    public List<Dto.Studio> findAll() throws SQLException {
        List<Dto.Studio> list = new ArrayList<>();
        String sql = "SELECT S.StudioID, S.StudioName, S.CountryID, C.CountryName " +
                "FROM Studio S LEFT JOIN Country C ON C.CountryID=S.CountryID ORDER BY S.StudioName";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                list.add(new Dto.Studio(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4)));
        }
        return list;
    }
    public int insert(String name, int countryId) throws SQLException {
        return executeInsertReturnKey("INSERT INTO Studio(StudioName,CountryID) VALUES(?,?)", name, countryId);
    }
    public int update(int id, String name, int countryId) throws SQLException {
        return executeUpdate("UPDATE Studio SET StudioName=?,CountryID=? WHERE StudioID=?", name, countryId, id);
    }
    public int delete(int id) throws SQLException {
        return executeUpdate("DELETE FROM Studio WHERE StudioID=?", id);
    }
}

// ─────────────────────── AUDIT LOG ───────────────────────
class AuditLogDAO extends BaseDAO {
    public List<Dto.AuditLog> findAll() throws SQLException {
        List<Dto.AuditLog> list = new ArrayList<>();
        String sql = "SELECT LogID, TableName, OperationType, OperationDate FROM AuditLog ORDER BY OperationDate DESC LIMIT 500";
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("OperationDate");
                list.add(new Dto.AuditLog(
                        rs.getInt("LogID"), rs.getString("TableName"),
                        rs.getString("OperationType"),
                        ts != null ? ts.toLocalDateTime() : null
                ));
            }
        }
        return list;
    }
}
