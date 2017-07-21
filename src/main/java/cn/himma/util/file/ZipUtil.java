/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月27日
 * @ClassName ZipUtil
 */
public class ZipUtil {
    private final static Logger logger = Logger.getLogger(ZipUtil.class);

    /**
     * zip文件路径分隔符，与linux相同
     */
    private static final String ZIP_FILE_SEPARATOR = "/";

    /**
     * zipFilesByPaths压缩文件
     * 
     * @param filePaths 文件绝对路径列表
     * @param zipFilePath 压缩文件路径（可不传）
     * @param zipFileName 压缩文件名称（可不传）
     * @param keepSourceFile 是否保留源文件
     * @param append 是否追加模式
     * @return boolean 是否成功
     */
    public static boolean zipFilesByPaths(List<String> filePaths, String zipFilePath, String zipFileName, boolean keepSourceFile, boolean append) {
        if (CollectionUtils.isEmpty(filePaths)) {
            throw new IllegalArgumentException("filePaths不能为空");
        }
        String commonPath = findCommonPath(filePaths);
        logger.info("公共路径为：" + commonPath);
        if (!StringUtils.hasText(zipFilePath)) {
            zipFilePath = commonPath.substring(commonPath.lastIndexOf(File.separator) + 1);
        }
        if (!StringUtils.hasText(zipFilePath)) {
            zipFilePath = commonPath.substring(commonPath.lastIndexOf(File.separator) + 1);
        }
        List<File> files = getFilesByPaths(filePaths);
        return zipFilesByFiles(commonPath, files, zipFilePath, zipFileName, keepSourceFile, append);
    }

