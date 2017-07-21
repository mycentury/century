package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * @Desc 单页以Excel（含格式）方式打开的XML文件RandomAccessFile导出器（优点：消耗CPU、内存较小，导出效率非常高，无需备份 ；缺点：文件占用空间超大，打开较慢）
 * @method DOM创建节点，DOM生成xml形式String，DOM写入基本节点，RandomAccessFile读取追加位置追加写入
 * @author wenge.yan
 * @Date 2016年6月15日
 * @ClassName OneSheetXmlExporter
 */
public class OneSheetXmlStringExporter {
    private static final String DEFAULT_CHARSET = "ISO-8859-1";
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
    public void exportExcelXmlFile(String filePath, boolean appendable, List<Object> heads, List<Object> titles, List<List<Object>> rows) {
        checkParam(filePath, heads, titles, rows);
        File file = new File(filePath);

        Document dom = null;
        File backupFile = null;
        RandomAccessFile raf = null;
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewlines(true);
        XMLWriter xmlWriter = null;
        try {
            // 先备份
            backupFile = backupFile(filePath);
            // 写入
            if (file.exists() && !appendable || !file.exists()) {
                file.delete();
                dom = createDocument(file);
                xmlWriter = new XMLWriter(new FileOutputStream(file), format);
                xmlWriter.write(dom);
                xmlWriter.close();
            }
            raf = new RandomAccessFile(file, "rw");
            // 用RandomAccessFile写入大量数据，防止内存溢出
            writeTableDatas(raf, heads, titles, rows);
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
        File xlsxfile = new File(xlsxFilePathname);
        if (!xlsxfile.exists()) {
            System.out.println("File[" + xlsxFilePathname + "] not exists.");
            return;
        }
        String zipFilePathname = zipFilePath + File.separator + zipFileName;
        FileInputStream xlsxFileIn = new FileInputStream(xlsxfile);
        File zipfile = new File(zipFilePathname);
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipfile));
        zipOutputStream.putNextEntry(new ZipEntry(xlsxfile.getName()));
        byte[] buf = new byte[1024];
        int len;
        while ((len = xlsxFileIn.read(buf)) > 0) {
            zipOutputStream.write(buf, 0, len);
        }
        zipOutputStream.closeEntry();
        xlsxFileIn.close();
        zipOutputStream.close();
        if (!keepSourceFile) {
            xlsxfile.delete();
        }
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
     * @param file
     * @param head
     * @param titles
     * @param rows
     * @return Document
     */
    private Document createDocument(File file) {
        Document dom = createDocument();
        Element workbook = createWorkbook(dom);
        setDocumentProperties(workbook);
        setExcelProperties(workbook);
        setStyles(workbook);
        setSheetTable(workbook);
        return dom;
    }

    /**
     * @param workbook
     */
    private void setSheetTable(Element workbook) {
        Element sheet = workbook.addElement("Worksheet").addAttribute("ss:Name", "Sheet1");

        Element sheetOptions = sheet.addElement("WorksheetOptions", "urn:schemas-microsoft-com:office:excel");

        Element pageSetup = sheetOptions.addElement("PageSetup");
        pageSetup.addElement("Header").addAttribute("x:Margin", "0.3");
        pageSetup.addElement("Footer").addAttribute("x:Margin", "0.3");
        pageSetup.addElement("PageMargins").addAttribute("x:Bottom", "0.75").addAttribute("x:Left", "0.7").addAttribute("x:Right", "0.7")
                .addAttribute("x:Top", "0.75");

        sheetOptions.addElement("Unsynced");

        Element print = sheetOptions.addElement("Print");
        print.addElement("ValidPrinterInfo");
        print.addElement("PaperSizeIndex").setText("9");
        print.addElement("HorizontalResolution").setText("600");
        print.addElement("VerticalResolution").setText("600");

        sheetOptions.addElement("Selected");

        Element pane = sheetOptions.addElement("Panes").addElement("Pane");
        pane.addElement("Number").setText("3");
        pane.addElement("ActiveRow").setText("1");
        pane.addElement("RangeSelection").setText("R2");

        sheetOptions.addElement("ProtectObjects").setText("False");
        sheetOptions.addElement("ProtectScenarios").setText("False");

        sheet.addElement("Table").addAttribute("x:FullColumns", "1").addAttribute("x:FullRows", "1").addAttribute("ss:DefaultColumnWidth", "54")
                .addAttribute("ss:DefaultRowHeight", "13.5")
                // 设值行数列数
                .addAttribute("ss:ExpandedColumnCount", "128").addAttribute("ss:ExpandedRowCount", "1048576").setText("");
    }

