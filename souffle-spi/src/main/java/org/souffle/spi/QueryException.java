package org.souffle.spi;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 上午11:50
 * @see
 * @since JDK1.7
 */
public class QueryException extends RuntimeException {

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
