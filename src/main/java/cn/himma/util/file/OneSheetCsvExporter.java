package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dom4j.io.OutputFormat;

/**
 * @Desc 单页以Excel方式打开的Csv文件RandomAccessFile导出器（优点：消耗CPU、内存较小，导出效率较高，导出文件较小；缺点：除了无格式外，相比之下无缺点）
 * @method RandomAccessFile读取追加位置追加写入
 * @author wenge.yan
 * @Date 2016年6月15日
 * @ClassName OneSheetCsvExporter
 */
public class OneSheetCsvExporter {
    private static final String DEFAULT_CHARSET = "GBK";
    private static final String NEW_LINE = "\n";
    private static final int BUFFER_COUNT = 1000;
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * @param filePath 绝对路径文件名
     * @param appendable 是否可追加模式写入
     * @param heads 头部
     * @param titles 标题
     * @param rows 数据行
     * @throws IOException
     */
    public void exportExcelXmlFile(String filePath, boolean appendable, List<List<Object>> rows) {
        checkParam(filePath, rows);
        File file = new File(filePath);
        File backupFile = null;
        RandomAccessFile raf = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewlines(true);
        long position = 0L;
        try {
            // 先备份
            backupFile = backupFile(filePath);
            // 写入
            if (file.exists() && !appendable) {
                file.delete();
            }
            if (file.exists() && appendable) {
                position = file.length();
            }
            raf = new RandomAccessFile(file, "rw");
            // 用RandomAccessFile写入大量数据，防止内存溢出
            writeTableDatas(raf, position, rows);
            // 删除备份
            if (backupFile != null && backupFile.exists()) {
                backupFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 恢复备份
            if (backupFile != null && backupFile.exists()) {
                if (file.exists()) {
                    file.delete();
                }
                backupFile.renameTo(file);
            }
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 压缩文件
    public void zipReservationXlsx(String xlsxFilePathname, String zipFilePath, String zipFileName, boolean keepSourceFile) throws IOException {
        List<String> xlsxFilePathnames = new ArrayList<String>();
        xlsxFilePathnames.add(xlsxFilePathname);
        zipReservationXlsx(xlsxFilePathnames, zipFilePath, zipFileName, keepSourceFile);
    }

    // 压缩文件
    public void zipReservationXlsx(List<String> xlsxFilePathnames, String zipFilePath, String zipFileName, boolean keepSourceFile) throws IOException {
        String zipFilePathname = zipFilePath + File.separator + zipFileName;
        File zipfile = new File(zipFilePathname);
        @SuppressWarnings("resource")
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipfile));
        List<File> xlsxFiles = new ArrayList<File>();
        for (String xlsxFilePathname : xlsxFilePathnames) {
            File xlsxfile = new File(xlsxFilePathname);
            if (!xlsxfile.exists()) {
                System.out.println("File[" + xlsxFilePathname + "] not exists.");
                return;
            }
            FileInputStream xlsxFileIn = new FileInputStream(xlsxfile);
            zipOutputStream.putNextEntry(new ZipEntry(xlsxfile.getName()));
            byte[] buf = new byte[1024];
            int len;
            while ((len = xlsxFileIn.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, len);
            }
            zipOutputStream.closeEntry();
            xlsxFileIn.close();

            xlsxFiles.add(xlsxfile);
        }
        zipOutputStream.close();
        if (!keepSourceFile && !xlsxFiles.isEmpty()) {
            for (File xlsxfile : xlsxFiles) {
                xlsxfile.delete();
            }
        }
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
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
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

    /**
     * @param workbook
     * @throws IOException
     * @throws
     */
    private void writeTableDatas(RandomAccessFile raf, long position, List<List<Object>> rows) throws IOException {
        // 追加行数据
        raf.seek(position);
        // 分批写入数据行，防止消耗内存过大
        if (rows != null && !rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i += BUFFER_COUNT) {
                int toIndex = (i + BUFFER_COUNT) < rows.size() ? (i + BUFFER_COUNT) : rows.size();
                List<List<Object>> subList = rows.subList(i, toIndex);
                String str = convertTableDatasToString(subList);
                raf.write(str.getBytes(DEFAULT_CHARSET));
            }
        }
    }

    /**
     * @param workbook
     */
    private String convertTableDatasToString(List<List<Object>> rows) {
        StringBuilder sb = new StringBuilder();
        if (rows != null && !rows.isEmpty()) {
            for (List<Object> oneRow : rows) {
                if (oneRow == null || oneRow.isEmpty()) {
                    continue;
                }
                for (Object cell : oneRow) {
                    if (cell != null) {
                        sb.append("\"");
                        sb.append(cell.toString().replace("\"", "\"\""));
                        sb.append("\"");
                        sb.append(",");
                    }
                }
                sb.append(NEW_LINE);
            }
        }
        return sb.toString();
    }

    private void checkParam(String filePath, List<List<Object>> rows) {
        if (filePath == null) {
            throw new IllegalArgumentException("文件完整路径名不能为空");
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("待写入内容为空");
        }
    }

    /**
     * @param fileName
     * @return
     */
    private static String getFileNameByFilePath(String fileName) {
        int startIndex = fileName.lastIndexOf("\\");
        int endIndex = fileName.lastIndexOf(".");
        if (startIndex < 0) {
            startIndex = -1;
        }
        if (endIndex < 0) {
            endIndex = fileName.length();
        }
        String name = fileName.substring(startIndex + 1, endIndex);
        return name;
    }

    public static void main(String[] args) {
        OneSheetCsvExporter exporter = new OneSheetCsvExporter();
        int rows = 50 * 10000;
        int cols = 30;
        String fileName = "D:\\项目读写文件\\csvTest.csv";
        List<Object> heads = new ArrayList<Object>();
        heads.add(String.valueOf(rows * cols));
        List<Object> bodyTitle = new ArrayList<Object>();
        for (int i = 0; i < cols; i++) {
            bodyTitle.add("test t" + (i + 1));
        }
        List<List<Object>> body = new ArrayList<List<Object>>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<Object>();
            for (int j = 0; j < cols; j++) {
                row.add((10 - j) + "年之前,我不认识你");
            }
            body.add(row);
        }
        for (int i = 0; i < 3; i++) {
            long start = System.currentTimeMillis();
            exporter.exportExcelXmlFile(fileName, true, body);
            try {
                String name = getFileNameByFilePath(fileName);
                exporter.zipReservationXlsx(fileName, "D:\\项目读写文件", name + ".zip", true);
            } catch (IOException e) {
                System.out.println("ERROR！exporter.zipReservationXlsx() failed");
            }
            long second = (System.currentTimeMillis() - start) / 1000;
            System.out.println("耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second % 60 + "s");
        }
    }
}
