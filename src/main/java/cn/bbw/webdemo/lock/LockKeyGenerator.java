package cn.youai.commons.lock;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * LockUtils
 *
 */
public class LockKeyGenerator {
    private static final int MAX_KEY_SIZE = 100000;
    private static final String SEPARATOR = "-";

    /**
     * key 缓存
     */
    private static final LoadingCache<String, String> KEY_CACHE = Caffeine.newBuilder()
            .maximumSize(MAX_KEY_SIZE)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(LockKeyGenerator::loadKey);

    private static String loadKey(String key) {
        return key;
    }

    public static String keyOf(String... objs) {
        return KEY_CACHE.get(String.join(SEPARATOR, objs));
    }

    public static String keyOf(Object... objs) {
        return KEY_CACHE.get(genKey(objs));
    }

    public static String keyOf(Class<?> clazz, Object... objs) {
        return KEY_CACHE.get(clazz.getSimpleName() + SEPARATOR + genKey(objs));
    }

    public static String genKey(Object... objs) {
        StringBuilder lockKey = new StringBuilder();
        if (null != objs && objs.length > 0) {
            for (Object obj : objs) {
                if (null != obj) {
                    lockKey.append(SEPARATOR).append(obj);
                }
            }
        }
        return lockKey.toString();
    }
}
