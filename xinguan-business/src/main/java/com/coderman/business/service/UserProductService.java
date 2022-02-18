package com.coderman.business.service;

import com.coderman.common.response.ResponseBean;

public interface UserProductService {

    /**
     * 出库
     */
    ResponseBean reduceProduct(String pNum);

    /**
     * 入库
     */
    ResponseBean addProduct(String pNum);
}
