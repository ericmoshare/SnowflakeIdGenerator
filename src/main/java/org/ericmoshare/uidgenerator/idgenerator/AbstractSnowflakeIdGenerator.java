package org.ericmoshare.uidgenerator.idgenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Twitter的snowflake理论
 * id构成: 41位的时间前缀 + 10位的节点标识 + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 *
 * @author eric.mo
 */

public abstract class AbstractSnowflakeIdGenerator implements IdGenerator {

    private final static Logger logger = LoggerFactory.getLogger(RedisSnowflakeIdGenerator.class);

    /**
     * 时间起始标记点，作为基准，一般取系统第一次运行的的时间毫秒值作为"新纪元"
     */
    private final long epoch = 1451577600605L;
    /**
     * 毫秒内序列
     */
    private long sequence = 0L;
    /**
     * 毫秒内自增位数
     */
    private final long sequenceBits = 13L;
    /**
     * 毫秒内最大自增序列值:4095,12位
     */
    private final long sequenceMax = -1L ^ -1L << this.sequenceBits;
    /**
     * worker标识位数
     */
    private final long workerIdBits = 9L;
    /**
     * workerId左移动位
     */
    private final long workerIdLeftShift = this.sequenceBits;
    /**
     * 时间戳左移动位
     */
    private final long timestampLeftShift = this.sequenceBits + this.workerIdBits;
    /**
     * 上次生产id时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 获取uniqueId(long)
     *
     * @return snowflake
     */
    @Override
    public synchronized long nextId() {
        long timestamp = this.getCurrentTime();
        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1 & this.sequenceMax;
            if (this.sequence == 0) {
                timestamp = this.waitingNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            logger.error(String.format("clock moved backwards.Refusing to generate snowflake for %d milliseconds", (this.lastTimestamp - timestamp)));
            throw new IllegalStateException(String.format("clock moved backwards.Refusing to generate snowflake for %d milliseconds", (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        return timestamp - this.epoch << this.timestampLeftShift | getWorkerId() << this.workerIdLeftShift | this.sequence;
    }

    @Override
    public synchronized String nextStringValue() {
        return String.valueOf(nextId());
    }

    private long waitingNextMillis(long lastTimestamp) {
        long timestamp = this.getCurrentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = this.getCurrentTime();
        }
        return timestamp;
    }

    private long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取worker的id位
     *
     * @return workId
     */
    protected abstract long getWorkerId();

}