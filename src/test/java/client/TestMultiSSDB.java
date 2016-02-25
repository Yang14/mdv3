package client;

import base.md.MdAttr;
import base.tool.PortEnum;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Mr-yang on 16-2-23.
 */
public class TestMultiSSDB {
    private static Logger logger = LoggerFactory.getLogger("TestMultiSSDB");

    private int threadCount = 1000;
    private CountDownLatch totalLatch = new CountDownLatch(3);
    private CountDownLatch latch = new CountDownLatch(threadCount);

    private Map<String, Long> createTimeMap = new ConcurrentHashMap<String, Long>();
    private Map<String, Long> findTimeMap = new ConcurrentHashMap<String, Long>();

    private final int createCount = 2000 / threadCount;

    private Map<Integer, Integer> dCodeMap;
    private static SSDB ssdb = SSDBs.pool("192.168.0.13", PortEnum.SSDB_PORT, 10000, null);

    @Before
    public void setUp() {
        dCodeMap = new HashMap<Integer, Integer>();
        for (int i = 0; i < threadCount; i++) {
            dCodeMap.put(i, 100 + i);
        }
    }

    @Test
    public void testPerformance() throws InterruptedException {
        testMultiCreate();
        testMultiListDir();
        testMultiFindFile();
        totalLatch.await();
        logger.info("test finished...");
    }

    /**
     * 测试多个线程并发创建100w索引数据需要的时间
     * 通过rmi接口
     */
    @Test
    public void testMultiCreate() throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(new CreateIndexWithNet(), dCodeMap.get(i) + "").start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("with net: %s thread create %s mdAttr, use time: %sms", threadCount, createCount, (end - start)));
        logger.info("each thread create index time spend is:" + createTimeMap);
        totalLatch.countDown();
    }

    /**
     * 测试多个线程并发创建100w索引数据需要的时间
     * 通过rmi接口
     */
    @Test
    public void testMultiListDir() throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(new ListDir(), dCodeMap.get(i) + "").start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("%s thread find %s mdAttr, use time: %sms", threadCount, createCount, (end - start)));
        logger.info("each thread find index time spend is:" + findTimeMap);
        totalLatch.countDown();
    }

    @Test
    public void testMultiFindFile() throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(new FindFileMd(), dCodeMap.get(i) + "").start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("%s thread find %s mdAttr, use time: %sms", threadCount, createCount, (end - start)));
        logger.info("each thread find index time spend is:" + findTimeMap);
        totalLatch.countDown();
    }


    @Test
    public void testHSet() {
        long dCode = Long.parseLong(Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        for (int i = 0; i < createCount; i++) {
            ssdb.hset(dCode, "file" + i, JSON.toJSONString(getMdAttr("file" + i, i, false)));
        }
        long end = System.currentTimeMillis();
        createTimeMap.put(dCode + "", end - start);
    }

    @Test
    public void testGetMdAttr() {
        long dCode = Long.parseLong(Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        for (int i = 0; i < createCount; i++) {
            JSON.parseObject(ssdb.hget(dCode, "file" + i).asString());
        }
        long end = System.currentTimeMillis();
        findTimeMap.put(dCode + "", end - start);
    }

    @Test
    public void testHGet() {
        long dCode = Long.parseLong(Thread.currentThread().getName());
        Response response = ssdb.hgetall(dCode);
        Map<String, String> mdAttrMap = response.mapString();
        for (String key : mdAttrMap.keySet()) {
            JSON.parseObject(mdAttrMap.get(key), MdAttr.class);
        }
    }

    /**
     * 执行删除文件时可以根据resp的notFound选项判断是否删除了存在的文件
     */
    @Test
    public void testDelResult() {
        ssdb.hset("testDelete", "beDel", "content");
        assertEquals(ssdb.hget("testDelete", "beDel").asString(), "content");
        assertTrue(ssdb.hdel("testDelete", "beDel").ok());
        assertFalse(ssdb.hdel("testDelete", "beDel2").notFound());
        ssdb.hset("testDelete", "beDel", "content2");
        ssdb.hclear("testDelete");
        assertFalse(ssdb.hdel("testDelete", "beDel2").notFound());

    }

    class CreateIndexWithNet implements Runnable {

        @Override
        public void run() {
            testHSet();
            latch.countDown();
        }
    }

    class ListDir implements Runnable {

        @Override
        public void run() {
            testHGet();
            latch.countDown();
        }
    }

    class FindFileMd implements Runnable {

        @Override
        public void run() {
            testGetMdAttr();
            latch.countDown();
        }
    }

    private MdAttr getMdAttr(String name, int size, boolean isDir) {
        MdAttr mdAttr = new MdAttr();
        mdAttr.setName(name);
        mdAttr.setSize(size);
        mdAttr.setType(isDir);
        mdAttr.setCreateTime(System.currentTimeMillis());
        return mdAttr;
    }


}
