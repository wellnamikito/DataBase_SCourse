package frontend.UI.Panels;

import backend.dao.QueryDAO;
import backend.util.*;
import backend.dao.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryPanel extends JPanel {

    private final QueryDAO queryDAO = new QueryDAO();

    private JLabel       lblSelect;
    private JComboBox<String> queryCombo;
    private JPanel       paramsPanel, controlPanel;
    private JScrollPane  paramsScroll, resultScroll;
    private List<JTextField> paramFields = new ArrayList<>();
    private JTable       resultTable;
    private DefaultTableModel resultModel;
    private JButton      btnRun;
    private JLabel       statusLabel;

    public QueryPanel() {
        setLayout(new BorderLayout(0, 8));
        buildUI();
        applyTheme();
        ThemeManager.getInstance().addListener(this::applyTheme);
        I18n.addListener(this::updateTexts);
    }

    private void buildUI() {
        // Выбор запроса
        JPanel topPanel = new JPanel(new BorderLayout(0, 6));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        lblSelect = new JLabel(I18n.t("query.select_lbl"));
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 13));
        topPanel.add(lblSelect, BorderLayout.NORTH);

        Map<String, String[]> defs = QueryDAO.getQueryDefinitions();
        queryCombo = new JComboBox<>(defs.keySet().toArray(new String[0]));
        queryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        queryCombo.addActionListener(e -> updateParamFields());
        topPanel.add(queryCombo, BorderLayout.CENTER);

        // Параметры
        paramsPanel = new JPanel();
        paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
        paramsScroll = new JScrollPane(paramsPanel);
        paramsScroll.setPreferredSize(new Dimension(0, 110));
        paramsScroll.setBorder(BorderFactory.createEmptyBorder());

        // Кнопка
        btnRun = new JButton(I18n.t("btn.run"));
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRun.setBackground(ThemeManager.btnRun());
        btnRun.setForeground(Color.WHITE);
        btnRun.setFocusPainted(false);
        btnRun.setBorderPainted(false);
        btnRun.setOpaque(true);
        btnRun.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRun.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnRun.addActionListener(e -> runQuery());

        JPanel runRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        runRow.setOpaque(false);
        runRow.add(btnRun);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));

        // Таблица результатов
        resultModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(resultModel);
        resultTable.setAutoCreateRowSorter(true);
        resultTable.setRowHeight(24);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultTable.getTableHeader().setReorderingAllowed(false);
        resultScroll = new JScrollPane(resultTable);

        // Сборка
        controlPanel = new JPanel(new BorderLayout(0, 4));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 10));
        controlPanel.add(topPanel,    BorderLayout.NORTH);
        controlPanel.add(paramsScroll,BorderLayout.CENTER);
        controlPanel.add(runRow,      BorderLayout.SOUTH);

        add(controlPanel,  BorderLayout.NORTH);
        add(resultScroll,  BorderLayout.CENTER);
        add(statusLabel,   BorderLayout.SOUTH);

        updateParamFields();
    }

    private void updateParamFields() {
        paramsPanel.removeAll();
        paramFields.clear();

        String selected = (String) queryCombo.getSelectedItem();
        if (selected == null) return;

        Map<String, String[]> defs = QueryDAO.getQueryDefinitions();
        String[] def = defs.get(selected);

        Color bgPanel = ThemeManager.bgPanel();
        Color fgText  = ThemeManager.fgText();
        Color fgDim   = ThemeManager.fgDim();
        Color bgComp  = ThemeManager.bgComponent();
        Color border  = ThemeManager.borderColor();

        paramsPanel.setBackground(bgPanel);

        // Заголовок секции параметров
        JLabel title = new JLabel(I18n.t("query.params_title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        title.setForeground(fgDim);
        title.setBackground(bgPanel);
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(6, 10, 4, 10));
        paramsPanel.add(title);

        if (def == null || def[1].isEmpty()) {
            JLabel noP = new JLabel(I18n.t("query.no_params"));
            noP.setForeground(fgDim);
            noP.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            noP.setBackground(bgPanel); noP.setOpaque(true);
            noP.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 10));
            paramsPanel.add(noP);
        } else {
            for (String paramName : def[1].split("\\|")) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
                row.setBackground(bgPanel);
                JLabel lbl = new JLabel(paramName + ":");
                lbl.setForeground(fgText);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                lbl.setPreferredSize(new Dimension(230, 22));
                JTextField field = new JTextField(18);
                field.setBackground(bgComp); field.setForeground(fgText); field.setCaretColor(fgText);
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(border), BorderFactory.createEmptyBorder(2,6,2,6)));
                paramFields.add(field);
                row.add(lbl); row.add(field);
                paramsPanel.add(row);
            }
        }
        paramsPanel.revalidate();
        paramsPanel.repaint();
    }

    private void runQuery() {
        String selected = (String) queryCombo.getSelectedItem();
        if (selected == null) return;
        String[] params = paramFields.stream().map(JTextField::getText).toArray(String[]::new);
        try {
            DefaultTableModel result = queryDAO.executeNamedQuery(selected, params);
            Object[] cols = new Object[result.getColumnCount()];
            for (int c = 0; c < result.getColumnCount(); c++) cols[c] = result.getColumnName(c);
            Object[][] data = new Object[result.getRowCount()][result.getColumnCount()];
            for (int r = 0; r < result.getRowCount(); r++)
                for (int c = 0; c < result.getColumnCount(); c++)
                    data[r][c] = result.getValueAt(r, c);
            resultModel.setDataVector(data, cols);
            statusLabel.setText(I18n.t("query.rows") + result.getRowCount() + " | " + selected);
            statusLabel.setForeground(new Color(34, 197, 94));
        } catch (SQLException e) {
            statusLabel.setText(I18n.t("query.error") + e.getMessage());
            statusLabel.setForeground(new Color(239, 68, 68));
            JOptionPane.showMessageDialog(this, I18n.t("msg.db_error") + "\n" + e.getMessage(),
                    I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyTheme() {
        Color bgPanel = ThemeManager.bgPanel();
        Color bgHead  = ThemeManager.bgHeader();
        Color bgTable = ThemeManager.bgTable();
        Color fgText  = ThemeManager.fgText();
        Color fgDim   = ThemeManager.fgDim();
        Color border  = ThemeManager.borderColor();

        setBackground(bgPanel);
        controlPanel.setBackground(bgPanel);
        lblSelect.setForeground(fgText);

        // ComboBox
        queryCombo.setBackground(ThemeManager.bgComponent());
        queryCombo.setForeground(fgText);

        // Кнопка
        btnRun.setBackground(ThemeManager.btnRun());
        btnRun.getParent().setBackground(bgPanel);

        // Статус
        statusLabel.setBackground(bgPanel);

        // Таблица
        resultTable.setBackground(bgTable);
        resultTable.setForeground(fgText);
        resultTable.setSelectionBackground(ThemeManager.bgSelected());
        resultTable.setSelectionForeground(Color.WHITE);
        resultTable.setGridColor(border);
        resultTable.getTableHeader().setBackground(bgHead);
        resultTable.getTableHeader().setForeground(fgText);
        resultTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,border));

        resultScroll.setBackground(bgPanel);
        resultScroll.getViewport().setBackground(bgTable);
        resultScroll.setBorder(titled(I18n.t("query.result_title"), fgDim, border));

        // ScrollPane параметров
        paramsScroll.setBackground(bgPanel);
        paramsScroll.getViewport().setBackground(bgPanel);
        paramsScroll.setBorder(BorderFactory.createEmptyBorder());

        updateParamFields();
        repaint();
        revalidate();
    }

    private void updateTexts() {
        lblSelect.setText(I18n.t("query.select_lbl"));
        btnRun.setText(I18n.t("btn.run"));
        updateParamFields();
        applyTheme();
    }

    private javax.swing.border.Border titled(String t, Color fg, Color border) {
        return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(border),
                t, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.PLAIN, 11), fg);
    }
}