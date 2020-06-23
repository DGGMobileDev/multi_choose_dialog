package com.dgg.multidialog;

import java.util.List;

/**
 * @Description TODO
 * @Author LiuLang
 * @Date 2020/1/3 13:34
 */
public class ProvinceData implements MultistageData {
    private String name;
    private String code;
    private List<ProvinceData> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ProvinceData> getChildren() {
        return children;
    }

    public void setChildren(List<ProvinceData> children) {
        this.children = children;
    }

    @Override
    public String getValue() {
        return this.name;
    }

    @Override
    public int getSelectedColor() {
        return 0xff10BBB8;
    }

    @Override
    public int getNormalColor() {
        return 0xff333333;
    }
}
