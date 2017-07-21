/**
 * 
 */
package cn.himma.util.capturer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年5月19日
 * @ClassName NewsCapturer
 */
public class NewsCapturer {

    /**
     * 从整个doc文档获取新闻链接元素
     * 
     * @param parents
     * @return
     */
    protected Elements extractNewsLinkElemsFromParents(Element document, Rule rule) {
        Elements parents = this.findElementDeeply(document, rule.newsParentRules, false);
        if (parents == null || parents.isEmpty()) {
            System.out.println("Error for no parents of links found!");
            return null;
        }
        return this.findElementDeeply(parents, rule.newsLinksRules, false);
    }

    /**
     * 从新闻详细链接提取新闻数据
     * 
     * @param url
     * @return
     */
    public List<DetailInfo> capturerNewsDatas(String url, Rule rule) {
        List<Rule> rules = new ArrayList<NewsCapturer.Rule>();
        rules.add(rule);
        return this.capturerNewsDatas(url, rules);
    }

    /**
     * 分三大步： 从新闻详细链接提取新闻数据
     * 
     * @param url
     * @return
     */
    public List<DetailInfo> capturerNewsDatas(String url, List<Rule> rules) {
        // 1.通过url获取新闻主页文本
        Document document = this.getDocumentByUrl(url);
        List<DetailInfo> result = new ArrayList<NewsCapturer.DetailInfo>();
        for (Rule rule : rules) {
            // 2.从新闻主页文本素提取新闻链接元素（又分：①先获取一个父元素②再获取链接）
            Elements allNews = this.extractNewsLinkElemsFromParents(document, rule);
            // 3.从新闻链接元素提取新闻详细信息（又分：①获取整个页面②提取父元素③提取详细信息：title，imgAndTexts）
            result.addAll(this.extractDetailDatasFromNewsLinks(allNews, rule));
        }
        return result;
    }

    /**
     * 从新闻页元素中获取新闻数据
     * 
     * @param news
     * @return
     */
    private List<DetailInfo> extractDetailDatasFromNewsLinks(Elements newsList, Rule rule) {
        List<DetailInfo> detailNewsList = new ArrayList<NewsCapturer.DetailInfo>();
        for (Element news : newsList) {
            String sourceUrl = news.attr("href");
            Document document = this.getDocumentByUrl(sourceUrl);
            detailNewsList.add(this.extractDetailInfo(document, sourceUrl, rule));
        }
        return detailNewsList;
    }

    /**
     * 根据tag的属性从元素深度遍历搜索
     * 
     * @param parent
     * @param attributes
     * @param canSkip
     * @return
     */
    private Elements findElementDeeply(Element parent, List<Attribute> attributes, boolean canSkip) {
        Elements elements = new Elements();
        elements.add(parent);
        return this.findElementDeeply(elements, attributes, canSkip);
    }

    /**
     * 根据tag的属性从元素列表深度遍历搜索
     * 
     * @param elements
     * @param attributes
     * @param canSkip
     * @return
     */
    private Elements findElementDeeply(Elements elements, List<Attribute> attributes, boolean canSkip) {
        Elements result = elements;
        for (Attribute attribute : attributes) {
            Elements temp = new Elements();
            for (Element element : result) {
                temp.addAll(findElementByAttribute(element, attribute, canSkip));
            }
            result = temp;
        }
        return result;
    }

    /**
     * 根据tag的属性从父元素搜索子元素
     * 
     * @param parent
     * @param attribute
     * @param canSkip
     * @return
     */
    private Elements findElementByAttribute(Element parent, Attribute attribute, boolean canSkip) {
        if (attribute.getTag() == null) {
            System.out.println("Attribute tagName null");
            return null;
        }

        Elements elementsByTag = parent.getElementsByTag(attribute.getTag());
        if ("h".equals(attribute.getTag().toLowerCase())) {
            for (int i = 0; i < 10; i++) {
                elementsByTag.addAll(parent.getElementsByTag("h" + i));
            }
        }
        if (elementsByTag == null || elementsByTag.isEmpty()) {
            System.out.println("Error!Can't find tag:" + attribute.getTag() + " in element:" + parent);
            return elementsByTag;
        }

        // 根据tag匹配，再根据attr和value匹配
        Elements elementsByAttr = new Elements();
        for (Element element : elementsByTag) {
            if (attribute.getAttr() == null || element.attr(attribute.getAttr()).matches(attribute.getValue())) {
                elementsByAttr.add(element);
            }
        }

        // 可跳过式
        if (elementsByAttr.isEmpty() && canSkip) {
            System.out.println("Error!Can't find attribute：" + attribute + " in element:" + parent);
            return elementsByTag;
        }
        return elementsByAttr;
    }

    /**
     * @param document
     * @param sourceUrl
     * @return
     */
    protected DetailInfo extractDetailInfo(Document document, String sourceUrl, Rule rule) {
        DetailInfo detailInfo = new DetailInfo();
        detailInfo.setSourceUrl(sourceUrl);
        Elements parents = this.findElementDeeply(document, rule.detailParentRules, false);
        if (parents == null || parents.isEmpty()) {
            System.out.println("Error!Can't find the news parent element!");
            return null;
        }
        Element parent = parents.get(0);

        Elements titles = this.findElementDeeply(parent, rule.detailTitleRules, false);
        if (titles == null || titles.isEmpty()) {
            System.out.println("Error!Can't find the title of the news!");
            return null;
        }

        List<String> imageAndTexts = extractImgAndTexts(parent);

        detailInfo.setTitle(extractTextOfElement(titles.get(0)));
        detailInfo.setImageAndTexts(imageAndTexts);

        return detailInfo;
    }

