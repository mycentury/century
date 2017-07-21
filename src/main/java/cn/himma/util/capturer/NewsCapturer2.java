/**
 * 
 */
package cn.himma.util.capturer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年5月19日
 * @ClassName NewsCapturer
 */
public class NewsCapturer2 {

    private String url;
    private List<RegExpMapping> indexAndDetailRegExp;

    public String captureWholeHtml() {
        // url = "http://mini.eastday.com/";
        StringBuffer codeBuffer = null;
        BufferedReader reader = null;
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
            /**
             * 为了限制客户端不通过网页直接读取网页内容,就限制只能从浏览器提交请求. 但是我们可以通过修改http头的User-Agent来伪装,这个代码就是这个作用
             */
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
            // 读取url流内容
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            codeBuffer = new StringBuffer();
            String tempCode = "";
            // 把buffer内的值读取出来,保存到code中
            while ((tempCode = reader.readLine()) != null) {
                codeBuffer.append(tempCode).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return codeBuffer.toString();
    }

    /**
     * @param url
     * @param regExp
     */
    public NewsCapturer2(String url, List<RegExpMapping> indexAndDetailRegExp) {
        super();
        this.url = url;
        this.indexAndDetailRegExp = indexAndDetailRegExp;
    }

    @Data
    public static class SimpleInfo {
        private String title;
        private String source;
        private List<String> imageUrls;

    }

    @Data
    public static class DetailInfo {
        private String title;
        private String source;
        private List<String> imageUrls;

    }

    @Data
    public static class RegExpMapping {
        private String indexRegExp;
        private String detailRegExp;
    }

    public List<Data> extractNews(String html) {
        String replaceAll = deleteScript(html);
        return null;
    }

    /**
     * @param html
     * @return
     */
    public String deleteScript(String html) {
        // return html.replaceAll("<script.*</script>", "").replaceAll("<script(.*)/>", "").replaceAll("<script.*>",
        // "");
        if (html.matches("[\\s\\S]*<script>[\\s\\S]*</script>[\\s\\S]*")) {
            return html.replaceAll("<script>[\\s\\S]*</script>", "");
        }
        return html.replaceAll("<script>[\\s\\S]*</script>", "");
    }

    public static void main(String[] args) {
        String eastDayUrl = "http://mini.eastday.com/";
        RegExpMapping regExp = new RegExpMapping();
        regExp.setIndexRegExp("<html[\\s\\S]*>" + "<body[\\s\\S]*>[\\s\\S]*" + "<div class=\"first-view\">" + "</body></html>");
        regExp.setDetailRegExp("");
        List<RegExpMapping> regExpList = new ArrayList<RegExpMapping>();
        regExpList.add(regExp);
        NewsCapturer2 sina = new NewsCapturer2(eastDayUrl, regExpList);
        String wholeHtml = sina.captureWholeHtml();
        // System.out.println(wholeHtml);
        String deleteScript = sina.deleteScript(wholeHtml);
        System.out.println(deleteScript);
    }
}
