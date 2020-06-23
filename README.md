# DggMultistageDialog
多级选择器，自由选择器，为了满足理论中的无限级树的选择，DggMultistageDialog借鉴安卓系统文件选择器的思路，实现了无限级树的数据结构选择，最终发现可以实现多种数据结构的选择，因为数据的传递是自由的，只要每次传递一个list集合的数据，就能选择一次，数据之间的关联关系完全交给使用者。

### 使用

      //每次加载数据之前的loading等待框
       View loadingView= LayoutInflater.from(this).inflate(R.layout.loading_view,null,false);
        multistageDialog =new DggMultistageDialog.Builder(this)
                .setTitle("选择所在地区")
                .setCurrentColor(0xff525BDF)
                .setLoadingView(loadingView)
                .addListener(onChooseItemListener)
                .build();
        multistageDialog.show();
        //dialog已经显示出来了，但是没有任何数据，所以我延迟了2秒模拟网络请求成功后添加数据
        //dialog显示之后一定要addData设置数据，否则不会有选择列表
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                multistageDialog.addData(jsonBean);
            }
        },2000);
        
        
        
    private DggMultistageDialog.OnChooseItemListener onChooseItemListener=new DggMultistageDialog.OnChooseItemListener<ProvinceData>() {
        @Override
        public void onChoose(final ProvinceData data, List<ProvinceData> selectData) {
            //这个地方我是从本地解析的json文件，数据是嵌套的树形结构，
            // 所以如果有子节点我就继续让其选择，直到最后一级
            //延迟2秒时为了演示网络加载的一个过程，根据实际情况拉力addData即可
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
    
    
### 可配置选项
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
