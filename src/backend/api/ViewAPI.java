package backend.api;

import backend.data.dao.DAORegistry;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * API для доступа к PostgreSQL VIEW
 */
public class ViewAPI {

    private final DAORegistry dao = DAORegistry.getInstance();

    public List<Map<String, Object>> filmsFull()           throws SQLException { return dao.views().getFilmsFull(); }
    public List<Map<String, Object>> cassettesFull()       throws SQLException { return dao.views().getCassettesFull(); }
    public List<Map<String, Object>> receiptsServices()    throws SQLException { return dao.views().getReceiptsServices(); }
    public List<Map<String, Object>> videoWithCassettes()  throws SQLException { return dao.views().getVideoWithCassettes(); }
    public List<Map<String, Object>> totalRevenue()        throws SQLException { return dao.views().getTotalRevenue(); }
    public List<Map<String, Object>> people()              throws SQLException { return dao.views().getPeople(); }
    public List<Map<String, Object>> receiptCategory()     throws SQLException { return dao.views().getReceiptCategory(); }
    public List<Map<String, Object>> usedCassettes()       throws SQLException { return dao.views().getUsedCassettes(); }
    public List<Map<String, Object>> unusedCassettes()     throws SQLException { return dao.views().getUnusedCassettes(); }
    public List<Map<String, Object>> videosWithoutReceipts()throws SQLException{ return dao.views().getVideosWithoutReceipts(); }
    public List<Map<String, Object>> filmsAboveAverage()   throws SQLException { return dao.views().getFilmsAboveAverage(); }
    public List<Map<String, Object>> bestWorstVideos()     throws SQLException { return dao.views().getBestWorstVideos(); }
    public List<Map<String, Object>> revenueDifference()   throws SQLException { return dao.views().getRevenueDifference(); }
    public List<Map<String, Object>> nightVideoPercent()   throws SQLException { return dao.views().getNightVideoPercent(); }
    public List<Map<String, Object>> avgClients()          throws SQLException { return dao.views().getAvgClients(); }
}
