package org.souffle.spi.engine;

import org.souffle.spi.context.QueryContext;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:07
 * @see
 * @since JDK1.7
 */
public interface QueryAnalyzer {

    QueryPlan analyseQuery(QueryContext context);

    double progress();
}
