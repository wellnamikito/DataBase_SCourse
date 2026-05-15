package frontend.UI.Panels;

import backend.util.I18n;
import backend.util.ThemeManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TablePanel extends JPanel {

    protected JTable table;
    protected DefaultTableModel tableModel;
    protected TableRowSorter<DefaultTableModel> sorter;
    protected JTextField searchField;

    private final JLabel lblTitle;
    private final JLabel lblSearch;

    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnClear;

    private final String titleKey;
    private final boolean showCrud;

    private JPanel northPanel, searchPanel, btnPanel;
    private JScrollPane scrollPane;

    private CrudListener crudListener;

    private boolean crudEnabled = true;

    public interface CrudListener {
        void onAdd();
        void onEdit(int selectedRow);
        void onDelete(int selectedRow);
        void onRefresh();
    }

    public TablePanel(String titleKey, boolean showCrud) {
        this.titleKey = titleKey;
        this.showCrud = showCrud;

        setLayout(new BorderLayout());

        // ===== TITLE =====
        lblTitle = new JLabel("  " + resolveTitle());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setOpaque(true);

        // ===== SEARCH =====
        searchPanel = new JPanel(new BorderLayout(6, 6));

        lblSearch = new JLabel(I18n.t("search.label"));
        searchField = new JTextField();

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilter();
            }
        });

        btnClear = new JButton(I18n.t("btn.clear_search"));
        btnClear.addActionListener(e -> {
            searchField.setText("");
            applyFilter();
        });

        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnClear, BorderLayout.EAST);

        // ===== TABLE =====
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        scrollPane = new JScrollPane(table);

        // ===== CRUD BUTTONS =====
        btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        if (showCrud) {
            btnAdd     = new JButton(I18n.t("btn.add"));
            btnEdit    = new JButton(I18n.t("btn.edit"));
            btnDelete  = new JButton(I18n.t("btn.delete"));
            btnRefresh = new JButton(I18n.t("btn.refresh"));

            btnAdd.addActionListener(e -> {
                if (!crudEnabled) { warn(); return; }
                if (crudListener != null) crudListener.onAdd();
            });

            btnEdit.addActionListener(e -> {
                if (!crudEnabled) { warn(); return; }
                if (crudListener != null) crudListener.onEdit(getSelectedModelRow());
            });

            btnDelete.addActionListener(e -> {
                if (!crudEnabled) { warn(); return; }
                if (crudListener != null) crudListener.onDelete(getSelectedModelRow());
            });

            btnRefresh.addActionListener(e -> {
                if (crudListener != null) crudListener.onRefresh();
            });

            btnPanel.add(btnAdd);
            btnPanel.add(btnEdit);
            btnPanel.add(btnDelete);
            btnPanel.add(btnRefresh);
        }

        // ===== LAYOUT =====
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(lblTitle,    BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        if (showCrud) add(btnPanel, BorderLayout.SOUTH);

        applyTheme();

        // ===== СЛУШАТЕЛЬ СМЕНЫ ЯЗЫКА =====
        I18n.addListener(this::updateTexts);
    }

    // ══════════════════════════════════════════════════
    //  ОБНОВЛЕНИЕ ТЕКСТОВ ПРИ СМЕНЕ ЯЗЫКА
    // ══════════════════════════════════════════════════

    private void updateTexts() {
        lblTitle.setText("  " + resolveTitle());
        lblSearch.setText(I18n.t("search.label"));
        btnClear.setText(I18n.t("btn.clear_search"));

        if (showCrud) {
            btnAdd.setText(I18n.t("btn.add"));
            btnEdit.setText(I18n.t("btn.edit"));
            btnDelete.setText(I18n.t("btn.delete"));
            btnRefresh.setText(I18n.t("btn.refresh"));
        }

        revalidate();
        repaint();
    }

    // ══════════════════════════════════════════════════
    //  ПРЕДУПРЕЖДЕНИЕ (режим только просмотра)
    // ══════════════════════════════════════════════════

    private void warn() {
        JOptionPane.showMessageDialog(this,
                I18n.t("msg.access_denied"),
                I18n.t("msg.warning"),
                JOptionPane.WARNING_MESSAGE);
    }

    private String resolveTitle() {
        String t = I18n.t(titleKey);
        return (t.startsWith("[") ? titleKey : t);
    }

    // ══════════════════════════════════════════════════
    //  ТЕМА
    // ══════════════════════════════════════════════════

    public void applyTheme() {
        setBackground(ThemeManager.bgPanel());

        lblTitle.setBackground(ThemeManager.bgHeader());
        lblTitle.setForeground(ThemeManager.fgText());

        searchPanel.setBackground(ThemeManager.bgPanel());
        searchField.setBackground(ThemeManager.bgComponent());
        searchField.setForeground(ThemeManager.fgText());

        table.setBackground(ThemeManager.bgPanel());
        table.setForeground(ThemeManager.fgText());
        table.setSelectionBackground(ThemeManager.bgSelected());
    }

    // ══════════════════════════════════════════════════
    //  ФИЛЬТР
    // ══════════════════════════════════════════════════

    public void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // ══════════════════════════════════════════════════
    //  ДАННЫЕ
    // ══════════════════════════════════════════════════

    public void loadData(DefaultTableModel model) {
        tableModel.setDataVector(
                toData(model),
                toColumns(model)
        );
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

    // ══════════════════════════════════════════════════
    //  ВЫБОР СТРОКИ
    // ══════════════════════════════════════════════════

    public int getSelectedModelRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return -1;
        return table.convertRowIndexToModel(viewRow);
    }

    public Object getSelectedValue(int column) {
        int row = getSelectedModelRow();
        if (row < 0) return null;
        return tableModel.getValueAt(row, column);
    }

    public JTable getTable() { return table; }

    public DefaultTableModel getTableModel() { return tableModel; }

    public void setCrudListener(CrudListener l) { this.crudListener = l; }

    // ══════════════════════════════════════════════════
    //  РОЛИ
    // ══════════════════════════════════════════════════

    public void setCrudEnabled(boolean enabled) {
        this.crudEnabled = enabled;

        if (!showCrud) return;

        if (btnPanel != null)   btnPanel.setVisible(enabled);
        if (btnAdd != null)     btnAdd.setEnabled(enabled);
        if (btnEdit != null)    btnEdit.setEnabled(enabled);
        if (btnDelete != null)  btnDelete.setEnabled(enabled);
        if (btnRefresh != null) btnRefresh.setEnabled(true); // Refresh всегда доступен

        revalidate();
        repaint();
    }
}