package frontend.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.Map;

/**
 * Утилита для динамической генерации столбцов TableView
 * из generic List<Map<String,Object>> — результата SELECT запросов.
 */
public class TableHelper {

    /**
     * Заполняет TableView из списка Map-строк.
     * Автоматически создаёт столбцы по ключам первой строки.
     */
    public static void populate(TableView<Map<String, Object>> table,
                                List<Map<String, Object>> data) {
        table.getColumns().clear();
        table.getItems().clear();

        if (data == null || data.isEmpty()) {
            table.setItems(FXCollections.emptyObservableList());
            return;
        }

        // Создаём столбцы из ключей первой строки
        for (String key : data.get(0).keySet()) {
            TableColumn<Map<String, Object>, Object> col = new TableColumn<>(formatHeader(key));
            col.setCellValueFactory(cellData ->
                    new SimpleObjectProperty<>(cellData.getValue().get(key))
            );
            col.setMinWidth(100);
            col.setPrefWidth(computeWidth(key, data));
            table.getColumns().add(col);
        }

        table.setItems(FXCollections.observableArrayList(data));
    }

    /**
     * Форматирует имя столбца: snake_case → Title Case
     */
    private static String formatHeader(String key) {
        if (key == null) return "";
        // Уже отформатирован (русский текст с кавычками)
        if (key.startsWith("\"")) return key.replace("\"", "");
        String[] parts = key.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0)))
                        .append(p.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    /**
     * Вычисляет предпочтительную ширину столбца по данным
     */
    private static double computeWidth(String key, List<Map<String, Object>> data) {
        int maxLen = key.length();
        for (Map<String, Object> row : data) {
            Object val = row.get(key);
            if (val != null) {
                maxLen = Math.max(maxLen, val.toString().length());
            }
        }
        return Math.min(Math.max(maxLen * 8 + 20, 100), 300);
    }
}
