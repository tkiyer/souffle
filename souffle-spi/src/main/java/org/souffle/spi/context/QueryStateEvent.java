package org.souffle.spi.context;

import org.souffle.metadata.QueryState;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 下午4:52
 * @see
 * @since JDK1.7
 */
public class QueryStateEvent extends QueryEvent {

    private final QueryState oldQueryState;

    private final QueryState newQueryState;

    public QueryStateEvent(QueryContext queryContext, QueryState oldQueryState, QueryState newQueryState) {
        super(queryContext);
        this.oldQueryState = oldQueryState;
        this.newQueryState = newQueryState;
    }

    public QueryState getOldQueryState() {
        return oldQueryState;
    }

    public QueryState getNewQueryState() {
        return newQueryState;
    }
}
