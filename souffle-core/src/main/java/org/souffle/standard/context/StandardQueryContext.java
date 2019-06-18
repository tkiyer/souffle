package org.souffle.standard.context;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.souffle.execution.QueryExecutor;
import org.souffle.execution.QueryTask;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QueryException;
import org.souffle.spi.QueryIdGenerator;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.context.QueryStateChangeListener;
import org.souffle.spi.context.QueryStateEvent;
import org.souffle.spi.context.StateableQueryContext;
import org.souffle.spi.support.QueryRecovery;
import org.souffle.utils.ClassUtils;
import org.souffle.utils.Conditions;
import org.souffle.utils.Network;
import org.souffle.utils.QueryLogger;
import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.metadata.QueryState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;

import static org.souffle.spi.QueryConfigurable.QUERY_EXECUTE_TIMEOUT_MILLISECONDS;
import static org.souffle.spi.QueryConfigurable.QUERY_RECOVERY_CLASSNAME;


/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午9:55
 * @see
 * @since JDK1.7
 */
public class StandardQueryContext implements StateableQueryContext {

    public final static long DEFAULT_QUERY_EXECUTE_TIMEOUT_MILLISECONDS = 5 * 60 * 1000;

    private final static Logger LOGGER = LoggerFactory.getLogger(StandardQueryContext.class);

    private final static String DEFAULT_QUERY_RECOVERY_CLASSNAME = "org.souffle.core.context.RedisQueryRecoverySyncer";

    private final String applicationId;

    private final QueryConfiguration queryConfiguration;

    private final QueryExecutor queryExecutor;

    private final QueryMetadata queryMetadata;

    private QueryMetric queryMetric;

    private QueryTask queryTask;

    private final Object lock = new Object();

    private volatile QueryState queryState = QueryState.DEFINE;

    private Throwable errorThrowable;

    private String errorMessage;

    private final QueryId queryId;

    private final QueryGroup queryGroup;

    private final List<QueryStateChangeListener> queryStateChangeListeners = new ArrayList<>(10);

    private long taskExecuteTimeoutMilliseconds;

    private QueryRecovery queryRecovery;

    private GsonBuilder gsonBuilder;

    protected StandardQueryContext(String applicationId, QueryConfiguration queryConfiguration, QueryExecutor queryExecutor, QueryMetadata queryMetadata, QueryRecovery queryRecovery) {
        this(applicationId, queryConfiguration, queryExecutor, queryMetadata, queryRecovery, QueryIdGenerator.get().createNextQueryId(), new QueryMetric());
    }

    protected StandardQueryContext(String applicationId, QueryConfiguration queryConfiguration, QueryExecutor queryExecutor, QueryMetadata queryMetadata, QueryRecovery queryRecovery, QueryId queryId, QueryMetric queryMetric) {
        this.applicationId = applicationId;
        this.queryConfiguration = queryConfiguration;
        this.queryExecutor = queryExecutor;
        this.queryMetadata = queryMetadata;
        this.queryMetric = queryMetric;
        this.queryId = queryId;
        this.queryGroup = new QueryGroup(this.queryMetadata.getGroupId());

        this.queryRecovery = queryRecovery;

        this.queryTask = new QueryTask(this);

        taskExecuteTimeoutMilliseconds = this.queryConfiguration.getLong(QUERY_EXECUTE_TIMEOUT_MILLISECONDS, DEFAULT_QUERY_EXECUTE_TIMEOUT_MILLISECONDS);

        this.gsonBuilder = new GsonBuilder().serializeNulls();
    }

    @Override
    public String getApplicationId() {
        return this.applicationId;
    }

    @Override
    public QueryId getQueryId() {
        return this.queryId;
    }

    @Override
    public QueryGroup getQueryGroup() {
        return this.queryGroup;
    }

    @Override
    public QueryMetadata getQueryMetadata() {
        return this.queryMetadata;
    }

    @Override
    public QueryMetric getQueryMetric() {
        return this.queryMetric;
    }

    @Override
    public QueryRecovery getQueryRecovery() {
        return this.queryRecovery;
    }

    @Override
    public QueryConfiguration getQueryConfiguration() {
        return this.queryConfiguration;
    }

    @Override
    public QueryState getQueryState() {
        return this.queryState;
    }

