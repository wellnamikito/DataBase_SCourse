package backend.util;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Тёмная тема для Swing-приложения.
 * Вызвать DarkTheme.apply() до создания любых окон.
 */
public class DarkTheme {

    // Цвета темы
    public static final Color BG_DARK       = new Color(30, 30, 30);
    public static final Color BG_PANEL      = new Color(40, 40, 40);
    public static final Color BG_COMPONENT  = new Color(55, 55, 55);
    public static final Color BG_TABLE_ROW  = new Color(45, 45, 45);
    public static final Color BG_TABLE_ALT  = new Color(50, 50, 50);
    public static final Color BG_SELECTED   = new Color(70, 130, 180);
    public static final Color BG_HEADER     = new Color(35, 35, 35);

    public static final Color FG_TEXT       = new Color(220, 220, 220);
    public static final Color FG_DIM        = new Color(160, 160, 160);
    public static final Color FG_WHITE      = new Color(240, 240, 240);

    public static final Color BORDER_COLOR  = new Color(70, 70, 70);
    public static final Color ACCENT        = new Color(70, 130, 180);

    // Кнопки
    public static final Color BTN_ADD       = new Color(39, 174, 96);
    public static final Color BTN_EDIT      = new Color(41, 128, 185);
    public static final Color BTN_DELETE    = new Color(192, 57, 43);
    public static final Color BTN_REFRESH   = new Color(90, 90, 90);
    public static final Color BTN_EXPORT    = new Color(39, 174, 96);
    public static final Color BTN_RUN       = new Color(41, 128, 185);

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // === Общие ===
        UIManager.put("Panel.background",           BG_PANEL);
        UIManager.put("OptionPane.background",      BG_PANEL);
        UIManager.put("OptionPane.messageForeground",FG_TEXT);

