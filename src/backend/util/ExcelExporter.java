package backend.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Утилита для экспорта JTable / DefaultTableModel в Excel (.xlsx).
 * Использует Apache POI.
 */
public class ExcelExporter {

    /**
     * Экспортировать TableModel в Excel-файл с заголовком.
     *
     * @param model     данные таблицы
     * @param title     заголовок отчёта (строка 0)
     * @param filePath  путь к сохраняемому файлу
     */
    public static void export(DefaultTableModel model, String title, String filePath) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Отчёт");

            // Стили
            CellStyle headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle titleStyle = wb.createCellStyle();
            Font titleFont = wb.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            int cols = model.getColumnCount();

            // Строка заголовка отчёта
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(title);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, cols - 1));

            // Строка с именами колонок
            Row colRow = sheet.createRow(1);
            for (int c = 0; c < cols; c++) {
                Cell cell = colRow.createCell(c);
                cell.setCellValue(model.getColumnName(c));
                cell.setCellStyle(headerStyle);
            }

            // Данные
            for (int r = 0; r < model.getRowCount(); r++) {
                Row row = sheet.createRow(r + 2);
                for (int c = 0; c < cols; c++) {
                    Cell cell = row.createCell(c);
                    Object val = model.getValueAt(r, c);
                    if (val == null) {
                        cell.setCellValue("");
                    } else if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else {
                        cell.setCellValue(val.toString());
                    }
                    cell.setCellStyle(dataStyle);
                }
            }

            // Автоширина колонок
            for (int c = 0; c < cols; c++) {
                sheet.autoSizeColumn(c);
            }

            // Сохранить файл
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                wb.write(fos);
            }
        }
    }

    /**
     * Показать диалог выбора файла и экспортировать.
     */
    public static void exportWithDialog(JFrame parent, DefaultTableModel model, String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(title.replaceAll("[^a-zA-Zа-яА-Я0-9]", "_") + ".xlsx"));
        chooser.setDialogTitle("Сохранить отчёт как...");
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".xlsx")) path += ".xlsx";
            try {
                export(model, title, path);
                JOptionPane.showMessageDialog(parent, "Файл сохранён:\n" + path, "Экспорт Excel", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Ошибка экспорта:\n" + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}