    /**
     * 该步可能比较复杂，需要重写
     * 
     * @param parents
     * @param parent
     * @return
     */
    protected List<String> extractImgAndTexts(Element parent) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("div", "class", "J-contain_detail_cnt contain_detail_cnt"));
        Elements contents = this.findElementDeeply(parent, attributes, false);
        if (contents == null || contents.isEmpty()) {
            System.out.println("Error!Can't find the contents of the news!");
            return null;
        }

        List<String> imageAndTexts = new ArrayList<String>();
        for (Element element : contents.get(0).getAllElements()) {
            if ("img".equals(element.tagName())) {
                imageAndTexts.add(element.attr("src"));
            }
            if ("p".equals(element.tagName())) {
                Elements elementsByTag = element.getElementsByTag("span");
                if (elementsByTag != null && !elementsByTag.isEmpty()) {
                    imageAndTexts.add(extractTextOfElement(elementsByTag.get(0)));
                } else {
                    imageAndTexts.add(extractTextOfElement(element));
                }
            }
        }
        return imageAndTexts;
    }

    /**
     * @param parent
     * @return
     */
    protected Elements extractLinkElementsFromParent(Element parent, Rule rule) {
        return this.findElementDeeply(parent, rule.newsLinksRules, false);
    }

    // 用get方法,post有待研究
    private Document getDocumentByUrl(String url) {
        try {
            return Jsoup.connect(url).data("query", "Java").userAgent("Mozilla").cookie("auth", "token").timeout(5000).get();
        } catch (IOException e) {
            try {
                return Jsoup.connect(url).get();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param title
     * @return
     */
    private String extractTextOfElement(Element title) {
        return title.ownText().replaceAll("<[^<>]*>", "").trim();
    }

    @Data
    protected static class DetailInfo {
        private String sourceUrl;
        private String title;
        private List<String> imageAndTexts;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String imgOrText : imageAndTexts) {
                sb.append("\n").append(imgOrText);
            }
            return sourceUrl + "\n" + title + sb;
        }
    }

    @Data
    protected static class Attribute {
        private String tag;
        private String attr;
        private String value;

        /**
         * @param tag
         * @param attr
         * @param value
         */
        protected Attribute(String tag, String attr, String value) {
            super();
            this.tag = tag;
            this.attr = attr;
            this.value = value;
        }
    }

    @Data
    public static class Rule {
        private List<Attribute> newsParentRules;
        private List<Attribute> newsLinksRules;
        private List<Attribute> detailParentRules;
        private List<Attribute> detailTitleRules;
        private List<Attribute> detailImgAndTextRules;

        /**
         * @param newsParentRules
         * @param newsLinksRules
         * @param detailParentRules
         * @param detailTitleRules
         * @param detailImgAndTextRules
         */
        public Rule(List<Attribute> newsParentRules, List<Attribute> newsLinksRules, List<Attribute> detailParentRules,
                List<Attribute> detailTitleRules, List<Attribute> detailImgAndTextRules) {
            super();
            this.newsParentRules = newsParentRules;
            this.newsLinksRules = newsLinksRules;
            this.detailParentRules = detailParentRules;
            this.detailTitleRules = detailTitleRules;
            this.detailImgAndTextRules = detailImgAndTextRules;
        }
    }

    public static void main(String[] args) {
        String eastDayUrl = "http://mini.eastday.com/";
        NewsCapturer sina = new NewsCapturer() {
        };

        List<Attribute> newsParentRules = new ArrayList<NewsCapturer.Attribute>();
        newsParentRules.add(new Attribute("div", "id", "J_hot_news"));

        List<Attribute> newsLinksRules = new ArrayList<NewsCapturer.Attribute>();
        newsLinksRules.add(new Attribute("a", null, null));

        List<Attribute> detailParentRules = new ArrayList<NewsCapturer.Attribute>();
        detailParentRules.add(new Attribute("div", "class", "section"));
        detailParentRules.add(new Attribute("div", "class", "detail_cnt[\\s\\t]*clear-fix"));
        detailParentRules.add(new Attribute("div", "class", "article"));
        detailParentRules.add(new Attribute("div", "class", "detail_left[\\s\\t]*clear-fix"));
        detailParentRules.add(new Attribute("div", "class", "detail_left_cnt"));

        List<Attribute> detailTitleRules = new ArrayList<NewsCapturer.Attribute>();
        detailTitleRules.add(new Attribute("div", "class", "J-title_detail title_detail"));
        detailTitleRules.add(new Attribute("h", null, null));
        detailTitleRules.add(new Attribute("span", null, null));

        Rule rule = new Rule(newsParentRules, newsLinksRules, detailParentRules, detailTitleRules, null);
        List<DetailInfo> allNews = sina.capturerNewsDatas(eastDayUrl, rule);
        File file = new File("news.html");
        RandomAccessFile raf = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rw");
            raf.write(("<html><head><meta http-equiv=Content-Type content=\"text/html;charset=utf-8\" /></head><body>").getBytes("UTF-8"));
            for (DetailInfo detailInfo : allNews) {
                StringBuilder sb = new StringBuilder();
                for (String imgOrText : detailInfo.getImageAndTexts()) {
                    sb.append("\n").append(imgOrText);
                }
                String toString = "<a href=\"" + detailInfo.getSourceUrl() + "\">" + detailInfo.getTitle() + "</a>" + sb + "\n";
                raf.write((toString + "\n").getBytes("UTF-8"));
                System.out.println(detailInfo);
            }
            raf.write(("</body></html>").getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
