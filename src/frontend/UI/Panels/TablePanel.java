package frontend.UI.Panels;
import backend.util.DarkTheme;
import backend.util.I18n;
import backend.util.ThemeManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Универсальная панель с JTable, поиском и CRUD кнопками.
 *
 * ИСПРАВЛЕНИЕ: titleKey теперь может быть как ключом i18n (содержит точку),
 * так и прямым текстом. Так решается проблема "panel.owners" в заголовке.
 */
public class TablePanel extends JPanel {

    protected JTable table;
    protected DefaultTableModel tableModel;
    protected TableRowSorter<DefaultTableModel> sorter;
    protected JTextField searchField;

    private final JLabel lblTitle;
    private final JLabel lblSearch;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnClear;

    // Хранить ключ или прямой текст
    private final String titleKey;
    private final boolean showCrud;

    // Ссылки на панели для перекраски
    private JPanel northPanel, searchPanel, btnPanel;
    private JScrollPane scrollPane;

    public interface CrudListener {
        void onAdd();
        void onEdit(int selectedRow);
        void onDelete(int selectedRow);
        void onRefresh();
    }

    private CrudListener crudListener;

    public TablePanel(String titleKey, boolean showCrud) {
        this.titleKey  = titleKey;
        this.showCrud  = showCrud;
        setLayout(new BorderLayout(0, 0));

        // === Заголовок ===
        lblTitle = new JLabel("  " + resolveTitle(), SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setOpaque(true);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        // === Поиск ===
        searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        lblSearch = new JLabel(I18n.t("search.label"));
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(lblSearch, BorderLayout.WEST);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        searchPanel.add(searchField, BorderLayout.CENTER);

        btnClear = new JButton("✕");
        btnClear.setPreferredSize(new Dimension(32, 28));
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setOpaque(true);
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(e -> { searchField.setText(""); applyFilter(); });
        searchPanel.add(btnClear, BorderLayout.EAST);

        // === Таблица ===
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        scrollPane = new JScrollPane(table);

        // === Кнопки CRUD ===
        btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));

        if (showCrud) {
            btnAdd     = makeBtn(I18n.t("btn.add"),     ThemeManager.btnAdd());
            btnEdit    = makeBtn(I18n.t("btn.edit"),    ThemeManager.btnEdit());
            btnDelete  = makeBtn(I18n.t("btn.delete"),  ThemeManager.btnDelete());
            btnRefresh = makeBtn(I18n.t("btn.refresh"), ThemeManager.btnRefresh());

            btnAdd.addActionListener(e     -> { if (crudListener != null) crudListener.onAdd(); });
            btnEdit.addActionListener(e    -> { if (crudListener != null) crudListener.onEdit(getSelectedModelRow()); });
            btnDelete.addActionListener(e  -> { if (crudListener != null) crudListener.onDelete(getSelectedModelRow()); });
            btnRefresh.addActionListener(e -> { if (crudListener != null) crudListener.onRefresh(); });

            btnPanel.add(btnAdd);
            btnPanel.add(btnEdit);
            btnPanel.add(btnDelete);
            btnPanel.add(btnRefresh);
        }

        // === Сборка ===
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(lblTitle,    BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        if (showCrud) add(btnPanel, BorderLayout.SOUTH);

        // Применить тему сразу
        applyTheme();

        // Слушатели
        ThemeManager.getInstance().addListener(this::applyTheme);
        I18n.addListener(this::updateTexts);
    }

    /** Разрешить заголовок: если есть точка — это i18n ключ, иначе прямой текст */
    private String resolveTitle() {
        String resolved = I18n.t(titleKey);
        // I18n возвращает "[key]" если ключ не найден
        if (resolved.startsWith("[") && resolved.endsWith("]")) return titleKey;
        return resolved;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    public void applyTheme() {
        boolean dark   = ThemeManager.getInstance().isDark();
        Color bgPanel  = ThemeManager.bgPanel();
        Color bgHeader = ThemeManager.bgHeader();
        Color bgTable  = dark ? new Color(33, 33, 33) : Color.WHITE;
        Color fgText   = ThemeManager.fgText();
        Color fgDim    = ThemeManager.fgDim();
        Color border   = ThemeManager.borderColor();
        Color bgComp   = ThemeManager.bgComponent();
        Color bgSelected = ThemeManager.bgSelected();

        setBackground(bgPanel);

        // Заголовок
        lblTitle.setBackground(bgHeader);
        lblTitle.setForeground(fgText);
        lblTitle.setText("  " + resolveTitle());

        // Поиск
        searchPanel.setBackground(bgPanel);
        lblSearch.setForeground(fgDim);
        searchField.setBackground(bgComp);
        searchField.setForeground(fgText);
        searchField.setCaretColor(fgText);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        btnClear.setBackground(ThemeManager.btnRefresh());
        btnClear.setForeground(Color.WHITE);

        // North panel
        northPanel.setBackground(bgPanel);

        // Таблица
        table.setBackground(bgTable);
        table.setForeground(fgText);
        table.setSelectionBackground(bgSelected);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(border);
        table.getTableHeader().setBackground(bgHeader);
        table.getTableHeader().setForeground(fgText);
        table.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, border));

        // ScrollPane — убрать белые рамки
        scrollPane.setBorder(BorderFactory.createLineBorder(border));
        scrollPane.setBackground(bgPanel);
        scrollPane.getViewport().setBackground(bgTable);

        // Кнопочная панель
        btnPanel.setBackground(bgPanel);
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, border));

        if (showCrud && btnAdd != null) {
            btnAdd.setBackground(ThemeManager.btnAdd());
            btnEdit.setBackground(ThemeManager.btnEdit());
            btnDelete.setBackground(ThemeManager.btnDelete());
            btnRefresh.setBackground(ThemeManager.btnRefresh());
        }

        repaint();
        revalidate();
    }

    private void updateTexts() {
        lblTitle.setText("  " + resolveTitle());
        lblSearch.setText(I18n.t("search.label"));
        if (showCrud && btnAdd != null) {
            btnAdd.setText(I18n.t("btn.add"));
            btnEdit.setText(I18n.t("btn.edit"));
            btnDelete.setText(I18n.t("btn.delete"));
            btnRefresh.setText(I18n.t("btn.refresh"));
        }
    }

    public void applyFilter() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
    }

    public void loadData(DefaultTableModel model) {
        int cols = model.getColumnCount();
        int rows = model.getRowCount();
        Object[] colNames = new Object[cols];
        for (int c = 0; c < cols; c++) colNames[c] = model.getColumnName(c);
        Object[][] data = new Object[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                data[r][c] = model.getValueAt(r, c);
        tableModel.setDataVector(data, colNames);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        applyFilter();
    }

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

    public void setCrudListener(CrudListener l) { this.crudListener = l; }
    public JTable getTable()                     { return table; }
    public DefaultTableModel getTableModel()     { return tableModel; }
}