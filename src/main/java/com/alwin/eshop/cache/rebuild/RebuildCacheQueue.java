package com.alwin.eshop.cache.rebuild;

import com.alwin.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<>(1000);

    public void putProductInfo(ProductInfo productInfo) {
        try {
            queue.put(productInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ProductInfo takeProductInfo() {
        try {
            return queue.take();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Singleton {
        private static RebuildCacheQueue instance = new RebuildCacheQueue();
        public static RebuildCacheQueue getInstance() {
            return instance;
        }
    }

    public static RebuildCacheQueue getInstance() {
        return Singleton.getInstance();
    }

}
