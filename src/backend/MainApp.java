package backend;

import backend.data.db.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Точка входа JavaFX приложения "VideoRental"
 * Система управления видеопрокатом
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("VideoRental — Система управления видеопрокатом");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Загружаем экран авторизации
        showLoginScene();

        primaryStage.show();
    }

    /**
     * Отображает экран авторизации
     */
    public static void showLoginScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/videorental/views/LoginView.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        MainApp.class.getResource("/com/videorental/styles/app.css")
                ).toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
    }

    /**
     * Отображает главный экран после успешной авторизации
     */
    public static void showMainScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/com/videorental/views/MainView.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(
                Objects.requireNonNull(
                        MainApp.class.getResource("/com/videorental/styles/app.css")
                ).toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMaximized(true);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        // Закрываем пул соединений при выходе
        DatabaseConnection.getInstance().close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}