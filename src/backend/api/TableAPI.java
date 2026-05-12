package backend.api;
import backend.security.Session;
import backend.data.dao.DAORegistry;
import backend.data.model.Dto;

import java.sql.SQLException;
import java.util.List;

/**
 * API для работы с таблицами.
 * Содержит проверку прав доступа.
 * UI работает ТОЛЬКО через этот класс — никакого прямого SQL во фронтенде.
 */
public class TableAPI {

    private final DAORegistry dao = DAORegistry.getInstance();

    // ─────────── OWNERS ───────────
    public List<Dto.Owner> getOwners() throws SQLException {
        return dao.owners().findAll();
    }

    public void createOwner(String familia, String name, String otchestvo) throws Exception {
        requireAdmin();
        dao.owners().insert(familia, name, otchestvo);
    }

    public void updateOwner(int id, String familia, String name, String otchestvo) throws Exception {
        requireAdmin();
        dao.owners().update(id, familia, name, otchestvo);
    }

    public void deleteOwner(int id) throws Exception {
        requireAdmin();
        dao.owners().delete(id);
    }

    // ─────────── DISTRICTS ───────────
    public List<Dto.District> getDistricts() throws SQLException {
        return dao.districts().findAll();
    }

    public void createDistrict(String name) throws Exception {
        requireAdmin();
        dao.districts().insert(name);
    }

    public void updateDistrict(int id, String name) throws Exception {
        requireAdmin();
        dao.districts().update(id, name);
    }

    public void deleteDistrict(int id) throws Exception {
        requireAdmin();
        dao.districts().delete(id);
    }

    // ─────────── QUALITY ───────────
    public List<Dto.Quality> getQualities() throws SQLException {
        return dao.qualities().findAll();
    }

    public void createQuality(String name) throws Exception {
        requireAdmin();
        dao.qualities().insert(name);
    }

    public void updateQuality(int id, String name) throws Exception {
        requireAdmin();
        dao.qualities().update(id, name);
    }

    public void deleteQuality(int id) throws Exception {
        requireAdmin();
        dao.qualities().delete(id);
    }

    // ─────────── SERVICE ───────────
    public List<Dto.Service> getServices() throws SQLException {
        return dao.services().findAll();
    }

    public void createService(String name) throws Exception {
        requireAdmin();
        dao.services().insert(name);
    }

    public void updateService(int id, String name) throws Exception {
        requireAdmin();
        dao.services().update(id, name);
    }

    public void deleteService(int id) throws Exception {
        requireAdmin();
        dao.services().delete(id);
    }

    // ─────────── DIRECTOR ───────────
    public List<Dto.Director> getDirectors() throws SQLException {
        return dao.directors().findAll();
    }

    public void createDirector(String f, String n, String o) throws Exception {
        requireAdmin();
        dao.directors().insert(f, n, o);
    }

    public void updateDirector(int id, String f, String n, String o) throws Exception {
        requireAdmin();
        dao.directors().update(id, f, n, o);
    }

    public void deleteDirector(int id) throws Exception {
        requireAdmin();
        dao.directors().delete(id);
    }

    // ─────────── COUNTRY ───────────
    public List<Dto.Country> getCountries() throws SQLException {
        return dao.countries().findAll();
    }

    public void createCountry(String name) throws Exception {
        requireAdmin();
        dao.countries().insert(name);
    }

    public void updateCountry(int id, String name) throws Exception {
        requireAdmin();
        dao.countries().update(id, name);
    }

    public void deleteCountry(int id) throws Exception {
        requireAdmin();
        dao.countries().delete(id);
    }

    // ─────────── STUDIO ───────────
    public List<Dto.Studio> getStudios() throws SQLException {
        return dao.studios().findAll();
    }

    public void createStudio(String name, int countryId) throws Exception {
        requireAdmin();
        dao.studios().insert(name, countryId);
    }

