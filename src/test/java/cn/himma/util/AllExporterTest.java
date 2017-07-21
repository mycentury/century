/**
 * 
 */
package cn.himma.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.gen5.api.Test;

import cn.himma.BaseTest;
import cn.himma.util.file.OneSheetXlsxExporter;
import cn.himma.util.file.OneSheetXlsxGenerater;
import cn.himma.util.file.OneSheetXmlExporter;
import cn.himma.util.file.OneSheetXmlStringExporter;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月15日
 * @ClassName AllExporterTest
 */
public class AllExporterTest extends BaseTest {

    @Test
    public void testAllXlsxAndXmlExporter() {
        int rows = 10 * 10000;
        int cols = 20;
        List<Object> heads = new ArrayList<Object>();
        heads.add(String.valueOf(rows * cols));
        List<Object> titles = new ArrayList<Object>();
        for (int i = 0; i < cols; i++) {
            titles.add("test t" + (i + 1));
        }
        List<List<Object>> datas = new ArrayList<List<Object>>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<Object>();
            for (int j = 0; j < cols; j++) {
                row.add((10 - j) + "年之前,我不认识你");
            }
            datas.add(row);
        }
        String fileName1 = "D:\\项目读写文件\\export1.xlsx";
        String fileName2 = "D:\\项目读写文件\\export2.xml";
        String fileName3 = "D:\\项目读写文件\\export3.xml";
        String fileName4 = "D:\\项目读写文件\\export4.xlsx";

        long startTime;
        long endTime;
        long second;
        System.out.println(rows + " X " + cols);
        startTime = System.currentTimeMillis();
        OneSheetXlsxExporter one = new OneSheetXlsxExporter();
        one.exportXlsxFileInBatchesNew(fileName1, heads, titles, datas);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println("Excel\tPOI读POI写\t文件大小：" + calulateFileSize(fileName1) + "\t\t耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m"
                + second % 60 + "s");

        startTime = System.currentTimeMillis();
        OneSheetXmlExporter two = new OneSheetXmlExporter();
        two.exportExcelXmlFile(fileName2, true, heads, titles, datas);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println("Xml\tDOM读Str写\t文件大小：" + calulateFileSize(fileName2) + "\t\t耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second
                % 60 + "s");

        startTime = System.currentTimeMillis();
        OneSheetXmlStringExporter three = new OneSheetXmlStringExporter();
        three.exportExcelXmlFile(fileName3, true, heads, titles, datas);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println("Xml\tStr读Str写\t文件大小：" + calulateFileSize(fileName3) + "\t\t耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second
                % 60 + "s");

        startTime = System.currentTimeMillis();
        OneSheetXlsxGenerater four = new OneSheetXlsxGenerater();
        four.exportXlsxFile(fileName4, true, new OneSheetXlsxGenerater.Data(heads, titles, datas));
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println("Excel\tObj读POI写\t文件大小：" + calulateFileSize(fileName4) + "\t\t耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m"
                + second % 60 + "s");
    }

    @Test
    public void testXmlStringExporter() {
        int rows = 50 * 10000;
        int cols = 50;
        List<Object> heads = new ArrayList<Object>();
        heads.add(String.valueOf(rows * cols));
        List<Object> titles = new ArrayList<Object>();
        for (int i = 0; i < cols; i++) {
            titles.add("test t" + (i + 1));
        }
        List<List<Object>> datas = new ArrayList<List<Object>>();
        for (int i = 0; i < rows; i++) {
            List<Object> row = new ArrayList<Object>();
            for (int j = 0; j < cols; j++) {
                row.add((10 - j) + "年之前,我不认识你");
            }
            datas.add(row);
        }
        String fileName5 = "D:\\项目读写文件\\export5.xml";

        System.out.println(rows + " X " + cols);
        long startTime;
        long endTime;
        long second;
        startTime = System.currentTimeMillis();
        OneSheetXmlStringExporter three = new OneSheetXmlStringExporter();
        three.exportExcelXmlFile(fileName5, true, heads, titles, datas);
        endTime = System.currentTimeMillis();
        second = (endTime - startTime) / 1000;
        System.out.println("Xml\tStr读Str写\t文件大小：" + calulateFileSize(fileName5) + "\t\t耗时:" + second / 3600 + "h" + (second / 60) % 60 + "m" + second
                % 60 + "s");
    }

    private String calulateFileSize(String filePath) {
        File file = new File(filePath);
        double size = ((double) (file.length())) / 1024 / 1024;
        return String.format("%.2f", size) + "M";
    }

}
