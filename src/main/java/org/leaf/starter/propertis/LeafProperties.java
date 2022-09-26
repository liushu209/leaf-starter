package org.leaf.starter.propertis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "leaf.zookeeper")
public class LeafProperties {
    /**
     * localhost:2181
     */
    private String address = "localhost:2181";

    public String getAddress() {
        return address;
    }

    public void setAddress(String addr) {
        this.address = addr;
    }
}
