# -*- coding: utf-8 -*-
"""批量导入农产品数据到 SQLite"""
import sqlite3
import random
from datetime import datetime, timedelta

DB_PATH = r"E:\Grouptest_git\agriculture-trace-platform\data\trace.db"

conn = sqlite3.connect(DB_PATH)
cur = conn.cursor()

now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

def add_product(name, category, origin, spec, price, batch_no, trace_level="AA", stock=None):
    # 去重：检查批次号是否已存在
    cur.execute("SELECT COUNT(*) FROM batch WHERE batch_no=?", (batch_no,))
    if cur.fetchone()[0] > 0:
        print(f"  跳过已存在的批次: {batch_no}")
        return
    stock = stock or random.randint(100, 800)
    desc = f"{origin}{name}，全程溯源可查，品质保障。"
    cur.execute(
        "INSERT INTO product (name, category, origin, spec, price, stock, batch_no, description, trace_level, status, create_time, update_time) VALUES (?,?,?,?,?,?,?,?,?,1,?,?)",
        (name, category, origin, spec, price, stock, batch_no, desc, trace_level, now, now)
    )
    product_id = cur.lastrowid
    
    # 批次
    produce_date = (datetime.now() - timedelta(days=random.randint(30, 180))).strftime("%Y-%m-%d")
    harvest_date = (datetime.now() - timedelta(days=random.randint(1, 15))).strftime("%Y-%m-%d")
    quantity = random.randint(500, 5000)
    farm_address = f"{origin}生产基地"
    farm_area = f"{random.randint(20, 100)}亩"
    soil_types = ["棕壤土", "红壤土", "黑土", "沙壤土", "黄壤土"]
    soil_type = random.choice(soil_types)
    cur.execute(
        "INSERT INTO batch (batch_no, product_id, produce_date, harvest_date, quantity, farm_address, farm_area, soil_type, create_time) VALUES (?,?,?,?,?,?,?,?,?)",
        (batch_no, product_id, produce_date, harvest_date, quantity, farm_address, farm_area, soil_type, now)
    )
    batch_id = cur.lastrowid
    
    # 农事记录
    records = [
        ("种植", (datetime.now() - timedelta(days=random.randint(90, 120))).strftime("%Y-%m-%d"), f"完成{name}种苗定植"),
        ("施肥", (datetime.now() - timedelta(days=random.randint(45, 60))).strftime("%Y-%m-%d"), "施用有机肥500kg/亩"),
        ("灌溉", (datetime.now() - timedelta(days=random.randint(20, 30))).strftime("%Y-%m-%d"), "智能滴灌系统灌溉"),
        ("采收", (datetime.now() - timedelta(days=random.randint(3, 7))).strftime("%Y-%m-%d"), "人工挑选采收，分级包装"),
        ("加工", (datetime.now() - timedelta(days=random.randint(1, 3))).strftime("%Y-%m-%d"), "清洗、分拣、称重、冷链包装"),
    ]
    for rtype, rdate, rcontent in records:
        cur.execute(
            "INSERT INTO trace_record (batch_id, record_type, record_time, content, create_time) VALUES (?,?,?,?,?)",
            (batch_id, rtype, rdate, rcontent, now)
        )
    
    # 农残检测
    items = [("毒死蜱", "0.05"), ("多菌灵", "0.10"), ("氯氰菊酯", "0.20"), ("吡虫啉", "0.05"), ("啶虫脒", "0.05")]
    for item, limit in random.sample(items, random.randint(3, 4)):
        result = "未检出" if random.random() < 0.85 else f"{random.uniform(0.005, float(limit)*0.8):.3f}"
        cur.execute(
            "INSERT INTO pesticide_report (batch_id, report_no, test_date, test_organization, item_name, result, standard_limit, unit, is_compliant, create_time) VALUES (?,?,?,?,?,?,?,?,1,?)",
            (batch_id, f"REP-{random.randint(10000, 99999)}", harvest_date, "农业农村部农产品质量监督检验测试中心", item, result, limit, "mg/kg", now)
        )
    
    return product_id

