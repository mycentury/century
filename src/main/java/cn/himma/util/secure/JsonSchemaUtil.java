/**
 * 
 */
package cn.himma.util.secure;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月24日
 * @ClassName JsonSchemaUtil
 */
public class JsonSchemaUtil {

	private static URL url;
	static {
		url = JsonSchemaUtil.class.getResource("/");
	}

	public static boolean validateByFilePath(String schemaPath, String jsonPath)
			throws IOException {
		JsonNode schema = JsonLoader.fromPath(url.getPath() + schemaPath);
		JsonNode json = JsonLoader.fromPath(url.getPath() + jsonPath);
		ProcessingReport report = JsonSchemaFactory.byDefault().getValidator()
				.validateUnchecked(schema, json);
		boolean result = report.isSuccess();
		if (!result) {
			for (ProcessingMessage processingMessage : report) {
				System.out.println(processingMessage);
			}
		}
		return result;
	}

	public static boolean validateByString(String schemaString,
			String jsonString) throws IOException {
		JsonNode schema = JsonLoader.fromString(schemaString);
		JsonNode json = JsonLoader.fromString(jsonString);
		ProcessingReport report = JsonSchemaFactory.byDefault().getValidator()
				.validateUnchecked(schema, json);
		boolean result = report.isSuccess();
		if (!result) {
			for (ProcessingMessage processingMessage : report) {
				System.out.println(processingMessage);
			}
		}
		return result;
	}

	public static void main(String[] args) {
		try {
			boolean result = validateByFilePath(
					"cn/himma/json/StorageSchema.json",
					"cn/himma/json/StorageData.json");
			System.out.println(result);
			result = validateByFilePath("cn/himma/json/RefSchema.json",
					"cn/himma/json/RefData.json");
			System.out.println(result);
			result = validateByFilePath("cn/himma/json/ProductSchema.json",
					"cn/himma/json/ProductData.json");
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
