package com.coderman.common.model.business;





import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "biz_user_product")
public class UserProduct {

    @Id
    private Long id;

    private Integer userId;

    /**
     * 产品
     */
    private String pNum;

    /**
     * 持有产品数量
     */
    private Integer pSum;
}
