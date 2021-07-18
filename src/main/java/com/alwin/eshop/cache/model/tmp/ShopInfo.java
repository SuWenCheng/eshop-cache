package com.alwin.eshop.cache.model.tmp;

import java.util.Date;
import javax.persistence.*;

@Table(name = "shop_info")
public class ShopInfo {
    @Id
    private Long id;

    private String name;

    private Integer level;

    @Column(name = "good_comment_rate")
    private String goodCommentRate;

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
     * @return level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }

    /**
     * @return good_comment_rate
     */
    public String getGoodCommentRate() {
        return goodCommentRate;
    }

    /**
     * @param goodCommentRate
     */
    public void setGoodCommentRate(String goodCommentRate) {
        this.goodCommentRate = goodCommentRate;
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