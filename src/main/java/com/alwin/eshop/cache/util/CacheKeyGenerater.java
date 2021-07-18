package com.alwin.eshop.cache.util;

public class CacheKeyGenerater {

    private static final String PRODUCT_INFO_ID_FORMAT = "product_info_%s";

    private static final String SHOP_INFO_ID_FORMAT = "shop_info_%s";

    public static String generateProductInfoKey(Long productId) {
        return String.format(PRODUCT_INFO_ID_FORMAT, productId);
    }

    public static String generateShopInfoKey(Long productId) {
        return String.format(SHOP_INFO_ID_FORMAT, productId);
    }
}