    public void updateStudio(int id, String name, int countryId) throws Exception {
        requireAdmin();
        dao.studios().update(id, name, countryId);
    }

    public void deleteStudio(int id) throws Exception {
        requireAdmin();
        dao.studios().delete(id);
    }

    // ─────────── FILM ───────────
    public List<Dto.Film> getFilms() throws SQLException {
        return dao.films().findAll();
    }

    public void createFilm(String caption, String year, int duration, String info,
                           int directorId, int studioId) throws Exception {
        requireAdmin();
        dao.films().insert(caption, year, duration, info, directorId, studioId);
    }

    public void updateFilm(int id, String caption, String year, int duration, String info,
                           int directorId, int studioId) throws Exception {
        requireAdmin();
        dao.films().update(id, caption, year, duration, info, directorId, studioId);
    }

    public void deleteFilm(int id) throws Exception {
        requireAdmin();
        dao.films().delete(id);
    }

    // ─────────── VIDEO ───────────
    public List<Dto.Video> getVideos() throws SQLException {
        return dao.videos().findAll();
    }

    public void createVideo(String caption, int districtId, String address, String type,
                            String phone, String licence, int timeStart, int timeEnd,
                            int amount, int ownerId) throws Exception {
        requireAdmin();
        dao.videos().insert(caption, districtId, address, type, phone, licence,
                timeStart, timeEnd, amount, ownerId);
    }

    public void updateVideo(int id, String caption, int districtId, String address, String type,
                            String phone, String licence, int timeStart, int timeEnd,
                            int amount, int ownerId) throws Exception {
        requireAdmin();
        dao.videos().update(id, caption, districtId, address, type, phone, licence,
                timeStart, timeEnd, amount, ownerId);
    }

    public void deleteVideo(int id) throws Exception {
        requireAdmin();
        dao.videos().delete(id);
    }

    // ─────────── CASSETTE ───────────
    public List<Dto.Cassette> getCassettes() throws SQLException {
        return dao.cassettes().findAll();
    }

    public void createCassette(int filmId, int videoId, int qualityId,
                               byte[] photo, boolean demand) throws Exception {
        requireAdmin();
        dao.cassettes().insert(filmId, videoId, qualityId, photo, demand);
    }

    public void updateCassette(int id, int filmId, int videoId, int qualityId,
                               byte[] photo, boolean demand) throws Exception {
        requireAdmin();
        dao.cassettes().update(id, filmId, videoId, qualityId, photo, demand);
    }

    public void updateCassettePhoto(int id, byte[] photo) throws Exception {
        requireAdmin();
        dao.cassettes().updatePhoto(id, photo);
    }

    public void deleteCassette(int id) throws Exception {
        requireAdmin();
        dao.cassettes().delete(id);
    }

    // ─────────── RECEIPT ───────────
    public List<Dto.Receipt> getReceipts() throws SQLException {
        return dao.receipts().findAll();
    }

    public void createReceipt(int cassetteId, int videoId, int serviceId,
                              java.time.LocalDate date, int price) throws Exception {
        requireAdmin();
        dao.receipts().insert(cassetteId, videoId, serviceId, date, price);
    }

    public void updateReceipt(int id, int cassetteId, int videoId, int serviceId,
                              java.time.LocalDate date, int price) throws Exception {
        requireAdmin();
        dao.receipts().update(id, cassetteId, videoId, serviceId, date, price);
    }

    public void deleteReceipt(int id) throws Exception {
        requireAdmin();
        dao.receipts().delete(id);
    }

    // ─────────── AUDIT LOG ───────────
    public List<Dto.AuditLog> getAuditLog() throws SQLException {
        return dao.auditLogs().findAll();
    }

    // ─────────── ACCESS CONTROL ───────────
    private void requireAdmin() throws SecurityException {
        if (!Session.getInstance().isAdmin()) {
            throw new SecurityException("Операция доступна только администратору");
        }
    }
}
