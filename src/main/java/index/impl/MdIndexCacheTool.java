package index.impl;

import index.model.MdIndex;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Mr-yang on 16-2-19.
 */
public class MdIndexCacheTool {
    private static final Map<String, MdIndex> indexMap = new ConcurrentHashMap<String, MdIndex>();
    private static final Set<String> indexExistSet = Collections.newSetFromMap(
            new ConcurrentHashMap<String, Boolean>()
    );

    public static MdIndex getMdIndexFromCache(String path) {
        return indexMap.get(path);
    }

    public static void setMdIndexToCache(String path, MdIndex mdIndex) {
        indexMap.put(path, mdIndex);
    }

    public static void setMdIndexToMap(long pCode, String dirName) {
        indexExistSet.add(pCode + dirName);
    }

    public static void removeMdIndexToMap(long pCode, String dirName) {
        indexExistSet.remove(pCode + dirName);
    }

    public static void clearMdIndexInMap() {
        indexExistSet.clear();
    }

    public static boolean isMdIndexInMap(long pCode, String dirName){
        return indexExistSet.contains(pCode+dirName);
    }

    public static void removeMdIndex(String path) {
        indexMap.remove(path);
    }
}
