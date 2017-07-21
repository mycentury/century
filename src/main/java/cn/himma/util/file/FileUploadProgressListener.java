/**
 * 
 */
package cn.himma.util.file;

import javax.servlet.http.HttpSession;

import lombok.Data;

import org.apache.commons.fileupload.ProgressListener;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月6日
 * @ClassName File
 */
public class FileUploadProgressListener implements ProgressListener {

    private HttpSession session;

    public FileUploadProgressListener() {
    }

    public FileUploadProgressListener(HttpSession session) {
        this.session = session;
        Progress progresses = new Progress();
        session.setAttribute("upload_progress", progresses);
    }

    /**
     * pBytesRead 到目前为止读取文件的比特数 pContentLength 文件总大小 pItems 目前正在读取第几个文件
     */
    @Override
    public void update(long bytesRead, long contentLength, int items) {
        Progress progress = (Progress) session.getAttribute("upload_progress");
        if (progress == null) {
            progress = new Progress();
        }
        progress.setBytesRead(bytesRead);
        progress.setContentLength(contentLength);
        progress.setItems(items);
        session.setAttribute("upload_progress", progress);
    }

    @Data
    public static class Progress {
        private long bytesRead;
        private long contentLength;
        private int items;
    }
}