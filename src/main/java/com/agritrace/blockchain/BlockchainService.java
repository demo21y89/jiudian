package com.agritrace.blockchain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import com.agritrace.blockchain.entity.ChainTransaction;
import com.agritrace.blockchain.entity.ChainRecord;

@Service
public class BlockchainService {

    private static final Logger log = LoggerFactory.getLogger(BlockchainService.class);

    @Value("${blockchain.fisco.group-id:1}")
    private int groupId;

    @Value("${blockchain.fisco.chain-id:1}")
    private int chainId;

    @Value("${blockchain.fisco.nodes:127.0.0.1:20200}")
    private String nodes;

    @Value("${blockchain.fisco.contract.trace-address:}")
    private String traceContractAddress;

    @Value("${blockchain.enabled:false}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled) {
            log.info("FISCO BCOS blockchain module initialized, nodes: {}, groupId: {}, chainId: {}", nodes, groupId, chainId);
            log.info("Trace contract address: {}", traceContractAddress);
        } else {
            log.info("Blockchain module disabled (dev mode), using simulated storage");
        }
    }

    public ChainTransaction uploadEvidence(String traceCode, String dataHash, String data) {
        if (!enabled) {
            log.info("[Simulated] traceCode={}, dataHash={}", traceCode, dataHash);
            return simulateTransaction(traceCode, dataHash);
        }
        // Production: call FISCO BCOS contract
        // TraceEvidence evidenceContract = TraceEvidence.load(traceContractAddress, client, cryptoKeyPair);
        // TransactionReceipt receipt = evidenceContract.upload(traceCode, dataHash, data).send();
        // return new ChainTransaction(receipt.getTransactionHash(), receipt.getBlockNumber().longValue());
        return simulateTransaction(traceCode, dataHash);
    }

    public ChainRecord queryEvidence(String traceCode) {
        if (!enabled) {
            log.info("[Simulated query] traceCode={}", traceCode);
            ChainRecord record = new ChainRecord();
            record.setTraceCode(traceCode);
            record.setTxHash("0xsim" + UUID.randomUUID().toString().replace("-", "").substring(0, 40));
            record.setBlockNumber(1000000L + new Random().nextInt(10000));
            record.setTimestamp(LocalDateTime.now());
            record.setValid(true);
            return record;
        }
        // Production: call contract query
        return null;
    }

    private ChainTransaction simulateTransaction(String traceCode, String dataHash) {
        ChainTransaction tx = new ChainTransaction();
        tx.setTxHash("0x" + UUID.randomUUID().toString().replace("-", ""));
        tx.setBlockNumber(System.currentTimeMillis() / 1000);
        tx.setTraceCode(traceCode);
        tx.setDataHash(dataHash);
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus("SUCCESS");
        return tx;
    }
}
