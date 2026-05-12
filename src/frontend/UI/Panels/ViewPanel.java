package frontend.UI.Panels;

import backend.dao.ViewDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class ViewPanel extends JPanel {

    private final ViewDAO viewDAO = new ViewDAO();

    private JComboBox<String> combo;
    private JTable table;
    private DefaultTableModel model;

    public ViewPanel() {

        setLayout(new BorderLayout(8, 8));

        combo = new JComboBox<>();

        JButton btnLoad = new JButton("Открыть VIEW");

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(combo, BorderLayout.CENTER);
        top.add(btnLoad, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);

        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> loadView());

        refreshViews();   // 👈 ВАЖНО: сначала заполняем combo
        loadView();       // 👈 потом грузим
    }

    private void refreshViews() {
        combo.removeAllItems();

        for (String key : ViewDAO.getViews().keySet()) {
            combo.addItem(key);
        }

        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private void loadView() {

        String selected = (String) combo.getSelectedItem();
        if (selected == null) return;

        try {
            DefaultTableModel m = viewDAO.executeView(selected);

            model.setDataVector(
                    toData(m),
                    toColumns(m)
            );

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