    /**
     * @param workbook
     * @throws IOException
     * @throws
     */
    private void writeTableDatas(RandomAccessFile raf, List<Object> heads, List<Object> titles, List<List<Object>> rows) throws IOException {
        StringBuilder restContent = new StringBuilder();
        int addPosition = getPositionAndRestContent(raf, "</Table>", restContent);
        // 追加行数据
        raf.seek(addPosition);
        // 分批写入数据行，防止消耗内存过大
        if (rows != null && !rows.isEmpty()) {
            for (int i = 0; i < rows.size(); i += BUFFER_COUNT) {
                int toIndex = (i + BUFFER_COUNT) < rows.size() ? (i + BUFFER_COUNT) : rows.size();
                List<List<Object>> subList = rows.subList(i, toIndex);
                String xmlData = (i == 0 ? convertTableDatasToString(heads, titles, subList) : convertTableDatasToString(null, null, subList));
                raf.write(xmlData.getBytes());
            }
        } else {
            String xmlData = convertTableDatasToString(heads, titles, null);
            raf.write(xmlData.getBytes());
        }
        raf.write(restContent.toString().getBytes());
    }

    /**
     * @param raf
     * @param addPosition
     * @param endLabel
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private int getPositionAndRestContent(RandomAccessFile raf, String tagName, StringBuilder restContent) throws IOException,
            UnsupportedEncodingException {
        int addPosition = 0;
        raf.seek(0);
        String line = null;
        boolean hasFound = false;
        while ((line = raf.readLine()) != null) {
            line = new String(line.getBytes(DEFAULT_CHARSET), Charset.defaultCharset());
            int indexOf = line.indexOf(tagName);
            if (indexOf >= 0) {
                addPosition += line.substring(0, indexOf).getBytes().length;
                hasFound = true;
                restContent.append(line.substring(indexOf));
            } else {
                if (hasFound) {
                    restContent.append(NEW_LINE).append(line);
                } else {
                    addPosition += line.getBytes().length;
                }
            }
            if (!hasFound) {
                addPosition += NEW_LINE.getBytes().length;
            }
        }
        return addPosition;
    }

    /**
     * @param workbook
     */
    private String convertTableDatasToString(List<Object> heads, List<Object> titles, List<List<Object>> rows) {
        StringBuilder sb = new StringBuilder();
        if (heads != null && !heads.isEmpty()) {
            for (Object head : heads) {
                Element row = DocumentHelper.createElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5")
                        .addAttribute("ss:StyleID", "s63");
                int mergeCols = titles != null ? titles.size() - 1 : 0;
                Element cell = row.addElement("Cell").addAttribute("ss:MergeAcross", String.valueOf(mergeCols));
                Element data = cell.addElement("Data").addAttribute("ss:Type", "String");
                data.setText(head.toString());
                sb.append(row.asXML()).append(NEW_LINE);
            }
        }

        Element titleRow = DocumentHelper.createElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5");
        if (titles != null && !titles.isEmpty()) {
            for (Object title : titles) {
                Element cell = titleRow.addElement("Cell").addAttribute("ss:StyleID", "s67");
                Element data = cell.addElement("Data").addAttribute("ss:Type", "String");
                data.setText(title.toString());
            }
        }
        sb.append(titleRow.asXML()).append(NEW_LINE);

        if (rows != null && !rows.isEmpty()) {
            for (List<Object> oneRow : rows) {
                Element row = DocumentHelper.createElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5");
                if (oneRow == null || oneRow.isEmpty()) {
                    continue;
                }
                for (int i = 0; i < oneRow.size(); i++) {
                    Element cell = row.addElement("Cell").addAttribute("ss:StyleID", "s68");
                    Element data = cell.addElement("Data").addAttribute("ss:Type", "String");
                    data.setText(oneRow.get(i).toString());
                }
                sb.append(row.asXML()).append(NEW_LINE);
            }
        }
        return sb.toString();
    }

    /**
     */
    private Document createDocument() {
        Document dom = DocumentHelper.createDocument();
        dom.addProcessingInstruction("mso-application", "progid=\"Excel.Sheet\"");
        return dom;
    }

