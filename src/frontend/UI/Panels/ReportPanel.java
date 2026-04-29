package frontend.UI.Panels;

import backend.dao.ReportDAO;
import backend.util.ExcelExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Панель отчётов с 3 видами и экспортом в Excel.
 * Также поддерживает вызов SQL-функции get_service_stats.
 */
public class ReportPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();
    private DefaultTableModel currentModel;
    private String currentTitle;

    private final JTable reportTable;
    private final DefaultTableModel reportModel;
    private final JLabel statusLabel;

    // Поля для функции get_service_stats
    private JTextField serviceField, yearField;

    public ReportPanel() {
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("📋 Отчёты", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // === Панель кнопок отчётов ===
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        btnPanel.setBorder(BorderFactory.createTitledBorder("Выбор отчёта"));

        JButton btn1 = makeBtn("👤 Однотабличный\n(Владельцы)", new Color(60, 110, 180));
        JButton btn2 = makeBtn("🎞️ Многотабличный\n(Кассеты)", new Color(80, 150, 80));
        JButton btn3 = makeBtn("💰 Агрегированный\n(Выручка)", new Color(160, 100, 30));

        btn1.addActionListener(e -> loadReport("Отчёт: Список владельцев", () -> reportDAO.reportOwners()));
        btn2.addActionListener(e -> loadReport("Отчёт: Кассеты с деталями", () -> reportDAO.reportCassettesDetailed()));
        btn3.addActionListener(e -> loadReport("Отчёт: Сводная выручка по салонам", () -> reportDAO.reportRevenueSummary()));

        btnPanel.add(btn1);
        btnPanel.add(btn2);
        btnPanel.add(btn3);

        // === Панель функции get_service_stats ===
        JPanel funcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        funcPanel.setBorder(BorderFactory.createTitledBorder("🔧 Функция: get_service_stats(услуга, год)"));
        serviceField = new JTextField("Аренда", 12);
        yearField    = new JTextField("2023", 6);
        JButton btnFunc = makeBtn("▶ Выполнить", new Color(30, 120, 200));
        btnFunc.addActionListener(e -> {
            String svc = serviceField.getText().trim();
            String yr  = yearField.getText().trim();
            try {
                int year = Integer.parseInt(yr);
                loadReport("Отчёт: get_service_stats('" + svc + "', " + year + ")",
                        () -> reportDAO.reportServiceStats(svc, year));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Год должен быть числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
        funcPanel.add(new JLabel("Услуга:"));
        funcPanel.add(serviceField);
        funcPanel.add(new JLabel("Год:"));
        funcPanel.add(yearField);
        funcPanel.add(btnFunc);

        // === Кнопка экспорта ===
        JButton btnExport = makeBtn("📥 Экспорт в Excel", new Color(30, 140, 30));
        btnExport.addActionListener(e -> {
            if (currentModel == null || currentModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Сначала загрузите отчёт", "Экспорт", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            ExcelExporter.exportWithDialog(parent, currentModel, currentTitle);
        });

        // === Статусная строка ===
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        // === Таблица результатов ===
        reportModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(reportModel);
        reportTable.setAutoCreateRowSorter(true);
        reportTable.setRowHeight(24);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(reportTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Данные отчёта"));

        // === Компоновка ===
        JPanel topControl = new JPanel(new BorderLayout(0, 4));
        topControl.add(title, BorderLayout.NORTH);
        topControl.add(btnPanel, BorderLayout.CENTER);
        topControl.add(funcPanel, BorderLayout.SOUTH);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(btnExport, BorderLayout.WEST);
        bottomBar.add(statusLabel, BorderLayout.CENTER);

        add(topControl, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    @FunctionalInterface
    interface ReportSupplier {
        DefaultTableModel get() throws SQLException;
    }

    private void loadReport(String title, ReportSupplier supplier) {
        try {
            DefaultTableModel model = supplier.get();
            currentModel = model;
            currentTitle = title;

            Object[] cols = new Object[model.getColumnCount()];
            for (int c = 0; c < model.getColumnCount(); c++) cols[c] = model.getColumnName(c);
            Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
            for (int r = 0; r < model.getRowCount(); r++)
                for (int c = 0; c < model.getColumnCount(); c++)
                    data[r][c] = model.getValueAt(r, c);
            reportModel.setDataVector(data, cols);

            statusLabel.setText("✅ " + title + " | Строк: " + model.getRowCount());
            statusLabel.setForeground(new Color(0, 100, 0));
        } catch (SQLException e) {
            statusLabel.setText("❌ Ошибка: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Ошибка:\n" + e.getMessage(), "SQL Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton makeBtn(String text, Color color) {
        JButton btn = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>");
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(160, 45));
        return btn;
    }
}