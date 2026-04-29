package frontend.UI.Dialogs;


import backend.dao.ReceiptDAO;
import backend.dao.SimpleDAO;
import backend.dao.VideoDAO;
import backend.model.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Диалог добавления/редактирования квитанции */
public class ReceiptEditDialog extends JDialog {

    private boolean saved = false;
    private final Receipt receipt;
    private final ReceiptDAO receiptDAO = new ReceiptDAO();
    private final SimpleDAO simpleDAO = new SimpleDAO();
    private final VideoDAO videoDAO = new VideoDAO();

    private JComboBox<String> serviceCombo;
    private JComboBox<String> videoCombo;
    private JTextField fCassetteId = new JTextField(8);
    private JTextField fDate       = new JTextField(LocalDate.now().toString(), 12);
    private JSpinner   fPrice      = new JSpinner(new SpinnerNumberModel(100, 1, 99999, 10));

    private List<Service> services;
    private List<Video> videos;

    public ReceiptEditDialog(Window parent, Receipt receipt) {
        super(parent, receipt == null ? "Добавить квитанцию" : "Редактировать квитанцию",
                ModalityType.APPLICATION_MODAL);
        this.receipt = receipt;

        try {
            services = simpleDAO.getServiceList();
            videos   = videoDAO.getAllList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, "Ошибка: " + e.getMessage());
            dispose(); return;
        }

        serviceCombo = new JComboBox<>(services.stream().map(Service::toString).toArray(String[]::new));
        videoCombo   = new JComboBox<>(videos.stream().map(Video::toString).toArray(String[]::new));

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        form.add(new JLabel("Кассета ID:"));  form.add(fCassetteId);
        form.add(new JLabel("Видеосалон:")); form.add(videoCombo);
        form.add(new JLabel("Услуга:"));     form.add(serviceCombo);
        form.add(new JLabel("Дата (YYYY-MM-DD):")); form.add(fDate);
        form.add(new JLabel("Цена:"));       form.add(fPrice);

        if (receipt != null) {
            fCassetteId.setText(String.valueOf(receipt.getCassetteId()));
            fDate.setText(receipt.getDate() != null ? receipt.getDate().toString() : "");
            fPrice.setValue(receipt.getPrice());
            for (int i = 0; i < services.size(); i++)
                if (services.get(i).getServiceId() == receipt.getServiceId()) { serviceCombo.setSelectedIndex(i); break; }
            for (int i = 0; i < videos.size(); i++)
                if (videos.get(i).getVideoId() == receipt.getVideoId()) { videoCombo.setSelectedIndex(i); break; }
        }

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
        try {
            Receipt r = (receipt != null) ? receipt : new Receipt();
            r.setCassetteId(Integer.parseInt(fCassetteId.getText().trim()));
            int vIdx = videoCombo.getSelectedIndex();
            r.setVideoId(vIdx >= 0 ? videos.get(vIdx).getVideoId() : 0);
            int sIdx = serviceCombo.getSelectedIndex();
            r.setServiceId(sIdx >= 0 ? services.get(sIdx).getServiceId() : 0);
            r.setDate(LocalDate.parse(fDate.getText().trim()));
            r.setPrice((Integer) fPrice.getValue());

            if (receipt == null) receiptDAO.insert(r);
            else                  receiptDAO.update(r);
            saved = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
