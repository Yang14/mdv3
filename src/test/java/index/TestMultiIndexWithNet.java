package index;

import base.api.IndexOpsService;
import client.service.tool.ConnTool;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-22.
 * 使用多线程模拟客户端创建和查询索引
 */
public class TestMultiIndexWithNet {
    private static Logger logger = LoggerFactory.getLogger("TestMultiBackendWithNet");
    private int threadCount = 1;
    private CountDownLatch startGate = new CountDownLatch(1);
    private CountDownLatch latch = new CountDownLatch(threadCount);

    private IndexOpsService indexOps = ConnTool.getIndexOpsService();

    private Map<String, Long> createTimeMap = new ConcurrentHashMap<String, Long>();
    private Map<String, Long> findTimeMap = new ConcurrentHashMap<String, Long>();

    private final int createCount = 1000000 / threadCount;

    private Map<Integer, String> pathMap;

    @Before
    public void setUp() {
        pathMap = new HashMap<Integer, String>();
        String pathStr = "/";
        for (int i = 0; i < threadCount; i++) {
            pathStr += i == 0 ? "d" + i : "/d" + i;
            pathMap.put(i, pathStr);
        }
    }

    /**
     * 测试多个线程并发创建100w索引数据需要的时间
     * 通过rmi接口
     */
    @Test
    public void testMultiCreate() throws InterruptedException, RemoteException {
        buildDirTreeBeforeTest();
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(new CreateIndexWithNet(), pathMap.get(i)).start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("with net: %s thread create %s index, use time: %sms", threadCount, createCount, (end - start)));
        // logger.info("each thread create index time spend is:" + createTimeMap);
    }

    /**
     * 测试多个线程并发创建100w索引数据需要的时间
     * 通过rmi接口
     */
    @Test
    public void testMultiFind() throws InterruptedException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(new FindIndexWithNet(), pathMap.get(i)).start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("%s thread find %sw index, use time: %sms", threadCount, createCount, (end - start)));
        logger.info("each thread find index time spend is:" + findTimeMap);
    }


    /**
     * 测试创建1w索引数据需要的时间和空间
     */
    @Test
    public void testCreateOneMillionIndexUsedTimeAndSize() throws RemoteException {
        String parentPath = Thread.currentThread().getName();
        String dirName = "dir";
        long start = System.currentTimeMillis();
        for (long i = 0; i < createCount; ++i) {
            indexOps.createDirIndex(parentPath, dirName + i);
        }
        long end = System.currentTimeMillis();
        createTimeMap.put(parentPath, end - start);
    }

    private void buildDirTreeBeforeTest() throws RemoteException {
        pathMap = new HashMap<Integer, String>();
        String pathStr = "/";
        for (int i = 0; i < threadCount; i++) {
            indexOps.createDirIndex(pathStr, "d" + i);
            pathStr += i == 0 ? "d" + i : "/d" + i;
            pathMap.put(i, pathStr);
        }
        startGate.countDown();
    }


    /**
     * 测试随机读取10w索引的时间
     */
    @Test
    public void testGetIndexRandom() throws RemoteException {
        String parentPath = Thread.currentThread().getName();
        String dirPath = parentPath.equals("/") ? parentPath + "dir" : parentPath + "/dir";
        long start = System.currentTimeMillis();
        for (long i = 0; i < createCount; ++i) {
            indexOps.getMdPosList(dirPath + i);
        }
        long end = System.currentTimeMillis();
        findTimeMap.put(parentPath, end - start);
    }

    class CreateIndexWithNet implements Runnable {

        @Override
        public void run() {
            try {
                startGate.await();
                testCreateOneMillionIndexUsedTimeAndSize();
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    class FindIndexWithNet implements Runnable {

        @Override
        public void run() {
            try {
                testGetIndexRandom();
                latch.countDown();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
