package org.souffle.execution;

import org.souffle.spi.QueryException;
import org.souffle.spi.context.ExecutionQueryContext;
import org.souffle.spi.context.StateableQueryContext;
import org.souffle.spi.engine.QueryAnalyzer;
import org.souffle.spi.engine.QueryEngine;
import org.souffle.spi.engine.QueryEngineFactory;
import org.souffle.spi.engine.QueryPlan;
import org.souffle.spi.engine.QueryRunner;

import java.util.concurrent.FutureTask;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午8:51
 * @see
 * @since JDK1.7
 */
public class QueryTask {

    private StateableQueryContext context;

    private QueryAnalyzer queryAnalyzer;

    private QueryRunner queryRunner;

    private QueryExecutionFutureTask executionFutureTask;

    private boolean isSkipAnalysis = false;

    public QueryTask(StateableQueryContext context) {
        this.context = context;
        QueryEngineFactory factory = QueryEngineFactory.getInstance();
        QueryEngine engine = factory.getQueryEngine(context.getQueryMetadata().getEngineName());
        this.queryAnalyzer = engine.getQueryAnalyzer();
        this.queryRunner = engine.getQueryRunner();
        this.executionFutureTask = new QueryExecutionFutureTask(this);
    }

    public void skipAnalysis() {
        this.isSkipAnalysis = true;
    }

    public boolean cancel() {
        return this.executionFutureTask.cancel(true);
    }

    public double progress() {
        double nowProgress = this.queryAnalyzer.progress() * 0.3 + this.queryRunner.progress() * 0.7;
        // when progress more than 1.0, return 1.0
        if (nowProgress > 1) {
            return 1.0d;
        }
        return nowProgress;
    }

    QueryExecutionFutureTask getExecutionFutureTask() {
        return this.executionFutureTask;
    }

    static class QueryExecutionFutureTask extends FutureTask<Boolean> {

        private final QueryTask task;

        private QueryExecutionFutureTask(final QueryTask task) {
            super(() -> {
                if (!task.isSkipAnalysis) {
                    task.context.analysis();
                    QueryPlan plan = task.queryAnalyzer.analyseQuery(task.context);
                    task.context.getQueryMetric().getAnalysisTime().end();
                    if (plan.canQuery()) {
                        task.context.running();
                        ExecutionQueryContext eqc = new ExecutionQueryContext(task.context, plan);
                        QueryMonitor.get().registerExecution(eqc);
                        task.queryRunner.runQuery(eqc);
                        task.context.getQueryMetric().getExecutionTime().end();
                    } else {
                        throw new QueryException("Query plan not pass.");
                    }
                } else {
                    task.context.running();
                    ExecutionQueryContext eqc = new ExecutionQueryContext(task.context, () -> true);
                    QueryMonitor.get().registerExecution(eqc);
                    task.queryRunner.runQuery(eqc);
                    task.context.getQueryMetric().getExecutionTime().end();
                }
                return true;
            });
            this.task = task;
        }

        @Override
        protected void set(Boolean v) {
            super.set(v);
            task.context.finished();
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
            task.context.failed(t);
        }

        void reject() {
            task.context.rejected();
        }

        void onQueue() {
            task.context.queued();
        }
    }
}
