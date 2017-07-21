/**
 * 
 */
package cn.himma.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.himma.util.constant.SystemConstant;
import cn.himma.util.file.FileNameUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月14日
 * @ClassName EmojiController
 */
@Controller
@RequestMapping("emoji")
public class EmojiController {
	private final static Logger logger = Logger
			.getLogger(EmojiController.class);

	@RequestMapping(value = "getEmojiesByPage.do", method = {
			RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody List<String> getEmojiesByPage(
			HttpServletRequest request, @RequestParam("page") String page) {
		List<String> result = new ArrayList<String>();
		try {
			URL url = this.getClass().getResource("/");
			if (url == null) {
				logger.error("EmojiController.getEmojiesByPage:未找到路径");
				return result;
			}
			String path = url.getPath().replace("classes", "emoji") + page;
			if (SystemConstant.isWindows) {
				logger.info(path);
				path = path.substring(1);
			}
			logger.info(path);
			List<File> files = FileNameUtil.getChildFilesBySuffix(null, path);
			for (File file : files) {
				result.add(file.getName());
			}
			FileNameUtil.sortStringListByNumberValue(result, "([0-9]+).*", 1);
		} catch (Exception e) {
			logger.error("EmojiController.getEmojiesByPage", e);
		}
		return result;
	}
}
