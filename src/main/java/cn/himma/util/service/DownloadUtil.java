/**
 * 
 */
package cn.himma.util.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import cn.himma.util.file.FileNameUtil;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年7月28日
 * @ClassName DownloadUtil
 */
public class DownloadUtil {
    public static List<String> setCacheFileNameToSession(HttpSession session, List<String> fileNames) {
        Map<String, String> fileMap = (HashMap<String, String>) session.getAttribute("download");
        if (fileMap == null) {
            fileMap = new HashMap<String, String>();
        }
        List<String> randomFileNames = new ArrayList<String>();
        for (String fileName : fileNames) {
            String randomFileName = FileNameUtil.generateRandomFileName(10);
            fileMap.put(randomFileName, fileName);
            randomFileNames.add(randomFileName);
        }
        session.setAttribute("download", fileMap);
        return randomFileNames;
    }
}
