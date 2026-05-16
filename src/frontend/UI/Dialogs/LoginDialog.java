package frontend.UI.Dialogs;

import backend.dao.AuthDAO;
import backend.model.User;
import backend.util.UIUtils;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private User user;

    private JTextField fLogin = new JTextField(15);
    private JPasswordField fPassword = new JPasswordField(15);

    public LoginDialog(Window parent) {
        super(parent, "Авторизация", ModalityType.APPLICATION_MODAL);

        JPanel panel = new JPanel(new GridLayout(6, 1, 20, 20));

        panel.add(new JLabel("БД: Сеть видеосалонов"));
        panel.add(new JLabel("Разработчик: Куклин Кирилл Викторович"));

        panel.add(new JLabel("Логин"));
        panel.add(fLogin);

        panel.add(new JLabel("Пароль"));
        panel.add(fPassword);

        JButton btnLogin = new JButton("Войти");

        // ❗ ЕДИНАЯ ТОЧКА ЛОГИНА
        btnLogin.addActionListener(e -> doLogin());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(btnLogin, BorderLayout.SOUTH);

        // 🔥 ВАЖНО: только ОДИН Enter handler
        getRootPane().setDefaultButton(btnLogin);

        // ❌ УБРАТЬ Enter-trigger с password field
        // fPassword.addActionListener(e -> doLogin());  ← удаляем

        // ❌ НЕ используем Enter-navigation здесь вообще
        // UIUtils.enableEnterToNextField(panel);        ← убираем

        pack();
        setLocationRelativeTo(parent);
    }

    private void doLogin() {
        try {
            AuthDAO dao = new AuthDAO();

            User u = dao.login(
                    fLogin.getText().trim(),
                    new String(fPassword.getPassword())
            );

            if (u == null) {
                JOptionPane.showMessageDialog(this,
                        "Неверный логин или пароль");
                return;
            }

            user = u;
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage());
        }
    }

    public User getUser() {
        return user;
    }
}