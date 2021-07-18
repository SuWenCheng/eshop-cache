package com.alwin.eshop.cache.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "product_info")
@Data
public class ProductInfo {
    @Id
    private Long id;

    private String name;

    private Double price;

    @Column(name = "picture_list")
    private String pictureList;

    private String specification;

    private String service;

    private String color;

    private Double size;

    @Column(name = "shop_id")
    private Long shopId;

    @Column(name = "modify_time")
    private Date modifyTime;

}