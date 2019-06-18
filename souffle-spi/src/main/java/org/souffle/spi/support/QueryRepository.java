package org.souffle.spi.support;

import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryState;
import org.souffle.spi.context.QueryContextBuilder;
import org.souffle.spi.result.QueryResultFetcher;

import java.util.List;

/**
 * Your class description. <p />
 *
 * @author tkiyer
 * @version 1.0.0
 * @date 2019/6/4 20:01
 * @see
 * @since JDK1.8
 */
public interface QueryRepository {

    QueryStorage getQuery(QueryId queryId);

    List<QueryStorage> getGroupQueries(QueryGroup queryGroup);

    List<QueryStorage> getApplicationQueries(QueryState... stats);

    QueryContextBuilder newQueryContextBuilder();

    QueryRecovery newQueryRecovery();

    QueryResultFetcher newQueryResultFetcher(QueryId queryId);
}
