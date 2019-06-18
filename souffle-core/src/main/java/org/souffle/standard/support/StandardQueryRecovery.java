package org.souffle.standard.support;

import org.souffle.spi.QueryConfiguration;
import org.souffle.spi.support.QueryRecovery;

import java.util.concurrent.locks.ReentrantLock;

import static org.souffle.spi.QueryConfigurable.QUERY_RECOVERY_RSYNC_INTERVAL_MILLIS;

/**
 * Your class description. <p />
 *
 * @author tkiyer
 * @version 1.0.0
 * @date 2019/6/4 20:38
 * @see
 * @since JDK1.8
 */
public class StandardQueryRecovery implements QueryRecovery {

    private static final long DEFAULT_RSYNC_INTERVAL_MILLIS = 1000L;

    protected final String applicationId;

    private final ReentrantLock rsyncLock = new ReentrantLock();

    private long previousRsyncTimeMillis = System.currentTimeMillis();

    private long rsyncIntervalMillis;

    protected StandardQueryRecovery(String applicationId, QueryConfiguration queryConfiguration) {
        this.applicationId = applicationId;
        this.rsyncIntervalMillis = queryConfiguration.getLong(QUERY_RECOVERY_RSYNC_INTERVAL_MILLIS, DEFAULT_RSYNC_INTERVAL_MILLIS);
    }

    @Override
    public void link() {
        // DO NOTHING.
    }

    @Override
    public boolean rsync(boolean isCheckIntervalTime) {
        if (isCheckIntervalTime) {
            final ReentrantLock rlock = this.rsyncLock;
            rlock.lock();
            try {
                long now = System.currentTimeMillis();
                if (now - previousRsyncTimeMillis > this.rsyncIntervalMillis) {
                    // now rsync
                    boolean isRsync = doRsync();
                    //set now is rsync
                    this.previousRsyncTimeMillis = now;
                    // return it.
                    return isRsync;
                } else {
                    // do not rsync
                    return false;
                }
            } finally {
                rlock.unlock();
            }
        } else {
            return doRsync();
        }
    }

    @Override
    public boolean rsync() {
        return true;
    }

    @Override
    public boolean wipe() {
        return true;
    }

    protected boolean doRsync() {
        return true;
    }
}
