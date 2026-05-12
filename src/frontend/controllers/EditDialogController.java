package frontend.controllers;
import backend.api.TableAPI;
import backend.data.model.Dto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Универсальный контроллер диалога добавления/редактирования.
 * Динамически строит форму на основе выбранной таблицы.
 * Инициализируется через метод initialize() (не FXML).
 */
public class EditDialogController {

    @FXML private Label     lblDialogTitle;
    @FXML private GridPane  fieldsGrid;
    @FXML private Label     lblDialogError;
    @FXML private Button    btnSave;

    private TableAPI tableAPI;
    private String   tableName;
    private Map<String, Object> existingData;   // null = INSERT mode
    private Runnable onSuccess;

    /** Map fieldId → control (TextField / CheckBox / DatePicker / ComboBox) */
    private final Map<String, Control> controls = new LinkedHashMap<>();

    // ─────────────────────────────────────────
    //  Public init (called from MainController)
    // ─────────────────────────────────────────
    public void initialize(String tableName, Map<String, Object> existingData,
                           TableAPI tableAPI, Runnable onSuccess) {
        this.tableName    = tableName;
        this.existingData = existingData;
        this.tableAPI     = tableAPI;
        this.onSuccess    = onSuccess;

        boolean isEdit = existingData != null;
        lblDialogTitle.setText(isEdit
                ? "Редактировать: " + tableLabel(tableName)
                : "Добавить: "      + tableLabel(tableName));

        buildForm(tableName, existingData);
    }

    // ─────────────────────────────────────────
    //  Form builder
    // ─────────────────────────────────────────
    private void buildForm(String table, Map<String, Object> data) {
        fieldsGrid.getChildren().clear();
        controls.clear();
        int row = 0;

        switch (table) {

            case "Владельцы (Owners)" -> {
                row = addText(row, "familia",   "Фамилия *",   val(data, "Фамилия", ""));
                row = addText(row, "name",      "Имя *",       val(data, "Имя", ""));
                row = addText(row, "otchestvo", "Отчество *",  val(data, "Отчество", ""));
            }
            case "Районы (Districts)" -> {
                row = addText(row, "districtName", "Название района *", val(data, "Район", ""));
            }
            case "Качество (Quality)" -> {
                row = addText(row, "qualityName", "Название качества *", val(data, "Качество", ""));
            }
            case "Услуги (Service)" -> {
                row = addText(row, "serviceName", "Название услуги *", val(data, "Услуга", ""));
            }
            case "Режиссёры (Director)" -> {
                row = addText(row, "familia",   "Фамилия *",  val(data, "Фамилия", ""));
                row = addText(row, "name",      "Имя *",      val(data, "Имя", ""));
                row = addText(row, "otchestvo", "Отчество *", val(data, "Отчество", ""));
            }
            case "Страны (Country)" -> {
                row = addText(row, "countryName", "Название страны *", val(data, "Страна", ""));
            }
            case "Студии (Studio)" -> {
                row = addText(row, "studioName", "Название студии *",  val(data, "Студия", ""));
                row = addText(row, "countryId",  "ID страны (число) *", val(data, "countryId", "1"));
            }
            case "Фильмы (Film)" -> {
                row = addText(row, "caption",     "Название фильма *", val(data, "Название", ""));
                row = addText(row, "year",        "Год (4 цифры) *",   val(data, "Год", "2024"));
                row = addText(row, "duration",    "Длительность (мин)", val(data, "Длительность", "90"));
                row = addText(row, "information", "Описание",           val(data, "Описание", ""));
                row = addText(row, "directorId",  "ID режиссёра *",     val(data, "directorId", "1"));
                row = addText(row, "studioId",    "ID студии *",        val(data, "studioId", "1"));
            }
            case "Видеосалоны (Video)" -> {
                row = addText(row, "caption",    "Название *",       val(data, "Название", ""));
                row = addText(row, "districtId", "ID района *",      val(data, "districtId", "1"));
                row = addText(row, "address",    "Адрес *",          val(data, "Адрес", ""));
                row = addText(row, "type",       "Тип",              val(data, "Тип", ""));
                row = addText(row, "phone",      "Телефон (+7XXXXXXXXXX)", val(data, "Телефон", "+7"));
                row = addText(row, "licence",    "Лицензия (ЛИЦ-XXXXXX)", val(data, "Лицензия", "ЛИЦ-"));
                row = addText(row, "timeStart",  "Время открытия (0-23)",  val(data, "Время начала", "9"));
                row = addText(row, "timeEnd",    "Время закрытия (0-23)",  val(data, "Время конца", "21"));
                row = addText(row, "amount",     "Количество кассет",      val(data, "Кол-во", "0"));
                row = addText(row, "ownerId",    "ID владельца *",         val(data, "ownerId", "1"));
            }
            case "Кассеты (Cassette)" -> {
                row = addText(row,  "filmId",    "ID фильма *",    val(data, "filmId", "1"));
                row = addText(row,  "videoId",   "ID видеосалона *", val(data, "videoId", "1"));
                row = addText(row,  "qualityId", "ID качества *",  val(data, "qualityId", "1"));
                row = addCheck(row, "demand",    "В спросе",       boolVal(data, "Спрос"));
            }
            case "Квитанции (Receipt)" -> {
                row = addText(row, "cassetteId", "ID кассеты *",      val(data, "cassetteId", "1"));
                row = addText(row, "videoId",    "ID видеосалона *",   val(data, "videoId", "1"));
                row = addText(row, "serviceId",  "ID услуги *",        val(data, "serviceId", "1"));
                row = addDate(row, "date",       "Дата *",             val(data, "Дата", LocalDate.now().toString()));
                row = addText(row, "price",      "Цена (> 0) *",       val(data, "Цена", "100"));
            }
            default -> {
                addLabel(row, "⚠ Таблица не поддерживается для редактирования");
                btnSave.setDisable(true);
            }
        }
    }

