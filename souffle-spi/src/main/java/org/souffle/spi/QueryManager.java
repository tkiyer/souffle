package org.souffle.spi;


import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryResultSet;

/**
 * 查询管理器，负责对查询的执行调度和查询状态的获取. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:02
 * @see
 * @since JDK1.7
 */
public interface QueryManager {

    QuerySummary signalQuery(QueryMetadata queryMetadata);

    QuerySummary signalQuery(QueryMetadata queryMetadata, boolean isSkipAnalysis);

    QueryResultSet getQueryResultSet(QueryId queryId);

    QueryResultSet getQueryResultSetPage(QueryId queryId, int from, int size);

    QuerySummary getQuerySummary(QueryId queryId);

    QueryGroupSummary getQueryGroupSummary(QueryGroup queryGroup);

    void cancelQuery(QueryId queryId);

}
