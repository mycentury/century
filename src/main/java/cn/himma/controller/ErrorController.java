/**
 * 
 */
package cn.himma.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月4日
 * @ClassName FileUploadController
 */
@Controller
@RequestMapping("error")
public class ErrorController {
    @RequestMapping(value = "beyondMaxFileSize.do", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody Map<String, String> getProgress(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("result", "error");
        return map;
    }

}
