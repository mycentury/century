/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Desc 单个多页xlsx文件读写工具 （优点：文件占用空间较小 ；缺点：导出效率略低，尤其是追加，消耗CPU、内存大）
 * @author wewenge.yan
 * @Date 2016年9月1日
 * @ClassName ExcelUtil
 */
public class ExcelOperateUtil {
    private static final int BUFFER_COUNT = 1000;

    public static int write(String filePath, ExcelData data, String sheetName) {
        return write(filePath, data, sheetName, true, false);
    }

    public static Map<String, List<Row>> read(String filePath) {
        Map<String, List<Row>> sheets = new HashMap<String, List<Row>>();
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("文件路径有误:" + filePath);
            }
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            int pages = workbook.getNumberOfSheets();
            for (int i = 0; i < pages; i++) {
                List<Row> rows = new ArrayList<Row>();
                Sheet sheet = workbook.getSheetAt(i);
                for (Row row : sheet) {
                    rows.add(row);
                }
                sheets.put(sheet.getSheetName(), rows);
            }
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sheets;
    }

    /**
     * @param filePath
     * @param data
     * @param sheetAppendable sheet页追加模式
     * @param rowAppendable row行追加模式
     * @return
     */
    public static int write(String filePath, ExcelData data, String sheetName, boolean sheetAppendable, boolean rowAppendable) {
        if (!checkParam(filePath, data)) {
            return 0;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        XSSFWorkbook workbook = null;
        SXSSFWorkbook sxssfWorkbook = null;
        Sheet sheet = null;
        int rows = 0;
        File oldFile = null;
        File backFile = null;
        try {
            // ①先备份或创建
            oldFile = new File(filePath);
            if (oldFile.exists()) {
                backFile = FileBackUtil.backupFile(filePath);
            } else if (!oldFile.getParentFile().exists()) {
                oldFile.getParentFile().mkdirs();
            }
            if (oldFile.exists() && sheetAppendable) {
                fis = new FileInputStream(filePath);
                workbook = new XSSFWorkbook(fis);
            } else {
                workbook = new XSSFWorkbook();
            }
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            } else {
                System.out.println("[INFO]" + filePath + "[" + sheetName + "]已经存在！目前采用" + (rowAppendable ? "追加" : "覆盖") + "模式");
            }
            sxssfWorkbook = new SXSSFWorkbook(workbook, BUFFER_COUNT, true);
            // ②写入
            rows += writeContent(filePath, sxssfWorkbook, sheet, data, rowAppendable);

            // ③成功后删除备份
            if (backFile != null && backFile.exists()) {
                backFile.delete();
            }
        } catch (Exception e) {
            // ③失败后恢复
            FileBackUtil.recoverFile(backFile, oldFile, true);
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (fis != null) fis.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rows;
    }

    /**
     * @param filePath
     * @param head
     * @param bodyTitle
     * @param body
     * @return
     */
    private static boolean checkParam(String filePath, ExcelData data) {
        if (filePath == null) {
            return false;
        }
        if ((data.head == null || data.head.isEmpty()) && (data.title == null || data.title.isEmpty()) && (data.body == null || data.body.isEmpty())) {
            return false;
        }
        return true;
    }

    private static int writeContent(String filePath, Workbook wb, Sheet sheet, ExcelData excelData, boolean rowAppendable) throws IOException {
        int startIndex = rowAppendable && sheet.getRow(0) != null ? sheet.getLastRowNum() + 1 : 0;

        if (excelData.head != null && excelData.head.size() > 0) {
            for (int j = 0; j < excelData.head.size(); j++) {
                Row row = sheet.createRow(startIndex++);
                if (excelData.headStyle != null) {
                    row.setRowStyle(excelData.headStyle);
                }
                Cell cell = row.createCell(0);
                Object object = excelData.head.get(j);
                cell.setCellValue(object == null ? "null" : object.toString());
            }
        }
        if (excelData.title != null) {
            Row row = sheet.createRow(startIndex++);
            if (excelData.titleStyle != null) {
                row.setRowStyle(excelData.titleStyle);
            }
            for (int j = 0; j < excelData.title.size(); j++) {
                Cell cell = row.createCell(j);
                Object object = excelData.title.get(j);
                cell.setCellValue(object == null ? "null" : object.toString());
            }
        }
        if (excelData.body != null) {
            for (int i = 0; i < excelData.body.size(); i++) {
                Row row = sheet.createRow(startIndex++);
                if (excelData.bodyStyle != null) {
                    row.setRowStyle(excelData.bodyStyle);
                }
                List<Object> list = excelData.body.get(i);
                for (int j = 0; j < list.size(); j++) {
                    Object object = list.get(j);
                    row.createCell(j).setCellValue(object == null ? "null" : object.toString());
                }
            }
        }
        FileOutputStream fos = new FileOutputStream(filePath);
        wb.write(fos);
        return (sheet.getRow(0) != null ? sheet.getLastRowNum() + 1 : 0) - startIndex;
    }

    public static class ExcelData {
        private List<Object> head;
        private List<Object> title;
        private List<List<Object>> body;
        private CellStyle headStyle;
        private CellStyle titleStyle;
        private CellStyle bodyStyle;

        public ExcelData(List<Object> head, List<Object> title, List<List<Object>> body, CellStyle headStyle, CellStyle titleStyle,
                CellStyle bodyStyle) {
            super();
            this.head = head;
            this.title = title;
            this.body = body;
            this.headStyle = headStyle;
            this.titleStyle = titleStyle;
            this.bodyStyle = bodyStyle;
        }

        public ExcelData(List<List<Object>> body) {
            super();
            this.body = body;
        }
    }

    public static void main(String[] args) throws IOException {
        String filePath = "D:\\项目读写文件\\source.xlsx";
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            System.out.println(sheet.getFirstRowNum() + "，" + sheet.getLastRowNum());
            System.out.println(sheet.getRow(0));
        }
    }
}
