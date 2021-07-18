package com.alwin.eshop.cache.kafka.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alwin.eshop.cache.model.ProductInfo;
import com.alwin.eshop.cache.model.ShopInfo;
import com.alwin.eshop.cache.service.CacheService;
import com.alwin.eshop.cache.util.JsonHelper;
import com.alwin.eshop.cache.zk.ZookeeperSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@Component
public class EshopCacheConsumer {

    private final static String TOPIC = "cache-message";

    private final CacheService cacheService;

    @KafkaListener(topics = {TOPIC})
    public void listen(List<ConsumerRecord<String, String>> records) {
        log.info("receive: {}", records.size());
        records.forEach(record -> {
            String message = record.value();
            log.info("=================收到的kafka消息：" + message);
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONObject.parseObject(message);
            } catch (Exception e) {
                log.error("parse jsonObject error");
                jsonObject = new JSONObject();
            }
            String serviceId = jsonObject.getString("serviceId");
            if ("productInfoService".equals(serviceId)) {
                saveProductInfo(message);
            } else if ("shopInfoService".equals(serviceId)){
                saveShopInfo(message);
            }
        });
    }

    // {"serviceId": "productInfoService", "id": 1, "name": "手机", "price": 2999, "shopId": 1}
    //{"serviceId":"productInfoService","id":1,"name":"iPhone11pro","price":9999.0,"pictureList":"a.jpg,b.jpg","specification":"iPhone规格","service":"iPhone售后服务","color":"白色","size":6.7,"shopId":1,"modifyTime":1625931027000}
    private void saveProductInfo(String message) {

        ProductInfo productInfo = JsonHelper.fromJson(message, ProductInfo.class);
        Long productId = productInfo.getId();

        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        zkSession.acquireDistributedLock(productId);

        ProductInfo existedProductInfo = cacheService.getProductInfoFromRedisCache(productId);
        if (existedProductInfo != null) {
            try {
                Date date = productInfo.getModifyTime();
                Date existedDate = existedProductInfo.getModifyTime();

                if (date.before(existedDate)) {
                    log.info("current date[{}] is before existedDate[{}], no need to update....", date, existedDate);
                    return;
                }
                log.info("current date[{}] is after existedDate[{}], start to update...", date, existedDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.info("existed product info is null.............");
        }

        cacheService.saveProductInfo2RedisCache(productInfo);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 释放分布式锁
        zkSession.releaseDistributedLock(productId);

        cacheService.saveProductInfo2LocalCache(productInfo);


    }

    // {"serviceId": "shopInfoService", "id": 1, "name": "手机商铺", "level": 1, "goodCommentRate": 95}
    private void saveShopInfo(String message) {
        ShopInfo shopInfo = JsonHelper.fromJson(message, ShopInfo.class);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        log.info("===================获取刚保存到本地的商品信息：" + cacheService.getShopInfoFromLocalCache(shopInfo.getId()));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }

}
