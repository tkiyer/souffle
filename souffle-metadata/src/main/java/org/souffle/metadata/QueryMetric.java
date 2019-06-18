package org.souffle.metadata;

import java.io.Serializable;
import java.util.Date;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午8:01
 * @see
 * @since JDK1.8
 */
public class QueryMetric implements Serializable {

    private DurationTime queuedTime;

    private DurationTime analysisTime;

    private DurationTime executionTime;

    private DurationTime elapsedTime;

    public QueryMetric() {
        this(new DurationTime(), new DurationTime(), new DurationTime(), new DurationTime());
    }

    public QueryMetric(DurationTime queuedTime, DurationTime analysisTime, DurationTime executionTime, DurationTime elapsedTime) {
        this.queuedTime = queuedTime;
        this.analysisTime = analysisTime;
        this.executionTime = executionTime;
        this.elapsedTime = elapsedTime;
    }

    public DurationTime getQueuedTime() {
        return queuedTime;
    }

    public DurationTime getAnalysisTime() {
        return analysisTime;
    }

    public DurationTime getExecutionTime() {
        return executionTime;
    }

    public DurationTime getElapsedTime() {
        return elapsedTime;
    }

    public void endAllDurationTime() {
        this.queuedTime.end();
        this.analysisTime.end();
        this.executionTime.end();
        this.elapsedTime.end();
    }

    public static class DurationTime implements Serializable {
        private Date begin = null;
        private Date end = null;

        public DurationTime() {
            this(null, null);
        }

        public DurationTime(Date begin, Date end) {
            this.begin = begin;
            this.end = end;
        }

        public void begin() {
            if (null == this.begin) {
                this.begin = new Date();
            }
        }

        public void end() {
            if (null == this.end) {
                this.end = new Date();
            }
        }

        public Date getBegin() {
            return begin;
        }

        public Date getEnd() {
            return end;
        }

        public long getDuration() {
            if (null == this.begin) {
                return 0L;
            }
            if (null == this.end) {
                return System.currentTimeMillis() - this.begin.getTime();
            }
            return this.end.getTime() - this.begin.getTime();
        }
    }
}
