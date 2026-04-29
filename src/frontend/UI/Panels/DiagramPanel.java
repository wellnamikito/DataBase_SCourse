package frontend.UI.Panels;

import backend.dao.ReceiptDAO;
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

/**
 * Панель диаграмм:
 * - Pie chart: категории цен квитанций
 * - Bar chart: выручка по видеосалонам
 * - 3D Bar chart: выручка по видеосалонам (3D)
 */
public class DiagramPanel extends JPanel {

    private final ReceiptDAO receiptDAO = new ReceiptDAO();
    private final JTabbedPane tabs = new JTabbedPane();

    public DiagramPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("📊 Диаграммы", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 8));

        JButton btnRefresh = new JButton("🔄 Обновить диаграммы");
        btnRefresh.addActionListener(e -> loadCharts());

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(btnRefresh, BorderLayout.EAST);
        top.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        loadCharts();
    }

    private void loadCharts() {
        tabs.removeAll();
        try {
            addPieChart();
            addBarChart();
            add3DBarChart();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных для диаграмм:\n" + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pie Chart: категории цен (дешево / средне / дорого) */
    private void addPieChart() throws SQLException {
        List<Object[]> data = receiptDAO.getPriceCategoryData();
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Object[] row : data) {
            dataset.setValue((String) row[0], ((Number) row[1]).doubleValue());
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Квитанции по категориям цен",
                dataset, true, true, false
        );
        chart.setBackgroundPaint(new Color(245, 248, 255));
        tabs.addTab("🥧 Категории цен", new ChartPanel(chart));
    }

    /** Bar Chart: выручка по видеосалонам */
    private void addBarChart() throws SQLException {
        List<Object[]> data = receiptDAO.getRevenueByVideo();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] row : data) {
            dataset.addValue(((Number) row[1]).doubleValue(), "Выручка", (String) row[0]);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Выручка по видеосалонам",
                "Видеосалон", "Сумма (руб.)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );
        chart.setBackgroundPaint(new Color(245, 248, 255));
        tabs.addTab("📊 Выручка (Bar)", new ChartPanel(chart));
    }

    /** 3D Bar Chart: выручка по видеосалонам */
    private void add3DBarChart() throws SQLException {
        List<Object[]> data = receiptDAO.getRevenueByVideo();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] row : data) {
            dataset.addValue(((Number) row[1]).doubleValue(), "Выручка", (String) row[0]);
        }
        JFreeChart chart = ChartFactory.createBarChart3D(
                "Выручка по видеосалонам (3D)",
                "Видеосалон", "Сумма (руб.)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );
        chart.setBackgroundPaint(new Color(245, 248, 255));
        tabs.addTab("📦 Выручка (3D)", new ChartPanel(chart));
    }
}