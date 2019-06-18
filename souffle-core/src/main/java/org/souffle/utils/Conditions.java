package org.souffle.utils;

import org.souffle.metadata.QueryState;

import java.util.Arrays;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/9 下午2:55
 * @see
 * @since JDK1.7
 */
public final class Conditions {

    public static void checkQueryStateSet(QueryState nowState, QueryState... checkStates) {
        checkState(checkQueryStateSetResult(nowState, checkStates), String.format("State is %s must in %s.", nowState.toString(), Arrays.toString(checkStates)));
    }

    public static boolean checkQueryStateSetResult(QueryState nowState, QueryState... checkStates) {
        boolean has = false;
        for (QueryState checkState : checkStates) {
            if (checkState == nowState) {
                has = true;
                break;
            }
        }
        return has;
    }

    public static void checkQueryState(QueryState nowState, QueryState checkState) {
        checkState(nowState == checkState, String.format("State is %s must be %s.", nowState.toString(), checkState.toString()));
    }

    public static void checkState(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage);
        }
    }
}
