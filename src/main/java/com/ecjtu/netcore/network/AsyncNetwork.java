package com.ecjtu.netcore.network;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by KerriGan on 2017/7/14.
 */
public class AsyncNetwork extends BaseNetwork {

    private static final String TAG = "AsyncNetwork";

    private static AtomicInteger sThreadsCount = new AtomicInteger(0);

    private static final boolean DEBUG = false;

    private ThreadPoolExecutor sFixedThreadPool = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() + 1,
            30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue()); // 阻塞队列防止Rejection异常

    private Future<?> mFuture = null;

    @Override
    public BaseNetwork request(String urlStr, Map<String, String> mutableMap) {
        mFuture = sFixedThreadPool.submit(() -> {
            if (DEBUG) {
                System.out.println(TAG + " task begin " + toString() + " task count:" + sThreadsCount.incrementAndGet());
            }
            try {
                super.request(urlStr, mutableMap);
            } catch (Exception e) {
                System.out.println(TAG + "task exception " + e.toString());
            }
            if (DEBUG) {
                System.out.println(TAG + "task end " + toString() + " task count:" + sThreadsCount.decrementAndGet());
            }
        });
        return this;
    }

    public void cancel() {
        super.cancel();
        mFuture.cancel(true);
    }
}