import java.sql.*;
import java.util.Scanner;

public class Main {

    static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    static final String USER = "postgres";
    static final String PASSWORD = "welll";

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean exit = false;

        do {
            System.out.println("\n=== ГЛАВНОЕ МЕНЮ ===");
            System.out.println("1. Справочники");
            System.out.println("2. Основные таблицы");
            System.out.println("0. Выход");

            int choice = getIntInput("Выберите пункт: ");

            switch (choice) {
                case 1 -> dictionaryMenu();
                case 2 -> mainTablesMenu();
                case 0 -> {
                    System.out.println("Выход...");
                    exit = true;
                }
                default -> System.out.println("❌ Неверный выбор!");
            }

        } while (!exit);
    }

    // ================= СПРАВОЧНИКИ =================

    static void dictionaryMenu() {

        boolean back = false;

        do {
            System.out.println("\n=== СПРАВОЧНИКИ ===");
            System.out.println("1. Service");
            System.out.println("2. Districts");
            System.out.println("3. Country");
            System.out.println("4. Director");
            System.out.println("5. Показать таблицу");
            System.out.println("0. Назад");

            int choice = getIntInput("Выберите пункт: ");

            switch (choice) {
                case 1 -> insertSimple("Service", "ServiceName", getValidString("Название услуги: "));
                case 2 -> insertSimple("Districts", "DistrictName", getValidString("Название района: "));
                case 3 -> insertSimple("Country", "CountryName", getValidString("Название страны: "));
                case 4 -> addDirector();
                case 5 -> chooseDictionaryTable();
                case 0 -> back = true;
            }

        } while (!back);
    }

    static void addDirector() {
        String familia = getValidString("Фамилия: ");
        String name = getValidString("Имя: ");
        String otch = getValidString("Отчество: ");

        String sql = "INSERT INTO Director (Familia, Name, Otchestvo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, familia);
            ps.setString(2, name);
            ps.setString(3, otch);
            ps.executeUpdate();

            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ОСНОВНЫЕ ТАБЛИЦЫ =================

    static void mainTablesMenu() {

        boolean back = false;

        do {
            System.out.println("\n=== ОСНОВНЫЕ ТАБЛИЦЫ ===");
            System.out.println("1. Owners");
            System.out.println("2. Quality");
            System.out.println("3. Studio");
            System.out.println("4. Film");
            System.out.println("5. Video");
            System.out.println("6. Cassette");
            System.out.println("7. Receipt");
            System.out.println("8. Показать таблицу");
            System.out.println("9. ⚡ Заполнить БД (генератор)");
            System.out.println("0. Назад");

            int choice = getIntInput("Выберите: ");

            switch (choice) {
                case 1 -> addOwner();
                case 2 -> insertSimple("Quality", "QualityName", getValidString("Название качества: "));
                case 3 -> addStudio();
                case 4 -> addFilm();
                case 5 -> addVideo();
                case 6 -> addCassette();
                case 7 -> addReceipt();
                case 8 -> chooseMainTable();
                case 9 -> {
                    int count = getIntInput("Сколько записей сгенерировать: ");
                    DataGenerator.generate(count);
                }
                case 0 -> back = true;
            }

        } while (!back);
    }

    // ================= INSERT =================

    static void insertSimple(String table, String column, String value) {

        String sql = "INSERT INTO " + table + " (" + column + ") VALUES (?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.executeUpdate();
            System.out.println("✔ Добавлено в " + table);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addOwner() {

        String f = getValidString("Фамилия: ");
        String n = getValidString("Имя: ");
        String o = getValidString("Отчество: ");

        String sql = "INSERT INTO Owners (Familia, Name, Otchestvo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, f);
            ps.setString(2, n);
            ps.setString(3, o);
            ps.executeUpdate();

            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addStudio() {

        String name = getValidString("Название студии: ");
        int countryId = chooseFromTable("Country", "CountryID", "CountryName");

        String sql = "INSERT INTO Studio (StudioName, CountryID) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, countryId);
            ps.executeUpdate();

            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addFilm() {

        System.out.print("Название: ");
        String caption = scanner.nextLine();

        System.out.print("Год: ");
        String year = scanner.nextLine();

        int duration = getIntInput("Длительность: ");

        System.out.print("Описание: ");
        String info = scanner.nextLine();

        int directorId = chooseFromTable("Director", "DirectorID", "Familia");
        int studioId = chooseFromTable("Studio", "StudioID", "StudioName");

        String sql = "INSERT INTO Film VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, caption);
            ps.setString(2, year);
            ps.setInt(3, duration);
            ps.setString(4, info);
            ps.setInt(5, directorId);
            ps.setInt(6, studioId);

            ps.executeUpdate();
            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addVideo() {

        System.out.print("Название: ");
        String caption = scanner.nextLine();

        int districtId = chooseFromTable("Districts", "DistrictID", "DistrictName");

        System.out.print("Адрес: ");
        String address = scanner.nextLine();

        System.out.print("Тип: ");
        String type = scanner.nextLine();

        System.out.print("Телефон: ");
        String phone = scanner.nextLine();

        System.out.print("Лицензия: ");
        String licence = scanner.nextLine();

        int start = getIntInput("Начало: ");
        int end = getIntInput("Конец: ");
        int amount = getIntInput("Количество: ");

        int ownerId = chooseFromTable("Owners", "OwnerID", "Familia");

        String sql = "INSERT INTO Video VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, caption);
            ps.setInt(2, districtId);
            ps.setString(3, address);
            ps.setString(4, type);
            ps.setString(5, phone);
            ps.setString(6, licence);
            ps.setInt(7, start);
            ps.setInt(8, end);
            ps.setInt(9, amount);
            ps.setInt(10, ownerId);

            ps.executeUpdate();
            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addCassette() {

        int filmId = chooseFromTable("Film", "FilmID", "Caption");
        int videoId = chooseFromTable("Video", "VideoID", "Caption");
        int qualityId = chooseFromTable("Quality", "QualityID", "QualityName");

        System.out.print("Спрос (true/false): ");
        boolean demand = Boolean.parseBoolean(scanner.nextLine());

        String sql = "INSERT INTO Cassette (FilmID, VideoID, QualityID, Demand) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, filmId);
            ps.setInt(2, videoId);
            ps.setInt(3, qualityId);
            ps.setBoolean(4, demand);

            ps.executeUpdate();
            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void addReceipt() {

        int cassetteId = chooseFromTable("Cassette", "CasseteID", "CasseteID");
        int videoId = chooseFromTable("Video", "VideoID", "Caption");
        int serviceId = chooseFromTable("Service", "ServiceID", "ServiceName");

        System.out.print("Дата (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        int price = getIntInput("Цена: ");

        String sql = "INSERT INTO Receipt (CassetteID, VideoID, ServiceID, Date, Price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cassetteId);
            ps.setInt(2, videoId);
            ps.setInt(3, serviceId);
            ps.setDate(4, Date.valueOf(date));
            ps.setInt(5, price);

            ps.executeUpdate();
            System.out.println("✔ Добавлено");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ВЫБОР FK =================

    static int chooseFromTable(String table, String idCol, String nameCol) {

        String sql = "SELECT " + idCol + ", " + nameCol + " FROM " + table;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nВыберите из " + table + ":");

            while (rs.next()) {
                System.out.println(rs.getInt(1) + " - " + rs.getString(2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return getIntInput("Введите ID: ");
    }

    // ================= SHOW =================

    static void chooseDictionaryTable() {
        System.out.println("1.Service 2.Districts 3.Country 4.Director");
        int c = getIntInput(">");
        switch (c) {
            case 1 -> showTable("Service");
            case 2 -> showTable("Districts");
            case 3 -> showTable("Country");
            case 4 -> showTable("Director");
        }
    }

    static void chooseMainTable() {
        System.out.println("1.Owners 2.Quality 3.Studio 4.Film 5.Video 6.Cassette 7.Receipt");
        int c = getIntInput(">");
        switch (c) {
            case 1 -> showTable("Owners");
            case 2 -> showTable("Quality");
            case 3 -> showTable("Studio");
            case 4 -> showTable("Film");
            case 5 -> showTable("Video");
            case 6 -> showTable("Cassette");
            case 7 -> showTable("Receipt");
        }
    }

    static void showTable(String table) {

        String sql = "SELECT * FROM " + table;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            System.out.println("\n=== " + table + " ===");

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    System.out.print(rs.getString(i) + " | ");
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= VALIDATION =================

    static int getIntInput(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("❌ Ошибка!");
            }
        }
    }

    static String getValidString(String msg) {
        while (true) {
            System.out.print(msg);
            String s = scanner.nextLine().trim();

            if (s.isEmpty()) continue;
            if (!s.matches("[a-zA-Zа-яА-Я]+")) continue;

            return s;
        }
    }
}