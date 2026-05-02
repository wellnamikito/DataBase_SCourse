package backend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Интернационализация (i18n) — русский / английский.
 * Использование: I18n.t("table.owners")
 */
public class I18n {

    public enum Lang { RU, EN }

    private static Lang current = Lang.RU;
    private static final List<Runnable> listeners = new ArrayList<>();
    private static final Map<String, String[]> strings = new HashMap<>();

    static {
        // Формат: ключ -> [RU, EN]

        // === Меню ===
        put("menu.tables",          "📂 Таблицы",           "📂 Tables");
        put("menu.views",           "👁️ Представления",     "👁️ Views");
        put("menu.queries",         "🔍 Запросы",           "🔍 Queries");
        put("menu.charts",          "📊 Диаграммы",         "📊 Charts");
        put("menu.reports",         "📋 Отчёты",            "📋 Reports");
        put("menu.help",            "❓ Справка",            "❓ Help");
        put("menu.about",           "О программе",          "About");
        put("menu.settings",        "⚙️ Настройки",         "⚙️ Settings");

        // === Таблицы ===
        put("table.owners",         "👥 Владельцы",         "👥 Owners");
        put("table.video",          "🏪 Видеосалоны",       "🏪 Video Stores");
        put("table.film",           "🎬 Фильмы",            "🎬 Films");
        put("table.cassette",       "📼 Кассеты",           "📼 Cassettes");
        put("table.receipt",        "🧾 Квитанции",         "🧾 Receipts");
        put("table.districts",      "📍 Районы",            "📍 Districts");
        put("table.service",        "🔧 Услуги",            "🔧 Services");
        put("table.quality",        "⭐ Качество",           "⭐ Quality");
        put("table.director",       "🎭 Режиссёры",         "🎭 Directors");
        put("table.studio",         "🏭 Студии",            "🏭 Studios");
        put("table.country",        "🌍 Страны",            "🌍 Countries");
        put("table.master_detail",  "🔗 Видеосалон + Кассеты", "🔗 Store + Cassettes");

        // === Представления ===
        put("view.films_full",      "vw_films_full",                   "vw_films_full");
        put("view.revenue",         "vw_total_revenue",                "vw_total_revenue");
        put("view.people",          "vw_people",                       "vw_people");
        put("view.category",        "vw_receipt_category",             "vw_receipt_category");
        put("view.simple",          "✏️ vw_video_simple (обновляемое)", "✏️ vw_video_simple (editable)");

        // === Кнопки CRUD ===
        put("btn.add",              "➕  Добавить",          "➕  Add");
        put("btn.edit",             "✏️  Редактировать",    "✏️  Edit");
        put("btn.delete",           "🗑️  Удалить",          "🗑️  Delete");
        put("btn.refresh",          "🔄  Обновить",         "🔄  Refresh");
        put("btn.save",             "💾 Сохранить",         "💾 Save");
        put("btn.cancel",           "Отмена",               "Cancel");
        put("btn.run",              "▶ Выполнить запрос",   "▶ Run Query");
        put("btn.export",           "📥 Экспорт в Excel",   "📥 Export to Excel");
        put("btn.refresh_charts",   "🔄 Обновить диаграммы","🔄 Refresh Charts");

        // === Поиск ===
        put("search.label",         "🔍 Поиск:",            "🔍 Search:");
        put("search.tooltip",       "Введите текст для фильтрации", "Type to filter");

        // === Отчёты ===
        put("report.single",        "👤 Однотабличный\n(Владельцы)",       "👤 Single Table\n(Owners)");
        put("report.multi",         "🎞️ Многотабличный\n(Кассеты)",        "🎞️ Multi-Table\n(Cassettes)");
        put("report.aggregate",     "💰 Агрегированный\n(Выручка)",        "💰 Aggregate\n(Revenue)");
        put("report.func_label",    "🔧 Функция: get_service_stats(услуга, год)", "🔧 Function: get_service_stats(service, year)");
        put("report.service_lbl",   "Услуга:",              "Service:");
        put("report.year_lbl",      "Год:",                 "Year:");
        put("report.run_func",      "▶ Выполнить",          "▶ Run");
        put("report.data_title",    "Данные отчёта",        "Report Data");
        put("report.rows",          "✅ Строк: ",            "✅ Rows: ");

        // === Диаграммы ===
        put("chart.pie_title",      "Квитанции по категориям цен",  "Receipts by Price Category");
        put("chart.bar_title",      "Выручка по видеосалонам",      "Revenue by Video Store");
        put("chart.3d_title",       "Выручка по видеосалонам (3D)", "Revenue by Video Store (3D)");
        put("chart.tab_pie",        "🥧 Категории цен",             "🥧 Price Categories");
        put("chart.tab_bar",        "📊 Выручка (Bar)",             "📊 Revenue (Bar)");
        put("chart.tab_3d",         "📦 Выручка (3D)",              "📦 Revenue (3D)");
        put("chart.axis_store",     "Видеосалон",                   "Video Store");
        put("chart.axis_sum",       "Сумма (руб.)",                 "Amount (RUB)");
        put("chart.revenue_series", "Выручка",                      "Revenue");

        // === Запросы ===
        put("query.select_label",   "Выберите запрос:",     "Select query:");
        put("query.params_title",   "Параметры запроса",    "Query Parameters");
        put("query.no_params",      "  (нет параметров)",   "  (no parameters)");
        put("query.result_title",   "Результат",            "Result");
        put("query.error",          "❌ Ошибка: ",          "❌ Error: ");

        // === VIEW simple ===
        put("view.simple.info",
                "<html><b>📝 Обновляемое представление vw_video_simple</b><br>" +
                        "<i>Изменения записываются через триггер trg_update_video в таблицу Video</i></html>",
                "<html><b>📝 Updatable view vw_video_simple</b><br>" +
                        "<i>Changes are written via trigger trg_update_video to Video table</i></html>"
        );
        put("view.simple.edit_btn", "✏️ Редактировать через VIEW (триггер)", "✏️ Edit via VIEW (trigger)");
        put("view.simple.saved",    "✅ Обновлено через VIEW (триггер выполнен)", "✅ Updated via VIEW (trigger fired)");

        // === Сообщения ===
        put("msg.select_row",       "Выберите строку в таблице",    "Please select a row");
        put("msg.select_video",     "Сначала выберите видеосалон",  "Select a video store first");
        put("msg.select_cassette",  "Выберите кассету",             "Select a cassette");
        put("msg.confirm_delete",   "Удалить запись?",              "Delete this record?");
        put("msg.confirm_title",    "Подтверждение",                "Confirm");
        put("msg.db_error",         "Ошибка БД:",                   "Database error:");
        put("msg.error_title",      "Ошибка",                       "Error");
        put("msg.warning_title",    "Предупреждение",               "Warning");
        put("msg.connected",        "✅ Подключено к PostgreSQL",   "✅ Connected to PostgreSQL");
        put("msg.no_connection",    "❌ Нет подключения к БД",      "❌ No database connection");

        // === Настройки ===
        put("settings.theme",       "Тема:",                        "Theme:");
        put("settings.dark",        "🌙 Тёмная",                   "🌙 Dark");
        put("settings.light",       "☀️ Светлая",                  "☀️ Light");
        put("settings.lang",        "Язык / Language:",             "Language:");
        put("settings.ru",          "🇷🇺 Русский",                 "🇷🇺 Russian");
        put("settings.en",          "🇬🇧 English",                  "🇬🇧 English");

        // === Заголовки панелей ===
        put("panel.owners",         "Владельцы",                    "Owners");
        put("panel.video",          "Видеосалоны",                  "Video Stores");
        put("panel.film",           "Фильмы",                       "Films");
        put("panel.cassette",       "Кассеты",                      "Cassettes");
        put("panel.receipt",        "Квитанции",                    "Receipts");
        put("panel.master",         "📼 Видеосалоны (Master)",      "📼 Video Stores (Master)");
        put("panel.detail",         "🎞️ Кассеты выбранного видеосалона (Detail)", "🎞️ Cassettes of Selected Store (Detail)");
        put("panel.queries",        "📋 Запросы из ТЗ",            "📋 Queries from Spec");
        put("panel.reports",        "📋 Отчёты",                   "📋 Reports");
        put("panel.charts",         "📊 Диаграммы",                 "📊 Charts");

        // === О программе ===
        put("about.text",
                "<html><b>Видеопрокат — ИС</b><br>Архитектура: MVC + DAO<br>СУБД: PostgreSQL<br>UI: Java Swing<br>Charts: JFreeChart<br>Export: Apache POI</html>",
                "<html><b>Video Rental — IS</b><br>Architecture: MVC + DAO<br>DB: PostgreSQL<br>UI: Java Swing<br>Charts: JFreeChart<br>Export: Apache POI</html>"
        );

        // === Диалоги редактирования ===
        put("dlg.add_owner",        "Добавить владельца",           "Add Owner");
        put("dlg.edit_owner",       "Редактировать владельца",      "Edit Owner");
        put("dlg.add_video",        "Добавить видеосалон",          "Add Video Store");
        put("dlg.edit_video",       "Редактировать видеосалон",     "Edit Video Store");
        put("dlg.add_film",         "Добавить фильм",               "Add Film");
        put("dlg.edit_film",        "Редактировать фильм",          "Edit Film");
        put("dlg.add_cassette",     "Добавить кассету",             "Add Cassette");
        put("dlg.edit_cassette",    "Редактировать кассету",        "Edit Cassette");
        put("dlg.add_receipt",      "Добавить квитанцию",           "Add Receipt");
        put("dlg.edit_receipt",     "Редактировать квитанцию",      "Edit Receipt");
        put("dlg.add_director",     "Добавить режиссёра",           "Add Director");
        put("dlg.edit_director",    "Редактировать режиссёра",      "Edit Director");

        // === Поля форм ===
        put("field.familia",        "Фамилия:",                     "Last Name:");
        put("field.name",           "Имя:",                         "First Name:");
        put("field.otchestvo",      "Отчество:",                    "Middle Name:");
        put("field.caption",        "Название:",                    "Name:");
        put("field.district",       "Район:",                       "District:");
        put("field.address",        "Адрес:",                       "Address:");
        put("field.type",           "Тип:",                         "Type:");
        put("field.phone",          "Телефон (+7XXXXXXXXXX):",      "Phone (+7XXXXXXXXXX):");
        put("field.licence",        "Лицензия (ЛИЦ-XXXXX):",       "Licence (ЛИЦ-XXXXX):");
        put("field.time_start",     "Время открытия:",              "Opening Time:");
        put("field.time_end",       "Время закрытия:",              "Closing Time:");
        put("field.amount",         "Кол-во клиентов:",             "Client Count:");
        put("field.owner",          "Владелец:",                    "Owner:");
        put("field.film",           "Фильм:",                       "Film:");
        put("field.quality",        "Качество:",                    "Quality:");
        put("field.demand",         "Спрос (Demand)",               "Demand");
        put("field.year",           "Год:",                         "Year:");
        put("field.duration",       "Длительность (мин):",          "Duration (min):");
        put("field.description",    "Описание:",                    "Description:");
        put("field.studio",         "Студия:",                      "Studio:");
        put("field.cassette_id",    "Кассета ID:",                  "Cassette ID:");
        put("field.video",          "Видеосалон:",                  "Video Store:");
        put("field.service",        "Услуга:",                      "Service:");
        put("field.date",           "Дата (YYYY-MM-DD):",           "Date (YYYY-MM-DD):");
        put("field.price",          "Цена:",                        "Price:");
        put("field.name_label",     "Название:",                    "Name:");

        // === Валидация ===
        put("val.fio_required",     "Фамилия и Имя обязательны",    "Last name and First name are required");
        put("val.fio_invalid",      "ФИО содержит недопустимые символы!\nТолько буквы, дефис и пробел.",
                "Name contains invalid characters!\nOnly letters, hyphen and space.");
        put("val.phone_invalid",    "Телефон должен быть в формате +7XXXXXXXXXX\nНапример: +79001234567",
                "Phone must be in format +7XXXXXXXXXX\nExample: +79001234567");
        put("val.licence_invalid",  "Лицензия должна быть в формате ЛИЦ-XXXXXX\nНапример: ЛИЦ-123456",
                "Licence must be in format ЛИЦ-XXXXXX\nExample: ЛИЦ-123456");
        put("val.name_required",    "Название обязательно",         "Name is required");
        put("val.empty_field",      "Поле не может быть пустым",    "Field cannot be empty");
        put("val.year_number",      "Год должен быть числом",       "Year must be a number");

        // === Excel ===
        put("excel.save_title",     "Сохранить отчёт как...",       "Save report as...");
        put("excel.saved",          "Файл сохранён:\n",             "File saved:\n");
        put("excel.error",          "Ошибка экспорта:\n",           "Export error:\n");
        put("excel.no_data",        "Сначала загрузите отчёт",      "Load a report first");
        put("excel.title",          "Экспорт Excel",                "Excel Export");
    }

    private static void put(String key, String ru, String en) {
        strings.put(key, new String[]{ru, en});
    }

    /** Получить строку по ключу на текущем языке */
    public static String t(String key) {
        String[] arr = strings.get(key);
        if (arr == null) return "[" + key + "]";
        return arr[current == Lang.RU ? 0 : 1];
    }

    public static Lang getLang()       { return current; }
    public static boolean isRu()       { return current == Lang.RU; }

    public static void setLang(Lang lang) {
        current = lang;
        listeners.forEach(Runnable::run);
    }

    public static void addListener(Runnable r) { listeners.add(r); }
}
