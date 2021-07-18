package com.alwin.eshop.cache.service.impl;

import com.alwin.eshop.cache.mapper.ProductInfoMapper;
import com.alwin.eshop.cache.mapper.ShopInfoMapper;
import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.model.ShopInfo;
import com.alwin.eshop.cache.service.CacheService;
import com.alwin.eshop.cache.util.CacheKeyGenerater;
import com.alwin.eshop.cache.util.JsonHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CacheServiceImpl implements CacheService {

    private static final String CACHE_NAME = "local";

    private final ProductInfoMapper productInfoMapper;
    private final ShopInfoMapper shopInfoMapper;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @CachePut(value = CACHE_NAME, key = "'key_' + #productInfo.getId()")
    public ProductInfo saveLocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'key_' + #id")
    public ProductInfo getProductInfo(Long id) {
        return null;
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'product_info_' + #productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'product_info_' + #productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'shop_info_' + #shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'shop_info_' + #shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }

    @Override
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        String key = CacheKeyGenerater.generateProductInfoKey(productInfo.getId());
        redisTemplate.opsForValue().set(key, JsonHelper.toJson(productInfo));
    }

    @Override
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        String key = CacheKeyGenerater.generateShopInfoKey(shopInfo.getId());
        redisTemplate.opsForValue().set(key, JsonHelper.toJson(shopInfo));
    }

    @Override
    public ProductInfo getProductInfoFromRedisCache(Long productId) {
        String key = CacheKeyGenerater.generateProductInfoKey(productId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || "".equals(json)) {
            /*ProductInfo productInfo = productInfoMapper.selectByPrimaryKey(productId);
            json = JsonHelper.toJson(productInfo);
            redisTemplate.opsForValue().set(key, json);*/
            return null;
        }
        log.info("============getProductInfoFromRedisCache: " + json);
        return JsonHelper.fromJson(json, ProductInfo.class);
    }

    @Override
    public ShopInfo getShopInfoFromRedisCache(Long shopId) {
        String key = CacheKeyGenerater.generateShopInfoKey(shopId);
        String json = redisTemplate.opsForValue().get(key);
        if (json == null || "".equals(json)) {
            ShopInfo shopInfo = shopInfoMapper.selectByPrimaryKey(shopId);
            json = JsonHelper.toJson(shopInfo);
            redisTemplate.opsForValue().set(key, json);
        }
        return JsonHelper.fromJson(json, ShopInfo.class);
    }

    @Override
    public ProductInfo getProductInfoFromDB(Long productId) {
        return productInfoMapper.selectByPrimaryKey(productId);
    }

    @Override
    public ShopInfo getShopInfoFromDB(Long shopId) {
        return shopInfoMapper.selectByPrimaryKey(shopId);
    }
}
