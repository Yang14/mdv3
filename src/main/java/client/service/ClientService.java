package client.service;


import base.md.MdAttr;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Mr-yang on 16-1-9.
 */
public interface ClientService {

    public boolean createFileMd(String parentDirPath, String fileName, MdAttr mdAttr) throws RemoteException;

    public boolean createDirMd(String parentDirPath, String dirName, MdAttr mdAttr) throws RemoteException;

    public MdAttr findFileMd(String parentDirPath, String fileName) throws RemoteException;

    public List<MdAttr> listDir(String dirPath) throws RemoteException;

    public boolean renameDir(String parentDirPath, String oldName, String newName) throws RemoteException;

    public boolean renameFile(String parentDirPath, String oldName, String newName) throws RemoteException;

    //TODO
    public boolean deleteDir(String dirPath) throws RemoteException;

    //TODO
    public boolean deleteFile(String parentDirPath, String fileName) throws RemoteException;

}
