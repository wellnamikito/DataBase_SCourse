package backend.dao;

import backend.model.*;
import backend.util.DatabaseConnection;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleDAO extends BaseDAO {

    // ======= Districts =======
    public DefaultTableModel getDistricts() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT DistrictID, DistrictName FROM Districts ORDER BY DistrictName");
        renameColumns(m, new String[]{"ID", "Название района"});
        return m;
    }
    public List<District> getDistrictList() throws SQLException {
        List<District> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT DistrictID, DistrictName FROM Districts ORDER BY DistrictName");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new District(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public void insertDistrict(String name) throws SQLException { executeInsertGetKey("INSERT INTO Districts(DistrictName) VALUES(?)", name); }
    public void updateDistrict(int id, String name) throws SQLException { executeUpdate("UPDATE Districts SET DistrictName=? WHERE DistrictID=?", name, id); }
    public void deleteDistrict(int id) throws SQLException { executeUpdate("DELETE FROM Districts WHERE DistrictID=?", id); }

    // ======= Service =======
    public DefaultTableModel getServices() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT ServiceID, ServiceName FROM Service ORDER BY ServiceName");
        renameColumns(m, new String[]{"ID", "Название услуги"});
        return m;
    }
    public List<Service> getServiceList() throws SQLException {
        List<Service> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT ServiceID, ServiceName FROM Service ORDER BY ServiceName");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Service(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public void insertService(String name) throws SQLException { executeInsertGetKey("INSERT INTO Service(ServiceName) VALUES(?)", name); }
    public void updateService(int id, String name) throws SQLException { executeUpdate("UPDATE Service SET ServiceName=? WHERE ServiceID=?", name, id); }
    public void deleteService(int id) throws SQLException { executeUpdate("DELETE FROM Service WHERE ServiceID=?", id); }

    // ======= Quality =======
    public DefaultTableModel getQualities() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT QualityID, QualityName FROM Quality ORDER BY QualityName");
        renameColumns(m, new String[]{"ID", "Качество"});
        return m;
    }
    public List<Quality> getQualityList() throws SQLException {
        List<Quality> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT QualityID, QualityName FROM Quality ORDER BY QualityName");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Quality(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public void insertQuality(String name) throws SQLException { executeInsertGetKey("INSERT INTO Quality(QualityName) VALUES(?)", name); }
    public void updateQuality(int id, String name) throws SQLException { executeUpdate("UPDATE Quality SET QualityName=? WHERE QualityID=?", name, id); }
    public void deleteQuality(int id) throws SQLException { executeUpdate("DELETE FROM Quality WHERE QualityID=?", id); }

    // ======= Director =======
    public DefaultTableModel getDirectors() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT Familia, Name, Otchestvo FROM Director ORDER BY Familia");
        renameColumns(m, new String[]{"Фамилия", "Имя", "Отчество"});
        return m;
    }
    public List<Director> getDirectorList() throws SQLException {
        List<Director> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT DirectorID, Familia, Name, Otchestvo FROM Director ORDER BY Familia");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Director(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
        }
        return list;
    }
    public void insertDirector(Director d) throws SQLException { executeInsertGetKey("INSERT INTO Director(Familia,Name,Otchestvo) VALUES(?,?,?)", d.getFamilia(), d.getName(), d.getOtchestvo()); }
    public void updateDirector(Director d) throws SQLException { executeUpdate("UPDATE Director SET Familia=?,Name=?,Otchestvo=? WHERE DirectorID=?", d.getFamilia(), d.getName(), d.getOtchestvo(), d.getDirectorId()); }
    public void deleteDirector(int id) throws SQLException { executeUpdate("DELETE FROM Director WHERE DirectorID=?", id); }

    // ======= Studio =======
    public DefaultTableModel getStudios() throws SQLException {
        DefaultTableModel m = executeQuery(
                "SELECT S.StudioName, C.CountryName FROM Studio S LEFT JOIN Country C ON C.CountryID=S.CountryID ORDER BY S.StudioName"
        );
        renameColumns(m, new String[]{"Студия", "Страна"});
        return m;
    }
    public List<Studio> getStudioList() throws SQLException {
        List<Studio> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT StudioID, StudioName, CountryID FROM Studio ORDER BY StudioName");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Studio(rs.getInt(1), rs.getString(2), rs.getInt(3)));
        }
        return list;
    }
    public void insertStudio(Studio s) throws SQLException { executeInsertGetKey("INSERT INTO Studio(StudioName, CountryID) VALUES(?,?)", s.getStudioName(), s.getCountryId()); }
    public void updateStudio(Studio s) throws SQLException { executeUpdate("UPDATE Studio SET StudioName=?, CountryID=? WHERE StudioID=?", s.getStudioName(), s.getCountryId(), s.getStudioId()); }
    public void deleteStudio(int id) throws SQLException { executeUpdate("DELETE FROM Studio WHERE StudioID=?", id); }

    // ======= Country =======
    public DefaultTableModel getCountries() throws SQLException {
        DefaultTableModel m = executeQuery("SELECT CountryName FROM Country ORDER BY CountryName");
        renameColumns(m, new String[]{"Страна"});
        return m;
    }
    public List<Country> getCountryList() throws SQLException {
        List<Country> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement("SELECT CountryID, CountryName FROM Country ORDER BY CountryName");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Country(rs.getInt(1), rs.getString(2)));
        }
        return list;
    }
    public void insertCountry(String name) throws SQLException { executeInsertGetKey("INSERT INTO Country(CountryName) VALUES(?)", name); }
    public void updateCountry(int id, String name) throws SQLException { executeUpdate("UPDATE Country SET CountryName=? WHERE CountryID=?", name, id); }
    public void deleteCountry(int id) throws SQLException { executeUpdate("DELETE FROM Country WHERE CountryID=?", id); }
}