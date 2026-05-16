package frontend.UI.Dialogs;

import backend.dao.CassetteDAO;
import backend.dao.FilmDAO;
import backend.dao.SimpleDAO;
import backend.model.Cassette;
import backend.model.Film;
import backend.model.Quality;
import backend.util.I18n;
import backend.util.SaveGuard;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Диалог с полной информацией о кассете + просмотр и загрузка фото.
 * readOnly=true — только просмотр (для оператора).
 */
public class CassetteDetailDialog extends JDialog {

    private final CassetteDAO cassetteDAO = new CassetteDAO();
    private final FilmDAO     filmDAO     = new FilmDAO();
    private final SimpleDAO   simpleDAO   = new SimpleDAO();

    private final SaveGuard saveGuard = new SaveGuard();

    private final int cassetteId;
    private final boolean readOnly;
    private boolean saved = false;

    private JLabel lblPhoto;
    private JLabel lblPhotoStatus;
    private JButton btnUpload;
    private JButton btnRemove;
    private JComboBox<String> filmCombo;
    private JComboBox<String> qualityCombo;
    private JCheckBox cbDemand;
    private List<Film> films;
    private List<Quality> qualities;
    private byte[] currentPhoto;

    // ── Конструкторы ──────────────────────────────────────────────

    public CassetteDetailDialog(Window parent, int cassetteId) {
        this(parent, cassetteId, false);
    }

