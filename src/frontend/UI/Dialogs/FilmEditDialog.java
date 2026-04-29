package frontend.UI.Dialogs;

import backend.dao.FilmDAO;
import backend.dao.SimpleDAO;
import backend.model.Director;
import backend.model.Film;
import backend.model.Studio;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Диалог добавления/редактирования фильма */
public class FilmEditDialog extends JDialog {

    private boolean saved = false;
    private final Film film;
    private final FilmDAO filmDAO = new FilmDAO();
    private final SimpleDAO simpleDAO = new SimpleDAO();

    private final JTextField fCaption  = new JTextField(25);
    private final JTextField fYear     = new JTextField(5);
    private final JSpinner   fDuration = new JSpinner(new SpinnerNumberModel(90, 1, 999, 1));
    private final JTextArea  fInfo     = new JTextArea(3, 25);
    private JComboBox<String> directorCombo;
    private JComboBox<String> studioCombo;
    private List<Director> directors;
    private List<Studio> studios;

    public FilmEditDialog(Window parent, Film film) {
        super(parent, film == null ? "Добавить фильм" : "Редактировать фильм",
                ModalityType.APPLICATION_MODAL);
        this.film = film;

        try {
            directors = simpleDAO.getDirectorList();
            studios   = simpleDAO.getStudioList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Ошибка: " + e.getMessage());
            dispose(); return;
        }

        directorCombo = new JComboBox<>(directors.stream().map(Director::toString).toArray(String[]::new));
        studioCombo   = new JComboBox<>(studios.stream().map(Studio::toString).toArray(String[]::new));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, gbc, 0, "Название:", fCaption);
        addRow(form, gbc, 1, "Год:", fYear);
        addRow(form, gbc, 2, "Длительность (мин):", fDuration);
        addRow(form, gbc, 3, "Режиссер:", directorCombo);
        addRow(form, gbc, 4, "Студия:", studioCombo);

        gbc.gridx = 0; gbc.gridy = 5; form.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1; form.add(new JScrollPane(fInfo), gbc);

        if (film != null) {
            fCaption.setText(film.getCaption());
            fYear.setText(film.getYear());
            fDuration.setValue(film.getDuration() > 0 ? film.getDuration() : 90);
            fInfo.setText(film.getInformation());
            for (int i = 0; i < directors.size(); i++)
                if (directors.get(i).getDirectorId() == film.getDirectorId()) { directorCombo.setSelectedIndex(i); break; }
            for (int i = 0; i < studios.size(); i++)
                if (studios.get(i).getStudioId() == film.getStudioId()) { studioCombo.setSelectedIndex(i); break; }
        }

        JButton btnSave   = new JButton("💾 Сохранить");
        JButton btnCancel = new JButton("Отмена");
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        setLayout(new BorderLayout());
        add(new JScrollPane(form), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0; gbc.gridy = row; p.add(new JLabel(label), gbc);
        gbc.gridx = 1; p.add(comp, gbc);
    }

    private void save() {
        try {
            Film f = (film != null) ? film : new Film();
            f.setCaption(fCaption.getText().trim());
            f.setYear(fYear.getText().trim());
            f.setDuration((Integer) fDuration.getValue());
            f.setInformation(fInfo.getText().trim());
            int dIdx = directorCombo.getSelectedIndex();
            f.setDirectorId(dIdx >= 0 ? directors.get(dIdx).getDirectorId() : 0);
            int sIdx = studioCombo.getSelectedIndex();
            f.setStudioId(sIdx >= 0 ? studios.get(sIdx).getStudioId() : 0);

            if (film == null) filmDAO.insert(f);
            else              filmDAO.update(f);
            saved = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
