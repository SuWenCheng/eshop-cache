package com.alwin.eshop.cache.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Table(name = "shop_info")
@Data
public class ShopInfo {
    @Id
    private Long id;

    private String name;

    private Integer level;

    @Column(name = "good_comment_rate")
    private String goodCommentRate;

    @Column(name = "modify_time")
    private Date modifyTime;

}