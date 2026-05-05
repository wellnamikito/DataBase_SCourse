package frontend.UI;

import backend.dao.*;
import backend.model.*;
import frontend.UI.Dialogs.*;
import frontend.UI.Panels.*;
import backend.util.DatabaseConnection;
import backend.util.*;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Главное окно приложения.
 * Тему полностью управляет FlatLaf; applyFrameTheme() удалён.
 */
public class MainFrame extends JFrame {

    private final OwnerDAO    ownerDAO    = new OwnerDAO();
    private final VideoDAO    videoDAO    = new VideoDAO();
    private final FilmDAO     filmDAO     = new FilmDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final ReceiptDAO  receiptDAO  = new ReceiptDAO();
    private final SimpleDAO   simpleDAO   = new SimpleDAO();

    private final JPanel cardPanel = new JPanel(new CardLayout());

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
    private static final String C_MASTER   = "masterdetail";
    private static final String C_VW_FILMS = "vw_films";
    private static final String C_VW_REV   = "vw_revenue";
    private static final String C_VW_PEOPLE= "vw_people";
    private static final String C_VW_SIMPLE= "vw_simple";
    private static final String C_VW_CATEG = "vw_category";
    private static final String C_QUERIES  = "queries";
    private static final String C_CHARTS   = "charts";
    private static final String C_REPORTS  = "reports";

    private TablePanel pOwners, pVideo, pFilm, pCassette, pReceipt;
    private TablePanel pDistrict, pService, pQuality, pDirector, pStudio, pCountry;
    private TablePanel pVwFilms, pVwRevenue, pVwPeople, pVwCateg;

    private JLabel       statusBar;
//    private JToggleButton btnTheme;
    private JComboBox<String> langCombo;
    // Храним ссылки на все компоненты toolbar для Future переиспользования (если понадобится)
    private JPanel       toolbarPanel;
    private JPanel fadePanel;
    private JLabel       lblLang;
//    private JSeparator   toolbarSep;

    private JMenuBar menuBar;
    private JMenu menuTables, menuViews, menuQueries, menuCharts, menuReports, menuHelp;

    public MainFrame() {
        super("🎬 Видеопрокат — Информационная система");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        buildToolBar();
        buildMenu();
        buildCards();
        buildStatusBar();
        initFadeLayer();

        showCard(C_MASTER);
        checkConnection();

        // ThemeManager теперь отвечает только за логический флаг isDark()
        // I18n.addListener(this::rebuildMenuTexts);
        // I18n.addListener(this::updateToolbarTexts);

        // Обновление темы через FlatLaf уже применяется в main
    }

    // ══════════════════════════════════════════════════
    //  TOOLBAR
    // ══════════════════════════════════════════════════

    private void buildToolBar() {
        toolbarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));

        lblLang = new JLabel(I18n.t("settings.lang") + " ");
        lblLang.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        langCombo = new JComboBox<>(new String[]{"🇷🇺 Русский", "🇬🇧 English"});
        langCombo.setSelectedIndex(I18n.isRu() ? 0 : 1);
        langCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        langCombo.setPreferredSize(new Dimension(140, 28));
        langCombo.setFocusable(false);

        // 🔥 смена языка + обновление UI
        langCombo.addActionListener(e -> {
            I18n.setLang(
                    langCombo.getSelectedIndex() == 0
                            ? I18n.Lang.RU
                            : I18n.Lang.EN
            );

            updateToolbarTexts();
            rebuildMenuTexts();
        });

        toolbarPanel.add(lblLang);
        toolbarPanel.add(langCombo);

        add(toolbarPanel, BorderLayout.NORTH);
    }

    private void styleToggle(JToggleButton btn) {
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
    }

    private void updateToolbarTexts() {
        lblLang.setText(I18n.t("settings.lang") + " ");
    }

    private void initFadeLayer() {
        fadePanel = new JPanel();
        fadePanel.setOpaque(true);
        fadePanel.setBackground(new Color(0, 0, 0, 0));
        fadePanel.setVisible(false);

        setGlassPane(fadePanel);
    }

    // ══════════════════════════════════════════════════
    //  ТЕМА ЧЕРЕЗ FlatLaf (переключение Dark/Light)
    // ══════════════════════════════════════════════════

