package com.agritrace.config;

import com.agritrace.module.mall.entity.Product;
import com.agritrace.module.mall.repository.ProductRepository;
import com.agritrace.module.user.entity.User;
import com.agritrace.module.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(ProductRepository productRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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
            log.info("初始化演示商品...");
            createProduct("红富士苹果", "水果", "山东省烟台市", "5kg/箱", "斤", 9.99, 500,
                "烟台红富士，脆甜多汁，有机种植", "有机认证", "B20260701",
                "施用有机肥，生物防治病虫害", "农残检测全部合格，符合GB 2763标准");
            createProduct("五常大米", "粮油", "黑龙江省五常市", "10kg/袋", "袋", 29.90, 200,
                "正宗五常稻花香，有机种植", "地理标志", "B20260702",
                "有机种植，人工除草", "重金属检测合格");
            createProduct("有机西红柿", "蔬菜", "山东省寿光市", "1kg/盒", "盒", 5.99, 300,
                "沙瓤多汁，酸甜可口，熊蜂授粉", "绿色食品", "B20260703",
                "大棚种植，熊蜂授粉", "零农残检测报告");
            createProduct("土鸡蛋", "畜禽", "湖南省浏阳市", "30枚/板", "枚", 1.50, 1000,
                "农家散养土鸡蛋，无抗生素", null, "B20260704",
                "散养鸡，五谷喂养", "无抗生素检测");
            createProduct("安溪铁观音", "茶叶", "福建省安溪县", "250g/盒", "盒", 128.00, 100,
                "兰花香，回甘持久，传统工艺", "地理标志", "B20260705",
                "传统工艺制作", "符合GB/T 19598标准");
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
        p.setPrice(java.math.BigDecimal.valueOf(price));
        p.setStock(stock);
        p.setDescription(desc);
        p.setCertificationLabel(cert);
        p.setBatchNo(batch);
        p.setFarmingRecord(farming);
        p.setTestReport(testReport);
        p.setPublished(true);
        productRepository.save(p);
    }
}