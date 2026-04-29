package frontend.UI;

import backend.dao.*;
import backend.model.*;
import frontend.UI.Dialogs.*;
import frontend.UI.Panels.*;
import backend.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Главное окно приложения "Видеопрокат".
 *
 * Структура:
 * - JMenuBar: Таблицы | Представления | Запросы | Диаграммы | Отчёты
 * - Центр: CardLayout с панелями
 * - Строка состояния: статус подключения
 */
public class MainFrame extends JFrame {

    // DAO
    private final OwnerDAO   ownerDAO   = new OwnerDAO();
    private final VideoDAO   videoDAO   = new VideoDAO();
    private final FilmDAO    filmDAO    = new FilmDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final ReceiptDAO  receiptDAO = new ReceiptDAO();
    private final SimpleDAO   simpleDAO  = new SimpleDAO();

    // Центральная панель с CardLayout
    private final JPanel cardPanel = new JPanel(new CardLayout());
    private final JLabel statusBar = new JLabel(" 🔌 Нет подключения");

    // Ключи карточек
    private static final String CARD_OWNERS    = "owners";
    private static final String CARD_VIDEO     = "video";
    private static final String CARD_FILM      = "film";
    private static final String CARD_CASSETTE  = "cassette";
    private static final String CARD_RECEIPT   = "receipt";
    private static final String CARD_DISTRICT  = "district";
    private static final String CARD_SERVICE   = "service";
    private static final String CARD_QUALITY   = "quality";
    private static final String CARD_DIRECTOR  = "director";
    private static final String CARD_STUDIO    = "studio";
    private static final String CARD_COUNTRY   = "country";
    private static final String CARD_MASTER    = "master_detail";
    private static final String CARD_VW_FILMS  = "vw_films";
    private static final String CARD_VW_REVENUE= "vw_revenue";
    private static final String CARD_VW_PEOPLE = "vw_people";
    private static final String CARD_VW_SIMPLE = "vw_video_simple";
    private static final String CARD_VW_CATEG  = "vw_receipt_category";
    private static final String CARD_QUERIES   = "queries";
    private static final String CARD_CHARTS    = "charts";
    private static final String CARD_REPORTS   = "reports";

    // Таблица-панели
    private TablePanel pOwners, pVideo, pFilm, pCassette, pReceipt;
    private TablePanel pDistrict, pService, pQuality, pDirector, pStudio, pCountry;
    private TablePanel pVwFilms, pVwRevenue, pVwPeople, pVwCateg;

    public MainFrame() {
        super("🎬 Видеопрокат — Информационная система");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        buildMenu();
        buildCards();
        buildStatusBar();

        // Стартовый экран
        showCard(CARD_MASTER);

        // Проверка подключения
        checkConnection();
    }

    // ======================== МЕНЮ ========================

