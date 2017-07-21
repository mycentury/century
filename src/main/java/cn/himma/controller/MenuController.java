/**
 * 
 */
package cn.himma.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Desc 可以自定义menu转换库，防止链接看出页面名称
 * @author wewenge.yan
 * @Date 2016年7月21日
 * @ClassName MenuController
 */
@Controller
public class MenuController {
    @RequestMapping("menu.do")
    public String getMenuView(HttpServletRequest request) {
        String menu = request.getParameter("menu");
        if ("index".equals(menu)) {
            return "redirect:index.do";
        }
        return menu;
    }

}
