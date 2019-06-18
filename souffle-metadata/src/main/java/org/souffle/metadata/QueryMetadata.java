package org.souffle.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/8 下午7:14
 * @see
 * @since JDK1.8
 */
public class QueryMetadata implements Serializable {

    private String queryString;

    private String engineName;

    private String groupId;

    private String clientId;

    private boolean cachedResult;

    private Map<String, Object> queryConfig = new HashMap<>();

    private String callbackUrl;

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isCachedResult() {
        return cachedResult;
    }

    public void setCachedResult(boolean cachedResult) {
        this.cachedResult = cachedResult;
    }

    public Map<String, Object> getQueryConfig() {
        return queryConfig;
    }

    public void setQueryConfig(Map<String, Object> queryConfig) {
        this.queryConfig = queryConfig;
    }

    public void addQueryConfig(String key, Object val) {
        this.queryConfig.put(key, val);
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }
}
