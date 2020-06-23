package com.dgg.multidialog;

/**
 * @Description 多级选择器每次选中后的回调
 * @Author LiuLang
 * @Date 2019/12/19 9:42
 */
public interface MultistageChooseListener<T extends MultistageData> {
    void onChoose(int position, T data);
}
