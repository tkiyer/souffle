package org.souffle.spi.engine;

import org.souffle.spi.QueryException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:08
 * @see
 * @since JDK1.7
 */
public class QueryEngineFactory {

    private final static QueryEngineFactory _INSTANCE = new QueryEngineFactory();

    private static Map<String, QueryEngine> queryEngineMap = new HashMap<>();

    static {
        ServiceLoader<QueryEngine> serviceLoader = ServiceLoader.load(QueryEngine.class);
        for (QueryEngine queryEngine : serviceLoader) {
            if (!queryEngine.isAvailable()) {
                throw new QueryException(String.format("Query engine [%s] is not available.", queryEngine.getEngineName()));
            }
            queryEngineMap.put(queryEngine.getEngineName(), queryEngine);
        }
    }

    private QueryEngineFactory() {}

    public static QueryEngineFactory getInstance() {
        return _INSTANCE;
    }

    public QueryEngine getQueryEngine(String engineName) {
        return queryEngineMap.get(engineName);
    }
}
