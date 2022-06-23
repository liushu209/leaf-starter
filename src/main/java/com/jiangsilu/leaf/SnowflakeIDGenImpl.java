package com.jiangsilu.leaf;

import com.jiangsilu.leaf.exception.LeafException;

/**
 * @author sixuncle
 */
public class SnowflakeIDGenImpl implements IDGen {

    public SnowflakeIDGenImpl(SnowflakeZookeeperHolder snowflakeZookeeperHolder) throws Exception {
        snowflakeZookeeperHolder.init();
        this.workerId = snowflakeZookeeperHolder.getWorkerId();
    }

    /**
     * 工作节点id
     */
    private long workerId;
    /**
     * 序列号
     */
    private long sequence = 0L;
    /**
     * 起始时间
     */
    private long twepoch = 1288834974657L;

    private final long workerIdBits = 10L;

    private final long sequenceBits = 12L;

    private final long maxWorkerId = ~(-1L << workerIdBits);

    private final long workerIdShift = sequenceBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits;

    private final long sequenceMask = ~(-1L << sequenceBits);

    private long lastTimestamp = -1L;

    /**
     * 获取下一个id
     *
     * @return
     */
    @Override
    public synchronized long get() throws LeafException {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new LeafException(50000, "操作失败");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return (timestamp - twepoch) << timestampLeftShift | workerId << workerIdShift | sequence;
    }

    /**
     * 阻塞到下一个毫秒 ，获取到新的时间戳
     *
     * @param lastTimestamp lastTimestamp
     * @return timestamp
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 获取当前时间戳
     *
     * @return timestamp
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
