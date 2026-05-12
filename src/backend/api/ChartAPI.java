package backend.api;

import backend.data.dao.DAORegistry;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * API для получения данных диаграмм
 */
public class ChartAPI {

    private final DAORegistry dao = DAORegistry.getInstance();

    public List<Map<String, Object>> revenueByVideo() throws SQLException {
        return dao.views().getRevenueByVideo();
    }

    public List<Map<String, Object>> cassettesByQuality() throws SQLException {
        return dao.views().getCassettesByQualityChart();
    }

    public List<Map<String, Object>> revenueByMonth() throws SQLException {
        return dao.views().getRevenueByMonth();
    }

    public List<Map<String, Object>> cassettesByDemand() throws SQLException {
        return dao.views().getCassettesByDemand();
    }
}
