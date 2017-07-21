/**
 * 
 */
package cn.himma.ws.api;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.junit.Test;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月23日
 * @ClassName HelloApiTest
 */
public class WorldApiTest {

	/**
	 * Test method for
	 * {@link cn.himma.ws.api.WorldApi#sayWorld(java.lang.String)}.
	 */
	// @Ignore
	@Test
	public void testSayWorld() {
		// 可以参考:http://localhost:8080/himma/ws/hello?wsdl
		// 默认名称HelloApi，也可以取serviceName
		final QName SERVICE_NAME = new QName("http://api.ws.himma.cn/",
				"WorldApiService");
		// 默认名称HelloApiPort
		final QName PORT_NAME = new QName("http://api.ws.himma.cn/",
				"WorldApiPort");
		Service service = Service.create(SERVICE_NAME);
		String endpointAddress = "http://localhost:8280/ws/world";

		// Add a port to the Service
		service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING,
				endpointAddress);

		WorldApi worldApi = service.getPort(WorldApi.class);
		System.out.println(worldApi.sayWorld("test"));
	}

}