    // ─────────────────────────────────────────
    //  Field builders
    // ─────────────────────────────────────────
    private int addText(int row, String id, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#475569;");
        TextField tf = new TextField(value);
        tf.setPrefWidth(340);
        tf.setStyle("-fx-background-color:#f8fafc; -fx-border-color:#cbd5e1; -fx-border-radius:6; -fx-background-radius:6; -fx-padding:7 10;");
        fieldsGrid.add(lbl, 0, row);
        fieldsGrid.add(tf, 1, row);
        controls.put(id, tf);
        return row + 1;
    }

    private int addCheck(int row, String id, String label, boolean value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#475569;");
        CheckBox cb = new CheckBox();
        cb.setSelected(value);
        fieldsGrid.add(lbl, 0, row);
        fieldsGrid.add(cb, 1, row);
        controls.put(id, cb);
        return row + 1;
    }

    private int addDate(int row, String id, String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#475569;");
        DatePicker dp = new DatePicker();
        try { dp.setValue(LocalDate.parse(value)); } catch (Exception e) { dp.setValue(LocalDate.now()); }
        dp.setPrefWidth(340);
        fieldsGrid.add(lbl, 0, row);
        fieldsGrid.add(dp, 1, row);
        controls.put(id, dp);
        return row + 1;
    }

    private void addLabel(int row, String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:#64748b; -fx-font-size:13px;");
        fieldsGrid.add(lbl, 0, row, 2, 1);
    }

    // ─────────────────────────────────────────
    //  FXML Actions
    // ─────────────────────────────────────────
    @FXML private void onSave() {
        lblDialogError.setText("");
        try {
            performSave();
            onSuccess.run();
            close();
        } catch (SecurityException se) {
            lblDialogError.setText("🔒 " + se.getMessage());
        } catch (Exception ex) {
            lblDialogError.setText("⚠ " + ex.getMessage());
        }
    }

    @FXML private void onCancel() { close(); }

