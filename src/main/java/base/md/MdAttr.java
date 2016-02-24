package base.md;

import java.io.Serializable;

/**
 * Created by Mr-yang on 16-1-9.
 * 元数据属性信息
 */
public class MdAttr implements Serializable {

    private static final long serialVersionUID = -1L;
    private String name;
    private Boolean type;
    private Short acl;
    private Integer size;
    private Long createTime;
    private Long updateTime;
    private Object otherAttrs;

    public MdAttr() {
    }

    public MdAttr(String name, Boolean type, Short acl, Integer size, Long createTime, Long updateTime, Object otherAttrs) {
        this.name = name;
        this.type = type;
        this.acl = acl;
        this.size = size;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.otherAttrs = otherAttrs;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }

    public Short getAcl() {
        return acl;
    }

    public void setAcl(Short acl) {
        this.acl = acl;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Object getOtherAttrs() {
        return otherAttrs;
    }

    public void setOtherAttrs(Object otherAttrs) {
        this.otherAttrs = otherAttrs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MdAttr{" +

                "name='" + name + '\'' +
                ", type=" + type +
                ", acl=" + acl +
                ", size=" + size +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", otherAttrs=" + otherAttrs +
                '}';
    }
}
