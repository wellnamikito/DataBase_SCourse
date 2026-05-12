package backend.api;

import backend.data.dao.DAORegistry;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * API для формирования и экспорта отчётов
 */
public class ReportAPI {

    private final DAORegistry dao = DAORegistry.getInstance();

    // ════════ DATA ════════

    /** Однотабличный: владельцы */
    public List<Map<String, Object>> reportOwners() throws SQLException {
        return dao.views().reportOwners();
    }

    /** Многотабличный: фильмы с полной информацией */
    public List<Map<String, Object>> reportFilmsFull() throws SQLException {
        return dao.views().reportFilmsFullInfo();
    }

    /** Агрегированный: выручка по видеосалонам */
    public List<Map<String, Object>> reportRevenue() throws SQLException {
        return dao.views().reportRevenueAggregated();
    }

    // ════════ EXPORT ════════

    /**
     * Экспорт в Excel (.xlsx)
     */
    public File exportToExcel(String reportTitle, List<Map<String, Object>> data) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(reportTitle);

            // Стиль заголовка
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            if (!data.isEmpty()) {
                // Заголовки столбцов
                Row header = sheet.createRow(0);
                String[] cols = data.get(0).keySet().toArray(new String[0]);
                for (int i = 0; i < cols.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(cols[i]);
                    cell.setCellStyle(headerStyle);
                }
                // Данные
                for (int r = 0; r < data.size(); r++) {
                    Row row = sheet.createRow(r + 1);
                    int c = 0;
                    for (Object val : data.get(r).values()) {
                        Cell cell = row.createCell(c++);
                        if (val != null) cell.setCellValue(val.toString());
                    }
                }
                // Авторазмер столбцов
                for (int i = 0; i < cols.length; i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            File file = createTempFile(reportTitle, ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }
            return file;
        }
    }

    /**
     * Экспорт в TXT (tabular format)
     */
    public File exportToTxt(String reportTitle, List<Map<String, Object>> data) throws IOException {
        File file = createTempFile(reportTitle, ".txt");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("═".repeat(80));
            pw.println("  ОТЧЁТ: " + reportTitle);
            pw.println("  Дата формирования: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            pw.println("═".repeat(80));
            pw.println();

            if (!data.isEmpty()) {
                String[] cols = data.get(0).keySet().toArray(new String[0]);
                // Заголовок
                StringBuilder sb = new StringBuilder();
                for (String col : cols) {
                    sb.append(String.format("%-25s", col));
                }
                pw.println(sb);
                pw.println("─".repeat(80));
                // Строки
                for (Map<String, Object> row : data) {
                    sb = new StringBuilder();
                    for (Object val : row.values()) {
                        sb.append(String.format("%-25s", val != null ? val.toString() : ""));
                    }
                    pw.println(sb);
                }
            }
            pw.println();
            pw.println("═".repeat(80));
            pw.println("  Всего записей: " + data.size());
        }
        return file;
    }

    /**
     * Экспорт в HTML
     */
    public File exportToHtml(String reportTitle, List<Map<String, Object>> data) throws IOException {
        File file = createTempFile(reportTitle, ".html");
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            pw.println("<!DOCTYPE html><html lang='ru'><head>");
            pw.println("<meta charset='UTF-8'>");
            pw.println("<title>" + reportTitle + "</title>");
            pw.println("<style>");
            pw.println("body{font-family:'Segoe UI',sans-serif;margin:40px;background:#f8f9fa;color:#212529}");
            pw.println("h1{color:#2c3e50;border-bottom:2px solid #3498db;padding-bottom:10px}");
            pw.println("p.meta{color:#6c757d;font-size:13px}");
            pw.println("table{border-collapse:collapse;width:100%;background:#fff;border-radius:8px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.1)}");
            pw.println("th{background:#3498db;color:#fff;padding:12px 16px;text-align:left;font-weight:600}");
            pw.println("td{padding:10px 16px;border-bottom:1px solid #e9ecef}");
            pw.println("tr:last-child td{border-bottom:none}");
            pw.println("tr:nth-child(even){background:#f8f9fa}");
            pw.println("tr:hover{background:#e3f2fd}");
            pw.println(".footer{margin-top:20px;color:#6c757d;font-size:13px}");
            pw.println("</style></head><body>");
            pw.println("<h1>📊 " + reportTitle + "</h1>");
            pw.println("<p class='meta'>Дата формирования: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                    " | Всего записей: " + data.size() + "</p>");

            if (!data.isEmpty()) {
                pw.println("<table><thead><tr>");
                for (String col : data.get(0).keySet()) {
                    pw.println("<th>" + col + "</th>");
                }
                pw.println("</tr></thead><tbody>");
                for (Map<String, Object> row : data) {
                    pw.println("<tr>");
                    for (Object val : row.values()) {
                        pw.println("<td>" + (val != null ? val.toString() : "") + "</td>");
                    }
                    pw.println("</tr>");
                }
                pw.println("</tbody></table>");
            }

            pw.println("<div class='footer'>VideoRental — Система управления видеопрокатом</div>");
            pw.println("</body></html>");
        }
        return file;
    }

    private File createTempFile(String name, String ext) throws IOException {
        String safeName = name.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9_]", "_");
        return File.createTempFile("report_" + safeName + "_", ext);
    }
}
