package com.agri.trace.config;

import com.agri.trace.entity.*;
import com.agri.trace.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private BatchMapper batchMapper;
    @Autowired
    private TraceRecordMapper traceRecordMapper;
    @Autowired
    private PesticideReportMapper pesticideReportMapper;
    @Autowired
    private KnowledgeDocMapper knowledgeDocMapper;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 建表
        try {
            ClassPathResource resource = new ClassPathResource("schema.sql");
            String sql = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
            String[] statements = sql.split(";");
            for (String stmt : statements) {
                String trimmed = stmt.trim();
                if (!trimmed.isEmpty()) {
                    try { jdbcTemplate.execute(trimmed); } catch (Exception e) { log.debug("SQL忽略: {}", e.getMessage()); }
                }
            }
            log.info("数据库表结构初始化完成");
        } catch (Exception e) {
            log.warn("建表异常: {}", e.getMessage());
        }

        if (userMapper.selectCount(null) > 0) {
            log.info("已有数据，跳过初始化");
            return;
        }

        insertTestUsers();
        insertBaseProducts();
        insertKnowledgeDocs();
        log.info("测试数据初始化完成！");
    }

    private void insertTestUsers() {
        User admin = new User(); admin.setUsername("admin"); admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRealName("系统管理员"); admin.setRole("ADMIN"); admin.setCreateTime(LocalDateTime.now()); userMapper.insert(admin);
        User consumer = new User(); consumer.setUsername("user"); consumer.setPassword(passwordEncoder.encode("user123"));
        consumer.setRealName("测试消费者"); consumer.setRole("CONSUMER"); consumer.setCreateTime(LocalDateTime.now()); userMapper.insert(consumer);
        User producer = new User(); producer.setUsername("producer"); producer.setPassword(passwordEncoder.encode("prod123"));
        producer.setRealName("山东果园合作社"); producer.setRole("PRODUCER"); producer.setCreateTime(LocalDateTime.now()); userMapper.insert(producer);
        log.info("用户已创建: admin/admin123, user/user123, producer/prod123");
    }

    private void insertBaseProducts() {
        Product apple = new Product();
        apple.setName("山东红富士苹果"); apple.setCategory("水果"); apple.setOrigin("山东烟台");
        apple.setSpec("5kg/箱"); apple.setPrice(new BigDecimal("49.90")); apple.setStock(1000);
        apple.setBatchNo("BATCH-F-001"); apple.setDescription("山东烟台红富士苹果，色泽红润，口感脆甜。");
        apple.setTraceLevel("AAA"); apple.setStatus(1); apple.setCreateTime(LocalDateTime.now());
        productMapper.insert(apple);

        Batch b1 = new Batch();
        b1.setBatchNo("BATCH-F-001"); b1.setProductId(apple.getId());
        b1.setProduceDate(LocalDate.of(2026, 3, 15)); b1.setHarvestDate(LocalDate.of(2026, 6, 20));
        b1.setQuantity(5000); b1.setFarmAddress("山东省烟台市栖霞市蛇窝泊镇");
        b1.setFarmArea("50亩"); b1.setSoilType("棕壤土"); b1.setCreateTime(LocalDateTime.now());
        batchMapper.insert(b1);
        addRecord(b1.getId(),"种植","2026-03-15","完成苹果树春季修剪与施肥");
        addRecord(b1.getId(),"施肥","2026-04-10","施用有机肥500kg");
        addRecord(b1.getId(),"采收","2026-06-20","人工采摘分拣");
        addReport(b1.getId(),"毒死蜱","0.01","0.05","mg/kg",true);
        addReport(b1.getId(),"多菌灵","0.02","0.10","mg/kg",true);

        Product rice = new Product();
        rice.setName("五常有机大米"); rice.setCategory("粮食"); rice.setOrigin("黑龙江五常");
        rice.setSpec("10kg/袋"); rice.setPrice(new BigDecimal("89.90")); rice.setStock(500);
        rice.setBatchNo("BATCH-G-001"); rice.setDescription("黑龙江五常有机稻花香大米。");
        rice.setTraceLevel("AA"); rice.setStatus(1); rice.setCreateTime(LocalDateTime.now());
        productMapper.insert(rice);
        Batch b2 = new Batch();
        b2.setBatchNo("BATCH-G-001"); b2.setProductId(rice.getId());
        b2.setProduceDate(LocalDate.of(2026,4,1)); b2.setHarvestDate(LocalDate.of(2026,6,15));
        b2.setQuantity(3000); b2.setFarmAddress("黑龙江省五常市龙凤山镇");
        b2.setFarmArea("80亩"); b2.setSoilType("黑土"); b2.setCreateTime(LocalDateTime.now());
        batchMapper.insert(b2);
        addRecord(b2.getId(),"种植","2026-04-01","有机水稻种植");
        addRecord(b2.getId(),"采收","2026-06-15","联合收割机采收");
        addReport(b2.getId(),"毒死蜱","未检出","0.05","mg/kg",true);

        log.info("基础商品已创建");
    }

    private void addRecord(Long batchId, String type, String date, String content) {
        TraceRecord r = new TraceRecord();
        r.setBatchId(batchId); r.setRecordType(type);
        r.setRecordTime(LocalDate.parse(date));
        r.setContent(content); r.setCreateTime(LocalDateTime.now());
        traceRecordMapper.insert(r);
    }

    private void addReport(Long batchId, String item, String result, String limit, String unit, boolean ok) {
        PesticideReport r = new PesticideReport();
        r.setBatchId(batchId); r.setReportNo("REP-" + System.currentTimeMillis());
        r.setTestDate(LocalDate.now()); r.setTestOrganization("农业农村部农产品质检中心");
        r.setItemName(item); r.setResult(result); r.setStandardLimit(limit);
        r.setUnit(unit); r.setIsCompliant(ok); r.setCreateTime(LocalDateTime.now());
        pesticideReportMapper.insert(r);
    }

    private void insertKnowledgeDocs() {
        KnowledgeDoc d1 = new KnowledgeDoc();
        d1.setTitle("GB 2763-2021 食品中农药最大残留限量"); d1.setCategory("法规");
        d1.setContent("GB 2763-2021规定了食品中农药的最大残留限量。水果类：毒死蜱≤0.05mg/kg，多菌灵≤0.10mg/kg。蔬菜类：毒死蜱≤0.05mg/kg。粮食类：毒死蜱≤0.05mg/kg。茶叶类：联苯菊酯≤0.01mg/kg。");
        knowledgeDocMapper.insert(d1);

        KnowledgeDoc d2 = new KnowledgeDoc();
        d2.setTitle("GB 14881-2013 食品生产通用卫生规范"); d2.setCategory("法规");
        d2.setContent("GB 14881规定了食品生产过程中原料采购、加工、包装、贮存和运输等环节的卫生要求。");
        knowledgeDocMapper.insert(d2);
        log.info("知识库文档已创建");
    }
}
