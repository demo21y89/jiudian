package com.agri.trace.mapper;

import com.agri.trace.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapperExt<User> {

    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);
}
