/**
 * 
 */
package cn.himma.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

/**
 * @Desc 按照某种规则给文件夹内文件重命名
 * @author wewenge.yan
 * @Date 2016年7月14日
 * @ClassName FileNamingUtil
 */
public class FileNameUtil {
    final static char[] FILE_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private final static String[] HEX_CHAR_ARRAY = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public static Character hex2Char(String hexString) {
        if (!hexString.matches("0x[0-9a-fA-F]+")) {
            throw new IllegalArgumentException("参数应是一个以 0x 开头的十六进制字符串");
        }
        int codePoint = Integer.parseInt(hexString.substring(2), 16);
        if ((codePoint < 0) || (codePoint > Character.MAX_CODE_POINT)) {
            throw new IllegalArgumentException(hexString + " 所能表示的字符范围溢出");
        }
        return Character.valueOf((char) codePoint);
    }

    public static List<String> generateByLengthAndCount(int length, int count) {
        if (Math.pow(HEX_CHAR_ARRAY.length, length) < count) {
            throw new IllegalArgumentException("需要结果太多，该字符串和长度不足以表示！");
        }

        int properLength = 0;
        for (; properLength < length; properLength++) {
            if (Math.pow(HEX_CHAR_ARRAY.length, properLength) >= count) {
                break;
            }
        }

        StringBuilder prifix = new StringBuilder("0x");
        for (int i = 0; i < length - properLength; i++) {
            prifix.append('0');
        }

        List<String> result = new ArrayList<String>();
        result.add(prifix.toString());
        for (int i = 0; i < properLength; i++) {
            result = addOneChar(result);
        }
        return result;
    }

    private static List<String> addOneChar(List<String> lastResult) {
        List<String> thisResult = new ArrayList<String>();
        for (String string : lastResult) {
            for (int i = 0; i < HEX_CHAR_ARRAY.length; i++) {
                thisResult.add(string + HEX_CHAR_ARRAY[i]);
            }
        }
        return thisResult;
    }

    public static List<File> getChildFilesBySuffix(String suffix, String path) {
        List<File> result = new ArrayList<File>();
        File dir = new File(path);
        if (!dir.isDirectory()) {
            return result;
        }
        File[] listFiles = dir.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (!StringUtils.hasText(suffix) || listFiles[i].getName().endsWith(suffix)) {
                result.add(listFiles[i]);
            }
        }
        return result;
    }

    public static void sortStringListByNumberValue(List<String> names, String regexp, int g) {
        Pattern pattern = Pattern.compile(regexp);
        for (int i = names.size() - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                String prefix = names.get(j);
                String suffix = names.get(j + 1);
                if (!prefix.matches(regexp) || !suffix.matches(regexp)) {
                    break;
                }
                try {
                    Matcher preMatcher = pattern.matcher(prefix);
                    Matcher sufMatcher = pattern.matcher(suffix);
                    if (preMatcher.find() && sufMatcher.find()) {
                        String numberOfPrefix = preMatcher.group(g);
                        String numberOfSuffix = sufMatcher.group(g);
                        if (numberOfPrefix.length() > numberOfSuffix.length() || numberOfPrefix.length() == numberOfSuffix.length()
                                && Integer.valueOf(numberOfPrefix) > Integer.valueOf(numberOfSuffix)) {
                            String set = names.set(j + 1, prefix);
                            set = names.set(j, suffix);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        String path = "D:\\WorkSpace_One\\himma\\src\\main\\webapp\\WEB-INF\\emoji\\weixin";
        String suffix = ".png";
        int length = 4;
        List<File> childFiles = getChildFilesBySuffix(suffix, path);
        List<String> generate = generateByLengthAndCount(length, childFiles.size());
        for (int i = 0; i < childFiles.size(); i++) {
            File file = childFiles.get(i);
            File dest = new File(path + File.separator + generate.get(i) + suffix);
            file.renameTo(dest);
        }
    }

    public static String generateRandomFileName(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            double random = Math.random();
            int round = (int) Math.round(random * FILE_CHAR.length);
            result.append(FILE_CHAR[round]);
        }
        return result.toString();
    }
}
