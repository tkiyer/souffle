package org.souffle.metadata;

import java.io.Serializable;
import java.util.Objects;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午5:35
 * @see
 * @since JDK1.8
 */
public class QueryGroup implements Serializable {

    private final String id;

    public QueryGroup(String id) {
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
        QueryGroup other = (QueryGroup) obj;
        return Objects.equals(this.id, other.id);
    }
}
