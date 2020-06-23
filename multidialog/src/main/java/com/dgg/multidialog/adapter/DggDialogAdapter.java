package com.dgg.multidialog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @Description 列表适配器基类
 * @Author LiuLang
 * @Date 2019/12/17 10:15
 */
public abstract class DggDialogAdapter extends RecyclerView.Adapter<ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.getConvertView().setClickable(true);
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, position);
            }
        });

        onBindView(holder, holder.getLayoutPosition());

    }

    public abstract void onBindView(ViewHolder holder, int position);

    @Override
    public int getItemViewType(int position) {
        return getLayoutID(position);
    }


    public abstract int getLayoutID(int position);

    public void onItemClick(View v, int position) {
    }

}