    /**
     * @param dom
     * @return
     */
    private Element createWorkbook(Document dom) {
        Element workbook = dom.addElement("Workbook", "urn:schemas-microsoft-com:office:spreadsheet");// 添加根元素,Workbook
        workbook.addNamespace("o", "urn:schemas-microsoft-com:office:office");
        workbook.addNamespace("x", "urn:schemas-microsoft-com:office:excel");
        workbook.addNamespace("ss", "urn:schemas-microsoft-com:office:spreadsheet");
        workbook.addNamespace("html", "http://www.w3.org/TR/REC-html40");
        return workbook;
    }

    /**
     * @param workbook
     */
    private void setStyles(Element workbook) {
        Element styles = workbook.addElement("Styles");

        Element defaultStyle = styles.addElement("Style").addAttribute("ss:ID", "Default").addAttribute("ss:Name", "Normal");
        defaultStyle.addElement("Borders");
        addCommonFont(defaultStyle);
        defaultStyle.addElement("Interior");
        defaultStyle.addElement("NumberFormat");
        defaultStyle.addElement("Protection");

        Element style_63 = styles.addElement("Style").addAttribute("ss:ID", "s63");
        style_63.addElement("Alignment").addAttribute("ss:Vertical", "Center");

        Element style_67 = styles.addElement("Style").addAttribute("ss:ID", "s67");
        addCommonBorders(style_67);
        addCommonFont(style_67);

        Element style_68 = styles.addElement("Style").addAttribute("ss:ID", "s68");
        addCommonBorders(style_68);
    }

    /**
     * @param style
     */
    private void addCommonBorders(Element style) {
        Element borders = style.addElement("Borders");
        borders.addElement("Border").addAttribute("ss:Position", "Bottom").addAttribute("ss:LineStyle", "Continuous").addAttribute("ss:Weight", "1")
                .addAttribute("ss:Color", "#808080");
        borders.addElement("Border").addAttribute("ss:Position", "Left").addAttribute("ss:LineStyle", "Continuous").addAttribute("ss:Weight", "1")
                .addAttribute("ss:Color", "#808080");
        borders.addElement("Border").addAttribute("ss:Position", "Right").addAttribute("ss:LineStyle", "Continuous").addAttribute("ss:Weight", "1")
                .addAttribute("ss:Color", "#808080");
        borders.addElement("Border").addAttribute("ss:Position", "Top").addAttribute("ss:LineStyle", "Continuous").addAttribute("ss:Weight", "1")
                .addAttribute("ss:Color", "#808080");
    }

    /**
     * @return
     */
    private void addCommonFont(Element style) {
        style.addElement("Font").addAttribute("ss:FontName", "宋体").addAttribute("x:CharSet", "134").addAttribute("ss:Size", "11")
                .addAttribute("ss:Color", "#000000");
    }

    /**
     * @param workbook
     */
    private void setExcelProperties(Element workbook) {
        Element excelProperties = workbook.addElement("ExcelWorkbook", "urn:schemas-microsoft-com:office:excel");
        excelProperties.addElement("WindowHeight").setText("8445");
        excelProperties.addElement("WindowWidth").setText("24735");
        excelProperties.addElement("WindowTopX").setText("120");
        excelProperties.addElement("WindowTopY").setText("60");
        excelProperties.addElement("ProtectStructure").setText("False");
        excelProperties.addElement("ProtectWindows").setText("False");
    }

    /**
     * @param workbook
     * @return
     */
    private void setDocumentProperties(Element workbook) {
        Element documentProperties = workbook.addElement("DocumentProperties", "urn:schemas-microsoft-com:office:office");
        documentProperties.addElement("Author").setText("Yawn.Chen");
        documentProperties.addElement("Created").setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        documentProperties.addElement("LastAuthor").setText("Yawn.Chen");
        documentProperties.addElement("Updated").setText("");
        documentProperties.addElement("Company").setText("HUBS1");
        documentProperties.addElement("Version").setText("1.0.0");
    }

    private void checkParam(String filePath, List<Object> head, List<Object> titles, List<List<Object>> rows) {
        if (filePath == null) {
            throw new IllegalArgumentException("文件完整路径名不能为空");
        }
        if ((head == null || head.isEmpty()) && (titles == null || titles.isEmpty()) && (rows == null || rows.isEmpty())) {
            throw new IllegalArgumentException("待写入内容为空");
        }
    }

    public static void main(String[] args) {
        OneSheetXmlStringExporter exporter = new OneSheetXmlStringExporter();
        int rows = 1000;
        int cols = 30;
        String fileName = "D:\\项目读写文件\\testAppend.xml";
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
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            exporter.exportExcelXmlFile(fileName, true, heads, bodyTitle, body);
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
}
