package com.coderman.business.service.imp;

import com.coderman.business.mapper.ProductStockMapper;
import com.coderman.business.mapper.UserProductMapper;
import com.coderman.business.service.UserProductService;
import com.coderman.common.model.business.ProductStock;
import com.coderman.common.model.business.UserProduct;
import com.coderman.common.response.ActiveUser;
import com.coderman.common.response.ResponseBean;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserProductServiceImpl implements UserProductService {

    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private ProductStockMapper productStockMapper;


    /**
     * 购入物品
     * @param pNum
     * @return
     */
    @Override
    public ResponseBean reduceProduct(String pNum) {
        //扣减总体库存
        ProductStock productStock = new ProductStock();
        productStock.setPNum(pNum);
        Example example = new Example(ProductStock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("pNum", pNum);

        productStock = productStockMapper.selectOneByExample(example);
        productStock.setStock(productStock.getStock()-1);
        productStockMapper.updateByPrimaryKey(productStock);

        //增加个人库存
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        UserProduct userProduct = new UserProduct();
        userProduct.setPNum(pNum);
        userProduct.setUserId(Math.toIntExact(activeUser.getUser().getId()));

        Example userExample = new Example(UserProduct.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("pNum", pNum);
        userCriteria.andEqualTo("userId", userProduct.getUserId());
        UserProduct selectOne = userProductMapper.selectOneByExample(userExample);

        if(selectOne!=null){
            selectOne.setPSum(selectOne.getPSum()+1);
            userProductMapper.updateByPrimaryKeySelective(selectOne);
        }else {
                userProduct.setPSum(1);
                userProductMapper.insertSelective(userProduct);
        } 
        return ResponseBean.success();
    }

    @Override
    public ResponseBean addProduct(String pNum) {
        //增加总体库存
        ProductStock productStock = new ProductStock();
        productStock.setPNum(pNum);
        Example example = new Example(ProductStock.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("pNum", pNum);

        productStock = productStockMapper.selectOneByExample(example);
        productStock.setStock(productStock.getStock() + 1);
        productStockMapper.updateByPrimaryKey(productStock);
        return ResponseBean.success();
    }

}
