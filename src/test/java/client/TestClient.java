package client;

import base.md.MdAttr;
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
public class TestClient {
    private static Logger logger = LoggerFactory.getLogger("TestClient");

    private ClientService clientService = new ClientServiceImpl();

    private int threadCount = 5;
    private CountDownLatch latch = new CountDownLatch(threadCount);

    class CreateMd implements Runnable{
        @Override
        public void run() {
            try {
                testCreatePerform();
                latch.countDown();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    @Before
    public void setUp() throws RemoteException {
        clientService.createDirMd("/", "d1", getMdAttr("d1", 1, true));
        clientService.createDirMd("/d1", "d2", getMdAttr("d2", 2, true));
        clientService.createDirMd("/d1/d2", "d3", getMdAttr("d3", 3, true));
        clientService.createDirMd("/d1/d2/d3", "d4", getMdAttr("d4", 4, true));
        clientService.createDirMd("/d1/d2/d3/d4", "d5", getMdAttr("d5", 5, true));
        clientService.createDirMd("/d1/d2/d3/d4/d5", "d6", getMdAttr("d6", 5, true));
        testBuildDirTreePerform();
    }
    @Test
    public void testMultiCreate() throws InterruptedException, RemoteException {
        long start = System.currentTimeMillis();
        for (int i=0;i<threadCount;++i){
            new Thread(new CreateMd(),"thread"+i).start();
        }
        latch.await();
        long end = System.currentTimeMillis();
        logger.info("con create ok, thread count is " + threadCount);
        logger.info(String.format("time: %s", (end - start)));
    }


    @Test
    public void testCreatePerform() throws RemoteException {
        String[] dirArray = new String[]{"/", "/d1/d2", "/d1/d2/d3/d4", "/d1", "/d1/d2/d3", "/d1/d2/d3/d4/d5"};
        for (int i = 0; i < 3; i++) {
            //logger.info("build 1w subDir to dir" + dirArray[i]);
            buildSubDir(dirArray[i]);
        }
        for (int i = 3; i < 6; i++) {
            //logger.info("build 1w file to dir" + dirArray[i]);
            buildSubFile(dirArray[i]);
        }
    }

    @Test
    public void testFindPerform() throws RemoteException {
        String[] dirArray = new String[]{"/", "/d1/d2", "/d1/d2/d3/d4", "/d1", "/d1/d2/d3", "/d1/d2/d3/d4/d5"};
        for (int i = 0; i < 6; i++) {
            logger.info("list dir" + dirArray[i]);
            testListDirTree(dirArray[i]);
        }
        /*for (int i = 3; i < 6; i++) {
            logger.info("find file in dir" + dirArray[i]);
            testFindFile(dirArray[i]);
        }*/
    }

    private void buildSubDir(String parentDir) throws RemoteException {
        String subDir ="dir" + Thread.currentThread().getName();
        //long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            clientService.createDirMd(parentDir, subDir + i, getMdAttr(subDir + i, i, true));
        }
        //long end = System.currentTimeMillis();
        //logger.info(String.format("time: %s", (end - start)));
    }

    private void buildSubFile(String parentDir) throws RemoteException {
        String fileName = "file" + Thread.currentThread().getName();
        //long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            clientService.createFileMd(parentDir, fileName + i, getMdAttr(fileName + i, i, false));
        }
        //long end = System.currentTimeMillis();
       // logger.info(String.format("time: %s", (end - start)));
    }

    public void buildDirTree(String dir) throws RemoteException {
        long start = System.currentTimeMillis();
        String secondDir = dir;
        for (int i = 0; i < 100; i++) {
            clientService.createDirMd("/", secondDir + i, getMdAttr(secondDir + i, i, true));
        }
        long end = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end - start)));

        String thirdDir = "foo";
        String thirdFile = "a.t";
        end = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                clientService.createDirMd("/" + secondDir + i, thirdDir + j, getMdAttr(thirdDir + j, i, true));
                clientService.createFileMd("/" + secondDir + i, thirdFile + j, getMdAttr(thirdFile + j, i, false));
            }
        }
        long end2 = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end2 - end)));
    }

    public void testBuildDirTreePerform() throws RemoteException {
        String dirName = "bin";
        for (int i = 0; i < 1; i++) {
            buildDirTree(dirName + i);
        }
    }

    public void testListDirTree(String dirName) throws RemoteException {
//        long start = System.currentTimeMillis();
        System.out.println(clientService.listDir(dirName));
//        long end = System.currentTimeMillis();
//        logger.info(String.format("time: %s", (end - start)));
    }

    @Test
    public void testListDir() throws RemoteException {
        buildSubDir("/d1/d2");
        System.out.println(clientService.listDir("/d1/d2").size()+"");

    }

    public void testFindFile(String parentDir) throws RemoteException {
        String fileName = "file" + Thread.currentThread().getName();
//        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            clientService.findFileMd(parentDir, fileName + i);
        }
//        long end = System.currentTimeMillis();
//        logger.info(String.format("time: %s", (end - start)));
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
