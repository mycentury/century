package cn.himma.mq;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ConsumerListener {
    private static final Logger logger = Logger.getLogger(ConsumerListener.class);

    public void onMessage(String payId) throws Exception {
        logger.info("json message: " + payId);
    }
}
