package com.coderman.common.vo.business;

import lombok.Data;

@Data
public class UserProductVo {

    /**
     * '我'的库存
     */
    private int ownStock;

    /**
     * 推荐评分情况
     */
    private int recommendedScore;

    /**
     * 物品型号
     */
    private String pNum;

    /**
     * 物品名称
     */
    private String pName;

    /**
     * 销量(被购买数)
     */
    private int pSum;


}
