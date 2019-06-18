package org.souffle.spi.support;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/16 下午6:34
 * @see
 * @since JDK1.7
 */
public interface QueryRecovery {

    String RSYNC_KEY_SPLIT = "::";

    void link();

    boolean rsync(boolean isCheckIntervalTime);

    boolean rsync();

    boolean wipe();
}
