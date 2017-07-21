package cn.himma.mq;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {
    private static Logger log = Logger.getLogger(RabbitMQSender.class);
    private int retryTimes = 3; // 重试次数

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.testone}")
    private String exchange;
    @Value("${rabbitmq.queue.testone}")
    private String queue;

    /**
     * 发送消息
     * 
     * @param exchange
     * @param message
     */
    public void sendMsg(Object message) {
        sendMsgWithRetry(message, retryTimes);
    }

    /**
     * @param message
     * @param retry2
     */
    private void sendMsgWithRetry(Object message, int retryTimes) {
        for (int i = 0; i < retryTimes; i++) {
            try {
                rabbitTemplate.setExchange(exchange);
                // rabbitTemplate.setQueue(switchjrezQueue);
                // rabbitTemplate.setRoutingKey("");
                rabbitTemplate.setEncoding("UTF-8");
                rabbitTemplate.convertAndSend(message);
                log.info("send success!");
                break;
            } catch (Exception e) {
                log.error("sendMsg(" + message.toString() + ")", e);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    log.error("Thread.sleep error", e);
                }
            }
        }
    }
}
