package client.service.dao;

import base.md.MdAttr;
import base.md.MdPos;

import java.util.List;

/**
 * Created by Mr-yang on 16-2-25.
 */
public interface SSDBDao {

    public boolean insertMd(MdPos mdPos, String name, MdAttr mdAttr);

    public MdAttr findFileMd(MdPos mdPos, String name);

    public List<MdAttr> listDir(MdPos mdPos);

    public boolean renameMd(MdPos mdPos, String oldName, String newName);

    public boolean deleteMd(MdPos mdPos, String name);

    public boolean deleteDirMd(MdPos mdPos);
}
