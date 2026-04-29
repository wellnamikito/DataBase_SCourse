package frontend.UI.Dialogs;

import backend.dao.CassetteDAO;
import backend.dao.FilmDAO;
import backend.dao.SimpleDAO;
import backend.model.Cassette;
import backend.model.Film;
import backend.model.Quality;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Диалог добавления/редактирования кассеты */
public class CassetteEditDialog extends JDialog {

    private boolean saved = false;
    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final FilmDAO filmDAO = new FilmDAO();
    private final SimpleDAO simpleDAO = new SimpleDAO();

    private final int videoId;
    private final Integer editCassetteId; // null = добавление

    private JComboBox<String> filmCombo;
    private JComboBox<String> qualityCombo;
    private JCheckBox cbDemand;
    private List<Film> films;
    private List<Quality> qualities;

    public CassetteEditDialog(Window parent, Integer cassetteId, int videoId) {
        super(parent, cassetteId == null ? "Добавить кассету" : "Редактировать кассету",
                ModalityType.APPLICATION_MODAL);
        this.videoId = videoId;
        this.editCassetteId = cassetteId;

        try {
            films     = filmDAO.getAllList();
            qualities = simpleDAO.getQualityList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Ошибка: " + e.getMessage());
            dispose(); return;
        }

        filmCombo    = new JComboBox<>(films.stream().map(Film::toString).toArray(String[]::new));
        qualityCombo = new JComboBox<>(qualities.stream().map(Quality::toString).toArray(String[]::new));
        cbDemand     = new JCheckBox("Спрос (Demand)", false);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(new JLabel("Фильм:"));   form.add(filmCombo);
        form.add(new JLabel("Качество:")); form.add(qualityCombo);
        form.add(new JLabel("Спрос:"));   form.add(cbDemand);

        JButton btnSave   = new JButton("💾 Сохранить");
        JButton btnCancel = new JButton("Отмена");
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void save() {
        int filmIdx    = filmCombo.getSelectedIndex();
        int qualIdx    = qualityCombo.getSelectedIndex();
        if (filmIdx < 0 || qualIdx < 0) {
            JOptionPane.showMessageDialog(this, "Выберите фильм и качество");
            return;
        }
        Cassette c = new Cassette();
        if (editCassetteId != null) c.setCassetteId(editCassetteId);
        c.setFilmId(films.get(filmIdx).getFilmId());
        c.setVideoId(videoId);
        c.setQualityId(qualities.get(qualIdx).getQualityId());
        c.setDemand(cbDemand.isSelected());

        try {
            if (editCassetteId == null) cassetteDAO.insert(c);
            else                        cassetteDAO.update(c);
            saved = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}