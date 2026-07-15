package com.agri.trace.mapper;

import com.agri.trace.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapperExt<Order> {

    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order findByOrderNo(@Param("orderNo") String orderNo);
}
