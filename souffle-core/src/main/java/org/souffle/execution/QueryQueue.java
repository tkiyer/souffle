package org.souffle.execution;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/10 下午3:07
 * @see
 * @since JDK1.7
 */
public class QueryQueue extends LinkedBlockingQueue<Runnable> {

    public QueryQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(Runnable r, long timeout, TimeUnit unit) throws InterruptedException {
        boolean isOffered = super.offer(r, timeout, unit);
        if (isOffered) {
            if (r instanceof QueryTask.QueryExecutionFutureTask) {
                ((QueryTask.QueryExecutionFutureTask) r).onQueue();
            }
        }
        return isOffered;
    }

    @Override
    public boolean offer(Runnable r) {
        boolean isOffered = super.offer(r);
        if (isOffered) {
            if (r instanceof QueryTask.QueryExecutionFutureTask) {
                ((QueryTask.QueryExecutionFutureTask) r).onQueue();
            }
        }
        return isOffered;
    }
}
