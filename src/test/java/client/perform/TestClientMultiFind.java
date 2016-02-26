package client.perform;

import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMultiFind {
    private static Logger logger = LoggerFactory.getLogger("TestClient");

    private ClientService clientService = new ClientServiceImpl();

    private int threadCount = 64;
    private int count = 10000;
    private CountDownLatch latchCreate = new CountDownLatch(threadCount);
    private CountDownLatch latchFind = new CountDownLatch(threadCount);

    private CountDownLatch latchForOps = new CountDownLatch(1);

    private String[] threadNameArray;

    @Before
    public void setUp() throws RemoteException {
        String[] name = new String[threadCount];
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            name[i] = threadName;
        }
        threadNameArray = name;
    }

    @Test
    public void testMultiFind() throws InterruptedException, RemoteException {
        testMultiListDir();
        latchForOps.countDown();
        testMultiFindFile();
    }

    public void testMultiListDir() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    listDir("/" + Thread.currentThread().getName());
                    latchCreate.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchFind.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("list dir, thread count is %s time: %s", threadCount, (end - start)));
    }
    public void testMultiFindFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    findFile("/" + Thread.currentThread().getName() + "-forFile");
                    latchCreate.countDown();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        };
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; ++i) {
            new Thread(run, threadNameArray[i]).start();
        }
        latchFind.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("find file, thread count is %s time: %s", threadCount, (end - start)));
    }
    private void listDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.listDir(parentDir+ "/file" + i);
        }
    }

    private void findFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.findFileMd(parentDir, "file" + i);
        }
    }
}
