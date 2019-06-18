package org.souffle.standard.context;

import org.souffle.execution.QueryExecutor;
import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.support.QueryRecovery;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/18 下午4:19
 * @see
 * @since JDK1.7
 */
public class RestoredQueryContext extends StandardQueryContext {

    protected RestoredQueryContext(String applicationId, QueryConfiguration queryConfiguration, QueryExecutor queryExecutor, QueryMetadata queryMetadata,
                                   QueryRecovery queryRecovery, QueryId queryId, QueryMetric queryMetric) {
        super(applicationId, queryConfiguration, queryExecutor, queryMetadata, queryRecovery, queryId, queryMetric);
    }
}
