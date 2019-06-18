package org.souffle.standard.support;

import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.context.ExecutionQueryContext;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.context.QueryContextBuilder;
import org.souffle.spi.context.StateableQueryContext;
import org.souffle.spi.result.QueryResultFetcher;
import org.souffle.spi.support.QueryRecovery;
import org.souffle.spi.support.QueryRepository;
import org.souffle.spi.support.QueryStorage;
import org.souffle.standard.context.StandardQueryContextBuilder;
import org.souffle.execution.QueryMonitor;
import org.souffle.utils.Conditions;
import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryState;

import java.util.ArrayList;
import java.util.List;

/**
 * Your class description. <p />
 *
 * @author tkiyer
 * @version 1.0.0
 * @date 2019/6/4 20:32
 * @see
 * @since JDK1.8
 */
public class StandardQueryRepository implements QueryRepository {

    private final String applicationId;

    private QueryConfiguration queryConfiguration;

    public StandardQueryRepository(String applicationId, QueryConfiguration queryConfiguration) {
        this.applicationId = applicationId;
        this.queryConfiguration = queryConfiguration;
    }

    @Override
    public QueryStorage getQuery(QueryId queryId) {
        QueryContext queryContext = QueryMonitor.get().getQuery(queryId);
        if (null != queryContext) {
            return new QueryStorage(queryContext);
        }
        return null;
    }

    @Override
    public List<QueryStorage> getGroupQueries(QueryGroup queryGroup) {
        List<QueryContext> qcl = QueryMonitor.get().getGroupQueries(queryGroup);
        if (qcl.isEmpty()) {
            return null;
        }
        List<QueryStorage> qsl = new ArrayList<>(qcl.size());
        for (QueryContext queryContext : qcl) {
            qsl.add(new QueryStorage(queryContext));
        }
        return qsl;
    }

    @Override
    public List<QueryStorage> getApplicationQueries(QueryState... stats) {
        List<QueryContext> qcl = QueryMonitor.get().getApplicationQueries();
        if (qcl.isEmpty()) {
            return null;
        }
        List<QueryStorage> qsl = new ArrayList<>();
        for (QueryContext queryContext : qcl) {
            if (Conditions.checkQueryStateSetResult(((StateableQueryContext) queryContext).getQueryState(), stats)) {
                qsl.add(new QueryStorage(queryContext));
            }
        }
        return qsl;
    }

    @Override
    public StandardQueryContextBuilder newQueryContextBuilder() {
        return new StandardQueryContextBuilder(this.applicationId, this.queryConfiguration, this);
    }

    @Override
    public QueryRecovery newQueryRecovery() {
        return new StandardQueryRecovery(this.applicationId, this.queryConfiguration);
    }

    @Override
    public QueryResultFetcher newQueryResultFetcher(QueryId queryId) {
        QueryContext queryContext = QueryMonitor.get().getQuery(queryId);
        if (queryContext instanceof ExecutionQueryContext) {
            ExecutionQueryContext eqc = (ExecutionQueryContext) queryContext;
            if (eqc.getQueryState().isDone()) {
                return eqc.getQueryResultFetcher();
            }
        }
        return null;
    }
}
