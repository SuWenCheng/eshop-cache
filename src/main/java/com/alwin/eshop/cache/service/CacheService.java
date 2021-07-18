package com.alwin.eshop.cache.service;

import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.model.ShopInfo;

public interface CacheService {

    ProductInfo saveLocalCache(ProductInfo productInfo);

    ProductInfo getProductInfo(Long id);

    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    ProductInfo getProductInfoFromLocalCache(Long productId);

    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    ShopInfo getShopInfoFromLocalCache(Long shopId);

    void saveProductInfo2RedisCache(ProductInfo productInfo);

    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    ProductInfo getProductInfoFromRedisCache(Long productId);

    ShopInfo getShopInfoFromRedisCache(Long shopId);

    ProductInfo getProductInfoFromDB(Long productId);

    ShopInfo getShopInfoFromDB(Long shopId);

}
