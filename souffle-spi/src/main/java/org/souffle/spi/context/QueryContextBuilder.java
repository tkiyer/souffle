package org.souffle.spi.context;

import org.souffle.metadata.QueryId;
import org.souffle.metadata.QueryMetadata;
import org.souffle.metadata.QueryMetric;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.support.QueryRepository;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:17
 * @see
 * @since JDK1.7
 */
public abstract class QueryContextBuilder {

    protected final String applicationId;

    protected QueryConfiguration queryConfiguration;

    protected QueryMetadata queryMetadata;

    protected QueryId queryId;

    protected QueryMetric queryMetric;

    protected QueryRepository queryRepository;

    protected QueryContextBuilder(String applicationId, QueryConfiguration queryConfiguration, QueryRepository queryRepository) {
        this.applicationId = applicationId;
        this.queryConfiguration = queryConfiguration;
        this.queryRepository = queryRepository;
    }

    public QueryContextBuilder preparedQueryMetadata(QueryMetadata queryMetadata) {
        this.queryMetadata = queryMetadata;
        return this;
    }

    public QueryContextBuilder preparedQueryId(QueryId queryId) {
        this.queryId = queryId;
        return this;
    }

    public QueryContextBuilder preparedQueryMetric(QueryMetric queryMetric) {
        this.queryMetric = queryMetric;
        return this;
    }

    public abstract QueryContext build();

    public abstract QueryContext build(boolean isRestored);
}
