package com.agritrace.mcp.core;

import com.agritrace.mcp.skill.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 内置 Skill 注册配置
 * 将6个核心 Skill 注册到 MCP 调度器中
 */
@Configuration
public class SkillRegistration {

    private static final Logger log = LoggerFactory.getLogger(SkillRegistration.class);

    private final MCPDispatcher dispatcher;
    private final TraceQuerySkill traceQuerySkill;
    private final PesticideAnalysisSkill pesticideAnalysisSkill;
    private final LogisticsQuerySkill logisticsQuerySkill;
    private final RecommendSkill recommendSkill;
    private final RegulationQuerySkill regulationQuerySkill;
    private final CertVerificationSkill certVerificationSkill;

    public SkillRegistration(MCPDispatcher dispatcher,
                             TraceQuerySkill traceQuerySkill,
                             PesticideAnalysisSkill pesticideAnalysisSkill,
                             LogisticsQuerySkill logisticsQuerySkill,
                             RecommendSkill recommendSkill,
                             RegulationQuerySkill regulationQuerySkill,
                             CertVerificationSkill certVerificationSkill) {
        this.dispatcher = dispatcher;
        this.traceQuerySkill = traceQuerySkill;
        this.pesticideAnalysisSkill = pesticideAnalysisSkill;
        this.logisticsQuerySkill = logisticsQuerySkill;
        this.recommendSkill = recommendSkill;
        this.regulationQuerySkill = regulationQuerySkill;
        this.certVerificationSkill = certVerificationSkill;
    }

    @PostConstruct
    public void registerSkills() {
        dispatcher.registerPlugin(traceQuerySkill);
        dispatcher.registerPlugin(pesticideAnalysisSkill);
        dispatcher.registerPlugin(logisticsQuerySkill);
        dispatcher.registerPlugin(recommendSkill);
        dispatcher.registerPlugin(regulationQuerySkill);
        dispatcher.registerPlugin(certVerificationSkill);
        log.info("已注册 6 个核心 MCP Skill: 溯源查询、农残解析、物流查询、智能推荐、法规检索、合格证验真");
    }
}
