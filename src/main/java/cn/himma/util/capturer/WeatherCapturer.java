/**
 * 
 */
package cn.himma.util.capturer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月1日
 * @ClassName WeatherCapturer
 */
public class WeatherCapturer {
    private static Map<Integer, String> apiMap = new HashMap<Integer, String>();
    private final static String CHINA_CODE = "[\u4e00-\u9fa5]{2,6}";// 或者[^x00-xff]或者[^\\x00-\\xff]
    private final static String CONCAT_CODE = "[:：=]{1}";
    private final static String NUMBER_CODE = "[0-9]{9}";

    private void initApiMap() {
        apiMap.put(1, "http://www.weather.com.cn/adat/sk/101020100.html");
    }

    public static boolean getAllCities() {
        String fileName2 = "src/main/java/cn/himma/util/capturer/city2.properties";
        String fileName3 = "src/main/java/cn/himma/util/capturer/city3.properties";
        BufferedReader br;
        BufferedWriter bw;
        try {
            br = new BufferedReader(new FileReader(fileName2), 1024 * 1024);
            bw = new BufferedWriter(new FileWriter(fileName3), 1024 * 1024);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            System.out.println(sb.toString());
            Matcher matcher = Pattern.compile("(" + CHINA_CODE + CONCAT_CODE + NUMBER_CODE + "|" + NUMBER_CODE + CONCAT_CODE + CHINA_CODE + ")")
                    .matcher(sb.toString());//
            while (matcher.find()) {
                String city = matcher.group(0);
                System.out.println(city);
                String[] split = city.split(CONCAT_CODE);
                try {
                    bw.write(split[1] + "=" + split[0] + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void compare(String fileName1, String fileName2) {
        Properties p1 = new Properties();
        Properties p2 = new Properties();
        try {
            p1.load(new FileInputStream(fileName1));
            p2.load(new FileInputStream(fileName2));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Entry<Object, Object> entry : p1.entrySet()) {
            if (p2.get(entry.getKey()) != null && !entry.getValue().equals(p2.get(entry.getKey()))) {
                System.out.println(entry.getKey() + "不同，p1：" + convertForProperties(entry.getValue()) + "，p2："
                        + convertForProperties(p2.get(entry.getKey())));
            }
            p2.remove(entry.getKey());
        }
        for (Entry<Object, Object> entry : p2.entrySet()) {
            System.out.println("p1没有：" + entry.getKey() + "=" + convertForProperties(entry.getValue()));
        }
    }

    public static String convertForProperties(Object orginal) {
        if (orginal == null) {
            return null;
        }
        try {
            return new String(orginal.toString().getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return orginal.toString();
        }
    }

    public static void main(String[] args) {
        String fileName1 = "src/main/java/cn/himma/util/capturer/city1.properties";
        String fileName2 = "src/main/java/cn/himma/util/capturer/city2.properties";
        compare(fileName1, fileName2);
    }
}
