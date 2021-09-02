package cn.youai.commons.lock;

/**
 * 实体标识接口
 *
 * @param <T>
 */
public interface IEntity<T extends Comparable> {
    /**
     * 获取实体标识
     *
     * @return
     */
    T getIdentity();
}
