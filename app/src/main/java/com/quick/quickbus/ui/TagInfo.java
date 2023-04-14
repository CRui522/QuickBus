package com.quick.quickbus.ui;

/**
 * 流式布局中每个item对应的bean
 */
public class TagInfo {

    private int mTagType;               // tag类型
    private String mTagDesc;            // tag描述文本

    public String getTagDesc() {
        return mTagDesc;
    }

    public void setTagDesc(String mTagName) {
        this.mTagDesc = mTagName;
    }

    public int getTagType() {
        return mTagType;
    }

    public void setTagType(int mTagType) {
        this.mTagType = mTagType;
    }
}
