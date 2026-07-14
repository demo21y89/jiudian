package com.agri.trace.mapper;

import com.agri.trace.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderDetailMapper extends BaseMapperExt<OrderDetail> {

    @Select("SELECT * FROM order_detail WHERE order_id = #{orderId}")
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);
}
