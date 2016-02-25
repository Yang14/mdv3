package index.impl;

import base.api.IndexOpsService;
import base.md.MdPos;
import client.service.dao.SSDBDao;
import client.service.dao.SSDBDaoImpl;
import com.mongodb.MongoClient;
import index.common.CommonModule;
import index.common.CommonModuleImpl;
import index.model.MdIndex;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class IndexOpsServiceImpl extends UnicastRemoteObject implements IndexOpsService {
    private static Logger logger = LoggerFactory.getLogger("IndexOpsServiceImpl");

    private volatile boolean isInit = false;

    private CommonModule commonModule = new CommonModuleImpl();

    private SSDBDao ssdbDao = new SSDBDaoImpl();

    private Datastore datastore;

    public IndexOpsServiceImpl() throws RemoteException {
        super();
        MongoClient mongo = new MongoClient("192.168.0.13", 27017);
        Morphia morphia = new Morphia();
        morphia.mapPackage("index.model");
        datastore = morphia.createDatastore(mongo, "mdIndexManager");
        datastore.ensureIndexes();
        if (!isInit) {
            initRootDir();
        }
    }

    private void initRootDir() {
        long parentCode = -1;
        long fCode = -1;
        long dCode = 0;
        String name = "/";
        MdIndex mdIndex = genDirIndex(parentCode, name, fCode, dCode);
        if (datastore.createQuery(MdIndex.class).filter("fName = ", "/").countAll() == 0) {
            datastore.save(mdIndex);
            isInit = true;
            logger.info("root dir init ...");
        }
    }

    private MdIndex genDirIndex(long parentCode, String name, long fCode, long dCode) {
        List<Long> dCodes = new ArrayList<Long>();
        dCodes.add(dCode);
        return new MdIndex(parentCode, name, fCode, dCodes);
    }

    @Override
    public MdPos createDirIndex(String parentPath, String dirName) throws RemoteException {
        MdIndex parentIndex = getMdIndexByPath(parentPath);
        if (isDirExist(parentIndex.getfCode(), dirName)) {
            return null;
        }
        MdIndex dirIndex = genDirIndex(parentIndex.getfCode(), dirName,
                commonModule.genFCode(), commonModule.genDCode());
        datastore.save(dirIndex);
        return getMdAttrPos(parentIndex);
    }

    private boolean isDirExist(long pCode, String dirName) {
        return datastore.createQuery(MdIndex.class)
                .filter("pCode = ", pCode)
                .filter("fName = ", dirName).get() != null;
    }

    private MdPos getMdAttrPos(MdIndex parentIndex) {
        List<Long> dCodeList = parentIndex.getdCodeList();
        long dCode = dCodeList.get(dCodeList.size() - 1);
        boolean isFit = commonModule.isDCodeFit(dCode);
        if (!isFit) {
            dCode = commonModule.genDCode();
            updateDCodeListWithNewCode(parentIndex, dCode);
        }
        return commonModule.buildMdPos(dCode);
    }

    //先要得到保存父目录的键，再更新节点信息
    private boolean updateDCodeListWithNewCode(MdIndex mdIndex, long newDCode) {
        List<Long> dCodeList = mdIndex.getdCodeList();
        dCodeList.add(newDCode);
        mdIndex.setdCodeList(dCodeList);
        UpdateOperations<MdIndex> ops = datastore.createUpdateOperations(MdIndex.class);
        ops.set("dCodeList", dCodeList);
        return datastore.update(mdIndex, ops).getUpdatedExisting();
    }

    @Override
    public MdPos getMdPosForCreateFile(String path) throws RemoteException {
        MdIndex parentIndex = getMdIndexByPath(path);
        return getMdAttrPos(parentIndex);
    }

    @Override
    public List<MdPos> getMdPosList(String path) throws RemoteException {
        MdIndex mdIndex = getMdIndexByPath(path);
        return commonModule.buildMdPosList(mdIndex.getdCodeList());
    }

    @Override
    public List<MdPos> renameDirIndex(String parentPath, String oldName, String newName) throws RemoteException {
        MdIndex parentIndex = getMdIndexByPath(parentPath);
        MdIndex dirIndex = datastore.createQuery(MdIndex.class)
                .filter("pCode = ", parentIndex.getpCode())
                .filter("fName = ", oldName).get();
        UpdateOperations<MdIndex> ops = datastore.createUpdateOperations(MdIndex.class);
        ops.set("fName", newName);
        datastore.update(dirIndex, ops);
        return commonModule.buildMdPosList(parentIndex.getdCodeList());
    }

    @Override
    public boolean deleteDir(String path) throws RemoteException {
        MdIndex mdIndex = getMdIndexByPath(path);
        delDirHashBucket(mdIndex);
        deleteDirByMdIndex(mdIndex);
        return true;
    }

    private void deleteDirByMdIndex(MdIndex mdIndex) {
        Iterable<MdIndex> subMdIndexes = datastore.createQuery(MdIndex.class)
                .field("pCode").equal(mdIndex.getfCode()).fetch();
        for (MdIndex subIndex : subMdIndexes) {
            delDirHashBucket(subIndex);
            deleteDirByMdIndex(subIndex);
        }
    }

    private void delDirHashBucket(MdIndex mdIndex) {
        datastore.delete(datastore.createQuery(MdIndex.class).field("id").equal(mdIndex.getId()));
        List<MdPos> mdPoses = commonModule.buildMdPosList(mdIndex.getdCodeList());
        for (MdPos mdPos : mdPoses) {
            ssdbDao.deleteDirMd(mdPos);
        }
    }

    public String[] splitPath(String path) {
        if (path == null || path.equals("") || path.charAt(0) != '/') {
            logger.info("splitPath params err: " + path);
            throw new IllegalArgumentException("splitPath params err: " + path);
        }
        if (path.equals("/")) {
            return new String[]{"/"};
        }
        String[] nameArray = path.split("/");
        nameArray[0] = "/";
        return nameArray;
    }

    public MdIndex getMdIndexByPath(String path) {
        MdIndex mdIndex = MdIndexCacheTool.getMdIndexFromCache(path);
        if (mdIndex != null) {
            return mdIndex;
        }
        String[] nameArray = splitPath(path);
        long code = -1;
        for (String name : nameArray) {
            mdIndex = datastore.createQuery(MdIndex.class)
                    .filter("pCode = ", code).filter("fName = ", name).get();
            if (mdIndex == null) {
                throw new IllegalArgumentException(String.format("path %s not exist.", path));
            }
            code = mdIndex.getfCode();
        }
        MdIndexCacheTool.setMdIndexToCache(path, mdIndex);
        return mdIndex;
    }
}
