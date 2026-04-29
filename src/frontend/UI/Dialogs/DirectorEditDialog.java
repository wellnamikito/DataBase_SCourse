package frontend.UI.Dialogs;

import backend.dao.SimpleDAO;
import backend.model.Director;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/** Диалог для добавления/редактирования режиссера */
public class DirectorEditDialog extends JDialog {

    private boolean saved = false;
    private final Director director;
    private final SimpleDAO simpleDAO = new SimpleDAO();

    private final JTextField fFam  = new JTextField(20);
    private final JTextField fName = new JTextField(20);
    private final JTextField fOtch = new JTextField(20);

    public DirectorEditDialog(Window parent, Director director) {
        super(parent, director == null ? "Добавить режиссера" : "Редактировать режиссера",
                ModalityType.APPLICATION_MODAL);
        this.director = director;

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(new JLabel("Фамилия:")); form.add(fFam);
        form.add(new JLabel("Имя:"));    form.add(fName);
        form.add(new JLabel("Отчество:")); form.add(fOtch);

        if (director != null) {
            fFam.setText(director.getFamilia());
            fName.setText(director.getName());
            fOtch.setText(director.getOtchestvo());
        }

        JButton btnSave   = new JButton("💾 Сохранить");
        JButton btnCancel = new JButton("Отмена");
        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void save() {
        try {
            Director d = director != null ? director : new Director();
            d.setFamilia(fFam.getText().trim());
            d.setName(fName.getText().trim());
            d.setOtchestvo(fOtch.getText().trim());
            if (director == null) simpleDAO.insertDirector(d);
            else                   simpleDAO.updateDirector(d);
            saved = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
