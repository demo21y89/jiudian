package com.agri.trace.controller.admin;

import com.agri.trace.dto.R;
import com.agri.trace.entity.AgentDialog;
import com.agri.trace.mapper.AgentDialogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/dialogs")
public class AdminDialogsController {

    @Autowired
    private AgentDialogMapper agentDialogMapper;

    @GetMapping
    public R<List<AgentDialog>> list() {
        return R.ok(agentDialogMapper.selectList(null));
    }
}
