package frontend.controllers;

import backend.MainApp;
import backend.api.*;
import backend.security.Session;
import backend.service.AuthService;
import backend.data.model.Dto;
import frontend.ui.AlertHelper;
import frontend.ui.TableHelper;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Главный контроллер — управляет навигацией и отображением всех страниц
 */
public class MainController implements Initializable {

    // ════════ FXML References ════════
    @FXML private Label lblRoleBadge;
    @FXML private Label lblUsername;
    @FXML private Label lblDbName;
    @FXML private Label lblPageTitle;
    @FXML private Label lblStatus;
    @FXML private Label lblTime;

    // Кнопки навигации
    @FXML private Button btnTables, btnViews, btnQueries, btnCharts, btnReports, btnSources, btnAudit;

    // Pages
    @FXML private StackPane contentStack;
    @FXML private VBox pageTables, pageViews, pageQueries, pageCharts, pageReports, pageSources, pageAudit;

    // Tables page
    @FXML private ComboBox<String> cmbTableSelect;
    @FXML private TableView<Map<String, Object>> tableView;
    @FXML private Button btnAdd, btnEdit, btnDelete;

    // Views page
    @FXML private ComboBox<String> cmbViewSelect;
    @FXML private TableView<Map<String, Object>> viewTable;

    // Queries page
    @FXML private ComboBox<String> cmbQuerySelect;
    @FXML private TableView<Map<String, Object>> queryTable;
    @FXML private VBox queryParamsPanel;

    // Charts page
    @FXML private ComboBox<String> cmbChartSelect;
    @FXML private StackPane chartContainer;

    // Reports page
    @FXML private TableView<Map<String, Object>> reportTable;

    // Audit page
    @FXML private TableView<Map<String, Object>> auditTable;

    // ════════ APIs ════════
    private final TableAPI  tableAPI  = new TableAPI();
    private final ViewAPI   viewAPI   = new ViewAPI();
    private final QueryAPI  queryAPI  = new QueryAPI();
    private final ChartAPI  chartAPI  = new ChartAPI();
    private final ReportAPI reportAPI = new ReportAPI();

