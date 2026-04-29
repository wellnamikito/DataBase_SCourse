package frontend.UI.Panels;

import backend.dao.VideoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * Панель для работы с обновляемым VIEW vw_video_simple.
 * Редактирование производится напрямую через VIEW,
 * что активирует триггер trg_update_video.
 */
public class ViewSimplePanel extends JPanel {

    private final VideoDAO videoDAO = new VideoDAO();
    private final TablePanel tablePanel;

    public ViewSimplePanel() {
        setLayout(new BorderLayout());

        JLabel info = new JLabel(
                "<html><b>📝 Обновляемое представление vw_video_simple</b><br>" +
                        "<i>Изменения записываются через триггер trg_update_video в таблицу Video</i></html>"
        );
        info.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 8));

        tablePanel = new TablePanel("vw_video_simple (VideoID, Caption, Address)", false);

        // Кнопки
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnEdit = new JButton("✏️ Редактировать через VIEW (триггер)");
        btnEdit.setBackground(new Color(30, 100, 200));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(e -> editViaView());

        JButton btnRefresh = new JButton("🔄 Обновить");
        btnRefresh.addActionListener(e -> refresh());

        btnPanel.add(btnEdit);
        btnPanel.add(btnRefresh);

        add(info, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        refresh();
    }

    private void refresh() {
        try {
            tablePanel.loadData(videoDAO.getSimpleView());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
    }

    /** Редактировать запись через VIEW (вызывает триггер) */
    private void editViaView() {
        int row = tablePanel.getSelectedModelRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Выберите строку"); return; }

        Object idVal      = tablePanel.getSelectedValue(0);
        Object captionVal = tablePanel.getSelectedValue(1);
        Object addressVal = tablePanel.getSelectedValue(2);

        if (!(idVal instanceof Integer)) { JOptionPane.showMessageDialog(this, "Ошибка: ID не найден"); return; }
        int videoId = (Integer) idVal;

        // Диалог редактирования
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        JTextField fCaption = new JTextField(captionVal != null ? captionVal.toString() : "");
        JTextField fAddress = new JTextField(addressVal != null ? addressVal.toString() : "");
        form.add(new JLabel("Название:"));
        form.add(fCaption);
        form.add(new JLabel("Адрес:"));
        form.add(fAddress);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Редактировать через vw_video_simple (ID=" + videoId + ")",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                videoDAO.updateViaView(videoId, fCaption.getText(), fAddress.getText());
                JOptionPane.showMessageDialog(this, "✅ Обновлено через VIEW (триггер выполнен)");
                refresh();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "SQL Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}