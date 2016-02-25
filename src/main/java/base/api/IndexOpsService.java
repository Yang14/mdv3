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

    /**
     * 索引端提供删除目录功能
     * 1.找到目录索引，得到目录编码
     * 2.根据编码，递归找到目录及子目录索引，找到所有子目录索引
     * 3.得到所有索引子目录分布编码，并获得分布编码对应的后端节点，得到List<MdPos>
     * 4.由索引节点直接连接后端节点并执行删除哈希桶操作，完成目录删除功能
     * <p/>
     * 并未提供删除文件功能，因为删除文件将由客户端自行完成，先找到文件父目录索引获得MdPos后，删除文件
     * TODO
     */
    public boolean deleteDir(String path) throws RemoteException;


}
