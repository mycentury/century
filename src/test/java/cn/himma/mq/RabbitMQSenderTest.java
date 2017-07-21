/**
 * 
 */
package cn.himma.mq;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.himma.BaseTest;

/**
 * @Desc
 * @author wewenge.yan
 * @Date 2016年08鏈04日
 * @ClassName RabbitMQSenderTest
 */
public class RabbitMQSenderTest extends BaseTest {

	@Autowired
	private RabbitMQSender rabbitMQSender;

	/**
	 * Test method for
	 * {@link cn.himma.mq.RabbitMQSender#sendMsg(java.lang.Object)}.
	 */
	@Test
	public void testSendMsg() {
		rabbitMQSender.sendMsg("Fine!Thank you!And you?");
	}

}
