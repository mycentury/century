/**
 * 
 */
package cn.himma.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.himma.util.service.FileCacheUtil;
import cn.himma.util.service.FileCacheUtil.FileInfo;

/**
 * @Desc 根据临时文件检查进度。优点：可区分每个文件。缺点：进度不准确，解析文件的同时，文件已经在上传了，而此时文件信息没有加入session，因此获取不到进度
 * @author wenge.yan
 * @Date 2016年7月4日
 * @ClassName FileTwoController
 */
@Controller
@RequestMapping("file")
public class FileTwoController {
    private final static Logger logger = Logger.getLogger(FileTwoController.class);

    @RequestMapping(value = "getProgress2.do", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody Map<String, String> getProgress2(HttpServletRequest request) {
        Map<String, String> result = new HashMap<String, String>();
        String[] fileNames = request.getParameterValues("fileNames");
        HttpSession session = request.getSession();
        List<FileCacheUtil.FileInfo> fileInfos = (List<FileCacheUtil.FileInfo>) session.getAttribute("fileInfos");
        if (fileNames == null || fileNames.length <= 0) {
            result.put("result", "fail");
            return result;
        }
        for (int i = 0; i < fileNames.length; i++) {
            String originalName = fileNames[i];
            boolean found = false;
            if (!CollectionUtils.isEmpty(fileInfos)) {
                for (FileInfo fileInfo : fileInfos) {
                    if (originalName.equals(fileInfo.getOriginalName())) {
                        long progress = FileCacheUtil.getProgressByFile(fileInfo.getSize(), fileInfo.getTempFile(), fileInfo.getTargetName());
                        result.put("progress" + i, String.valueOf(progress));
                        found = true;
                    }
                }
            }
            if (!found) {
                result.put("progress" + i, "0");
            }
        }
        logger.info("进度：" + result);
        return result;
    }

    @RequestMapping(value = "upload2.do", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody Map<String, String> uploadFiles(HttpServletRequest request) {
        logger.info("上传-------------start");
        HttpSession session = request.getSession();
        String targetPath = "D:\\WorkSpace_One\\himma\\src\\main\\webapp\\WEB-INF\\upload";
        File dir = new File(targetPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // logger.info(System.getProperty("java.io.tmpdir"));
        Map<String, String> result = new HashMap<String, String>();

        // 使用Apache文件上传组件处理文件上传步骤：
        // 1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");
        // 3、判断提交上来的数据是否是上传表单的数据
        if (!ServletFileUpload.isMultipartContent(request)) {
            // 按照传统方式获取数据
            result.put("result", "fail");
            result.put("error_msg", "未检测到文件！");
            return result;
        }
        // 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
        List<FileItem> list = null;
        try {
            list = upload.parseRequest(request);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(list)) {
            // 按照传统方式获取数据
            result.put("result", "fail");
            result.put("error_msg", "未检测到文件！");
            return result;
        }
        boolean uploadResult = true;
        // 加入文件缓存
        for (FileItem file : list) {
            if (file.isFormField()) {
                continue;
            }
            File tempFile = ((DiskFileItem) file).getStoreLocation();
            String originalName = file.getName();
            originalName = originalName.substring(originalName.lastIndexOf(File.separator) + 1);
            long size = file.getSize();
            String targetName = FileCacheUtil.generateNameAfterUpload(targetPath, originalName);

            List<FileCacheUtil.FileInfo> fileInfos = (List<FileInfo>) session.getAttribute("fileInfos");
            if (CollectionUtils.isEmpty(fileInfos)) {
                fileInfos = new ArrayList<FileCacheUtil.FileInfo>();
            }
            fileInfos.add(new FileCacheUtil.FileInfo(originalName, size, tempFile, targetName));
            session.setAttribute("fileInfos", fileInfos);
            // 要捕获结果，不采用线程后台执行上传
            if (!excuteUpload(file, targetName)) {
                uploadResult = false;
                String error_files = result.get("error_files");
                if (StringUtils.hasText(error_files)) {
                    error_files += ";" + originalName;
                } else {
                    error_files = originalName;
                }
                result.put("error_files", error_files);
                result.put("error_msg", "文件上传失败！");
            }
        }
        result.put("result", uploadResult ? "success" : "fail");
        logger.info("上传-------------end:" + result);
        return result;
    }

    private boolean excuteUpload(FileItem file, String targetName) {
        try {
            File targetFile = new File(targetName);
            // 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
            // 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
            // 获取item中的上传文件的输入流
            InputStream in = file.getInputStream();
            // 创建一个文件输出流
            FileOutputStream out = new FileOutputStream(targetFile);
            // 创建一个缓冲区
            byte buffer[] = new byte[1024];
            // 判断输入流中的数据是否已经读完的标识
            int len = 0;
            // 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
            while ((len = in.read(buffer)) > 0) {
                // 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\" + filename)当中
                out.write(buffer, 0, len);
            }
            // 关闭输入流
            in.close();
            // 关闭输出流
            out.close();
            // 删除处理文件上传时生成的临时文件
            file.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
