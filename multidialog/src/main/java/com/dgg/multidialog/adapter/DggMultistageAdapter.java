package com.dgg.multidialog.adapter;

import android.view.View;
import android.widget.TextView;

import com.dgg.multidialog.MultistageChooseListener;
import com.dgg.multidialog.MultistageData;
import com.dgg.multidialog.R;

import java.util.List;

/**
 * @Description 多级选择器列表适配器
 * @Author LiuLang
 * @Date 2019/12/19 9:31
 */
public class DggMultistageAdapter<T extends MultistageData> extends DggDialogAdapter {
    private List<T> data;
    private int selectedPosition=-1;
    private MultistageChooseListener listener;

    public void setListener(MultistageChooseListener listener) {
        this.listener = listener;
    }

    public DggMultistageAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public void onBindView(ViewHolder holder, int position) {
        TextView tv = holder.getView(R.id.tv_name);
        TextView chooseTv=holder.getView(R.id.tv_choose);
        tv.setText(data.get(position).getValue());
        if (position==selectedPosition){
            tv.setTextColor(data.get(position).getSelectedColor());
            chooseTv.setTextColor(data.get(position).getSelectedColor());
            chooseTv.setVisibility(View.VISIBLE);
        }else {
            tv.setTextColor(data.get(position).getNormalColor());
            chooseTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutID(int position) {
        return R.layout.item_multistage_dialog;
    }

    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    @Override
    public void onItemClick(View v, int position) {
        super.onItemClick(v, position);
        selectedPosition=position;
        if (listener!=null){
            listener.onChoose(position,data.get(position));
        }
        notifyDataSetChanged();
    }
}
