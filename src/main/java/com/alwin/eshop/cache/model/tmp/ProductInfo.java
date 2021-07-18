package com.alwin.eshop.cache.model.tmp;

import java.util.Date;
import javax.persistence.*;

@Table(name = "product_info")
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

    /**
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return picture_list
     */
    public String getPictureList() {
        return pictureList;
    }

    /**
     * @param pictureList
     */
    public void setPictureList(String pictureList) {
        this.pictureList = pictureList;
    }

    /**
     * @return specification
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * @param specification
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * @return service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return size
     */
    public Double getSize() {
        return size;
    }

    /**
     * @param size
     */
    public void setSize(Double size) {
        this.size = size;
    }

    /**
     * @return shop_id
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * @param shopId
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    /**
     * @return modify_time
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}