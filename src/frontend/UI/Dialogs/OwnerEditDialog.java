package frontend.UI.Dialogs;

import backend.dao.OwnerDAO;
import backend.model.Owner;
import backend.util.SaveGuard;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Диалог добавления/редактирования владельца.
 */
public class OwnerEditDialog extends JDialog {

    private boolean saved = false;
    private Owner owner;
    private final OwnerDAO ownerDAO = new OwnerDAO();

    private final SaveGuard saveGuard = new SaveGuard();

    private final JTextField fFamilia   = new JTextField(20);
    private final JTextField fName      = new JTextField(20);
    private final JTextField fOtchestvo = new JTextField(20);

    public OwnerEditDialog(Window parent, Owner owner) {
        super(parent,
                owner == null ? "Добавить владельца" : "Редактировать владельца",
                ModalityType.APPLICATION_MODAL);

        this.owner = owner;

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        form.add(new JLabel("Фамилия:"));   form.add(fFamilia);
        form.add(new JLabel("Имя:"));       form.add(fName);
        form.add(new JLabel("Отчество:"));  form.add(fOtchestvo);

        if (owner != null) {
            fFamilia.setText(owner.getFamilia());
            fName.setText(owner.getName());
            fOtchestvo.setText(owner.getOtchestvo());
        }

        JButton btnSave   = new JButton("💾 Сохранить");
        JButton btnCancel = new JButton("Отмена");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);

        // ❗ ВАЖНО: только ОДИН обработчик сохранения
        btnSave.addActionListener(e ->
                saveGuard.run(this::save)
        );

        btnCancel.addActionListener(e -> dispose());

        // Enter = save
        getRootPane().setDefaultButton(btnSave);

        // Tab navigation нормальная
        setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());
        setFocusTraversalPolicyProvider(true);
    }

    private void save() {

        String fam  = fFamilia.getText().trim();
        String name = fName.getText().trim();
        String otch = fOtchestvo.getText().trim();

        String fioRegex = "^[A-Za-zА-Яа-яЁё\\- ]+$";

        if (fam.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Фамилия и Имя обязательны",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            saveGuard.reset();
            return;
        }

        if (!fam.matches(fioRegex)) {
            JOptionPane.showMessageDialog(this,
                    "Неверная фамилия",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            saveGuard.reset();
            return;
        }

        if (!name.matches(fioRegex)) {
            JOptionPane.showMessageDialog(this,
                    "Неверное имя",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            saveGuard.reset();
            return;
        }

        if (!otch.isEmpty() && !otch.matches(fioRegex)) {
            JOptionPane.showMessageDialog(this,
                    "Неверное отчество",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            saveGuard.reset();
            return;
        }

        try {
            if (owner == null) {
                ownerDAO.insert(new Owner(0, fam, name, otch));
            } else {
                owner.setFamilia(fam);
                owner.setName(name);
                owner.setOtchestvo(otch);
                ownerDAO.update(owner);
            }

            saved = true;
            dispose();

        } catch (SQLException e) {
            saveGuard.reset();

            JOptionPane.showMessageDialog(this,
                    "Ошибка БД:\n" + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}