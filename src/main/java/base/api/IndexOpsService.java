package base.api;


import base.md.MdPos;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-15.
 */
public interface IndexOpsService extends Remote {

    /**
     * 创建目录索引
     * 返回目录元数据保存的后端节点信息
     */
    public MdPos createDirIndex(String parentPath, String dirName) throws RemoteException;

    /**
     * 查询待创建文件所用的dCode以及对应的后端节点信息
     */
    public MdPos getMdPosForCreateFile(String path) throws RemoteException;

    /**
     * 查询文件时：path是查询文件的父目录路径
     * 列表目录时，path是目录路径
     * 返回分布列表对于的后端节点信息
     */
    public List<MdPos> getMdPosList(String path) throws RemoteException;

    /**
     * 重命名目录索引
     */
    public List<MdPos> renameDirIndex(String parentPath, String oldName, String newName) throws RemoteException;

}
