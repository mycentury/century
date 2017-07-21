/**
 * 
 */
package cn.himma.task;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import cn.himma.util.framework.SpringContextUtil;

/**
 * @Desc
 * @author wenge.yan
 * @Date 2016年6月24日
 * @ClassName HelloTask
 */
@Component
@Profile("hello")
public class HelloTask {

    private long start = System.currentTimeMillis();

    // @Scheduled(cron = "0 0/1 * * * ?")
    public void sayHello() {
        System.out.println("Hello!----------" + (System.currentTimeMillis() - start) / 1000);
        // try {
        // Thread.sleep(10000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) (SpringContextUtil.getBean("executor"));
        System.out.println(threadPoolTaskExecutor.getActiveCount());
        System.out.println("end!------------" + (System.currentTimeMillis() - start) / 1000);
    }
}
