package backend.api;
import backend.data.dao.DAORegistry;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * API для выполнения параметризованных запросов через PostgreSQL функции
 */
public class QueryAPI {

    private final DAORegistry dao = DAORegistry.getInstance();

    public List<Map<String, Object>> videosByOwner(String familia) throws SQLException {
        return dao.views().getVideosByOwner(familia);
    }

    public List<Map<String, Object>> cassettesByQuality(String qualityName) throws SQLException {
        return dao.views().getCassettesByQuality(qualityName);
    }

    public List<Map<String, Object>> receiptsByServicePeriod(String serviceName, LocalDate from, LocalDate to) throws SQLException {
        return dao.views().getReceiptsByServicePeriod(serviceName, from, to);
    }

    public List<Map<String, Object>> operationsFromDate(String serviceName, LocalDate from) throws SQLException {
        return dao.views().getOperationsFromDate(serviceName, from);
    }

    public List<Map<String, Object>> videoRevenueOver(int minRevenue) throws SQLException {
        return dao.views().getVideoRevenueOver(minRevenue);
    }

    public List<Map<String, Object>> revenueByPeriod(LocalDate from, LocalDate to) throws SQLException {
        return dao.views().getRevenueByPeriod(from, to);
    }

    public List<Map<String, Object>> videosByMask(String mask) throws SQLException {
        return dao.views().getVideosByMask(mask);
    }

    public List<Map<String, Object>> receiptsByPrice(int price) throws SQLException {
        return dao.views().getReceiptsByPrice(price);
    }

    public List<Map<String, Object>> receiptsPriceOver(int price) throws SQLException {
        return dao.views().getReceiptsPriceOver(price);
    }

    public List<Map<String, Object>> studiosByYearRevenue(String year, int minRevenue) throws SQLException {
        return dao.views().getStudiosByYearRevenue(year, minRevenue);
    }

    public List<Map<String, Object>> serviceStats(String serviceName, int year) throws SQLException {
        return dao.views().getServiceStats(serviceName, year);
    }
}
