package frontend.UI.Dialogs;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Универсальный диалог редактирования одного поля (справочники).
 */
public class SimpleEditDialog extends JDialog {

    private boolean saved = false;
    private final JTextField field = new JTextField(25);

    public SimpleEditDialog(Window parent, String title, String currentValue, Consumer<String> onSave) {
        super(parent, title, ModalityType.APPLICATION_MODAL);

        if (currentValue != null) field.setText(currentValue);

        JPanel form = new JPanel(new FlowLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(new JLabel("Название:"));
        form.add(field);

        JButton btnSave   = new JButton("💾 Сохранить");
        JButton btnCancel = new JButton("Отмена");
        btnSave.addActionListener(e -> {
            String val = field.getText().trim();
            if (val.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Поле не может быть пустым");
                return;
            }
            onSave.accept(val);
            saved = true;
            dispose();
        });
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

    public boolean isSaved() { return saved; }
}
