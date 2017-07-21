package cn.himma.util.excutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class ThreadPool<T extends Runnable> {
    private static final Logger logger = Logger.getLogger(ThreadPool.class);

    public ThreadPool(int maxSize) {
        this.maxSize = maxSize;
    }

    // 公用线程池
    public final static ExecutorService executor = Executors.newCachedThreadPool();
    private final Integer maxSize;
    private AtomicInteger currentSize = new AtomicInteger(0);

    public void excute(T t) {
        boolean b = false;
        while (true) {
            if (currentSize.get() < maxSize) {
                b = true;
                currentSize.incrementAndGet();
            }
            if (b) {
                executor.execute(t);
                break;
            }
            synchronized (this) {
                try {
                    wait(1000 * 1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public void free() {
        currentSize.getAndDecrement();
    }

    public abstract static class PoolRunnable<T extends PoolRunnable<?>> implements Runnable {

        @Override
        public void run() {
            try {
                excute();
            } catch (Exception e) {
                logger.error("excute", e);
            } finally {
                getPool().free();
            }
        }

        protected abstract void excute();

        public abstract ThreadPool<T> getPool();

    }

}
