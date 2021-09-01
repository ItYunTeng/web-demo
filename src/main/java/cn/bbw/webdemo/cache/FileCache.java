package cn.bbw.webdemo.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author buliangliang
 * @version V1.0.0
 * @date 2021/9/1 9:25 下午
 * @since 1.0
 */
@Slf4j
public final class FileCache extends BaseCache<String, InputStream> {

    private static class CacheHolder {
        private static final FileCache INSTANCE = new FileCache();
    }

    public static FileCache getInstance() {
        return FileCache.CacheHolder.INSTANCE;
    }

    private FileCache() {
        super();
    }

    @Override
    public InputStream without(String key) {
        ClassPathResource resource = new ClassPathResource("test.pdf");
        try {
            log.info("test-------pdf");
            return resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fileName() {
        return "";
    }

    public void put(Collection<InputStream> list) {
        for (InputStream inputStream : list) {
            put(fileName(), inputStream);
        }
    }
}