    public CassetteDetailDialog(Window parent, int cassetteId, boolean readOnly) {
        super(parent,
                I18n.t("cassette.dlg.title") + cassetteId +
                        (readOnly ? " — " + I18n.t("cassette.dlg.title.readonly") : ""),
                ModalityType.APPLICATION_MODAL);

        this.cassetteId = cassetteId;
        this.readOnly   = readOnly;

        try {
            films     = filmDAO.getAllList();
            qualities = simpleDAO.getQualityList();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent,
                    I18n.t("msg.db_error") + "\n" + e.getMessage(),
                    I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        buildUI();
        loadData();

        setSize(580, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // ── UI ─────────────────────────────────────────────

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(
                BorderFactory.createEmptyBorder(12, 14, 8, 14));

        lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(200, 220));
        lblPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblPhoto.setVerticalAlignment(SwingConstants.CENTER);
        lblPhoto.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        lblPhoto.setBackground(new Color(40, 40, 40));
        lblPhoto.setOpaque(true);
        lblPhoto.setForeground(Color.GRAY);
        lblPhoto.setText("<html><center>📷<br>" + I18n.t("cassette.dlg.no_photo") + "</center></html>");

        lblPhotoStatus = new JLabel(" ", SwingConstants.CENTER);

        btnUpload = new JButton(I18n.t("btn.upload_photo"));
        btnRemove = new JButton(I18n.t("btn.remove_photo"));

        btnUpload.addActionListener(e -> uploadPhoto());
        btnRemove.addActionListener(e -> removePhoto());

        JPanel photoBtns = new JPanel(new GridLayout(2, 1, 4, 4));
        photoBtns.add(btnUpload);
        photoBtns.add(btnRemove);

        JPanel photoPanel = new JPanel(new BorderLayout(4, 6));
        photoPanel.add(lblPhotoStatus, BorderLayout.NORTH);
        photoPanel.add(lblPhoto, BorderLayout.CENTER);
        photoPanel.add(photoBtns, BorderLayout.SOUTH);
        photoPanel.setBorder(titledBorder(I18n.t("cassette.dlg.photo_border")));

        filmCombo = new JComboBox<>(films.stream().map(Film::toString).toArray(String[]::new));
        qualityCombo = new JComboBox<>(qualities.stream().map(Quality::toString).toArray(String[]::new));
        cbDemand = new JCheckBox(I18n.t("f.demand"));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addRow(form, gbc, 0, new JLabel(I18n.t("f.film")), filmCombo);
        addRow(form, gbc, 1, new JLabel(I18n.t("f.quality")), qualityCombo);
        addRow(form, gbc, 2, new JLabel(I18n.t("f.demand")), cbDemand);

        form.setBorder(titledBorder(I18n.t("cassette.dlg.data_border")));

        JPanel center = new JPanel(new BorderLayout(12, 0));
        center.add(photoPanel, BorderLayout.WEST);
        center.add(form, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JButton btnSave = new JButton("💾 " + I18n.t("btn.save"));
        JButton btnCancel = new JButton(I18n.t("btn.cancel"));

        // 🔥 ВАЖНО: теперь save только через SaveGuard
        btnSave.addActionListener(e -> saveGuard.run(this::save));
        btnCancel.addActionListener(e -> dispose());

        getRootPane().setDefaultButton(btnSave);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.add(btnSave);
        btnRow.add(btnCancel);
        add(btnRow, BorderLayout.SOUTH);

        if (readOnly) {
            filmCombo.setEnabled(false);
            qualityCombo.setEnabled(false);
            cbDemand.setEnabled(false);
            btnSave.setVisible(false);
            btnUpload.setEnabled(false);
            btnRemove.setEnabled(false);
        }
    }

    private void addRow(JPanel p, GridBagConstraints gbc, int row,
                        JLabel lbl, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(field, gbc);
    }

    private TitledBorder titledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 11), Color.GRAY);
    }

    // ── DATA ─────────────────────────────────────────────

    private void loadData() {
        try {
            Cassette c = cassetteDAO.getById(cassetteId);
            if (c == null) return;

            for (int i = 0; i < films.size(); i++) {
                if (films.get(i).getFilmId() == c.getFilmId()) {
                    filmCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < qualities.size(); i++) {
                if (qualities.get(i).getQualityId() == c.getQualityId()) {
                    qualityCombo.setSelectedIndex(i);
                    break;
                }
            }

            cbDemand.setSelected(c.isDemand());
            currentPhoto = c.getPhoto();
            updatePhotoLabel(currentPhoto);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    I18n.t("msg.db_error") + "\n" + e.getMessage(),
                    I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── PHOTO ─────────────────────────────────────────────

    private void uploadPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(I18n.t("cassette.dlg.choose_photo"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());

            if (bytes.length > 5 * 1024 * 1024) return;

            currentPhoto = bytes;
            updatePhotoLabel(currentPhoto);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    I18n.t("cassette.dlg.photo_err") + "\n" + ex.getMessage());
        }
    }

    private void removePhoto() {
        currentPhoto = null;
        updatePhotoLabel(null);
    }

    private void updatePhotoLabel(byte[] photoBytes) {
        if (photoBytes == null || photoBytes.length == 0) {
            lblPhoto.setIcon(null);
            lblPhoto.setText("<html><center>📷<br>" +
                    I18n.t("cassette.dlg.no_photo") + "</center></html>");
            return;
        }

        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(photoBytes));
            if (img == null) return;

            int w = 196, h = 200;
            double scale = Math.min((double) w / img.getWidth(), (double) h / img.getHeight());

            Image scaled = img.getScaledInstance(
                    (int) (img.getWidth() * scale),
                    (int) (img.getHeight() * scale),
                    Image.SCALE_SMOOTH);

            lblPhoto.setIcon(new ImageIcon(scaled));
            lblPhoto.setText(null);

        } catch (Exception ignored) {}
    }

    // ── SAVE ─────────────────────────────────────────────

    private void save() {
        int filmIdx = filmCombo.getSelectedIndex();
        int qualIdx = qualityCombo.getSelectedIndex();

        if (filmIdx < 0 || qualIdx < 0) {
            JOptionPane.showMessageDialog(this,
                    I18n.t("cassette.dlg.select_both"));
            saveGuard.reset();
            return;
        }

        try {
            Cassette existing = cassetteDAO.getById(cassetteId);
            if (existing == null) return;

            Cassette c = new Cassette();
            c.setCassetteId(cassetteId);
            c.setFilmId(films.get(filmIdx).getFilmId());
            c.setQualityId(qualities.get(qualIdx).getQualityId());
            c.setDemand(cbDemand.isSelected());
            c.setVideoId(existing.getVideoId());

            cassetteDAO.update(c);
            cassetteDAO.updatePhoto(cassetteId, currentPhoto);

            saved = true;
            dispose();

        } catch (SQLException e) {
            saveGuard.reset();
            JOptionPane.showMessageDialog(this,
                    I18n.t("msg.db_error") + "\n" + e.getMessage());
        }
    }

    public boolean isSaved() {
        return saved;
    }
}