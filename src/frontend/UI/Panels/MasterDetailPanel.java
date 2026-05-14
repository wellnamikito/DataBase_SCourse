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
 * Master-Detail панель.
 *
 * ИСПРАВЛЕНИЕ: VideoDAO.getAllForMaster() возвращает таблицу
 * где ПЕРВАЯ колонка = VideoID (скрытая, но нужна для Detail).
 * Колонка VideoID скрывается через setMinWidth(0) + setMaxWidth(0).
 */
public class MasterDetailPanel extends JPanel {

    private final VideoDAO    videoDAO    = new VideoDAO();
    private final CassetteDAO cassetteDAO = new CassetteDAO();

    private final TablePanel masterPanel;
    private final TablePanel detailPanel;

    public MasterDetailPanel() {
        setLayout(new BorderLayout());

        masterPanel = new TablePanel("panel.master", true);
        detailPanel = new TablePanel("panel.detail", true);

        // При выборе строки в master — загрузить кассеты
        masterPanel.getTable().getSelectionModel().addListSelectionListener(
                (ListSelectionEvent e) -> {
                    if (!e.getValueIsAdjusting()) refreshDetail();
                }
        );

        // CRUD Master (Video)
        masterPanel.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                VideoEditDialog dlg = new VideoEditDialog(
                        SwingUtilities.getWindowAncestor(MasterDetailPanel.this), null);
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshMaster();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { msg("Выберите строку"); return; }
                int id = getSelectedVideoId();
                if (id < 0) { msg("Не удалось получить ID видеосалона"); return; }
                try {
                    Video v = videoDAO.getById(id);
                    VideoEditDialog dlg = new VideoEditDialog(
                            SwingUtilities.getWindowAncestor(MasterDetailPanel.this), v);
                    dlg.setVisible(true);
                    if (dlg.isSaved()) refreshMaster();
                } catch (SQLException ex) { err(ex); }
            }
            @Override public void onDelete(int row) {
                if (row < 0) { msg("Выберите строку"); return; }
                int id = getSelectedVideoId();
                if (id < 0) return;
                if (JOptionPane.showConfirmDialog(null,
                        "Удалить видеосалон ID=" + id + "?", "Удаление",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try { videoDAO.delete(id); refreshMaster(); }
                    catch (SQLException ex) { err(ex); }
                }
            }
            @Override public void onRefresh() { refreshMaster(); }
        });

        // CRUD Detail (Cassette)
        detailPanel.setCrudListener(new TablePanel.CrudListener() {
            @Override public void onAdd() {
                int videoId = getSelectedVideoId();
                if (videoId < 0) { msg("Сначала выберите видеосалон в верхней таблице"); return; }
                CassetteEditDialog dlg = new CassetteEditDialog(
                        SwingUtilities.getWindowAncestor(MasterDetailPanel.this), null, videoId);
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshDetail();
            }
            @Override public void onEdit(int row) {
                if (row < 0) { msg("Выберите кассету"); return; }
                Object idObj = detailPanel.getSelectedValue(0);
                if (!(idObj instanceof Number)) { msg("Не удалось получить ID кассеты"); return; }
                int casId = ((Number) idObj).intValue();
                int videoId = getSelectedVideoId();
                CassetteEditDialog dlg = new CassetteEditDialog(
                        SwingUtilities.getWindowAncestor(MasterDetailPanel.this), casId, videoId);
                dlg.setVisible(true);
                if (dlg.isSaved()) refreshDetail();
            }
            @Override public void onDelete(int row) {
                if (row < 0) { msg("Выберите кассету"); return; }
                Object idObj = detailPanel.getSelectedValue(0);
                if (!(idObj instanceof Number)) return;
                int id = ((Number) idObj).intValue();
                if (JOptionPane.showConfirmDialog(null,
                        "Удалить кассету ID=" + id + "?", "Удаление",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try { cassetteDAO.delete(id); refreshDetail(); }
                    catch (SQLException ex) { err(ex); }
                }
            }
            @Override public void onRefresh() { refreshDetail(); }
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, masterPanel, detailPanel);
        split.setResizeWeight(0.5);
        split.setDividerLocation(300);

        add(split, BorderLayout.CENTER);
        refreshMaster();
    }

    private void refreshMaster() {
        try {
            javax.swing.table.DefaultTableModel model = videoDAO.getAllWithId();
            masterPanel.loadData(model);
            // Скрыть первую колонку (VideoID) — она нужна только для Detail
            hideColumn(0);
        } catch (SQLException e) { err(e); }
    }

    /**
     * Скрыть колонку по индексу в JTable (данные остаются, колонка не видна).
     */
    private void hideColumn(int colIndex) {
        JTable table = masterPanel.getTable();
        if (table.getColumnCount() <= colIndex) return;
        table.getColumnModel().getColumn(colIndex).setMinWidth(0);
        table.getColumnModel().getColumn(colIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(colIndex).setWidth(0);
        table.getColumnModel().getColumn(colIndex).setPreferredWidth(0);
    }

    private void refreshDetail() {
        int videoId = getSelectedVideoId();
        if (videoId < 0) {
            detailPanel.loadData(new javax.swing.table.DefaultTableModel());
            return;
        }
        try {
            detailPanel.loadData(cassetteDAO.getByVideoId(videoId));
        } catch (SQLException e) { err(e); }
    }

    /**
     * Получить VideoID из скрытой первой колонки выбранной строки Master.
     * Возвращает -1 если ничего не выбрано или ID не число.
     */
    private int getSelectedVideoId() {
        int modelRow = masterPanel.getSelectedModelRow();
        if (modelRow < 0) return -1;
        Object val = masterPanel.getTableModel().getValueAt(modelRow, 0);
        if (val instanceof Number) return ((Number) val).intValue();
        // Попробовать парсить строку
        try { return Integer.parseInt(val.toString()); }
        catch (Exception e) { return -1; }
    }

    private void msg(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private void err(Exception e) {
        JOptionPane.showMessageDialog(this,
                "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }


}