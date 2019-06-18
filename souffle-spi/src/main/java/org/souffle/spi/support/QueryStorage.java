package org.souffle.spi.support;

import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.context.QueryContext;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/16 下午7:54
 * @see
 * @since JDK1.7
 */
public class QueryStorage implements Serializable, Comparable<QueryStorage> {

    private String applicationId;

    private QueryId queryId;

    private QueryMetadata queryMetadata;

    private QuerySummary querySummary;

    private String errorThrowable;

    private String errorMessage;

    public QueryStorage(QueryContext queryContext) {
        this.applicationId = queryContext.getApplicationId();
        this.queryId = queryContext.getQueryId();
        this.queryMetadata = queryContext.getQueryMetadata();
        this.querySummary = queryContext.ofSummary();
        if (null != queryContext.getErrorThrowable()) {
            this.errorThrowable = getStackTrace(queryContext.getErrorThrowable());
        }
        this.errorMessage = queryContext.getErrorMessage();
    }

    public QueryMetadata getQueryMetadata() {
        return queryMetadata;
    }

    public void setQueryMetadata(QueryMetadata queryMetadata) {
        this.queryMetadata = queryMetadata;
    }

    public QuerySummary getQuerySummary() {
        return querySummary;
    }

    public void setQuerySummary(QuerySummary querySummary) {
        this.querySummary = querySummary;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public QueryId getQueryId() {
        return queryId;
    }

    public void setQueryId(QueryId queryId) {
        this.queryId = queryId;
    }

    public String getErrorThrowable() {
        return errorThrowable;
    }

    public void setErrorThrowable(String errorThrowable) {
        this.errorThrowable = errorThrowable;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public int compareTo(QueryStorage o) {
        return this.queryId.compareTo(o.getQueryId());
    }

    private String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}