    private void buildMenu() {
        JMenuBar menuBar = new JMenuBar();

        // --- Таблицы ---
        JMenu menuTables = menu("📂 Таблицы");
        menuTables.add(menuItem("👥 Владельцы",    () -> { showCard(CARD_OWNERS);   refreshOwners(); }));
        menuTables.add(menuItem("🏪 Видеосалоны",  () -> { showCard(CARD_VIDEO);    refreshVideo(); }));
        menuTables.add(menuItem("🎬 Фильмы",       () -> { showCard(CARD_FILM);     refreshFilm(); }));
        menuTables.add(menuItem("📼 Кассеты",      () -> { showCard(CARD_CASSETTE); refreshCassette(); }));
        menuTables.add(menuItem("🧾 Квитанции",    () -> { showCard(CARD_RECEIPT);  refreshReceipt(); }));
        menuTables.addSeparator();
        menuTables.add(menuItem("📍 Районы",       () -> { showCard(CARD_DISTRICT); refreshDistrict(); }));
        menuTables.add(menuItem("🔧 Услуги",       () -> { showCard(CARD_SERVICE);  refreshService(); }));
        menuTables.add(menuItem("⭐ Качество",      () -> { showCard(CARD_QUALITY);  refreshQuality(); }));
        menuTables.add(menuItem("🎭 Режиссёры",    () -> { showCard(CARD_DIRECTOR); refreshDirector(); }));
        menuTables.add(menuItem("🏭 Студии",       () -> { showCard(CARD_STUDIO);   refreshStudio(); }));
        menuTables.add(menuItem("🌍 Страны",       () -> { showCard(CARD_COUNTRY);  refreshCountry(); }));
        menuTables.addSeparator();
        menuTables.add(menuItem("🔗 Master-Detail (Video + Cassette)", () -> showCard(CARD_MASTER)));

        // --- Представления ---
        JMenu menuViews = menu("👁️ Представления");
        menuViews.add(menuItem("vw_films_full",       () -> { showCard(CARD_VW_FILMS);   refreshVwFilms(); }));
        menuViews.add(menuItem("vw_total_revenue",    () -> { showCard(CARD_VW_REVENUE); refreshVwRevenue(); }));
        menuViews.add(menuItem("vw_people",           () -> { showCard(CARD_VW_PEOPLE);  refreshVwPeople(); }));
        menuViews.add(menuItem("vw_receipt_category", () -> { showCard(CARD_VW_CATEG);   refreshVwCateg(); }));
        menuViews.add(menuItem("✏️ vw_video_simple (обновляемое)", () -> showCard(CARD_VW_SIMPLE)));

        // --- Запросы ---
        JMenu menuQueries = menu("🔍 Запросы");
        menuQueries.add(menuItem("Выполнить SQL-запросы из ТЗ", () -> showCard(CARD_QUERIES)));

        // --- Диаграммы ---
        JMenu menuCharts = menu("📊 Диаграммы");
        menuCharts.add(menuItem("Открыть диаграммы", () -> showCard(CARD_CHARTS)));

        // --- Отчёты ---
        JMenu menuReports = menu("📋 Отчёты");
        menuReports.add(menuItem("Открыть отчёты", () -> showCard(CARD_REPORTS)));

        // --- О программе ---
        JMenu menuHelp = menu("❓ Справка");
        menuHelp.add(menuItem("О программе", () ->
                JOptionPane.showMessageDialog(this,
                        "<html><b>Видеопрокат — ИС</b><br>" +
                                "Архитектура: MVC + DAO<br>" +
                                "СУБД: PostgreSQL<br>" +
                                "UI: Java Swing<br>" +
                                "Charts: JFreeChart<br>" +
                                "Export: Apache POI</html>",
                        "О программе", JOptionPane.INFORMATION_MESSAGE)));

        menuBar.add(menuTables);
        menuBar.add(menuViews);
        menuBar.add(menuQueries);
        menuBar.add(menuCharts);
        menuBar.add(menuReports);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
    }

    private JMenu menu(String text) {
        JMenu m = new JMenu(text);
        m.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return m;
    }

