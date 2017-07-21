/**
 * 
 */
package cn.himma.util.excutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Desc 命令执行器
 * @author wenge.yan
 * @Date 2016年6月30日
 * @ClassName ShellExcutor
 * @linux-chmod [ugo|a] [-][rwx|765]{3} ${filePath}
 * @linux-find ${filePath} -name '${fileName}' -ls
 * @linux-drwx:第一位表示文件类型：d-目录文件，l-链接文件，--普通文件，p-管道文件
 */
public class CommandExcutor {
    private final static Logger logger = Logger.getLogger(CommandExcutor.class);
    private final static Runtime runtime = Runtime.getRuntime();
    private final static boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");
    private final static String charsetName = isWindows ? System.getProperty("sun.jnu.encoding") : System.getProperty("file.encoding");

    /**
     * 执行shell脚本
     * 
     * @param shellPath
     * @param args
     * @return
     * @throws Exception
     */
    public List<String> excute(String shellPath, String[] args) throws Exception {
        String cmd = null;
        if (!isWindows && shellPath.endsWith(".sh")) {
            StringBuilder sb = new StringBuilder("/bin/sh ").append(shellPath);
            for (String arg : args) {
                sb.append(" ").append(arg);
            }
            cmd = sb.toString();
            Process process = runtime.exec(cmd);
            int waitFor = process.waitFor();
            // 等待执行完成
            if (waitFor == 0) {
                return findAllContentsInResult(process);
            }
        }
        throw new RuntimeException(shellPath + "执行错误");
    }

    /**
     * @param filePath
     * @param authority由三位数字组成，如755
     * @return
     * @throws Exception
     */
    public boolean grant(String filePath, String authority) throws Exception {
        int lastIndexOf = filePath.lastIndexOf(File.separator);
        if (lastIndexOf < 0) {
            throw new IllegalArgumentException(filePath + "不是有效文件路径");
        }
        String path = filePath.substring(0, lastIndexOf);
        String name = filePath.substring(lastIndexOf + 1);
        String cmd = "find " + path + " -name '" + name + "' -ls";
        List<String> info = excute(cmd);
        List<String> result = findMatchedContent(info, "[-dlp][-rwx]{9}", true);
        if (CollectionUtils.isEmpty(result)) {
            logger.error("未找到权限表达");
        }
        String orginal = result.get(0);
        char[] chars = orginal.toCharArray();
        if (chars[0] != '-') {
            logger.error("暂不支持普通文件外的赋权限");
        }
        int u = getNumberAuthorityByChar(chars[1], chars[2], chars[3]);
        int g = getNumberAuthorityByChar(chars[4], chars[5], chars[6]);
        int o = getNumberAuthorityByChar(chars[7], chars[8], chars[9]);
        // compare
        StringBuilder newAuthority = new StringBuilder(u).append(g).append(o);
        if (newAuthority.toString().compareTo(orginal) > 0) {
            cmd = "chmod " + authority + " " + filePath;
            if (runtime.exec(cmd).waitFor() != 0) {
                logger.warn("命令" + cmd + "运行错误");
            }
        }
        return true;
    }

    public int getNumberAuthorityByChar(char r, char w, char x) {
        int result = 0;
        if (r == 'r') result += 4;
        if (w == 'w') result += 2;
        if (x == 'x') result += 1;
        return result;
    }

    /**
     * 执行windows cmd或linux terminal命令，只统计结果，不提取结果内容
     * 
     * @param command
     * @return
     * @throws Exception
     */
    public List<String> excute(String command) throws Exception {
        String cmd = isWindows ? "cmd /c " + command : command;
        Process process = runtime.exec(cmd);
        int waitFor = process.waitFor();
        // 等待执行完成
        if (waitFor == 0) {
            return findAllContentsInResult(process);
        } else {
            throw new RuntimeException(command + "执行错误");
        }
    }

    /**
     * @param process
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private List<String> findAllContentsInResult(Process process) throws UnsupportedEncodingException, IOException {
        List<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream(), charsetName));
        String line = null;
        while ((line = br.readLine()) != null) {
            result.add(line);
        }
        return result;
    }

    /**
     * @param process
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private List<String> findMatchedContent(List<String> contents, String regex, boolean onlyOnce) {
        List<String> result = new ArrayList<String>();
        if (!StringUtils.hasText(regex)) {
            return result;
        }
        for (String content : contents) {
            Matcher matcher = Pattern.compile(regex).matcher(content);
            if (matcher.find()) {
                result.add(matcher.group(0));
                if (onlyOnce) {
                    break;
                }
            }

        }
        return result;
    }

    public static void main(String[] args) {
        Matcher matcher = Pattern.compile("[-dlp][-rwx]{9}").matcher("12 drwxr-xr-x 4 app app    4096 May 24 09:16 apps");
        String orginal = matcher.find() ? matcher.group(0) : null;
        System.out.println(orginal);
    }
}
