package com.alwin.eshop.cache.controller;

import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.model.ShopInfo;
import com.alwin.eshop.cache.prewarm.CachePrewarmComponent;
import com.alwin.eshop.cache.rebuild.RebuildCacheQueue;
import com.alwin.eshop.cache.service.CacheService;
import com.alwin.eshop.cache.util.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CacheController {

    private final CacheService cacheService;
    private final CachePrewarmComponent cachePrewarmComponent;

    @GetMapping("/testPutCache")
    public ProductInfo testPutCache(ProductInfo productInfo) {
        return cacheService.saveLocalCache(productInfo);
    }

    @GetMapping("/testGetCache")
    public ProductInfo testGetCache(Long id) {
        return cacheService.getProductInfo(id);
    }

    @GetMapping("/getProductInfo")
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfo = cacheService.getProductInfoFromRedisCache(productId);
        log.info("=============从redis获取缓存，商品信息=" + JsonHelper.toJson(productInfo));

        if (productInfo == null) {
            // 从ehcache获取数据
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            log.info("=============从ehcache获取缓存，商品信息=" + JsonHelper.toJson(productInfo));
        }

        if (productInfo == null) {
            // 从数据源获取数据
            productInfo = cacheService.getProductInfoFromDB(productId);
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.putProductInfo(productInfo);
            log.info("===============从数据源（DB）获取商品信息，商品信息=" + JsonHelper.toJson(productInfo));
        }
        return productInfo;
    }

    @GetMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        return cacheService.getShopInfoFromRedisCache(shopId);
    }

    @GetMapping("/prewarmCache")
    public void prewarmCache() {
        cachePrewarmComponent.prewarmCache();
    }

}
