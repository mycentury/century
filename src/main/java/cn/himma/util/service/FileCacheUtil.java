/**
 * 
 */
package cn.himma.util.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.springframework.util.CollectionUtils;

/**
 * @Desc 用于存储sessionId对应正在上传文件的信息，计算上传文件进度
 * @author wenge.yan
 * @Date 2016年7月7日
 * @ClassName FileCacheUtil
 */
public class FileCacheUtil {
    private final static Map<String, List<FileInfo>> fileMap = new HashMap<String, List<FileInfo>>();

    @Data
    public static class FileInfo {
        private String originalName;
        private long size;
        private File tempFile;
        private String targetName;

        public FileInfo(String originalName, long size, File tempFile, String targetName) {
            super();
            this.originalName = originalName;
            this.size = size;
            this.tempFile = tempFile;
            this.targetName = targetName;
        }
    }

    @Data
    public static class Progress {
        private String name;
        private String percent;

        public Progress(String name, String percent) {
            this.name = name;
            this.percent = percent;
        }
    }

    public static List<FileInfo> getFileInfosBySessionId(String sessionId) {
        return fileMap.get(sessionId);
    }

    public static Map<String, FileInfo> getFileInfoMapBySessionId(String sessionId) {
        Map<String, FileInfo> map = new HashMap<String, FileInfo>();
        List<FileInfo> list = fileMap.get(sessionId);
        if (CollectionUtils.isEmpty(list)) {
            return map;
        }
        for (FileInfo fileInfo : list) {
            map.put(fileInfo.getOriginalName(), fileInfo);
        }
        return map;
    }

    public static void setFileInfos(String sessionId, List<FileInfo> fileInfos) {
        fileMap.put(sessionId, fileInfos);
    }

    public static long getProgressByFile(long totalSize, File tempFile, String targetName) {
        File targetFile = new File(targetName);
        if (targetFile.exists()) {
            return Math.round((double) targetFile.length() * 100 / totalSize);
        } else if (tempFile.exists()) {
            return Math.round((double) tempFile.length() * 100 / totalSize);
        } else {
            return 0;
        }
    }

    /**
     * @param targetPath
     * @param originalName
     * @return
     */
    public static String generateNameAfterUpload(String targetPath, String originalName) {
        String pathname = targetPath + File.separator + "upload_" + originalName;
        File file = new File(pathname);
        int count = 1;
        while (file.exists()) {
            int indexOfDot = originalName.lastIndexOf(".");
            String name = indexOfDot > 0 ? originalName.substring(0, indexOfDot) : originalName;
            String suff = indexOfDot > 0 ? originalName.substring(indexOfDot) : "";
            pathname = targetPath + File.separator + "upload_" + name + "[" + count + "]" + suff;
            file = new File(pathname);
            count++;
        }
        return pathname;
    }
}
