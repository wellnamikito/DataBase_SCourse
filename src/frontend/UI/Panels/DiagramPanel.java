package frontend.UI.Panels;

import backend.dao.ReceiptDAO;
import backend.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.lang.reflect.Method;

/**
 * Панель диаграмм.
 * Совместима с JFreeChart 1.0.x и 1.5.x через reflection для методов стилизации.
 */
public class DiagramPanel extends JPanel {

    private final ReceiptDAO receiptDAO = new ReceiptDAO();
    private JLabel      lblTitle;
    private JButton     btnRefresh;
    private JPanel      topBar;
    private JTabbedPane tabs;

    public DiagramPanel() {
        setLayout(new BorderLayout(0, 0));
        buildUI();
        loadCharts();
        applyTheme();
        ThemeManager.getInstance().addListener(() -> { applyTheme(); loadCharts(); });
        I18n.addListener(() -> { updateTexts(); loadCharts(); });
    }

    private void buildUI() {
        topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lblTitle = new JLabel();
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        topBar.add(lblTitle, BorderLayout.WEST);

        btnRefresh = new JButton();
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setOpaque(true);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnRefresh.addActionListener(e -> { loadCharts(); applyTheme(); });
        topBar.add(btnRefresh, BorderLayout.EAST);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        add(topBar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);

        updateTexts();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void loadCharts() {
        tabs.removeAll();

        Color bg   = ThemeManager.bgPanel();
        Color fg   = ThemeManager.fgText();
        boolean dark = ThemeManager.getInstance().isDark();
        Color grid = dark ? new Color(60, 60, 60) : new Color(210, 210, 210);

        try {
            // ── Pie Chart ──────────────────────────────────────────────
            List<Object[]> pieData = receiptDAO.getPriceCategoryData();
            DefaultPieDataset pieDs = new DefaultPieDataset();
            for (Object[] r : pieData)
                pieDs.setValue((Comparable) r[0], ((Number) r[1]).doubleValue());

            JFreeChart pie = ChartFactory.createPieChart(
                    I18n.t("chart.pie_title"), pieDs, true, true, false);

            pie.setBackgroundPaint(bg);
            tryCall(pie.getTitle(), "setPaint", Paint.class, fg);
            tryCallLegend(pie, bg, fg);

            Object piePlot = pie.getPlot();
            tryCall(piePlot, "setBackgroundPaint",   Paint.class,   bg);
            tryCall(piePlot, "setOutlineVisible",     boolean.class, false);
            tryCall(piePlot, "setLabelBackgroundPaint", Paint.class, bg);
            tryCall(piePlot, "setLabelPaint",         Paint.class,   fg);
            tryCall(piePlot, "setLabelOutlinePaint",  Paint.class,   null);
            tryCall(piePlot, "setLabelShadowPaint",   Paint.class,   null);

            tabs.addTab(I18n.t("chart.tab_pie"), styledPanel(pie, bg));

            // ── Bar Chart ──────────────────────────────────────────────
            List<Object[]> barData = receiptDAO.getRevenueByVideo();
            DefaultCategoryDataset barDs = new DefaultCategoryDataset();
            for (Object[] r : barData)
                barDs.addValue(((Number) r[1]).doubleValue(), I18n.t("chart.series"), (Comparable) r[0]);

            JFreeChart bar = ChartFactory.createBarChart(
                    I18n.t("chart.bar_title"),
                    I18n.t("chart.axis_store"),
                    I18n.t("chart.axis_sum"),
                    barDs, PlotOrientation.VERTICAL, false, true, false);
            styleBarChart(bar, bg, fg, grid);
            tabs.addTab(I18n.t("chart.tab_bar"), styledPanel(bar, bg));

            // ── 3D Bar Chart ───────────────────────────────────────────
            JFreeChart bar3d = ChartFactory.createBarChart3D(
                    I18n.t("chart.3d_title"),
                    I18n.t("chart.axis_store"),
                    I18n.t("chart.axis_sum"),
                    barDs, PlotOrientation.VERTICAL, false, true, false);
            styleBarChart(bar3d, bg, fg, grid);
            tabs.addTab(I18n.t("chart.tab_3d"), styledPanel(bar3d, bg));

        } catch (SQLException e) {
            JLabel err = new JLabel(I18n.t("query.error") + e.getMessage(), SwingConstants.CENTER);
            err.setForeground(new Color(239, 68, 68));
            tabs.addTab("⚠️", err);
        }

        tabs.revalidate();
        tabs.repaint();
    }

    /** Стилизовать Bar/3D chart через reflection — совместимо с обеими версиями JFreeChart */
    private void styleBarChart(JFreeChart chart, Color bg, Color fg, Color grid) {
        chart.setBackgroundPaint(bg);
        tryCall(chart.getTitle(), "setPaint", Paint.class, fg);
        tryCallLegend(chart, bg, fg);

        Object plot = chart.getPlot();
        tryCall(plot, "setBackgroundPaint",      Paint.class,   bg);
        tryCall(plot, "setOutlineVisible",        boolean.class, false);
        tryCall(plot, "setRangeGridlinePaint",    Paint.class,   grid);
        tryCall(plot, "setDomainGridlinePaint",   Paint.class,   grid);

        // Оси — получаем через reflection
        Object domainAxis = tryGet(plot, "getDomainAxis");
        if (domainAxis != null) {
            tryCall(domainAxis, "setTickLabelPaint", Paint.class, fg);
            tryCall(domainAxis, "setLabelPaint",     Paint.class, fg);
            tryCall(domainAxis, "setAxisLinePaint",  Paint.class, grid);
            tryCall(domainAxis, "setTickMarkPaint",  Paint.class, grid);
        }

        Object rangeAxis = tryGet(plot, "getRangeAxis");
        if (rangeAxis != null) {
            tryCall(rangeAxis, "setTickLabelPaint", Paint.class, fg);
            tryCall(rangeAxis, "setLabelPaint",     Paint.class, fg);
            tryCall(rangeAxis, "setAxisLinePaint",  Paint.class, grid);
            tryCall(rangeAxis, "setTickMarkPaint",  Paint.class, grid);
        }
    }

    private void tryCallLegend(JFreeChart chart, Color bg, Color fg) {
        Object legend = chart.getLegend();
        if (legend == null) return;
        tryCall(legend, "setBackgroundPaint", Paint.class, bg);
        tryCall(legend, "setItemPaint",       Paint.class, fg);
    }

    /** Вызвать метод через reflection — не падает если метод не существует в данной версии */
    private void tryCall(Object obj, String method, Class<?> paramType, Object value) {
        if (obj == null) return;
        try {
            Method m = findMethod(obj.getClass(), method, paramType);
            if (m != null) { m.setAccessible(true); m.invoke(obj, value); }
        } catch (Exception ignored) {}
    }

    private Object tryGet(Object obj, String method) {
        if (obj == null) return null;
        try {
            Method m = obj.getClass().getMethod(method);
            return m.invoke(obj);
        } catch (Exception e) { return null; }
    }

    private Method findMethod(Class<?> cls, String name, Class<?> paramType) {
        // Ищем сначала с точным типом, потом с суперклассами параметра
        try { return cls.getMethod(name, paramType); } catch (NoSuchMethodException e1) {
            try { return cls.getMethod(name, Paint.class); } catch (NoSuchMethodException e2) {
                // Обойти иерархию
                for (Method m : cls.getMethods()) {
                    if (m.getName().equals(name) && m.getParameterCount() == 1) return m;
                }
                return null;
            }
        }
    }

    private ChartPanel styledPanel(JFreeChart chart, Color bg) {
        ChartPanel cp = new ChartPanel(chart);
        cp.setBackground(bg);
        cp.setBorder(BorderFactory.createEmptyBorder());
        return cp;
    }

    private void applyTheme() {
        Color bgPanel = ThemeManager.bgPanel();
        Color bgHead  = ThemeManager.bgHeader();
        Color fgText  = ThemeManager.fgText();
        Color border  = ThemeManager.borderColor();

        setBackground(bgPanel);

        topBar.setBackground(bgHead);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, border),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        lblTitle.setForeground(fgText);
        lblTitle.setBackground(bgHead);
        lblTitle.setOpaque(true);

        btnRefresh.setBackground(ThemeManager.btnRefresh());
        btnRefresh.setForeground(Color.WHITE);

        tabs.setBackground(bgPanel);
        tabs.setForeground(fgText);

        repaint();
        revalidate();
    }

    private void updateTexts() {
        lblTitle.setText("  " + I18n.t("chart.title"));
        btnRefresh.setText(I18n.t("btn.refresh_charts"));
    }
}