package client;

import base.md.MdAttr;
import client.service.ClientService;
import client.service.impl.ClientServiceImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestClientFunc {
    private static Logger logger = LoggerFactory.getLogger("TestClientFunc");

    private ClientService clientService = new ClientServiceImpl();

    @Test
    public void testDelFile() throws RemoteException {
        String dirName = "testDelFileDir";
        String fileName = "beDelFile";
        clientService.createDirMd("/", dirName, getMdAttr(dirName, 789, true));
        clientService.createFileMd("/" + dirName, fileName, getMdAttr(fileName, 987, false));
        String fileContent = clientService.listDir("/" + dirName).toString();
        assertTrue(clientService.deleteFile("/" + dirName, fileName));
        assertNotSame(clientService.listDir("/" + dirName).toString(), fileContent);
    }

    @Test
    public void testListDir() throws RemoteException {
        String dirName = "a191";
        clientService.createDirMd("/", dirName, getMdAttr(dirName, 789, true));
        clientService.createDirMd("/a191", "b", getMdAttr("b", 789, true));
        clientService.createDirMd("/a191/b", "c", getMdAttr("c", 789, true));
        List<MdAttr> mdAttrs = clientService.listDir("/" + dirName);
        for (MdAttr mdAttr : mdAttrs) {
            System.out.println(mdAttr);
        }
        mdAttrs = clientService.listDir("/a191/b");
        for (MdAttr mdAttr : mdAttrs) {
            System.out.println(mdAttr);
        }
        System.out.println("start del.");
        clientService.deleteDir("/a191");
        System.out.println("del ok.");
        mdAttrs = clientService.listDir("/a191");
        for (MdAttr mdAttr : mdAttrs) {
            System.out.println(mdAttr);
        }
        mdAttrs = clientService.listDir("/a191/b");
        for (MdAttr mdAttr : mdAttrs) {
            System.out.println(mdAttr);
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
