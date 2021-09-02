package cn.bbw.webdemo.lock;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author buliangliang
 * @version V1.0.0
 * @className ChainLock
 * @date 2021/9/2 7:51 上午
 * @since 1.0
 */
public class ChainLock {
    private final List<? extends Lock> locks;
    /**
     * 获取锁超时时间,单位：毫秒
     */
    private static final int TIME_OUT = 5;
    /**
     * 获取锁超时时间,次数
     */
    private static final int TIMES = 3;

    /**
     * 初始化锁链
     *
     * @param locks
     */
    public ChainLock(List<? extends Lock> locks) {
        if ((locks == null) || (locks.isEmpty())) {
            throw new IllegalArgumentException("构建锁链的锁数量不能为0");
        }
        this.locks = locks;
    }

    /**
     * 加锁
     */
    public void lock() {
        boolean relock = false;
        do {
            relock = false;
            for (int i = 0; i < locks.size(); i++) {
                int count = 0;
                Lock current = locks.get(i);
                try {
                    while (true) {
                        if (current.tryLock() || current.tryLock(TIME_OUT, TimeUnit.MILLISECONDS)) {
                            break;
                        }

                        if (count++ >= TIMES) {
                            relock = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    relock = true;
                }
                if (relock) {
                    unlock(i);
                    break;
                }
            }
        } while (relock);
    }

    /**
     * 解锁
     */
    public void unlock() {
        unlock(locks.size());
    }

    /**
     * 解锁
     *
     * @param end 位置
     */
    private void unlock(int end) {
        end = Math.min(end, locks.size());
        for (int i = 0; i < end; i++) {
            Lock objectLock = locks.get(i);
            try {
                if (objectLock != null) {
                    objectLock.unlock();
                }
            } catch (Exception e) {
            }
        }
    }
}
