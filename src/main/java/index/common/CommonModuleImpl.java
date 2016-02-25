package index.common;

import base.md.MdPos;
import base.tool.PortEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mr-yang on 16-1-11.
 */
public class CommonModuleImpl implements CommonModule {

    private static Random random = new Random(Long.MAX_VALUE);

    @Override
    public long genFCode() {
        return random.nextLong();
    }

    @Override
    public long genDCode() {
        return Long.valueOf(new Random().nextInt() & 0x0FFFFFFFF);
    }

    @Override
    public boolean isDCodeFit(long dCode) {
        return true;
    }

    @Override
    public MdPos buildMdPos(long dCode) {
        MdPos md = new MdPos();
        if (dCode % 3 == 0) {
            md.setIp("node-03");
        } else if (dCode % 2 == 0) {
            md.setIp("node-02");
        } else {
            md.setIp("node-01");
        }
        md.setIp("192.168.0.13");
        md.setdCode(dCode);
        md.setPort(PortEnum.SSDB_PORT);
        return md;
    }

    @Override
    public MdPos createMdPos() {
        return buildMdPos(genDCode());
    }

    @Override
    public List<MdPos> buildMdPosList(List<Long> dCodeList) {
        if (dCodeList == null) {
            return null;
        }
        List<MdPos> mdPoses = new ArrayList<MdPos>();
        for (long code : dCodeList) {
            mdPoses.add(buildMdPos(code));
        }
        return mdPoses;
    }
}
