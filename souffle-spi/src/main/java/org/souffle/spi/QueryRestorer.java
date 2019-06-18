package org.souffle.spi;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/18 下午3:56
 * @see
 * @since JDK1.7
 */
public interface QueryRestorer {

    void restoreQueries();

    QueryConfiguration getQueryConfiguration();
}
