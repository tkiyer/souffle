package org.souffle.execution;


import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.spi.context.ExecutionQueryContext;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.context.StateableQueryContext;
import org.souffle.spi.support.QueryRecovery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/10 下午2:52
 * @see
 * @since JDK1.7
 */
public class QueryMonitor {

    private final String monitorId;

    private ConcurrentMap<String, StateableQueryContext> queryContextCacheMap = new ConcurrentHashMap<>();

    private volatile boolean isCancelQueryContextMonitor = false;

    private Runnable queryContextMonitorRunnable;

    private ExecutorService memMonitorThreadPool;

    private static final QueryMonitor QUERY_MONITOR = new QueryMonitor();

    private QueryMonitor() {
        this.monitorId = UUID.randomUUID().toString();
        this.memMonitorThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {

            private final AtomicInteger threadCount = new AtomicInteger(0);

            private final ThreadGroup threadGroup = new ThreadGroup("SOUFFLE-MONITOR");

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(threadGroup, r, "SOUFFLE-MONITOR-" + threadCount.incrementAndGet());
                t.setDaemon(true);
                return t;
            }
        });
        initMonitorRunnable();
    }

    private void initMonitorRunnable() {
        this.queryContextMonitorRunnable = () -> {
            while (!isCancelQueryContextMonitor) {
                Iterator<Map.Entry<String, StateableQueryContext>> iter = this.queryContextCacheMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, StateableQueryContext> queryContextEntry = iter.next();
                    StateableQueryContext queryContext = queryContextEntry.getValue();
                    if (queryContext.getQueryState().isDone()) {
                        // if query is done, wipe query recovery (remove from external cache.).
                       queryContext.getQueryRecovery().wipe();   // recovery auto expire from repository, not manual wipe.
                        // remove from memory cache
                        iter.remove();
                    } else {
                        queryContext.tryTimeout();
                        // re-check query state
                        if (!queryContext.getQueryState().isDone()) {
                            // rsync
                            queryContext.getQueryRecovery().rsync();
                        }
                    }
                }
                // sleep 200ms
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        };
    }

    public void addQuery(QueryContext queryContext) {
        StateableQueryContext sqc = (StateableQueryContext) queryContext;
        // add listener, rsync current query summary info to recovery, not check rsync interval time.
        sqc.addQueryStateChangeListener(event -> event.getQueryContext().getQueryRecovery().rsync(false));
        this.queryContextCacheMap.put(getQueryIdentifier(queryContext), sqc);
    }

    protected void registerExecution(ExecutionQueryContext executionQueryContext) {
        this.queryContextCacheMap.put(getQueryIdentifier(executionQueryContext), executionQueryContext);
    }

    public QueryContext getQuery(QueryId queryId) {
        return this.queryContextCacheMap.getOrDefault(queryId, null);
    }

    public List<QueryContext> getGroupQueries(QueryGroup queryGroup) {
        List<QueryContext> qcl = new ArrayList<>();
        for (Map.Entry<String, StateableQueryContext> entry : queryContextCacheMap.entrySet()) {
            if (entry.getKey().startsWith(entry.getValue().getApplicationId() + QueryRecovery.RSYNC_KEY_SPLIT + queryGroup.getId() + QueryRecovery.RSYNC_KEY_SPLIT)) {
                qcl.add(entry.getValue());
            }
        }
        return qcl;
    }

    public List<QueryContext> getApplicationQueries() {
        return new ArrayList<>(this.queryContextCacheMap.values());
    }

    public void start() {
        memMonitorThreadPool.submit(this.queryContextMonitorRunnable);
    }

    public void stop() {
        this.isCancelQueryContextMonitor = true;
        memMonitorThreadPool.shutdown();
    }

    public static QueryMonitor get() {
        return QUERY_MONITOR;
    }

    public final String getMonitorId() {
        return monitorId;
    }

    private String getQueryIdentifier(QueryContext queryContext) {
        return queryContext.getApplicationId() + QueryRecovery.RSYNC_KEY_SPLIT +
                queryContext.getQueryGroup().getId() + QueryRecovery.RSYNC_KEY_SPLIT +
                queryContext.getQueryId().getId();
    }
}
