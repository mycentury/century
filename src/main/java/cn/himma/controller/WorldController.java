/**
 * 
 */
package cn.himma.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import cn.himma.business.ILoginBiz;
import cn.himma.entity.UserEntity;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月20日
 * @ClassName HelleController
 */
@Controller
@RequestMapping(value = "world")
public class WorldController {
    @Autowired
    private ILoginBiz loginBiz;

    @RequestMapping(value = "login", method = { RequestMethod.GET, RequestMethod.POST })
    // 请求url地址映射，类似Struts的action-mapping
    public String testLogin(@RequestParam(value = "username", required = false) String username, String password, HttpServletRequest request) {
        // @RequestParam是指请求url地址映射中必须含有的参数(除非属性required=false)
        // @RequestParam可简写为：@RequestParam("username")

        if (!"admin".equals(username) || !"admin".equals(password)) {
            return "loginError"; // 跳转页面路径（默认为转发），该路径不需要包含spring-servlet配置文件中配置的前缀和后缀
        }
        return "loginSuccess";
    }

    @RequestMapping("login2.do")
    public ModelAndView login2(ModelMap model) {
        model.addAttribute("message", "Hello Spring MVC Framework!");
        return new ModelAndView("index");
    }

    @RequestMapping("/test/login3.do")
    public ModelAndView testLogin3(UserEntity user) {
        // 同样支持参数为表单对象，类似于Struts的ActionForm，User不需要任何配置，直接写即可
        String username = user.getUsername();
        String password = user.getPassword();

        if (!"admin".equals(username) || !"admin".equals(password)) {
            return new ModelAndView("loginError");
        }
        return new ModelAndView("loginSuccess");
    }

    @RequestMapping("/test/login4.do")
    public String testLogin4(UserEntity user) {
        if (loginBiz.login(user) == false) {
            return "loginError";
        }
        return "loginSuccess";
    }
}
