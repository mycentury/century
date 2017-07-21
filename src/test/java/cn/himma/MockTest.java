/**
 * 
 */
package cn.himma;

import mockit.integration.junit4.JMockit;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月20日
 * @ClassName BaseTest
 */
@ContextConfiguration(locations = { "classpath:config/spring-context.xml",
		"classpath:config/springmvc-servlet.xml",
		"classpath:config/hessian-client.xml",
		"classpath:config/spring-rabbitmq.xml" })
@RunWith(JMockit.class)
public abstract class MockTest extends AbstractJUnit4SpringContextTests {

}