    // State
    private String currentTable = null;
    private List<Map<String, Object>> currentReportData = null;
    private String currentReportTitle = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSessionInfo();
        setupTableCombo();
        setupViewCombo();
        setupQueryCombo();
        setupChartCombo();
        setupClock();
        showTables(); // Начальная страница
    }

    // ════════════════════════════════
    //   SESSION INFO
    // ════════════════════════════════
    private void setupSessionInfo() {
        Session session = Session.getInstance();
        lblUsername.setText(session.getUsername());
        lblDbName.setText(session.getDbName());

        if (session.isAdmin()) {
            lblRoleBadge.setText("ADMIN");
            lblRoleBadge.setStyle("-fx-background-color: #e74c3c;");
        } else {
            lblRoleBadge.setText("USER");
            lblRoleBadge.setStyle("-fx-background-color: #2ecc71;");
        }
    }

    private void setupClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e ->
                        lblTime.setText(LocalDateTime.now().format(fmt))
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
        lblTime.setText(LocalDateTime.now().format(fmt));
    }

    // ════════════════════════════════
    //   NAVIGATION
    // ════════════════════════════════
    private void showPage(VBox page, String title, Button activeBtn) {
        // Скрываем все страницы
        for (Node n : contentStack.getChildren()) n.setVisible(false);
        page.setVisible(true);
        lblPageTitle.setText(title);

        // Снимаем активный стиль со всех кнопок
        for (Button b : List.of(btnTables, btnViews, btnQueries, btnCharts, btnReports, btnSources, btnAudit)) {
            b.getStyleClass().remove("nav-btn-active");
        }
        activeBtn.getStyleClass().add("nav-btn-active");
    }

    @FXML private void showTables()  { showPage(pageTables,  "Таблицы",          btnTables); }
    @FXML private void showViews()   { showPage(pageViews,   "Представления",    btnViews); }
    @FXML private void showQueries() { showPage(pageQueries, "Запросы",           btnQueries); }
    @FXML private void showCharts()  { showPage(pageCharts,  "Диаграммы",         btnCharts); }
    @FXML private void showReports() { showPage(pageReports, "Отчёты",            btnReports); }
    @FXML private void showSources() { showPage(pageSources, "Источники данных",  btnSources); }
    @FXML private void showAudit()   {
        showPage(pageAudit, "Журнал аудита", btnAudit);
        onRefreshAudit();
    }

    // ════════════════════════════════
    //   TABLES PAGE
    // ════════════════════════════════
    private void setupTableCombo() {
        cmbTableSelect.setItems(FXCollections.observableArrayList(
                "Владельцы (Owners)", "Районы (Districts)", "Качество (Quality)",
                "Услуги (Service)", "Режиссёры (Director)", "Страны (Country)",
                "Студии (Studio)", "Фильмы (Film)", "Видеосалоны (Video)",
                "Кассеты (Cassette)", "Квитанции (Receipt)"
        ));

        // Admin-only кнопки
        boolean isAdmin = Session.getInstance().isAdmin();
        btnAdd.setVisible(isAdmin);
        btnEdit.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);
    }

    @FXML private void onTableSelected() {
        currentTable = cmbTableSelect.getValue();
        if (currentTable == null) return;
        loadCurrentTable();
    }

    private void loadCurrentTable() {
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = getTableData(currentTable);
                Platform.runLater(() -> {
                    TableHelper.populate(tableView, data);
                    setStatus("✓ Загружено записей: " + data.size(), true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    setStatus("✗ Ошибка: " + ex.getMessage(), false);
                    AlertHelper.error("Ошибка загрузки", ex.getMessage());
                });
            }
        });
    }

    private List<Map<String, Object>> getTableData(String table) throws Exception {
        return switch (table) {
            case "Владельцы (Owners)"     -> toMapList(tableAPI.getOwners());
            case "Районы (Districts)"     -> toMapList(tableAPI.getDistricts());
            case "Качество (Quality)"     -> toMapList(tableAPI.getQualities());
            case "Услуги (Service)"       -> toMapList(tableAPI.getServices());
            case "Режиссёры (Director)"   -> toMapList(tableAPI.getDirectors());
            case "Страны (Country)"       -> toMapList(tableAPI.getCountries());
            case "Студии (Studio)"        -> toMapList(tableAPI.getStudios());
            case "Фильмы (Film)"          -> toMapList(tableAPI.getFilms());
            case "Видеосалоны (Video)"    -> toMapList(tableAPI.getVideos());
            case "Кассеты (Cassette)"     -> toMapList(tableAPI.getCassettes());
            case "Квитанции (Receipt)"    -> toMapList(tableAPI.getReceipts());
            default -> Collections.emptyList();
        };
    }

    @FXML private void onRefresh() {
        if (currentTable != null) loadCurrentTable();
    }

    @FXML private void onAdd() {
        openEditDialog(null);
    }

    @FXML private void onEdit() {
        Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.info("Выбор записи", "Выберите запись для редактирования");
            return;
        }
        openEditDialog(selected);
    }

    @FXML private void onDelete() {
        Map<String, Object> selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.info("Выбор записи", "Выберите запись для удаления");
            return;
        }
        if (!AlertHelper.confirm("Удаление записи", "Вы уверены, что хотите удалить выбранную запись?")) return;

        runAsync(() -> {
            try {
                deleteRecord(currentTable, selected);
                Platform.runLater(() -> {
                    setStatus("✓ Запись удалена", true);
                    loadCurrentTable();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    setStatus("✗ Ошибка удаления", false);
                    AlertHelper.error("Ошибка удаления", ex.getMessage());
                });
            }
        });
    }

    private void deleteRecord(String table, Map<String, Object> row) throws Exception {
        switch (table) {
            case "Владельцы (Owners)"   -> tableAPI.deleteOwner(toInt(row, "OwnerID"));
            case "Районы (Districts)"   -> tableAPI.deleteDistrict(toInt(row, "DistrictID"));
            case "Качество (Quality)"   -> tableAPI.deleteQuality(toInt(row, "QualityID"));
            case "Услуги (Service)"     -> tableAPI.deleteService(toInt(row, "ServiceID"));
            case "Режиссёры (Director)" -> tableAPI.deleteDirector(toInt(row, "DirectorID"));
            case "Страны (Country)"     -> tableAPI.deleteCountry(toInt(row, "CountryID"));
            case "Студии (Studio)"      -> tableAPI.deleteStudio(toInt(row, "StudioID"));
            case "Фильмы (Film)"        -> tableAPI.deleteFilm(toInt(row, "FilmID"));
            case "Видеосалоны (Video)"  -> tableAPI.deleteVideo(toInt(row, "VideoID"));
            case "Кассеты (Cassette)"   -> tableAPI.deleteCassette(toInt(row, "CassetteID"));
            case "Квитанции (Receipt)"  -> tableAPI.deleteReceipt(toInt(row, "ReceiptID"));
        }
    }

    private void openEditDialog(Map<String, Object> existingData) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/videorental/views/EditDialog.fxml")
            );
            Parent root = loader.load();
            EditDialogController ctrl = loader.getController();
            ctrl.initialize(currentTable, existingData, tableAPI, () -> {
                Platform.runLater(this::loadCurrentTable);
            });
            Stage stage = new Stage();
            stage.setTitle(existingData == null ? "Добавить запись" : "Редактировать запись");
            stage.setScene(new Scene(root, 500, 450));
            stage.getScene().getStylesheets().add(
                    getClass().getResource("/com/videorental/styles/app.css").toExternalForm()
            );
            stage.initOwner(MainApp.getPrimaryStage());
            stage.showAndWait();
        } catch (Exception e) {
            AlertHelper.error("Ошибка", "Не удалось открыть диалог: " + e.getMessage());
        }
    }

    // ════════════════════════════════
    //   VIEWS PAGE
    // ════════════════════════════════
    private void setupViewCombo() {
        cmbViewSelect.setItems(FXCollections.observableArrayList(
                "Фильмы с режиссёрами и студиями",
                "Кассеты с фильмами и видеосалонами",
                "Квитанции с услугами и видеосалонами",
                "Видеосалоны и кассеты",
                "Общая и арендная выручка",
                "Все люди (владельцы и режиссёры)",
                "Категории квитанций",
                "Использованные кассеты",
                "Неиспользованные кассеты",
                "Видеосалоны без операций",
                "Фильмы выше среднего кол-ва кассет",
                "Лучшие и худшие видеосалоны",
                "Разница max/min выручки",
                "Процент ночных видеотек",
                "Среднее кол-во клиентов"
        ));
    }

    @FXML private void onViewSelected() {
        String view = cmbViewSelect.getValue();
        if (view == null) return;
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = loadView(view);
                Platform.runLater(() -> {
                    TableHelper.populate(viewTable, data);
                    setStatus("✓ Загружено строк: " + data.size(), true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка VIEW", ex.getMessage()));
            }
        });
    }

    private List<Map<String, Object>> loadView(String view) throws Exception {
        return switch (view) {
            case "Фильмы с режиссёрами и студиями"        -> viewAPI.filmsFull();
            case "Кассеты с фильмами и видеосалонами"     -> viewAPI.cassettesFull();
            case "Квитанции с услугами и видеосалонами"   -> viewAPI.receiptsServices();
            case "Видеосалоны и кассеты"                  -> viewAPI.videoWithCassettes();
            case "Общая и арендная выручка"               -> viewAPI.totalRevenue();
            case "Все люди (владельцы и режиссёры)"       -> viewAPI.people();
            case "Категории квитанций"                    -> viewAPI.receiptCategory();
            case "Использованные кассеты"                 -> viewAPI.usedCassettes();
            case "Неиспользованные кассеты"               -> viewAPI.unusedCassettes();
            case "Видеосалоны без операций"               -> viewAPI.videosWithoutReceipts();
            case "Фильмы выше среднего кол-ва кассет"     -> viewAPI.filmsAboveAverage();
            case "Лучшие и худшие видеосалоны"            -> viewAPI.bestWorstVideos();
            case "Разница max/min выручки"                -> viewAPI.revenueDifference();
            case "Процент ночных видеотек"                -> viewAPI.nightVideoPercent();
            case "Среднее кол-во клиентов"                -> viewAPI.avgClients();
            default -> Collections.emptyList();
        };
    }

    @FXML private void onRefreshView() { onViewSelected(); }

    // ════════════════════════════════
    //   QUERIES PAGE
    // ════════════════════════════════
    private void setupQueryCombo() {
        cmbQuerySelect.setItems(FXCollections.observableArrayList(
                "Видеосалоны по фамилии владельца",
                "Кассеты по качеству",
                "Квитанции по услуге и периоду",
                "Операции начиная с даты",
                "Выручка видеосалонов выше суммы",
                "Выручка за период дат",
                "Видеосалоны по маске названия",
                "Квитанции по точной цене",
                "Квитанции выше цены",
                "Студии по году и мин. выручке",
                "Статистика по услуге и году"
        ));
    }

    @FXML private void onQuerySelected() {
        buildQueryParams(cmbQuerySelect.getValue());
    }

    private void buildQueryParams(String query) {
        queryParamsPanel.getChildren().clear();
        if (query == null) return;

        HBox row = new HBox(12);
        row.setStyle("-fx-alignment: CENTER_LEFT;");

        switch (query) {
            case "Видеосалоны по фамилии владельца" -> {
                row.getChildren().addAll(paramLabel("Фамилия:"), paramField("familia", "Иванов"));
            }
            case "Кассеты по качеству" -> {
                row.getChildren().addAll(paramLabel("Качество:"), paramField("quality", "4K"));
            }
            case "Квитанции по услуге и периоду" -> {
                row.getChildren().addAll(
                        paramLabel("Услуга:"), paramField("service", "Аренда"),
                        paramLabel("Дата с:"), paramField("dateFrom", "2023-01-01"),
                        paramLabel("Дата по:"), paramField("dateTo", "2023-12-31")
                );
            }
            case "Операции начиная с даты" -> {
                row.getChildren().addAll(
                        paramLabel("Услуга:"), paramField("service", "Аренда"),
                        paramLabel("Дата с:"), paramField("dateFrom", "2023-01-01")
                );
            }
            case "Выручка видеосалонов выше суммы" -> {
                row.getChildren().addAll(paramLabel("Мин. выручка:"), paramField("minRevenue", "1000"));
            }
            case "Выручка за период дат" -> {
                row.getChildren().addAll(
                        paramLabel("Дата с:"), paramField("dateFrom", "2023-01-01"),
                        paramLabel("Дата по:"), paramField("dateTo", "2023-12-31")
                );
            }
            case "Видеосалоны по маске названия" -> {
                row.getChildren().addAll(paramLabel("Маска (%):"), paramField("mask", "Видео%"));
            }
            case "Квитанции по точной цене" -> {
                row.getChildren().addAll(paramLabel("Цена:"), paramField("price", "1000"));
            }
            case "Квитанции выше цены" -> {
                row.getChildren().addAll(paramLabel("Мин. цена:"), paramField("price", "500"));
            }
            case "Студии по году и мин. выручке" -> {
                row.getChildren().addAll(
                        paramLabel("Год:"), paramField("year", "2023"),
                        paramLabel("Мин. выручка:"), paramField("minRevenue", "1000")
                );
            }
            case "Статистика по услуге и году" -> {
                row.getChildren().addAll(
                        paramLabel("Услуга:"), paramField("service", "Аренда"),
                        paramLabel("Год:"), paramField("year", "2023")
                );
            }
        }
        queryParamsPanel.getChildren().add(row);
    }

    private Label paramLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");
        return l;
    }

    private TextField paramField(String id, String placeholder) {
        TextField tf = new TextField();
        tf.setId(id);
        tf.setPromptText(placeholder);
        tf.setPrefWidth(140);
        tf.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 6; -fx-padding: 6;");
        return tf;
    }

    @FXML private void onRunQuery() {
        String query = cmbQuerySelect.getValue();
        if (query == null) {
            AlertHelper.info("Выбор запроса", "Выберите запрос из списка");
            return;
        }

        Map<String, String> params = collectParams();

        runAsync(() -> {
            try {
                List<Map<String, Object>> data = executeQuery(query, params);
                Platform.runLater(() -> {
                    TableHelper.populate(queryTable, data);
                    setStatus("✓ Результат: " + data.size() + " строк", true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    setStatus("✗ Ошибка выполнения", false);
                    AlertHelper.error("Ошибка запроса", ex.getMessage());
                });
            }
        });
    }

    private Map<String, String> collectParams() {
        Map<String, String> params = new HashMap<>();
        for (Node n : queryParamsPanel.lookupAll(".text-input")) {
            if (n instanceof TextField tf && tf.getId() != null) {
                params.put(tf.getId(), tf.getText().trim());
            }
        }
        return params;
    }

    private List<Map<String, Object>> executeQuery(String query, Map<String, String> p) throws Exception {
        return switch (query) {
            case "Видеосалоны по фамилии владельца" ->
                    queryAPI.videosByOwner(p.get("familia"));
            case "Кассеты по качеству" ->
                    queryAPI.cassettesByQuality(p.get("quality"));
            case "Квитанции по услуге и периоду" ->
                    queryAPI.receiptsByServicePeriod(p.get("service"),
                            parseDate(p.get("dateFrom")), parseDate(p.get("dateTo")));
            case "Операции начиная с даты" ->
                    queryAPI.operationsFromDate(p.get("service"), parseDate(p.get("dateFrom")));
            case "Выручка видеосалонов выше суммы" ->
                    queryAPI.videoRevenueOver(parseInt(p.get("minRevenue")));
            case "Выручка за период дат" ->
                    queryAPI.revenueByPeriod(parseDate(p.get("dateFrom")), parseDate(p.get("dateTo")));
            case "Видеосалоны по маске названия" ->
                    queryAPI.videosByMask(p.get("mask"));
            case "Квитанции по точной цене" ->
                    queryAPI.receiptsByPrice(parseInt(p.get("price")));
            case "Квитанции выше цены" ->
                    queryAPI.receiptsPriceOver(parseInt(p.get("price")));
            case "Студии по году и мин. выручке" ->
                    queryAPI.studiosByYearRevenue(p.get("year"), parseInt(p.get("minRevenue")));
            case "Статистика по услуге и году" ->
                    queryAPI.serviceStats(p.get("service"), parseInt(p.get("year")));
            default -> Collections.emptyList();
        };
    }

    // ════════════════════════════════
    //   CHARTS PAGE
    // ════════════════════════════════
    private void setupChartCombo() {
        cmbChartSelect.setItems(FXCollections.observableArrayList(
                "Выручка по видеосалонам (PieChart)",
                "Кассеты по качеству (BarChart)",
                "Выручка по месяцам (BarChart)",
                "Кассеты по спросу (PieChart)"
        ));
    }

    @FXML private void onChartSelected() {
        String chart = cmbChartSelect.getValue();
        if (chart == null) return;
        runAsync(() -> {
            try {
                Chart chartNode = buildChart(chart);
                Platform.runLater(() -> {
                    chartContainer.getChildren().clear();
                    chartContainer.getChildren().add(chartNode);
                    StackPane.setMargin(chartNode, new javafx.geometry.Insets(16));
                });
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка диаграммы", ex.getMessage()));
            }
        });
    }

    @FXML private void onRefreshChart() { onChartSelected(); }

    private Chart buildChart(String type) throws Exception {
        return switch (type) {
            case "Выручка по видеосалонам (PieChart)" -> {
                PieChart pie = new PieChart();
                pie.setTitle("Выручка по видеосалонам");
                List<Map<String, Object>> data = chartAPI.revenueByVideo();
                for (Map<String, Object> row : data) {
                    String label = str(row, "Caption");
                    double val = toDouble(row, "total");
                    if (val > 0) pie.getData().add(new PieChart.Data(label, val));
                }
                pie.setLegendVisible(true);
                yield pie;
            }
            case "Кассеты по качеству (BarChart)" -> {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Качество");
                yAxis.setLabel("Количество кассет");
                BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
                bar.setTitle("Кассеты по качеству");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Кассеты");
                for (Map<String, Object> row : chartAPI.cassettesByQuality()) {
                    series.getData().add(new XYChart.Data<>(str(row, "QualityName"), toDouble(row, "cnt")));
                }
                bar.getData().add(series);
                yield bar;
            }
            case "Выручка по месяцам (BarChart)" -> {
                CategoryAxis xAxis = new CategoryAxis();
                NumberAxis yAxis = new NumberAxis();
                xAxis.setLabel("Месяц");
                yAxis.setLabel("Выручка");
                BarChart<String, Number> bar = new BarChart<>(xAxis, yAxis);
                bar.setTitle("Выручка по месяцам");
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Выручка");
                List<Map<String, Object>> rev = chartAPI.revenueByMonth();
                Collections.reverse(rev); // Хронологический порядок
                for (Map<String, Object> row : rev) {
                    series.getData().add(new XYChart.Data<>(str(row, "month"), toDouble(row, "total")));
                }
                bar.getData().add(series);
                yield bar;
            }
            case "Кассеты по спросу (PieChart)" -> {
                PieChart pie = new PieChart();
                pie.setTitle("Кассеты по спросу");
                for (Map<String, Object> row : chartAPI.cassettesByDemand()) {
                    pie.getData().add(new PieChart.Data(str(row, "label"), toDouble(row, "cnt")));
                }
                yield pie;
            }
            default -> new PieChart();
        };
    }

    // ════════════════════════════════
    //   REPORTS PAGE
    // ════════════════════════════════
    @FXML private void viewReport1() { loadReport("Владельцы видеопрокатов", "1"); }
    @FXML private void viewReport2() { loadReport("Фильмы с полной информацией", "2"); }
    @FXML private void viewReport3() { loadReport("Выручка по видеосалонам", "3"); }

    private void loadReport(String title, String id) {
        currentReportTitle = title;
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = switch (id) {
                    case "1" -> reportAPI.reportOwners();
                    case "2" -> reportAPI.reportFilmsFull();
                    case "3" -> reportAPI.reportRevenue();
                    default  -> Collections.emptyList();
                };
                currentReportData = data;
                Platform.runLater(() -> {
                    TableHelper.populate(reportTable, data);
                    setStatus("✓ Отчёт загружен: " + data.size() + " строк", true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка отчёта", ex.getMessage()));
            }
        });
    }

    @FXML private void exportReport1Excel() { exportExcel("Владельцы видеопрокатов", "1"); }
    @FXML private void exportReport1Txt()   { exportTxt("Владельцы видеопрокатов", "1"); }
    @FXML private void exportReport1Html()  { exportHtml("Владельцы видеопрокатов", "1"); }
    @FXML private void exportReport2Excel() { exportExcel("Фильмы с полной информацией", "2"); }
    @FXML private void exportReport2Txt()   { exportTxt("Фильмы с полной информацией", "2"); }
    @FXML private void exportReport2Html()  { exportHtml("Фильмы с полной информацией", "2"); }
    @FXML private void exportReport3Excel() { exportExcel("Выручка по видеосалонам", "3"); }
    @FXML private void exportReport3Txt()   { exportTxt("Выручка по видеосалонам", "3"); }
    @FXML private void exportReport3Html()  { exportHtml("Выручка по видеосалонам", "3"); }

    private void exportExcel(String title, String id) {
        File saveFile = AlertHelper.showSaveDialog(MainApp.getPrimaryStage(),
                "Сохранить Excel", "Excel файлы (*.xlsx)", "*.xlsx");
        if (saveFile == null) return;
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = getReportData(id);
                File tmp = reportAPI.exportToExcel(title, data);
                Files.copy(tmp.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Platform.runLater(() -> {
                    setStatus("✓ Экспортировано в Excel", true);
                    AlertHelper.info("Экспорт завершён", "Файл сохранён: " + saveFile.getAbsolutePath());
                });
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка экспорта", ex.getMessage()));
            }
        });
    }

    private void exportTxt(String title, String id) {
        File saveFile = AlertHelper.showSaveDialog(MainApp.getPrimaryStage(),
                "Сохранить TXT", "Текстовые файлы (*.txt)", "*.txt");
        if (saveFile == null) return;
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = getReportData(id);
                File tmp = reportAPI.exportToTxt(title, data);
                Files.copy(tmp.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Platform.runLater(() -> setStatus("✓ Экспортировано в TXT", true));
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка экспорта", ex.getMessage()));
            }
        });
    }

    private void exportHtml(String title, String id) {
        File saveFile = AlertHelper.showSaveDialog(MainApp.getPrimaryStage(),
                "Сохранить HTML", "HTML файлы (*.html)", "*.html");
        if (saveFile == null) return;
        runAsync(() -> {
            try {
                List<Map<String, Object>> data = getReportData(id);
                File tmp = reportAPI.exportToHtml(title, data);
                Files.copy(tmp.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Platform.runLater(() -> {
                    setStatus("✓ Экспортировано в HTML", true);
                    openInBrowser(saveFile);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка экспорта", ex.getMessage()));
            }
        });
    }

    private List<Map<String, Object>> getReportData(String id) throws Exception {
        return switch (id) {
            case "1" -> reportAPI.reportOwners();
            case "2" -> reportAPI.reportFilmsFull();
            case "3" -> reportAPI.reportRevenue();
            default  -> Collections.emptyList();
        };
    }

    // ════════════════════════════════
    //   SOURCES PAGE
    // ════════════════════════════════
    @FXML private void openLink(javafx.event.ActionEvent e) {
        if (e.getSource() instanceof Hyperlink hl) {
            String url = (String) hl.getUserData();
            openInBrowser(url);
        }
    }

    private void openInBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            AlertHelper.error("Ошибка", "Не удалось открыть ссылку: " + url);
        }
    }

    private void openInBrowser(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception ex) {
            AlertHelper.info("Файл сохранён", file.getAbsolutePath());
        }
    }

    // ════════════════════════════════
    //   AUDIT PAGE
    // ════════════════════════════════
    @FXML private void onRefreshAudit() {
        runAsync(() -> {
            try {
                List<Dto.AuditLog> logs = tableAPI.getAuditLog();
                List<Map<String, Object>> data = toMapList(logs);
                Platform.runLater(() -> TableHelper.populate(auditTable, data));
            } catch (Exception ex) {
                Platform.runLater(() -> AlertHelper.error("Ошибка аудита", ex.getMessage()));
            }
        });
    }

    // ════════════════════════════════
    //   LOGOUT
    // ════════════════════════════════
    @FXML private void onLogout() {
        if (AlertHelper.confirm("Выход из системы", "Вы действительно хотите выйти?")) {
            new AuthService().logout();
            try {
                MainApp.showLoginScene();
            } catch (Exception ex) {
                AlertHelper.error("Ошибка", ex.getMessage());
            }
        }
    }

    // ════════════════════════════════
    //   UTILITIES
    // ════════════════════════════════
    private void runAsync(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void setStatus(String msg, boolean ok) {
        lblStatus.setText(msg);
        lblStatus.setStyle(ok
                ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private int toInt(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v != null) return Integer.parseInt(v.toString());
        return 0;
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private java.time.LocalDate parseDate(String s) {
        try { return java.time.LocalDate.parse(s); } catch (Exception e) {
            return java.time.LocalDate.now();
        }
    }

    private String str(Map<String, Object> row, String key) {
        Object v = row.get(key);
        return v != null ? v.toString() : "";
    }

    private double toDouble(Map<String, Object> row, String key) {
        Object v = row.get(key);
        if (v instanceof Number n) return n.doubleValue();
        if (v != null) {
            try { return Double.parseDouble(v.toString()); } catch (Exception e) {}
        }
        return 0;
    }

    // Generic DTO → Map conversion for TableHelper
    private <T> List<Map<String, Object>> toMapList(List<T> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (T item : items) {
            result.add(dtoToMap(item));
        }
        return result;
    }

    private Map<String, Object> dtoToMap(Object dto) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (java.lang.reflect.Method m : dto.getClass().getMethods()) {
            String name = m.getName();
            // Only getXxx() methods (no getId, no getClass, etc.)
            if (name.startsWith("get") && !name.equals("getClass") && m.getParameterCount() == 0) {
                // Skip ID fields
                String field = name.substring(3);
                if (field.endsWith("Id") || field.endsWith("ID") || field.equals("Photo")) continue;
                try {
                    Object val = m.invoke(dto);
                    map.put(formatFieldName(field), val);
                } catch (Exception ignored) {}
            }
        }
        return map;
    }

    private String formatFieldName(String name) {
        // CamelCase → readable label
        return switch (name) {
            case "Familia"    -> "Фамилия";
            case "Name"       -> "Имя";
            case "Otchestvo"  -> "Отчество";
            case "Caption"    -> "Название";
            case "Address"    -> "Адрес";
            case "Phone"      -> "Телефон";
            case "Licence"    -> "Лицензия";
            case "TimeStart"  -> "Время начала";
            case "TimeEnd"    -> "Время конца";
            case "Amount"     -> "Кол-во";
            case "Year"       -> "Год";
            case "Duration"   -> "Длительность";
            case "Information"-> "Описание";
            case "Demand"     -> "Спрос";
            case "Date"       -> "Дата";
            case "Price"      -> "Цена";
            case "TableName"  -> "Таблица";
            case "OperationType"-> "Операция";
            case "OperationDate"-> "Дата операции";
            case "DistrictName"-> "Район";
            case "QualityName" -> "Качество";
            case "ServiceName" -> "Услуга";
            case "CountryName" -> "Страна";
            case "StudioName"  -> "Студия";
            case "DirectorName"-> "Режиссёр";
            case "OwnerName"   -> "Владелец";
            case "FilmCaption" -> "Фильм";
            case "VideoCaption"-> "Видеосалон";
            case "Type"        -> "Тип";
            default -> name;
        };
    }
}
