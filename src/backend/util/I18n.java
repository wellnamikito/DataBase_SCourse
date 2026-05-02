package backend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * Интернационализация (i18n) — RU / EN.
 * Использование: I18n.t("btn.add")
 * Смена языка: I18n.setLang(I18n.Lang.EN)
 * Слушатель: I18n.addListener(() -> updateUI())
 */
public class I18n {

    public enum Lang { RU, EN }

    private static Lang current;
    private static final List<Runnable> listeners = new ArrayList<>();
    private static final Map<String, String[]> str = new HashMap<>();
    private static final Preferences prefs = Preferences.userNodeForPackage(I18n.class);
    private static final String PREF_LANG = "app_lang";

    static {
        // Загрузить сохранённый язык
        String saved = prefs.get(PREF_LANG, "RU");
        current = saved.equals("EN") ? Lang.EN : Lang.RU;

        // Формат: ключ -> [RU, EN]

        // === Меню ===
        p("menu.tables",        "📂 Таблицы",           "📂 Tables");
        p("menu.views",         "👁️ Представления",     "👁️ Views");
        p("menu.queries",       "🔍 Запросы",           "🔍 Queries");
        p("menu.charts",        "📊 Диаграммы",         "📊 Charts");
        p("menu.reports",       "📋 Отчёты",            "📋 Reports");
        p("menu.help",          "❓ Справка",            "❓ Help");
        p("menu.about",         "О программе",          "About");

        // === Таблицы (меню) ===
        p("table.owners",       "👥 Владельцы",         "👥 Owners");
        p("table.video",        "🏪 Видеосалоны",       "🏪 Video Stores");
        p("table.film",         "🎬 Фильмы",            "🎬 Films");
        p("table.cassette",     "📼 Кассеты",           "📼 Cassettes");
        p("table.receipt",      "🧾 Квитанции",         "🧾 Receipts");
        p("table.districts",    "📍 Районы",            "📍 Districts");
        p("table.service",      "🔧 Услуги",            "🔧 Services");
        p("table.quality",      "⭐ Качество",           "⭐ Quality");
        p("table.director",     "🎭 Режиссёры",         "🎭 Directors");
        p("table.studio",       "🏭 Студии",            "🏭 Studios");
        p("table.country",      "🌍 Страны",            "🌍 Countries");
        p("table.masterdetail", "🔗 Видеосалон + Кассеты", "🔗 Store + Cassettes");

        // === Представления (меню) ===
        p("view.films_full",    "vw_films_full",                    "vw_films_full");
        p("view.revenue",       "vw_total_revenue",                 "vw_total_revenue");
        p("view.people",        "vw_people",                        "vw_people");
        p("view.category",      "vw_receipt_category",              "vw_receipt_category");
        p("view.simple",        "✏️ vw_video_simple (обновляемое)", "✏️ vw_video_simple (editable)");

        // === Кнопки ===
        p("btn.add",            "➕  Добавить",          "➕  Add");
        p("btn.edit",           "✏️  Редактировать",    "✏️  Edit");
        p("btn.delete",         "🗑️  Удалить",          "🗑️  Delete");
        p("btn.refresh",        "🔄  Обновить",         "🔄  Refresh");
        p("btn.save",           "💾 Сохранить",         "💾 Save");
        p("btn.cancel",         "Отмена",               "Cancel");
        p("btn.run",            "▶  Выполнить",         "▶  Run");
        p("btn.export",         "📥 Экспорт в Excel",   "📥 Export to Excel");
        p("btn.refresh_charts", "🔄 Обновить",          "🔄 Refresh");
        p("btn.run_func",       "▶ Выполнить",          "▶ Run");
        p("btn.edit_view",      "✏️ Редактировать через VIEW", "✏️ Edit via VIEW");
        p("btn.clear_search",   "✕",                    "✕");

        // === Поиск ===
        p("search.label",       "🔍 Поиск:",            "🔍 Search:");
        p("search.tooltip",     "Введите текст для фильтрации", "Type to filter");

        // === Панели — заголовки ===
        p("panel.owners",       "Владельцы",            "Owners");
        p("panel.video",        "Видеосалоны",          "Video Stores");
        p("panel.film",         "Фильмы",               "Films");
        p("panel.cassette",     "Кассеты",              "Cassettes");
        p("panel.receipt",      "Квитанции",            "Receipts");
        p("panel.districts",    "Районы",               "Districts");
        p("panel.service",      "Услуги",               "Services");
        p("panel.quality",      "Качество",             "Quality");
        p("panel.director",     "Режиссёры",            "Directors");
        p("panel.studio",       "Студии",               "Studios");
        p("panel.country",      "Страны",               "Countries");
        p("panel.master",       "📼 Видеосалоны (Master)", "📼 Video Stores (Master)");
        p("panel.detail",       "🎞️ Кассеты выбранного видеосалона", "🎞️ Cassettes of Selected Store");

        // === Запросы ===
        p("query.select_lbl",   "Выберите запрос:",     "Select query:");
        p("query.params_title", "Параметры запроса",    "Query Parameters");
        p("query.no_params",    "  (нет параметров)",   "  (no parameters)");
        p("query.result_title", "Результат",            "Result");
        p("query.rows",         "✅ Строк: ",            "✅ Rows: ");
        p("query.error",        "❌ Ошибка: ",          "❌ Error: ");

        // === Отчёты ===
        p("report.title",       "📋 Отчёты",            "📋 Reports");
        p("report.single",      "👤 Однотабличный",     "👤 Single Table");
        p("report.single_sub",  "(Владельцы)",          "(Owners)");
        p("report.multi",       "🎞️ Многотабличный",   "🎞️ Multi-Table");
        p("report.multi_sub",   "(Кассеты)",            "(Cassettes)");
        p("report.aggr",        "💰 Агрегированный",    "💰 Aggregate");
        p("report.aggr_sub",    "(Выручка)",            "(Revenue)");
        p("report.func_title",  "🔧 Функция: get_service_stats", "🔧 Function: get_service_stats");
        p("report.svc_lbl",     "Услуга:",              "Service:");
        p("report.year_lbl",    "Год:",                 "Year:");
        p("report.data_border", "Данные отчёта",        "Report Data");
        p("report.rows",        "✅ Строк: ",            "✅ Rows: ");
        p("report.no_data",     "Сначала загрузите отчёт", "Load a report first");

        // === Диаграммы ===
        p("chart.title",        "📊 Диаграммы",         "📊 Charts");
        p("chart.tab_pie",      "🥧 Категории цен",     "🥧 Price Categories");
        p("chart.tab_bar",      "📊 Выручка (Bar)",     "📊 Revenue (Bar)");
        p("chart.tab_3d",       "📦 Выручка (3D)",      "📦 Revenue (3D)");
        p("chart.pie_title",    "Квитанции по категориям цен", "Receipts by Price Category");
        p("chart.bar_title",    "Выручка по видеосалонам",     "Revenue by Video Store");
        p("chart.3d_title",     "Выручка по видеосалонам (3D)","Revenue by Video Store (3D)");
        p("chart.axis_store",   "Видеосалон",           "Store");
        p("chart.axis_sum",     "Сумма (руб.)",         "Amount (RUB)");
        p("chart.series",       "Выручка",              "Revenue");

        // === VIEW simple ===
        p("view.simple.info",
                "<html><b>📝 Обновляемое представление vw_video_simple</b><br>" +
                        "<i>Изменения записываются через триггер trg_update_video в таблицу Video</i></html>",
                "<html><b>📝 Updatable view vw_video_simple</b><br>" +
                        "<i>Changes go via trigger trg_update_video to the Video table</i></html>");
        p("view.simple.saved",
                "✅ Обновлено через VIEW (триггер выполнен)",
                "✅ Updated via VIEW (trigger fired)");

        // === Настройки ===
        p("settings.theme",     "Тема:",                "Theme:");
        p("settings.dark",      "🌙 Тёмная",           "🌙 Dark");
        p("settings.light",     "☀️ Светлая",          "☀️ Light");
        p("settings.lang",      "Язык:",                "Language:");

        // === Сообщения ===
        p("msg.select_row",     "Выберите строку в таблице",   "Please select a row");
        p("msg.select_video",   "Сначала выберите видеосалон", "Select a video store first");
        p("msg.confirm_delete", "Удалить выбранную запись?",   "Delete selected record?");
        p("msg.confirm_title",  "Подтверждение",               "Confirm");
        p("msg.db_error",       "Ошибка БД:",                  "Database error:");
        p("msg.error",          "Ошибка",                      "Error");
        p("msg.warning",        "Предупреждение",              "Warning");
        p("msg.connected",      "✅ Подключено к PostgreSQL (localhost:5432/videorental)",
                "✅ Connected to PostgreSQL (localhost:5432/videorental)");
        p("msg.no_conn",        "❌ Нет подключения к БД — проверьте DatabaseConnection.java",
                "❌ No database connection — check DatabaseConnection.java");

        // === Валидация ===
        p("val.fam_required",   "Фамилия и Имя обязательны",  "Last name and first name are required");
        p("val.fio_chars",
                "ФИО содержит недопустимые символы!\nТолько буквы, дефис и пробел.",
                "Name contains invalid characters!\nOnly letters, hyphen and space.");
        p("val.phone",
                "Телефон должен быть в формате +7XXXXXXXXXX\nНапример: +79001234567",
                "Phone must be in format +7XXXXXXXXXX\nExample: +79001234567");
        p("val.licence",
                "Лицензия должна быть в формате ЛИЦ-XXXXXX\nНапример: ЛИЦ-123456",
                "Licence must be in format ЛИЦ-XXXXXX\nExample: ЛИЦ-123456");
        p("val.name_required",  "Название обязательно",        "Name is required");
        p("val.empty",          "Поле не может быть пустым",   "Field cannot be empty");
        p("val.year_num",       "Год должен быть числом",      "Year must be a number");

        // === Поля форм ===
        p("f.familia",    "Фамилия:",                 "Last Name:");
        p("f.name",       "Имя:",                     "First Name:");
        p("f.otchestvo",  "Отчество:",                "Middle Name:");
        p("f.caption",    "Название:",                "Name:");
        p("f.district",   "Район:",                   "District:");
        p("f.address",    "Адрес:",                   "Address:");
        p("f.type",       "Тип:",                     "Type:");
        p("f.phone",      "Телефон (+7XXXXXXXXXX):",  "Phone (+7XXXXXXXXXX):");
        p("f.licence",    "Лицензия (ЛИЦ-XXXXX):",   "Licence (ЛИЦ-XXXXX):");
        p("f.time_start", "Время открытия:",          "Opening Time:");
        p("f.time_end",   "Время закрытия:",          "Closing Time:");
        p("f.amount",     "Кол-во клиентов:",         "Client Count:");
        p("f.owner",      "Владелец:",                "Owner:");
        p("f.film",       "Фильм:",                   "Film:");
        p("f.quality",    "Качество:",                "Quality:");
        p("f.demand",     "Спрос (Demand)",           "Demand");
        p("f.year",       "Год:",                     "Year:");
        p("f.duration",   "Длительность (мин):",      "Duration (min):");
        p("f.info",       "Описание:",                "Description:");
        p("f.studio",     "Студия:",                  "Studio:");
        p("f.cassette_id","Кассета ID:",              "Cassette ID:");
        p("f.video",      "Видеосалон:",              "Video Store:");
        p("f.service",    "Услуга:",                  "Service:");
        p("f.date",       "Дата (YYYY-MM-DD):",       "Date (YYYY-MM-DD):");
        p("f.price",      "Цена:",                    "Price:");
        p("f.name_lbl",   "Название:",                "Name:");
        p("f.country",    "Страна:",                  "Country:");
        p("f.director",   "Режиссёр:",                "Director:");

        // === Заголовки колонок таблиц (без двоеточия) ===
        p("col.receipt_id",     "№ Чека",           "Receipt #");
        p("col.cassette_id",    "ID Кассеты",       "Cassette ID");
        p("col.total_revenue",  "Общая выручка",    "Total Revenue");
        p("col.rent_revenue",   "Выручка (аренда)", "Rental Revenue");
        p("col.price_category", "Категория цены",   "Price Category");
        p("col.district_name",  "Название района",  "District Name");
        p("col.service_name",   "Название услуги",  "Service Name");

        // Поля без двоеточия (для заголовков таблиц)
        p("f.familia",    "Фамилия",   "Last Name");
        p("f.name",       "Имя",       "First Name");
        p("f.otchestvo",  "Отчество",  "Middle Name");
        p("f.caption",    "Название",  "Name");
        p("f.district",   "Район",     "District");
        p("f.address",    "Адрес",     "Address");
        p("f.type",       "Тип",       "Type");
        p("f.phone",      "Телефон",   "Phone");
        p("f.licence",    "Лицензия",  "Licence");
        p("f.time_start", "Откр.",     "Opens");
        p("f.time_end",   "Закр.",     "Closes");
        p("f.amount",     "Клиентов",  "Clients");
        p("f.owner",      "Владелец",  "Owner");
        p("f.film",       "Фильм",     "Film");
        p("f.quality",    "Качество",  "Quality");
        p("f.demand",     "Спрос",     "Demand");
        p("f.year",       "Год",       "Year");
        p("f.duration",   "Длит.(мин)","Duration(min)");
        p("f.info",       "Описание",  "Description");
        p("f.studio",     "Студия",    "Studio");
        p("f.service",    "Услуга",    "Service");
        p("f.date",       "Дата",      "Date");
        p("f.price",      "Цена (руб.)","Price (RUB)");
        p("f.video",      "Видеосалон","Video Store");
        p("f.country",    "Страна",    "Country");
        p("f.director",   "Режиссёр",  "Director");

        // === Excel ===
        p("excel.save_as",  "Сохранить отчёт как...", "Save report as...");
        p("excel.saved",    "Файл сохранён:\n",       "File saved:\n");
        p("excel.err",      "Ошибка экспорта:\n",     "Export error:\n");
        p("excel.title",    "Экспорт Excel",          "Excel Export");

        // === О программе ===
        p("about.text",
                "<html><b>Видеопрокат — ИС</b><br>Архитектура: MVC + DAO<br>" +
                        "СУБД: PostgreSQL<br>UI: Java Swing<br>Charts: JFreeChart<br>Export: Apache POI</html>",
                "<html><b>Video Rental — IS</b><br>Architecture: MVC + DAO<br>" +
                        "DB: PostgreSQL<br>UI: Java Swing<br>Charts: JFreeChart<br>Export: Apache POI</html>");

        // === Диалоги ===
        p("dlg.add_owner",    "Добавить владельца",        "Add Owner");
        p("dlg.edit_owner",   "Редактировать владельца",   "Edit Owner");
        p("dlg.add_video",    "Добавить видеосалон",       "Add Video Store");
        p("dlg.edit_video",   "Редактировать видеосалон",  "Edit Video Store");
        p("dlg.add_film",     "Добавить фильм",            "Add Film");
        p("dlg.edit_film",    "Редактировать фильм",       "Edit Film");
        p("dlg.add_cassette", "Добавить кассету",          "Add Cassette");
        p("dlg.edit_cassette","Редактировать кассету",     "Edit Cassette");
        p("dlg.add_receipt",  "Добавить квитанцию",        "Add Receipt");
        p("dlg.edit_receipt", "Редактировать квитанцию",   "Edit Receipt");
        p("dlg.add_director", "Добавить режиссёра",        "Add Director");
        p("dlg.edit_director","Редактировать режиссёра",   "Edit Director");
        p("dlg.add_item",     "Добавить запись",           "Add Record");
        p("dlg.edit_item",    "Редактировать запись",      "Edit Record");
    }

    private static void p(String key, String ru, String en) {
        str.put(key, new String[]{ru, en});
    }

    /** Получить строку на текущем языке */
    public static String t(String key) {
        String[] arr = str.get(key);
        if (arr == null) return "[" + key + "]";
        return arr[current == Lang.RU ? 0 : 1];
    }

    public static Lang getLang()  { return current; }
    public static boolean isRu()  { return current == Lang.RU; }

    public static void setLang(Lang lang) {
        if (current == lang) return;
        current = lang;
        prefs.put(PREF_LANG, lang.name());
        listeners.forEach(Runnable::run);
    }

    public static void addListener(Runnable r) { listeners.add(r); }
}
