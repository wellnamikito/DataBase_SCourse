package backend.util;
import java.sql.*;

/**
 * Класс для управления подключением к PostgreSQL
 * Обеспечивает единственное соединение на время работы приложнеия
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "welll";

    private static Connection connection;
    private DatabaseConnection(){}

    /**
     * Возвращает активное соединение, при необходимости - создаст новое
     */
    public static Connection getConnection() throws SQLException{
        if(connection == null || connection.isClosed()){
            try{
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setAutoCommit(true);
                System.out.println("[DB] Соединение установленно: " + URL);
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver не найден", e);
            }
        }
        return connection;
    }

    /** закрыть соединение при завершении приложения */
    public static void close(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("[DB] Соедение закрыто.");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
