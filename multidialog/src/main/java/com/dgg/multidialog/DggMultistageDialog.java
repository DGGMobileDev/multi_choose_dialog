package com.dgg.multidialog;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dgg.multidialog.adapter.DggMultistageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @Description 多级选择框，构建者模式构建
 * 思路是每传入一个集合就构建一个列表来装载数据
 * * @Author LiuLang
 * @Date 2019/12/18 18:45
 */
public class DggMultistageDialog<T extends MultistageData> extends AlertDialog {

    private Context mContext;
    //用于支持选中项的左右滑动查看
    private HorizontalScrollView horizontalScrollView;
    //用于装在选中项
    private LinearLayout horizontalContainer;
    //装在每级列表
    private FrameLayout viewContainer;
    //第一个选中项的值
    private TextView firstDataTv;
    //弹框标题
    private TextView titleTv;
    private ImageView closeBtn;
    private FrameLayout flLoading;
    private View loadingView;  //在数据传入之前的加载等待框

    private Timer timer;
    private int mWidth;

    private int loadingBgColor;//加载框蒙层颜色
    private int currentColor; //当前选中的颜色
    private int normalColor; //前面选中的正常颜色
    private int closeBtnRes; //关闭按钮的资源
    private String title;
    private int titleColor;
    private int titleSize;
    private int selectSize;

    private OnChooseItemListener listener;

    private List<T> mSelectData=new ArrayList<>();


    /**
     * 设置数据监听器,也可以通过构造器传入
     * @param listener
     */
    public void setListener(OnChooseItemListener listener) {
        this.listener = listener;
    }

    protected DggMultistageDialog(@NonNull Context context) {
        super(context, R.style.StyleDggAlertDialog);
        mContext=context;
    }
    protected DggMultistageDialog(Builder builder) {
        this(builder.context);
        currentColor=builder.currentColor;
        normalColor=builder.normalColor;
        closeBtnRes=builder.closeBtnRes;
        title=builder.title;
        titleColor=builder.titleColor;
        titleSize=builder.titleSize;
        selectSize=builder.selectSize;
        loadingView=builder.loadingView;
        listener=builder.listener;
        loadingBgColor=builder.loadingBgColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dgg_multistage_dialog);
        int height=-2;
        WindowManager wm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        if (wm!=null){
            Point point = new Point();
            wm.getDefaultDisplay().getRealSize(point);
            height= (int) (point.y*0.6f);
            mWidth=point.x;
        }

