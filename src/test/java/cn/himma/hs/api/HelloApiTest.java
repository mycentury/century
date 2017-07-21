/**
 * 
 */
package cn.himma.hs.api;

import java.net.MalformedURLException;

import org.junit.Test;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月22日
 * @ClassName HelloApiTest
 */
public class HelloApiTest {
	/**
	 * Test method for
	 * {@link cn.himma.hs.api.HelloApi#sayHello(java.lang.String)}.
	 */
	// @Ignore
	@Test
	public void testSayHello() {

		String url = "http://localhost:8280/hessian/HelloApi";
		HessianProxyFactory factory = new HessianProxyFactory();
		HelloApi helloApi = null;
		try {
			helloApi = (HelloApi) factory.create(HelloApi.class, url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println(helloApi.sayHello("test"));
	}

}
