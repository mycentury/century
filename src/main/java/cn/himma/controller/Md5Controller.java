/**
 * 
 */
package cn.himma.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.himma.util.secure.MD5Util;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月23日
 * @ClassName Md5Controller
 */
@Controller
@RequestMapping("md5")
public class Md5Controller {

	@RequestMapping(value = "md5test.do", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody String testMd5(HttpServletRequest request) {
		String source = request.getParameter("source");
		return MD5Util.encode(source);
	}
}
