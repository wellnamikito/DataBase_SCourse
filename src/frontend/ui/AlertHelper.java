package frontend.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Optional;

/**
 * Утилита для отображения диалоговых окон
 */
public class AlertHelper {

    public static void error(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(title);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    public static void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(title);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText(title);
        alert.setContentText(message);
        styleAlert(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static File showSaveDialog(Window owner, String title,
                                      String extName, String extFilter) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(extName, extFilter)
        );
        return fc.showSaveDialog(owner);
    }

    public static File showOpenImageDialog(Window owner) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите изображение");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        return fc.showOpenDialog(owner);
    }

    private static void styleAlert(Alert alert) {
        // Применяем стиль приложения если доступен
        try {
            alert.getDialogPane().getStylesheets().add(
                    AlertHelper.class.getResource("/com/videorental/styles/app.css").toExternalForm()
            );
        } catch (Exception ignored) {}
    }
}
