package org.souffle.metadata;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 查询状态枚举，在查询过程中的一些状态变更。<p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午11:56
 * @see
 * @since JDK1.8
 */
public enum QueryState {

    /**
     * 初始化状态.
     * 当查询刚刚被创建的时候的状态.
     */
    DEFINE(false),
    /**
     * 查询已经被接受，表示查询已经准备进入执行引擎开始执行了.
     */
    ACCEPTED(false),
    /**
     * 查询分析状态.
     */
    ANALYSIS(false),
    /**
     * 查询等待状态，表示当前同时查询的数量过多，此查询需要在查询队列中等待空闲资源.
     */
    QUEUED(false),
    /**
     * 查询正在执行.
     */
    RUNNING(false),
    /**
     * 查询完成状态，表示查询已经执行结束并且成功.
     */
    FINISHED(true),
    /**
     * 查询失败状态，表示查询已经执行结束并且失败了.在执行过程中有异常信息.
     */
    FAILED(true),
    /**
     * 查询取消状态，表示查询在执行过程中被手动取消或者由于查询超时导致系统自动取消查询.
     */
    CANCELED(true),
    /**
     * 查询被拒绝状态，表示当前服务中执行的查询过多，查询队列已经满了，此时会拒绝接收查询.
     */
    REJECTED(true)
    ;
    private boolean done;

    /**
     * 表示查询结束状态的集合.
     */
    public static final Set<QueryState> TERMINAL_QUERY_STATES = Stream.of(QueryState.values()).filter(QueryState::isDone).collect(Collectors.toSet());

    /**
     * 表示查询未结束状态的集合.
     */
    public static final Set<QueryState> NOT_TERMINAL_QUERY_STATES = Stream.of(QueryState.values()).filter(queryState -> !queryState.isDone()).collect(Collectors.toSet());

    QueryState(boolean done) {
        this.done = done;
    }

    /**
     * 状态是否为结束状态.
     *
     * @return  如果是则返回True.
     */
    public boolean isDone() {
        return this.done;
    }
}
