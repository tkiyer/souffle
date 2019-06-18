package org.souffle.spi;

import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.metadata.QueryState;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午8:03
 * @see
 * @since JDK1.7
 */
public class QuerySummary implements Serializable {

    public final static String DATETIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

    private final String applicationId;

    private final String queryId;

    private final String groupId;

    private final String state;

    private final String queryEngine;

    private final String startDateTime;

    private final String endDateTime;

    private final long totalDuration;

    private final String queuedStartDateTime;

    private final String queuedEndDateTime;

    private final long queuedDuration;

    private final String analysisStartDateTime;

    private final String analysisEndDateTime;

    private final long analysisDuration;

    private final String executionStartDateTime;

    private final String executionEndDateTime;

    private final long executionDuration;

    private final double progress;

    public QuerySummary(String applicationId, QueryId queryId, QueryMetadata metadata, QueryMetric queryMetric, double progress, QueryState state) {
        this.applicationId = applicationId;

        this.queryId = queryId.toString();
        this.groupId = metadata.getGroupId();
        this.queryEngine = metadata.getEngineName();

        this.startDateTime = formatDateTime(queryMetric.getElapsedTime().getBegin());
        this.endDateTime = formatDateTime(queryMetric.getElapsedTime().getEnd());
        this.totalDuration = queryMetric.getElapsedTime().getDuration();

        this.queuedStartDateTime = formatDateTime(queryMetric.getQueuedTime().getBegin());
        this.queuedEndDateTime = formatDateTime(queryMetric.getQueuedTime().getEnd());
        this.queuedDuration = queryMetric.getQueuedTime().getDuration();

        this.analysisStartDateTime = formatDateTime(queryMetric.getAnalysisTime().getBegin());
        this.analysisEndDateTime = formatDateTime(queryMetric.getAnalysisTime().getEnd());
        this.analysisDuration = queryMetric.getAnalysisTime().getDuration();

        this.executionStartDateTime = formatDateTime(queryMetric.getExecutionTime().getBegin());
        this.executionEndDateTime = formatDateTime(queryMetric.getExecutionTime().getEnd());
        this.executionDuration = queryMetric.getExecutionTime().getDuration();

        this.progress = progress;

        this.state = state.name();
    }

    public String getQueryId() {
        return queryId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getState() {
        return state;
    }

    public String getQueryEngine() {
        return queryEngine;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public String getQueuedStartDateTime() {
        return queuedStartDateTime;
    }

    public String getQueuedEndDateTime() {
        return queuedEndDateTime;
    }

    public long getQueuedDuration() {
        return queuedDuration;
    }

    public String getAnalysisStartDateTime() {
        return analysisStartDateTime;
    }

    public String getAnalysisEndDateTime() {
        return analysisEndDateTime;
    }

    public long getAnalysisDuration() {
        return analysisDuration;
    }

    public String getExecutionStartDateTime() {
        return executionStartDateTime;
    }

    public String getExecutionEndDateTime() {
        return executionEndDateTime;
    }

    public long getExecutionDuration() {
        return executionDuration;
    }

    public double getProgress() {
        return progress;
    }

    private String formatDateTime(Date d) {
        if (null == d) {
            return "";
        }
        return new SimpleDateFormat(DATETIME_FORMAT_PATTERN).format(d);
    }

    public String getApplicationId() {
        return applicationId;
    }
}
