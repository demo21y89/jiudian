-- V2: 种子数据 - 演示用农产品和溯源数据

-- 合格证数据
MERGE INTO certificates (cert_no, product_name, batch_no, producer, origin, statement, status)
KEY(cert_no)
VALUES ('CQ20260701001', '红富士苹果', 'B20260701', '老王果园', '山东省烟台市', '本生产者对产品质量安全及真实性负责，不使用禁用农药，符合食品安全国家标准。', 'VALID');

MERGE INTO certificates (cert_no, product_name, batch_no, producer, origin, statement, status)
KEY(cert_no)
VALUES ('CQ20260702001', '五常大米', 'B20260702', '五常市稻花香合作社', '黑龙江省五常市', '本生产者承诺产品质量安全，符合食品安全国家标准。', 'VALID');

-- 溯源记录
MERGE INTO trace_records (trace_code, batch_no, product_name, origin, cert_no, cert_valid, stages, tx_hash, scan_count)
KEY(trace_code)
VALUES ('TRC20260714001', 'B20260701', '红富士苹果', '山东省烟台市', 'CQ20260701001', TRUE,
'[{"stage":"种植","time":"2026-03-15","data":"春季修剪、施肥"},{"stage":"开花","time":"2026-04-10","data":"苹果花盛开期"},{"stage":"疏果","time":"2026-05-20","data":"人工疏果、套袋"},{"stage":"采收","time":"2026-07-01","data":"成熟采收、分拣"},{"stage":"加工","time":"2026-07-05","data":"清洗、分级、包装"},{"stage":"检测","time":"2026-07-08","data":"农残检测合格"}]',
'0x7a3f9c2b8e1d5f4a6c0b3e8d2f1a7c4b9e5d6f3a', 23);

MERGE INTO trace_records (trace_code, batch_no, product_name, origin, cert_no, cert_valid, stages, tx_hash, scan_count)
KEY(trace_code)
VALUES ('TRC20260713002', 'B20260702', '五常大米', '黑龙江省五常市', 'CQ20260702001', TRUE,
'[{"stage":"育苗","time":"2026-03-01","data":"大棚育苗"},{"stage":"插秧","time":"2026-04-20","data":"机械插秧"},{"stage":"田间管理","time":"2026-05-15","data":"有机施肥、人工除草"},{"stage":"收割","time":"2026-07-10","data":"机械收割"},{"stage":"加工","time":"2026-07-15","data":"脱壳、碾米、抛光、色选"},{"stage":"检测","time":"2026-07-18","data":"重金属检测合格"}]',
'0x8b4d1e2f3a6c7b9d0e5f1a2c3b4d5e6f7a8b9c0d', 15);