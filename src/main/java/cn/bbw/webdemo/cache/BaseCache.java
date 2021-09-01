package cn.bbw.webdemo.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/9/1 8:33 下午
 * @since 1.0
 */
@Slf4j
@Getter
public abstract class BaseCache<K, T> {

    private final String name;

    @Setter
    private BaseCache<K, ?> parent;

    /**
     * 内存可存放最大元素个数
     */
    private final int maxSize;

    /**
     * 缓存更新周期, 单位秒
     */
    private final int duration;

    private final List<ICache<K, ?>> children = new ArrayList<>();

    @Setter
    private boolean expireAfterAccess = true;


    public BaseCache() {
        this(null, null);
    }

    public BaseCache(BaseCache<K, ?> parent) {
        this(null, parent, CacheFactory.MAX_SIZE, CacheFactory.EXPIRED_SECONDS);
    }

    public BaseCache(String name, BaseCache<K, ?> parent, int duration) {
        this(name, parent, CacheFactory.MAX_SIZE, duration);
    }

    public BaseCache(String name, BaseCache<K, ?> parent) {
        this(name, parent, CacheFactory.MAX_SIZE, CacheFactory.EXPIRED_SECONDS);
    }

    public BaseCache(String name, BaseCache<K, ?> parent, int maxSize, int duration) {
        if (name == null) {
            name = this.getClass().getSimpleName();
        }
        this.name = name;
        this.parent = parent;
        this.maxSize = maxSize;
        this.duration = duration;
    }


    /**
     * 从缓存读取不到时会调用从接口，一般从数据库中重新读取
     *
     * @param key 缓存的key
     * @return {@linkplain T}
     */
    public abstract T without(K key);

    /**
     * 增加校验缓存合法性, 例如跨天的处理
     *
     * @param t 缓存中的数据
     * @return 过期返回 true
     */
    public boolean isExpired(T t) {
        return false;
    }

    protected ICache<K, T> getCache() {
        return CacheFactory.getInstance().getCache(this);
    }

    public T get(K k) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            T t = null;
            try {
                t = cache.get(k);
            } catch (ExecutionException e) {
                log.error("[BaseCache] load error, key:" + k, e);
            }
            if (t != null && isExpired(t)) {
                return reload(k);
            }
            return t;
        }
        return null;
    }

    public T getCacheOnly(K k) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.getCacheOnly(k);
        }
        return null;
    }

    public boolean put(K k, T val) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.put(k, val);
        }
        return false;
    }

    public T reload(K k) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.reload(k);
        }
        return null;
    }

    public boolean replace(K k, T val) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.replace(k, val);
        }
        return false;
    }

    public void remove(K k) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            cache.remove(k);
        }
    }

    public boolean exist(K k) {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.exist(k);
        }
        return false;
    }

    public Collection<K> keys() {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.keys();
        }
        return null;
    }

    public Collection<T> values() {
        ICache<K, T> cache = getCache();
        return null != cache ? cache.values() : null;
    }

    public void evictExpiredElements() {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            cache.cleanUp();
        }
    }

    public void clear() {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            cache.clear();
        }
    }

    public int size() {
        ICache<K, T> cache = getCache();
        if (cache != null) {
            return cache.size();
        }
        return 0;
    }
}
