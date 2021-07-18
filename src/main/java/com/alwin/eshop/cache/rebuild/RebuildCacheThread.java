package com.alwin.eshop.cache.rebuild;

import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.service.CacheService;
import com.alwin.eshop.cache.zk.ZookeeperSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Component
@Slf4j
public class RebuildCacheThread implements Runnable{

    private final CacheService cacheService;

    @Override
    public void run() {
        log.info("============RebuildCacheThread线程开启=========================");
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZookeeperSession zksSession = ZookeeperSession.getInstance();

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();
            Long productId = productInfo.getId();
            zksSession.acquireDistributedLock(productId);

            ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productId);

            if (existedProductInfo != null) {
                try {
                    Date date = productInfo.getModifyTime();
                    Date existedDate = existedProductInfo.getModifyTime();

                    if (date.before(existedDate)) {
                        log.info("RebuildCacheThread: current date[{}] is before existedDate[{}], no need to update....", date, existedDate);
                        zksSession.releaseDistributedLock(productId);
                        continue;
                    }
                    log.info("RebuildCacheThread: current date[{}] is after existedDate[{}], start to update...", date, existedDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.info("RebuildCacheThread: existed product info is null.............");
            }

            cacheService.saveProductInfo2RedisCache(productInfo);
            zksSession.releaseDistributedLock(productId);
        }
    }

    @PostConstruct
    public void init() {
        new Thread(this).start();
    }
}
