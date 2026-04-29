package frontend.UI.Panels;

import backend.dao.QueryDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Панель выполнения SQL-запросов из ТЗ.
 * Позволяет выбрать запрос, ввести параметры и посмотреть результат.
 */
public class QueryPanel extends JPanel {

    private final QueryDAO queryDAO = new QueryDAO();
    private final JComboBox<String> queryCombo;
    private final JPanel paramsPanel;
    private final List<JTextField> paramFields = new ArrayList<>();
    private final JTable resultTable;
    private final DefaultTableModel resultModel;
    private final JLabel statusLabel;

    public QueryPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Map<String, String[]> defs = QueryDAO.getQueryDefinitions();

        // === Верхняя панель выбора запроса ===
        JPanel topPanel = new JPanel(new BorderLayout(8, 4));

        JLabel lblSelect = new JLabel("Выберите запрос:");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        topPanel.add(lblSelect, BorderLayout.NORTH);

        queryCombo = new JComboBox<>(defs.keySet().toArray(new String[0]));
        queryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        queryCombo.addActionListener(e -> updateParamFields());
        topPanel.add(queryCombo, BorderLayout.CENTER);

        // === Панель параметров ===
        paramsPanel = new JPanel();
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        paramsPanel.setBorder(BorderFactory.createTitledBorder("Параметры запроса"));

        // === Кнопка выполнения ===
        JButton btnRun = new JButton("▶ Выполнить запрос");
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRun.setBackground(new Color(30, 120, 200));
        btnRun.setForeground(Color.WHITE);
        btnRun.setFocusPainted(false);
        btnRun.setBorderPainted(false);
        btnRun.addActionListener(e -> runQuery());

        JPanel runPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        runPanel.add(btnRun);

        // === Статус ===
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(80, 80, 80));

        // === Таблица результатов ===
        resultModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(resultModel);
        resultTable.setAutoCreateRowSorter(true);
        resultTable.setRowHeight(22);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Результат"));

        // Компоновка верхней части
        JPanel controlPanel = new JPanel(new BorderLayout(0, 4));
        controlPanel.add(topPanel, BorderLayout.NORTH);
        controlPanel.add(paramsPanel, BorderLayout.CENTER);
        controlPanel.add(runPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        updateParamFields(); // инициализация при открытии
    }

    /** Обновить поля ввода параметров при смене запроса */
    private void updateParamFields() {
        paramsPanel.removeAll();
        paramFields.clear();

        String selected = (String) queryCombo.getSelectedItem();
        if (selected == null) return;

        Map<String, String[]> defs = QueryDAO.getQueryDefinitions();
        String[] def = defs.get(selected);
        if (def == null || def[1].isEmpty()) {
            paramsPanel.add(new JLabel("  (нет параметров)"));
        } else {
            String[] paramNames = def[1].split("\\|");
            for (String paramName : paramNames) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
                JLabel lbl = new JLabel(paramName + ":");
                lbl.setPreferredSize(new Dimension(220, 22));
                JTextField field = new JTextField(20);
                paramFields.add(field);
                row.add(lbl);
                row.add(field);
                paramsPanel.add(row);
            }
        }

        paramsPanel.revalidate();
        paramsPanel.repaint();
    }

    /** Выполнить выбранный запрос */
    private void runQuery() {
        String selected = (String) queryCombo.getSelectedItem();
        if (selected == null) return;

        String[] params = paramFields.stream()
                .map(JTextField::getText)
                .toArray(String[]::new);

        try {
            DefaultTableModel result = queryDAO.executeNamedQuery(selected, params);
            // Обновить таблицу результатов
            Object[] cols = new Object[result.getColumnCount()];
            for (int c = 0; c < result.getColumnCount(); c++) cols[c] = result.getColumnName(c);

            Object[][] data = new Object[result.getRowCount()][result.getColumnCount()];
            for (int r = 0; r < result.getRowCount(); r++)
                for (int c = 0; c < result.getColumnCount(); c++)
                    data[r][c] = result.getValueAt(r, c);

            resultModel.setDataVector(data, cols);
            statusLabel.setText("✅ Строк: " + result.getRowCount() + " | " + selected);
            statusLabel.setForeground(new Color(0, 120, 0));
        } catch (SQLException e) {
            statusLabel.setText("❌ Ошибка: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Ошибка выполнения запроса:\n" + e.getMessage(),
                    "SQL Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}