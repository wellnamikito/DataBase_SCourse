package backend.util;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Тёмная тема.
 *
 * КРИТИЧНО для macOS:
 * - НЕ вызываем setLookAndFeel() — оставляем нативный Aqua LAF
 * - НЕ устанавливаем бордеры для Menu/MenuItem/PopupMenu/ComboBox
 * - Только цвета через ColorUIResource
 */
public class DarkTheme {

    public static void apply() {
        // НЕ МЕНЯТЬ LAF на macOS — это ломает AquaMenuPainter
        // UIManager.setLookAndFeel(...) — ЗАПРЕЩЕНО

        Color transparent = new Color(0, 0, 0, 0);

        // === Панели ===
        UIManager.put("Panel.background",              new ColorUIResource(28, 28, 28));

        // === Текст ===
        UIManager.put("Label.foreground",              new ColorUIResource(220, 220, 220));
        UIManager.put("Label.background",              new ColorUIResource(28, 28, 28));
        UIManager.put("TextField.background",          new ColorUIResource(42, 42, 42));
        UIManager.put("TextField.foreground",          new ColorUIResource(220, 220, 220));
        UIManager.put("TextField.caretForeground",     new ColorUIResource(220, 220, 220));
        UIManager.put("TextArea.background",           new ColorUIResource(42, 42, 42));
        UIManager.put("TextArea.foreground",           new ColorUIResource(220, 220, 220));
        UIManager.put("TextArea.caretForeground",      new ColorUIResource(220, 220, 220));
        UIManager.put("FormattedTextField.background", new ColorUIResource(42, 42, 42));
        UIManager.put("FormattedTextField.foreground", new ColorUIResource(220, 220, 220));
        UIManager.put("PasswordField.background",      new ColorUIResource(42, 42, 42));
        UIManager.put("PasswordField.foreground",      new ColorUIResource(220, 220, 220));

        // === Кнопки ===
        UIManager.put("Button.background",             new ColorUIResource(42, 42, 42));
        UIManager.put("Button.foreground",             new ColorUIResource(220, 220, 220));
        UIManager.put("Button.focus",                  new ColorUIResource(transparent));
        UIManager.put("ToggleButton.background",       new ColorUIResource(42, 42, 42));
        UIManager.put("ToggleButton.foreground",       new ColorUIResource(220, 220, 220));
        UIManager.put("ToggleButton.focus",            new ColorUIResource(transparent));

        // === Таблица ===
        UIManager.put("Table.background",              new ColorUIResource(33, 33, 33));
        UIManager.put("Table.foreground",              new ColorUIResource(220, 220, 220));
        UIManager.put("Table.selectionBackground",     new ColorUIResource(59, 130, 246));
        UIManager.put("Table.selectionForeground",     new ColorUIResource(255, 255, 255));
        UIManager.put("Table.gridColor",               new ColorUIResource(55, 55, 55));
        UIManager.put("TableHeader.background",        new ColorUIResource(22, 22, 22));
        UIManager.put("TableHeader.foreground",        new ColorUIResource(220, 220, 220));

        // === ScrollPane ===
        UIManager.put("ScrollPane.background",         new ColorUIResource(28, 28, 28));
        UIManager.put("Viewport.background",           new ColorUIResource(33, 33, 33));

        // === ComboBox — ТОЛЬКО цвета, НЕТ бордеров ===
        UIManager.put("ComboBox.background",           new ColorUIResource(42, 42, 42));
        UIManager.put("ComboBox.foreground",           new ColorUIResource(220, 220, 220));
        UIManager.put("ComboBox.selectionBackground",  new ColorUIResource(59, 130, 246));
        UIManager.put("ComboBox.selectionForeground",  new ColorUIResource(255, 255, 255));

        // === CheckBox / RadioButton ===
        UIManager.put("CheckBox.background",           new ColorUIResource(28, 28, 28));
        UIManager.put("CheckBox.foreground",           new ColorUIResource(220, 220, 220));
        UIManager.put("CheckBox.focus",                new ColorUIResource(transparent));
        UIManager.put("RadioButton.background",        new ColorUIResource(28, 28, 28));
        UIManager.put("RadioButton.foreground",        new ColorUIResource(220, 220, 220));

        // === Spinner ===
        UIManager.put("Spinner.background",            new ColorUIResource(42, 42, 42));
        UIManager.put("Spinner.foreground",            new ColorUIResource(220, 220, 220));

        // === TabbedPane ===
        UIManager.put("TabbedPane.background",         new ColorUIResource(28, 28, 28));
        UIManager.put("TabbedPane.foreground",         new ColorUIResource(220, 220, 220));
        UIManager.put("TabbedPane.selected",           new ColorUIResource(42, 42, 42));
        UIManager.put("TabbedPane.focus",              new ColorUIResource(transparent));

        // === SplitPane ===
        UIManager.put("SplitPane.background",          new ColorUIResource(28, 28, 28));
        UIManager.put("SplitPaneDivider.background",   new ColorUIResource(55, 55, 55));

        // === List ===
        UIManager.put("List.background",               new ColorUIResource(42, 42, 42));
        UIManager.put("List.foreground",               new ColorUIResource(220, 220, 220));
        UIManager.put("List.selectionBackground",      new ColorUIResource(59, 130, 246));
        UIManager.put("List.selectionForeground",      new ColorUIResource(255, 255, 255));

        // === Tree ===
        UIManager.put("Tree.background",               new ColorUIResource(42, 42, 42));
        UIManager.put("Tree.foreground",               new ColorUIResource(220, 220, 220));

        // === OptionPane ===
        UIManager.put("OptionPane.background",         new ColorUIResource(28, 28, 28));
        UIManager.put("OptionPane.messageForeground",  new ColorUIResource(220, 220, 220));

        // === ToolTip ===
        UIManager.put("ToolTip.background",            new ColorUIResource(42, 42, 42));
        UIManager.put("ToolTip.foreground",            new ColorUIResource(220, 220, 220));

        // === Menu — ТОЛЬКО цвета, НЕТ бордеров (macOS Aqua NPE!) ===
        UIManager.put("MenuBar.background",            new ColorUIResource(22, 22, 22));
        UIManager.put("MenuBar.foreground",            new ColorUIResource(220, 220, 220));
        UIManager.put("Menu.background",               new ColorUIResource(22, 22, 22));
        UIManager.put("Menu.foreground",               new ColorUIResource(220, 220, 220));
        UIManager.put("Menu.selectionBackground",      new ColorUIResource(59, 130, 246));
        UIManager.put("Menu.selectionForeground",      new ColorUIResource(255, 255, 255));
        UIManager.put("MenuItem.background",           new ColorUIResource(28, 28, 28));
        UIManager.put("MenuItem.foreground",           new ColorUIResource(220, 220, 220));
        UIManager.put("MenuItem.selectionBackground",  new ColorUIResource(59, 130, 246));
        UIManager.put("MenuItem.selectionForeground",  new ColorUIResource(255, 255, 255));
        UIManager.put("PopupMenu.background",          new ColorUIResource(28, 28, 28));
    }
}
