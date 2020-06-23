package com.dgg.multidialog.adapter;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Description ViewHolder 借鉴第三方框架的优秀的写法
 * @Author LiuLang
 * @Date 2019/12/17 10:17
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    protected final SparseArray<View> mViews;
    protected View mConvertView;


    public ViewHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        mConvertView = itemView;
    }


    /**
     * 通过控件的Id获取对应的控件，如果没有则加入mViews，则从item根控件中查找并保存到mViews中
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public ViewHolder setBgColor(@IdRes int resID, int color) {
        getView(resID).setBackgroundColor(color);
        return this;
    }

    public ViewHolder setBgDrawable(@IdRes int resID, Drawable drawable) {
        getView(resID).setBackground(drawable);
        return this;
    }

    public ViewHolder setText(@IdRes int resID, String text) {
        ((TextView) getView(resID)).setText(text);
        return this;
    }

    public ViewHolder setTextSize(@IdRes int resID, int spSize) {
        ((TextView) getView(resID)).setTextSize(spSize);
        return this;
    }

    public ViewHolder setVisibility(@IdRes int resID, @Visibility int visibility) {
        switch (visibility) {
            case VISIBLE:
                getView(resID).setVisibility(View.VISIBLE);
                break;
            case INVISIBLE:
                getView(resID).setVisibility(View.INVISIBLE);
                break;
            case GONE:
                getView(resID).setVisibility(View.GONE);
                break;

        }
        return this;

    }

    public ViewHolder setTextColor(int id, int textColor) {
        ((TextView) getView(id)).setTextColor(textColor);
        return this;
    }


    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }

    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;

    public static final int GONE = 0x00000008;
}
