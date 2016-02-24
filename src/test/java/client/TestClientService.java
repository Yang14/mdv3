package client;

import base.api.IndexOpsService;
import base.md.MdAttr;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class TestClientService {
    private static Logger logger = LoggerFactory.getLogger("TestClientService");

    private ClientService clientService = new ClientServiceImpl();

    public void buildDirTree(String dir) throws RemoteException {
        long start = System.currentTimeMillis();
        String secondDir = dir;
        for (int i = 0; i < 10; i++) {
            clientService.createDirMd("/", secondDir + i, getMdAttr(secondDir + i, i, true));
        }
        long end = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end - start)));

        String thirdDir = "foo";
        String thirdFile = "a.t";
        end = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                clientService.createDirMd("/" + secondDir + i, thirdDir + j, getMdAttr(thirdDir + j, i, true));
                clientService.createFileMd("/" + secondDir + i, thirdFile + j, getMdAttr(thirdFile + j, i, false));
            }
        }
        long end2 = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end2 - end)));
    }

    @Test
    public void testBuildDirTreePerform() throws RemoteException {
        String dirName = "bin0";
        for (int i=0;i<1;i++){
            buildDirTree(dirName+i);
        }
        logger.info(clientService.listDir("/").toString());
    }

    @Test
    public void testListDirTree() throws RemoteException {
        long start = System.currentTimeMillis();
        String secondDir = "bin00";
        for (int i = 0; i < 100; i++) {
            clientService.listDir("/" + secondDir + i);
        }
        long end = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end - start)));

        String thirdDir = "foo";
        String thirdFile = "a.t";
        end = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                clientService.listDir("/" + secondDir + i);
                clientService.findFileMd("/" + secondDir + i,thirdFile +j);
            }
        }
        long end2 = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end2 - end)));
    }

    @Test
    public void testListDir() throws RemoteException, InterruptedException {
        long start = System.currentTimeMillis();
        logger.info(clientService.listDir("/bin01").size()+"");
//        logger.info(clientService.listDir("/bin1").toString());
        long end = System.currentTimeMillis();
        logger.info(String.format("time: %s", (end - start)));

        //clientService.createDirMd("/","a",getMdAttr("a",0,true));
        clientService.createDirMd("/a","b",getMdAttr("b",0,true));
        clientService.createDirMd("/a","e",getMdAttr("e",0,true));
        logger.info(clientService.listDir("/a").toString());
        clientService.createDirMd("/a","d",getMdAttr("d",0,true));
        logger.info(clientService.listDir("/a").toString());
        clientService.createDirMd("/a", "c", getMdAttr("c", 0, true));
        logger.info(clientService.listDir("/a").size()+"");

    }

    @Test
    public void testRenameFile() throws RemoteException {
//        logger.info(clientService.findFileMd("/bin0","a.t0").toString());
        logger.info(clientService.listDir("/bin0").toString());
        clientService.renameFile("/bin0", "a.t0", "renamed_a.t0");
        logger.info(clientService.listDir("/bin0").toString());
        logger.info(clientService.findFileMd("/bin0", "renamed_a.t0").toString());
    }

    @Test
    public void testRenameDir() throws RemoteException {
        logger.info(clientService.listDir("/bin0").toString());
        clientService.renameDir("/","bin0","rename_bin0");
        logger.info(clientService.listDir("/rename_bin0").toString());
    }


    private MdAttr getMdAttr(String name, int size, boolean isDir) {
        MdAttr mdAttr = new MdAttr();
        mdAttr.setName(name);
        mdAttr.setSize(size);
        mdAttr.setType(isDir);
        mdAttr.setCreateTime(System.currentTimeMillis());
        return mdAttr;
    }

    @Test
    public void remoteCall() {
        final int INDEX_PORT = 8888;
        final String INDEX_IP = "rmi://192.168.0.13:";
        IndexOpsService indexOps = null;
        try {
            indexOps = (IndexOpsService) Naming.lookup(INDEX_IP + INDEX_PORT + "/INDEX");
            logger.info("get index ops ok.");
            indexOps.createDirIndex("/","bin");
            logger.info(indexOps.getMdPosList("/").toString());
        } catch (Exception e) {
            logger.error("error info:" + e.getMessage());
        }

    }
}
