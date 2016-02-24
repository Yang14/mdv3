package index.impl;

import index.model.MdIndex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mr-yang on 16-2-19.
 */
public class MdIndexCacheTool {
    private static final Map<String, MdIndex> indexMap = new ConcurrentHashMap<String, MdIndex>();

    public static MdIndex getMdIndexFromCache(String path) {
        return indexMap.get(path);
    }

    public static void setMdIndexToCache(String path, MdIndex mdIndex) {
        indexMap.put(path, mdIndex);
    }

    public static void removeMdIndex(String path){
        indexMap.remove(path);
    }
}
