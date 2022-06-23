package com.jiangsilu.leaf.propertis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author sixuncle
 */
@ConfigurationProperties(prefix = "leaf.zookeeper")
public class LeafProperties {
    /**
     * localhost:2181
     */
    private String addr = "localhost:2181";

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
