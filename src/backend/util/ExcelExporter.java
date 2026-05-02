package backend.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Экспорт DefaultTableModel в Excel (.xlsx).
 * ИСПРАВЛЕНО: корректная запись всех строк и колонок.
 */
public class ExcelExporter {

    public static void export(DefaultTableModel model, String reportTitle, String filePath) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Отчёт");

            // === Стили ===
            CellStyle titleStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleFont.setColor(IndexedColors.WHITE.getIndex());

            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            setBorderAll(headerStyle, BorderStyle.THIN);

            CellStyle dataStyle = wb.createCellStyle();
            setBorderAll(dataStyle, BorderStyle.THIN);

            CellStyle numStyle = wb.createCellStyle();
            setBorderAll(numStyle, BorderStyle.THIN);
            numStyle.setAlignment(HorizontalAlignment.RIGHT);

            int cols = model.getColumnCount();

            // === Строка заголовка отчёта ===
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(22);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(reportTitle);
            titleCell.setCellStyle(titleStyle);
            if (cols > 1) sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols - 1));

            // === Строка колонок ===
            Row headerRow = sheet.createRow(1);
            headerRow.setHeightInPoints(18);
            for (int c = 0; c < cols; c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(model.getColumnName(c));
                cell.setCellStyle(headerStyle);
            }

            // === Данные ===
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 2);
                for (int c = 0; c < cols; c++) {
                    Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    if (val == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataStyle);
                    } else if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                        cell.setCellStyle(numStyle);
                    } else if (val instanceof Boolean) {
                        cell.setCellValue((Boolean) val);
                        cell.setCellStyle(dataStyle);
                    } else {
                        cell.setCellValue(val.toString());
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // === Автоширина ===
            for (int c = 0; c < cols; c++) {
                sheet.autoSizeColumn(c);
                // Минимальная ширина
                if (sheet.getColumnWidth(c) < 3000) sheet.setColumnWidth(c, 3000);
            }

            // === Сохранение ===
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
        }
    }

    private static void setBorderAll(CellStyle s, BorderStyle bs) {
        s.setBorderTop(bs);
        s.setBorderBottom(bs);
        s.setBorderLeft(bs);
        s.setBorderRight(bs);
    }

    /**
     * Показать диалог выбора файла и экспортировать.
     */
    public static void exportWithDialog(JFrame parent, DefaultTableModel model, String title) {
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent,
                    I18n.t("report.no_data"), I18n.t("msg.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        // Безопасное имя файла
        String safeName = title.replaceAll("[^a-zA-Zа-яА-Я0-9_\\- ]", "_").trim();
        if (safeName.isEmpty()) safeName = "report";
        chooser.setSelectedFile(new File(safeName + ".xlsx"));
        chooser.setDialogTitle(I18n.t("excel.save_as"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xlsx")) path += ".xlsx";
            try {
                export(model, title, path);
                JOptionPane.showMessageDialog(parent,
                        I18n.t("excel.saved") + path,
                        I18n.t("excel.title"), JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent,
                        I18n.t("excel.err") + e.getMessage(),
                        I18n.t("msg.error"), JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}