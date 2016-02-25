package index;

import base.api.IndexOpsService;
import index.impl.IndexOpsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by Mr-yang on 16-2-25.
 */
public class TestIndexFunc {
    private static Logger logger = LoggerFactory.getLogger("TestIndexFunc");

    private IndexOpsService indexOpsService;

    @Before
    public void setUp() throws RemoteException {
        indexOpsService = new IndexOpsServiceImpl();
    }

    @Test
    public void testDelFile() throws RemoteException {

        indexOpsService.createDirIndex("/","a1");
        indexOpsService.createDirIndex("/a1","b");
        indexOpsService.createDirIndex("/a1/b","c");
        indexOpsService.deleteDir("/a1");

    }

}
