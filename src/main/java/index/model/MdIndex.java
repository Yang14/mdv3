package index.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr-yang on 16-2-24.
 */
@Entity("mdIndexes")
public class MdIndex {
    @Id
    ObjectId id;

    @Indexed(value = IndexDirection.ASC, name = "pCode")
    private long pCode;

    @Indexed(value = IndexDirection.ASC, name = "fName")
    private String fName;

    private long fCode;

    @Embedded
    private List<Long> dCodeList = new ArrayList<Long>();

    public MdIndex() {
    }

    public MdIndex(long pCode, String fName, long fCode, List<Long> dCodeList) {

        this.pCode = pCode;
        this.fName = fName;
        this.fCode = fCode;
        this.dCodeList = dCodeList;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MdIndex)) return false;

        MdIndex mdIndex = (MdIndex) o;

        if (fCode != mdIndex.fCode) return false;
        if (pCode != mdIndex.pCode) return false;
        if (dCodeList != null ? !dCodeList.equals(mdIndex.dCodeList) : mdIndex.dCodeList != null) return false;
        if (fName != null ? !fName.equals(mdIndex.fName) : mdIndex.fName != null) return false;
        if (id != null ? !id.equals(mdIndex.id) : mdIndex.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (int) (pCode ^ (pCode >>> 32));
        result = 31 * result + (fName != null ? fName.hashCode() : 0);
        result = 31 * result + (int) (fCode ^ (fCode >>> 32));
        result = 31 * result + (dCodeList != null ? dCodeList.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MdIndex{" +
                "id=" + id +
                ", pCode=" + pCode +
                ", fName='" + fName + '\'' +
                ", fCode=" + fCode +
                ", dCodeList=" + dCodeList +
                '}';
    }

    public long getpCode() {

        return pCode;
    }

    public void setpCode(long pCode) {
        this.pCode = pCode;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public long getfCode() {
        return fCode;
    }

    public void setfCode(long fCode) {
        this.fCode = fCode;
    }

    public List<Long> getdCodeList() {
        return dCodeList;
    }

    public void setdCodeList(List<Long> dCodeList) {
        this.dCodeList = dCodeList;
    }
}
