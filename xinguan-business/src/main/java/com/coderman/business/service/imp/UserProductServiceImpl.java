package com.coderman.business.service.imp;

import com.coderman.business.mapper.ProductMapper;
import com.coderman.business.mapper.ProductStockMapper;
import com.coderman.business.mapper.UserProductMapper;
import com.coderman.business.service.UserProductService;
import com.coderman.common.model.business.Product;
import com.coderman.common.model.business.ProductStock;
import com.coderman.common.model.business.UserProduct;
import com.coderman.common.response.ActiveUser;
import com.coderman.common.response.ResponseBean;
import com.coderman.common.vo.business.UserProductVo;
import com.mysql.cj.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserProductServiceImpl implements UserProductService {

    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private ProductStockMapper productStockMapper;
    @Resource
    private ProductMapper productMapper;


    /**
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
        if (productStock.getStock() <= 0) {
            return ResponseBean.error("库存不足！");
        }
        productStock.setStock(productStock.getStock() - 1);
        productStockMapper.updateByPrimaryKey(productStock);

        //销量
        Example productExample = new Example(Product.class);
        Example.Criteria productCriteria = productExample.createCriteria();
        productCriteria.andEqualTo("pNum", pNum);
        Product product = productMapper.selectOneByExample(productExample);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        UserProduct userProduct = new UserProduct();
        userProduct.setPName(product.getName());
        userProduct.setPNum(pNum);
        userProduct.setUserId(Math.toIntExact(activeUser.getUser().getId()));

        Example userExample = new Example(UserProduct.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("pNum", pNum);
        userCriteria.andEqualTo("userId", userProduct.getUserId());
        UserProduct selectOne = userProductMapper.selectOneByExample(userExample);

        if (selectOne != null) {
            selectOne.setPSum(selectOne.getPSum() + 1);
            userProductMapper.updateByPrimaryKeySelective(selectOne);
        } else {
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

    /**
     * 查询销量
     *
     * @return
     */
    @Override
    public ResponseBean querySales(String pname) {

        ArrayList<UserProductVo> userProductVos = new ArrayList<>();
        List<Product> products;
        if(StringUtils.isNullOrEmpty(pname)){
            products = productMapper.selectAll();
        }else {
            Example userExample = new Example(Product.class);
            Example.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andLike("name","%"+pname+"%");
            products = productMapper.selectByExample(userExample);
        }

        products.stream().forEach(product -> {
            UserProductVo vo = new UserProductVo();
            vo.setPName(product.getName());
            vo.setPNum(product.getPNum());
            // 设置个人库存
            Example userExample = new Example(ProductStock.class);
            Example.Criteria userCriteria = userExample.createCriteria();
            userCriteria.andEqualTo("pNum", product.getPNum());
            ProductStock selectOne = productStockMapper.selectOneByExample(userExample);
            vo.setOwnStock(selectOne == null ? 0 : selectOne.getStock().intValue());

            // 设置销量
            Example userExample2 = new Example(UserProduct.class);
            Example.Criteria userCriteria2 = userExample2.createCriteria();
            userCriteria2.andEqualTo("pNum", product.getPNum());
            List<UserProduct> userProducts = userProductMapper.selectByExample(userExample2);
            int temp = 0;
            for (UserProduct userProduct : userProducts) {
                temp += userProduct.getPSum();
            }
            vo.setPSum(temp);

            //设置推荐等级
            if (vo.getPSum() > 50) {
                if (vo.getOwnStock() < 20) {
                    vo.setRecommendedScore(5);
                } else {
                    vo.setRecommendedScore(4);
                }
            } else if (50 >= vo.getPSum() && vo.getPSum() > 40) {
                if (vo.getOwnStock() < 20) {
                    vo.setRecommendedScore(4);
                } else {
                    vo.setRecommendedScore(3);
                }
            } else if (40 >= vo.getPSum() && vo.getPSum() > 30) {
                if (vo.getOwnStock() < 20) {
                    vo.setRecommendedScore(3);
                } else {
                    vo.setRecommendedScore(2);
                }

            } else if (30 >= vo.getPSum() && vo.getPSum() > 20) {
                if (vo.getOwnStock() < 20) {
                    vo.setRecommendedScore(2);
                } else {
                    vo.setRecommendedScore(1);
                }
            } else if (20 >= vo.getPSum()) {
                vo.setRecommendedScore(1);
            }

            userProductVos.add(vo);
        });
        List<UserProductVo> newList = userProductVos.stream().sorted(Comparator.comparing(UserProductVo::getPSum).reversed())
                .collect(Collectors.toList());

        return ResponseBean.success(newList.size() >= 8 ? newList.subList(0, 8) : newList);
    }

}
