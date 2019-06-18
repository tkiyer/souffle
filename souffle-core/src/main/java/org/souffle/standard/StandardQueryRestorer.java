package org.souffle.standard;

import org.souffle.execution.QueryExecutor;
import org.souffle.execution.QueryMonitor;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetric;
import org.souffle.metadata.QueryState;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QueryRestorer;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.support.QueryRepository;
import org.souffle.spi.support.QueryStorage;
import org.souffle.standard.context.StandardQueryContextBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.souffle.spi.QueryConfigurable.QUERY_EXECUTE_TIMEOUT_MILLISECONDS;
import static org.souffle.standard.context.StandardQueryContext.DEFAULT_QUERY_EXECUTE_TIMEOUT_MILLISECONDS;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/18 下午3:57
 * @see
 * @since JDK1.7
 */
public class StandardQueryRestorer implements QueryRestorer {

    private static final SimpleDateFormat QUERY_SUMMARY_DATE_FORMAT = new SimpleDateFormat(QuerySummary.DATETIME_FORMAT_PATTERN);

    private final QueryConfiguration queryConfiguration;

    private QueryExecutor queryExecutor;

    private QueryRepository queryRepository;

    private long queryExecuteTimeoutMillis;

    public StandardQueryRestorer(QueryConfiguration queryConfiguration, QueryRepository queryRepository, StandardQueryManager queryManager) {
        this.queryConfiguration = queryConfiguration;
        this.queryRepository = queryRepository;
        this.queryExecuteTimeoutMillis = this.queryConfiguration.getLong(QUERY_EXECUTE_TIMEOUT_MILLISECONDS, DEFAULT_QUERY_EXECUTE_TIMEOUT_MILLISECONDS);
        this.queryExecutor = queryManager.getQueryExecutor();
    }

    @Override
    public void restoreQueries() {
        List<QueryStorage> storageList = this.queryRepository.getApplicationQueries(QueryState.NOT_TERMINAL_QUERY_STATES.toArray(new QueryState[0]));

        for (QueryStorage storage : storageList) {
            // filter less than timeout millis.
            QuerySummary summary = storage.getQuerySummary();
            if (summary.getTotalDuration() < this.queryExecuteTimeoutMillis) {
                // restore query.
                QueryMetric restoredQueryMetric = createRestoredQueryMetric(summary);
                StandardQueryContextBuilder builder = (StandardQueryContextBuilder) this.queryRepository.newQueryContextBuilder();
                QueryContext queryContext = builder.preparedQueryExecutor(this.queryExecutor)
                                                    .preparedQueryMetadata(storage.getQueryMetadata())
                                                    .preparedQueryId(QueryId.of(summary.getQueryId()))
                                                    .preparedQueryMetric(restoredQueryMetric)
                                                    .build(true);
                queryContext.signal(true);
                QueryMonitor.get().addQuery(queryContext);
            }
        }
    }

    @Override
    public QueryConfiguration getQueryConfiguration() {
        return queryConfiguration;
    }

    private QueryMetric createRestoredQueryMetric(QuerySummary summary) {
        QueryMetric.DurationTime queuedTime = new QueryMetric.DurationTime(parseMetricDate(summary.getQueuedStartDateTime()), parseMetricDate(summary.getQueuedEndDateTime()));
        QueryMetric.DurationTime analysisTime = new QueryMetric.DurationTime(parseMetricDate(summary.getAnalysisStartDateTime()), parseMetricDate(summary.getAnalysisEndDateTime()));
        QueryMetric.DurationTime executionTime = new QueryMetric.DurationTime(parseMetricDate(summary.getExecutionStartDateTime()), parseMetricDate(summary.getExecutionEndDateTime()));
        QueryMetric.DurationTime elapsedTime = new QueryMetric.DurationTime(parseMetricDate(summary.getStartDateTime()), parseMetricDate(summary.getEndDateTime()));
        return new QueryMetric(queuedTime, analysisTime, executionTime, elapsedTime);
    }

    private Date parseMetricDate(String strDatetime) {
        if (null == strDatetime || strDatetime.trim().equals("")) {
            return null;
        }
        try {
            return QUERY_SUMMARY_DATE_FORMAT.parse(strDatetime);
        } catch (ParseException e) {
            return null;
        }
    }
}
