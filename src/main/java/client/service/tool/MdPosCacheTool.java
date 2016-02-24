package client.service.tool;


import base.md.MdPos;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mr-yang on 16-2-19.
 */
public class MdPosCacheTool {
    private static final Map<String, List<MdPos>> posMap = new ConcurrentHashMap<String, List<MdPos>>();


    public static List<MdPos> getMdPosListFromCache(String path) {
        return posMap.get(path);
    }

    public static void setMdPosListToCache(String path, List<MdPos> mdPosList) {
        posMap.put(path, mdPosList);
    }

    public static void removeMdPosList(String path){
        posMap.remove(path);
    }
}
