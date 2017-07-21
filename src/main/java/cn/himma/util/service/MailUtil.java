/**
 * 
 */
package cn.himma.util.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 * @Desc 邮箱 POP3服务器（端口995） SMTP服务器（端口465或587） qq.com pop.qq.com smtp.qq.com
 * @author wewenge.yan
 * @Date 2016年7月18日
 * @ClassName MailUtil
 */
public class MailUtil {
    private final static String USER = "617932280@qq.com";
    private final static String PWD = "wqrelxjwvvycbedh";

    public static boolean sendEmail(List<String> dests, String subject, String content, List<EmailAttachment> attachments) {
        HtmlEmail email = new HtmlEmail();

        email.setCharset("UTF-8");
        email.setHostName("smtp.qq.com");
        email.setSmtpPort(465);
        email.setSSLOnConnect(Boolean.TRUE);
        email.setSSLCheckServerIdentity(Boolean.TRUE); // 设定是否使用SSL
        email.setSslSmtpPort("465"); // 设定SSL端口
        if (null != USER && !"".equals(USER.trim())) {
            email.setAuthentication(USER, PWD);
        }
        try {
            long length = 0;
            if (attachments != null && !attachments.isEmpty()) {
                for (EmailAttachment attachment : attachments) {
                    File file = new File(attachment.getPath());
                    length += file.length() / 1024;
                    System.out.println("附件" + attachment.getPath() + "大小" + length / 1024 + "M" + length % 1024 + "K");
                    if (length >= 50 * 1024) {
                        System.out.println("ERROR-->附件超过50M，当前大小：" + length + "K");
                        return false;
                    }
                    email.attach(attachment);
                }
            }
            email.setFrom(USER);
            email.setSubject(subject);
            email.setHtmlMsg(content);
            for (String dest : dests) {
                email.addTo(dest);
            }
            email.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendEmail(Email email) {
        try {
            email.send();
            return true;
        } catch (EmailException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendEmail(MailInfo email) {
        return sendEmail(email.dests, email.subject, email.content, email.attachments);
    }

    @Data
    public static class MailInfo {
        private List<String> dests;
        private String subject;
        private String content;
        private List<EmailAttachment> attachments;

        /**
         * @param dests
         * @param subject
         * @param content
         * @param attachment
         */
        public MailInfo(List<String> dests, String subject, String content, List<EmailAttachment> attachments) {
            super();
            this.dests = dests;
            this.subject = subject;
            this.content = content;
            this.attachments = attachments;
        }
    }

    public static void main(String[] args) {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setName("himma20160727.rar");
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        // attachment.setDescription("test attachment");
        attachment.setPath("D:\\WorkSpace_One\\himma20160727.rar");

        List<String> dests = new ArrayList<String>();
        dests.add(USER);
        List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
        attachments.add(attachment);
        sendEmail(dests, "test", "test", null);
    }
}
