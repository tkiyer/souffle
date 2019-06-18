package org.souffle.metadata;

import java.io.Serializable;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午2:47
 * @see
 * @since JDK1.7
 */
public class QueryResultMetadata implements Serializable {
    private String[] rowNames;
    private String[] rowTypes;

    public QueryResultMetadata(String[] rowNames) {
        this.rowNames = rowNames;
    }

    public QueryResultMetadata(String[] rowNames, String[] rowTypes) {
        this.rowNames = rowNames;
        this.rowTypes = rowTypes;
    }

    public String[] getRowNames() {
        return rowNames;
    }

    public void setRowNames(String[] rowNames) {
        this.rowNames = rowNames;
    }

    public String[] getRowTypes() {
        return rowTypes;
    }

    public void setRowTypes(String[] rowTypes) {
        this.rowTypes = rowTypes;
    }
}
