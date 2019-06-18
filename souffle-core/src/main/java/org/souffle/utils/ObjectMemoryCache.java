package org.souffle.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Your class description. <p />
 *
 * @author tuyan
 * @version 1.0.0
 * @date 2018/1/11 下午4:30
 * @see
 * @since JDK1.7
 */
public class ObjectMemoryCache<K, V> {

    private final int limitSize;

    private final LinkedBlockingQueue<K> headQueue;

    private final ConcurrentMap<K, V> objectMap;

    public ObjectMemoryCache(int limitSize) {
        this.limitSize = limitSize;
        this.headQueue = new LinkedBlockingQueue<>(this.limitSize);
        this.objectMap = new ConcurrentHashMap<>(this.limitSize);
    }

    public int getLimitSize() {
        return this.limitSize;
    }

    public boolean isFull() {
        return this.limitSize == this.headQueue.size();
    }

    public void add(K k, V v) {
        // if cache is full, remove first cached object.
        if (!this.headQueue.offer(k)) {
            removeFirst();
        }
        this.headQueue.add(k);
        this.objectMap.put(k, v);
    }

    public V getCachedObject(K k) {
        return this.objectMap.getOrDefault(k, null);
    }

    public boolean containsKey(K k) {
        return this.objectMap.containsKey(k);
    }

    private void removeFirst() {
        // Get and remove first key from head quque.
        K headKey = this.headQueue.remove();
        // remove key from object map.
        this.objectMap.remove(headKey);
    }
}
