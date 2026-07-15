package com.agri.trace.agent;

import com.agri.trace.agent.tools.McpTool;
import com.agri.trace.agent.tools.ToolRegistry;
import com.agri.trace.agent.rag.RagService;
import com.agri.trace.entity.Product;
import com.agri.trace.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MainCoordinatorAgent {

    private static final Logger log = LoggerFactory.getLogger(MainCoordinatorAgent.class);

    @Autowired
    private ToolRegistry toolRegistry;

    @Autowired
    private RagService ragService;

    @Autowired
    private LlmService llmService;

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> processMessage(String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            // 1. 意图识别
            String intent = detectIntent(message);

            // 2. 提取实体
            Map<String, String> entities = extractEntities(message);

            // 3. 调用 MCP 工具（获取数据库中的真实数据）
            List<String> toolsCalled = getToolsForIntent(intent);
            List<Map<String, Object>> toolResults = new ArrayList<>();

            for (String toolName : toolsCalled) {
                McpTool tool = toolRegistry.getTool(toolName);
                if (tool != null) {
                    Map<String, Object> toolParams = new HashMap<>(entities);
                    try {
                        Map<String, Object> toolResult = tool.execute(toolParams);
                        toolResults.add(toolResult);
                        log.info("工具 {} 调用成功", toolName);
                    } catch (Exception e) {
                        log.warn("工具 {} 异常: {}", toolName, e.getMessage());
                        Map<String, Object> err = new HashMap<>();
                        err.put("error", toolName + " 调用失败");
                        toolResults.add(err);
                    }
                }
            }

            // 4. RAG 知识库检索
            List<Map<String, Object>> ragResults = ragService.search(message);

            // 5. 构建增强的系统提示词（含商品目录）
            String enhancedPrompt = buildSystemPrompt(message);

            // 6. 判断在线/离线模式，生成回答
            boolean useLlm = hasValidApiKey();
            String answer;

            if (useLlm) {
                // 在线模式：LLM 基于工具返回的真实数据生成回答
                answer = llmService.chat(enhancedPrompt, message, toolResults);
                log.info("LLM 在线回答完成");
            } else {
                // 离线模式：基于规则模板回答
                answer = generateOfflineAnswer(message, intent, toolResults, ragResults);
                log.info("离线模板回答");
            }

            result.put("answer", answer);
            result.put("intent", intent);
            result.put("toolResults", toolResults);
            result.put("sources", ragResults.stream()
                    .map(r -> (String) r.getOrDefault("title", ""))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()));
            result.put("mode", useLlm ? "online" : "offline");

        } catch (Exception e) {
            log.error("Agent 异常", e);
            result.put("answer", "抱歉，处理请求时出现异常：" + e.getMessage());
            result.put("error", e.getMessage());
        }
        return result;
    }

    /**
     * 构建系统提示词，包含平台商品信息供 LLM 参考
     */
    private String buildSystemPrompt(String userMessage) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个专业的农产品溯源与食品安全助手「农源AI」，运行在农产品溯源智能交易平台中。\n\n");
        sb.append("## 你的核心能力\n\n");
        sb.append("1. **溯源查询** — 查询农产品从种植/养殖到销售的全链路信息（农事记录、加工记录、检测报告）\n");
        sb.append("2. **农残合规分析** — 解析农残检测报告，对比国家标准判断是否达标\n");
        sb.append("3. **物流追踪** — 查询农产品物流运输路径\n");
        sb.append("4. **商品推荐** — 根据用户偏好推荐平台商品\n");
        sb.append("5. **产地信息** — 提供农产品产地气候、土壤、种植方式等知识\n");
        sb.append("6. **合规标准** — 查询各类农产品适用的国家标准\n\n");
        sb.append("## 当前平台商品目录\n\n");

        // 加载商品目录供 LLM 参考
        try {
            List<Product> products = productService.list();
            if (products != null && !products.isEmpty()) {
                // 按分类分组
                Map<String, List<Product>> byCategory = products.stream()
                        .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                        .collect(Collectors.groupingBy(Product::getCategory));

                for (Map.Entry<String, List<Product>> entry : byCategory.entrySet()) {
                    sb.append("### ").append(entry.getKey()).append("\n");
                    for (Product p : entry.getValue()) {
                        sb.append("- ").append(p.getName())
                          .append("（").append(p.getOrigin()).append("）")
                          .append(" | ￥").append(p.getPrice())
                          .append(" | 批次: ").append(p.getBatchNo())
                          .append(" | 溯源等级: ").append(p.getTraceLevel()).append("\n");
                    }
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            log.warn("加载商品目录失败: {}", e.getMessage());
        }

        sb.append("## 回答规范\n\n");
        sb.append("- 使用友好、专业的中文回答用户问题\n");
        sb.append("- 当用户查询溯源信息时，基于【系统查询到的数据】中的真实数据回答\n");
        sb.append("- 回答中引用具体数据（如检测值、产地、物流时间等）\n");
        sb.append("- 如果用户未指定具体商品，可根据上下文推荐或引导用户提供更多信息\n");
        sb.append("- 严格遵守食品安全相关法律法规\n");

        return sb.toString();
    }

    /**
     * 基于关键词的意图识别
     */
    private String detectIntent(String message) {
        String msg = message.toLowerCase();

        // 溯源查询相关关键词
        if (containsAny(msg, "溯源", "批次", "batch", "源头", "来源", "产地证", "追溯",
                "种植记录", "养殖记录", "加工记录", "检测报告", "档案")) {
            return "trace_query";
        }

        // 农残查询相关关键词
        if (containsAny(msg, "农残", "农药", "残留", "达标", "超标", "检测合格",
                "gb2763", "gb 2763", "食品安全", "合规")) {
            return "pesticide_check";
        }

        // 物流查询相关关键词
        if (containsAny(msg, "物流", "运输", "快递", "配送", "发货", "运送",
                "几天到", "到哪里", "配送时间")) {
            return "logistics_query";
        }

        // 商品推荐相关关键词
        if (containsAny(msg, "推荐", "有什么", "介绍", "买", "选购",
                "想吃", "想喝", "想要", "有机", "绿色")) {
            return "product_recommend";
        }

        // 产地信息相关关键词
        if (containsAny(msg, "产地", "气候", "土壤", "种植方式", "环境",
                "海拔", "温度", "降水")) {
            return "origin_info";
        }

        // 合规标准相关关键词
        if (containsAny(msg, "标准", "国标", "gb", "规范", "规定",
                "合规", "要求", "限量")) {
            return "compliance_check";
        }

        // 问候与闲聊
        if (containsAny(msg, "你好", "您好", "嗨", "hello", "hi",
                "早上好", "下午好", "晚上好", "在吗", "在不在", "help", "帮助")) {
            return "greeting";
        }

        // 默认：通用聊天
        return "general_chat";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从消息中提取实体（批次号、品类、产地、订单号等）
     */
    private Map<String, String> extractEntities(String message) {
        Map<String, String> entities = new HashMap<>();

        // 提取批次号：支持 BATCH-F-001 和 BATCH20260701001 两种格式
        Pattern batchPattern = Pattern.compile("BATCH(?:-[A-Z]-\\d{3,}|\\d{6,})", Pattern.CASE_INSENSITIVE);
        Matcher batchMatcher = batchPattern.matcher(message.toUpperCase());
        if (batchMatcher.find()) {
            entities.put("batchNo", batchMatcher.group());
        }

        // 提取订单号
        Pattern orderPattern = Pattern.compile("ORD\\d{8,}", Pattern.CASE_INSENSITIVE);
        Matcher orderMatcher = orderPattern.matcher(message.toUpperCase());
        if (orderMatcher.find()) {
            entities.put("orderNo", orderMatcher.group());
        }

        // 提取品类关键词
        String[] categories = {"水果", "蔬菜", "粮食", "茶叶", "畜禽",
                "苹果", "梨", "葡萄", "草莓", "香蕉", "橙子", "橘子", "猕猴桃",
                "白菜", "菠菜", "番茄", "黄瓜", "土豆", "萝卜",
                "大米", "小米", "小麦", "玉米", "大豆",
                "绿茶", "红茶", "乌龙茶", "普洱茶", "铁观音",
                "猪肉", "牛肉", "羊肉", "鸡肉", "鸡蛋",
                "山东", "陕西", "新疆", "四川", "云南", "福建", "黑龙江", "辽宁"};
        for (String cat : categories) {
            if (message.contains(cat)) {
                entities.put("keyword", cat);
                entities.put("category", cat);
                break;
            }
        }

        // 如果没有匹配到关键词，尝试提取第一个有意义的名词作为关键词
        if (!entities.containsKey("keyword")) {
            // 提取2个中文字符以上的连续文本
            Pattern kwPattern = Pattern.compile("[\\u4e00-\\u9fa5]{2,}");
            Matcher kwMatcher = kwPattern.matcher(message);
            if (kwMatcher.find()) {
                String kw = kwMatcher.group();
                if (!containsAny(kw, "你好", "您好", "什么", "怎么", "如何", "请问", "谢谢", "多少")) {
                    entities.put("keyword", kw);
                }
            }
        }

        // 提取产地
        String[] origins = {"山东", "陕西", "新疆", "四川", "云南", "福建",
                "黑龙江", "辽宁", "广东", "广西", "海南", "甘肃", "河南", "河北",
                "浙江", "江苏", "安徽", "湖南", "湖北", "江西", "贵州", "山西",
                "北京", "上海", "天津", "重庆"};
        for (String origin : origins) {
            if (message.contains(origin)) {
                entities.put("origin", origin);
                break;
            }
        }

        return entities;
    }

    /**
     * 根据意图获取需要调用的工具列表
     */
    private List<String> getToolsForIntent(String intent) {
        List<String> tools = new ArrayList<>();
        switch (intent) {
            case "trace_query":
                tools.add("trace_query");
                tools.add("pesticide_analyze");
                break;
            case "pesticide_check":
                tools.add("pesticide_analyze");
                tools.add("trace_query");
                break;
            case "logistics_query":
                tools.add("logistics_trace");
                break;
            case "product_recommend":
                tools.add("product_recommend");
                break;
            case "origin_info":
                tools.add("trace_query");
                tools.add("product_recommend");
                break;
            case "compliance_check":
                tools.add("compliance_check");
                break;
            case "greeting":
            case "general_chat":
            default:
                tools.add("product_recommend");
                break;
        }
        return tools;
    }

    /**
     * 判断是否有有效的 API Key
     */
    private boolean hasValidApiKey() {
        try {
            String apiKey = aiConfig.getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                log.info("API Key 为空，使用离线模式");
                return false;
            }
            // 排除未解析的 Spring 占位符（如 ${AI_API_KEY}）
            if (apiKey.startsWith("${") && apiKey.endsWith("}")) {
                log.info("API Key 为未解析占位符，使用离线模式");
                return false;
            }

            // 排除占位符密钥
            String lowerKey = apiKey.toLowerCase();
            if (lowerKey.contains("placeholder") || lowerKey.contains("your-api-key") || lowerKey.contains("your_api_key") || lowerKey.startsWith("sk-")) {
                log.info("API Key 为占位符，使用离线模式");
                return false;
            }
            // 至少需要看起来像有效的 key（长度 > 10）
            if (apiKey.length() < 10) {
                log.info("API Key 长度不足，使用离线模式");
                return false;
            }
            log.info("API Key 有效，使用在线模式（provider={}, key前缀={}）",
                    aiConfig.getProvider(),
                    apiKey.substring(0, Math.min(15, apiKey.length())) + "...");
            return true;
        } catch (Exception e) {
            log.warn("检查 API Key 异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 离线模式下生成基于规则的模板回答
     */
    private String generateOfflineAnswer(String message, String intent,
                                          List<Map<String, Object>> toolResults,
                                          List<Map<String, Object>> ragResults) {
        StringBuilder answer = new StringBuilder();

        switch (intent) {
            case "trace_query":
                answer.append("📋 **溯源信息查询结果**\n\n");
                boolean foundTrace = false;
                for (Map<String, Object> result : toolResults) {
                    if (result.containsKey("error")) {
                        answer.append("❌ ").append(result.get("error")).append("\n");
                    } else if (result.containsKey("batch")) {
                        foundTrace = true;
                        @SuppressWarnings("unchecked")
                        Map<String, Object> batch = (Map<String, Object>) result.get("batch");
                        answer.append("**批次信息**\n");
                        answer.append("- 批次号：").append(batch.get("batchNo")).append("\n");
                        answer.append("- 生产日期：").append(batch.get("produceDate")).append("\n");
                        answer.append("- 采收日期：").append(batch.get("harvestDate")).append("\n");
                        answer.append("- 产地地址：").append(batch.get("farmAddress")).append("\n");
                        answer.append("- 种植面积：").append(batch.get("farmArea")).append("\n");
                        answer.append("- 土壤类型：").append(batch.get("soilType")).append("\n\n");

                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> records = (List<Map<String, Object>>) result.get("traceRecords");
                        if (records != null && !records.isEmpty()) {
                            answer.append("**溯源记录**\n");
                            for (Map<String, Object> r : records) {
                                answer.append("- ").append(r.get("recordType"))
                                      .append(" | ").append(r.get("recordTime"))
                                      .append(" | ").append(r.get("content")).append("\n");
                            }
                            answer.append("\n");
                        }

                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> reports = (List<Map<String, Object>>) result.get("pesticideReports");
                        if (reports != null && !reports.isEmpty()) {
                            answer.append("**农残检测报告**\n");
                            for (Map<String, Object> r : reports) {
                                answer.append("- ").append(r.get("itemName"))
                                      .append("：检测值 ").append(r.get("result"))
                                      .append(r.get("unit"))
                                      .append("（标准限值 ").append(r.get("standardLimit"))
                                      .append(r.get("unit")).append("）")
                                      .append(" → ").append(Boolean.TRUE.equals(r.get("isCompliant")) ? "✅ 达标" : "❌ 超标")
                                      .append("\n");
                            }
                            answer.append("\n");
                            Boolean overall = (Boolean) result.get("overallCompliant");
                            if (overall != null) {
                                answer.append("**综合判定：** ").append(overall ? "✅ 该批次农残检测全部达标" : "❌ 存在超标项，请查看详情").append("\n");
                            }
                        }
                    }
                }
                if (!foundTrace) {
                    answer.append("💡 请输入具体的批次号（如 BATCH-F-001）以查询溯源信息。\n");
                    answer.append("平台支持的批次格式：BATCH-F-001（水果）、BATCH-V-001（蔬菜）、BATCH-G-001（粮食）、BATCH-T-001（茶叶）、BATCH-L-001（畜禽）\n");
                }
                break;

            case "pesticide_check":
                answer.append("🔬 **农残检测分析**\n\n");
                boolean foundPesticide = false;
                for (Map<String, Object> result : toolResults) {
                    if (result.containsKey("error")) {
                        answer.append("❌ ").append(result.get("error")).append("\n");
                    } else if (result.containsKey("compliant")) {
                        foundPesticide = true;
                        Boolean compliant = (Boolean) result.get("compliant");
                        if (compliant == null) {
                            answer.append("该批次暂无农残检测报告。\n");
                            answer.append("请提供具体的批次号以查询农残检测信息。\n");
                        } else if (compliant) {
                            answer.append("✅ **该批次农残检测全部达标**\n");
                            answer.append("参考标准：").append(result.get("standard")).append("\n\n");
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> details = (List<Map<String, Object>>) result.get("details");
                            if (details != null) {
                                for (Map<String, Object> d : details) {
                                    answer.append("- ").append(d.get("itemName"))
                                          .append("：").append(d.get("result")).append(d.get("unit"))
                                          .append("（标准限值 ").append(d.get("standardLimit")).append(d.get("unit") + "）")
                                          .append(" ✅\n");
                                }
                            }
                        } else {
                            answer.append("❌ **该批次存在超标项**\n");
                            answer.append("参考标准：").append(result.get("standard")).append("\n\n");
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> details = (List<Map<String, Object>>) result.get("details");
                            if (details != null) {
                                for (Map<String, Object> d : details) {
                                    answer.append("- ").append(d.get("itemName"))
                                          .append("：").append(d.get("result")).append(d.get("unit"))
                                          .append("（标准限值 ").append(d.get("standardLimit")).append(d.get("unit") + "）")
                                          .append(Boolean.TRUE.equals(d.get("compliant")) ? " ✅" : " ❌ 超标")
                                          .append("\n");
                                }
                            }
                        }
                    }
                }
                if (!foundPesticide) {
                    answer.append("请提供具体的批次号（如 BATCH-F-001）以查询农残检测信息。\n");
                }
                break;

            case "product_recommend":
            case "greeting":
            case "general_chat":
                if ("greeting".equals(intent)) {
                    answer.append("👋 **您好！欢迎来到农源AI溯源平台**\n\n");
                    answer.append("我是您的AI溯源助手，可以帮您：\n");
                    answer.append("1. 🔍 查询农产品溯源信息（输入批次号）\n");
                    answer.append("2. 🔬 分析农残检测报告\n");
                    answer.append("3. 📦 追踪物流运输信息\n");
                    answer.append("4. 🛒 推荐优质农产品\n");
                    answer.append("5. 🌾 了解产地信息和种植知识\n\n");
                    answer.append("您可以直接问我，比如：\n");
                    answer.append("- \"查询批次BATCH-F-001的溯源信息\"\n");
                    answer.append("- \"山东苹果的农残达标吗？\"\n");
                    answer.append("- \"有什么水果推荐？\"\n");
                    answer.append("- \"讲一下黑龙江大米的产地特点\"\n\n");
                    answer.append("💡 **提示：** 配置 API Key 后可获得 AI 联网回答，更精准灵活。\n");
                    break;
                }

                answer.append("🛒 **平台推荐商品**\n\n");
                boolean hasProducts = false;
                for (Map<String, Object> result : toolResults) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
                    if (products != null && !products.isEmpty()) {
                        hasProducts = true;
                        for (Map<String, Object> p : products) {
                            answer.append("**").append(p.get("name")).append("**")
                                  .append("（").append(p.get("origin")).append("）")
                                  .append("\n  💵 ￥").append(p.get("price"))
                                  .append(" | 📝 库存 ").append(p.get("stock")).append("件")
                                  .append(" | 🏷️ ").append(p.get("category"))
                                  .append(" | 🔆 批次 ").append(p.get("traceLevel")).append("级溯源\n\n");
                        }
                    }
                }
                if (!hasProducts) {
                    answer.append("平台暂无在售商品，请稍后再来。\n");
                }
                answer.append("💡 您也可以问「山东苹果的农残达标吗？」查询溯源信息，或访问商城浏览更多商品。\n");
                break;

            case "logistics_query":
                answer.append("🚚 **物流信息**\n\n");
                boolean foundLogistics = false;
                for (Map<String, Object> result : toolResults) {
                    if (result.containsKey("error")) {
                        answer.append("❌ ").append(result.get("error")).append("\n");
                    } else if (result.containsKey("orderNo")) {
                        foundLogistics = true;
                        answer.append("订单号：").append(result.get("orderNo")).append("\n");
                        answer.append("状态：").append(result.get("status")).append("\n");
                        if (result.get("logisticsNo") != null) {
                            answer.append("物流单号：").append(result.get("logisticsNo")).append("\n");
                        }
                        if (result.get("logisticsCompany") != null) {
                            answer.append("物流公司：").append(result.get("logisticsCompany")).append("\n");
                        }
                    }
                }
                if (!foundLogistics) {
                    answer.append("💡 请提供订单号（如 ORD20260701001）查询详细物流信息。\n");
                }
                break;

            case "origin_info":
                answer.append("🌾 **产地知识**\n\n");
                answer.append("平台商品来自全国各地优质产区：\n");
                answer.append("- 🍎 **山东烟台**：红富士苹果核心产区，温带季风气候，棕壤土\n");
                answer.append("- 🌾 **黑龙江五常**：中国优质大米产区，黑土地，昼夜温差大\n");
                answer.append("- 🍵 **福建安溪**：铁观音原产地，红壤山地，海拔300-1000米\n");
                answer.append("- 🥬 **云南昆明**：高原蔬菜产区，气候四季如春\n\n");
                answer.append("💡 输入具体商品名称可查询详细产地和种植信息。\n");
                break;

            case "compliance_check":
                answer.append("📐 **合规标准**\n\n");
                for (Map<String, Object> result : toolResults) {
                    if (result.containsKey("category")) {
                        @SuppressWarnings("unchecked")
                        List<String> standards = (List<String>) result.get("applicableStandards");
                        if (standards != null) {
                            answer.append("**").append(result.get("category")).append("** 适用标准：\n");
                            for (String s : standards) {
                                answer.append("- ").append(s).append("\n");
                            }
                        }
                    } else if (result.containsKey("applicableStandards")) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> standardsMap = (Map<String, List<String>>) result.get("applicableStandards");
                        if (standardsMap != null) {
                            for (Map.Entry<String, List<String>> entry : standardsMap.entrySet()) {
                                answer.append("**").append(entry.getKey()).append("**\n");
                                for (String s : entry.getValue()) {
                                    answer.append("- ").append(s).append("\n");
                                }
                            }
                        }
                    }
                }
                answer.append("\n💡 输入具体品类名称可查询对应的国家标准详情。\n");
                break;
        }

        if (!ragResults.isEmpty()) {
            answer.append("\n📎 **参考文档**\n");
            for (Map<String, Object> doc : ragResults) {
                answer.append("- 《").append(doc.get("title")).append("》\n");
            }
        }

        return answer.toString();
    }
}