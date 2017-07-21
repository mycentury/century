/**
 * 
 */
package cn.himma.hs.api;

import org.junit.Test;

import cn.himma.BaseTest;
import cn.himma.util.framework.SpringContextUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月22日
 * @ClassName HelloApiTest
 */
public class HelloApiSpringTest extends BaseTest {
	private HelloApi helloApi;

	/**
	 * Test method for
	 * {@link cn.himma.hs.api.HelloApi#sayHello(java.lang.String)}.
	 */
	// @Ignore
	@Test
	public void testSayHello() {
		initHelloApi();
		System.out.println(helloApi.sayHello("test"));
	}

	private void initHelloApi() {
		if (helloApi == null) {
			helloApi = (HelloApi) SpringContextUtil.getBean("helloApi");
		}
	}

}
