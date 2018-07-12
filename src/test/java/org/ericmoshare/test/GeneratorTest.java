package org.ericmoshare.test;

import org.ericmoshare.uidgenerator.idgenerator.IdGenerator;
import org.testng.annotations.Test;

import javax.annotation.Resource;

/**
 * @author eric.mo
 * @since 2018/5/4
 */
public class GeneratorTest extends BaseNGTest {

    @Resource(name = "mysqlSnowflakeIdGenerator")
    private IdGenerator mysqlSnowflakeIdGenerator;

    @Resource(name = "redisSnowflakeIdGenerator")
    private IdGenerator redisSnowflakeIdGenerator;

    @Test
    public void nextLong() {

        for (int i = 0; i < 10; i++) {
            System.out.println(mysqlSnowflakeIdGenerator.nextId());
        }

    }

    @Test
    public void nextLong2() {

        for (int i = 0; i < 10; i++) {
            System.out.println(redisSnowflakeIdGenerator.nextId());
        }

    }

}
