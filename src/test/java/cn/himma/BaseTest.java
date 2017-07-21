/**
 * 
 */
package cn.himma;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月20日
 * @ClassName BaseTest
 */
@ContextConfiguration(locations = { "classpath:spring/spring-context.xml",
		"classpath:spring/springmvc-servlet.xml",
		"classpath:spring/spring-rabbitmq.xml",
		"classpath:spring/cxf-client.xml",
		"classpath:spring/hessian-client.xml" })
// @RunWith(JUnit5.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
// @ActiveProfiles({})
public abstract class BaseTest extends AbstractJUnit4SpringContextTests {

}
