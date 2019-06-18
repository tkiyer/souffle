package org.souffle.spi;

import java.io.Serializable;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午9:32
 * @see
 * @since JDK1.7
 */
public interface QueryConfigurable extends Serializable {

    String QUERY_DEFAULT_ENGINE_NAME = "query.default.engine.name";

    String QUERY_PROJECT_PATH = "query.project.path";

    String QUERY_EXECUTE_TIMEOUT_MILLISECONDS = "query.execute.timeout.milliseconds";

    String QUERY_EXECUTE_THREAD_CORE_SIZE = "query.execute.thread.core.size";

    String QUERY_EXECUTE_THREAD_MAX_SIZE = "query.execute.thread.max.size";

    String QUERY_QUEUE_MAX_SIZE = "query.queue.max.size";

    String QUERY_REPOSITORY_CLASSNAME = "query.repository.classname";

    String QUERY_RECOVERY_CACHE_HOST = "query.cache.redis.host";

    String QUERY_CACHE_REDIS_PORT = "query.cache.redis.port";

    String QUERY_CACHE_REDIS_MAX_TOTAL = "query.cache.redis.max.total";

    String QUERY_CACHE_REDIS_MAX_IDLE = "query.cache.redis.max.idle";

    String QUERY_CACHE_REDIS_MAX_WAIT_MILLIS = "query.cache.redis.max.wait.millis";

    String QUERY_CACHE_RECOVERY_EXPIRE_SECONDS = "query.cache.recovery.expire.seconds";

    String QUERY_ENGINE_HIVE_ANALYSIS_MAX_DATASIZE = "query.engine.hive.analysis.max.datasize";

    String QUERY_ENGINE_HIVE_ANALYSIS_MAX_ROWS = "query.engine.hive.analysis.max.rows";

    String QUERY_ENGINE_HIVE_ANALYSIS_MAX_JOBS = "query.engine.hive.analysis.max.jobs";

    String QUERY_ENGINE_HIVE_GLOBAL_UDF_FILE = "query.engine.hive.global.udf.file";

    String QUERY_ENGINE_HIVE_JOB_QUEUENAME = "query.engine.hive.job.queuename";

    String QUERY_RECOVERY_CLASSNAME = "query.recovery.classname";

    String QUERY_RECOVERY_RSYNC_INTERVAL_MILLIS = "query.recovery.rsync.millis";

    String QUERY_RECOVERY_BOOTSTRAP_ENABLE = "query.recovery.bootstrap.enable";

    String QUERY_RECOVERY_JDBC_DRIVER = "query.recovery.jdbc.driver";

    String QUERY_RECOVERY_JDBC_URL = "query.recovery.jdbc.url";

    String QUERY_RECOVERY_JDBC_USERNAME = "query.recovery.jdbc.username";

    String QUERY_RECOVERY_JDBC_PASSWORD = "query.recovery.jdbc.password";

    String QUERY_RECOVERY_JDBC_POOL_INITIAL_SIZE = "query.recovery.jdbc.pool.initialSize";

    String QUERY_RECOVERY_JDBC_POOL_MIN_IDLE = "query.recovery.jdbc.pool.minIdle";

    String QUERY_RECOVERY_JDBC_POOL_MAX_ACTIVE = "query.recovery.jdbc.pool.maxActive";

    String QUERY_RECOVERY_JDBC_POOL_MAX_WAIT = "query.recovery.jdbc.pool.maxWait";

    String QUERY_RESULT_HDFS_URI = "query.result.hdfs.uri";

    String QUERY_RESULT_HDFS_DIR = "query.result.hdfs.dir";

    String QUERY_RESULT_HDFS_LINEGAP = "query.result.hdfs.linegap";
}
