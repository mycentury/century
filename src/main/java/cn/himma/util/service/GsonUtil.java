/**
 * 
 */
package cn.himma.util.service;

import com.google.gson.Gson;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月18日
 * @ClassName GsonUtil
 */
public class GsonUtil {
	private final static Gson GSON = new Gson();

	public static String toJsonString(Object obj) {
		return GSON.toJson(obj);
	}

	public static <T> T parseJson(String json, Class<T> clazz) {
		return GSON.fromJson(json, clazz);
	}
}
