package com.agri.trace.service.impl;

import com.agri.trace.entity.AgentDialog;
import com.agri.trace.mapper.AgentDialogMapper;
import com.agri.trace.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    private AgentDialogMapper agentDialogMapper;

    @Override
    public List<AgentDialog> findBySessionId(String sessionId) {
        return agentDialogMapper.findBySessionId(sessionId);
    }

    @Override
    public List<AgentDialog> findByUserId(Long userId) {
        return agentDialogMapper.findByUserId(userId);
    }

    @Override
    public boolean save(AgentDialog dialog) {
        dialog.setCreateTime(LocalDateTime.now());
        return agentDialogMapper.insert(dialog) > 0;
    }
}
