package org.souffle.metadata;

import java.io.Serializable;
import java.util.Objects;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:02
 * @see
 * @since JDK1.8
 */
public class QueryId implements Serializable, Comparable<QueryId> {

    public static QueryId of(String queryId) {
        return new QueryId(queryId);
    }

    private final String id;

    public QueryId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        QueryId other = (QueryId) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int compareTo(QueryId o) {
        String[] arr1 = this.id.split("_");
        String[] arr2 = o.getId().split("_");
        int t1 = Integer.parseInt(arr1[1]);
        int t2 = Integer.parseInt(arr2[1]);
        if (t1 == t2) {
            int c1 = Integer.parseInt(arr1[2]);
            int c2 = Integer.parseInt(arr2[2]);
            if (c1 == c2) {
                return arr1[3].compareTo(arr2[3]);
            } else {
                return c2 - c1;
            }
        } else {
            return t2 - t1;
        }
    }
}
