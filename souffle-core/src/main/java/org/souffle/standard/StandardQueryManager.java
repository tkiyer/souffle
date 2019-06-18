package org.souffle.standard;

import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QueryException;
import org.souffle.spi.QueryGroupSummary;
import org.souffle.spi.QueryManager;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.context.StateableQueryContext;
import org.souffle.spi.support.QueryRepository;
import org.souffle.spi.support.QueryStorage;
import org.souffle.standard.context.StandardQueryContextBuilder;
import org.souffle.execution.QueryExecutor;
import org.souffle.execution.QueryMonitor;
import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午9:55
 * @see
 * @since JDK1.7
 */
public class StandardQueryManager implements QueryManager {

    private final String applicationId;

    private QueryConfiguration queryConfiguration;

    private QueryExecutor queryExecutor;

    private QueryRepository queryRepository;

    public StandardQueryManager(String applicationId, QueryConfiguration queryConfiguration, QueryRepository queryRepository) {
        this.applicationId = applicationId;
        this.queryConfiguration = queryConfiguration;
        this.queryExecutor = new QueryExecutor(this.queryConfiguration);
        this.queryRepository = queryRepository;
    }

    @Override
    public QuerySummary signalQuery(QueryMetadata queryMetadata) {
        return signalQuery(queryMetadata, false);
    }

    @Override
    public QuerySummary signalQuery(QueryMetadata queryMetadata, boolean isSkipAnalysis) {
        StandardQueryContextBuilder builder = (StandardQueryContextBuilder) this.queryRepository.newQueryContextBuilder();
        QueryContext queryContext = builder.preparedQueryExecutor(this.queryExecutor)
                                            .preparedQueryMetadata(queryMetadata)
                                            .build();
        // Monitoring query.
        QueryMonitor.get().addQuery(queryContext);
        // Signal query start. run as thread backend.
        queryContext.signal(isSkipAnalysis);
        return queryContext.ofSummary();
    }

    @Override
    public QueryResultSet getQueryResultSet(QueryId queryId) {
        return this.queryRepository.newQueryResultFetcher(queryId).fetch();
    }

    @Override
    public QueryResultSet getQueryResultSetPage(QueryId queryId, int from, int size) {
        return this.queryRepository.newQueryResultFetcher(queryId).fetch(from, size);
    }

    @Override
    public QuerySummary getQuerySummary(QueryId queryId) {
        QueryStorage storage = this.queryRepository.getQuery(queryId);
        if (null != storage) {
            return storage.getQuerySummary();
        }
        throw new QueryException("Query not found with id " + queryId);
    }

    @Override
    public QueryGroupSummary getQueryGroupSummary(QueryGroup queryGroup) {
        List<QueryStorage> storageList = this.queryRepository.getGroupQueries(queryGroup);
        if (null == storageList || storageList.isEmpty()) {
            return new QueryGroupSummary(applicationId, queryGroup, new ArrayList<>());
        }
        List<QuerySummary> querySummaries = new ArrayList<>();
        for (QueryStorage storage : storageList) {
            querySummaries.add(storage.getQuerySummary());
        }
        return new QueryGroupSummary(applicationId, queryGroup, querySummaries);
    }

    @Override
    public void cancelQuery(QueryId queryId) {
        QueryContext queryContext = QueryMonitor.get().getQuery(queryId);
        Objects.requireNonNull(queryContext, String.format("Query[%s] already canceled or not exists.", queryId));
        ((StateableQueryContext) queryContext).canceled();
    }

    public QueryExecutor getQueryExecutor() {
        return this.queryExecutor;
    }
}
