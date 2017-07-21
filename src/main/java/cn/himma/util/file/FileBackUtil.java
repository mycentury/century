/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年9月2日
 * @ClassName FileBackUtil
 */
public class FileBackUtil {
    private static final int BUFFER_SIZE = 1024 * 1024;

    /**
     * 使用默认备份文件名备份
     * 
     * @param filePath
     * @return
     */
    public static File backupFile(String filePath) {
        String backFilePath = generateBackFilePath(filePath);
        return backupFile(filePath, backFilePath);
    }

    /**
     * 使用默认备份文件名恢复
     * 
     * @param backFile
     * @return
     */
    public static boolean recoverFile(File backFile, boolean forced) {
        try {
            if (backFile != null && backFile.exists()) {
                String originalName = backFile.getName().replaceFirst("_back_[0-9]{14}", "");
                String originalPath = backFile.getPath().replace(backFile.getName(), originalName);
                File originalFile = new File(originalPath);
                if (forced && originalFile.exists()) {
                    originalFile.delete();
                }
                backFile.renameTo(originalFile);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 指定文件名恢复
     * 
     * @param backFile
     * @param originalFile
     * @param forced 强制性（是否删除原文件），一般为true
     * @return
     */
    public static boolean recoverFile(File backFile, File originalFile, boolean forced) {
        try {
            if (backFile != null && backFile.exists()) {
                if (forced && originalFile.exists()) {
                    originalFile.delete();
                }
                backFile.renameTo(originalFile);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 指定文件名备份
     * 
     * @param filePath
     * @param backFilePath
     * @return
     */
    public static File backupFile(String filePath, String backFilePath) {
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
    private static String generateBackFilePath(String filePath) {
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

    public static void main(String[] args) {
        String sourcePath = "D:\\项目读写文件\\Excel2Properties\\test.xlsx";
        File backupFile = FileBackUtil.backupFile(sourcePath);
        FileBackUtil.recoverFile(backupFile, true);
    }
}
