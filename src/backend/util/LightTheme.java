package backend.util;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * Светлая тема.
 * НЕ меняем LAF — только цвета через ColorUIResource.
 */
public class LightTheme {

    public static void apply() {
        // НЕ МЕНЯТЬ LAF

        Color transparent = new Color(0, 0, 0, 0);

        UIManager.put("Panel.background",              new ColorUIResource(250, 250, 250));
        UIManager.put("Label.foreground",              new ColorUIResource(30, 30, 30));
        UIManager.put("Label.background",              new ColorUIResource(250, 250, 250));

        UIManager.put("TextField.background",          new ColorUIResource(255, 255, 255));
        UIManager.put("TextField.foreground",          new ColorUIResource(30, 30, 30));
        UIManager.put("TextField.caretForeground",     new ColorUIResource(30, 30, 30));
        UIManager.put("TextArea.background",           new ColorUIResource(255, 255, 255));
        UIManager.put("TextArea.foreground",           new ColorUIResource(30, 30, 30));
        UIManager.put("TextArea.caretForeground",      new ColorUIResource(30, 30, 30));
        UIManager.put("FormattedTextField.background", new ColorUIResource(255, 255, 255));
        UIManager.put("FormattedTextField.foreground", new ColorUIResource(30, 30, 30));

        UIManager.put("Button.background",             new ColorUIResource(241, 243, 245));
        UIManager.put("Button.foreground",             new ColorUIResource(30, 30, 30));
        UIManager.put("Button.focus",                  new ColorUIResource(transparent));
        UIManager.put("ToggleButton.background",       new ColorUIResource(241, 243, 245));
        UIManager.put("ToggleButton.foreground",       new ColorUIResource(30, 30, 30));
        UIManager.put("ToggleButton.focus",            new ColorUIResource(transparent));

        UIManager.put("Table.background",              new ColorUIResource(255, 255, 255));
        UIManager.put("Table.foreground",              new ColorUIResource(30, 30, 30));
        UIManager.put("Table.selectionBackground",     new ColorUIResource(59, 130, 246));
        UIManager.put("Table.selectionForeground",     new ColorUIResource(255, 255, 255));
        UIManager.put("Table.gridColor",               new ColorUIResource(210, 210, 210));
        UIManager.put("TableHeader.background",        new ColorUIResource(241, 243, 245));
        UIManager.put("TableHeader.foreground",        new ColorUIResource(30, 30, 30));

        UIManager.put("ScrollPane.background",         new ColorUIResource(250, 250, 250));
        UIManager.put("Viewport.background",           new ColorUIResource(255, 255, 255));

        UIManager.put("ComboBox.background",           new ColorUIResource(255, 255, 255));
        UIManager.put("ComboBox.foreground",           new ColorUIResource(30, 30, 30));
        UIManager.put("ComboBox.selectionBackground",  new ColorUIResource(59, 130, 246));
        UIManager.put("ComboBox.selectionForeground",  new ColorUIResource(255, 255, 255));

        UIManager.put("CheckBox.background",           new ColorUIResource(250, 250, 250));
        UIManager.put("CheckBox.foreground",           new ColorUIResource(30, 30, 30));
        UIManager.put("CheckBox.focus",                new ColorUIResource(transparent));
        UIManager.put("RadioButton.background",        new ColorUIResource(250, 250, 250));
        UIManager.put("RadioButton.foreground",        new ColorUIResource(30, 30, 30));

        UIManager.put("Spinner.background",            new ColorUIResource(255, 255, 255));
        UIManager.put("Spinner.foreground",            new ColorUIResource(30, 30, 30));

        UIManager.put("TabbedPane.background",         new ColorUIResource(250, 250, 250));
        UIManager.put("TabbedPane.foreground",         new ColorUIResource(30, 30, 30));
        UIManager.put("TabbedPane.selected",           new ColorUIResource(255, 255, 255));
        UIManager.put("TabbedPane.focus",              new ColorUIResource(transparent));

        UIManager.put("SplitPane.background",          new ColorUIResource(250, 250, 250));
        UIManager.put("SplitPaneDivider.background",   new ColorUIResource(210, 210, 210));

        UIManager.put("List.background",               new ColorUIResource(255, 255, 255));
        UIManager.put("List.foreground",               new ColorUIResource(30, 30, 30));
        UIManager.put("List.selectionBackground",      new ColorUIResource(59, 130, 246));
        UIManager.put("List.selectionForeground",      new ColorUIResource(255, 255, 255));

        UIManager.put("Tree.background",               new ColorUIResource(255, 255, 255));
        UIManager.put("Tree.foreground",               new ColorUIResource(30, 30, 30));

        UIManager.put("OptionPane.background",         new ColorUIResource(250, 250, 250));
        UIManager.put("OptionPane.messageForeground",  new ColorUIResource(30, 30, 30));

        UIManager.put("ToolTip.background",            new ColorUIResource(255, 255, 220));
        UIManager.put("ToolTip.foreground",            new ColorUIResource(30, 30, 30));

        // Menu — только цвета
        UIManager.put("MenuBar.background",            new ColorUIResource(241, 243, 245));
        UIManager.put("MenuBar.foreground",            new ColorUIResource(30, 30, 30));
        UIManager.put("Menu.background",               new ColorUIResource(241, 243, 245));
        UIManager.put("Menu.foreground",               new ColorUIResource(30, 30, 30));
        UIManager.put("Menu.selectionBackground",      new ColorUIResource(59, 130, 246));
        UIManager.put("Menu.selectionForeground",      new ColorUIResource(255, 255, 255));
        UIManager.put("MenuItem.background",           new ColorUIResource(250, 250, 250));
        UIManager.put("MenuItem.foreground",           new ColorUIResource(30, 30, 30));
        UIManager.put("MenuItem.selectionBackground",  new ColorUIResource(59, 130, 246));
        UIManager.put("MenuItem.selectionForeground",  new ColorUIResource(255, 255, 255));
        UIManager.put("PopupMenu.background",          new ColorUIResource(250, 250, 250));
    }
}
