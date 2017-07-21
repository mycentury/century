/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Row;

import cn.himma.util.file.ExcelOperateUtil.ExcelData;

/**
 * @Desc Excel与properties互转的工具类（实质上含有具体业务，仅供参考）
 * @author wewenge.yan
 * @Date 2016年9月1日
 * @ClassName Excel2PropertiesUtil
 */
public class Excel2PropertiesUtil {
    private static final String CN_SUFFIX = "_cn.properties";
    private static final String EN_SUFFIX = "_en.properties";
    private static final String EXCEL_SUFFIX = ".xlsx";

    /**
     * excel转properties,只支持文件(不再使用，应该private化)
     * 
     * @param excelPath
     * @param cnPropPath
     * @param enPropPath
     */
    private static void convertFromExcelToProperties(String excelPath) {
        if (!excelPath.endsWith(EXCEL_SUFFIX)) {
            throw new IllegalArgumentException("excel文件路径必须以" + EXCEL_SUFFIX + "结尾！");
        }
        Map<String, List<Row>> sheets = ExcelOperateUtil.read(excelPath);
        Properties cnProp = new Properties();
        Properties enProp = new Properties();
        for (Entry<String, List<Row>> sheet : sheets.entrySet()) {
            for (Row row : sheet.getValue()) {
                if (row != null && row.getCell(0) != null && row.getCell(1) != null) {
                    cnProp.setProperty(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
                }
                if (row != null && row.getCell(0) != null && row.getCell(2) != null) {
                    enProp.setProperty(row.getCell(0).getStringCellValue(), row.getCell(2).getStringCellValue());
                }
            }
            FileInfo fileInfo = getFileInfoByPath(excelPath);
            String filePath = fileInfo.path + "/" + fileInfo.name;
            File file = new File(filePath);
            if (file.isDirectory() && !file.exists()) {
                file.mkdirs();
            }
            PropertiesOperateUtil.write(filePath + "/" + sheet.getKey() + CN_SUFFIX, cnProp);
            PropertiesOperateUtil.write(filePath + "/" + sheet.getKey() + EN_SUFFIX, enProp);
        }
    }

    /**
     * properties转excel,只支持单个文件(不再使用，应该private化)
     * 
     * @param cnPropPath
     * @param enPropPath
     * @param excelPath
     */
    private static void convertFromPropertiesToExcel(String propPath) {
        if (!propPath.endsWith(CN_SUFFIX) && !propPath.endsWith(EN_SUFFIX)) {
            throw new IllegalArgumentException("properties文件路径必须以" + CN_SUFFIX + "或者" + EN_SUFFIX + "结尾！propPath=" + propPath);
        }
        String cnPropPath = propPath.replace(EN_SUFFIX, CN_SUFFIX);
        String enPropPath = propPath.replace(CN_SUFFIX, EN_SUFFIX);
        Properties cnProp = PropertiesOperateUtil.read(cnPropPath);
        Properties enProp = PropertiesOperateUtil.read(enPropPath);
        if (cnProp.keySet().size() < enProp.keySet().size()) {
            throw new RuntimeException(cnPropPath + "中文字典不全");
        }
        List<List<Object>> body = new ArrayList<List<Object>>();
        for (Entry<Object, Object> entry : cnProp.entrySet()) {
            List<Object> row = new ArrayList<Object>();
            Object key = entry.getKey();
            row.add(key);
            row.add(entry.getValue());
            row.add(enProp.getProperty(key == null ? null : key.toString()));
            body.add(row);
        }
        FileInfo fileInfo = getFileInfoByPath(cnPropPath);
        String excelPath = fileInfo.path + EXCEL_SUFFIX;
        ExcelOperateUtil.write(excelPath, new ExcelData(body), fileInfo.name);
    }

    /**
     * excel转properties,支持文件夹
     * 
     * @param excelPath
     * @param cnPropPath
     * @param enPropPath
     */
    public static void convertAllExcelsToProperties(String excelPath) {
        File parentFile = new File(excelPath);
        if (parentFile.exists() && parentFile.isDirectory()) {
            File[] subFiles = parentFile.listFiles();
            for (File subFile : subFiles) {
                if (subFile.getPath().endsWith(EXCEL_SUFFIX)) {
                    convertFromExcelToProperties(subFile.getPath());
                }
            }
        } else if (parentFile.exists() && parentFile.isFile()) {
            convertFromExcelToProperties(excelPath);
        }
    }

    /**
     * properties转excel,支持文件夹
     * 
     * @param propPath
     * @param enPropPath
     * @param excelPath
     */
    public static void convertAllPropertiesToExcel(String propPath) {
        File parentFile = new File(propPath);
        if (parentFile.exists() && parentFile.isDirectory()) {
            File[] subFiles = parentFile.listFiles();
            for (File subFile : subFiles) {
                if (subFile.getPath().endsWith(CN_SUFFIX)) {
                    convertFromPropertiesToExcel(subFile.getPath());
                }
            }
        } else if (parentFile.exists() && parentFile.isFile()) {
            convertFromPropertiesToExcel(propPath);
        }
    }

    /**
     * @param cnPropPath
     * @return
     */
    private static FileInfo getFileInfoByPath(String filePath) {
        int lastIndexOf = filePath.lastIndexOf("/");
        if (lastIndexOf < 0) {
            lastIndexOf = filePath.lastIndexOf("\\");
        }
        if (lastIndexOf < 0) {
            throw new IllegalArgumentException("文件路径有误：" + filePath);
        }
        String path = filePath.substring(0, lastIndexOf);
        String nameAndSuff = filePath.substring(lastIndexOf + 1);
        if (nameAndSuff.endsWith(CN_SUFFIX)) {
            return new FileInfo(path, nameAndSuff.replace(CN_SUFFIX, ""), CN_SUFFIX);
        } else if (nameAndSuff.endsWith(EN_SUFFIX)) {
            return new FileInfo(path, nameAndSuff.replace(EN_SUFFIX, ""), EN_SUFFIX);
        } else if (nameAndSuff.endsWith(EXCEL_SUFFIX)) {
            return new FileInfo(path, nameAndSuff.replace(EXCEL_SUFFIX, ""), EXCEL_SUFFIX);
        }
        throw new IllegalArgumentException("文件路径解析有误：path=" + path + ",nameAndSuff" + nameAndSuff);
    }

    private static class FileInfo {
        private String path;
        private String name;
        private String suffix;

        /**
         * @param path
         * @param name
         * @param suffix
         */
        public FileInfo(String path, String name, String suffix) {
            super();
            this.path = path;
            this.name = name;
            this.suffix = suffix;
        }
    }

    /**
     * 相对路径测试---->参见Excel2PropertiesUtilTest
     * 
     * @param args
     */
    public static void main(String[] args) {
        String sourcePath = "D:\\项目读写文件\\Excel2Properties\\one\\source.xlsx";
        String propPath = "D:\\项目读写文件\\Excel2Properties\\one\\source\\menu_cn.properties";

        String path = "D:\\项目读写文件\\Excel2Properties\\multi";

        Excel2PropertiesUtil.convertAllExcelsToProperties(sourcePath);
        Excel2PropertiesUtil.convertAllPropertiesToExcel(propPath);

        Excel2PropertiesUtil.convertAllPropertiesToExcel(path);
        Excel2PropertiesUtil.convertAllExcelsToProperties(path);
    }
}
