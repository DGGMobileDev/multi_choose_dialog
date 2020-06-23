package com.dgg.multidialog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 多级选择器演示
 * @Author LiuLang
 * @Date 2020/06/22 9:31
 */

public class MainActivity extends AppCompatActivity {

    private DggMultistageDialog multistageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //按钮点击事件，写在xml里面的

    /**
     * 这个地方是解析本地json文件的数据，需要根据自己的需求构造数据，
     * 该数据可以是本地json解析，也可以是网络请求的数据
     * @param view
     */
    public void showDialog(View view) {
        if (isLoaded && jsonBean!=null&&jsonBean.size()>0){
            showProvinceDialog();
        }else {
            isLoaded=false;
            mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        }
    }


    private void showProvinceDialog() {
//        TextView textView=new TextView(this);
//        textView.setText("数据加载中...");
//        textView.setTextColor(Color.RED);
        View loadingView= LayoutInflater.from(this).inflate(R.layout.loading_view,null,false);
        multistageDialog =new DggMultistageDialog.Builder(this)
                .setTitle("选择所在地区")
                .setCurrentColor(0xff525BDF)
//                .setLoadingView(textView)
                .setLoadingView(loadingView)
                .addListener(onChooseItemListener)
                .build();
//        multistageDialog.setListener(onChooseItemListener);
        multistageDialog.show();
        //dialog已经显示出来了，但是没有任何数据，所以我延迟了2秒模拟网络请求成功后添加数据
        //dialog显示之后一定要addData设置数据，否则不会有选择列表
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                multistageDialog.addData(jsonBean);
            }
        },2000);
    }

    private DggMultistageDialog.OnChooseItemListener onChooseItemListener=new DggMultistageDialog.OnChooseItemListener<ProvinceData>() {
        @Override
        public void onChoose(final ProvinceData data, List<ProvinceData> selectData) {
            //这个地方我是从本地解析的json文件，数据是嵌套的树形结构，
            // 所以如果有子节点我就继续让其选择，直到最后一级
            //演示2秒时为了演示网络加载的一个过程，根据实际情况拉力addData即可
            if (data!=null&&data.getChildren()!=null&&data.getChildren().size()>0){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        multistageDialog.addData(data.getChildren());
                    }
                },2000);
            }else {
                multistageDialog.dismiss();
                String value="";
                for (ProvinceData provinceData:selectData){
                    value=value+provinceData.getName()+"-";
                }
                Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();
            }
        }
    };

    //========================以下部分都是数据解析过程逻辑===============================


    private ArrayList<MultistageData> jsonBean;
    private void initJsonData() {//解析数据

        String JsonData = new GetJsonDataUtil().getJson(this, "data.json");//获取assets目录下的json文件数据

        jsonBean = parseData(JsonData);//用Gson 转成实体

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    private ArrayList<MultistageData> parseData(String jsonData) {
        ArrayList<MultistageData> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(jsonData);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                MultistageData entity = gson.fromJson(data.optJSONObject(i).toString(), ProvinceData.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private static boolean isLoaded = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    showProvinceDialog();
                    break;

                case MSG_LOAD_FAILED:
                    break;
            }
        }
    };
}
