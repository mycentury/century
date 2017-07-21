/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.net.URL;
import java.util.List;

import cn.himma.util.capturer.DomainCapture;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年7月21日
 * @ClassName Test
 */
public class Test {
    public static void main(String[] args) {
        URL url = FileContentExtractor.class.getResource(".");
        String path = url.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        List<String> domains = FileContentExtractor.extractContentIntoListByRegexp(path + File.separator + "source.txt", "([a-z]{2,})[^(\\.)]+", 1);
        DomainCapture query = new DomainCapture();
        List<String> result = query.getAvailableDomainList(domains, new String[] { "com" });
        if (result.isEmpty()) {
            System.out.println("已被抢空o(︶︿︶)o");
        } else {
            System.out.println("可用域名：" + result);
        }
    }
}