# ===== 水果 =====
fruits = [
    ("山东红富士苹果", "水果", "山东烟台", "5kg/箱", 49.90, "BATCH-F-001"),
    ("新疆库尔勒香梨", "水果", "新疆库尔勒", "4kg/箱", 39.90, "BATCH-F-002"),
    ("四川爱媛果冻橙", "水果", "四川眉山", "5kg/箱", 45.00, "BATCH-F-003"),
    ("广西武鸣沃柑", "水果", "广西武鸣", "5kg/箱", 35.90, "BATCH-F-004"),
    ("海南金钻凤梨", "水果", "海南海口", "2个/盒", 29.90, "BATCH-F-005"),
    ("云南冰糖橙", "水果", "云南玉溪", "5kg/箱", 38.00, "BATCH-F-006"),
    ("陕西洛川苹果", "水果", "陕西延安", "5kg/箱", 42.00, "BATCH-F-007"),
    ("浙江仙居杨梅", "水果", "浙江台州", "3kg/箱", 68.00, "BATCH-F-008"),
    ("广东桂味荔枝", "水果", "广东茂名", "3kg/箱", 55.00, "BATCH-F-009"),
    ("福建平和蜜柚", "水果", "福建漳州", "2个/袋", 25.90, "BATCH-F-010"),
    ("新疆哈密瓜", "水果", "新疆吐鲁番", "1个/箱", 32.00, "BATCH-F-011"),
    ("辽宁巨峰葡萄", "水果", "辽宁大连", "2.5kg/箱", 48.00, "BATCH-F-012"),
    ("河北赵县雪梨", "水果", "河北赵县", "5kg/箱", 28.00, "BATCH-F-013"),
    ("江西赣南脐橙", "水果", "江西赣州", "5kg/箱", 42.00, "BATCH-F-014"),
    ("甘肃花牛苹果", "水果", "甘肃天水", "5kg/箱", 36.00, "BATCH-F-015"),
    ("广西百香果", "水果", "广西玉林", "2.5kg/箱", 33.00, "BATCH-F-016"),
    ("海南三亚芒果", "水果", "海南三亚", "3kg/箱", 45.00, "BATCH-F-017"),
    ("山东大樱桃", "水果", "山东烟台", "2kg/箱", 78.00, "BATCH-F-018"),
    ("福建东魁杨梅", "水果", "福建龙海", "2.5kg/箱", 58.00, "BATCH-F-019"),
    ("云南澄江蓝莓", "水果", "云南澄江", "12盒/箱", 88.00, "BATCH-F-020"),
    ("海南凤梨释迦", "水果", "海南乐东", "2个/盒", 69.00, "BATCH-F-021"),
]

# ===== 蔬菜 =====
vegetables = [
    ("云南有机蔬菜礼盒", "蔬菜", "云南昆明", "3kg/盒", 68.00, "BATCH-V-001"),
    ("山东章丘大葱", "蔬菜", "山东济南", "2kg/把", 12.90, "BATCH-V-002"),
    ("河南铁棍山药", "蔬菜", "河南焦作", "2kg/箱", 28.00, "BATCH-V-003"),
    ("寿光新鲜黄瓜", "蔬菜", "山东潍坊", "1.5kg/袋", 9.90, "BATCH-V-004"),
    ("甘肃兰州百合", "蔬菜", "甘肃兰州", "500g/袋", 35.00, "BATCH-V-005"),
    ("福建古田鲜银耳", "蔬菜", "福建宁德", "300g/袋", 22.00, "BATCH-V-006"),
    ("四川儿菜", "蔬菜", "四川成都", "2kg/袋", 13.00, "BATCH-V-007"),
    ("东北黑木耳", "蔬菜", "黑龙江牡丹江", "250g/袋", 32.00, "BATCH-V-008"),
    ("云南香格里拉松茸", "蔬菜", "云南香格里拉", "500g/盒", 168.00, "BATCH-V-009"),
    ("湖南洞庭湖莲藕", "蔬菜", "湖南洞庭湖", "2.5kg/袋", 15.00, "BATCH-V-010"),
    ("海南有机番茄", "蔬菜", "海南三亚", "2kg/袋", 14.90, "BATCH-V-011"),
    ("山东紫甘蓝", "蔬菜", "山东临沂", "1个/袋", 6.50, "BATCH-V-012"),
    ("云南甜玉米", "蔬菜", "云南西双版纳", "5根/袋", 15.00, "BATCH-V-013"),
    ("河北平泉香菇", "蔬菜", "河北承德", "500g/盒", 18.00, "BATCH-V-014"),
    ("浙江杭州雷笋", "蔬菜", "浙江杭州", "1.5kg/袋", 22.00, "BATCH-V-015"),
    ("广东菜心", "蔬菜", "广东清远", "500g/把", 6.00, "BATCH-V-016"),
    ("宁夏菜心", "蔬菜", "宁夏银川", "500g/把", 7.50, "BATCH-V-017"),
    ("江苏徐州芦笋", "蔬菜", "江苏徐州", "500g/盒", 19.00, "BATCH-V-018"),
    ("山东有机土豆", "蔬菜", "山东滕州", "2.5kg/袋", 11.00, "BATCH-V-019"),
    ("湖南红菜苔", "蔬菜", "湖南长沙", "500g/把", 8.00, "BATCH-V-020"),
    ("福建姬松茸", "蔬菜", "福建古田", "300g/盒", 42.00, "BATCH-V-021"),
]

