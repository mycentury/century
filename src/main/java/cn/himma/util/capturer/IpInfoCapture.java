/**
 * 
 */
package cn.himma.util.capturer;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年7月4日
 * @ClassName IpInfoCapture
 */
public class IpInfoCapture {
    private static Map<Integer, String> map = new HashMap<Integer, String>();

    static {
        map.put(1, "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=");// js或者json
        map.put(2, "http://ip.taobao.com/service/getIpInfo.php?ip=");
        map.put(3, "http://ip.ws.126.net/ipquery?ip=");
        map.put(4, "http://pv.sohu.com/cityjson?ie=utf-8&ip=");
    }
}
