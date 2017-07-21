/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.net.URL;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年9月2日
 * @ClassName FilePathUtil
 */
public class FilePathUtil {
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
        URL resource = FilePathUtil.class.getResource("/");
        String rootPath = resource.getPath();
        // if (rootPath.startsWith("/")) {
        // rootPath = rootPath.substring(1);
        // }
        return rootPath + filePath;
    }

    public static void main(String[] args) {
        String realFilePath1 = getRealFilePath("");
        String realFilePath2 = realFilePath1.substring(1);
        File file1 = new File(realFilePath1);
        File file2 = new File(realFilePath2);
        System.out.println(realFilePath1);
        System.out.println(file1.exists());
        System.out.println(file1.isDirectory());
        System.out.println(realFilePath2);
        System.out.println(file2.exists());
        System.out.println(file2.isDirectory());
    }
}
