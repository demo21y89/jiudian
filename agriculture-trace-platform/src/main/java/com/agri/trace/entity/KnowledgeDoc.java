package com.agri.trace.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("knowledge_doc")
public class KnowledgeDoc {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String content;
    private String category;     // 法规/标准/规范/产品知识
    private String source;
    private LocalDateTime uploadTime;
}
