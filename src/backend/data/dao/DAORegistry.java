package backend.data.dao;

/**
 * Реестр всех DAO — единая точка доступа к DATA слою.
 * Используется из API слоя.
 */
public class DAORegistry {

    private static DAORegistry instance;

    private final OwnerDAO ownerDAO           = new OwnerDAO();
    private final DistrictDAO districtDAO     = new DistrictDAO();
    private final QualityDAO qualityDAO       = new QualityDAO();
    private final ServiceDAO serviceDAO       = new ServiceDAO();
    private final DirectorDAO directorDAO     = new DirectorDAO();
    private final CountryDAO countryDAO       = new CountryDAO();
    private final StudioDAO studioDAO         = new StudioDAO();
    private final FilmDAO filmDAO             = new FilmDAO();
    private final VideoDAO videoDAO           = new VideoDAO();
    private final CassetteDAO cassetteDAO     = new CassetteDAO();
    private final ReceiptDAO receiptDAO       = new ReceiptDAO();
    private final AuditLogDAO auditLogDAO     = new AuditLogDAO();
    private final ViewDAO viewDAO             = new ViewDAO();

    private DAORegistry() {}

    public static DAORegistry getInstance() {
        if (instance == null) instance = new DAORegistry();
        return instance;
    }

    public OwnerDAO owners()     { return ownerDAO; }
    public DistrictDAO districts(){ return districtDAO; }
    public QualityDAO qualities() { return qualityDAO; }
    public ServiceDAO services()  { return serviceDAO; }
    public DirectorDAO directors(){ return directorDAO; }
    public CountryDAO countries() { return countryDAO; }
    public StudioDAO studios()    { return studioDAO; }
    public FilmDAO films()        { return filmDAO; }
    public VideoDAO videos()      { return videoDAO; }
    public CassetteDAO cassettes(){ return cassetteDAO; }
    public ReceiptDAO receipts()  { return receiptDAO; }
    public AuditLogDAO auditLogs(){ return auditLogDAO; }
    public ViewDAO views()        { return viewDAO; }
}
