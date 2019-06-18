package org.souffle.spi.context;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 下午4:54
 * @see
 * @since JDK1.7
 */
public interface QueryStateChangeListener {

    void stateChanged(QueryStateEvent event);
}
