package org.souffle.standard;

import org.souffle.execution.QueryMonitor;
import org.souffle.spi.QueryApplication;
import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.QueryManager;
import org.souffle.spi.QueryRestorer;
import org.souffle.spi.support.QueryRepository;
import org.souffle.utils.ClassUtils;

import static org.souffle.spi.QueryConfigurable.QUERY_RECOVERY_BOOTSTRAP_ENABLE;
import static org.souffle.spi.QueryConfigurable.QUERY_REPOSITORY_CLASSNAME;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 上午9:35
 * @see
 * @since JDK1.7
 */
public class StandardQueryApplication implements QueryApplication {

    private final static String DEFAULT_QUERY_REPOSITORY_CLASSNAME = "org.souffle.core.support.StandardQueryRepository";

    private QueryConfiguration queryConfiguration;

    private QueryManager queryManager;

    private QueryRestorer queryRestorer;

    private QueryRepository queryRepository;

    public StandardQueryApplication() {
    }

    @Override
    public String getApplicationId() {
        return QueryConfiguration.APPLICATION_ID;
    }

    @Override
    public void start() {
        start(() -> {
            // DO NOTHING...
        });
    }

    @Override
    public void start(QueryApplicationHook hook) {
        loadQueryConfiguration();
        createQueryRepository();
        createQueryManager();
        createQueryRestorer();
        restoreQueries();
        QueryMonitor.get().start();
        hook.doHook();
    }

    @Override
    public void stop() {
        stop(() -> {
            // DO NOTHING...
        });
    }

    @Override
    public void stop(QueryApplicationHook hook) {
        QueryMonitor.get().stop();
        hook.doHook();
    }

    @Override
    public QueryManager getQueryManager() {
        return queryManager;
    }

    @Override
    public QueryRepository getQueryRepository() {
        return this.queryRepository;
    }

    private void loadQueryConfiguration() {
        this.queryConfiguration = QueryConfiguration.load();
    }

    private void createQueryRepository() {
        String className = queryConfiguration.get(QUERY_REPOSITORY_CLASSNAME, DEFAULT_QUERY_REPOSITORY_CLASSNAME);
        this.queryRepository = ClassUtils.newInstance(className, QueryRepository.class,
                new Class[]{ String.class, QueryConfiguration.class },
                new Object[]{ getApplicationId(), this.queryConfiguration }
                );
    }

    private void createQueryManager() {
        this.queryManager = new StandardQueryManager(getApplicationId(), this.queryConfiguration, this.queryRepository);
    }

    private void createQueryRestorer() {
        this.queryRestorer = new StandardQueryRestorer(this.queryConfiguration, this.queryRepository, (StandardQueryManager) this.queryManager);
    }

    private void restoreQueries() {
        boolean isBootstrapEnable = queryConfiguration.getBoolean(QUERY_RECOVERY_BOOTSTRAP_ENABLE, true);
        if (isBootstrapEnable) {
            this.queryRestorer.restoreQueries();
        }
    }

}
