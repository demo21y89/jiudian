package com.agritrace.config;

import com.agritrace.module.mall.entity.Product;
import com.agritrace.module.mall.repository.ProductRepository;
import com.agritrace.module.order.entity.Order;
import com.agritrace.module.order.repository.OrderRepository;
import com.agritrace.module.trace.entity.TraceRecord;
import com.agritrace.module.trace.entity.TraceStage;
import com.agritrace.module.trace.repository.TraceRepository;
import com.agritrace.module.trace.repository.TraceStageRepository;
import com.agritrace.module.user.entity.User;
import com.agritrace.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TraceRepository traceRepository;
    private final TraceStageRepository stageRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ProductRepository productRepository,
                           UserRepository userRepository,
                           OrderRepository orderRepository,
                           TraceRepository traceRepository,
                           TraceStageRepository stageRepository,
                           PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.traceRepository = traceRepository;
        this.stageRepository = stageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("初始化演示用户...");
            createUser("farmer_demo", "123456", "老王果园", "FARMER", "13800138001");
            createUser("consumer_demo", "123456", "测试消费者", "CONSUMER", "13800138002");
            createUser("dist_demo", "123456", "丰源分销", "DISTRIBUTOR", "13800138003");
            createUser("admin_demo", "123456", "系统管理员", "ADMIN", "13800138000");
        }

        if (productRepository.count() == 0) {
            log.info("初始化商品数据...");
            // 水果类
            createProduct("红富士苹果", "水果", "山东省烟台市", "5kg/箱", "斤", 9.99, 500,
                "烟台红富士苹果，脆甜多汁，有机种植，日照充足",
                "有机认证", "B20260701", "施用有机肥，生物防治病虫害", "农残检测全部合格，符合GB 2763标准");
            createProduct("海南芒果", "水果", "海南省三亚市", "2.5kg/箱", "斤", 15.80, 200,
                "海南金煌芒，果肉细腻，香甜多汁",
                "绿色食品", "B20260706", "热带气候种植，有机施肥", "农残检测合格");
            createProduct("丹东草莓", "水果", "辽宁省丹东市", "1kg/盒", "盒", 25.00, 150,
                "丹东99草莓，果大味甜，牛奶灌溉",
                "地理标志", "B20260707", "大棚种植，蜜蜂授粉", "零农残检测");
            createProduct("新疆葡萄", "水果", "新疆吐鲁番", "2kg/箱", "斤", 12.00, 180,
                "吐鲁番无核白葡萄，皮薄肉脆，甜度高",
                "地理标志", "B20260708", "天然晾晒，无农药", "农残检测合格");
            createProduct("赣南脐橙", "水果", "江西省赣州市", "5kg/箱", "斤", 8.50, 400,
                "赣南脐橙，汁多味甜，富含维C",
                "地理标志", "B20260709", "山地种植，有机肥", "符合国家标准");
            createProduct("无锡水蜜桃", "水果", "江苏省无锡市", "2kg/箱", "斤", 18.00, 120,
                "阳山水蜜桃，汁多如蜜，入口即化",
                "地理标志", "B20260710", "传统种植，人工套袋", "农残检测合格");
            createProduct("库尔勒香梨", "水果", "新疆库尔勒", "3kg/箱", "斤", 10.00, 250,
                "库尔勒香梨，皮薄肉细，酥脆多汁",
                "地理标志", "B20260711", "自然生长，有机标准", "零农残检测");
            

            // 蔬菜类
            createProduct("有机西红柿", "蔬菜", "山东省寿光市", "1kg/盒", "盒", 5.99, 300,
                "沙瓤多汁，酸甜可口，熊蜂授粉，零激素",
                "绿色食品", "B20260703", "大棚种植，熊蜂授粉", "零农残检测报告");
            
            
            

            // 谷物类
            createProduct("五常大米", "谷物", "黑龙江省五常市", "10kg/袋", "袋", 29.90, 200,
                "正宗五常稻花香，有机种植，颗粒饱满",
                "地理标志", "B20260702", "有机种植，人工除草", "重金属检测合格");
            createProduct("甜玉米", "谷物", "吉林省公主岭市", "5kg/箱", "斤", 4.50, 400,
                "东北甜玉米，鲜甜可口，非转基因",
                "无公害", "B20260716", "黑土地种植，自然成熟", "非转基因检测合格");
            createProduct("有机土豆", "蔬菜", "甘肃省定西市", "5kg/箱", "斤", 3.00, 500,
                "定西土豆，粉糯沙甜，适合多种烹饪",
                "地理标志", "B20260717", "黄土高原种植，有机肥", "重金属检测合格");

            // 禽蛋肉类
            createProduct("土鸡蛋", "禽蛋", "湖南省浏阳市", "30枚/板", "板", 1.50, 1000,
                "农家散养土鸡蛋，无抗生素，蛋黄饱满",
                null, "B20260704", "散养鸡，五谷喂养", "无抗生素检测");
            

            // 茶叶类
            createProduct("安溪铁观音", "茶叶", "福建省安溪县", "250g/盒", "盒", 128.00, 100,
                "兰花香，回甘持久，传统工艺制作",
                "地理标志", "B20260705", "传统工艺制作", "符合GB/T 19598标准");

            // 其他
            createProduct("有机沙拉菜", "蔬菜", "云南省昆明市", "500g/盒", "盒", 8.80, 220,
                "混合沙拉蔬菜，有机种植，鲜嫩可口",
                "有机认证", "B20260719", "有机基质种植，无土栽培", "零农残检测");
        }

        // Seed orders
        if (orderRepository.count() == 0) {
            log.info("初始化订单数据...");
            createOrder("ORD20260714001", 3L, 1L, 3, BigDecimal.valueOf(29.97), "COMPLETED", "北京市朝阳区建国路88号", "SF1234567890", "顺丰速运", "新鲜好吃，请尽快发货");
            createOrder("ORD20260714002", 3L, 3L, 2, BigDecimal.valueOf(11.98), "SHIPPED", "北京市朝阳区建国路88号", "SF1234567891", "顺丰速运", "要熟透的");
            createOrder("ORD20260714003", 2L, 1L, 1, BigDecimal.valueOf(9.99), "COMPLETED", "上海市浦东新区陆家嘴100号", "YT9876543210", "圆通快递", "");
            createOrder("ORD20260714004", 2L, 5L, 4, BigDecimal.valueOf(34.00), "SHIPPED", "上海市浦东新区陆家嘴100号", "YT9876543211", "圆通快递", "送前电话联系");
            createOrder("ORD20260714005", 2L, 9L, 2, BigDecimal.valueOf(11.98), "PENDING", "广东省深圳市南山区科技园路50号", null, null, "周末配送");
            createOrder("ORD20260714006", 3L, 7L, 1, BigDecimal.valueOf(10.00), "PENDING", "浙江省杭州市西湖区文三路200号", null, null, "");
            
            createOrder("ORD20260714008", 2L, 15L, 2, BigDecimal.valueOf(60.00), "SHIPPED", "江苏省南京市鼓楼区中山北路128号", "SF1234567892", "顺丰速运", "送父母，请包装好");
            
            createOrder("ORD20260714010", 2L, 18L, 3, BigDecimal.valueOf(89.70), "COMPLETED", "天津市和平区南京路88号", "YT9876543212", "圆通快递", "");
            
            createOrder("ORD20260714012", 2L, 6L, 2, BigDecimal.valueOf(36.00), "SHIPPED", "陕西省西安市雁塔区科技二路68号", "JD1112223331", "京东物流", "");

            log.info("订单数据初始化完成：9条订单");
        }

        if (traceRepository.count() == 0) {
            log.info("初始化溯源数据...");
            // 溯源记录 1: 红富士苹果
            TraceRecord tr1 = createTrace("TRC20260714001", "B20260701", "红富士苹果", "山东省烟台市", "CQ20260701001", true, "0x7a3f9c2b8e1d5f4a", 128);
            createStage(tr1, "种植", "春季修剪、施有机肥，生物防虫", "烟台栖霞果园", "张农户", LocalDateTime.of(2026,3,15,8,0), 1);
            createStage(tr1, "开花授粉", "苹果花盛开，蜜蜂自然授粉", "烟台栖霞果园", "自然授粉", LocalDateTime.of(2026,4,10,9,0), 2);
            createStage(tr1, "疏果套袋", "人工疏果，套袋保护果实", "烟台栖霞果园", "李工人", LocalDateTime.of(2026,5,20,7,30), 3);
            createStage(tr1, "采摘分选", "人工采摘，果径80mm以上精选", "烟台栖霞果园", "王包装", LocalDateTime.of(2026,6,10,6,0), 4);
            createStage(tr1, "农残检测", "符合GB 2763-2021标准，全部合格", "烟台质检中心", "质检中心", LocalDateTime.of(2026,6,12,10,0), 5);
            createStage(tr1, "冷链包装", "冷链保鲜包装，贴溯源码", "烟台冷链仓", "物流部", LocalDateTime.of(2026,6,13,14,0), 6);
            createStage(tr1, "出库发货", "冷链物流发出，全程温控4℃", "烟台冷链仓", "物流部", LocalDateTime.of(2026,6,14,8,0), 7);

            // 溯源记录 2: 五常大米
            TraceRecord tr2 = createTrace("TRC20260714002", "B20260702", "五常大米", "黑龙江省五常市", "CQ20260702001", true, "0x8b4d1e2f3a6c7b9d", 56);
            createStage(tr2, "育苗", "大棚育苗，精选稻种", "五常育苗基地", "赵农艺师", LocalDateTime.of(2026,3,1,8,0), 1);
            createStage(tr2, "机械插秧", "机械化插秧，株行距标准", "五常稻田", "赵农户", LocalDateTime.of(2026,4,20,6,0), 2);
            createStage(tr2, "田间管理", "有机施肥、人工除草、生物防虫", "五常稻田", "赵农户", LocalDateTime.of(2026,5,15,7,0), 3);
            createStage(tr2, "收割", "机械化收割，稻谷成熟度95%", "五常稻田", "收割队", LocalDateTime.of(2026,7,10,5,0), 4);
            createStage(tr2, "加工", "脱壳、碾米、色选、真空包装", "五常加工厂", "加工车间", LocalDateTime.of(2026,7,15,9,0), 5);
            createStage(tr2, "质检", "重金属、农残检测全部合格", "五常质检中心", "质检中心", LocalDateTime.of(2026,7,18,10,0), 6);

            // 溯源记录 3: 有机西红柿
            TraceRecord tr3 = createTrace("TRC20260714003", "B20260703", "有机西红柿", "山东省寿光市", "CQ20260703001", true, "0x3c8a5f1d9b2e6a4c", 89);
            createStage(tr3, "育苗", "穴盘育苗，有机基质", "寿光育苗中心", "刘技术员", LocalDateTime.of(2026,2,20,8,0), 1);
            createStage(tr3, "定植", "大棚定植，滴灌系统", "寿光蔬菜基地", "刘农户", LocalDateTime.of(2026,3,15,7,0), 2);
            createStage(tr3, "授粉管理", "熊蜂授粉，温湿度智能监控", "寿光蔬菜基地", "智能系统", LocalDateTime.of(2026,4,10,9,0), 3);
            createStage(tr3, "采收", "人工采收，成熟度8成", "寿光蔬菜基地", "采收队", LocalDateTime.of(2026,5,20,6,0), 4);
            createStage(tr3, "农残检测", "零农残检测，符合有机标准", "寿光质检中心", "质检中心", LocalDateTime.of(2026,5,22,10,0), 5);
            createStage(tr3, "包装发货", "气调保鲜包装，冷链配送", "寿光冷链仓", "物流部", LocalDateTime.of(2026,5,23,8,0), 6);

            // 溯源记录 4: 丹东草莓
            TraceRecord tr4 = createTrace("TRC20260714004", "B20260707", "丹东草莓", "辽宁省丹东市", "CQ20260707001", true, "0x5d2b8e4a1f9c3a7b", 45);
            createStage(tr4, "育苗", "脱毒组培苗，温室培育", "丹东育苗中心", "孙技术员", LocalDateTime.of(2026,1,10,8,0), 1);
            createStage(tr4, "定植", "大棚定植，高畦栽培", "丹东草莓基地", "孙农户", LocalDateTime.of(2026,2,1,7,0), 2);
            createStage(tr4, "花期管理", "蜜蜂授粉，水肥一体化", "丹东草莓基地", "孙农户", LocalDateTime.of(2026,3,5,9,0), 3);
            createStage(tr4, "采收", "人工采摘，果型整齐", "丹东草莓基地", "采收队", LocalDateTime.of(2026,4,15,6,0), 4);
            createStage(tr4, "质检", "零农残检测，符合绿色食品标准", "丹东质检中心", "质检中心", LocalDateTime.of(2026,4,17,10,0), 5);
            createStage(tr4, "冷链配送", "泡沫箱+冰袋，顺丰冷链", "丹东冷链仓", "物流部", LocalDateTime.of(2026,4,18,8,0), 6);



                        // 溯源记录 5: 海南芒果
            TraceRecord tr5 = createTrace("TRC20260714005", "B20260706", "海南芒果", "海南省三亚市", "CQ20260706001", true, "0x2f8a1c4d7e9b3a5f", 32);
            createStage(tr5, "育苗嫁接", "优质金煌芒种苗，标准化嫁接", "三亚育苗基地", "陈技术员", LocalDateTime.of(2026,2,10,8,0), 1);
            createStage(tr5, "花期管理", "人工授粉，疏花疏果", "三亚芒果园", "陈农户", LocalDateTime.of(2026,3,5,7,0), 2);
            createStage(tr5, "套袋保护", "防虫防病套袋，减少农药使用", "三亚芒果园", "陈农户", LocalDateTime.of(2026,4,15,9,0), 3);
            createStage(tr5, "采摘分选", "8成熟以上采摘，果形端正", "三亚芒果园", "采收队", LocalDateTime.of(2026,5,20,6,0), 4);
            createStage(tr5, "质检", "符合绿色食品标准，农残合格", "三亚质检中心", "质检中心", LocalDateTime.of(2026,5,22,10,0), 5);
            createStage(tr5, "冷链发货", "泡沫箱包装，空运直达", "三亚冷链仓", "物流部", LocalDateTime.of(2026,5,23,8,0), 6);

            // 溯源记录 6: 新疆葡萄
            TraceRecord tr6 = createTrace("TRC20260714006", "B20260708", "新疆葡萄", "新疆吐鲁番", "CQ20260708001", true, "0x7c3f5a9b1d8e2f4c", 41);
            createStage(tr6, "开墩上架", "春季开墩，葡萄藤上架", "吐鲁番葡萄园", "阿布都农户", LocalDateTime.of(2026,3,1,8,0), 1);
            createStage(tr6, "灌溉管理", "坎儿井水灌溉，滴灌技术", "吐鲁番葡萄园", "阿布都农户", LocalDateTime.of(2026,4,10,7,0), 2);
            createStage(tr6, "疏果整穗", "人工疏果，保证果粒均匀", "吐鲁番葡萄园", "阿布都农户", LocalDateTime.of(2026,5,20,6,0), 3);
            createStage(tr6, "成熟采收", "糖度22%以上采摘，清晨采收", "吐鲁番葡萄园", "采收队", LocalDateTime.of(2026,6,25,5,0), 4);
            createStage(tr6, "质检分级", "果粒大小分级，无农药残留", "吐鲁番质检中心", "质检中心", LocalDateTime.of(2026,6,27,10,0), 5);
            createStage(tr6, "冷链配送", "冷链运输，48小时直达", "吐鲁番冷链仓", "物流部", LocalDateTime.of(2026,6,28,8,0), 6);

            // 溯源记录 7: 赣南脐橙
            TraceRecord tr7 = createTrace("TRC20260714007", "B20260709", "赣南脐橙", "江西省赣州市", "CQ20260709001", true, "0x4e6b8d2a1f3c5a7e", 67);
            createStage(tr7, "春季修剪", "整形修剪，通风透光", "赣州脐橙基地", "钟农户", LocalDateTime.of(2026,3,10,8,0), 1);
            createStage(tr7, "水肥管理", "有机肥为主，滴灌施肥", "赣州脐橙基地", "钟农户", LocalDateTime.of(2026,4,15,7,0), 2);
            createStage(tr7, "病虫害防治", "生物防治为主，物理诱捕", "赣州脐橙基地", "钟农户", LocalDateTime.of(2026,5,20,6,0), 3);
            createStage(tr7, "成熟采收", "果皮转黄，酸甜比最佳时采收", "赣州脐橙基地", "采收队", LocalDateTime.of(2026,6,10,6,0), 4);
            createStage(tr7, "清洗分选", "清洗打蜡，果径分级", "赣州加工厂", "加工车间", LocalDateTime.of(2026,6,12,9,0), 5);
            createStage(tr7, "质检发货", "符合地理标志产品标准", "赣州质检中心", "质检中心", LocalDateTime.of(2026,6,14,10,0), 6);

            // 溯源记录 8: 无锡水蜜桃
            TraceRecord tr8 = createTrace("TRC20260714008", "B20260710", "无锡水蜜桃", "江苏省无锡市", "CQ20260710001", true, "0x9d1b5f3c7a8e2d4b", 53);
            createStage(tr8, "冬季修剪", "开心形修剪，控制树势", "阳山桃园", "钱农户", LocalDateTime.of(2026,1,15,8,0), 1);
            createStage(tr8, "疏花疏果", "人工疏花，每枝留1-2果", "阳山桃园", "钱农户", LocalDateTime.of(2026,3,20,7,0), 2);
            createStage(tr8, "套袋管理", "专用果袋套袋，防虫防病", "阳山桃园", "钱农户", LocalDateTime.of(2026,4,10,6,0), 3);
            createStage(tr8, "成熟采收", "8成熟采摘，轻拿轻放", "阳山桃园", "采收队", LocalDateTime.of(2026,6,5,5,0), 4);
            createStage(tr8, "质检分级", "单果重250g以上，糖度检测", "无锡质检中心", "质检中心", LocalDateTime.of(2026,6,7,10,0), 5);
            createStage(tr8, "冷链配送", "单果独立网套，冷链空运", "无锡冷链仓", "物流部", LocalDateTime.of(2026,6,8,8,0), 6);

            // 溯源记录 9: 库尔勒香梨
            TraceRecord tr9 = createTrace("TRC20260714009", "B20260711", "库尔勒香梨", "新疆库尔勒", "CQ20260711001", true, "0x3a7c5e9f1b2d8f6a", 38);
            createStage(tr9, "萌芽管理", "春季修剪，促进花芽分化", "库尔勒梨园", "艾力农户", LocalDateTime.of(2026,3,5,8,0), 1);
            createStage(tr9, "花期授粉", "蜜蜂授粉，人工辅助", "库尔勒梨园", "艾力农户", LocalDateTime.of(2026,4,1,7,0), 2);
            createStage(tr9, "肥水管理", "天山雪水灌溉，有机施肥", "库尔勒梨园", "艾力农户", LocalDateTime.of(2026,5,10,6,0), 3);
            createStage(tr9, "成熟采收", "果皮转黄，香气浓郁时采收", "库尔勒梨园", "采收队", LocalDateTime.of(2026,6,20,6,0), 4);
            createStage(tr9, "分选包装", "按果径分级，独立包装", "库尔勒加工厂", "包装车间", LocalDateTime.of(2026,6,22,9,0), 5);
            createStage(tr9, "质检发货", "地理标志产品认证合格", "库尔勒质检中心", "质检中心", LocalDateTime.of(2026,6,24,10,0), 6);

            // 溯源记录 10: 有机土豆
            TraceRecord tr10 = createTrace("TRC20260714010", "B20260717", "有机土豆", "甘肃省定西市", "CQ20260717001", true, "0x8b2d6f4a1c9e3a7b", 26);
            createStage(tr10, "选种切块", "精选脱毒种薯，科学切块", "定西种植基地", "马农户", LocalDateTime.of(2026,3,1,8,0), 1);
            createStage(tr10, "播种覆膜", "机械化播种，覆膜保墒", "定西种植基地", "马农户", LocalDateTime.of(2026,3,20,7,0), 2);
            createStage(tr10, "田间管理", "中耕培土，有机追肥", "定西种植基地", "马农户", LocalDateTime.of(2026,5,1,6,0), 3);
            createStage(tr10, "收获", "机械化收获，人工分拣", "定西种植基地", "收获队", LocalDateTime.of(2026,6,25,5,0), 4);
            createStage(tr10, "质检", "重金属检测合格，淀粉含量达标", "定西质检中心", "质检中心", LocalDateTime.of(2026,6,28,10,0), 5);
            createStage(tr10, "仓储发货", "恒温库存储，分级包装发货", "定西仓储中心", "物流部", LocalDateTime.of(2026,7,1,9,0), 6);

            // 溯源记录 11: 甜玉米
            TraceRecord tr11 = createTrace("TRC20260714011", "B20260716", "甜玉米", "吉林省公主岭市", "CQ20260716001", true, "0x5f1a8c3e7b9d2f4a", 44);
            createStage(tr11, "整地播种", "深耕整地，机械化精量播种", "公主岭种植基地", "杨农户", LocalDateTime.of(2026,3,10,8,0), 1);
            createStage(tr11, "苗期管理", "间苗定苗，中耕除草", "公主岭种植基地", "杨农户", LocalDateTime.of(2026,4,5,7,0), 2);
            createStage(tr11, "水肥管理", "滴灌追肥，增施有机肥", "公主岭种植基地", "杨农户", LocalDateTime.of(2026,5,10,6,0), 3);
            createStage(tr11, "适时采收", "乳熟期采收，含糖量最高", "公主岭种植基地", "采收队", LocalDateTime.of(2026,6,15,5,0), 4);
            createStage(tr11, "质检", "非转基因检测合格，农残未检出", "吉林质检中心", "质检中心", LocalDateTime.of(2026,6,17,10,0), 5);
            createStage(tr11, "冷链发货", "带皮冷链运输，锁住鲜甜", "公主岭冷链仓", "物流部", LocalDateTime.of(2026,6,18,8,0), 6);

            // 溯源记录 12: 土鸡蛋
            TraceRecord tr12 = createTrace("TRC20260714012", "B20260704", "土鸡蛋", "湖南省浏阳市", "CQ20260704001", true, "0x1d9e3b7f5c2a8f6e", 92);
            createStage(tr12, "鸡苗选育", "本地土鸡品种，健康鸡苗", "浏阳散养基地", "黄农户", LocalDateTime.of(2026,1,10,8,0), 1);
            createStage(tr12, "散养管理", "山林散养，自由觅食", "浏阳散养基地", "黄农户", LocalDateTime.of(2026,2,1,7,0), 2);
            createStage(tr12, "饲料管理", "五谷杂粮为主，辅以青饲料", "浏阳散养基地", "黄农户", LocalDateTime.of(2026,3,1,6,0), 3);
            createStage(tr12, "产蛋收集", "每日定时收蛋，分类存放", "浏阳散养基地", "黄农户", LocalDateTime.of(2026,4,1,7,0), 4);
            createStage(tr12, "清洗分选", "温水清洗，光照检测，按大小分级", "浏阳加工中心", "加工车间", LocalDateTime.of(2026,4,2,9,0), 5);
            createStage(tr12, "质检发货", "无抗生素检测合格，新鲜度达标", "浏阳质检中心", "质检中心", LocalDateTime.of(2026,4,3,10,0), 6);

            // 溯源记录 13: 安溪铁观音
            TraceRecord tr13 = createTrace("TRC20260714013", "B20260705", "安溪铁观音", "福建省安溪县", "CQ20260705001", true, "0x6c4a2f8e1b9d3a7f", 75);
            createStage(tr13, "茶园管理", "高山茶园，云雾滋养，有机施肥", "安溪感德茶园", "林茶农", LocalDateTime.of(2026,2,20,8,0), 1);
            createStage(tr13, "采摘", "一芽两叶，清晨手工采摘", "安溪感德茶园", "采茶队", LocalDateTime.of(2026,4,10,5,0), 2);
            createStage(tr13, "晒青", "日光萎凋，散发青草气", "安溪制茶坊", "制茶师", LocalDateTime.of(2026,4,10,10,0), 3);
            createStage(tr13, "摇青发酵", "传统摇青工艺，半发酵", "安溪制茶坊", "制茶师", LocalDateTime.of(2026,4,11,8,0), 4);
            createStage(tr13, "炒青烘焙", "高温杀青，炭火烘焙定型", "安溪制茶坊", "制茶师", LocalDateTime.of(2026,4,12,9,0), 5);
            createStage(tr13, "质检包装", "符合GB/T 19598标准，真空包装", "安溪质检中心", "质检中心", LocalDateTime.of(2026,4,15,10,0), 6);

            // 溯源记录 14: 有机沙拉菜
            TraceRecord tr14 = createTrace("TRC20260714014", "B20260719", "有机沙拉菜", "云南省昆明市", "CQ20260719001", true, "0xae2b5d7f1c3a9e6b", 29);
            createStage(tr14, "基质准备", "有机椰糠基质，高温消毒", "昆明植物工厂", "李技术员", LocalDateTime.of(2026,2,1,8,0), 1);
            createStage(tr14, "播种育苗", "穴盘播种，LED补光育苗", "昆明植物工厂", "李技术员", LocalDateTime.of(2026,2,10,7,0), 2);
            createStage(tr14, "水培管理", "营养液循环，温湿度智能控制", "昆明植物工厂", "智能系统", LocalDateTime.of(2026,3,1,6,0), 3);
            createStage(tr14, "采收", "无菌采收，活体包装", "昆明植物工厂", "采收队", LocalDateTime.of(2026,4,5,6,0), 4);
            createStage(tr14, "质检", "零农残，微生物指标合格", "昆明质检中心", "质检中心", LocalDateTime.of(2026,4,7,10,0), 5);
            createStage(tr14, "冷链配送", "气调包装，全程冷链2-6℃", "昆明冷链仓", "物流部", LocalDateTime.of(2026,4,8,8,0), 6);

            log.info("溯源数据初始化完成：14条记录，85个环节");
        }
    }

    private void createUser(String username, String password, String nickname, String role, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setRole(role);
        user.setPhone(phone);
        user.setEnabled(true);
        userRepository.save(user);
    }

    private void createProduct(String name, String category, String origin, String spec, String unit,
                                double price, int stock, String desc, String cert, String batch,
                                String farming, String testReport) {
        Product p = new Product();
        p.setName(name);
        p.setCategory(category);
        p.setOrigin(origin);
        p.setSpecifications(spec);
        p.setUnit(unit);
        p.setPrice(BigDecimal.valueOf(price));
        p.setStock(stock);
        p.setDescription(desc);
        p.setCertificationLabel(cert);
        p.setBatchNo(batch);
        p.setFarmingRecord(farming);
        p.setTestReport(testReport);
        p.setFarmerId(1L);
        p.setPublished(true);
        p.setEnabled(true);
        productRepository.save(p);
    }

    private TraceRecord createTrace(String code, String batch, String name, String origin, String certNo, boolean certValid, String txHash, int scans) {
        TraceRecord tr = new TraceRecord();
        tr.setTraceCode(code);
        tr.setBatchNo(batch);
        tr.setProductName(name);
        tr.setOrigin(origin);
        tr.setCertNo(certNo);
        tr.setCertStatus(certValid ? "valid" : "invalid");
        tr.setTxHash(txHash);
        tr.setScanCount(scans);
        tr.setValid(true);
        return traceRepository.save(tr);
    }

    private void createStage(TraceRecord trace, String name, String desc, String location, String operator, LocalDateTime time, int order) {
        TraceStage stage = new TraceStage();
        stage.setTraceId(trace.getId());
        stage.setStageName(name);
        stage.setDescription(desc);
        stage.setLocation(location);
        stage.setOperator(operator);
        stage.setStageTime(time);
        stage.setSortOrder(order);
        stageRepository.save(stage);
    }

    private void createOrder(String orderNo, Long userId, Long productId, int quantity, BigDecimal totalPrice,
                              String status, String address, String trackingNo, String logistics, String remark) {
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(status);
        order.setShippingAddress(address);
        order.setTrackingNo(trackingNo);
        order.setLogisticsCompany(logistics);
        order.setRemark(remark);
        orderRepository.save(order);
    }
}