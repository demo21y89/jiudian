package com.agri.trace.controller.api;

import cn.hutool.core.util.IdUtil;
import com.agri.trace.dto.ChatRequest;
import com.agri.trace.dto.ChatResponse;
import com.agri.trace.dto.R;
import com.agri.trace.entity.AgentDialog;
import com.agri.trace.service.DialogService;
import com.agri.trace.agent.MainCoordinatorAgent;
import com.agri.trace.agent.tools.ToolRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private MainCoordinatorAgent agent;

    @Autowired
    private DialogService dialogService;

    @Autowired
    private ToolRegistry toolRegistry;

    @PostMapping("/send")
    public R<ChatResponse> send(@Valid @RequestBody ChatRequest request) {
        String sessionId = request.getSessionId();
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = IdUtil.fastSimpleUUID();
        }

        Map<String, Object> result = agent.processMessage(request.getMessage());

        AgentDialog dialog = new AgentDialog();
        dialog.setSessionId(sessionId);
        dialog.setQuestion(request.getMessage());
        dialog.setAnswer((String) result.get("answer"));
        dialog.setToolCalls(result.get("intent") != null ? result.get("intent").toString() : "");
        dialogService.save(dialog);

        ChatResponse response = new ChatResponse();
        response.setAnswer((String) result.get("answer"));
        response.setSessionId(sessionId);
        @SuppressWarnings("unchecked")
        java.util.List<String> sources = (java.util.List<String>) result.get("sources");
        response.setSources(sources);

        return R.ok(response);
    }

    @GetMapping("/tools")
    public R<?> listTools() {
        return R.ok(toolRegistry.getToolDescriptions());
    }
}