        final Window window = getWindow();
        if (window == null) return;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, height);
        window.setGravity(Gravity.BOTTOM);//dialog底部弹出
        window.getDecorView().setPadding(0, 0, 0, 0);

        titleTv=findViewById(R.id.tv_title);
        closeBtn=findViewById(R.id.iv_close_btn);
        horizontalScrollView=findViewById(R.id.horizontal_scroll_view);
        horizontalContainer=findViewById(R.id.ll_horizontal_container);
        //第一个选项的值，默认是提示"请选择"
        firstDataTv=findViewById(R.id.tv_first_data);
        firstDataTv.setGravity(Gravity.CENTER);
        firstDataTv.setTextColor(currentColor);
        firstDataTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,selectSize);
        firstDataTv.setPadding(30,0,30,0);
        firstDataTv.setTag(0);
        firstDataTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(v);
            }
        });
        viewContainer=findViewById(R.id.view_container);
        flLoading=findViewById(R.id.fl_loading);
        flLoading.setBackgroundColor(loadingBgColor);
        if (loadingView!=null){
            FrameLayout.LayoutParams layoutParams=
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.CENTER;
            flLoading.addView(loadingView,layoutParams);
        }
        setLayoutTransition();
        if (!TextUtils.isEmpty(title)){
            titleTv.setText(title);
            titleTv.setTextColor(titleColor);
            titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,titleSize);
        }
        if (closeBtnRes!=-1){
            closeBtn.setImageDrawable(ContextCompat.getDrawable(mContext,closeBtnRes));
            closeBtn.setVisibility(View.VISIBLE);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }else {
            closeBtn.setVisibility(View.GONE);
        }
        timer=new Timer();
    }

    /**
     * 传入列表数据
     * @param list
     */
    public void addData(final List<T> list){
        if (list!=null&&list.size()>0){
            flLoading.setVisibility(View.GONE);
            RecyclerView recyclerView=new RecyclerView(mContext);
            recyclerView.setBackgroundColor(0xffffffff);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            DggMultistageAdapter adapter=new DggMultistageAdapter(list);
            adapter.setListener(new MultistageChooseListener<T>() {
                @Override
                public void onChoose(int position, T data) {
                    if (viewContainer.getLayoutTransition().isRunning()){
                        return;
                    }
                    int countTitle=horizontalContainer.getChildCount();
                    int countList=viewContainer.getChildCount();
                    if (countTitle==countList && countList>0){
                        ((TextView)horizontalContainer.getChildAt(countTitle-1)).setText(data.getValue());
                        if (mSelectData.size()>0){
                            mSelectData.set(countTitle-1,data);
                        }else {
                            mSelectData.add(data);
                        }
                        if (listener!=null){
                            flLoading.setVisibility(View.VISIBLE);
                            listener.onChoose(data,mSelectData);
                        }
                    }else if (countList-countTitle==1){
                        if (countTitle>0){
                            ((TextView)horizontalContainer.getChildAt(countTitle-1)).setTextColor(normalColor);
                        }
                        TextView textView=new TextView(mContext);
                        textView.setGravity(Gravity.CENTER);
                        textView.setTextColor(currentColor);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,selectSize);
                        textView.setPadding(30,0,30,0);
                        textView.setText(data.getValue());
                        textView.setTag(horizontalContainer.getChildCount());
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                click(v);
                            }
                        });
                        horizontalContainer.addView(textView,horizontalContainer.getChildCount());
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                            }
                        },100L);
                        mSelectData.add(data);
                        if (listener!=null){
                            flLoading.setVisibility(View.VISIBLE);
                            listener.onChoose(data,mSelectData);
                        }
                    }

                }
            });
            recyclerView.setAdapter(adapter);
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            viewContainer.addView(recyclerView,viewContainer.getChildCount(),layoutParams);
        }
    }

    private void click(View v) {
        if (viewContainer.getLayoutTransition().isRunning()){
            return;
        }
        if (mSelectData.size()<1){
            return;
        }
        int index=(int)v.getTag();
        int countTitle=horizontalContainer.getChildCount()-index-1;
        int countList=viewContainer.getChildCount()-index-1;
        for (int i=0;i<countTitle;i++){
            horizontalContainer.removeViewAt(horizontalContainer.getChildCount()-1);
            mSelectData.remove(mSelectData.size()-1);
        }
        for (int i=0;i<countList;i++){
            viewContainer.removeViewAt(viewContainer.getChildCount()-1);
        }
        ((TextView)v).setTextColor(currentColor);
    }

    /**
     * 为列表的进入和退出添加动画效果
     */
    private void setLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
        // 子View添加到mContainer时的动画
        Animator appearAnim = ObjectAnimator
                .ofFloat(null, "translationX", mWidth, 0)
                .setDuration(transition.getDuration(LayoutTransition.APPEARING));
        transition.setAnimator(LayoutTransition.APPEARING, appearAnim);
        // 子Veiw从mContainer中移除时的动画
        Animator disappearAnim = ObjectAnimator
                .ofFloat(null, "translationX", 0, mWidth)
                .setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnim);

        viewContainer.setLayoutTransition(transition);
    }


    public interface OnChooseItemListener<T extends MultistageData>{
        void onChoose(T currentData, List<T> selectData);
    }


    public static class Builder{

        //当前级选中字体颜色
        private int currentColor=0xff10BBB8;
        //前面级选中的字体颜色
        private int normalColor=0xff333333;
        //关闭按钮图标资源 默认不显示
        private int closeBtnRes=-1;
        //标题颜色
        private int titleColor=0xff333333;
        //标题字体大小
        private int titleSize=18;
        //选中了的字体大小
        private int selectSize=14;
        private Context context;
        //标题文本
        private String title;
        //数据加载框
        private View loadingView;
        //数据选中监听回调
        private OnChooseItemListener listener;
        //加载框蒙层背景颜色,默认是全透明
        private int loadingBgColor=0x00000000;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        public Builder setCurrentColor(@ColorInt int currentColor) {
            this.currentColor = currentColor;
            return this;
        }

        public Builder setNormalColor(@ColorInt int normalColor) {
            this.normalColor = normalColor;
            return this;
        }

        public Builder setLoadingBgColor(@ColorInt int loadingBgColor) {
            this.loadingBgColor = loadingBgColor;
            return this;
        }

        public Builder setCloseBtnRes(@DrawableRes int closeBtnRes) {
            this.closeBtnRes = closeBtnRes;
            return this;
        }

        public Builder setTitleColor(@ColorInt int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder setTitleSize(int titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        public Builder setSelectSize(int selectSize) {
            this.selectSize = selectSize;
            return this;
        }

        public Builder setLoadingView(View loadingView) {
            this.loadingView = loadingView;
            return this;
        }
        public Builder addListener(OnChooseItemListener listener) {
            this.listener = listener;
            return this;
        }
        public DggMultistageDialog build(){
            return new DggMultistageDialog(this);
        }

    }
}
