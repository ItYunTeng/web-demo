package cn.bbw.webdemo.cache;

import com.google.common.cache.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/9/1 8:35 下午
 * @since 1.0
 */
@Slf4j
public class MemCache<K, V> extends CacheLoader<K, Optional<V>> implements ICache<K, V>, RemovalListener<K, Optional<V>> {

    private final LoadingCache<K, Optional<V>> loadingCache;
    private final BaseCache<K, V> baseCache;

    public MemCache(BaseCache<K, V> baseCache) {
        this.baseCache = baseCache;
        if (baseCache.isExpireAfterAccess()) {
            // expireAfterAccess是指定项在一定时间内没有读写，会移除该key，下次取的时候从loading中取
            loadingCache = CacheBuilder.newBuilder().maximumSize(baseCache.getMaxSize())
                    .expireAfterAccess(baseCache.getDuration(), TimeUnit.SECONDS)
                    .removalListener(this)
                    .build(this);
        } else {
            // expireAfterWrite是在指定项在一定时间内没有创建/覆盖时，会移除该key，下次取的时候从loading中取
            loadingCache = CacheBuilder.newBuilder().maximumSize(baseCache.getMaxSize())
                    .expireAfterWrite(baseCache.getDuration(), TimeUnit.SECONDS)
                    .removalListener(this)
                    .build(this);
        }
    }

    public void onRemoval(@Nullable K key, @Nullable Optional<V> value, @NonNull RemovalCause cause) {
        baseCache.getChildren().forEach(child -> {
            try {
                child.remove(key);
            } catch (Exception e) {
                log.error("[MemCache] onRemoval error, key:" + key + ", value:" + value, e);
            }
        });
    }

    @Override
    public Optional<V> load(@NonNull K key) {
        try {
            V v = baseCache.without(key);
            return Optional.ofNullable(v);
        } catch (Exception e) {
            log.error("[MemCache] load error, key:" + key, e);
        }
        return Optional.empty();
    }

    @Override
    public V get(final K key) {
        Optional<V> v = Optional.empty();
        try {
            v = loadingCache.get(key);
            return v.orElse(null);
        } catch (ExecutionException e) {
            log.error("[MemCache] load error, key:" + key, e);
        }
        return v.orElse(null);
    }

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return T 值
     */
    @Override
    public V getCacheOnly(K key) {
        return Objects.requireNonNull(loadingCache.getIfPresent(key)).orElse(null);
    }

    /**
     * 设置缓存
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public boolean put(K key, V value) {
        try {
            loadingCache.put(key, Optional.of(value));
            return true;
        } catch (Exception e) {
            log.error("[MemCache] put error, key:" + key + ", value:" + value, e);
        }
        return false;
    }

    /**
     * 重新设置缓存,先删除(包括清除绑定在身上的其他缓存),再重新加载
     *
     * @param key 键
     * @return 缓存内容
     */
    @Override
    public V reload(K key) {
        remove(key);
        Optional<V> v = load(key);
        loadingCache.put(key, v);
        return v.orElse(null);
    }

    /**
     * 重新设置缓存
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public boolean replace(K key, V value) {
        remove(key);
        return put(key, value);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     */
    @Override
    public void remove(K key) {
        loadingCache.invalidate(key);
    }

    /**
     * 缓存中是否有 key值
     *
     * @param key 键
     * @return 是否存在key
     */
    @Override
    public boolean exist(K key) {
        return getCacheOnly(key) != null;
    }

    /**
     * 获取所有key
     *
     * @return key集合
     */
    @Override
    public Collection<K> keys() {
        return loadingCache.asMap().keySet();
    }

    @Override
    public Collection<V> values() {
        Collection<Optional<V>> values = loadingCache.asMap().values();
        return !values.isEmpty() ? values.stream().filter(v -> v != null && v.isPresent()).map(Optional::get).collect(Collectors.toCollection(CopyOnWriteArrayList::new)) : null;
    }

    @Override
    public void cleanUp() {
        loadingCache.cleanUp();
    }

    @Override
    public void clear() {
        loadingCache.invalidateAll();
    }

    @Override
    public int size() {
        return (int) loadingCache.size();
    }

    @Override
    public void onRemoval(RemovalNotification<K, Optional<V>> notification) {
        onRemoval(notification.getKey(), notification.getValue(), notification.getCause());
    }
}
