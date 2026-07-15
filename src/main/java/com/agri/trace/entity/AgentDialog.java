package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("agent_dialog")
public class AgentDialog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String sessionId;
    private String question;
    private String answer;
    private String toolCalls;    // JSON格式记录工具调用
    private LocalDateTime createTime;
}
