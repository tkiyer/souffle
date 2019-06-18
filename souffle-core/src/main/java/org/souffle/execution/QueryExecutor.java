package org.souffle.execution;


import org.souffle.spi.QueryConfiguration;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.souffle.spi.QueryConfigurable.QUERY_EXECUTE_THREAD_CORE_SIZE;
import static org.souffle.spi.QueryConfigurable.QUERY_EXECUTE_THREAD_MAX_SIZE;
import static org.souffle.spi.QueryConfigurable.QUERY_QUEUE_MAX_SIZE;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/10 上午12:27
 * @see
 * @since JDK1.7
 */
public class QueryExecutor {

    private static final int DEFAULT_THREAD_POOL_CORE_SIZE = 10;

    private static final int DEFAULT_THREAD_POOL_MAX_SIZE = 20;

    private static final int DEFAULT_QUEUE_MAX_SIZE = 10000;

    private final QueryConfiguration queryConfiguration;

    private ThreadPoolExecutor threadPoolExecutor;

    private final QueryQueue queryQueue;

    private QueryTaskThreadFactory threadFactory;

    private QueryTaskRejectExecutionHandler rejectExecutionHandler;

    public QueryExecutor(QueryConfiguration queryConfiguration) {
        this.queryConfiguration = queryConfiguration;
        int poolCoreSize = queryConfiguration.getInt(QUERY_EXECUTE_THREAD_CORE_SIZE, DEFAULT_THREAD_POOL_CORE_SIZE);
        int poolMaxSize = queryConfiguration.getInt(QUERY_EXECUTE_THREAD_MAX_SIZE, DEFAULT_THREAD_POOL_MAX_SIZE);
        int queryQueueSize = queryConfiguration.getInt(QUERY_QUEUE_MAX_SIZE, DEFAULT_QUEUE_MAX_SIZE);
        this.queryQueue = new QueryQueue(queryQueueSize);
        this.threadFactory = new QueryTaskThreadFactory();
        this.rejectExecutionHandler = new QueryTaskRejectExecutionHandler();
        this.threadPoolExecutor = new ThreadPoolExecutor(poolCoreSize, poolMaxSize, 1L, TimeUnit.MINUTES, this.queryQueue, this.threadFactory, this.rejectExecutionHandler);
    }

    public void submit(QueryTask queryTask) {
        threadPoolExecutor.execute(queryTask.getExecutionFutureTask());
    }

    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }

    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    public int getCurrentQueryQueueSize() {
        return this.queryQueue.size();
    }

    public int getCurrentCreatedThreadCount() {
        return this.threadFactory.threadCount.get();
    }

    public int getCurrentRejectedQueryTaskCount() {
        return this.rejectExecutionHandler.rejectedTaskCount.get();
    }

    public QueryConfiguration getQueryConfiguration() {
        return queryConfiguration;
    }

    private class QueryTaskThreadFactory implements ThreadFactory {

        private ThreadGroup threadGroup;

        private final AtomicInteger threadCount = new AtomicInteger(0);

        private QueryTaskThreadFactory() {
            super();
            this.threadGroup = new ThreadGroup("SOUFFLE-QUERY");
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.threadGroup, r, nextThreadName());
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

        private String nextThreadName() {
            return "SOUFFLE-QUERY-RUN-" + this.threadCount.incrementAndGet();
        }
    }

    private class QueryTaskRejectExecutionHandler implements RejectedExecutionHandler {

        private final AtomicInteger rejectedTaskCount = new AtomicInteger(0);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            this.rejectedTaskCount.incrementAndGet();
            if (r instanceof QueryTask.QueryExecutionFutureTask) {
                ((QueryTask.QueryExecutionFutureTask) r).reject();
            }
        }
    }
}
