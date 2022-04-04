package com.coderman.business.service;

import com.coderman.common.response.ResponseBean;
import org.springframework.web.bind.annotation.GetMapping;

public interface UserProductService {

    /**
     * 出库
     */
    ResponseBean reduceProduct (String pNum);

    /**
     * 入库
     */
    ResponseBean addProduct(String pNum);

    /**
     * 查询销量
     */
    ResponseBean querySales(String pname);
}
