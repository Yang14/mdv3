package index.common;

import base.md.MdPos;

import java.util.List;

/**
 * Created by Mr-yang on 16-1-11.
 */
public interface CommonModule {
    /**
     * 生成文件编码
     */
    public long genFCode();

    /**
     * 生成分布编码
     */
    public long genDCode();

    /**
     * 检验分布编码对应的节点能否继续保持新的元数据
     */
    public boolean isDCodeFit(long dCode);

    /**
     * 获取分布编码对应的元数据节点信息
     */
    public MdPos buildMdPos(long dCode);

    /**
     * 生成分布编码对应的元数据节点信息
     */
    public MdPos createMdPos();

    public List<MdPos> buildMdPosList(List<Long> dCodeList);
}
