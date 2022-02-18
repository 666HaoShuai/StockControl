package com.coderman.controller.business;

import com.coderman.business.service.UserProductService;
import com.coderman.common.response.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/business/userProduct")
public class UserProductController {

    @Resource
    private UserProductService userProductService;

    /**
     * 出库
     * @param pNum
     * @return
     */
    @GetMapping("/reduce/{pNum}")
    public ResponseBean reduce(@PathVariable String pNum){
        log.info("调用【购入商品】接口, [{}]", pNum);
        ResponseBean responseBean = userProductService.reduceProduct(pNum);
        log.info("调用【购入商品】接口返回值[{}]", responseBean);
        return responseBean;
    }

    /**
     * 入库
     * @param pNum
     * @return
     */
    @GetMapping("/add/{pNum}")
    public ResponseBean add(@PathVariable String pNum){
        ResponseBean responseBean = userProductService.addProduct(pNum);
        return responseBean;
    }
}
