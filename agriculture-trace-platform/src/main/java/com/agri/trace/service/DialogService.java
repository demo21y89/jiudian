package com.agri.trace.service;

import com.agri.trace.entity.AgentDialog;
import java.util.List;

public interface DialogService {
    List<AgentDialog> findBySessionId(String sessionId);
    List<AgentDialog> findByUserId(Long userId);
    boolean save(AgentDialog dialog);
}