    private JMenuItem menuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.addActionListener(e -> action.run());
        return item;
    }

    // ======================== КАРТОЧКИ ========================

    private void buildCards() {
        // Таблицы с CRUD
        pOwners   = new TablePanel("Владельцы (Owners)", true);
        pVideo    = new TablePanel("Видеосалоны (Video)", true);
        pFilm     = new TablePanel("Фильмы (Film)", true);
        pCassette = new TablePanel("Кассеты (Cassette)", true);
        pReceipt  = new TablePanel("Квитанции (Receipt)", true);
        pDistrict = new TablePanel("Районы (Districts)", true);
        pService  = new TablePanel("Услуги (Service)", true);
        pQuality  = new TablePanel("Качество (Quality)", true);
        pDirector = new TablePanel("Режиссёры (Director)", true);
        pStudio   = new TablePanel("Студии (Studio)", true);
        pCountry  = new TablePanel("Страны (Country)", true);

        // Представления (без CRUD)
        pVwFilms   = new TablePanel("Представление: vw_films_full", false);
        pVwRevenue = new TablePanel("Представление: vw_total_revenue", false);
        pVwPeople  = new TablePanel("Представление: vw_people", false);
        pVwCateg   = new TablePanel("Представление: vw_receipt_category", false);

        // CRUD-листенеры
        wireOwnerCrud();
        wireVideoCrud();
        wireFilmCrud();
        wireCassetteCrud();
        wireReceiptCrud();
        wireSimpleCrud();

        // Специальные панели
        MasterDetailPanel masterDetail = new MasterDetailPanel();
        ViewSimplePanel   viewSimple   = new ViewSimplePanel();
        QueryPanel        queryPanel   = new QueryPanel();
        DiagramPanel      chartPanel   = new DiagramPanel();
        ReportPanel       reportPanel  = new ReportPanel();

        // Добавить карточки
        cardPanel.add(pOwners,    CARD_OWNERS);
        cardPanel.add(pVideo,     CARD_VIDEO);
        cardPanel.add(pFilm,      CARD_FILM);
        cardPanel.add(pCassette,  CARD_CASSETTE);
        cardPanel.add(pReceipt,   CARD_RECEIPT);
        cardPanel.add(pDistrict,  CARD_DISTRICT);
        cardPanel.add(pService,   CARD_SERVICE);
        cardPanel.add(pQuality,   CARD_QUALITY);
        cardPanel.add(pDirector,  CARD_DIRECTOR);
        cardPanel.add(pStudio,    CARD_STUDIO);
        cardPanel.add(pCountry,   CARD_COUNTRY);
        cardPanel.add(masterDetail, CARD_MASTER);
        cardPanel.add(pVwFilms,   CARD_VW_FILMS);
        cardPanel.add(pVwRevenue, CARD_VW_REVENUE);
        cardPanel.add(pVwPeople,  CARD_VW_PEOPLE);
        cardPanel.add(viewSimple, CARD_VW_SIMPLE);
        cardPanel.add(pVwCateg,   CARD_VW_CATEG);
        cardPanel.add(queryPanel, CARD_QUERIES);
        cardPanel.add(chartPanel, CARD_CHARTS);
        cardPanel.add(reportPanel, CARD_REPORTS);

        add(cardPanel, BorderLayout.CENTER);
    }

    private void buildStatusBar() {
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void showCard(String key) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, key);
    }

    // ======================== CRUD Listeners ========================

    private void wireOwnerCrud() {
        pOwners.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                OwnerEditDialog d = new OwnerEditDialog(MainFrame.this, null);
                d.setVisible(true);
                if (d.isSaved()) refreshOwners();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                try {
                    int id = (int) pOwners.getSelectedValue(0);
                    List<Owner> list = ownerDAO.getAllList();
                    Owner owner = list.stream().filter(o -> o.getOwnerID() == id).findFirst().orElse(null);
                    OwnerEditDialog d = new OwnerEditDialog(MainFrame.this, owner);
                    d.setVisible(true);
                    if (d.isSaved()) refreshOwners();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pOwners.getSelectedValue(0);
                if (confirm("Удалить владельца ID=" + id + "?")) {
                    try { ownerDAO.delete(id); refreshOwners(); } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshOwners(); }
        });
    }

    private void wireVideoCrud() {
        pVideo.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                VideoEditDialog d = new VideoEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshVideo();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                try {
                    int id = (int) pVideo.getSelectedValue(0);
                    Video v = videoDAO.getById(id);
                    VideoEditDialog d = new VideoEditDialog(MainFrame.this, v);
                    d.setVisible(true); if (d.isSaved()) refreshVideo();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pVideo.getSelectedValue(0);
                if (confirm("Удалить видеосалон ID=" + id + "?"))
                    try { videoDAO.delete(id); refreshVideo(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshVideo(); }
        });
    }

    private void wireFilmCrud() {
        pFilm.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                FilmEditDialog d = new FilmEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshFilm();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                try {
                    int id = (int) pFilm.getSelectedValue(0);
                    Film f = filmDAO.getAllList().stream().filter(x -> x.getFilmId()==id).findFirst().orElse(null);
                    FilmEditDialog d = new FilmEditDialog(MainFrame.this, f);
                    d.setVisible(true); if (d.isSaved()) refreshFilm();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pFilm.getSelectedValue(0);
                if (confirm("Удалить фильм ID=" + id + "?"))
                    try { filmDAO.delete(id); refreshFilm(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshFilm(); }
        });
    }

    private void wireCassetteCrud() {
        pCassette.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                CassetteEditDialog d = new CassetteEditDialog(MainFrame.this, null, 0);
                d.setVisible(true); if (d.isSaved()) refreshCassette();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pCassette.getSelectedValue(0);
                CassetteEditDialog d = new CassetteEditDialog(MainFrame.this, id, 0);
                d.setVisible(true); if (d.isSaved()) refreshCassette();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pCassette.getSelectedValue(0);
                if (confirm("Удалить кассету ID=" + id + "?"))
                    try { cassetteDAO.delete(id); refreshCassette(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshCassette(); }
        });
    }

    private void wireReceiptCrud() {
        pReceipt.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                ReceiptEditDialog d = new ReceiptEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshReceipt();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                // Упрощённо — создаём новый объект Receipt с ID
                Receipt r = new Receipt();
                r.setReceiptId((int) pReceipt.getSelectedValue(0));
                ReceiptEditDialog d = new ReceiptEditDialog(MainFrame.this, r);
                d.setVisible(true); if (d.isSaved()) refreshReceipt();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pReceipt.getSelectedValue(0);
                if (confirm("Удалить квитанцию ID=" + id + "?"))
                    try { receiptDAO.delete(id); refreshReceipt(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshReceipt(); }
        });
    }

    /** Справочники: Район, Услуга, Качество, Режиссёр, Студия, Страна */
    private void wireSimpleCrud() {
        // District
        pDistrict.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Добавить район", null,
                        name -> { try { simpleDAO.insertDistrict(name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshDistrict();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pDistrict.getSelectedValue(0);
                String cur = (String) pDistrict.getSelectedValue(1);
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Редактировать район", cur,
                        name -> { try { simpleDAO.updateDistrict(id, name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshDistrict();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pDistrict.getSelectedValue(0);
                if (confirm("Удалить район ID=" + id + "?"))
                    try { simpleDAO.deleteDistrict(id); refreshDistrict(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshDistrict(); }
        });

        // Service
        pService.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Добавить услугу", null,
                        name -> { try { simpleDAO.insertService(name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshService();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pService.getSelectedValue(0);
                String cur = (String) pService.getSelectedValue(1);
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Редактировать услугу", cur,
                        name -> { try { simpleDAO.updateService(id, name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshService();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pService.getSelectedValue(0);
                if (confirm("Удалить услугу ID=" + id + "?"))
                    try { simpleDAO.deleteService(id); refreshService(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshService(); }
        });

        // Quality
        pQuality.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Добавить качество", null,
                        name -> { try { simpleDAO.insertQuality(name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshQuality();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pQuality.getSelectedValue(0);
                String cur = (String) pQuality.getSelectedValue(1);
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Редактировать качество", cur,
                        name -> { try { simpleDAO.updateQuality(id, name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshQuality();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pQuality.getSelectedValue(0);
                if (confirm("Удалить качество?"))
                    try { simpleDAO.deleteQuality(id); refreshQuality(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshQuality(); }
        });

        // Director
        pDirector.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                DirectorEditDialog d = new DirectorEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshDirector();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                try {
                    int id = (int) pDirector.getSelectedValue(0);
                    Director dir = simpleDAO.getDirectorList().stream().filter(x -> x.getDirectorId()==id).findFirst().orElse(null);
                    DirectorEditDialog d = new DirectorEditDialog(MainFrame.this, dir);
                    d.setVisible(true); if (d.isSaved()) refreshDirector();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pDirector.getSelectedValue(0);
                if (confirm("Удалить режиссёра?"))
                    try { simpleDAO.deleteDirector(id); refreshDirector(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshDirector(); }
        });

        // Studio / Country — упрощённо
        pStudio.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() { JOptionPane.showMessageDialog(MainFrame.this, "Добавление студии — через диалог StudioEditDialog (расширение)"); }
            @Override public void onEdit(int row) { JOptionPane.showMessageDialog(MainFrame.this, "Редактирование студии — расширение"); }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pStudio.getSelectedValue(0);
                if (confirm("Удалить студию?"))
                    try { simpleDAO.deleteStudio(id); refreshStudio(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshStudio(); }
        });

        pCountry.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Добавить страну", null,
                        name -> { try { simpleDAO.insertCountry(name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshCountry();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pCountry.getSelectedValue(0);
                String cur = (String) pCountry.getSelectedValue(1);
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, "Редактировать страну", cur,
                        name -> { try { simpleDAO.updateCountry(id, name); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refreshCountry();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = (int) pCountry.getSelectedValue(0);
                if (confirm("Удалить страну?"))
                    try { simpleDAO.deleteCountry(id); refreshCountry(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshCountry(); }
        });
    }

    // ======================== Refresh ========================

    private void refreshOwners()  { try { pOwners.loadData(ownerDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVideo()   { try { pVideo.loadData(videoDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshFilm()    { try { pFilm.loadData(filmDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshCassette(){ try { pCassette.loadData(cassetteDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshReceipt() { try { pReceipt.loadData(receiptDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshDistrict(){ try { pDistrict.loadData(simpleDAO.getDistricts()); } catch (SQLException e) { dbErr(e); } }
    private void refreshService() { try { pService.loadData(simpleDAO.getServices()); } catch (SQLException e) { dbErr(e); } }
    private void refreshQuality() { try { pQuality.loadData(simpleDAO.getQualities()); } catch (SQLException e) { dbErr(e); } }
    private void refreshDirector(){ try { pDirector.loadData(simpleDAO.getDirectors()); } catch (SQLException e) { dbErr(e); } }
    private void refreshStudio()  { try { pStudio.loadData(simpleDAO.getStudios()); } catch (SQLException e) { dbErr(e); } }
    private void refreshCountry() { try { pCountry.loadData(simpleDAO.getCountries()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwFilms()  { try { pVwFilms.loadData(filmDAO.getFilmsFullView()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwRevenue(){ try { pVwRevenue.loadData(receiptDAO.getTotalRevenueView()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwPeople() { try { pVwPeople.loadData(ownerDAO.getAll()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwCateg()  { try { pVwCateg.loadData(receiptDAO.getReceiptCategoryView()); } catch (SQLException e) { dbErr(e); } }

    // ======================== Утилиты ========================

    private void checkConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                try { DatabaseConnection.getConnection(); return true; }
                catch (Exception e) { return false; }
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        statusBar.setText(" ✅ Подключено к PostgreSQL (localhost:5432/videorental)");
                        statusBar.setForeground(new Color(0, 120, 0));
                    } else {
                        statusBar.setText(" ❌ Нет подключения к БД — проверьте настройки в DatabaseConnection.java");
                        statusBar.setForeground(Color.RED);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void noSel() {
        JOptionPane.showMessageDialog(this, "Выберите строку в таблице", "Предупреждение", JOptionPane.WARNING_MESSAGE);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Подтверждение",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void dbErr(SQLException e) {
        JOptionPane.showMessageDialog(this, "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // ======================== MAIN ========================

    public static void main(String[] args) {
        // Установить системный Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}