/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Desc 单页xlsx文件导出器 （优点：文件占用空间较小 ；缺点：导出效率低，尤其是追加，消耗CPU、内存大）
 * @author wenge.yan
 * @Date 2016年6月12日
 * @ClassName OneSheetXlsxWriter
 */
public class OneSheetXlsxExporter {

    private static final int BUFFER_COUNT = 1000;
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * @param filePath 绝对路径文件名
     * @param head 头部
     * @param bodyTitle 内容标题
     * @param body 内容
     */
    @Deprecated
    public int exportXlsxFile(String filePath, List<Object> head, List<Object> bodyTitle, List<List<Object>> body) {
        return appendFile(filePath, false, head, bodyTitle, body, false);
    }

    @Deprecated
    public int exportXlsxFileInBatchesOld(String filePath, List<Object> head, List<Object> bodyTitle, List<List<Object>> body) {
        return appendFile(filePath, false, head, bodyTitle, body, true);
    }

    public int exportXlsxFileInBatchesNew(String filePath, List<Object> head, List<Object> bodyTitle, List<List<Object>> body) {
        return appendFile(filePath, true, head, bodyTitle, body, false);
    }

    private int appendFile(String filePath, boolean isSXSSF, List<Object> head, List<Object> bodyTitle, List<List<Object>> body, boolean needSplit) {
        if (!checkParam(filePath, head, bodyTitle, body)) {
            return 0;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Workbook workbook = null;
        Sheet sheet = null;
        int count = 0;
        File oldFile = null;
        File backFile = null;
        try {
            // ①先备份或创建
            oldFile = new File(filePath);
            if (oldFile.exists()) {
                backFile = backupFile(filePath);
                fis = new FileInputStream(filePath);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
            } else {
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("new sheet");
            }
            if (isSXSSF) {
                workbook = new SXSSFWorkbook((XSSFWorkbook) workbook, BUFFER_COUNT, true);
            }
            // ②写入
            if (needSplit && body != null && !body.isEmpty()) {
                for (int i = 0; i < body.size(); i += BUFFER_COUNT) {
                    int toIndex = i + BUFFER_COUNT < body.size() ? i + BUFFER_COUNT : body.size();
                    List<List<Object>> subList = body.subList(i, toIndex);
                    if (i == 0) {
                        count += writeContent(filePath, workbook, sheet, head, bodyTitle, subList);
                    } else {
                        count += writeContent(filePath, workbook, sheet, null, null, subList);
                    }
                }
            } else {
                count += writeContent(filePath, workbook, sheet, head, bodyTitle, body);
            }

            // ③成功后删除备份
            if (backFile != null && backFile.exists()) {
                backFile.delete();
            }
        } catch (Exception e) {
            // ③失败后恢复
            if (backFile != null && backFile.exists()) {
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                backFile.renameTo(oldFile);
            }
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
        return count;
    }

    /**
     * @param filePath
     * @param head
     * @param bodyTitle
     * @param body
     * @return
     */
    private boolean checkParam(String filePath, List<Object> head, List<Object> bodyTitle, List<List<Object>> body) {
        if (filePath == null) {
            return false;
        }
        if ((head == null || head.isEmpty()) && (bodyTitle == null || bodyTitle.isEmpty()) && (body == null || body.isEmpty())) {
            return false;
        }
        return true;
    }

    private File backupFile(String filePath) {
        String backFilePath = generateBackFilePath(filePath);
        return backupFile(filePath, backFilePath);
    }

    /**
     * @param filePath
     * @param backFilePath
     * @return
     */
    private File backupFile(String filePath, String backFilePath) {
        File backFile = new File(backFilePath);
        if (backFile.exists()) {
            backFile.delete();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ByteBuffer byteBuffer = null;
        try {
            fis = new FileInputStream(filePath);
            fos = new FileOutputStream(backFilePath);

            FileChannel ic = fis.getChannel();
            FileChannel oc = fos.getChannel();
            while (ic.position() != ic.size()) {
                int capacity = (int) (ic.size() - ic.position() < BUFFER_SIZE ? ic.size() - ic.position() : BUFFER_SIZE);
                byteBuffer = ByteBuffer.allocate(capacity);
                ic.read(byteBuffer);
                byteBuffer.flip();
                oc.write(byteBuffer);
                oc.force(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return backFile;
    }

    /**
     * @param filePath
     * @return
     */
    private String generateBackFilePath(String filePath) {
        int splitIndex = filePath.lastIndexOf(".");
        if (splitIndex <= 0) {
            throw new IllegalArgumentException("文件名称有误:" + filePath);
        }
        StringBuilder sb = new StringBuilder(filePath.substring(0, splitIndex));
        sb.append("_back_").append(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        sb.append(filePath.substring(splitIndex));
        String backFilePath = sb.toString();
        return backFilePath;
    }

    private int writeContent(String filePath, Workbook wb, Sheet sheet, List<Object> head, List<Object> bodyTitle, List<List<Object>> body)
            throws IOException {
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum == 0) {
            lastRowNum = -1;
        }
        int startIndex = lastRowNum + 1;

        if (head != null && head.size() > 0) {
            for (int j = 0; j < head.size(); j++) {
                CellStyle style = wb.createCellStyle();
                Font font = wb.createFont();
                font.setBold(true);
                style.setFont(font);
                style.setWrapText(false);
                style.setAlignment(CellStyle.ALIGN_CENTER);

                Row row = sheet.createRow(startIndex++);
                Cell cell = row.createCell(0);
                cell.setCellValue(head.get(j).toString());
                cell.setCellStyle(style);
            }
        }
        if (bodyTitle != null) {
            CellStyle style = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            style.setFont(font);
            style.setWrapText(false);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            Row row = sheet.createRow(startIndex++);
            row.setRowStyle(style);
            for (int j = 0; j < bodyTitle.size(); j++) {

                Cell cell = row.createCell(j);
                cell.setCellValue(bodyTitle.get(j).toString());
            }
        }
        if (body != null) {
            for (int i = 0; i < body.size(); i++) {
                Row row = sheet.createRow(startIndex++);
                List<Object> list = body.get(i);
                for (int j = 0; j < list.size(); j++) {
                    row.createCell(j).setCellValue(list.get(j).toString()); // 序号
                }
            }
        }
        FileOutputStream fos = new FileOutputStream(filePath);
        wb.write(fos);
        return startIndex - lastRowNum - 1;
    }

    public static void main(String[] args) {
        OneSheetXlsxExporter writer = new OneSheetXlsxExporter();
        int rows = 50000;
        int cols = 100;
        String fileName = "D:\\项目读写文件\\workbookBase.xlsx";
        String fileName1 = "D:\\项目读写文件\\workbookBase1.xlsx";
        String fileName2 = "D:\\项目读写文件\\workbookBase2.xlsx";
        String fileName3 = "D:\\项目读写文件\\workbookBase3.xlsx";
        // writer.backupFile(fileName, fileName1);
        // writer.backupFile(fileName, fileName2);
        // writer.backupFile(fileName, fileName3);
        List<Object> head = new ArrayList<Object>();
        head.add(String.valueOf(rows * cols));
        List<Object> bodyTitle = new ArrayList<Object>();
        for (int i = 0; i < cols; i++) {
            bodyTitle.add("test t" + (i + 1));
        }
        List<List<Object>> body = new ArrayList<List<Object>>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<Object>();
            for (int j = 0; j < cols; j++) {
                row.add((10 - j) + "年之前");
            }
            body.add(row);
        }
        System.out.println("原始不分批方法--------------开始");
        long startTime = System.currentTimeMillis();
        // writer.exportXlsxFile(fileName1, head, bodyTitle, body);
        long endTime = System.currentTimeMillis();
        long second = (endTime - startTime) / 1000;
        System.out.println((rows * cols) + "耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second % 60 + "s");

        System.out.println("自定义分批方法--------------开始");
        startTime = System.currentTimeMillis();
        // writer.exportXlsxFileInBatchesOld(fileName2, head, bodyTitle, body);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println((rows * cols) + "耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second % 60 + "s");

        System.out.println("新分批方法--------------开始");
        startTime = System.currentTimeMillis();
        writer.exportXlsxFileInBatchesNew(fileName3, head, bodyTitle, body);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println((rows * cols) + "耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second % 60 + "s");
    }
}
