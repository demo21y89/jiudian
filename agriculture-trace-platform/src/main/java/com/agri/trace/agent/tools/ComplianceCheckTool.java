package com.agri.trace.agent.tools;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class ComplianceCheckTool implements McpTool {

    @Override
    public String getName() {
        return "compliance_check";
    }

    @Override
    public String getDescription() {
        return "农产品合规性自查，根据产品品类判断是否符合GB国家标准，输入商品ID或品类名称";
    }

    @Override
    public Map<String, Object> execute(Map<String, Object> params) {
        String category = (String) params.get("category");
        Map<String, Object> result = new LinkedHashMap<>();

        Map<String, List<String>> standards = new LinkedHashMap<>();
        standards.put("水果", Arrays.asList("GB 2763-2021 食品中农药最大残留限量",
                "GB/T 23351-2009 水果和蔬菜中多种农药残留检测方法"));
        standards.put("蔬菜", Arrays.asList("GB 2763-2021 食品中农药最大残留限量",
                "NY/T 761-2008 蔬菜和水果中有机磷、有机氯、拟除虫菊酯和氨基甲酸酯类农药多残留检测方法"));
        standards.put("粮食", Arrays.asList("GB 2763-2021 食品中农药最大残留限量",
                "GB 2715-2016 食品安全国家标准 粮食"));
        standards.put("茶叶", Arrays.asList("GB 2763-2021 食品中农药最大残留限量",
                "GB/T 22291-2017 茶叶中农药残留检测方法"));
        standards.put("畜禽", Arrays.asList("GB 2763-2021 食品中农药最大残留限量",
                "GB 2707-2016 食品安全国家标准 鲜(冻)畜、禽产品"));

        if (category != null && standards.containsKey(category)) {
            result.put("category", category);
            result.put("applicableStandards", standards.get(category));
            result.put("conclusion", "该品类适用上述国家标准，具体合规性需查看对应批次农残检测报告");
        } else {
            result.put("applicableStandards", standards);
            result.put("message", "请选择具体品类查看适用标准，或提供商品ID查询详细合规信息");
        }

        return result;
    }
}
