/**
 * 
 */
package cn.himma.util.file;

import java.net.URL;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年9月2日
 * @ClassName FilePathUtil
 */
public class FilePathUtilTest {
    /**
     * 根据 给定路径（相对classes的路径或者绝对路径），获取真实路径
     * 
     * @param filePath
     * @return
     */
    public static String getRealFilePath(String filePath) {
        if (filePath.startsWith("/")) {
            return filePath;
        }
        URL resource = FilePathUtilTest.class.getResource("/");
        String rootPath = resource.getPath();
        // if (rootPath.startsWith("/")) {
        // rootPath = rootPath.substring(1);
        // }
        return rootPath.replace("test-classes", "classes") + filePath;
    }
}
