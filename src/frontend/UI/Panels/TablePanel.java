package frontend.UI.Panels;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Универсальная панель с JTable, строкой поиска и кнопками CRUID.
 * Используется для отображение любой таблицы/представления
 */
public class TablePanel extends JPanel {
    protected JTable table;
    protected DefaultTableModel tableModel;
    protected TableRowSorter<DefaultTableModel> sorter;
    protected JTextField searchField;

    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;

    // Listener для CRUD и обновления
    public interface CrudListener{
        void onAdd();
        void onEdit(int selectedRow);
        void onDelete(int selectedRow);
        void onRefresh();
    }

    private CrudListener crudListener;

    public TablePanel(String title, boolean showCrud){
        setLayout(new BorderLayout(0,0));

        // Заголовок
        JLabel lblTitle = new JLabel(" " + title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(8,8,4,8));

        // Панель поиска
        JPanel topPanel = new JPanel(new BorderLayout(8,0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        topPanel.add(new JLabel("🔍 Поиск:"), BorderLayout.WEST);
        searchField = new JTextField();
        searchField.setToolTipText("Введите текст для фильтрации");
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { applyFilter(); }
        });
        topPanel.add(searchField, BorderLayout.CENTER);

        JButton btnClear = new JButton("✕");
        btnClear.setToolTipText("Очистить поиск");
        btnClear.addActionListener(e -> {searchField.setText(""); applyFilter();});
        topPanel.add(btnClear, BorderLayout.EAST);


        // === Таблица ===
        tableModel = new DefaultTableModel() {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        // === Кнопки CRUD ===
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        if (showCrud) {
            btnAdd     = createBtn("➕ Добавить",   new Color(34, 139, 34));
            btnEdit    = createBtn("✏️ Редактировать", new Color(30, 100, 200));
            btnDelete  = createBtn("🗑️ Удалить",    new Color(180, 30, 30));
            btnRefresh = createBtn("🔄 Обновить",   new Color(90, 90, 90));

            btnAdd.addActionListener(e  -> { if (crudListener != null) crudListener.onAdd(); });
            btnEdit.addActionListener(e -> { if (crudListener != null) crudListener.onEdit(getSelectedModelRow()); });
            btnDelete.addActionListener(e -> { if (crudListener != null) crudListener.onDelete(getSelectedModelRow()); });
            btnRefresh.addActionListener(e -> { if (crudListener != null) crudListener.onRefresh(); });

            btnPanel.add(btnAdd);
            btnPanel.add(btnEdit);
            btnPanel.add(btnDelete);
            btnPanel.add(btnRefresh);
        }

        // === Компоновка ===
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(lblTitle, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        if (showCrud) add(btnPanel, BorderLayout.SOUTH);
    }

    private JButton createBtn(String text, Color color){
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return btn;
    }

    // Применить фильтр по тексту поиска ко всем колонкам
    protected void applyFilter(){
        String text = searchField.getText().trim();
        if(text.isEmpty()){
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    // Загрузить данные в таблицу
    public void loadData(DefaultTableModel model) {
        tableModel.setDataVector(
                getDataVector(model),
                getColumnNames(model)
        );
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        applyFilter();
    }

    private Object[][] getDataVector(DefaultTableModel m) {
        int rows = m.getRowCount(), cols = m.getColumnCount();
        Object[][] data = new Object[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                data[r][c] = m.getValueAt(r, c);
        return data;
    }

    private Object[] getColumnNames(DefaultTableModel m){
        int cols = m.getColumnCount();
        Object[] names = new Object[cols];
        for (int i = 0; i < cols; i++) {
            names[i] = m.getColumnName(i);
        }
        return names;
    }

    // Получить строку в model-координатах
    //(с учетом сортировки или фильтрации)
    public int getSelectedModelRow(){
        int viewRow = table.getSelectedRow();
        if(viewRow < 0) return -1;
        return table.convertRowIndexToModel(viewRow);
    }

    // Получить значение ячейки из выбранной строки
    public Object getSelectedValue(int column){
        int row = getSelectedModelRow();
        if ( row < 0) return null;
        return tableModel.getValueAt(row, column);
    }

    public void setCrudListener(CrudListener listener) { this.crudListener = listener; }
    public JTable getTable()                            { return table; }
    public DefaultTableModel getTableModel()            { return tableModel; }
}
