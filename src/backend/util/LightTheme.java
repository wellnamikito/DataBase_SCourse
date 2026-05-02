package backend.util;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Светлая тема приложения.
 */
public class LightTheme {

    public static final Color BG_DARK       = new Color(245, 245, 245);
    public static final Color BG_PANEL      = new Color(250, 250, 250);
    public static final Color BG_COMPONENT  = Color.WHITE;
    public static final Color BG_TABLE_ROW  = Color.WHITE;
    public static final Color BG_SELECTED   = new Color(51, 122, 183);
    public static final Color BG_HEADER     = new Color(236, 240, 241);

    public static final Color FG_TEXT       = new Color(30, 30, 30);
    public static final Color FG_DIM        = new Color(100, 100, 100);
    public static final Color FG_WHITE      = Color.WHITE;

    public static final Color BORDER_COLOR  = new Color(200, 200, 200);
    public static final Color ACCENT        = new Color(51, 122, 183);

    public static final Color BTN_ADD       = new Color(39, 174, 96);
    public static final Color BTN_EDIT      = new Color(41, 128, 185);
    public static final Color BTN_DELETE    = new Color(192, 57, 43);
    public static final Color BTN_REFRESH   = new Color(127, 140, 141);
    public static final Color BTN_EXPORT    = new Color(39, 174, 96);
    public static final Color BTN_RUN       = new Color(41, 128, 185);

    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",               BG_PANEL);
        UIManager.put("Label.foreground",               FG_TEXT);
        UIManager.put("Label.background",               BG_PANEL);
        UIManager.put("TextField.background",           BG_COMPONENT);
        UIManager.put("TextField.foreground",           FG_TEXT);
        UIManager.put("TextField.caretForeground",      FG_TEXT);
        UIManager.put("TextField.border",               BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("TextArea.background",            BG_COMPONENT);
        UIManager.put("TextArea.foreground",            FG_TEXT);
        UIManager.put("FormattedTextField.background",  BG_COMPONENT);
        UIManager.put("FormattedTextField.foreground",  FG_TEXT);

        UIManager.put("Button.background",              BG_COMPONENT);
        UIManager.put("Button.foreground",              FG_TEXT);
        UIManager.put("Button.border",                  BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Button.focus",                   new ColorUIResource(new Color(0,0,0,0)));

        UIManager.put("Table.background",               BG_TABLE_ROW);
        UIManager.put("Table.foreground",               FG_TEXT);
        UIManager.put("Table.selectionBackground",      BG_SELECTED);
        UIManager.put("Table.selectionForeground",      FG_WHITE);
        UIManager.put("Table.gridColor",                BORDER_COLOR);
        UIManager.put("TableHeader.background",         BG_HEADER);
        UIManager.put("TableHeader.foreground",         FG_TEXT);
        UIManager.put("TableHeader.cellBorder",         BorderFactory.createLineBorder(BORDER_COLOR));

        UIManager.put("ScrollPane.background",          BG_PANEL);
        UIManager.put("ScrollPane.border",              BorderFactory.createLineBorder(BORDER_COLOR));
        UIManager.put("Viewport.background",            BG_TABLE_ROW);

        UIManager.put("ComboBox.background",            BG_COMPONENT);
        UIManager.put("ComboBox.foreground",            FG_TEXT);
        UIManager.put("ComboBox.selectionBackground",   BG_SELECTED);
        UIManager.put("ComboBox.selectionForeground",   FG_WHITE);

        UIManager.put("Spinner.background",             BG_COMPONENT);
        UIManager.put("Spinner.foreground",             FG_TEXT);

        UIManager.put("CheckBox.background",            BG_PANEL);
        UIManager.put("CheckBox.foreground",            FG_TEXT);

        UIManager.put("TabbedPane.background",          BG_PANEL);
        UIManager.put("TabbedPane.foreground",          FG_TEXT);
        UIManager.put("TabbedPane.selected",            BG_COMPONENT);
        UIManager.put("TabbedPane.contentAreaColor",    BG_PANEL);

        UIManager.put("SplitPane.background",           BG_PANEL);
        UIManager.put("SplitPaneDivider.background",    BORDER_COLOR);

        UIManager.put("MenuBar.background",             BG_HEADER);
        UIManager.put("MenuBar.foreground",             FG_TEXT);
        UIManager.put("MenuBar.border",                 BorderFactory.createMatteBorder(0,0,1,0, BORDER_COLOR));
        UIManager.put("Menu.background",                BG_HEADER);
        UIManager.put("Menu.foreground",                FG_TEXT);
        UIManager.put("Menu.selectionBackground",       BG_SELECTED);
        UIManager.put("Menu.selectionForeground",       FG_WHITE);
        UIManager.put("MenuItem.background",            BG_COMPONENT);
        UIManager.put("MenuItem.foreground",            FG_TEXT);
        UIManager.put("MenuItem.selectionBackground",   BG_SELECTED);
        UIManager.put("MenuItem.selectionForeground",   FG_WHITE);
        UIManager.put("PopupMenu.background",           BG_COMPONENT);
        UIManager.put("PopupMenu.border",               BorderFactory.createLineBorder(BORDER_COLOR));

        UIManager.put("Dialog.background",              BG_PANEL);
        UIManager.put("OptionPane.background",          BG_PANEL);
        UIManager.put("OptionPane.messageForeground",   FG_TEXT);

        UIManager.put("TitledBorder.titleColor",        FG_DIM);
        UIManager.put("TitledBorder.border",            BorderFactory.createLineBorder(BORDER_COLOR));

        UIManager.put("ToolTip.background",             new Color(255, 255, 220));
        UIManager.put("ToolTip.foreground",             FG_TEXT);

        UIManager.put("List.background",                BG_COMPONENT);
        UIManager.put("List.foreground",                FG_TEXT);
        UIManager.put("List.selectionBackground",       BG_SELECTED);
        UIManager.put("List.selectionForeground",       FG_WHITE);
    }
}
