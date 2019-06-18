package org.souffle.spi.context;

import org.souffle.metadata.QueryState;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 上午12:26
 * @see
 * @since JDK1.7
 */
public interface StateableQueryContext extends QueryContext {

    QueryState getQueryState();

    void accepted();

    void analysis();

    void queued();

    void running();

    void finished();

    void failed(Throwable t);

    void canceled();

    void rejected();

    void tryTimeout();

    void addQueryStateChangeListener(QueryStateChangeListener listener);
}
