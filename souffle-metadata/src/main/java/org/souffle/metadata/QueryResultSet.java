package org.souffle.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午2:44
 * @see
 * @since JDK1.7
 */
public class QueryResultSet implements Serializable {

    private QueryId queryId;

    private QueryMetadata queryMetadata;

    private QueryResultMetadata resultMetadata;

    private List<String[]> rows;

    private long elapsedTime;

    private boolean isLastData;

    private int from;

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isLastData() {
        return isLastData;
    }

    public void setLastData(boolean lastData) {
        isLastData = lastData;
    }

    private int size;

    public QueryResultSet() {
        this.rows = new ArrayList<>();
        this.from = 0;
        this.size = 0;
    }

    public QueryResultSet(List<String[]> rows) {
        this.rows = rows;
    }

    public void addRow(String[] row) {
        this.rows.add(row);
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public QueryMetadata getQueryMetadata() {
        return queryMetadata;
    }

    public void setQueryMetadata(QueryMetadata queryMetadata) {
        this.queryMetadata = queryMetadata;
    }

    public QueryResultMetadata getResultMetadata() {
        return resultMetadata;
    }

    public void setResultMetadata(QueryResultMetadata resultMetadata) {
        this.resultMetadata = resultMetadata;
    }

    public List<String[]> getRows() {
        return rows;
    }

    public void setRows(List<String[]> rows) {
        this.rows = rows;
    }

    public QueryId getQueryId() {
        return queryId;
    }

    public void setQueryId(QueryId queryId) {
        this.queryId = queryId;
    }
}
