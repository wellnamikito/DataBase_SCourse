package backend.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static ThemeManager instance;
    private Theme current = Theme.DARK;
    private final List<Runnable> listeners = new ArrayList<>();

    private ThemeManager() {}

    public static ThemeManager getInstance() {
        if (instance == null) instance = new ThemeManager();
        return instance;
    }

    public Theme getTheme() { return current; }

    // Нестатический — только через getInstance().isDark()
    public boolean isDark() { return current == Theme.DARK; }

    public void setTheme(Theme theme) {
        if (current == theme) return;
        current = theme;
        if (theme == Theme.DARK) DarkTheme.apply();
        else                     LightTheme.apply();
        repaintAll();
        listeners.forEach(Runnable::run);
    }

    public void toggle() {
        setTheme(current == Theme.DARK ? Theme.LIGHT : Theme.DARK);
    }

    public void addListener(Runnable r) { listeners.add(r); }

    public static void repaintAll() {
        for (Window w : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(w);
            w.invalidate();
            w.validate();
            w.repaint();
        }
    }

    // Единственный приватный статический хелпер — используется только внутри этого класса
    private static boolean d() {
        return getInstance().current == Theme.DARK;
    }

    // ======= Цвета фона =======
    public static Color bgPanel()     { return d() ? new Color(28,28,28)   : new Color(250,250,250); }
    public static Color bgHeader()    { return d() ? new Color(22,22,22)   : new Color(241,243,245); }
    public static Color bgComponent() { return d() ? new Color(42,42,42)   : Color.WHITE; }
    public static Color bgTable()     { return d() ? new Color(33,33,33)   : Color.WHITE; }
    public static Color bgSelected()  { return new Color(59,130,246); }

    // ======= Цвета текста =======
    public static Color fgText()      { return d() ? new Color(220,220,220): new Color(30,30,30); }
    public static Color fgDim()       { return d() ? new Color(140,140,140): new Color(100,100,100); }
    public static Color borderColor() { return d() ? new Color(55,55,55)   : new Color(210,210,210); }

    // ======= Цвета кнопок =======
    public static Color btnAdd()      { return new Color(22,163,74); }
    public static Color btnEdit()     { return new Color(37,99,235); }
    public static Color btnDelete()   { return new Color(185,28,28); }
    public static Color btnRefresh()  { return d() ? new Color(71,85,105)  : new Color(100,116,139); }
    public static Color btnExport()   { return new Color(22,163,74); }
    public static Color btnRun()      { return new Color(37,99,235); }
    public static Color btnFunc()     { return new Color(124,58,237); }
}

