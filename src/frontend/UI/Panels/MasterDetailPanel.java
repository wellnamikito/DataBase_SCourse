package frontend.UI.Panels;

import backend.dao.CassetteDAO;
import backend.dao.VideoDAO;
import backend.model.Video;
import frontend.UI.Dialogs.CassetteEditDialog;
import frontend.UI.Dialogs.VideoEditDialog;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.sql.SQLException;

/**
 * Master-Detail панель:
 * - Верхняя часть: список Видеосалонов (Video)
 * - Нижняя часть: Кассеты выбранного видеосалона (Cassette)
 */
public class MasterDetailPanel extends JPanel {

    private final VideoDAO videoDAO = new VideoDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();

    private final TablePanel masterPanel;
    private final TablePanel detailPanel;

    public MasterDetailPanel() {
        setLayout(new BorderLayout());

        masterPanel = new TablePanel("📼 Видеосалоны (Master)", true);
        detailPanel = new TablePanel("🎞️ Кассеты выбранного видеосалона (Detail)", true);

        // При выборе видеосалона — обновить кассеты
        masterPanel.getTable().getSelectionModel().addListSelectionListener(
                (ListSelectionEvent e) -> {
                    if (!e.getValueIsAdjusting()) refreshDetail();
                }
        );

        // CRUD для Master (Video)
        masterPanel.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                VideoEditDialog dlg = new VideoEditDialog(SwingUtilities.getWindowAncestor(MasterDetailPanel.this), null);
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshMaster();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { JOptionPane.showMessageDialog(null, "Выберите строку"); return; }
                int id = (int) masterPanel.getSelectedValue(0);
                try {
                    Video v = videoDAO.getById(id);
                    VideoEditDialog dlg = new VideoEditDialog(SwingUtilities.getWindowAncestor(MasterDetailPanel.this), v);
                    dlg.setVisible(true);
                    if (dlg.isSaved()) refreshMaster();
                } catch (SQLException ex) { showError(ex); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { JOptionPane.showMessageDialog(null, "Выберите строку"); return; }
                int id = (int) masterPanel.getSelectedValue(0);
                if (JOptionPane.showConfirmDialog(null, "Удалить видеосалон ID=" + id + "?", "Удаление",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try { videoDAO.delete(id); refreshMaster(); } catch (SQLException ex) { showError(ex); }
                }
            }
            @Override public void onRefresh() { refreshMaster(); }
        });

        // CRUD для Detail (Cassette)
        detailPanel.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                int videoId = getSelectedVideoId();
                if (videoId < 0) { JOptionPane.showMessageDialog(null, "Сначала выберите видеосалон"); return; }
                CassetteEditDialog dlg = new CassetteEditDialog(SwingUtilities.getWindowAncestor(MasterDetailPanel.this), null, videoId);
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshDetail();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { JOptionPane.showMessageDialog(null, "Выберите кассету"); return; }
                int casId = (int) detailPanel.getSelectedValue(0);
                CassetteEditDialog dlg = new CassetteEditDialog(SwingUtilities.getWindowAncestor(MasterDetailPanel.this), casId, getSelectedVideoId());
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshDetail();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { JOptionPane.showMessageDialog(null, "Выберите кассету"); return; }
                int id = (int) detailPanel.getSelectedValue(0);
                if (JOptionPane.showConfirmDialog(null, "Удалить кассету ID=" + id + "?", "Удаление",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try { cassetteDAO.delete(id); refreshDetail(); } catch (SQLException ex) { showError(ex); }
                }
            }
            @Override public void onRefresh() { refreshDetail(); }
        });

        // Разделитель
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);

        refreshMaster();
    }

    private void refreshMaster() {
        try {
            masterPanel.loadData(videoDAO.getAll());
        } catch (SQLException e) { showError(e); }
    }

    private void refreshDetail() {
        int videoId = getSelectedVideoId();
        if (videoId < 0) {
            detailPanel.loadData(new javax.swing.table.DefaultTableModel());
            return;
        }
        try {
            detailPanel.loadData(cassetteDAO.getByVideoId(videoId));
        } catch (SQLException e) { showError(e); }
    }

    private int getSelectedVideoId() {
        Object val = masterPanel.getSelectedValue(0);
        return (val instanceof Integer) ? (Integer) val : -1;
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}