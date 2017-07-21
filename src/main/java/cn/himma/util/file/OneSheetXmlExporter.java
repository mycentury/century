package cn.himma.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @Desc 单页以Excel方式打开的XML文件导出器（优点：消耗CPU、内存较小，导出效率非常高，无需备份 ；缺点：文件占用空间超大，打开较慢）
 * @method DOM创建节点，DOM读取生成xml形式String覆盖式写入
 * @author wenge.yan
 * @Date 2016年6月15日
 * @ClassName OneSheetXmlExporter
 */
public class OneSheetXmlExporter {

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
        PrintWriter pw = null;
        try {
            if (file.exists() && appendable) {
                dom = appendDocument(file, heads, titles, rows);
            } else {
                dom = createDocument(file, heads, titles, rows);
            }
            String xml = dom.asXML();
            pw = new PrintWriter(file);
            pw.write(xml);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * @param file
     * @param head
     * @param titles
     * @param rows
     * @return Document
     */
    private Document createDocument(File file, List<Object> heads, List<Object> titles, List<List<Object>> rows) {
        Document dom = createDocument();
        Element workbook = createWorkbook(dom);
        setDocumentProperties(workbook);
        setExcelProperties(workbook);
        setStyles(workbook);
        Element table = createSheetTable(workbook);
        setTableDatas(table, heads, titles, rows);
        return dom;
    }

    /**
     * @param file
     * @param head
     * @param titles
     * @param rows
     * @throws DocumentException
     */
    private Document appendDocument(File file, List<Object> heads, List<Object> titles, List<List<Object>> rows) throws DocumentException {
        SAXReader reader = new SAXReader(false);
        Document document = null;
        document = reader.read(file);
        Element table = document.getRootElement().element("Worksheet").element("Table");
        setTableDatas(table, heads, titles, rows);
        return document;
    }

    /**
     * @param workbook
     */
    private Element createSheetTable(Element workbook) {
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
        Element table = sheet.addElement("Table").addAttribute("x:FullColumns", "1").addAttribute("x:FullRows", "1")
                .addAttribute("ss:DefaultColumnWidth", "54").addAttribute("ss:DefaultRowHeight", "13.5").addAttribute("ss:ExpandedColumnCount", "10")
                .addAttribute("ss:ExpandedRowCount", "20");

        return table;
    }

    /**
     * @param workbook
     */
    private void setTableDatas(Element table, List<Object> heads, List<Object> titles, List<List<Object>> rows) {
        Attribute colCountAttr = null;
        Attribute rowCountAttr = null;
        // 重设大小
        List<Attribute> attributes = table.attributes();
        for (Attribute attr : attributes) {
            if ("ss:ExpandedColumnCount".equals(attr.getName()) || "ExpandedColumnCount".equals(attr.getName())) {
                colCountAttr = attr;
            }
            if ("ss:ExpandedRowCount".equals(attr.getName()) || "ExpandedRowCount".equals(attr.getName())) {
                rowCountAttr = attr;
            }
        }

        int colCount = Integer.valueOf(colCountAttr.getValue());
        int rowCount = Integer.valueOf(rowCountAttr.getValue());
        if (heads != null && !heads.isEmpty()) {
            rowCount += heads.size();
            for (Object head : heads) {
                Element row = table.addElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5")
                        .addAttribute("ss:StyleID", "s63");
                int mergeCols = titles != null ? titles.size() - 1 : 0;
                Element cell = row.addElement("Cell").addAttribute("ss:MergeAcross", String.valueOf(mergeCols));
                Element data = cell.addElement("Data").addAttribute("ss:Type", "String");
                data.setText(head.toString());
            }
        }

        Element titleRow = table.addElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5");
        if (titles != null && !titles.isEmpty()) {
            if (titles.size() > colCount) {
                colCount = titles.size();
            }
            rowCount += 1;
            for (Object title : titles) {
                Element cell = titleRow.addElement("Cell");
                Element data = cell.addElement("Data").addAttribute("ss:Type", "String").addAttribute("ss:StyleID", "s67");
                data.setText(title.toString());
            }
        }

        if (rows != null && !rows.isEmpty()) {
            rowCount += rows.size();
            for (List<Object> oneRow : rows) {
                Element row = table.addElement("Row").addAttribute("ss:AutoFitHeight", "0").addAttribute("ss:Heigh", "16.5");
                if (oneRow == null || oneRow.isEmpty()) {
                    continue;
                }
                if (oneRow.size() > colCount) {
                    colCount = titles.size();
                }
                for (int i = 0; i < oneRow.size(); i++) {
                    Element cell = row.addElement("Cell").addAttribute("ss:StyleID", "s68");
                    Element data = cell.addElement("Data").addAttribute("ss:Type", "String");
                    data.setText(oneRow.get(i).toString());
                }
            }
        }
        // 重设大小
        colCountAttr.setValue(String.valueOf(colCount));
        rowCountAttr.setValue(String.valueOf(rowCount));
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
        OneSheetXmlExporter exporter = new OneSheetXmlExporter();
        int rows = 100000;
        int cols = 10;
        String fileName = "D:\\项目读写文件\\workbook.xml";
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
            exporter.exportExcelXmlFile(fileName, true, heads, bodyTitle, body);
            long second = (System.currentTimeMillis() - start) / 1000;
            System.out.println("耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second % 60 + "s");
        }
    }
}
