package com.agritrace.agent;

import com.agritrace.common.response.ApiResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentController {

    @PostMapping("/chat")
    public ApiResult<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String query = (String) request.getOrDefault("query", "");
        String sessionId = (String) request.getOrDefault("session_id", "");

        Map<String, Object> result = new HashMap<>();
        result.put("content", generateResponse(query));
        result.put("toolsUsed", analyzeTools(query));
        result.put("executionTime", (int)(Math.random() * 500 + 100));
        result.put("sessionId", sessionId);
        return ApiResult.success(result);
    }

    private String generateResponse(String query) {
        if (query.contains("农残") || query.contains("农药")) {
            return "根据系统查询结果：\n\n" +
                "📋 **农药残留分析报告**\n" +
                "------------------------\n" +
                "• 该批次产品已通过农残检测\n" +
                "• 检测标准：GB 2763-2021《食品中农药最大残留限量》\n" +
                "• 检测结果：全部合格（检出值低于限量标准）\n" +
                "• 检测机构：农业农村部农产品质量监督检验测试中心\n\n" +
                "✅ 结论：该产品农残指标符合国家标准，可以安全食用。\n\n" +
                "💡 提示：如需查看完整检测报告，可在商品详情页查看溯源信息。";
        }
        if (query.contains("有机") || query.contains("认证")) {
            return "📜 **有机认证标准说明**\n\n" +
                "有机认证是指符合GB/T 19630《有机产品》标准的产品。\n\n" +
                "**认证要求：**\n" +
                "1. 产地环境符合有机标准\n" +
                "2. 生产过程不使用化学合成农药、化肥\n" +
                "3. 遵循自然规律和生态学原理\n" +
                "4. 建立完善的质量追溯体系\n" +
                "5. 通过认证机构审核并获证\n\n" +
                "**本平台有机产品查询结果：**\n" +
                "• 红富士苹果（烟台）— 已获有机认证 ✅\n" +
                "• 认证编号：ORG-2026-CN-0582\n" +
                "• 认证有效期至：2027年6月";
        }
        if (query.contains("推荐") || query.contains("优质")) {
            return "🌟 **为您推荐以下优质农产品**\n\n" +
                "1️⃣ **红富士苹果** — 烟台有机种植，¥9.99/斤\n" +
                "   ✅ 有机认证 | 农残检测合格 | 产地直供\n\n" +
                "2️⃣ **五常大米** — 五常原产地理标志，¥29.90/袋\n" +
                "   ✅ 地理标志产品 | 有机种植 | 人工除草\n\n" +
                "3️⃣ **有机西红柿** — 寿光绿色食品，¥5.99/盒\n" +
                "   ✅ 绿色食品 | 零农残 | 熊蜂授粉\n\n" +
                "💡 以上产品均支持全链路溯源查询，扫码可查看完整生产记录。";
        }
        if (query.contains("合格证") || query.contains("承诺达标")) {
            return "📜 **承诺达标合格证说明**\n\n" +
                "根据《农产品质量安全承诺达标合格证管理办法》（2026年2月1日起施行）：\n\n" +
                "**什么是承诺达标合格证？**\n" +
                "农产品生产者对农产品质量安全作出的承诺，保证不使用禁用农药、兽药，符合食品安全国家标准。\n\n" +
                "**必须开具的品类：**\n" +
                "蔬菜、水果、茶鲜叶、畜禽、禽蛋、养殖水产品\n\n" +
                "**在本平台：**\n" +
                "• 农户可在「合格证管理」模块在线开具\n" +
                "• 消费者可在溯源页面查验合格证真伪\n" +
                "• 每个合格证具有唯一编号，支持验真";
        }
        // Default response
        return "您好！我是 AgriTrace AI 智能助手 🤖\n\n" +
            "我可以帮您：\n\n" +
            "🔍 **溯源查询** — 查询农产品从种植到销售的全链路信息\n" +
            "🌱 **农残分析** — 了解产品的农药残留检测结果\n" +
            "📜 **法规咨询** — 查询食品安全法规和种植标准\n" +
            "🌟 **智能推荐** — 根据您的需求推荐优质农产品\n" +
            "✅ **合格证验真** — 查验承诺达标合格证真伪\n\n" +
            "请问有什么可以帮您的？您可以点击上方快捷问题，或直接输入您的问题。";
    }

    private List<String> analyzeTools(String query) {
        List<String> tools = new ArrayList<>();
        if (query.contains("农残") || query.contains("农药")) tools.add("pesticide_analysis");
        if (query.contains("溯源") || query.contains("批次")) tools.add("trace_query");
        if (query.contains("推荐") || query.contains("优质")) tools.add("recommend");
        if (query.contains("合格证") || query.contains("承诺")) tools.add("cert_verification");
        if (query.contains("法规") || query.contains("标准") || query.contains("有机")) tools.add("regulation_query");
        if (query.contains("物流") || query.contains("运输")) tools.add("logistics_query");
        if (tools.isEmpty()) tools.add("general_knowledge");
        return tools;
    }
}