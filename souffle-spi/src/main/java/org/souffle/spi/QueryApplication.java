package org.souffle.spi;

import org.souffle.spi.support.QueryRepository;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 上午9:35
 * @see
 * @since JDK1.8
 */
public interface QueryApplication {

    String getApplicationId();

    void start();

    void start(QueryApplicationHook hook);

    void stop();

    void stop(QueryApplicationHook hook);

    QueryManager getQueryManager();

    QueryRepository getQueryRepository();

    interface QueryApplicationHook {
        void doHook();
    }
}
