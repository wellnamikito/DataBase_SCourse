package frontend.UI;

import backend.dao.*;
import backend.model.*;
import frontend.UI.Dialogs.*;
import frontend.UI.Panels.*;
import backend.util.DatabaseConnection;
import backend.util.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Главное окно приложения «Видеопрокат».
 *
 * Содержит:
 * - JMenuBar с навигацией
 * - Toolbar: тумблер темы 🌙/☀️ + переключатель языка RU/EN
 * - CardLayout для переключения панелей
 * - Строка состояния (статус БД)
 */
public class MainFrame extends JFrame {

    // DAO
    private final OwnerDAO    ownerDAO    = new OwnerDAO();
    private final VideoDAO    videoDAO    = new VideoDAO();
    private final FilmDAO     filmDAO     = new FilmDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final ReceiptDAO  receiptDAO  = new ReceiptDAO();
    private final SimpleDAO   simpleDAO   = new SimpleDAO();

    // Карточки
    private final JPanel cardPanel = new JPanel(new CardLayout());

    // Ключи карточек
    private static final String C_OWNERS   = "owners";
    private static final String C_VIDEO    = "video";
    private static final String C_FILM     = "film";
    private static final String C_CASSETTE = "cassette";
    private static final String C_RECEIPT  = "receipt";
    private static final String C_DISTRICT = "district";
    private static final String C_SERVICE  = "service";
    private static final String C_QUALITY  = "quality";
    private static final String C_DIRECTOR = "director";
    private static final String C_STUDIO   = "studio";
    private static final String C_COUNTRY  = "country";
    private static final String C_MASTER   = "master";
    private static final String C_VW_FILMS = "vw_films";
    private static final String C_VW_REV   = "vw_revenue";
    private static final String C_VW_PEOPLE= "vw_people";
    private static final String C_VW_SIMPLE= "vw_simple";
    private static final String C_VW_CATEG = "vw_category";
    private static final String C_QUERIES  = "queries";
    private static final String C_CHARTS   = "charts";
    private static final String C_REPORTS  = "reports";

    // Панели таблиц
    private TablePanel pOwners, pVideo, pFilm, pCassette, pReceipt;
    private TablePanel pDistrict, pService, pQuality, pDirector, pStudio, pCountry;
    private TablePanel pVwFilms, pVwRevenue, pVwPeople, pVwCateg;

    // Строка состояния
    private JLabel statusBar;

    // Toolbar — тема и язык
    private JToggleButton btnTheme;
    private JComboBox<String> langCombo;
    private JToolBar toolBar;

    // Меню — для обновления текстов при смене языка
    private JMenuBar menuBar;
    private JMenu menuTables, menuViews, menuQueries, menuCharts, menuReports, menuHelp;

    public MainFrame() {
        super("🎬 Видеопрокат — ИС");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        buildToolBar();
        buildMenu();
        buildCards();
        buildStatusBar();

        showCard(C_MASTER);
        checkConnection();

        // При смене языка — перестроить меню
        I18n.addListener(this::rebuildMenuTexts);
        ThemeManager.getInstance().addListener(this::applyFrameTheme);
        applyFrameTheme();
    }

    // ======================== TOOLBAR ========================

