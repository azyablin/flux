package com.magnit.flux.mybatis.mapper;


import com.magnit.flux.model.entity.Operation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.cursor.Cursor;

@Mapper
public interface OperationMapper {

    Cursor<Operation> getAllOperations();


}