//    private void toggleTheme() {
//        boolean dark = ThemeManager.getInstance().isDark();
//
//        fadeOut(() -> {
//            try {
//                UIManager.setLookAndFeel(
//                        dark ? new FlatLightLaf() : new FlatDarkLaf()
//                );
//
//                ThemeManager.getInstance().setDark(!dark);
//
//                for (Window w : Window.getWindows()) {
//                    SwingUtilities.updateComponentTreeUI(w);
//                }
//
//                updateToolbarTexts();
//
//                fadeIn();
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        });
//    }

    // ══════════════════════════════════════════════════
    //  МЕНЮ
    // ══════════════════════════════════════════════════

    private void buildMenu() {
        menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        menuTables  = new JMenu(); menuViews   = new JMenu();
        menuQueries = new JMenu(); menuCharts  = new JMenu();
        menuReports = new JMenu(); menuHelp    = new JMenu();

        Font mf = new Font("Segoe UI", Font.PLAIN, 13);
        for (JMenu m : new JMenu[]{menuTables,menuViews,menuQueries,menuCharts,menuReports,menuHelp})
            m.setFont(mf);

        fillMenuTables(); fillMenuViews(); fillMenuQueries();
        fillMenuCharts(); fillMenuReports(); fillMenuHelp();

        menuBar.add(menuTables); menuBar.add(menuViews); menuBar.add(menuQueries);
        menuBar.add(menuCharts); menuBar.add(menuReports);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);
    }

    private void fillMenuTables() {
        menuTables.setText(I18n.t("menu.tables")); menuTables.removeAll();
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
        menuViews.setText(I18n.t("menu.views")); menuViews.removeAll();
        menuViews.add(mi(I18n.t("view.films_full"), () -> { showCard(C_VW_FILMS);  refreshVwFilms(); }));
        menuViews.add(mi(I18n.t("view.revenue"),    () -> { showCard(C_VW_REV);    refreshVwRevenue(); }));
        menuViews.add(mi(I18n.t("view.people"),     () -> { showCard(C_VW_PEOPLE); refreshVwPeople(); }));
        menuViews.add(mi(I18n.t("view.category"),   () -> { showCard(C_VW_CATEG);  refreshVwCateg(); }));
        menuViews.add(mi(I18n.t("view.simple"),     () -> showCard(C_VW_SIMPLE)));
    }

    private void fillMenuQueries() {
        menuQueries.setText(I18n.t("menu.queries")); menuQueries.removeAll();
        menuQueries.add(mi(I18n.t("menu.queries"), () -> showCard(C_QUERIES)));
    }

    private void fillMenuCharts() {
        menuCharts.setText(I18n.t("menu.charts")); menuCharts.removeAll();
        menuCharts.add(mi(I18n.t("menu.charts"), () -> showCard(C_CHARTS)));
    }

    private void fillMenuReports() {
        menuReports.setText(I18n.t("menu.reports")); menuReports.removeAll();
        menuReports.add(mi(I18n.t("menu.reports"), () -> showCard(C_REPORTS)));
    }

    private void fillMenuHelp() {
        menuHelp.setText(I18n.t("menu.help")); menuHelp.removeAll();
        menuHelp.add(mi(I18n.t("menu.about"), () ->
                JOptionPane.showMessageDialog(this, I18n.t("about.text"),
                        I18n.t("menu.about"), JOptionPane.INFORMATION_MESSAGE)));
    }

    private void rebuildMenuTexts() {
        fillMenuTables(); fillMenuViews(); fillMenuQueries();
        fillMenuCharts(); fillMenuReports(); fillMenuHelp();
        menuBar.revalidate(); menuBar.repaint();

    }

    private JMenuItem mi(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.addActionListener(e -> action.run());
        return item;
    }

    // ══════════════════════════════════════════════════
    //  КАРТОЧКИ
    // ══════════════════════════════════════════════════

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

        wireOwnerCrud(); wireVideoCrud(); wireFilmCrud();
        wireCassetteCrud(); wireReceiptCrud(); wireSimpleCrud();

        MasterDetailPanel masterDetail = new MasterDetailPanel();
        ViewSimplePanel   viewSimple   = new ViewSimplePanel();
        QueryPanel        queryPanel   = new QueryPanel();
        DiagramPanel      chartPanel   = new DiagramPanel();
        ReportPanel       reportPanel  = new ReportPanel();

        cardPanel.add(pOwners,    C_OWNERS);   cardPanel.add(pVideo,     C_VIDEO);
        cardPanel.add(pFilm,      C_FILM);     cardPanel.add(pCassette,  C_CASSETTE);
        cardPanel.add(pReceipt,   C_RECEIPT);  cardPanel.add(pDistrict,  C_DISTRICT);
        cardPanel.add(pService,   C_SERVICE);  cardPanel.add(pQuality,   C_QUALITY);
        cardPanel.add(pDirector,  C_DIRECTOR); cardPanel.add(pStudio,    C_STUDIO);
        cardPanel.add(pCountry,   C_COUNTRY);  cardPanel.add(masterDetail,C_MASTER);
        cardPanel.add(pVwFilms,   C_VW_FILMS); cardPanel.add(pVwRevenue, C_VW_REV);
        cardPanel.add(pVwPeople,  C_VW_PEOPLE);cardPanel.add(viewSimple, C_VW_SIMPLE);
        cardPanel.add(pVwCateg,   C_VW_CATEG); cardPanel.add(queryPanel, C_QUERIES);
        cardPanel.add(chartPanel, C_CHARTS);   cardPanel.add(reportPanel,C_REPORTS);

        add(cardPanel, BorderLayout.CENTER);
    }

    private void buildStatusBar() {
        statusBar = new JLabel("  " + I18n.t("msg.no_conn"));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(statusBar, BorderLayout.SOUTH);
    }

    // ══════════════════════════════════════════════════
    //  ПЕРЕКРАСКА ФРЕЙМА
    // ══════════════════════════════════════════════════

    // ВСЁ ОБНОВЛЕНО ЧЕРЕЗ SwingUtilities.updateComponentTreeUI(this)
    // Ручная перекраска через setBackground(...), UI-цвета и т.п. УДАЛЕНА.
    // Если нужно — можно оставить только repaint/revalidate для локальных панелей.
    private void applyFrameTheme() {
        repaint();
        revalidate();
    }

    private void fadeOut(Runnable after) {
        fadePanel.setVisible(true);

        new Thread(() -> {
            try {
                for (int i = 0; i <= 60; i += 5) {
                    int alpha = i;
                    SwingUtilities.invokeLater(() -> {
                        fadePanel.setBackground(new Color(0, 0, 0, alpha));
                    });
                    Thread.sleep(15);
                }

                SwingUtilities.invokeLater(after);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void fadeIn() {
        new Thread(() -> {
            try {
                for (int i = 60; i >= 0; i -= 5) {
                    int alpha = i;
                    SwingUtilities.invokeLater(() -> {
                        fadePanel.setBackground(new Color(0, 0, 0, alpha));
                    });
                    Thread.sleep(15);
                }

                SwingUtilities.invokeLater(() -> fadePanel.setVisible(false));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showCard(String key) {
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, key);
    }

    // ══════════════════════════════════════════════════
    //  CRUD LISTENERS
    // ══════════════════════════════════════════════════

    private void wireOwnerCrud() {
        pOwners.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                OwnerEditDialog d = new OwnerEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshOwners();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String fam = String.valueOf(pOwners.getSelectedValue(0));
                try {
                    Owner owner = ownerDAO.getAllList().stream()
                            .filter(o -> o.getFamilia().equals(fam)).findFirst().orElse(null);
                    OwnerEditDialog d = new OwnerEditDialog(MainFrame.this, owner);
                    d.setVisible(true); if (d.isSaved()) refreshOwners();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String fam = String.valueOf(pOwners.getSelectedValue(0));
                if (confirm(I18n.t("msg.confirm_delete") + " (" + fam + ")")) {
                    try {
                        ownerDAO.getAllList().stream()
                                .filter(o -> o.getFamilia().equals(fam)).findFirst()
                                .ifPresent(o -> { try { ownerDAO.delete(o.getOwnerID()); }
                                catch (SQLException e) { dbErr(e); } });
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
                String caption = String.valueOf(pVideo.getSelectedValue(0));
                try {
                    Video v = videoDAO.getAllList().stream()
                            .filter(x -> x.getCaption().equals(caption)).findFirst().orElse(null);
                    VideoEditDialog d = new VideoEditDialog(MainFrame.this, v);
                    d.setVisible(true); if (d.isSaved()) refreshVideo();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String caption = String.valueOf(pVideo.getSelectedValue(0));
                if (confirm(I18n.t("msg.confirm_delete") + " (" + caption + ")")) {
                    try {
                        videoDAO.getAllList().stream()
                                .filter(x -> x.getCaption().equals(caption)).findFirst()
                                .ifPresent(v -> { try { videoDAO.delete(v.getVideoId()); }
                                catch (SQLException e) { dbErr(e); } });
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
                String caption = String.valueOf(pFilm.getSelectedValue(0));
                try {
                    Film f = filmDAO.getAllList().stream()
                            .filter(x -> x.getCaption().equals(caption)).findFirst().orElse(null);
                    FilmEditDialog d = new FilmEditDialog(MainFrame.this, f);
                    d.setVisible(true); if (d.isSaved()) refreshFilm();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String caption = String.valueOf(pFilm.getSelectedValue(0));
                if (confirm(I18n.t("msg.confirm_delete") + " (" + caption + ")")) {
                    try {
                        filmDAO.getAllList().stream()
                                .filter(x -> x.getCaption().equals(caption)).findFirst()
                                .ifPresent(f -> { try { filmDAO.delete(f.getFilmId()); }
                                catch (SQLException e) { dbErr(e); } });
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
                JOptionPane.showMessageDialog(MainFrame.this, "Используйте Master-Detail панель");
            }
            @Override public void onDelete(int row) {
                JOptionPane.showMessageDialog(MainFrame.this, "Используйте Master-Detail панель");
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
                Object idVal = pReceipt.getSelectedValue(0);
                Receipt r = new Receipt();
                r.setReceiptId(((Number) idVal).intValue());
                ReceiptEditDialog d = new ReceiptEditDialog(MainFrame.this, r);
                d.setVisible(true); if (d.isSaved()) refreshReceipt();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                int id = ((Number) pReceipt.getSelectedValue(0)).intValue();
                if (confirm(I18n.t("msg.confirm_delete") + " (№" + id + ")"))
                    try { receiptDAO.delete(id); refreshReceipt(); }
                    catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshReceipt(); }
        });
    }

    private void wireSimpleCrud() {
        wireSingle(pDistrict,
                n -> simpleDAO.insertDistrict(n),
                (id,n) -> simpleDAO.updateDistrict(id,n),
                id -> simpleDAO.deleteDistrict(id),
                () -> simpleDAO.getDistrictList().stream()
                        .map(d -> new Object[]{d.getDistrictId(), d.getDistrictName()})
                        .collect(java.util.stream.Collectors.toList()),
                this::refreshDistrict);

        wireSingle(pService,
                n -> simpleDAO.insertService(n),
                (id,n) -> simpleDAO.updateService(id,n),
                id -> simpleDAO.deleteService(id),
                () -> simpleDAO.getServiceList().stream()
                        .map(s -> new Object[]{s.getServiceId(), s.getServiceName()})
                        .collect(java.util.stream.Collectors.toList()),
                this::refreshService);

        wireSingle(pQuality,
                n -> simpleDAO.insertQuality(n),
                (id,n) -> simpleDAO.updateQuality(id,n),
                id -> simpleDAO.deleteQuality(id),
                () -> simpleDAO.getQualityList().stream()
                        .map(q -> new Object[]{q.getQualityId(), q.getQualityName()})
                        .collect(java.util.stream.Collectors.toList()),
                this::refreshQuality);

        wireSingle(pCountry,
                n -> simpleDAO.insertCountry(n),
                (id,n) -> simpleDAO.updateCountry(id,n),
                id -> simpleDAO.deleteCountry(id),
                () -> simpleDAO.getCountryList().stream()
                        .map(c -> new Object[]{c.getCountryId(), c.getCountryName()})
                        .collect(java.util.stream.Collectors.toList()),
                this::refreshCountry);

        pDirector.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                DirectorEditDialog d = new DirectorEditDialog(MainFrame.this, null);
                d.setVisible(true); if (d.isSaved()) refreshDirector();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                String fam = String.valueOf(pDirector.getSelectedValue(0));
                try {
                    Director dir = simpleDAO.getDirectorList().stream()
                            .filter(x -> x.getFamilia().equals(fam)).findFirst().orElse(null);
                    DirectorEditDialog d = new DirectorEditDialog(MainFrame.this, dir);
                    d.setVisible(true); if (d.isSaved()) refreshDirector();
                } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String fam = String.valueOf(pDirector.getSelectedValue(0));
                if (confirm(I18n.t("msg.confirm_delete")))
                    try {
                        simpleDAO.getDirectorList().stream()
                                .filter(x -> x.getFamilia().equals(fam)).findFirst()
                                .ifPresent(d -> { try { simpleDAO.deleteDirector(d.getDirectorId()); }
                                catch (SQLException e) { dbErr(e); } });
                        refreshDirector();
                    } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshDirector(); }
        });

        pStudio.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                JOptionPane.showMessageDialog(MainFrame.this, "Добавьте страну сначала");
            }
            @Override public void onEdit(int row) { }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                String name = String.valueOf(pStudio.getSelectedValue(0));
                if (confirm(I18n.t("msg.confirm_delete") + " (" + name + ")"))
                    try {
                        simpleDAO.getStudioList().stream()
                                .filter(x -> x.getStudioName().equals(name)).findFirst()
                                .ifPresent(s -> { try { simpleDAO.deleteStudio(s.getStudioId()); }
                                catch (SQLException e) { dbErr(e); } });
                        refreshStudio();
                    } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refreshStudio(); }
        });
    }

    @FunctionalInterface interface SC1 { void run(String s) throws SQLException; }
    @FunctionalInterface interface SC2 { void run(int id, String s) throws SQLException; }
    @FunctionalInterface interface SC3 { void run(int id) throws SQLException; }
    @FunctionalInterface interface SS  { List<Object[]> get() throws SQLException; }

    private void wireSingle(TablePanel p, SC1 ins, SC2 upd, SC3 del, SS list, Runnable refresh) {
        p.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.add_item"), null,
                        n -> { try { ins.run(n); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refresh.run();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { noSel(); return; }
                Object idObj  = p.getSelectedValue(0);
                Object nameObj= p.getSelectedValue(1);
                if (idObj == null) return;
                int id = ((Number) idObj).intValue();
                String cur = nameObj != null ? nameObj.toString() : "";
                SimpleEditDialog d = new SimpleEditDialog(MainFrame.this, I18n.t("dlg.edit_item"), cur,
                        n -> { try { upd.run(id, n); } catch (SQLException e) { dbErr(e); } });
                d.setVisible(true); if (d.isSaved()) refresh.run();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { noSel(); return; }
                Object idObj = p.getSelectedValue(0);
                if (idObj == null) return;
                int id = ((Number) idObj).intValue();
                if (confirm(I18n.t("msg.confirm_delete")))
                    try { del.run(id); refresh.run(); } catch (SQLException e) { dbErr(e); }
            }
            @Override public void onRefresh() { refresh.run(); }
        });
    }

    // ══════════════════════════════════════════════════
    //  REFRESH
    // ══════════════════════════════════════════════════

    private void refreshOwners()   { try { pOwners.loadData(ownerDAO.getAll()); }           catch (SQLException e) { dbErr(e); } }
    private void refreshVideo()    { try { pVideo.loadData(videoDAO.getAll()); }             catch (SQLException e) { dbErr(e); } }
    private void refreshFilm()     { try { pFilm.loadData(filmDAO.getAll()); }               catch (SQLException e) { dbErr(e); } }
    private void refreshCassette() { try { pCassette.loadData(cassetteDAO.getAll()); }       catch (SQLException e) { dbErr(e); } }
    private void refreshReceipt()  { try { pReceipt.loadData(receiptDAO.getAll()); }         catch (SQLException e) { dbErr(e); } }
    private void refreshDistrict() { try { pDistrict.loadData(simpleDAO.getDistricts()); }   catch (SQLException e) { dbErr(e); } }
    private void refreshService()  { try { pService.loadData(simpleDAO.getServices()); }     catch (SQLException e) { dbErr(e); } }
    private void refreshQuality()  { try { pQuality.loadData(simpleDAO.getQualities()); }    catch (SQLException e) { dbErr(e); } }
    private void refreshDirector() { try { pDirector.loadData(simpleDAO.getDirectors()); }   catch (SQLException e) { dbErr(e); } }
    private void refreshStudio()   { try { pStudio.loadData(simpleDAO.getStudios()); }       catch (SQLException e) { dbErr(e); } }
    private void refreshCountry()  { try { pCountry.loadData(simpleDAO.getCountries()); }    catch (SQLException e) { dbErr(e); } }
    private void refreshVwFilms()  { try { pVwFilms.loadData(filmDAO.getFilmsFullView()); }  catch (SQLException e) { dbErr(e); } }
    private void refreshVwRevenue(){ try { pVwRevenue.loadData(receiptDAO.getTotalRevenueView()); } catch (SQLException e) { dbErr(e); } }
    private void refreshVwPeople() { try { pVwPeople.loadData(ownerDAO.getAll()); }          catch (SQLException e) { dbErr(e); } }
    private void refreshVwCateg()  { try { pVwCateg.loadData(receiptDAO.getReceiptCategoryView()); } catch (SQLException e) { dbErr(e); } }

    // ══════════════════════════════════════════════════
    //  УТИЛИТЫ
    // ══════════════════════════════════════════════════

    private void checkConnection() {
        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                try { DatabaseConnection.getConnection(); return true; }
                catch (Exception e) { return false; }
            }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    statusBar.setText("  " + I18n.t(ok ? "msg.connected" : "msg.no_conn"));
                    statusBar.setForeground(ok ? new Color(34,197,94) : new Color(239,68,68));
                } catch (Exception e) { e.printStackTrace(); }
            }
        }.execute();
    }

    private void noSel() {
        JOptionPane.showMessageDialog(this, I18n.t("msg.select_row"),
                I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
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

    public static void main(String[] args) {
        try {
            // Фиксируем единый серый FlatLaf (тёмная тема)
            FlatDarkLaf.setup();

            // Улучшаем вид title bar (если используется FlatLaf decoration)
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            UIManager.put("TitlePane.unifiedBackground", true);
            UIManager.put("TitlePane.background", new Color(60, 63, 65));
            UIManager.put("TitlePane.foreground", new Color(220, 220, 220));
            UIManager.put("TitlePane.centerTitle", true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}