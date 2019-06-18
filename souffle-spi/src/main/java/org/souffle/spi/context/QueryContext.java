package org.souffle.spi.context;

import org.souffle.metadata.QueryGroup;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QuerySummary;
import org.souffle.spi.support.QueryRecovery;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:07
 * @see
 * @since JDK1.7
 */
public interface QueryContext {

    String getApplicationId();

    QueryId getQueryId();

    QueryGroup getQueryGroup();

    QueryMetadata getQueryMetadata();

    QueryMetric getQueryMetric();

    QueryRecovery getQueryRecovery();

    QueryConfiguration getQueryConfiguration();

    QuerySummary ofSummary();

    void signal(boolean isSkipAnalysis);

    Throwable getErrorThrowable();

    String getErrorMessage();
}