    private void buildToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                ThemeManager.borderColor()));

        toolBar.add(Box.createHorizontalGlue());

        // Тумблер темы
        btnTheme = new JToggleButton(
                ThemeManager.getInstance().isDark() ? "☀️  Светлая" : "🌙  Тёмная");
        btnTheme.setSelected(ThemeManager.getInstance().isDark());
        btnTheme.setFocusPainted(false);
        btnTheme.setBorderPainted(false);
        btnTheme.setOpaque(true);
        btnTheme.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTheme.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTheme.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        btnTheme.addActionListener(e -> {
            ThemeManager.getInstance().toggle();
            boolean dark = ThemeManager.getInstance().isDark();
            btnTheme.setText(dark ? "☀️  " + I18n.t("settings.light") : "🌙  " + I18n.t("settings.dark"));
        });
        toolBar.add(btnTheme);
        toolBar.addSeparator(new Dimension(12, 0));

        // Переключатель языка
        JLabel lblLang = new JLabel(I18n.t("settings.lang") + " ");
        lblLang.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        toolBar.add(lblLang);

        langCombo = new JComboBox<>(new String[]{"🇷🇺 Русский", "🇬🇧 English"});
        langCombo.setSelectedIndex(I18n.isRu() ? 0 : 1);
        langCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        langCombo.setPreferredSize(new Dimension(140, 28));
        langCombo.setMaximumSize(new Dimension(140, 28));
        langCombo.setFocusable(false);
        langCombo.addActionListener(e -> {
            I18n.Lang lang = langCombo.getSelectedIndex() == 0 ? I18n.Lang.RU : I18n.Lang.EN;
            I18n.setLang(lang);
            boolean dark = ThemeManager.getInstance().isDark();
            btnTheme.setText(dark ? "☀️  " + I18n.t("settings.light")
                    : "🌙  " + I18n.t("settings.dark"));
        });
        toolBar.add(langCombo);
        toolBar.addSeparator(new Dimension(10, 0));

        add(toolBar, BorderLayout.NORTH);
    }

    // ======================== МЕНЮ ========================

    private void buildMenu() {
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        menuTables  = new JMenu(I18n.t("menu.tables"));
        menuViews   = new JMenu(I18n.t("menu.views"));
        menuQueries = new JMenu(I18n.t("menu.queries"));
        menuCharts  = new JMenu(I18n.t("menu.charts"));
        menuReports = new JMenu(I18n.t("menu.reports"));
        menuHelp    = new JMenu(I18n.t("menu.help"));

        Font menuFont = new Font("Segoe UI", Font.PLAIN, 13);
        for (JMenu m : new JMenu[]{menuTables, menuViews, menuQueries, menuCharts, menuReports, menuHelp})
            m.setFont(menuFont);

        fillMenuTables();
        fillMenuViews();
        fillMenuQueries();
        fillMenuCharts();
        fillMenuReports();
        fillMenuHelp();

        menuBar.add(menuTables);
        menuBar.add(menuViews);
        menuBar.add(menuQueries);
        menuBar.add(menuCharts);
        menuBar.add(menuReports);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
    }

    private void fillMenuTables() {
        menuTables.removeAll();
        menuTables.add(mi(I18n.t("table.owners"),      () -> { showCard(C_OWNERS);   refreshOwners(); }));
        menuTables.add(mi(I18n.t("table.video"),       () -> { showCard(C_VIDEO);    refreshVideo(); }));
        menuTables.add(mi(I18n.t("table.film"),        () -> { showCard(C_FILM);     refreshFilm(); }));
        menuTables.add(mi(I18n.t("table.cassette"),    () -> { showCard(C_CASSETTE); refreshCassette(); }));
        menuTables.add(mi(I18n.t("table.receipt"),     () -> { showCard(C_RECEIPT);  refreshReceipt(); }));
        menuTables.addSeparator();
        menuTables.add(mi(I18n.t("table.districts"),   () -> { showCard(C_DISTRICT); refreshDistrict(); }));
        menuTables.add(mi(I18n.t("table.service"),     () -> { showCard(C_SERVICE);  refreshService(); }));
        menuTables.add(mi(I18n.t("table.quality"),     () -> { showCard(C_QUALITY);  refreshQuality(); }));
        menuTables.add(mi(I18n.t("table.director"),    () -> { showCard(C_DIRECTOR); refreshDirector(); }));
        menuTables.add(mi(I18n.t("table.studio"),      () -> { showCard(C_STUDIO);   refreshStudio(); }));
        menuTables.add(mi(I18n.t("table.country"),     () -> { showCard(C_COUNTRY);  refreshCountry(); }));
        menuTables.addSeparator();
        menuTables.add(mi(I18n.t("table.masterdetail"),() -> showCard(C_MASTER)));
    }

    private void fillMenuViews() {
        menuViews.removeAll();
        menuViews.add(mi(I18n.t("view.films_full"), () -> { showCard(C_VW_FILMS); refreshVwFilms(); }));
        menuViews.add(mi(I18n.t("view.revenue"),    () -> { showCard(C_VW_REV);   refreshVwRevenue(); }));
        menuViews.add(mi(I18n.t("view.people"),     () -> { showCard(C_VW_PEOPLE);refreshVwPeople(); }));
        menuViews.add(mi(I18n.t("view.category"),   () -> { showCard(C_VW_CATEG); refreshVwCateg(); }));
        menuViews.add(mi(I18n.t("view.simple"),     () -> showCard(C_VW_SIMPLE)));
    }

    private void fillMenuQueries() {
        menuQueries.removeAll();
        menuQueries.add(mi(I18n.t("menu.queries"), () -> showCard(C_QUERIES)));
    }

    private void fillMenuCharts() {
        menuCharts.removeAll();
        menuCharts.add(mi(I18n.t("menu.charts"), () -> showCard(C_CHARTS)));
    }

    private void fillMenuReports() {
        menuReports.removeAll();
        menuReports.add(mi(I18n.t("menu.reports"), () -> showCard(C_REPORTS)));
    }

    private void fillMenuHelp() {
        menuHelp.removeAll();
        menuHelp.add(mi(I18n.t("menu.about"), () ->
                JOptionPane.showMessageDialog(this,
                        I18n.t("about.text"), I18n.t("menu.about"), JOptionPane.INFORMATION_MESSAGE)));
    }

    /** Пересобрать тексты меню при смене языка */
    private void rebuildMenuTexts() {
        menuTables.setText(I18n.t("menu.tables"));
        menuViews.setText(I18n.t("menu.views"));
        menuQueries.setText(I18n.t("menu.queries"));
        menuCharts.setText(I18n.t("menu.charts"));
        menuReports.setText(I18n.t("menu.reports"));
        menuHelp.setText(I18n.t("menu.help"));
        fillMenuTables();
        fillMenuViews();
        fillMenuQueries();
        fillMenuCharts();
        fillMenuReports();
        fillMenuHelp();
        menuBar.revalidate();
        menuBar.repaint();
    }

    private JMenuItem mi(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.addActionListener(e -> action.run());
        return item;
    }

    // ======================== КАРТОЧКИ ========================

    private void buildCards() {
        pOwners   = new TablePanel("panel.owners",   true);
        pVideo    = new TablePanel("panel.video",    true);
        pFilm     = new TablePanel("panel.film",     true);
        pCassette = new TablePanel("panel.cassette", true);
        pReceipt  = new TablePanel("panel.receipt",  true);
        pDistrict = new TablePanel("panel.districts",true);
        pService  = new TablePanel("panel.service",  true);
        pQuality  = new TablePanel("panel.quality",  true);
        pDirector = new TablePanel("panel.director", true);
        pStudio   = new TablePanel("panel.studio",   true);
        pCountry  = new TablePanel("panel.country",  true);

        pVwFilms   = new TablePanel("view.films_full", false);
        pVwRevenue = new TablePanel("view.revenue",    false);
        pVwPeople  = new TablePanel("view.people",     false);
        pVwCateg   = new TablePanel("view.category",   false);

        wireOwnerCrud();
        wireVideoCrud();
        wireFilmCrud();
        wireCassetteCrud();
        wireReceiptCrud();
        wireSimpleCrud();

        MasterDetailPanel masterDetail = new MasterDetailPanel();
        ViewSimplePanel   viewSimple   = new ViewSimplePanel();
        QueryPanel        queryPanel   = new QueryPanel();
        DiagramPanel      chartPanel   = new DiagramPanel();
        ReportPanel       reportPanel  = new ReportPanel();

        cardPanel.setBackground(ThemeManager.bgPanel());
        cardPanel.add(pOwners,    C_OWNERS);
        cardPanel.add(pVideo,     C_VIDEO);
        cardPanel.add(pFilm,      C_FILM);
        cardPanel.add(pCassette,  C_CASSETTE);
        cardPanel.add(pReceipt,   C_RECEIPT);
        cardPanel.add(pDistrict,  C_DISTRICT);
        cardPanel.add(pService,   C_SERVICE);
        cardPanel.add(pQuality,   C_QUALITY);
        cardPanel.add(pDirector,  C_DIRECTOR);
        cardPanel.add(pStudio,    C_STUDIO);
        cardPanel.add(pCountry,   C_COUNTRY);
        cardPanel.add(masterDetail, C_MASTER);
        cardPanel.add(pVwFilms,   C_VW_FILMS);
        cardPanel.add(pVwRevenue, C_VW_REV);
        cardPanel.add(pVwPeople,  C_VW_PEOPLE);
        cardPanel.add(viewSimple, C_VW_SIMPLE);
        cardPanel.add(pVwCateg,   C_VW_CATEG);
        cardPanel.add(queryPanel, C_QUERIES);
        cardPanel.add(chartPanel, C_CHARTS);
        cardPanel.add(reportPanel,C_REPORTS);

        add(cardPanel, BorderLayout.CENTER);
    }

    private void buildStatusBar() {
        statusBar = new JLabel("  " + I18n.t("msg.no_conn"));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void applyFrameTheme() {
        Color bgPanel  = ThemeManager.bgPanel();
        Color bgHeader = ThemeManager.bgHeader();
        Color fgText   = ThemeManager.fgText();
        Color fgDim    = ThemeManager.fgDim();
        Color border   = ThemeManager.borderColor();
        Color bgComp   = ThemeManager.bgComponent();

        getContentPane().setBackground(bgPanel);
        cardPanel.setBackground(bgPanel);

        if (statusBar != null) {
            statusBar.setBackground(bgHeader);
            statusBar.setForeground(fgDim);
            statusBar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, border),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        }

        if (toolBar != null) {
            toolBar.setBackground(bgHeader);
            toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, border));
            for (Component c : toolBar.getComponents()) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(fgText);
                    c.setBackground(bgHeader);
                    ((JLabel) c).setOpaque(true);
                }
                if (c instanceof JComboBox) {
                    c.setBackground(bgComp);
                    c.setForeground(fgText);
                }
            }
            if (btnTheme != null) {
                boolean dark = ThemeManager.getInstance().isDark();
                btnTheme.setBackground(dark ? new Color(59, 130, 246) : new Color(245, 158, 11));
                btnTheme.setForeground(Color.WHITE);
            }
        }
        repaint();
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
                    // Получаем по индексу из full списка (pOwners показывает без ID)
                    List<Owner> list = ownerDAO.getAllList();
                    // Ищем совпадение по фамилии из выбранной строки
                    String fam = (String) pOwners.getSelectedValue(0);
                    Owner owner = list.stream()
                            .filter(o -> o.getFamilia().equals(fam)).findFirst().orElse(null);
                    OwnerEditDialog d = new OwnerEditDialog(MainFrame.this, owner);
                    d.setVisible(true);
                    if (d.isSaved()) refreshOwners();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String fam = (String) pOwners.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + fam + ")")) {
                    try {
                        List<Owner> list = ownerDAO.getAllList();
                        list.stream().filter(o -> o.getFamilia().equals(fam))
                                .findFirst().ifPresent(o -> {
                                    try { ownerDAO.delete(o.getOwnerID()); }
                                    catch (SQLException e) { dbErr(e); }
                                });
                        refreshOwners();
                    } catch (SQLException e) { dbErr(e); }
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
                // Название в первой колонке — найдём Video по caption
                String caption = (String) pVideo.getSelectedValue(0);
                try {
                    Video v = videoDAO.getAllList().stream()
                            .filter(x -> x.getCaption().equals(caption)).findFirst().orElse(null);
                    VideoEditDialog d = new VideoEditDialog(MainFrame.this, v);
                    d.setVisible(true); if (d.isSaved()) refreshVideo();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String caption = (String) pVideo.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + caption + ")")) {
                    try {
                        videoDAO.getAllList().stream()
                                .filter(x -> x.getCaption().equals(caption))
                                .findFirst().ifPresent(v -> {
                                    try { videoDAO.delete(v.getVideoId()); }
                                    catch (SQLException e) { dbErr(e); }
                                });
                        refreshVideo();
                    } catch (SQLException e) { dbErr(e); }
                }
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
                String caption = (String) pFilm.getSelectedValue(0);
                try {
                    Film f = filmDAO.getAllList().stream()
                            .filter(x -> x.getCaption().equals(caption)).findFirst().orElse(null);
                    FilmEditDialog d = new FilmEditDialog(MainFrame.this, f);
                    d.setVisible(true); if (d.isSaved()) refreshFilm();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String caption = (String) pFilm.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + caption + ")")) {
                    try {
                        filmDAO.getAllList().stream()
                                .filter(x -> x.getCaption().equals(caption))
                                .findFirst().ifPresent(f -> {
                                    try { filmDAO.delete(f.getFilmId()); }
                                    catch (SQLException e) { dbErr(e); }
                                });
                        refreshFilm();
                    } catch (SQLException e) { dbErr(e); }
                }
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
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Редактирование кассеты доступно в Master-Detail панели");
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Удаление кассеты доступно в Master-Detail панели");
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
                // Первая колонка — № Чека
                Object idVal = pReceipt.getSelectedValue(0);
                Receipt r = new Receipt();
                r.setReceiptId(((Number) idVal).intValue());
                ReceiptEditDialog d = new ReceiptEditDialog(MainFrame.this, r);
                d.setVisible(true); if (d.isSaved()) refreshReceipt();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                Object idVal = pReceipt.getSelectedValue(0);
                int id = ((Number) idVal).intValue();
                if (confirm(I18n.t("msg.confirm_delete") + " (№" + id + ")")) {
                    try { receiptDAO.delete(id); refreshReceipt(); }
                    catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshReceipt(); }
        });
    }

    private void wireSimpleCrud() {
        // Districts
        pDistrict.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.add_item"), null,
                        name -> { try { simpleDAO.insertDistrict(name); } catch (SQLException e) { dbErr(e); }});
                d.setVisible(true); if (d.isSaved()) refreshDistrict();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pDistrict.getSelectedValue(0);
                try {
                    int id = simpleDAO.getDistrictList().stream()
                            .filter(x -> x.getDistrictName().equals(cur))
                            .findFirst().map(x -> x.getDistrictId()).orElse(-1);
                    SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.edit_item"), cur,
                            name -> { try { simpleDAO.updateDistrict(id, name); } catch (SQLException e) { dbErr(e); }});
                    d.setVisible(true); if (d.isSaved()) refreshDistrict();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pDistrict.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + cur + ")")) {
                    try {
                        int id = simpleDAO.getDistrictList().stream()
                                .filter(x -> x.getDistrictName().equals(cur))
                                .findFirst().map(x -> x.getDistrictId()).orElse(-1);
                        simpleDAO.deleteDistrict(id);
                        refreshDistrict();
                    } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshDistrict(); }
        });

        // Service
        pService.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.add_item"), null,
                        name -> { try { simpleDAO.insertService(name); } catch (SQLException e) { dbErr(e); }});
                d.setVisible(true); if (d.isSaved()) refreshService();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pService.getSelectedValue(0);
                try {
                    int id = simpleDAO.getServiceList().stream()
                            .filter(x -> x.getServiceName().equals(cur))
                            .findFirst().map(x -> x.getServiceId()).orElse(-1);
                    SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.edit_item"), cur,
                            name -> { try { simpleDAO.updateService(id, name); } catch (SQLException e) { dbErr(e); }});
                    d.setVisible(true); if (d.isSaved()) refreshService();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pService.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + cur + ")")) {
                    try {
                        int id = simpleDAO.getServiceList().stream()
                                .filter(x -> x.getServiceName().equals(cur))
                                .findFirst().map(x -> x.getServiceId()).orElse(-1);
                        simpleDAO.deleteService(id);
                        refreshService();
                    } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshService(); }
        });

        // Quality
        pQuality.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.add_item"), null,
                        name -> { try { simpleDAO.insertQuality(name); } catch (SQLException e) { dbErr(e); }});
                d.setVisible(true); if (d.isSaved()) refreshQuality();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pQuality.getSelectedValue(0);
                try {
                    int id = simpleDAO.getQualityList().stream()
                            .filter(x -> x.getQualityName().equals(cur))
                            .findFirst().map(x -> x.getQualityId()).orElse(-1);
                    SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.edit_item"), cur,
                            name -> { try { simpleDAO.updateQuality(id, name); } catch (SQLException e) { dbErr(e); }});
                    d.setVisible(true); if (d.isSaved()) refreshQuality();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pQuality.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + cur + ")")) {
                    try {
                        int id = simpleDAO.getQualityList().stream()
                                .filter(x -> x.getQualityName().equals(cur))
                                .findFirst().map(x -> x.getQualityId()).orElse(-1);
                        simpleDAO.deleteQuality(id);
                        refreshQuality();
                    } catch (SQLException e) { dbErr(e); }
                }
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
                String fam = (String) pDirector.getSelectedValue(0);
                try {
                    Director dir = simpleDAO.getDirectorList().stream()
                            .filter(x -> x.getFamilia().equals(fam)).findFirst().orElse(null);
                    DirectorEditDialog d = new DirectorEditDialog(MainFrame.this, dir);
                    d.setVisible(true); if (d.isSaved()) refreshDirector();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String fam = (String) pDirector.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + fam + ")")) {
                    try {
                        simpleDAO.getDirectorList().stream()
                                .filter(x -> x.getFamilia().equals(fam))
                                .findFirst().ifPresent(d -> {
                                    try { simpleDAO.deleteDirector(d.getDirectorId()); }
                                    catch (SQLException e) { dbErr(e); }
                                });
                        refreshDirector();
                    } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshDirector(); }
        });

        // Studio — упрощённо
        pStudio.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Добавление студии:\nСначала добавьте страну в разделе 'Страны'");
            }
            @Override public void onEdit(int row) {
                JOptionPane.showMessageDialog(MainFrame.this, "Редактирование студии — в разработке");
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String name = (String) pStudio.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + name + ")")) {
                    try {
                        simpleDAO.getStudioList().stream()
                                .filter(x -> x.getStudioName().equals(name))
                                .findFirst().ifPresent(s -> {
                                    try { simpleDAO.deleteStudio(s.getStudioId()); }
                                    catch (SQLException e) { dbErr(e); }
                                });
                        refreshStudio();
                    } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshStudio(); }
        });

        // Country
        pCountry.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.add_item"), null,
                        name -> { try { simpleDAO.insertCountry(name); } catch (SQLException e) { dbErr(e); }});
                d.setVisible(true); if (d.isSaved()) refreshCountry();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pCountry.getSelectedValue(0);
                try {
                    int id = simpleDAO.getCountryList().stream()
                            .filter(x -> x.getCountryName().equals(cur))
                            .findFirst().map(x -> x.getCountryId()).orElse(-1);
                    SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.edit_item"), cur,
                            name -> { try { simpleDAO.updateCountry(id, name); } catch (SQLException e) { dbErr(e); }});
                    d.setVisible(true); if (d.isSaved()) refreshCountry();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String cur = (String) pCountry.getSelectedValue(0);
                if (confirm(I18n.t("msg.confirm_delete") + " (" + cur + ")")) {
                    try {
                        int id = simpleDAO.getCountryList().stream()
                                .filter(x -> x.getCountryName().equals(cur))
                                .findFirst().map(x -> x.getCountryId()).orElse(-1);
                        simpleDAO.deleteCountry(id);
                        refreshCountry();
                    } catch (SQLException e) { dbErr(e); }
                }
            }
            @Override public void onRefresh() { refreshCountry(); }
        });
    }

    // ======================== Refresh ========================

    private void refreshOwners()  { try { pOwners.loadData(ownerDAO.getAll()); }          catch (SQLException e) { dbErr(e); } }
    private void refreshVideo()   { try { pVideo.loadData(videoDAO.getAll()); }            catch (SQLException e) { dbErr(e); } }
    private void refreshFilm()    { try { pFilm.loadData(filmDAO.getAll()); }              catch (SQLException e) { dbErr(e); } }
    private void refreshCassette(){ try { pCassette.loadData(cassetteDAO.getAll()); }      catch (SQLException e) { dbErr(e); } }
    private void refreshReceipt() { try { pReceipt.loadData(receiptDAO.getAll()); }        catch (SQLException e) { dbErr(e); } }
    private void refreshDistrict(){ try { pDistrict.loadData(simpleDAO.getDistricts()); }  catch (SQLException e) { dbErr(e); } }
    private void refreshService() { try { pService.loadData(simpleDAO.getServices()); }    catch (SQLException e) { dbErr(e); } }
    private void refreshQuality() { try { pQuality.loadData(simpleDAO.getQualities()); }   catch (SQLException e) { dbErr(e); } }
    private void refreshDirector(){ try { pDirector.loadData(simpleDAO.getDirectors()); }  catch (SQLException e) { dbErr(e); } }
    private void refreshStudio()  { try { pStudio.loadData(simpleDAO.getStudios()); }      catch (SQLException e) { dbErr(e); } }
    private void refreshCountry() { try { pCountry.loadData(simpleDAO.getCountries()); }   catch (SQLException e) { dbErr(e); } }
    private void refreshVwFilms()  { try { pVwFilms.loadData(filmDAO.getFilmsFullView()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwRevenue(){ try { pVwRevenue.loadData(receiptDAO.getTotalRevenueView()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwPeople() { try { pVwPeople.loadData(ownerDAO.getAll()); }         catch (SQLException e) { dbErr(e); } }
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
                    boolean ok = get();
                    statusBar.setText("  " + I18n.t(ok ? "msg.connected" : "msg.no_conn"));
                    statusBar.setForeground(ok ? new Color(34, 197, 94) : new Color(239, 68, 68));
                } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void noSel() {
        JOptionPane.showMessageDialog(this,
                I18n.t("msg.select_row"), I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
    }

    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg,
                I18n.t("msg.confirm_title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void dbErr(SQLException e) {
        JOptionPane.showMessageDialog(this,
                I18n.t("msg.db_error") + "\n" + e.getMessage(),
                I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    // ======================== MAIN ========================

    public static void main(String[] args) {
        // Применить сохранённую тему до создания окна
        ThemeManager.getInstance();

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}