package org.souffle.spi.context;

import java.io.Serializable;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 下午4:52
 * @see
 * @since JDK1.7
 */
public class QueryEvent implements Serializable {

    private final QueryContext queryContext;

    public QueryEvent(QueryContext queryContext) {
        this.queryContext = queryContext;
    }

    public QueryContext getQueryContext() {
        return queryContext;
    }

    @Override
    public String toString() {
        return "Event{Metadata=" + queryContext.getQueryMetadata().toString() + ",Query=" + queryContext.getQueryId().toString() + "}";
    }
}
