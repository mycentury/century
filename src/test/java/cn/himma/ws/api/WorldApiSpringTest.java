/**
 * 
 */
package cn.himma.ws.api;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;

import cn.himma.BaseTest;
import cn.himma.util.framework.SpringContextUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月23日
 * @ClassName HelloApiTest
 */
public class WorldApiSpringTest extends BaseTest {
	private WorldApi worldApi;

	/**
	 * Test method for
	 * {@link cn.himma.ws.api.WorldApi#sayWorld(java.lang.String)}.
	 */
	@Test
	public void testSayHello() {
		initWorldApi();
		System.out.println(worldApi.sayWorld("test"));
	}

	@Test
	public void testByFactory() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(WorldApi.class);
		factory.setAddress("http://localhost:8280/ws/world");
		WorldApi helloApi = (WorldApi) factory.create();

		// HelloApi helloApi = service.getPort(HelloApi.class);
		System.out.println(helloApi.sayWorld("test"));
	}

	private void initWorldApi() {
		if (worldApi == null) {
			worldApi = (WorldApi) SpringContextUtil.getBean("worldService");
		}
	}

}
