/**
 * 
 */
package cn.himma.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.himma.util.file.StreamUtil;
import cn.himma.util.secure.EncryptMethod;
import cn.himma.util.secure.EncryptUtil;
import cn.himma.weixin.WXBizMsgCrypt;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年9月5日
 * @ClassName WeixinController
 */
@Controller
public class WeixinController {

    @RequestMapping(value = "weixin.do")
    public void verifyByWeixin(HttpServletRequest request, HttpServletResponse response) {
        for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            System.out.println(entry.getKey().toString() + "=" + Arrays.toString(entry.getValue()).toString());
        }
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");

        String nonce = request.getParameter("nonce");
        String openid = request.getParameter("openid");
        // 加密消息
        String encrypt_type = request.getParameter("encrypt_type");
        String msg_signature = request.getParameter("msg_signature");
        String symmetric_key = "aPB4nvtgRHd4zdPulBgmPTjNvMMQpwJmanrpa0cfCsy";
        if ("aes".equals(encrypt_type)) {
            System.out.println(openid + ":" + msg_signature);
        }
        try {
            InputStream inputStream = request.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            StreamUtil.copy(inputStream, outputStream);
            String body = outputStream.toString();
            Document document = DocumentHelper.parseText(body);
            Element rootElement = document.getRootElement();
            List<Element> elements = rootElement.elements("ToUserName");
            System.out.println(elements.get(0).getText());
            elements = rootElement.elements("Encrypt");
            WXBizMsgCrypt wx = new WXBizMsgCrypt("C2D4D70AE96F59AA27A0C99BFF671077", symmetric_key, "wxe418d74cd22c8da9");
            System.out.println(wx.decryptMsg(msg_signature, timestamp, nonce, body));
            System.out.println(elements.get(0).getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public @ResponseBody String accessByWeixin(HttpServletRequest request, HttpServletResponse response) {
        String token = "C2D4D70AE96F59AA27A0C99BFF671077";
        String signature = request.getParameter("signature");
        String timeStamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String echoStr = request.getParameter("echostr");
        String[] array = { token, timeStamp, nonce };
        Arrays.sort(array);
        StringBuilder sb = new StringBuilder();
        for (String string : array) {
            sb.append(string);
        }
        signature = EncryptUtil.encrypt(sb.toString(), null, EncryptMethod.SHA1);
        if (!signature.equals(signature)) {
            return null;
        }
        return echoStr;
        // 官方提供方法不会使用
        // String encodingAesKey = "aPB4nvtgRHd4zdPulBgmPTjNvMMQpwJmanrpa0cfCsy";
        // String appId = "wxe418d74cd22c8da9";
        // try {
        // WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAesKey, appId);
        // return wxcpt.verifyUrl(signature, timeStamp, nonce, echoStr);
        // } catch (AesException e) {
        // e.printStackTrace();
        // }
        // return null;
    }
}
