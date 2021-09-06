package com.yunbao.common.bean;

import java.util.List;

public class ShopTopBean {
    private String id;
    private String name;
    private String parent_id;
    private String pic;

    private List<ShopLeftBean> children;
    private boolean choosed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public List<ShopLeftBean> getChildren() {
        return children;
    }

    public void setChildren(List<ShopLeftBean> children) {
        this.children = children;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
