package org.souffle.spi.context;

import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.metadata.QueryState;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.engine.QueryPlan;
import org.souffle.spi.result.QueryResultFetcher;
import org.souffle.spi.support.QueryRecovery;

/**
 * Your class description. <p />
 *
 * @author tkiyer
 * @version 1.0.0
 * @date 2019/6/6 16:12
 * @see
 * @since JDK1.8
 */
public class ExecutionQueryContext implements StateableQueryContext {

    private final StateableQueryContext queryContext;

    private final QueryPlan queryPlan;

    private QueryResultFetcher queryResultFetcher;

    public ExecutionQueryContext(StateableQueryContext queryContext, QueryPlan queryPlan) {
        this.queryContext = queryContext;
        this.queryPlan = queryPlan;
    }

    public void registerResultFetcher(QueryResultFetcher queryResultFetcher) {
        this.queryResultFetcher = queryResultFetcher;
    }

    public QueryResultFetcher getQueryResultFetcher() {
        return this.queryResultFetcher;
    }

    public StateableQueryContext getQueryContext() {
        return this.queryContext;
    }

    public QueryPlan getQueryPlan() {
        return this.queryPlan;
    }

    @Override
    public QueryState getQueryState() {
        return this.queryContext.getQueryState();
    }

    @Override
    public void accepted() {
        this.queryContext.accepted();
    }

    @Override
    public void analysis() {
        this.queryContext.analysis();
    }

    @Override
    public void queued() {
        this.queryContext.queued();
    }

    @Override
    public void running() {
        this.queryContext.running();
    }

    @Override
    public void finished() {
        this.queryContext.finished();
    }

    @Override
    public void failed(Throwable t) {
        this.queryContext.failed(t);
    }

    @Override
    public void canceled() {
        this.queryContext.canceled();
    }

    @Override
    public void rejected() {
        this.queryContext.rejected();
    }

    @Override
    public void tryTimeout() {
        this.queryContext.tryTimeout();
    }

    @Override
    public void addQueryStateChangeListener(QueryStateChangeListener listener) {
        this.queryContext.addQueryStateChangeListener(listener);
    }

    @Override
    public String getApplicationId() {
        return this.queryContext.getApplicationId();
    }

    @Override
    public QueryId getQueryId() {
        return this.queryContext.getQueryId();
    }

    @Override
    public QueryGroup getQueryGroup() {
        return this.queryContext.getQueryGroup();
    }

    @Override
    public QueryMetadata getQueryMetadata() {
        return this.queryContext.getQueryMetadata();
    }

    @Override
    public QueryMetric getQueryMetric() {
        return this.queryContext.getQueryMetric();
    }

    @Override
    public QueryRecovery getQueryRecovery() {
        return this.queryContext.getQueryRecovery();
    }

    @Override
    public QueryConfiguration getQueryConfiguration() {
        return this.queryContext.getQueryConfiguration();
    }

    @Override
    public QuerySummary ofSummary() {
        return this.queryContext.ofSummary();
    }

    @Override
    public void signal(boolean isSkipAnalysis) {
        this.queryContext.signal(isSkipAnalysis);
    }

    @Override
    public Throwable getErrorThrowable() {
        return this.queryContext.getErrorThrowable();
    }

    @Override
    public String getErrorMessage() {
        return this.queryContext.getErrorMessage();
    }
}
