package cn.bbw.webdemo.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/9/1 8:57 下午
 * @since 1.0
 */
@Slf4j
public class CacheFactory {
    private static class CacheHolder {
        private static final CacheFactory INSTANCE = new CacheFactory();
    }

    public static CacheFactory getInstance() {
        return CacheHolder.INSTANCE;
    }

    /**
     * 过期时间
     */
    public static final int EXPIRED_SECONDS = 30 * 60;
    /**
     * 缓存最大元素个数
     */
    public static final int MAX_SIZE = 50000;

    private final ReentrantLock lock = new ReentrantLock();

    private final ConcurrentHashMap<String, ICache<?, ?>> cacheMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <K, V> ICache<K, V> getCache(BaseCache<K, V> gameCache) {
        String name = gameCache.getName();
        ICache<K, V> cache = (ICache<K, V>) cacheMap.get(name);
        if (cache == null) {
            lock.lock();
            try {
                cache = (ICache<K, V>) cacheMap.get(name);
                if (cache == null) {
                    cache = new MemCache<>(gameCache);
                    BaseCache<K, ?> parent = gameCache.getParent();
                    if (parent != null) {
                        parent.getChildren().add(cache);
                    }
                    cacheMap.putIfAbsent(name, cache);
                    cache = (ICache<K, V>) cacheMap.get(name);
                }

            } catch (Exception e) {
                log.error("getCache error, cache name:" + name, e);
            } finally {
                lock.unlock();
            }
        }
        return cache;
    }

    public void clear() {
        lock.lock();
        try {
            cacheMap.values().forEach(ICache::clear);
        } catch (Exception e) {
            log.error("clear cache error", e);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <K, V> void clear(String name) {
        ICache<K, V> cache = (ICache<K, V>) cacheMap.get(name);
        if (null != cache) {
            lock.lock();
            try {
                cache.clear();
            } catch (Exception e) {
                log.error("clear cache error", e);
            } finally {
                lock.unlock();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <K, V> ICache<K, V> getCache(String name) {
        return (ICache<K, V>) cacheMap.get(name);
    }

}
