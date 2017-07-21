/**
 * 
 */
package cn.himma.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年8月24日
 * @ClassName JsonSchemaTest
 */
public class JsonSchemaTest {

	@Test
	public void testRightData() {
		URL url = getClass().getResource("/");
		JsonNode schema = readJsonFile(url.getPath()
				+ "cn/himma/json/testSchema.json");
		JsonNode rightData = readJsonFile(url.getPath()
				+ "cn/himma/json/testRightData.json");
		ProcessingReport report = JsonSchemaFactory.byDefault().getValidator()
				.validateUnchecked(schema, rightData);
		Assert.assertTrue(report.isSuccess());
	}

	@Test
	public void testWrongData() {
		URL url = getClass().getResource("/");
		JsonNode schema = readJsonFile(url.getPath()
				+ "cn/himma/json/testSchema.json");
		JsonNode wrongData = readJsonFile(url.getPath()
				+ "cn/himma/json/testWrongData.json");
		ProcessingReport report = JsonSchemaFactory.byDefault().getValidator()
				.validateUnchecked(schema, wrongData);
		Assert.assertFalse(report.isSuccess());

		for (ProcessingMessage processingMessage : report) {
			System.out.println(processingMessage);
		}

	}

	private JsonNode readJsonFile(String filePath) {
		JsonNode instance = null;
		try {
			instance = new JsonNodeReader()
					.fromReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instance;
	}
}
