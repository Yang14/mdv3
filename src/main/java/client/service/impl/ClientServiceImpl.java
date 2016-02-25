package client.service.impl;

import base.api.IndexOpsService;
import base.md.MdAttr;
import base.md.MdPos;
import client.service.ClientService;
import client.service.dao.SSDBDao;
import client.service.dao.SSDBDaoImpl;
import client.service.tool.ConnTool;
import client.service.tool.MdPosCacheTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-18.
 */
public class ClientServiceImpl implements ClientService {
    private static Logger logger = LoggerFactory.getLogger("ClientServiceImpl");
    private static IndexOpsService indexOps = ConnTool.getIndexOpsService();

    private static SSDBDao ssdbService = new SSDBDaoImpl();

    @Override
    public boolean createFileMd(String parentDirPath, String fileName, MdAttr mdAttr) throws RemoteException {
        MdPosCacheTool.removeMdPosList(parentDirPath);
        MdPos mdPos = indexOps.getMdPosForCreateFile(parentDirPath);
        return ssdbService.insertMd(mdPos, fileName, mdAttr);
    }

    @Override
    public boolean createDirMd(String parentDirPath, String dirName, MdAttr mdAttr) throws RemoteException {
        MdPosCacheTool.removeMdPosList(parentDirPath);
        MdPos mdPos = indexOps.createDirIndex(parentDirPath, dirName);
        return ssdbService.insertMd(mdPos, dirName, mdAttr);
    }

    @Override
    public MdAttr findFileMd(String parentDirPath, String fileName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        MdAttr mdAttr = null;
        for (MdPos mdPos : mdPosList) {
            mdAttr = ssdbService.findFileMd(mdPos, fileName);
            if (mdAttr != null) {
                break;
            }
        }
        return mdAttr;
    }

    @Override
    public List<MdAttr> listDir(String dirPath) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(dirPath);
        List<MdAttr> mdAttrList = new ArrayList<MdAttr>();
        for (MdPos mdPos : mdPosList) {
            List<MdAttr> partMdAttrList = ssdbService.listDir(mdPos);
            if (partMdAttrList != null) {
                mdAttrList.addAll(partMdAttrList);
            }
        }
        return mdAttrList;
    }

    @Override
    public boolean renameDir(String parentDirPath, String oldName, String newName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListFromRenameDir(parentDirPath, oldName, newName);
        boolean renameResult = false;
        for (MdPos mdPos : mdPosList) {
            renameResult = ssdbService.renameMd(mdPos, oldName, newName);
            if (renameResult) {
                break;
            }
        }
        return renameResult;
    }

    @Override
    public boolean renameFile(String parentDirPath, String oldName, String newName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        boolean renameResult = false;
        for (MdPos mdPos : mdPosList) {
            renameResult = ssdbService.renameMd(mdPos, oldName, newName);
            if (renameResult) {
                break;
            }
        }
        return renameResult;
    }

    @Override
    public boolean deleteDir(String dirPath) throws RemoteException {
        return indexOps.deleteDir(dirPath);
    }

    @Override
    public boolean deleteFile(String parentDirPath, String fileName) throws RemoteException {
        List<MdPos> mdPosList = getMdPosListByPath(parentDirPath);
        boolean renameResult = false;
        for (MdPos mdPos : mdPosList) {
            renameResult = ssdbService.deleteMd(mdPos, fileName);
            if (renameResult) {
                break;
            }
        }
        return renameResult;
    }

    private List<MdPos> getMdPosListByPath(String path) throws RemoteException {
        List<MdPos> mdPosList = MdPosCacheTool.getMdPosListFromCache(path);
        if (mdPosList == null) {
            mdPosList = indexOps.getMdPosList(path);
            MdPosCacheTool.setMdPosListToCache(path, mdPosList);
        }
        return mdPosList;
    }

    private List<MdPos> getMdPosListFromRenameDir(String path, String oldName, String newName) throws RemoteException {
        List<MdPos> mdPosList = MdPosCacheTool.getMdPosListFromCache(path);
        if (mdPosList == null) {
            mdPosList = indexOps.renameDirIndex(path, oldName, newName);
            MdPosCacheTool.setMdPosListToCache(path, mdPosList);
        }
        return mdPosList;
    }

}
