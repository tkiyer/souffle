package org.souffle.spi.engine;

import org.souffle.spi.context.ExecutionQueryContext;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:06
 * @see
 * @since JDK1.7
 */
public interface QueryRunner {

    void runQuery(ExecutionQueryContext context);

    double progress();
}