    /**
     * zipFilesByFiles压缩文件
     * 
     * @param files 文件列表
     * @param zipFilePath 压缩文件路径（可不传）
     * @param zipFileName 压缩文件名称（可不传）
     * @param keepSourceFile 是否保留源文件
     * @param append 是否追加模式
     * @return boolean 是否成功
     */
    public static boolean zipFilesByFiles(List<File> files, String zipFilePath, String zipFileName, boolean keepSourceFile, boolean append) {
        if (CollectionUtils.isEmpty(files)) {
            logger.error("filePaths不能为空");
        }
        List<String> filePaths = new ArrayList<String>();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            if (file.exists()) {
                filePaths.add(file.getAbsolutePath());
            } else {
                files.remove(i);
                i--;
            }
        }
        String commonPath = findCommonPath(filePaths);
        return zipFilesByFiles(commonPath, files, zipFilePath, zipFileName, keepSourceFile, append);
    }

    private static boolean zipFilesByFiles(String commonPath, List<File> files, String zipFilePath, String zipFileName, boolean keepSourceFile,
            boolean append) {
        // commonPath=D:\项目读写文件\解压\A
        int pathIndex = commonPath.lastIndexOf(File.separator);
        String path = null;
        String name = null;
        if (pathIndex < 0) {
            path = "";
            name = commonPath;
        } else {
            path = commonPath.substring(0, pathIndex);
            name = commonPath.substring(pathIndex + 1);
        }
        if (!StringUtils.hasText(zipFilePath)) {
            zipFilePath = path;
        }
        if (!StringUtils.hasText(zipFileName)) {
            zipFileName = name;
        }
        File zipPathFile = new File(zipFilePath);
        if (!zipPathFile.isDirectory()) {
            logger.error(zipFilePath + "不是有效路径");
            return false;
        }
        if (!zipPathFile.exists()) {
            zipPathFile.mkdirs();
        }

        String zipFilePathname = zipFilePath + File.separator + zipFileName;
        File zipfile = new File(zipFilePathname);

        FileOutputStream fos = null;
        CheckedOutputStream cos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipfile, append);
            cos = new CheckedOutputStream(fos, new CRC32());
            zos = new ZipOutputStream(cos);
            for (File file : files) {
                if (!file.exists()) {
                    System.out.println("File[" + file + "] not exists!");
                    continue;
                }
                if (!file.getAbsolutePath().contains(path)) {
                    logger.error("commonPath计算错误");
                    return false;
                }
                String entryName = file.getAbsolutePath().substring(path.length() + 1).replace(File.separator, ZIP_FILE_SEPARATOR);
                if (file.isDirectory()) {
                    /**
                     * @说明：非空文件最好不写，写子文件时会自动写入，否则压缩文件会无端占用更多空间
                     * @结构：测试/B/中文/Test/UTF8.txt;测试/B/中文/UTF8.txt
                     * @压缩比例： UTF8.txt 7200B --> 89B
                     * @大小比较：重复写大小为972B ；不重复写大小为490B
                     */
                    if (file.listFiles().length == 0) {
                        zos.putNextEntry(new ZipEntry(entryName + ZIP_FILE_SEPARATOR));
                    }
                    continue;
                } else {
                    zos.putNextEntry(new ZipEntry(entryName));
                }
                FileInputStream fis = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
                if (!keepSourceFile && !files.isEmpty()) {
                    for (File xlsxfile : files) {
                        xlsxfile.delete();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null) zos.close();
                if (cos != null) cos.close();
                if (fos != null) fos.close();
            } catch (Exception e) {
                logger.warn("流关闭异常，不处理");
            }
        }
        logger.info("******************压缩完毕********************");
        return true;
    }

    /**
     * @param filePaths
     * @return
     */
    private static String findCommonPath(List<String> filePaths) {
        // 找到公共路径，用于计算压缩文件中的各文件路径
        // windows和linux
        String regex = "\\".equals(File.separator) ? "\\\\" : File.separator;

        String commonPath = filePaths.get(0);
        for (int i = 1; i < filePaths.size(); i++) {
            String filePath = filePaths.get(i);
            if (!filePath.contains(commonPath)) {
                String[] split = filePath.split(regex);
                StringBuilder sb = new StringBuilder(split[0]);
                if (!filePath.contains(sb.toString())) {
                    return "";
                }
                for (int j = 1; j < split.length; j++) {
                    if (filePath.contains(sb.toString() + File.separator + split[j])) {
                        sb.append(File.separator).append(split[j]);
                    }
                }

            }
        }
        return commonPath;
    }

    /**
     * @param filePaths
     * @param files
     */
    public static List<File> getFilesByPaths(List<String> filePaths) {
        List<File> files = new ArrayList<File>();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.error("路径无效：" + filePath);
                continue;
            }
            files.addAll(getChildFilesByFile(file));
        }
        return files;
    }

    /**
     * @param filePaths
     * @param files
     */
    public static List<File> getChildFilesByFile(File file) {
        List<File> files = new ArrayList<File>();
        // 先加入父文件
        files.add(file);
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (File child : children) {
                files.addAll(getChildFilesByFile(child));
            }
        }
        return files;
    }

    public static boolean unzipFiles(File zipFile, String unzipDir) {
        File unzipPath = new File(unzipDir);
        if (!unzipPath.exists()) {
            unzipPath.mkdirs();
        }
        ZipFile zip;
        try {
            zip = new ZipFile(zipFile);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String fileName = entry.getName();
                InputStream in = zip.getInputStream(entry);
                String outPath = unzipPath.getAbsolutePath() + File.separator + fileName.replace(ZIP_FILE_SEPARATOR, File.separator);
                logger.info("正在解压" + outPath);
                if (fileName.endsWith(ZIP_FILE_SEPARATOR)) {
                    in.close();
                    continue;
                }
                // 判断路径是否存在,不存在则创建文件路径
                File dir = new File(outPath.substring(0, outPath.lastIndexOf(File.separator)));
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                OutputStream out = new FileOutputStream(outPath);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            zip.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        logger.info("******************解压完毕********************");
        return true;
    }

    public static void main(String[] args) {

    }
}
