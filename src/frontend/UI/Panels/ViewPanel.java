package frontend.UI.Panels;

import backend.dao.VideoDAO;
import backend.dao.ViewDAO;
import backend.util.ExcelExporter;
import backend.util.I18n;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;

public class ViewPanel extends JPanel {

    private final ViewDAO  viewDAO  = new ViewDAO();
    private final VideoDAO videoDAO = new VideoDAO();

    private JComboBox<String> combo;
    private JLabel            lblTitle;
    private JLabel            lblInfo;
    private JButton           btnLoad;
    private JButton           btnExport;
    private JButton           btnEdit;
    private JTable            table;
    private DefaultTableModel model;

    private String selectedKey = null;

    // Ключ редактируемого VIEW
    private static final String EDITABLE_KEY = "view.editable";

    public ViewPanel() {
        setLayout(new BorderLayout(8, 8));

        // ── Заголовок ──────────────────────────────────────────────
        lblTitle = new JLabel("  " + I18n.t("menu.views"));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblInfo = new JLabel(" ");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(120, 120, 120));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 12, 2, 8));

        // ── Кнопки ─────────────────────────────────────────────────
        combo   = new JComboBox<>();
        btnLoad = new JButton(I18n.t("btn.refresh"));

        btnExport = new JButton(I18n.t("btn.export"));
        btnExport.setBackground(new Color(34, 139, 34));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.setBorderPainted(false);
        btnExport.setOpaque(true);
        btnExport.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> exportToExcel());

        btnEdit = new JButton(I18n.t("btn.edit_view"));
        btnEdit.setBackground(new Color(30, 100, 200));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.setBorderPainted(false);
        btnEdit.setOpaque(true);
        btnEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEdit.setVisible(false);
        btnEdit.addActionListener(e -> editViaView());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnEdit);
        btnRow.add(btnLoad);
        btnRow.add(btnExport);

        JPanel top = new JPanel(new BorderLayout(6, 4));
        top.add(combo,  BorderLayout.CENTER);
        top.add(btnRow, BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout());
        north.add(lblTitle, BorderLayout.NORTH);
        north.add(lblInfo,  BorderLayout.CENTER);
        north.add(top,      BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);

        // ── Таблица ────────────────────────────────────────────────
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setRowHeight(24);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────────────
        btnLoad.addActionListener(e -> loadView());

        combo.addActionListener(e -> {
            String label = (String) combo.getSelectedItem();
            if (label != null) {
                selectedKey = ViewDAO.getLocalizedViews().get(label);
                updateEditButton();
                loadView();
            }
        });

        I18n.addListener(this::rebuildCombo);

        refreshCombo();
        loadView();
    }

    // ── Показать/скрыть кнопку редактирования ──────────────────────

    private void updateEditButton() {
        boolean editable = EDITABLE_KEY.equals(selectedKey);
        btnEdit.setVisible(editable);
        lblInfo.setText(editable
                ? "  ✏️ " + I18n.t("view.simple.info_short")
                : " ");
    }

    // ── Экспорт в Excel ────────────────────────────────────────────

    private void exportToExcel() {
        String title = selectedKey != null ? I18n.t(selectedKey) : I18n.t("menu.views");
        ExcelExporter.exportWithDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                model,
                title
        );
    }

    // ── Редактирование через VIEW (триггер) ────────────────────────

    private void editViaView() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, I18n.t("msg.select_row"),
                    I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        // Колонки в модели: 0=VideoID (скрыта), 1=Caption, 2=Address
        Object idVal      = model.getValueAt(modelRow, 0);
        Object captionVal = model.getValueAt(modelRow, 1);
        Object addressVal = model.getValueAt(modelRow, 2);

        if (!(idVal instanceof Number)) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка: VideoID не найден", I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        int videoId = ((Number) idVal).intValue();

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JTextField fCaption = new JTextField(captionVal != null ? captionVal.toString() : "", 22);
        JTextField fAddress = new JTextField(addressVal != null ? addressVal.toString() : "", 22);
        form.add(new JLabel(I18n.t("f.caption")));
        form.add(fCaption);
        form.add(new JLabel(I18n.t("f.address")));
        form.add(fAddress);

        int result = JOptionPane.showConfirmDialog(
                this, form,
                "vw_video_edit — VideoID=" + videoId,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                videoDAO.updateViaView(videoId, fCaption.getText().trim(), fAddress.getText().trim());
                JOptionPane.showMessageDialog(this,
                        I18n.t("view.simple.saved"),
                        I18n.t("menu.views"), JOptionPane.INFORMATION_MESSAGE);
                loadView();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        I18n.t("msg.db_error") + "\n" + ex.getMessage(),
                        I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // ── Загрузка VIEW ──────────────────────────────────────────────

    private void loadView() {
        String label = (String) combo.getSelectedItem();
        if (label == null) return;

        String key = ViewDAO.getLocalizedViews().get(label);
        if (key != null) selectedKey = key;

        try {
            DefaultTableModel m;

            if (EDITABLE_KEY.equals(selectedKey)) {
                // Грузим с VideoID (колонка 0 будет скрыта)
                m = videoDAO.getSimpleView();
            } else {
                m = selectedKey != null
                        ? viewDAO.executeViewByKey(selectedKey)
                        : viewDAO.executeView(label);
            }

            model.setDataVector(toData(m), toColumns(m));

            // Скрыть колонку VideoID для редактируемого VIEW
            if (EDITABLE_KEY.equals(selectedKey)) {
                SwingUtilities.invokeLater(() -> hideColumn(0));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Ошибка VIEW", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Скрыть колонку (данные остаются в модели) ──────────────────

    private void hideColumn(int colIndex) {
        if (table.getColumnCount() <= colIndex) return;
        var col = table.getColumnModel().getColumn(colIndex);
        col.setMinWidth(0);
        col.setMaxWidth(0);
        col.setWidth(0);
        col.setPreferredWidth(0);
    }

    // ── Перестройка combo при смене языка ──────────────────────────

    private void rebuildCombo() {
        lblTitle.setText("  " + I18n.t("menu.views"));
        btnLoad.setText(I18n.t("btn.refresh"));
        btnExport.setText(I18n.t("btn.export"));
        btnEdit.setText(I18n.t("btn.edit_view"));
        refreshCombo();
        if (selectedKey != null) {
            combo.setSelectedItem(I18n.t(selectedKey));
        }
        updateEditButton();
    }

    private void refreshCombo() {
        combo.removeAllItems();
        Map<String, String> views = ViewDAO.getLocalizedViews();
        for (String label : views.keySet()) {
            combo.addItem(label);
        }
        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
            if (selectedKey == null) {
                selectedKey = views.get(combo.getItemAt(0));
            }
        }
    }

    // ── Утилиты ────────────────────────────────────────────────────

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