        // === Текст ===
        UIManager.put("Label.foreground",           FG_TEXT);
        UIManager.put("Label.background",           BG_PANEL);
        UIManager.put("TextField.background",       BG_COMPONENT);
        UIManager.put("TextField.foreground",       FG_TEXT);
        UIManager.put("TextField.caretForeground",  FG_WHITE);
        UIManager.put("TextField.border",           BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("TextArea.background",        BG_COMPONENT);
        UIManager.put("TextArea.foreground",        FG_TEXT);
        UIManager.put("TextArea.caretForeground",   FG_WHITE);
        UIManager.put("FormattedTextField.background", BG_COMPONENT);
        UIManager.put("FormattedTextField.foreground", FG_TEXT);
        UIManager.put("PasswordField.background",   BG_COMPONENT);
        UIManager.put("PasswordField.foreground",   FG_TEXT);

        // === Кнопки ===
        UIManager.put("Button.background",          BG_COMPONENT);
        UIManager.put("Button.foreground",          FG_TEXT);
        UIManager.put("Button.border",              BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Button.focus",               new ColorUIResource(new Color(0, 0, 0, 0)));

        // === Таблица ===
        UIManager.put("Table.background",           BG_TABLE_ROW);
        UIManager.put("Table.foreground",           FG_TEXT);
        UIManager.put("Table.selectionBackground",  BG_SELECTED);
        UIManager.put("Table.selectionForeground",  FG_WHITE);
        UIManager.put("Table.gridColor",            BORDER_COLOR);
        UIManager.put("TableHeader.background",     BG_HEADER);
        UIManager.put("TableHeader.foreground",     FG_TEXT);
        UIManager.put("TableHeader.cellBorder",     BorderFactory.createLineBorder(BORDER_COLOR));

        // === ScrollPane ===
        UIManager.put("ScrollPane.background",      BG_DARK);
        UIManager.put("ScrollPane.border",          BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Viewport.background",        BG_TABLE_ROW);

        // === ComboBox ===
        UIManager.put("ComboBox.background",        BG_COMPONENT);
        UIManager.put("ComboBox.foreground",        FG_TEXT);
        UIManager.put("ComboBox.selectionBackground", BG_SELECTED);
        UIManager.put("ComboBox.selectionForeground", FG_WHITE);
        UIManager.put("ComboBox.border",            BorderFactory.createLineBorder(BORDER_COLOR));

        // === Spinner ===
        UIManager.put("Spinner.background",         BG_COMPONENT);
        UIManager.put("Spinner.foreground",         FG_TEXT);

        // === CheckBox ===
        UIManager.put("CheckBox.background",        BG_PANEL);
        UIManager.put("CheckBox.foreground",        FG_TEXT);

        // === TabbedPane ===
        UIManager.put("TabbedPane.background",      BG_DARK);
        UIManager.put("TabbedPane.foreground",      FG_TEXT);
        UIManager.put("TabbedPane.selected",        BG_COMPONENT);
        UIManager.put("TabbedPane.contentAreaColor",BG_PANEL);
        UIManager.put("TabbedPane.shadow",          BORDER_COLOR);
        UIManager.put("TabbedPane.darkShadow",      BORDER_COLOR);
        UIManager.put("TabbedPane.light",           BG_COMPONENT);
        UIManager.put("TabbedPane.highlight",       BG_COMPONENT);

        // === SplitPane ===
        UIManager.put("SplitPane.background",       BG_DARK);
        UIManager.put("SplitPane.dividerSize",      6);
        UIManager.put("SplitPaneDivider.background",BG_COMPONENT);

        // === MenuBar / Menu ===
        UIManager.put("MenuBar.background",         BG_HEADER);
        UIManager.put("MenuBar.foreground",         FG_TEXT);
        UIManager.put("MenuBar.border",             BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        UIManager.put("Menu.background",            BG_HEADER);
        UIManager.put("Menu.foreground",            FG_TEXT);
        UIManager.put("Menu.selectionBackground",   BG_SELECTED);
        UIManager.put("Menu.selectionForeground",   FG_WHITE);
        UIManager.put("MenuItem.background",        BG_PANEL);
        UIManager.put("MenuItem.foreground",        FG_TEXT);
        UIManager.put("MenuItem.selectionBackground", BG_SELECTED);
        UIManager.put("MenuItem.selectionForeground", FG_WHITE);
        UIManager.put("PopupMenu.background",       BG_PANEL);
        UIManager.put("PopupMenu.border",           BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Separator.background",       BORDER_COLOR);
        UIManager.put("Separator.foreground",       BORDER_COLOR);

        // === Dialog / OptionPane ===
        UIManager.put("Dialog.background",          BG_PANEL);
        UIManager.put("OptionPane.background",      BG_PANEL);
        UIManager.put("OptionPane.foreground",      FG_TEXT);

        // === TitledBorder ===
        UIManager.put("TitledBorder.titleColor",    FG_DIM);
        UIManager.put("TitledBorder.border",        BorderFactory.createLineBorder(BORDER_COLOR));

        // === ToolTip ===
        UIManager.put("ToolTip.background",         BG_COMPONENT);
        UIManager.put("ToolTip.foreground",         FG_TEXT);
        UIManager.put("ToolTip.border",             BorderFactory.createLineBorder(BORDER_COLOR));

        // === FileChooser ===
        UIManager.put("FileChooser.background",     BG_PANEL);
        UIManager.put("FileView.background",        BG_PANEL);
        UIManager.put("List.background",            BG_COMPONENT);
        UIManager.put("List.foreground",            FG_TEXT);
        UIManager.put("List.selectionBackground",   BG_SELECTED);
        UIManager.put("List.selectionForeground",   FG_WHITE);
    }

    /** Создать стилизованную кнопку */
    public static JButton button(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(FG_WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** Создать TitledBorder в стиле тёмной темы */
    public static Border titledBorder(String title) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                title,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.PLAIN, 11),
                FG_DIM
        );
    }
}
