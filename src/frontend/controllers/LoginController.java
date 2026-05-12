package frontend.controllers;

import backend.MainApp;
import backend.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер экрана авторизации.
 * Выполняет подключение к PostgreSQL через AuthService.
 */
public class LoginController implements Initializable {

    @FXML private TextField      txtHost;
    @FXML private TextField      txtPort;
    @FXML private TextField      txtDatabase;
    @FXML private TextField      txtUsername;
    @FXML private PasswordField  txtPassword;
    @FXML private Button         btnLogin;
    @FXML private Label          lblError;
    @FXML private Label          lblSubtitle;
    @FXML private Label          lblDeveloper;
    @FXML private Label          lblDb;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Значения по умолчанию
        txtHost.setText("localhost");
        txtPort.setText("5432");
        txtDatabase.setText("videorental");
        txtUsername.setText("postgres");

        // Информация о разработчике
        lblDeveloper.setText("Разработчик: " + AuthService.DEVELOPER_NAME);
        lblDb.setText(AuthService.PROJECT_NAME);

        // Enter для входа
        txtPassword.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) onLogin();
        });

        // Фокус на поле пароля
        Platform.runLater(() -> txtPassword.requestFocus());
    }

    @FXML
    private void onLogin() {
        lblError.setText("");
        btnLogin.setDisable(true);
        btnLogin.setText("Подключение...");

        String host     = txtHost.getText().trim();
        String portStr  = txtPort.getText().trim();
        String database = txtDatabase.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Валидация
        if (host.isEmpty() || database.isEmpty() || username.isEmpty()) {
            showError("Заполните все поля подключения");
            resetButton();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            showError("Некорректный номер порта");
            resetButton();
            return;
        }

        // Выполняем подключение в фоновом потоке
        final int finalPort = port;
        Thread connectThread = new Thread(() -> {
            try {
                authService.authenticate(host, finalPort, database, username, password);
                Platform.runLater(() -> {
                    try {
                        MainApp.showMainScene();
                    } catch (Exception ex) {
                        showError("Ошибка открытия главного окна: " + ex.getMessage());
                        resetButton();
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("password authentication")) {
                        showError("Неверный логин или пароль");
                    } else if (msg != null && msg.contains("Connection refused")) {
                        showError("Не удалось подключиться к серверу " + host + ":" + finalPort);
                    } else {
                        showError("Ошибка: " + msg);
                    }
                    resetButton();
                });
            }
        });
        connectThread.setDaemon(true);
        connectThread.start();
    }

    private void showError(String message) {
        lblError.setText("⚠ " + message);
    }

    private void resetButton() {
        btnLogin.setDisable(false);
        btnLogin.setText("Войти в систему →");
    }
}
