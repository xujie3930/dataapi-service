package com.jinninghui.datasphere.icreditstudio.framework.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liyanhui
 */
@Component
public class ServiceThreadManager {

    private AtomicInteger threadCount = new AtomicInteger(0);
    private ExecutorService service;
    private static final Logger logger = LoggerFactory.getLogger(ServiceThreadManager.class);

    public ServiceThreadManager() {
        this.service = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), r -> {
                    Thread thread = new Thread(r);
                    thread.setName("Service Thread ->" + ServiceThreadManager.this.threadCount.getAndIncrement());
                    thread.setDaemon(true);
                    thread.setUncaughtExceptionHandler((t, e) -> logger.error("ServiceThreadManager exception ,", e));
                    return thread;
                });
    }

    public void execute(Runnable task) {
        this.service.execute(task);
    }
}
