package frontend.UI.Dialogs;

import backend.dao.UserDAO;
import backend.util.I18n;
import backend.util.SaveGuard;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class UserManagementDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private final SaveGuard saveGuard = new SaveGuard();

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String ROLE_ADMIN    = "admin";
    private static final String ROLE_OPERATOR = "operator";

    public UserManagementDialog(Window parent) {
        super(parent, "👤 Управление пользователями", ModalityType.APPLICATION_MODAL);

        buildUI();
        refresh();

        setSize(600, 450);
        setLocationRelativeTo(parent);
    }

    // ───────────────────────── UI ─────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(8, 8));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 12));

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Логин", "Роль"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // скрыть ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnAdd      = new JButton("➕ Добавить");
        JButton btnRole     = new JButton("🔑 Сменить роль");
        JButton btnPassword = new JButton("🔒 Сменить пароль");
        JButton btnDelete   = new JButton("🗑 Удалить");
        JButton btnClose    = new JButton(I18n.t("btn.cancel"));

        btnDelete.setForeground(new Color(200, 60, 60));

        btnAdd.addActionListener(e -> addUser());

        btnRole.addActionListener(e -> saveGuard.run(this::changeRole));
        btnPassword.addActionListener(e -> saveGuard.run(this::changePassword));
        btnDelete.addActionListener(e -> saveGuard.run(this::deleteUser));

        btnClose.addActionListener(e -> dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.add(btnAdd);
        btnPanel.add(btnRole);
        btnPanel.add(btnPassword);
        btnPanel.add(btnDelete);

        JPanel south = new JPanel(new BorderLayout());
        south.add(btnPanel, BorderLayout.WEST);
        south.add(btnClose, BorderLayout.EAST);

        add(south, BorderLayout.SOUTH);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    saveGuard.run(() -> changeRole());
                }
            }
        });
    }

    // ───────────────────────── DATA ─────────────────────────

    private void refresh() {
        try {
            DefaultTableModel m = userDAO.getAll();
            tableModel.setRowCount(0);

            for (int r = 0; r < m.getRowCount(); r++) {
                tableModel.addRow(new Object[]{
                        m.getValueAt(r, 0),
                        m.getValueAt(r, 1),
                        m.getValueAt(r, 2)
                });
            }

        } catch (SQLException e) {
            err(e);
        }
    }

    // ───────────────────────── ADD USER ─────────────────────────

    private void addUser() {

        JTextField fLogin = new JTextField(18);
        JPasswordField fPass = new JPasswordField(18);
        JPasswordField fPass2 = new JPasswordField(18);
        JComboBox<String> roleCombo =
                new JComboBox<>(new String[]{ROLE_OPERATOR, ROLE_ADMIN});

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Логин:")); form.add(fLogin);
        form.add(new JLabel("Пароль:")); form.add(fPass);
        form.add(new JLabel("Повтор:")); form.add(fPass2);
        form.add(new JLabel("Роль:")); form.add(roleCombo);

        int res = JOptionPane.showConfirmDialog(
                this, form, "Добавить пользователя",
                JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) return;

        String login = fLogin.getText().trim();
        String pass = new String(fPass.getPassword());
        String pass2 = new String(fPass2.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (login.isEmpty() || pass.isEmpty() || !pass.equals(pass2)) {
            warn("Проверьте данные");
            return;
        }

        try {
            if (userDAO.loginExists(login)) {
                warn("Логин уже существует");
                return;
            }

            userDAO.insert(login, pass, role);
            refresh();

        } catch (SQLException e) {
            err(e);
        }
    }

    // ───────────────────────── ROLE ─────────────────────────

    private void changeRole() {
        int[] sel = getSelected();
        if (sel == null) return;

        int id = sel[0];
        int row = sel[1];

        String login = tableModel.getValueAt(row, 1).toString();
        String current = tableModel.getValueAt(row, 2).toString();

        String[] roles = {ROLE_OPERATOR, ROLE_ADMIN};

        String newRole = (String) JOptionPane.showInputDialog(
                this,
                "Пользователь: " + login,
                "Смена роли",
                JOptionPane.PLAIN_MESSAGE,
                null,
                roles,
                current
        );

        if (newRole == null || newRole.equals(current)) return;

        try {
            userDAO.updateRole(id, newRole);
            refresh();
        } catch (SQLException e) {
            err(e);
        }
    }

    // ───────────────────────── PASSWORD ─────────────────────────

    private void changePassword() {

        int[] sel = getSelected();
        if (sel == null) return;

        int id = sel[0];
        String login = tableModel.getValueAt(sel[1], 1).toString();

        JPasswordField p1 = new JPasswordField(18);
        JPasswordField p2 = new JPasswordField(18);

        JPanel form = new JPanel(new GridLayout(2, 2));
        form.add(new JLabel("Новый пароль:")); form.add(p1);
        form.add(new JLabel("Повтор:")); form.add(p2);

        int res = JOptionPane.showConfirmDialog(
                this, form, "Пароль: " + login,
                JOptionPane.OK_CANCEL_OPTION);

        if (res != JOptionPane.OK_OPTION) return;

        String pass = new String(p1.getPassword());
        String pass2 = new String(p2.getPassword());

        if (!pass.equals(pass2)) {
            warn("Пароли не совпадают");
            return;
        }

        try {
            userDAO.updatePassword(id, pass);
        } catch (SQLException e) {
            err(e);
        }
    }

    // ───────────────────────── DELETE ─────────────────────────

    private void deleteUser() {

        int[] sel = getSelected();
        if (sel == null) return;

        int id = sel[0];
        String login = tableModel.getValueAt(sel[1], 1).toString();

        int res = JOptionPane.showConfirmDialog(
                this,
                "Удалить " + login + "?",
                "Удаление",
                JOptionPane.YES_NO_OPTION
        );

        if (res != JOptionPane.YES_OPTION) return;

        try {
            userDAO.delete(id);
            refresh();
        } catch (SQLException e) {
            err(e);
        }
    }

    // ───────────────────────── UTILS ─────────────────────────

    private int[] getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            warn("Выберите пользователя");
            return null;
        }

        int model = table.convertRowIndexToModel(row);
        int id = ((Number) tableModel.getValueAt(model, 0)).intValue();

        return new int[]{id, model};
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
    }

    private void err(SQLException e) {
        JOptionPane.showMessageDialog(this,
                I18n.t("msg.db_error") + "\n" + e.getMessage(),
                I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
    }
}