/**
 * 
 */
package cn.himma.util.capturer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc 正则表达式要匹配类型和版本（这里简单用group_1和group_2表示，可作为参数传入）
 * @author wewenge.yan
 * @Date 2016年7月21日
 * @ClassName BrowserCapture
 */
public class BrowserCapture {
    private final static Map<String, Integer> IE_REGEXS_MAP = new HashMap<String, Integer>();
    private final static Map<String, Integer> CHROME_REGEXS_MAP = new HashMap<String, Integer>();
    private final static Map<String, Integer> FF_REGEXS_MAP = new HashMap<String, Integer>();

    static {
        IE_REGEXS_MAP.put("[Tt]{1}rident" + "/" + "[0-9]+(\\.[0-9])*" + "; rv:" + "([0-9]+(\\.[0-9]+)*)", 2);
        IE_REGEXS_MAP.put("([Mm]{1}[Ss]{1})?([Ii]{1}[Ee]{1}){1}" + "[ :：|]*" + "([0-9]+(\\.[0-9])*)", 3);
        CHROME_REGEXS_MAP.put("([Aa]{1}pple[Ww]eb[Kk]it)*" + ".*" + "[Cc]{1}hrome" + "/" + "([0-9]+(\\.[0-9]+)*)", 2);
        FF_REGEXS_MAP.put("[Ff]{1}irefox" + "/" + "([0-9]+(\\.[0-9]+)*)", 1);
    }

    private String extractVersionFromUserAgent(String browserType, Map<String, Integer> map, String userAgent) {
        System.out.println("》》》(" + userAgent + ")匹配浏览器(" + browserType + ")《《《start");
        String version = null;
        String regexp = null;
        for (Entry<String, Integer> ieRegexpEntry : map.entrySet()) {
            regexp = ieRegexpEntry.getKey();
            Integer group = ieRegexpEntry.getValue();
            Matcher matcher = Pattern.compile(regexp).matcher(userAgent);
            if (matcher.find()) {
                // 可能会覆盖
                if (version != null) {
                    System.out.println("上次匹配正则：" + regexp + "，结果：" + version);
                }
                version = matcher.group(group);
            }
        }
        System.out.println("》》》(" + userAgent + ")匹配浏览器(" + browserType + ")《《《end");
        return version;
    }

    public static void main(String[] args) {
        BrowserCapture browserCapture = new BrowserCapture();
        String ieAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko";
        String chromeAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36";
        String ffAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.19) Gecko/20110707 Firefox/3.6.19";
        String version = browserCapture.extractVersionFromUserAgent("ie", IE_REGEXS_MAP, ieAgent);
        System.out.println(version);
        version = browserCapture.extractVersionFromUserAgent("chrome", CHROME_REGEXS_MAP, chromeAgent);
        System.out.println(version);
        version = browserCapture.extractVersionFromUserAgent("ff", FF_REGEXS_MAP, ffAgent);
        System.out.println(version);
    }
}