# ===== 粮食 =====
grains = [
    ("五常有机大米", "粮食", "黑龙江五常", "10kg/袋", 89.90, "BATCH-G-001"),
    ("黑龙江珍珠米", "粮食", "黑龙江佳木斯", "10kg/袋", 55.00, "BATCH-G-002"),
    ("吉林小町米", "粮食", "吉林长春", "10kg/袋", 62.00, "BATCH-G-003"),
    ("辽宁盘锦大米", "粮食", "辽宁盘锦", "10kg/袋", 58.00, "BATCH-G-004"),
    ("山西沁州黄小米", "粮食", "山西长治", "2.5kg/袋", 32.00, "BATCH-G-005"),
    ("内蒙古燕麦米", "粮食", "内蒙古赤峰", "2kg/袋", 28.00, "BATCH-G-006"),
    ("河北张家口藜麦", "粮食", "河北张北", "1kg/袋", 35.00, "BATCH-G-007"),
    ("云南红河紫米", "粮食", "云南红河", "2kg/袋", 38.00, "BATCH-G-008"),
    ("山东济宁黑米", "粮食", "山东济宁", "2.5kg/袋", 22.00, "BATCH-G-009"),
    ("河南周口面粉", "粮食", "河南周口", "5kg/袋", 25.00, "BATCH-G-010"),
    ("湖南湘西富硒米", "粮食", "湖南湘西", "5kg/袋", 45.00, "BATCH-G-011"),
    ("山东玉米糁", "粮食", "山东潍坊", "2kg/袋", 16.00, "BATCH-G-012"),
    ("陕西榆林荞麦", "粮食", "陕西榆林", "2kg/袋", 24.00, "BATCH-G-013"),
    ("东北有机黄豆", "粮食", "黑龙江黑河", "2.5kg/袋", 28.00, "BATCH-G-014"),
    ("内蒙古绿豆", "粮食", "内蒙古通辽", "2kg/袋", 22.00, "BATCH-G-015"),
    ("江西宜春红米", "粮食", "江西宜春", "2.5kg/袋", 30.00, "BATCH-G-016"),
    ("贵州黑糯米", "粮食", "贵州黔东南", "2.5kg/袋", 35.00, "BATCH-G-017"),
    ("安徽糯小米", "粮食", "安徽滁州", "2kg/袋", 26.00, "BATCH-G-018"),
    ("河北莜麦面", "粮食", "河北张家口", "2.5kg/袋", 22.00, "BATCH-G-019"),
    ("新疆鹰嘴豆", "粮食", "新疆木垒", "1kg/袋", 32.00, "BATCH-G-020"),
    ("西藏青稞米", "粮食", "西藏日喀则", "2kg/袋", 48.00, "BATCH-G-021"),
]

# ===== 茶叶 =====
teas = [
    ("安溪铁观音（有机）", "茶叶", "福建安溪", "250g/罐", 168.00, "BATCH-T-001"),
    ("西湖龙井", "茶叶", "浙江杭州", "250g/罐", 298.00, "BATCH-T-002"),
    ("云南普洱茶（生）", "茶叶", "云南西双版纳", "357g/饼", 125.00, "BATCH-T-003"),
    ("云南普洱茶（熟）", "茶叶", "云南临沧", "357g/饼", 138.00, "BATCH-T-004"),
    ("武夷山大红袍", "茶叶", "福建武夷山", "125g/罐", 198.00, "BATCH-T-005"),
    ("洞庭碧螺春", "茶叶", "江苏苏州", "250g/罐", 268.00, "BATCH-T-006"),
    ("黄山毛峰", "茶叶", "安徽黄山", "250g/罐", 188.00, "BATCH-T-007"),
    ("信阳毛尖", "茶叶", "河南信阳", "250g/罐", 158.00, "BATCH-T-008"),
    ("六安瓜片", "茶叶", "安徽六安", "250g/罐", 228.00, "BATCH-T-009"),
    ("君山银针", "茶叶", "湖南岳阳", "200g/盒", 268.00, "BATCH-T-010"),
    ("福鼎白毫银针", "茶叶", "福建福鼎", "200g/盒", 358.00, "BATCH-T-011"),
    ("安化黑茶", "茶叶", "湖南益阳", "500g/饼", 98.00, "BATCH-T-012"),
    ("祁门红茶", "茶叶", "安徽祁门", "250g/罐", 178.00, "BATCH-T-013"),
    ("正山小种", "茶叶", "福建武夷山", "250g/罐", 198.00, "BATCH-T-014"),
    ("金骏眉", "茶叶", "福建武夷山", "125g/罐", 388.00, "BATCH-T-015"),
    ("四川竹叶青", "茶叶", "四川峨眉山", "200g/盒", 218.00, "BATCH-T-016"),
    ("太平猴魁", "茶叶", "安徽黄山", "200g/盒", 248.00, "BATCH-T-017"),
    ("安吉白茶", "茶叶", "浙江安吉", "250g/罐", 198.00, "BATCH-T-018"),
    ("广西六堡茶", "茶叶", "广西梧州", "500g/饼", 108.00, "BATCH-T-019"),
    ("都匀毛尖", "茶叶", "贵州黔南", "250g/罐", 148.00, "BATCH-T-020"),
    ("台湾冻顶乌龙", "茶叶", "福建漳平", "250g/罐", 228.00, "BATCH-T-021"),
]

