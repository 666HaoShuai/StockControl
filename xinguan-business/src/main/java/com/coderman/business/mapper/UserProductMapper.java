package com.coderman.business.mapper;

import tk.mybatis.mapper.common.Mapper;
import com.coderman.common.model.business.UserProduct;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductMapper extends Mapper<UserProduct> {
}
