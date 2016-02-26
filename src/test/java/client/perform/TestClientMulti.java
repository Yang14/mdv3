package client.perform;

import base.md.MdAttr;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientMulti {
    private static Logger logger = LoggerFactory.getLogger("TestClient");

    private ClientService clientService = new ClientServiceImpl();

    private int threadCount = 4;
    private int count = 10000;
    private CountDownLatch latchCreate = new CountDownLatch(threadCount);
    private CountDownLatch latchFind = new CountDownLatch(threadCount);

    private CountDownLatch latchForOps = new CountDownLatch(1);

    private String[] threadNameArray;

    @Before
    public void setUp() throws RemoteException {
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 2, true));
        clientService.createDirMd("/d1/d2", "d3", getMdAttr("d3", 3, true));
        clientService.createDirMd("/d1/d2/d3", "d4", getMdAttr("d4", 4, true));
        clientService.createDirMd("/d1/d2/d3/d4", "d5", getMdAttr("d5", 5, true));
        clientService.createDirMd("/d1/d2/d3/d4/d5", "d6", getMdAttr("d6", 5, true));
        testBuildDirTreePerform();
        List<String> threadNameList = new ArrayList<String>();
        for (int i = 0; i < threadCount; i++) {
            String threadName = "t" + i;
            clientService.createDirMd("/", threadName, getMdAttr(threadName, 5, true));
            clientService.createFileMd("/", threadName + "-forFile", getMdAttr(threadName + "-forFile", 99, false));
            threadNameList.add(threadName);
        }
        threadNameArray = (String[]) threadNameList.toArray();
    }

    @Test
    public void testMultiCreate() throws InterruptedException, RemoteException {
        testMultiCreateDir();
        latchForOps.countDown();
        testMultiCreateFile();
    }

    public void testMultiCreateDir() throws InterruptedException, RemoteException {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSubDir(Thread.currentThread().getName());
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
        latchCreate.await();
        long end = System.currentTimeMillis();
        logger.info(String.format("create ok, thread count is %s time: %s", threadCount, (end - start)));
        latchForOps.countDown();
    }
    public void testMultiCreateFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSubFile(Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("find ok, thread count is %s time: %s", threadCount, (end - start)));
    }

    public void testMultiFindFile() throws InterruptedException, RemoteException {
        latchForOps.await();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    buildSubFile(Thread.currentThread().getName() + "-forFile");
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
        logger.info(String.format("find ok, thread count is %s time: %s", threadCount, (end - start)));
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createDirMd(parentDir, "dir" + i, getMdAttr("dir" + i, i, true));
        }
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.createFileMd(parentDir, "file" + i, getMdAttr("file" + i, i, false));
        }
    }

    private void findSubFile(String parentDir) throws RemoteException {
        for (int i = 0; i < count; i++) {
            clientService.findFileMd(parentDir, "file" + i);
        }
    }

    public void buildDirTree(String dir) throws RemoteException {
        long start = System.currentTimeMillis();
        String secondDir = dir;
        for (int i = 0; i < 100; i++) {
            clientService.createDirMd("/", secondDir + i, getMdAttr(secondDir + i, i, true));
        }
        long end = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end - start)));
    }

    public void testBuildDirTreePerform() throws RemoteException {
        String dirName = "bin";
        for (int i = 0; i < 1; i++) {
            buildDirTree(dirName + i);
        }
    }

    private MdAttr getMdAttr(String name, int size, boolean isDir) {
        MdAttr mdAttr = new MdAttr();
        mdAttr.setName(name);
        mdAttr.setSize(size);
        mdAttr.setType(isDir);
        // mdAttr.setCreateTime(System.currentTimeMillis());
        return mdAttr;
    }
}