    // ─────────────────────────────────────────
    //  Save dispatch
    // ─────────────────────────────────────────
    private void performSave() throws Exception {
        boolean isEdit = existingData != null;

        switch (tableName) {

            case "Владельцы (Owners)" -> {
                String f = text("familia"), n = text("name"), o = text("otchestvo");
                requireNonEmpty(f, "Фамилия"); requireNonEmpty(n, "Имя"); requireNonEmpty(o, "Отчество");
                if (isEdit) tableAPI.updateOwner(idFrom("OwnerID"), f, n, o);
                else        tableAPI.createOwner(f, n, o);
            }
            case "Районы (Districts)" -> {
                String name = text("districtName"); requireNonEmpty(name, "Название района");
                if (isEdit) tableAPI.updateDistrict(idFrom("DistrictID"), name);
                else        tableAPI.createDistrict(name);
            }
            case "Качество (Quality)" -> {
                String name = text("qualityName"); requireNonEmpty(name, "Название качества");
                if (isEdit) tableAPI.updateQuality(idFrom("QualityID"), name);
                else        tableAPI.createQuality(name);
            }
            case "Услуги (Service)" -> {
                String name = text("serviceName"); requireNonEmpty(name, "Название услуги");
                if (isEdit) tableAPI.updateService(idFrom("ServiceID"), name);
                else        tableAPI.createService(name);
            }
            case "Режиссёры (Director)" -> {
                String f = text("familia"), n = text("name"), o = text("otchestvo");
                requireNonEmpty(f, "Фамилия");
                if (isEdit) tableAPI.updateDirector(idFrom("DirectorID"), f, n, o);
                else        tableAPI.createDirector(f, n, o);
            }
            case "Страны (Country)" -> {
                String name = text("countryName"); requireNonEmpty(name, "Название страны");
                if (isEdit) tableAPI.updateCountry(idFrom("CountryID"), name);
                else        tableAPI.createCountry(name);
            }
            case "Студии (Studio)" -> {
                String name = text("studioName"); requireNonEmpty(name, "Название студии");
                int cid = parseInt("countryId", "ID страны");
                if (isEdit) tableAPI.updateStudio(idFrom("StudioID"), name, cid);
                else        tableAPI.createStudio(name, cid);
            }
            case "Фильмы (Film)" -> {
                String cap = text("caption"), year = text("year");
                requireNonEmpty(cap, "Название"); requireNonEmpty(year, "Год");
                int dur = parseInt("duration", "Длительность");
                String info = text("information");
                int did = parseInt("directorId", "ID режиссёра");
                int sid = parseInt("studioId", "ID студии");
                if (isEdit) tableAPI.updateFilm(idFrom("FilmID"), cap, year, dur, info, did, sid);
                else        tableAPI.createFilm(cap, year, dur, info, did, sid);
            }
            case "Видеосалоны (Video)" -> {
                String cap = text("caption"); requireNonEmpty(cap, "Название");
                int did = parseInt("districtId", "ID района");
                String addr = text("address"), type = text("type"),
                        phone = text("phone"), lic = text("licence");
                int ts = parseInt("timeStart", "Время открытия");
                int te = parseInt("timeEnd", "Время закрытия");
                int amt = parseInt("amount", "Количество");
                int oid = parseInt("ownerId", "ID владельца");
                if (isEdit) tableAPI.updateVideo(idFrom("VideoID"), cap, did, addr, type, phone, lic, ts, te, amt, oid);
                else        tableAPI.createVideo(cap, did, addr, type, phone, lic, ts, te, amt, oid);
            }
            case "Кассеты (Cassette)" -> {
                int fid = parseInt("filmId", "ID фильма");
                int vid = parseInt("videoId", "ID видеосалона");
                int qid = parseInt("qualityId", "ID качества");
                boolean demand = checked("demand");
                if (isEdit) tableAPI.updateCassette(idFrom("CassetteID"), fid, vid, qid, null, demand);
                else        tableAPI.createCassette(fid, vid, qid, null, demand);
            }
            case "Квитанции (Receipt)" -> {
                int cid  = parseInt("cassetteId", "ID кассеты");
                int vid  = parseInt("videoId", "ID видеосалона");
                int sid  = parseInt("serviceId", "ID услуги");
                LocalDate d = date("date");
                int price = parseInt("price", "Цена");
                if (price <= 0) throw new IllegalArgumentException("Цена должна быть больше 0");
                if (isEdit) tableAPI.updateReceipt(idFrom("ReceiptID"), cid, vid, sid, d, price);
                else        tableAPI.createReceipt(cid, vid, sid, d, price);
            }
        }
    }

    // ─────────────────────────────────────────
    //  Control value helpers
    // ─────────────────────────────────────────
    private String text(String id) {
        Control c = controls.get(id);
        if (c instanceof TextField tf) return tf.getText().trim();
        return "";
    }

    private boolean checked(String id) {
        Control c = controls.get(id);
        if (c instanceof CheckBox cb) return cb.isSelected();
        return false;
    }

    private LocalDate date(String id) {
        Control c = controls.get(id);
        if (c instanceof DatePicker dp && dp.getValue() != null) return dp.getValue();
        return LocalDate.now();
    }

    private int parseInt(String id, String label) {
        try { return Integer.parseInt(text(id)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException(label + " — введите число"); }
    }

    private void requireNonEmpty(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Поле «" + field + "» обязательно для заполнения");
    }

    /** Извлекает ID из существующей записи по одному из стандартных ключей */
    private int idFrom(String... keys) {
        for (String k : keys) {
            Object v = existingData.get(k);
            if (v instanceof Number n) return n.intValue();
            if (v != null) try { return Integer.parseInt(v.toString()); } catch (Exception ignored) {}
        }
        throw new IllegalStateException("Не удалось определить ID записи");
    }

    // ─────────────────────────────────────────
    //  Util
    // ─────────────────────────────────────────
    private String val(Map<String, Object> data, String key, String def) {
        if (data == null) return def;
        Object v = data.get(key);
        return v != null ? v.toString() : def;
    }

    private boolean boolVal(Map<String, Object> data, String key) {
        if (data == null) return false;
        Object v = data.get(key);
        if (v instanceof Boolean b) return b;
        if (v != null) return Boolean.parseBoolean(v.toString());
        return false;
    }

    private String tableLabel(String table) {
        int p = table.indexOf('(');
        return p > 0 ? table.substring(0, p).trim() : table;
    }

    private void close() {
        Stage stage = (Stage) btnSave.getScene().getWindow();
        stage.close();
    }
}
