package cn.himma.util.capturer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

public class DomainCapture {
    final static String[] CHARS = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z" };
    final static String[] NUMBERS = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    final static String[] ALL = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
            "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

    public final static String[] SUFFIXES = { "com", "cn", "net", "biz", "co", "club", "studio", "live", "tv", "in", "pro", "bio", "work", "top",
            "xyz", "wang", "com.cn", "net.cn", "org.cn", "org", "wiki", "rocks", "pw", "host", "me", "mobi", "site", "tech", "website", "win",
            "space", "pics", "photo", "news", "help", "best", "cc", "engineer", "download", "info", "love", "name", "ooo", "forsale", "sale",
            "software", "video", "la", "market", "pub", "sexy", "social", "vc", "vote", "online", "link", "date", "press", "party", "click", "trade",
            "science", "design", "fashion", "gift", "band", "bid", "loan", "credit", "family", "rent", "red", "black", "blue", "gold", "green",
            "audio", "bar", "menu", "bz", "sc", "sx", "mn", "移动", "中文网", "机构", "在线" };
    private Integer urlKey = 0;

    private List<String> domains;

    private String[] sources;

    private int length;

    private final static Map<Integer, String> queryUrlMap = new HashMap<Integer, String>();
    static {
        queryUrlMap.put(0, "http://panda.www.net.cn/cgi-bin/check.cgi?area_domain=");
        queryUrlMap.put(1, "http://www.networksolutions.com/whois/results.jsp?domain=");
        // put(2, "http://www.checkdomain.com/cgi-bin/checkdomain.pl?domain=");// 其实是调1
        // put(3, "http://who.is/whois/");// 需要SOCKET
    }

