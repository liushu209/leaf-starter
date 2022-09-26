package org.leaf.starter.core;

import org.leaf.starter.propertis.LeafProperties;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnowflakeZookeeperHolder {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeZookeeperHolder.class);

    private static final String SYMBOL = "/";
    /**
     * zookeeper 前缀
     */
    private static final String PATH = SYMBOL + "id/leaf";


    private final String appName;

    /**
     * 默认workId为0
     */
    private Integer workerId = 0;

    /**
     * zookeeper 连接字符串
     */
    private final String connectionString;
    private final Random rand = new Random();

    public SnowflakeZookeeperHolder(LeafProperties leafProperties, String appName) {
        this.connectionString = leafProperties.getAddress();
        this.appName = appName;
    }

    public void init() throws Exception {
        CuratorFramework curator = createWithOptions(connectionString, new RetryUntilElapsed(1000, 4));
        curator.start();
        String nodePath = PATH + SYMBOL + appName;
        Stat stat = curator.checkExists().forPath(PATH);
        if (stat == null) {
            curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(PATH);
        }
        List<String> strings = curator.getChildren().forPath(PATH);
        ArrayList<String> list = new ArrayList<>();
        for (String s : strings) {
            list.add(new String(curator.getData().forPath(PATH + SYMBOL + s)));
        }
        while (true) {
            String s = String.valueOf(rand.nextInt(1024));
            if (!list.contains(s)) {
                this.workerId = Integer.parseInt(s);
                break;
            }
        }
        String node = curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(nodePath, String.valueOf(workerId).getBytes());
        logger.info("\n---------------------Created node success------------------------\n------------------------ 临时节点 {}------------------------------\n-------------------------workerId {}-----------------------------\n---------------------Created node success------------------------\n", node, workerId);
    }


    private CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy) {
        return CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy).connectionTimeoutMs(10000).sessionTimeoutMs(6000).build();
    }

    public int getWorkerId() {
        return workerId;
    }
}
