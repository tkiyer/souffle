package org.souffle.spi;

import org.souffle.metadata.QueryGroup;

import java.io.Serializable;
import java.util.List;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午5:37
 * @see
 * @since JDK1.7
 */
public class QueryGroupSummary implements Serializable {

    private final String applicationId;

    private final String groupId;

    private final List<QuerySummary> querySummaries;

    public QueryGroupSummary(String applicationId, final QueryGroup queryGroup, List<QuerySummary> querySummaries) {
        this.applicationId = applicationId;
        this.groupId = queryGroup.getId();
        this.querySummaries = querySummaries;
    }

    public List<QuerySummary> getQuerySummaries() {
        return querySummaries;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
