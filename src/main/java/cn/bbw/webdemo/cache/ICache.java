package cn.bbw.webdemo.cache;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/9/1 8:34 下午
 * @since 1.0
 */
public interface ICache<K, V> {

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return {@linkplain V}
     */
    V get(K key) throws ExecutionException;

    /**
     * 获取缓存值
     *
     * @param key 键
     * @return {@linkplain V}
     */
    V getCacheOnly(K key);

    /**
     * 设置缓存
     *
     * @param key 键
     * @param val 值
     * @return 是否成功加入缓存
     */
    boolean put(K key, V val);

    /**
     * 重新设置缓存,先删除(包括清除绑定在身上的其他缓存),再重新加载
     *
     * @param key 键
     * @return {@linkplain V}
     */
    V reload(K key);

    /**
     * 重新设置缓存
     *
     * @param key 键
     * @param val 值
     * @return 是否替换成功
     */
    boolean replace(K key, V val);

    /**
     * 删除缓存
     *
     * @param key 键
     */
    void remove(K key);

    /**
     * 缓存中是否有 key值
     *
     * @param key 键
     * @return 是否存在
     */
    boolean exist(K key);

    /**
     * 获取所有key
     *
     * @return 键列表
     */
    Collection<K> keys();

    /**
     * 所有的值
     *
     * @return 值列表
     */
    Collection<V> values();

    /**
     * 刷新key、value,过期的清除掉
     */
    void cleanUp();

    /**
     * 清除所有
     */

    void clear();

    /**
     * 长度
     *
     * @return 缓存大小
     */
    int size();
}
