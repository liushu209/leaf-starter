package org.leaf.starter.core;

import org.leaf.starter.IdGen;
import org.leaf.starter.exception.LeafException;

public class SnowflakeIDGenImpl implements IdGen {

    public SnowflakeIDGenImpl(SnowflakeZookeeperHolder snowflakeZookeeperHolder) throws Exception {
        snowflakeZookeeperHolder.init();
        this.workerId = snowflakeZookeeperHolder.getWorkerId();
    }

    /**
     * 工作节点id
     */
    private final long workerId;
    /**
     * 序列号
     */
    private long sequence = 0L;

    private long lastTimestamp = -1L;

    /**
     * 获取下一个id
     *
     * @return long
     */
    @Override
    public synchronized long get() throws LeafException {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new LeafException("操作失败");
        }
        long sequenceBits = 12L;
        if (timestamp == lastTimestamp) {
            long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        long workerIdBits = 10L;
        long timestampLeftShift = sequenceBits + workerIdBits;
        return (timestamp - 1288834974657L) << timestampLeftShift | workerId << sequenceBits | sequence;
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
