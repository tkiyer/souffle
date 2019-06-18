package org.souffle.standard.context;


import org.souffle.execution.QueryExecutor;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.context.QueryContext;
import org.souffle.spi.context.QueryContextBuilder;
import org.souffle.spi.support.QueryRepository;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午9:56
 * @see
 * @since JDK1.7
 */
public class StandardQueryContextBuilder extends QueryContextBuilder {

    protected QueryExecutor queryExecutor;

    public StandardQueryContextBuilder preparedQueryExecutor(QueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
        return this;
    }

    public StandardQueryContextBuilder(String applicationId, QueryConfiguration queryConfiguration, QueryRepository queryRepository) {
        super(applicationId, queryConfiguration, queryRepository);
    }

    @Override
    public QueryContext build() {
        StandardQueryContext context = new StandardQueryContext(applicationId, queryConfiguration, queryExecutor, queryMetadata, queryRepository.newQueryRecovery());
        context.getQueryRecovery().link();
        return context;
    }

    @Override
    public QueryContext build(boolean isRestored) {
        if (!isRestored) {
            return build();
        }
        return new RestoredQueryContext(applicationId, queryConfiguration, queryExecutor, queryMetadata, queryRepository.newQueryRecovery(), queryId, queryMetric);
    }
}
