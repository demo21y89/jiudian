package com.agri.trace.mapper;

import com.agri.trace.entity.AgentDialog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AgentDialogMapper extends BaseMapperExt<AgentDialog> {

    @Select("SELECT * FROM agent_dialog WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<AgentDialog> findBySessionId(@Param("sessionId") String sessionId);

    @Select("SELECT * FROM agent_dialog WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<AgentDialog> findByUserId(@Param("userId") Long userId);
}