# ===== 畜禽 =====
meats = [
    ("黑山猪五花肉", "畜禽", "吉林长白山", "500g/盒", 32.00, "BATCH-L-001"),
    ("散养土鸡", "畜禽", "江西泰和", "1只约1.2kg", 68.00, "BATCH-L-002"),
    ("生态土鸡蛋", "畜禽", "湖南衡阳", "30枚/盒", 45.00, "BATCH-L-003"),
    ("宁夏盐池滩羊", "畜禽", "宁夏吴忠", "1kg/盒", 108.00, "BATCH-L-004"),
    ("内蒙古科尔沁牛", "畜禽", "内蒙古通辽", "1kg/盒", 88.00, "BATCH-L-005"),
    ("贵州散养鸭", "畜禽", "贵州六盘水", "1只约1.5kg", 56.00, "BATCH-L-006"),
    ("四川跑山猪排", "畜禽", "四川巴中", "500g/盒", 42.00, "BATCH-L-007"),
    ("安徽土鸭蛋", "畜禽", "安徽安庆", "20枚/盒", 36.00, "BATCH-L-008"),
    ("青海牦牛肉", "畜禽", "青海玉树", "1kg/盒", 128.00, "BATCH-L-009"),
    ("海南文昌鸡", "畜禽", "海南文昌", "1只约1kg", 72.00, "BATCH-L-010"),
    ("东北笨鹅", "畜禽", "黑龙江绥化", "1只约2kg", 88.00, "BATCH-L-011"),
    ("浙江湖羊", "畜禽", "浙江湖州", "1kg/盒", 98.00, "BATCH-L-012"),
    ("云南乌骨鸡", "畜禽", "云南楚雄", "1只约1kg", 62.00, "BATCH-L-013"),
    ("山东乳鸽", "畜禽", "山东济南", "2只/盒", 48.00, "BATCH-L-014"),
    ("广西巴马香猪", "畜禽", "广西河池", "1kg/盒", 78.00, "BATCH-L-015"),
    ("西藏藏香猪", "畜禽", "西藏林芝", "1kg/盒", 158.00, "BATCH-L-016"),
    ("高邮咸鸭蛋", "畜禽", "江苏高邮", "20枚/盒", 42.00, "BATCH-L-017"),
    ("福建河田鸡", "畜禽", "福建龙岩", "1只约1.2kg", 65.00, "BATCH-L-018"),
    ("陕西秦川牛肉", "畜禽", "陕西宝鸡", "1kg/盒", 82.00, "BATCH-L-019"),
    ("湖北洪湖鸭", "畜禽", "湖北洪湖", "1只约1.5kg", 52.00, "BATCH-L-020"),
    ("广东清远鸡", "畜禽", "广东清远", "1只约1.2kg", 58.00, "BATCH-L-021"),
]

all_products = fruits + vegetables + grains + teas + meats
print(f"共 {len(all_products)} 个商品，开始导入...")

for i, p in enumerate(all_products):
    add_product(*p)
    if (i+1) % 20 == 0:
        conn.commit()
        print(f"  已导入 {i+1}/{len(all_products)}...")

conn.commit()

# 验证
cur.execute("SELECT category, COUNT(*) FROM product GROUP BY category")
rows = cur.fetchall()
print(f"\n导入完成！各品类数量:")
total = 0
for cat, cnt in rows:
    print(f"  {cat}: {cnt} 种")
    total += cnt
print(f"  总计: {total} 种")

conn.close()
