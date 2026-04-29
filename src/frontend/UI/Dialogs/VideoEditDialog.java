package frontend.UI.Dialogs;

import backend.dao.OwnerDAO;
import backend.dao.SimpleDAO;
import backend.dao.VideoDAO;
import backend.model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** Диалог добавления/редактирования видеосалона */
public class VideoEditDialog extends JDialog {

    private boolean saved = false;
    private final Video video;
    private final VideoDAO videoDAO = new VideoDAO();
    private final SimpleDAO simpleDAO = new SimpleDAO();
    private final OwnerDAO ownerDAO = new OwnerDAO();

    private final JTextField fCaption   = new JTextField(25);
    private final JTextField fAddress   = new JTextField(25);
    private final JTextField fType      = new JTextField(15);
    private final JTextField fPhone     = new JTextField(13);
    private final JTextField fLicence   = new JTextField(15);
    private final JSpinner   fTimeStart = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1));
    private final JSpinner   fTimeEnd   = new JSpinner(new SpinnerNumberModel(21, 0, 23, 1));
    private final JSpinner   fAmount    = new JSpinner(new SpinnerNumberModel(50, 1, 9999, 1));
    private JComboBox<String> districtCombo;
    private JComboBox<String> ownerCombo;
    private List<District> districts;
    private List<Owner> owners;

    public VideoEditDialog(Window parent, Video video) {
        super(parent, video == null ? "Добавить видеосалон" : "Редактировать видеосалон",
                ModalityType.APPLICATION_MODAL);
        this.video = video;

        // Загрузка справочников
        try {
            districts = simpleDAO.getDistrictList();
            owners    = ownerDAO.getAllList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Ошибка загрузки справочников: " + e.getMessage());
            dispose(); return;
        }

        districtCombo = new JComboBox<>(districts.stream().map(District::getDistrictName).toArray(String[]::new));
        ownerCombo    = new JComboBox<>(owners.stream().map(Owner::toString).toArray(String[]::new));

        JPanel form = new JPanel(new GridLayout(10, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(new JLabel("Название:")); form.add(fCaption);
        form.add(new JLabel("Район:"));   form.add(districtCombo);
        form.add(new JLabel("Адрес:"));   form.add(fAddress);
        form.add(new JLabel("Тип:"));     form.add(fType);
        form.add(new JLabel("Телефон (+7XXXXXXXXXX):")); form.add(fPhone);
        form.add(new JLabel("Лицензия (ЛИЦ-XXXXX):")); form.add(fLicence);
        form.add(new JLabel("Время открытия:")); form.add(fTimeStart);
        form.add(new JLabel("Время закрытия:"));  form.add(fTimeEnd);
        form.add(new JLabel("Кол-во клиентов:")); form.add(fAmount);
        form.add(new JLabel("Владелец:"));        form.add(ownerCombo);

        if (video != null) {
            fCaption.setText(video.getCaption());
            fAddress.setText(video.getAddress());
            fType.setText(video.getType());
            fPhone.setText(video.getPhone());
            fLicence.setText(video.getLicence());
            fTimeStart.setValue(video.getTimeStart());
            fTimeEnd.setValue(video.getTimeEnd());
            fAmount.setValue(video.getAmount());
            // Выбрать нужный район
            for (int i = 0; i < districts.size(); i++)
                if (districts.get(i).getDistrictId() == video.getDistrictId()) { districtCombo.setSelectedIndex(i); break; }
            for (int i = 0; i < owners.size(); i++)
                if (owners.get(i).getOwnerID() == video.getOwnerId()) { ownerCombo.setSelectedIndex(i); break; }
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

    private void save() {
        String caption = fCaption.getText().trim();
        String address = fAddress.getText().trim();
        String type    = fType.getText().trim();
        String phone   = fPhone.getText().trim();
        String licence = fLicence.getText().trim();

        // Валидация обязательных полей
        if (caption.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Название обязательно", "Ошибка", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Валидация телефона: +7XXXXXXXXXX
        if (!phone.isEmpty() && !phone.matches("^\\+7[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this,
                    "Телефон должен быть в формате +7XXXXXXXXXX\nНапример: +79001234567",
                    "Ошибка валидации", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Валидация лицензии: ЛИЦ-XXXXXX
        if (!licence.isEmpty() && !licence.matches("ЛИЦ-[0-9]{1,6}$")) {
            JOptionPane.showMessageDialog(this,
                    "Лицензия должна быть в формате ЛИЦ-XXXXXX\nНапример: ЛИЦ-123456",
                    "Ошибка валидации", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Video v = (video != null) ? video : new Video();
            v.setCaption(caption);
            v.setAddress(address);
            v.setType(type);
            v.setPhone(phone);
            v.setLicence(licence);
            v.setTimeStart((Integer) fTimeStart.getValue());
            v.setTimeEnd((Integer) fTimeEnd.getValue());
            v.setAmount((Integer) fAmount.getValue());
            int distIdx = districtCombo.getSelectedIndex();
            v.setDistrictId(distIdx >= 0 ? districts.get(distIdx).getDistrictId() : 0);
            int ownIdx = ownerCombo.getSelectedIndex();
            v.setOwnerId(ownIdx >= 0 ? owners.get(ownIdx).getOwnerID() : 0);

            if (video == null) videoDAO.insert(v);
            else               videoDAO.update(v);
            saved = true;
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Ошибка БД:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}