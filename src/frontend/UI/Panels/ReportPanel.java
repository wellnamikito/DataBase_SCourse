package frontend.UI.Panels;
import backend.util.*;
import backend.dao.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ReportPanel extends JPanel {

    private final ReportDAO reportDAO = new ReportDAO();
    private DefaultTableModel currentModel;
    private String currentTitle = "";

    private JLabel     lblTitle;
    private JPanel     btnPanel, funcPanel, bottomBar, topAll;
    private JButton    btn1, btn2, btn3, btnFunc, btnExport;
    private JLabel     lblSvcKey, lblYearKey;
    private JTextField serviceField, yearField;
    private JTable     reportTable;
    private DefaultTableModel reportModel;
    private JScrollPane reportScroll;
    private JLabel     statusLabel;

    public ReportPanel() {
        setLayout(new BorderLayout(0, 0));
        buildUI();
        // Применить тему сразу при создании
        applyTheme();
        ThemeManager.getInstance().addListener(this::applyTheme);
        I18n.addListener(this::updateTexts);
    }

    private void buildUI() {
        lblTitle = new JLabel();
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setOpaque(true);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btn1 = makeBtn(new Color(37,  99, 235));
        btn2 = makeBtn(new Color(22, 163,  74));
        btn3 = makeBtn(new Color(124, 58, 237));
        btn1.addActionListener(e -> loadReport(() -> reportDAO.reportOwners(),            "report.single"));
        btn2.addActionListener(e -> loadReport(() -> reportDAO.reportCassettesDetailed(), "report.multi"));
        btn3.addActionListener(e -> loadReport(() -> reportDAO.reportRevenueSummary(),    "report.aggr"));
        btnPanel.add(btn1); btnPanel.add(btn2); btnPanel.add(btn3);

        funcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        lblSvcKey  = new JLabel(); lblSvcKey.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblYearKey = new JLabel(); lblYearKey.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        serviceField = new JTextField("Аренда", 12); serviceField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yearField    = new JTextField("2023",    6);  yearField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnFunc = makeSmallBtn(new Color(37, 99, 235));
        btnFunc.addActionListener(e -> runServiceStats());
        funcPanel.add(lblSvcKey); funcPanel.add(serviceField);
        funcPanel.add(lblYearKey); funcPanel.add(yearField);
        funcPanel.add(btnFunc);

        reportModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportTable = new JTable(reportModel);
        reportTable.setAutoCreateRowSorter(true);
        reportTable.setRowHeight(24);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reportTable.getTableHeader().setReorderingAllowed(false);
        reportScroll = new JScrollPane(reportTable);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        btnExport = makeSmallBtn(new Color(22, 163, 74));
        btnExport.addActionListener(e -> doExport());

        topAll = new JPanel(new BorderLayout(0, 0));
        topAll.add(lblTitle,  BorderLayout.NORTH);
        topAll.add(btnPanel,  BorderLayout.CENTER);
        topAll.add(funcPanel, BorderLayout.SOUTH);

        bottomBar = new JPanel(new BorderLayout());
        bottomBar.add(btnExport,   BorderLayout.WEST);
        bottomBar.add(statusLabel, BorderLayout.CENTER);

        add(topAll,       BorderLayout.NORTH);
        add(reportScroll, BorderLayout.CENTER);
        add(bottomBar,    BorderLayout.SOUTH);

        updateTexts();
    }

    @FunctionalInterface interface RS { DefaultTableModel get() throws SQLException; }

    private void loadReport(RS supplier, String titleKey) {
        try {
            DefaultTableModel model = supplier.get();
            currentModel = model; currentTitle = I18n.t(titleKey);
            loadIntoTable(model);
            statusLabel.setText(I18n.t("report.rows") + model.getRowCount() + "  |  " + currentTitle);
            statusLabel.setForeground(new Color(34, 197, 94));
        } catch (SQLException e) {
            statusLabel.setText(I18n.t("query.error") + e.getMessage());
            statusLabel.setForeground(new Color(239, 68, 68));
            JOptionPane.showMessageDialog(this, I18n.t("msg.db_error") + "\n" + e.getMessage(), I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void runServiceStats() {
        String svc = serviceField.getText().trim();
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            DefaultTableModel model = reportDAO.reportServiceStats(svc, year);
            currentModel = model; currentTitle = "get_service_stats('" + svc + "', " + year + ")";
            loadIntoTable(model);
            statusLabel.setText(I18n.t("report.rows") + model.getRowCount() + "  |  " + currentTitle);
            statusLabel.setForeground(new Color(34, 197, 94));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, I18n.t("val.year_num"), I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            statusLabel.setText(I18n.t("query.error") + e.getMessage());
            statusLabel.setForeground(new Color(239, 68, 68));
            JOptionPane.showMessageDialog(this, I18n.t("msg.db_error") + "\n" + e.getMessage(), I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadIntoTable(DefaultTableModel model) {
        Object[] cols = new Object[model.getColumnCount()];
        for (int c = 0; c < model.getColumnCount(); c++) cols[c] = model.getColumnName(c);
        Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
        for (int r = 0; r < model.getRowCount(); r++)
            for (int c = 0; c < model.getColumnCount(); c++)
                data[r][c] = model.getValueAt(r, c);
        reportModel.setDataVector(data, cols);
    }

    private void doExport() {
        if (currentModel == null || currentModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, I18n.t("report.no_data"), I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        ExcelExporter.exportWithDialog(parent, currentModel, currentTitle);
    }

    private void applyTheme() {
        Color bgPanel = ThemeManager.bgPanel();
        Color bgHead  = ThemeManager.bgHeader();
        Color bgTable = ThemeManager.bgTable();
        Color bgComp  = ThemeManager.bgComponent();
        Color fgText  = ThemeManager.fgText();
        Color fgDim   = ThemeManager.fgDim();
        Color border  = ThemeManager.borderColor();

        // Корень
        setBackground(bgPanel);

        // Заголовок
        lblTitle.setBackground(bgHead);
        lblTitle.setForeground(fgText);

        // Верхний блок
        topAll.setBackground(bgPanel);

        // Панель кнопок
        btnPanel.setBackground(bgPanel);
        btnPanel.setBorder(titled(I18n.t("report.select_title"), fgDim, border));

        // Панель функции
        funcPanel.setBackground(bgPanel);
        funcPanel.setBorder(titled(I18n.t("report.func_title"), fgDim, border));
        lblSvcKey.setForeground(fgText);
        lblYearKey.setForeground(fgText);
        styleField(serviceField, bgComp, fgText, border);
        styleField(yearField,    bgComp, fgText, border);

        // Нижняя панель
        bottomBar.setBackground(bgPanel);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, border));
        statusLabel.setBackground(bgPanel);

        // Таблица
        reportTable.setBackground(bgTable);
        reportTable.setForeground(fgText);
        reportTable.setSelectionBackground(ThemeManager.bgSelected());
        reportTable.setSelectionForeground(Color.WHITE);
        reportTable.setGridColor(border);
        reportTable.getTableHeader().setBackground(bgHead);
        reportTable.getTableHeader().setForeground(fgText);
        reportTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, border));

        // ScrollPane
        reportScroll.setBackground(bgPanel);
        reportScroll.getViewport().setBackground(bgTable);
        reportScroll.setBorder(titled(I18n.t("report.data_border"), fgDim, border));

        repaint();
        revalidate();
    }

    private void updateTexts() {
        lblTitle.setText("  " + I18n.t("report.title"));
        btn1.setText(html(I18n.t("report.single"), I18n.t("report.single_sub")));
        btn2.setText(html(I18n.t("report.multi"),  I18n.t("report.multi_sub")));
        btn3.setText(html(I18n.t("report.aggr"),   I18n.t("report.aggr_sub")));
        btnFunc.setText(I18n.t("btn.run_func"));
        btnExport.setText(I18n.t("btn.export"));
        lblSvcKey.setText(I18n.t("report.svc_lbl"));
        lblYearKey.setText(I18n.t("report.year_lbl"));
        applyTheme(); // обновить бордеры с новыми текстами
    }

    private JButton makeBtn(Color bg) {
        JButton b = new JButton(); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(170, 48));
        return b;
    }
    private JButton makeSmallBtn(Color bg) {
        JButton b = new JButton(); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setBorderPainted(false); b.setOpaque(true);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return b;
    }
    private void styleField(JTextField f, Color bg, Color fg, Color border) {
        f.setBackground(bg); f.setForeground(fg); f.setCaretColor(fg);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border), BorderFactory.createEmptyBorder(2,6,2,6)));
    }
    private javax.swing.border.Border titled(String t, Color fg, Color border) {
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(border),
                t, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.PLAIN, 11), fg);
    }
    private String html(String a, String b) {
        return "<html><center>" + a + "<br><small>" + b + "</small></center></html>";
    }
}