/**
 * 
 */
package cn.himma.util.file;

import org.junit.Test;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年9月9日
 * @ClassName Excel2PropertiesUtilTest
 */
public class Excel2PropertiesUtilTest {

    /**
     * Test method for
     * {@link net.hubs1.crs.jrez.group.utils.Excel2PropertiesUtil#convertAllExcelsToProperties(java.lang.String)}.
     */
    @Test
    public void testConvertByDirectory() {
        String propPath = FilePathUtilTest.getRealFilePath("i18n");
        String excelPath = FilePathUtilTest.getRealFilePath("");
        Excel2PropertiesUtil.convertAllPropertiesToExcel(propPath);
        Excel2PropertiesUtil.convertAllExcelsToProperties(excelPath);
    }

    /**
     * Test method for
     * {@link net.hubs1.crs.jrez.group.utils.Excel2PropertiesUtil#convertAllPropertiesToExcel(java.lang.String, java.lang.String)}
     * .
     */
    @Test
    public void testConvertByFile() {
        String propPath = FilePathUtilTest.getRealFilePath("i18n/basecode_cn.properties");
        String excelPath = FilePathUtilTest.getRealFilePath("i18n.xlsx");
        Excel2PropertiesUtil.convertAllPropertiesToExcel(propPath);
        Excel2PropertiesUtil.convertAllExcelsToProperties(excelPath);
    }

}
