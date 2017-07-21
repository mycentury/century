/**
 * 
 */
package cn.himma.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年7月21日
 * @ClassName FileContentExtractor
 */
public class FileContentExtractor {
    private static final int BUFFER_SIZE = 1024 * 1024;

    public static void extractContentIntoFileByRegexp(String sourceName, String targetName, String regex, int g) {
        File source = new File(sourceName);
        if (!source.exists() || !StringUtils.hasText(targetName) || !StringUtils.hasText(regex)) {
            return;
        }
        if (g < 0) {
            g = 0;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(targetName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis), BUFFER_SIZE);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos), BUFFER_SIZE);
            String readLine = null;
            Pattern pattern = Pattern.compile(regex);
            while ((readLine = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(readLine);
                while (matcher.find()) {
                    String group = matcher.group(g);
                    bw.write(group + "\n");
                }
            }
            br.close();
            bw.close();
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> extractContentIntoListByRegexp(String sourceName, String regex, int g) {
        File source = new File(sourceName);
        List<String> result = new ArrayList<String>();
        if (!source.exists() || !StringUtils.hasText(regex)) {
            return result;
        }
        if (g < 0) {
            g = 0;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(source);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis), BUFFER_SIZE);
            String readLine = null;
            Pattern pattern = Pattern.compile(regex);
            while ((readLine = br.readLine()) != null) {
                Matcher matcher = pattern.matcher(readLine);
                while (matcher.find()) {
                    String group = matcher.group(g);
                    result.add(group);
                }
            }
            br.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
        return result;
    }

    public static void main(String[] args) {
        URL url = FileContentExtractor.class.getResource(".");
        String path = url.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        extractContentIntoFileByRegexp(path + File.separator + "source.txt", path + File.separator + "target.txt", "([a-z]{2,})[^(\\.)[A-Za-z]]+", 1);
    }
}
