/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @Desc Properties文件读写工具类
 * @author wewenge.yan
 * @Date 2016年9月1日
 * @ClassName PropertiesUtil
 */
public class PropertiesOperateUtil {
    public static Properties read(String filePath) {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            properties.load(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void write(String filePath, Properties properties) {
        write(filePath, properties, null, false);
    }

    public static void write(String filePath, Properties properties, String comments, boolean appendable) {
        File originalFile = null;
        File backupFile = null;
        try {
            originalFile = new File(filePath);
            if (originalFile.exists()) {
                backupFile = FileBackUtil.backupFile(filePath);
            } else if (!originalFile.getParentFile().exists()) {
                originalFile.getParentFile().mkdirs();
            }
            if (appendable && originalFile.exists()) {
                writeInAppendMode(filePath, properties, comments);
            } else {
                writeInOverrideMode(filePath, properties, comments);
            }
            if (backupFile != null) {
                backupFile.delete();
            }
        } catch (Exception e) {
            FileBackUtil.recoverFile(backupFile, originalFile, true);
            e.printStackTrace();
        }
    }

    /**
     * @param filePath
     * @param properties
     * @param comments
     */
    private static void writeInAppendMode(String filePath, Properties properties, String comments) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(raf.length() - 1);
        int lastChar = raf.readChar();
        if (lastChar != '\n') {
            raf.write("\n".getBytes());
        }
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String line = entry.getKey() + "=" + entry.getValue() + "\n";
            raf.write(line.getBytes());
        }
        raf.close();
    }

    /**
     * @param filePath
     * @param properties
     * @param comments
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void writeInOverrideMode(String filePath, Properties properties, String comments) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        properties.store(fos, comments);
        fos.close();
    }
}
