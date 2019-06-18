package org.souffle.spi;

import org.souffle.metadata.QueryId;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/10 上午11:43
 * @see
 * @since JDK1.7
 */
public class QueryIdGenerator {

    // a-z, 0-9, except: l, o, 0, 1
    private static final char[] BASE_32 = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("YYYYMMdd_HHmmss");

    private static final long BASE_SYSTEM_TIME_MILLIS = System.currentTimeMillis();
    private static final long BASE_NANO_TIME = System.nanoTime();

    private final String coordinatorId;

    private long lastTimeInDays;
    private long lastTimeInSeconds;
    private String lastTimestamp;
    private int counter;

    private static final Map<Long, QueryIdGenerator> multiInstanceMap = new HashMap<>();
    private static final int MULTI_INSTANCE_SIZE = 16;

    static {
        for (long i = 0; i < MULTI_INSTANCE_SIZE; i++) {
            multiInstanceMap.put(i, new QueryIdGenerator());
        }
    }

    public static QueryIdGenerator get() {
        long position = System.nanoTime() % MULTI_INSTANCE_SIZE;
        return multiInstanceMap.get(position);
    }

    private QueryIdGenerator() {
        StringBuilder coordinatorId = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            coordinatorId.append(BASE_32[ThreadLocalRandom.current().nextInt(32)]);
        }
        this.coordinatorId = coordinatorId.toString();
    }

    /**
     * Generate next queryId using the following format:
     * <tt>YYYYMMdd_HHmmss_index_coordId</tt>
     * <p/>
     * Index rolls at the start of every day or when it reaches 99,999,999, and the
     * coordId is a randomly generated when this instance is created.
     */
    public synchronized QueryId createNextQueryId() {
        // only generate 100,000,000 ids per day
        if (counter > 99_999_999) {
            // wait for the second to rollover
            while (MILLISECONDS.toSeconds(nowInMillis()) == lastTimeInSeconds) {
                sleepUninterruptedly();
            }
            counter = 0;
        }

        // if it has been a second since the last id was generated, generate a new timestamp
        long now = nowInMillis();
        if (MILLISECONDS.toSeconds(now) != lastTimeInSeconds) {
            // generate new timestamp
            lastTimeInSeconds = MILLISECONDS.toSeconds(now);
            lastTimestamp = TIMESTAMP_FORMAT.format(now);

            // if the day has rolled over, restart the counter
            if (MILLISECONDS.toDays(now) != lastTimeInDays) {
                lastTimeInDays = MILLISECONDS.toDays(now);
                counter = 0;
            }
        }

        return new QueryId(String.format("QUERY_%s_%05d_%s", lastTimestamp, counter++, coordinatorId));
    }

    private void sleepUninterruptedly() {

        boolean interrupted = false;
        try {
            long remainingNanos = TimeUnit.SECONDS.toNanos(1);
            long end = System.nanoTime() + remainingNanos;
            while (true) {
                try {
                    // TimeUnit.sleep() treats negative timeouts just like zero.
                    NANOSECONDS.sleep(remainingNanos);
                    return;
                } catch (InterruptedException e) {
                    interrupted = true;
                    remainingNanos = end - System.nanoTime();
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private long nowInMillis() {
        // avoid problems with the clock moving backwards
        return BASE_SYSTEM_TIME_MILLIS + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - BASE_NANO_TIME);
    }
}
