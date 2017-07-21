/**
 * 
 */
package cn.himma.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.himma.util.service.DownloadUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月4日
 * @ClassName IndexController
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/index.do", method = { RequestMethod.GET, RequestMethod.POST })
    public String requestForIndex(HttpServletRequest request) {
        // Enumeration<String> headerNames = request.getHeaderNames();
        // while (headerNames.hasMoreElements()) {
        // String headerName = headerNames.nextElement();
        // String headerValue = request.getHeader(headerName);
        // System.out.println(headerName + ":" + headerValue);
        // }
        List<String> fileNames = new ArrayList<String>();
        fileNames.add("jre6.rar");
        fileNames.add("Flags.zip");
        List<String> cacheNames = DownloadUtil.setCacheFileNameToSession(request.getSession(), fileNames);
        request.setAttribute("files", cacheNames);
        return "index";
    }

    @RequestMapping(value = { "", "/" })
    public String index(HttpServletRequest request) {
        return "redirect:/index.do";
    }
}
