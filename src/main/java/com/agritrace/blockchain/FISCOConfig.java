package com.agritrace.blockchain;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * FISCO BCOS 配置
 */
@Configuration
@ConfigurationProperties(prefix = "blockchain.fisco")
public class FISCOConfig {

    private int groupId = 1;
    private int chainId = 1;
    private String nodes = "127.0.0.1:20200";
    private String certPath = "conf/";
    private Contract contract = new Contract();

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    public int getChainId() { return chainId; }
    public void setChainId(int chainId) { this.chainId = chainId; }
    public String getNodes() { return nodes; }
    public void setNodes(String nodes) { this.nodes = nodes; }
    public String getCertPath() { return certPath; }
    public void setCertPath(String certPath) { this.certPath = certPath; }
    public Contract getContract() { return contract; }
    public void setContract(Contract contract) { this.contract = contract; }

    public static class Contract {
        private String traceAddress = "";
        private String userAddress = "";

        public String getTraceAddress() { return traceAddress; }
        public void setTraceAddress(String traceAddress) { this.traceAddress = traceAddress; }
        public String getUserAddress() { return userAddress; }
        public void setUserAddress(String userAddress) { this.userAddress = userAddress; }
    }
}
