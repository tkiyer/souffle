package org.souffle.spi.result;

import org.souffle.metadata.QueryResultSet;

/**
 * Your class description. <p />
 *
 * @author tkiyer
 * @version 1.0.0
 * @date 2019/6/11 13:57
 * @see
 * @since JDK1.8
 */
public interface QueryResultFetcher {

    QueryResultSet fetch();

    QueryResultSet fetch(int from, int size);
}
