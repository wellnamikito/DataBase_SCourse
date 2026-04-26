package generator;

import java.sql.*;
import java.util.Random;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DataGenerator {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "welll";

    private static final Random random = new Random();

    // ================= ДОМЕННЫЕ ГЕНЕРАТОРЫ =================

    // hour_domain: 0–23
    static int randomHour() {
        return random.nextInt(24);
    }

    // price_domain: > 0
    static int randomPrice() {
        return 50 + random.nextInt(1000);
    }

    // demand_domain: boolean
    static boolean randomDemand() {
        return random.nextBoolean();
    }

    static String randomPhone() {
        return "+7" + String.format("%010d", random.nextInt(1_000_000_000));
    }

    // ================= СЛОВАРИ =================

    static String[] firstNames = {
            "Алексей", "Иван", "Дмитрий", "Сергей", "Андрей",
            "Максим", "Никита", "Егор", "Артем", "Павел"
    };

    static String[] lastNames = {
            "Иванов", "Петров", "Сидоров", "Кузнецов",
            "Смирнов", "Попов", "Васильев", "Новиков"
    };

    static String[] patronymics = {
            "Иванович", "Петрович", "Сергеевич",
            "Андреевич", "Дмитриевич", "Алексеевич"
    };

    static String[] countries = {
            "Россия", "США", "Франция", "Германия", "Япония"
    };

    static String[] districts = {
            "Центральный", "Северный", "Южный", "Западный", "Восточный"
    };

    static String[] services = {
            "Аренда", "Продажа", "Ремонт"
    };

    static String[] qualities = {
            "SD", "HD", "FullHD", "4K"
    };

    static String[] filmWords = {
            "Любовь", "Тень", "Огонь", "Судьба", "Город", "Ночь"
    };

    // ================= ЗАПУСК =================

    public static void generate(int count) {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            conn.setAutoCommit(false);

            clearDatabase(conn);

            insertCountries(conn);
            insertDistricts(conn);
            insertServices(conn);
            insertQuality(conn);
            insertDirectors(conn, count);
            insertOwners(conn, count);
            insertStudio(conn, count);
            insertFilm(conn, count);
            insertVideo(conn, count);
            insertCassette(conn, count);
            insertReceipt(conn, count);

            conn.commit();

            System.out.println("✅ БД заполнена (" + count + " записей)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ОЧИСТКА =================

    static void clearDatabase(Connection conn) throws Exception {

        String sql = """
            TRUNCATE TABLE 
                Receipt,
                Cassette,
                Video,
                Film,
                Studio,
                Director,
                Country,
                Service,
                Quality,
                Districts,
                Owners
            RESTART IDENTITY CASCADE;
        """;

        try (Statement st = conn.createStatement()) {
            st.execute(sql);
        }
    }

    // ================= ГЕНЕРАТОРЫ =================

    static String rand(String[] arr) {
        return arr[random.nextInt(arr.length)];
    }

    static String lastName() {
        return rand(lastNames);
    }

    static String firstName() {
        return rand(firstNames);
    }

    static String otch() {
        return rand(patronymics);
    }

    static String filmName() {
        return rand(filmWords) + " " + rand(filmWords);
    }

    static LocalDate randomDate() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        long days = ChronoUnit.DAYS.between(start, end);
        return start.plusDays(random.nextInt((int) days));
    }

    // ================= INSERT =================

    static void insertCountries(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Country(CountryName) VALUES (?)");

        for (String c : countries) {
            ps.setString(1, c);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertDistricts(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Districts(DistrictName) VALUES (?)");

        for (String d : districts) {
            ps.setString(1, d);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertServices(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Service(ServiceName) VALUES (?)");

        for (String s : services) {
            ps.setString(1, s);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertQuality(Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Quality(QualityName) VALUES (?)");

        for (String q : qualities) {
            ps.setString(1, q);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertDirectors(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Director(Familia, Name, Otchestvo) VALUES (?, ?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setString(1, lastName());
            ps.setString(2, firstName());
            ps.setString(3, otch());
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertOwners(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Owners(Familia, Name, Otchestvo) VALUES (?, ?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setString(1, lastName());
            ps.setString(2, firstName());
            ps.setString(3, otch());
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertStudio(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Studio(StudioName, CountryID) VALUES (?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setString(1, "Студия " + lastName());
            ps.setInt(2, random.nextInt(countries.length) + 1);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertFilm(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Film(Caption, Year, Duration, Information, DirectorID, StudioID) VALUES (?, ?, ?, ?, ?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setString(1, filmName());
            ps.setInt(2, 1990 + random.nextInt(35)); // год
            ps.setInt(3, 60 + random.nextInt(120));
            ps.setString(4, "Описание фильма " + filmName());
            ps.setInt(5, random.nextInt(count) + 1);
            ps.setInt(6, random.nextInt(count) + 1);
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertVideo(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Video(Caption, DistrictID, Address, Type, Phone, Licence, TimeStart, TimeEnd, Amount, OwnerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        for (int i = 0; i < count; i++) {

            int start = randomHour();
            int end = randomHour();

            if (end <= start) {
                end = start + 1;
                if (end > 23) end = 23;
            }

            ps.setString(1, "Видео " + lastName());
            ps.setInt(2, random.nextInt(districts.length) + 1);
            ps.setString(3, "ул. " + lastName() + ", д." + random.nextInt(100));
            ps.setString(4, "Магазин");
            ps.setString(5,  randomPhone());
            ps.setString(6, "ЛИЦ-" + random.nextInt(10000));
            ps.setInt(7, start);
            ps.setInt(8, end);
            ps.setInt(9, random.nextInt(100));
            ps.setInt(10, random.nextInt(count) + 1);

            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertCassette(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Cassette(FilmID, VideoID, QualityID, Demand) VALUES (?, ?, ?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setInt(1, random.nextInt(count) + 1);
            ps.setInt(2, random.nextInt(count) + 1);
            ps.setInt(3, random.nextInt(qualities.length) + 1);
            ps.setBoolean(4, randomDemand());
            ps.addBatch();
        }

        ps.executeBatch();
    }

    static void insertReceipt(Connection conn, int count) throws Exception {
        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Receipt(CassetteID, VideoID, ServiceID, Date, Price) VALUES (?, ?, ?, ?, ?)");

        for (int i = 0; i < count; i++) {
            ps.setInt(1, random.nextInt(count) + 1);
            ps.setInt(2, random.nextInt(count) + 1);
            ps.setInt(3, random.nextInt(services.length) + 1);
            ps.setDate(4, Date.valueOf(randomDate()));
            ps.setInt(5, randomPrice());
            ps.addBatch();
        }

        ps.executeBatch();
    }
}