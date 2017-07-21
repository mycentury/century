/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cn.himma.BaseTest;
import cn.himma.util.file.ZipUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月29日
 * @ClassName ZipUtilTest
 */
public class ZipUtilTest extends BaseTest {

    @Test
    public void test() {
        File unzipFile = new File("D:\\项目读写文件\\解压\\解压.zip");
        ZipUtil.unzipFiles(unzipFile, "D:\\项目读写文件\\解压\\A\\B\\C");

        List<String> filePaths = new ArrayList<String>();
        filePaths.add("D:\\项目读写文件\\解压\\测试");
        ZipUtil.zipFilesByPaths(filePaths, "D:\\项目读写文件\\解压", "压缩.zip", true, false);
    }

}