    @Override
    public Throwable getErrorThrowable() {
        return this.errorThrowable;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public void accepted() {
        this.queryMetric.getElapsedTime().begin();
        compareAndSetQueryState(QueryState.ACCEPTED, QueryState.DEFINE);
    }

    @Override
    public void analysis() {
        this.queryMetric.getQueuedTime().end();
        this.queryMetric.getAnalysisTime().begin();
        compareAndSetQueryState(QueryState.ANALYSIS, QueryState.ACCEPTED, QueryState.QUEUED);
    }

    @Override
    public void queued() {
        // set queued time.
        this.queryMetric.getQueuedTime().begin();
        compareAndSetQueryState(QueryState.QUEUED, QueryState.ACCEPTED);
    }

    @Override
    public void running() {
        this.queryMetric.getExecutionTime().begin();
        compareAndSetQueryState(QueryState.RUNNING, QueryState.ANALYSIS);
    }

    @Override
    public void finished() {
        this.queryMetric.endAllDurationTime();
        compareAndSetQueryState(QueryState.FINISHED, QueryState.RUNNING);
    }

    @Override
    public void failed(Throwable t) {
        LOGGER.error(t.getLocalizedMessage(), t);
        setErrorThrowable(t);
        this.queryMetric.endAllDurationTime();
        setQueryState(QueryState.FAILED);
    }

    @Override
    public void canceled() {
        try {
            if (queryTask.cancel()) {
                this.queryMetric.endAllDurationTime();
                // set state
                setQueryState(QueryState.CANCELED);
            } else {
                failed(new QueryException("Cannot cancel query."));
            }
        } catch (Throwable t) {
            failed(t);
        }
    }

    @Override
    public void rejected() {
        setErrorThrowable(new QueryException("Query task reject, query pool is full."));
        this.queryMetric.endAllDurationTime();
        compareAndSetQueryState(QueryState.REJECTED, QueryState.ACCEPTED);
    }

    @Override
    public void tryTimeout() {
        if (this.queryMetric.getElapsedTime().getDuration() > taskExecuteTimeoutMilliseconds) {
            LOGGER.debug(String.format("Query[%s] was time out, then cancel it.", this.queryId));
            setErrorThrowable(new QueryException("Query task execute timeout."));
            canceled();
        }
    }

    @Override
    public QuerySummary ofSummary() {
        return new QuerySummary(this.applicationId, this.queryId, this.queryMetadata, this.queryMetric, this.queryTask.progress(), this.queryState);
    }

    @Override
    public void signal(boolean isSkipAnalysis) {
        accepted();
        if (isSkipAnalysis) {
            this.queryTask.skipAnalysis();
        }
        // 结束处理
        addQueryStateChangeListener(event -> {
            if (event.getNewQueryState().isDone()) {
                signalQueryCallback();
            }
        });
        safeExecute();
        LOGGER.debug(String.format("Signal query[%s] execute.", this.queryId));
    }

    @Override
    public void addQueryStateChangeListener(QueryStateChangeListener listener) {
        Objects.requireNonNull(listener, "QueryStateChangeListener is null.");
        boolean isTerminalState;
        synchronized (lock) {
            isTerminalState = isTerminalState(this.queryState);
            if (!isTerminalState) {
                this.queryStateChangeListeners.add(listener);
            }
        }
        // state machine will never transition from a terminal state, so fire state change immediately
        if (isTerminalState) {
            listener.stateChanged(new QueryStateEvent(this, this.queryState, this.queryState));
        }
    }

    protected void fireStateChangeListener(QueryStateEvent event, List<QueryStateChangeListener> listeners) {
        Conditions.checkState(!Thread.holdsLock(lock), "Can not fire state change event while holding the lock");
        Objects.requireNonNull(event, "QueryStateEvent is null.");
        LOGGER.debug(String.format("Fire state change from [%s] to [%s] listeners(%s).", event.getOldQueryState(), event.getNewQueryState(), listeners.size()));
        for (QueryStateChangeListener listener : listeners) {
            try {
                listener.stateChanged(event);
            } catch (Throwable e) {
                LOGGER.error(String.format("Error notifying state from %s to %s change listener for %s", event.getOldQueryState(), event.getNewQueryState(), event.toString()), e);
            }
        }
    }

    private void setErrorThrowable(Throwable throwable) {
        this.errorThrowable = throwable;
        this.errorMessage = throwable.getMessage();
    }

    private void safeExecute() {
        try {
            queryExecutor.submit(queryTask);
        } catch (RejectedExecutionException ree) {
            if (queryExecutor.isShutdown()) {
                throw new QueryException("Server is shutting down", ree);
            }
            throw ree;
        }
    }

    private QueryState compareAndSetQueryState(QueryState newQueryState, QueryState... expectedState) {
        Conditions.checkState(!Thread.holdsLock(lock), "Can not set state while holding the lock.");
        Objects.requireNonNull(newQueryState, "New query state is null.");
        Objects.requireNonNull(expectedState, "Expected query state is null.");

        QueryState oldQueryState;
        List<QueryStateChangeListener> fireListeners;
        synchronized (lock) {
            if (this.queryState == newQueryState || this.queryState == QueryState.CANCELED) {
                return this.queryState;
            }
            Conditions.checkQueryStateSet(this.queryState, expectedState);
            Conditions.checkState(!isTerminalState(this.queryState), String.format("Query state can not transition from %s to %s", this.queryState, newQueryState));
            oldQueryState = this.queryState;
            this.queryState = newQueryState;
            LOGGER.debug(String.format("Compare current state[%s] expected[%s], set new state[%s]", this.queryState, Arrays.toString(expectedState), newQueryState));
            // log query summary info when state changed.
            QueryLogger.SUMMARY_LOGGER.info(this.applicationId + "|" + gsonBuilder.create().toJson(ofSummary()));
            // copy listeners
            fireListeners = Collections.unmodifiableList(new ArrayList<>(this.queryStateChangeListeners));
            // if terminal state, clear listeners.
            if (isTerminalState(this.queryState)) {
                this.queryStateChangeListeners.clear();
            }
            // release lock, notify all.
            lock.notifyAll();
        }
        // fire state changed event listeners.
        fireStateChangeListener(new QueryStateEvent(this, oldQueryState, newQueryState), fireListeners);
        return oldQueryState;
    }

    private QueryState setQueryState(QueryState newQueryState) {
        Conditions.checkState(!Thread.holdsLock(lock), "Can not set state while holding the lock.");
        Objects.requireNonNull(newQueryState, "New query state is null.");
        QueryState oldQueryState;
        List<QueryStateChangeListener> fireListeners;
        synchronized (lock) {
            if (this.queryState == newQueryState || this.queryState == QueryState.CANCELED) {
                return this.queryState;
            }
            Conditions.checkState(!isTerminalState(this.queryState), String.format("Query state can not transition from %s to %s", this.queryState, newQueryState));
            oldQueryState = this.queryState;
            this.queryState = newQueryState;
            LOGGER.debug(String.format("Set current state[%s] to new state [%s]", this.queryState, newQueryState));
            // log query summary info when state changed.
            QueryLogger.SUMMARY_LOGGER.info(this.applicationId + "|" + gsonBuilder.create().toJson(ofSummary()));
            // copy listeners
            fireListeners = Collections.unmodifiableList(new ArrayList<>(this.queryStateChangeListeners));
            // if terminal state, clear listeners.
            if (isTerminalState(this.queryState)) {
                this.queryStateChangeListeners.clear();
            }
            // release lock, notify all.
            lock.notifyAll();
        }
        // fire state changed event listeners.
        fireStateChangeListener(new QueryStateEvent(this, oldQueryState, newQueryState), fireListeners);
        return oldQueryState;
    }

    private boolean isTerminalState(QueryState queryState) {
        return QueryState.TERMINAL_QUERY_STATES.contains(queryState);
    }

    private QueryRecovery createQueryRecovery() {
        String className = this.queryConfiguration.get(QUERY_RECOVERY_CLASSNAME, DEFAULT_QUERY_RECOVERY_CLASSNAME);
        return ClassUtils.newInstance(className, QueryRecovery.class, new Class[]{QueryContext.class}, new Object[]{this});
    }

    private void signalQueryCallback() {
        String callbackUrl = this.queryMetadata.getCallbackUrl();
        if (StringUtils.isNotBlank(callbackUrl)) {
            Map<String, Object> params = new HashMap<>();
            params.put("queryId", this.queryId.toString());
            params.put("state", this.queryState.name());

            Network network = new Network(callbackUrl);
            try {
                if (network.verify()) {
                    network.doHttpPost(params, 3);
                }
            } finally {
                network.destroy();
            }
        }
    }
}
