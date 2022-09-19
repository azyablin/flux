package com.magnit.flux.mybatis.mapper;

import com.magnit.flux.model.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CustomerMapper {

    @Results(id = "customer")
    @Select("SELECT * FROM customer")
    Customer customer();

}
