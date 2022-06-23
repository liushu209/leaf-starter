package com.jiangsilu.leaf;

import com.jiangsilu.leaf.propertis.LeafProperties;
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

/**
 * @author sixuncle
 */
public class SnowflakeZookeeperHolder {
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeZookeeperHolder.class);

    /**
     * zookeeper 前缀
     */
    private static final String PATH = "/com/jiangsilu/id/leaf";

    private String appName;

    /**
     * 默认workId为0
     */
    private Integer workerId = 0;

    /**
     * zookeeper 连接字符串
     */
    private String connectionString;

    public SnowflakeZookeeperHolder(LeafProperties leafProperties, String appName) {
        this.connectionString = leafProperties.getAddr();
        this.appName = appName;
    }

    public void init() throws Exception {
        CuratorFramework curator = createWithOptions(connectionString, new RetryUntilElapsed(1000, 4), 10000, 6000);
        curator.start();
        String nodePath = PATH + "/" + appName;
        Stat stat = curator.checkExists().forPath(PATH);
        if (stat == null) {
            String node = curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(PATH);
        }
        List<String> strings = curator.getChildren().forPath(PATH);
        ArrayList<String> list = new ArrayList<>();
        for (String s : strings) {
            list.add(new String(curator.getData().forPath(PATH + "/" + s)));
        }
        Random random = new Random();
        while (true) {
            String s = String.valueOf(random.nextInt(1024));
            if (!list.contains(s)) {
                this.workerId = Integer.parseInt(s);
                break;
            }
        }
        String node = curator.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(nodePath, String.valueOf(workerId).getBytes());
        logger.info("创建zookeeper 临时节点 ---> " + node + "workerId ---> " + workerId);
    }


    private CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) {
        return CuratorFrameworkFactory.builder().connectString(connectionString).retryPolicy(retryPolicy).connectionTimeoutMs(connectionTimeoutMs).sessionTimeoutMs(sessionTimeoutMs).build();
    }

    public int getWorkerId() {
        return workerId;
    }
}
