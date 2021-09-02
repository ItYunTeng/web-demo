package cn.bbw.webdemo.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author buliangliang
 * @version V1.0.0
 * @className ObjectLockHolder
 * @date 2021/9/2 7:53 上午
 * @since 1.0
 */
public class ObjectLockHolder {
    /**
     * 所有类的对象锁持有者缓存
     */
    private final LoadingCache<Class, Holder> holders = Caffeine.newBuilder().maximumSize(1000).build(Holder::new);

    /**
     * 类的对象锁持有者
     *
     * @author zhujuan
     */
    public class Holder {
        /**
         * 对象锁持有者的类型
         */
        @SuppressWarnings("unused")
        private final Class clazz;
        /**
         * 加时锁，用于当对象的hash值(或实现自Entity接口的getIdentity())一样时保证锁的获取顺序
         */
        private final Lock tieLock = new ReentrantLock();
        /**
         * 实例的对象锁缓存
         */
        private final LoadingCache<Object, ObjectLock> locks = Caffeine.newBuilder().weakKeys().build(ObjectLock::new);

        public Holder(Class clazz) {
            this.clazz = clazz;
        }

        /**
         * 获取对象锁
         *
         * @param object
         * @return
         */
        public ObjectLock getLock(Object object) {
            return locks.get(object);
        }

        /**
         * 获取加时锁
         *
         * @return
         */
        public Lock getTieLock() {
            return tieLock;
        }

        /**
         * 获取锁的数量
         *
         * @return
         */
        public long count() {
            return locks.estimatedSize();
        }
    }

    /**
     * 获取类的对象锁持有者
     *
     * @param clz
     * @return
     */
    private Holder getHolder(Class clz) {
        return holders.get(clz);
    }

    /**
     * 获取对象实例的对象锁
     *
     * @param object
     * @return
     */
    public ObjectLock getLock(Object object) {
        if (object == null) {
            return null;
        }
        return getHolder(object.getClass()).getLock(object);
    }

    /**
     * 获取类的对象锁持有者的加时锁(tie-breaker)
     *
     * @param clz
     * @return
     */
    public Lock getTieLock(Class clz) {
        return getHolder(clz).getTieLock();
    }

    /**
     * 获取类的对象锁持有者的锁的数量
     *
     * @param clz
     * @return
     */
    public long count(Class clz) {
        Holder holder = holders.getIfPresent(clz);
        if (holder != null) {
            return holder.count();
        }
        return 0;
    }
}