    // 用get方法,post有待研究
    public Document getQueryResultByDomain(String domain) {
        String url = queryUrlMap.get(this.urlKey) + domain;
        Map<String, String> header = new HashMap<String, String>();
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        header.put("Accept-Encoding", "gzip, deflate, sdch");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");
        header.put("Cache-Control", "max-age=0");
        header.put("Connection", "keep-alive");
        header.put("Host", "panda.www.net.cn");
        header.put("Upgrade-Insecure-Requests", "1");
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
        try {
            Thread.sleep(500);
            // return Jsoup.connect(url).data(header).userAgent("Mozilla").cookie("auth", "token").timeout(5000).get();
            return Jsoup.connect(url).get();
        } catch (Exception e) {
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询是否可注册
     * 
     * @param domain
     * @return
     */
    public boolean isAvailable(String domain) {
        return isAvailable(domain, "original");
    }

    /**
     * 查询可注册域名
     * 
     * @param domain
     * @return List<String>
     */
    public List<String> getAvailableDomains(String domain) {
        List<String> availables = new ArrayList<String>();
        for (int i = 0; i < SUFFIXES.length; i++) {
            String realDomain = domain + "." + SUFFIXES[i];
            if (isAvailable(realDomain)) {
                availables.add(realDomain);
            }
        }
        return availables;
    }

    /**
     * 查询可注册域名
     * 
     * @param domain
     * @return
     */
    public List<String> getAvailableDomainList(String type, int length, String[] suffixes, int amountPerPage, int page) {
        if (length <= 0 || length > 10) {
            throw new IllegalArgumentException("length必须在1-10之间，当前值：" + length);
        }
        if (suffixes == null || suffixes.length <= 0) {
            throw new IllegalArgumentException("suffixes不能为空！");
        }
        String[] sources = getSourceCharsByType(type);
        if (sources == null || sources.length <= 0) {
            throw new IllegalArgumentException("字符源不能为空！");
        }
        generateDomainsBySourcesAndLength(sources, length);
        List<String> availableDomains = new ArrayList<String>();
        for (int i = amountPerPage * (page - 1); i < this.domains.size() && i < amountPerPage * page; i++) {
            String simpleDomain = this.domains.get(i);
            for (String suffix : suffixes) {
                String domain = simpleDomain + "." + suffix;
                if (isAvailable(domain)) {
                    availableDomains.add(domain);
                    System.out.println("恭喜！" + domain + "可用");
                }
            }
        }
        return availableDomains;
    }

    public List<String> getAvailableDomainList(List<String> domains, String[] suffixes) {

        if (suffixes == null || suffixes.length <= 0) {
            throw new IllegalArgumentException("suffixes不能为空！");
        }

        List<String> result = new ArrayList<String>();
        if (CollectionUtils.isEmpty(domains)) {
            return result;
        }
        for (String domain : domains) {
            for (String suffix : suffixes) {
                String compDomain = domain + "." + suffix;
                if (isAvailable(compDomain)) {
                    result.add(compDomain);
                    System.out.println("恭喜！" + compDomain + "可用");
                }
            }
        }
        return result;
    }

    /**
     * @param type
     * @param length
     * @param suffixes
     * @return
     */
    private void generateDomainsBySourcesAndLength(String[] sources, int length) {
        if (sources.equals(this.sources) && length != this.length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            domains = addOneCharForDomains(domains, sources);
        }
    }

    /**
     * @param domains
     * @param sources
     * @return
     */
    private List<String> addOneCharForDomains(List<String> domains, String[] sources) {
        List<String> result = new ArrayList<String>();
        if (CollectionUtils.isEmpty(domains)) {
            return Arrays.asList(sources);
        }
        for (String domain : domains) {
            for (String source : sources) {
                result.add(domain + source);
            }
        }
        return result;
    }

    /**
     * @param type
     * @return
     */
    private String[] getSourceCharsByType(String type) {
        String[] source = null;
        if ("numbers".equals(type)) {
            source = NUMBERS;
        } else if ("chars".equals(type)) {
            source = CHARS;
        } else {
            source = ALL;
        }
        return source;
    }

    public boolean isAvailable(String domain, String tagName) {
        Document document = getQueryResultByDomain(domain);
        if (!document.toString().contains("200")) {
            System.out.println("ERROR!返回：" + document + "域名：" + domain);
        }

        if (urlKey == 0) {
            String responseCode = extractResponseCode1(tagName, document);
            System.out.println(domain + ":" + responseCode);
            if (responseCode == null) {
                return false;
            }
            if (responseCode.contains("210")) {
                return true;
            } else if (responseCode.contains("211")) {
                return false;
            } else if (responseCode.contains("213")) {
                return false;
            } else if (responseCode.contains("216")) {
                System.out.println("查询过于频繁，已切换！");
                // urlKey = (urlKey++) % queryUrlMap.size();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000 * 60 * 60 * 24);
                            urlKey = 1;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return isAvailable(domain, tagName);
            } else {
                System.out.println("Error responseCode:" + responseCode + ",domain:" + domain);
            }
        } else if (urlKey == 2) {
            String response = extractResponseCode2(document, domain);
        }

        return false;
    }

    /**
     * @param tagName
     * @param document
     * @return
     */
    private String extractResponseCode2(Document document, String domain) {
        System.out.println(document);

        if (document.toString().contains("is registered")) {
            System.out.println(true);
        }
        Elements elementsByTag = document.getElementsByTag("script");
        Elements elements = document.getElementsContainingText("<strong>" + domain + "</strong>");
        Matcher matcher = Pattern.compile("[\\s\\S]*is\\s*(available|registered)").matcher(elements.get(0).toString());
        String result = null;
        if (matcher.find()) {
            result = matcher.group(1);
            System.out.println(result);
        }
        return result;
    }

    /**
     * @param tagName
     * @param document
     * @return
     */
    private String extractResponseCode1(String tagName, Document document) {
        String responseCode = null;
        if (document == null) {
            System.out.println("Error!Document null!");
            return null;
        }
        Elements elementsByTag = document.getElementsByTag(tagName);
        String text = elementsByTag.size() > 0 ? elementsByTag.get(0).ownText() : document.toString();
        String replaceAll = text.replaceAll(":", "\\|").replaceAll("：", "\\|");
        Matcher matcher = Pattern.compile("[0-9]{3}[\\s\t]*[\\|]{1}[\\s\t]*[^\")<>]*").matcher(replaceAll);
        if (matcher.find()) {
            responseCode = matcher.group();
        }
        if (responseCode == null) {
            System.out.println("Error!Can't metches responseCode!\nThe document is :" + document);
        }
        return responseCode;
    }

    public static void main(String[] args) {
        DomainCapture query = new DomainCapture();
        String type = "chars";// 域名范围
        int length = 6;// 域名长度
        String[] suffixes = { "com" };// 域名后缀
        int amountPerPage = 100;// 每次查询数量
        int page = 1;// 页码
        List<String> result = query.getAvailableDomainList(type, length, suffixes, amountPerPage, page);
        if (result.isEmpty()) {
            System.out.println("type=" + type + ",length=" + length + ",suffixes=" + suffixes + "已被抢空o(︶︿︶)o");
        } else {
            System.out.println("可用域名：" + result);
        }
    }

    public boolean isDomainAvailable(String domain) {
        return false;
    }

}