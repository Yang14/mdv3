package client.service.tool;

import base.api.IndexOpsService;
import base.md.MdPos;
import base.tool.PortEnum;
import org.nutz.ssdb4j.SSDBs;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ConnTool {
    private static final int INDEX_PORT = PortEnum.INDEX_PORT;
    private static final String INDEX_IP = "rmi://192.168.0.13:";

    private static final Map<String, SSDB> ssdbHolder = new ConcurrentHashMap<String, SSDB>();

    private static Logger logger = LoggerFactory.getLogger("ConnTool");

    public static IndexOpsService getIndexOpsService() {
        IndexOpsService indexOps = null;
        try {
            indexOps = (IndexOpsService) Naming.lookup(INDEX_IP + INDEX_PORT + "/INDEX");
        } catch (Exception e) {
            logger.error("error info:" + e.getMessage());
        }
        return indexOps;
    }

    public static SSDB getSSDB(MdPos mdPos) {
        SSDB ssdb = ssdbHolder.get(mdPos.getIp());
        if (ssdb == null) {
            ssdb = SSDBs.pool(mdPos.getIp(), PortEnum.SSDB_PORT, 60000, null);
            ssdbHolder.put(mdPos.getIp(), ssdb);
        }
        return ssdb;
    }

}
