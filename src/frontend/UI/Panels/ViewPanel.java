package frontend.UI.Panels;

import backend.dao.ViewDAO;
import backend.util.ExcelExporter;
import backend.util.I18n;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class ViewPanel extends JPanel {

    private final ViewDAO viewDAO = new ViewDAO();

    private JComboBox<String> combo;
    private JLabel            lblTitle;
    private JButton           btnLoad;
    private JButton           btnExport;
    private JTable            table;
    private DefaultTableModel model;

    // Текущий выбранный i18n-ключ (сохраняем при смене языка)
    private String selectedKey = null;

    public ViewPanel() {
        setLayout(new BorderLayout(8, 8));

        lblTitle = new JLabel("  " + I18n.t("menu.views"));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        combo     = new JComboBox<>();
        btnLoad   = new JButton(I18n.t("btn.refresh"));
        btnExport = new JButton(I18n.t("btn.export"));
        btnExport.setBackground(new Color(34, 139, 34));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        btnExport.setOpaque(true);
        btnExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> exportToExcel());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnLoad);
        btnRow.add(btnExport);

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(combo,   BorderLayout.CENTER);
        top.add(btnRow,  BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout());
        north.add(lblTitle, BorderLayout.NORTH);
        north.add(top,      BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadView());

        // Сохраняем ключ при ручном выборе в combo
        combo.addActionListener(e -> {
            String label = (String) combo.getSelectedItem();
            if (label != null) {
                selectedKey = ViewDAO.getLocalizedViews().get(label);
            }
        });

        // При смене языка — перестраиваем combo, сохраняя выбор
        I18n.addListener(this::rebuildCombo);

        refreshCombo();
        loadView();
    }

    private void exportToExcel() {
        String title = selectedKey != null ? I18n.t(selectedKey) : I18n.t("menu.views");
        ExcelExporter.exportWithDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                model,
                title
        );
    }

    /**
     * Перестроить combo при смене языка.
     * Сохраняем selectedKey и восстанавливаем выбор по нему.
     */
    private void rebuildCombo() {
        lblTitle.setText("  " + I18n.t("menu.views"));
        btnLoad.setText(I18n.t("btn.refresh"));
        btnExport.setText(I18n.t("btn.export"));
        refreshCombo();
        // Восстанавливаем выбор по сохранённому ключу
        if (selectedKey != null) {
            String newLabel = I18n.t(selectedKey);
            combo.setSelectedItem(newLabel);
        }
    }

    /**
     * Заполнить combo локализованными названиями текущего языка.
     */
    private void refreshCombo() {
        // Временно снимаем listener чтобы не сбить selectedKey
        combo.removeAllItems();

        Map<String, String> views = ViewDAO.getLocalizedViews();
        for (String label : views.keySet()) {
            combo.addItem(label);
        }

        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
            // Если selectedKey ещё не задан — берём первый
            if (selectedKey == null) {
                String firstLabel = (String) combo.getItemAt(0);
                selectedKey = views.get(firstLabel);
            }
        }
    }

    private void loadView() {
        String label = (String) combo.getSelectedItem();
        if (label == null) return;

        // Обновляем selectedKey
        String key = ViewDAO.getLocalizedViews().get(label);
        if (key != null) selectedKey = key;

        try {
            DefaultTableModel m = (selectedKey != null)
                    ? viewDAO.executeViewByKey(selectedKey)
                    : viewDAO.executeView(label);

            model.setDataVector(toData(m), toColumns(m));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Ошибка VIEW",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private Object[][] toData(DefaultTableModel m) {
        Object[][] data = new Object[m.getRowCount()][m.getColumnCount()];
        for (int r = 0; r < m.getRowCount(); r++)
            for (int c = 0; c < m.getColumnCount(); c++)
                data[r][c] = m.getValueAt(r, c);
        return data;
    }

    private Object[] toColumns(DefaultTableModel m) {
        Object[] cols = new Object[m.getColumnCount()];
        for (int i = 0; i < m.getColumnCount(); i++)
            cols[i] = m.getColumnName(i);
        return cols;
    }
}