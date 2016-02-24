package client.service.dao;

import base.md.MdAttr;
import base.md.MdPos;
import client.service.tool.ConnTool;
import com.alibaba.fastjson.JSON;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr-yang on 16-2-23.
 */
public class SSDBImpl {
    private static Logger logger = LoggerFactory.getLogger("SSDBImpl");

    public boolean insertMd(MdPos mdPos, String name, MdAttr mdAttr) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        Response response = ssdb.hset(mdPos.getdCode(), name, JSON.toJSONString(mdAttr));
        return response.ok();
    }

    public MdAttr findFileMd(MdPos mdPos, String name) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        Response response = ssdb.hexists(mdPos.getdCode(), name);
        if (!response.ok()) {
            return null;
        }
        return JSON.parseObject(ssdb.hget(mdPos.getdCode(), name).asString(), MdAttr.class);
    }

    public List<MdAttr> listDir(MdPos mdPos) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        Map<String, String> mdAttrMap = ssdb.hgetall(mdPos.getdCode()).mapString();
        List<MdAttr> mdAttrs = new ArrayList<MdAttr>();
        for (String value : mdAttrMap.values()) {
            mdAttrs.add(JSON.parseObject(value, MdAttr.class));
        }
        return mdAttrs;
    }

    public boolean renameMd(MdPos mdPos, String oldName, String newName) {
        SSDB ssdb = ConnTool.getSSDB(mdPos);
        long dCode = mdPos.getdCode();
        Response response = ssdb.hexists(dCode, oldName);
        if (!response.ok()) {
            return false;
        }
        MdAttr mdAttr = JSON.parseObject(ssdb.hget(dCode, oldName).asString(), MdAttr.class);
        mdAttr.setName(newName);
        ssdb.hdel(dCode, oldName);
        response = ssdb.hset(dCode, newName, JSON.toJSONString(mdAttr));
        return response.ok();
    }

}
