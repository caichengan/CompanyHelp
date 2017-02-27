package com.xht.android.companyhelp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;
import com.xht.android.companyhelp.net.APIListener;
import com.xht.android.companyhelp.net.VolleyHelpApi;
import com.xht.android.companyhelp.util.BitmapUtils;
import com.xht.android.companyhelp.util.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016-10-20.
 *  an  查看进度
 */
public class CheckProgress extends Activity{

    private static final String TAG = "CheckProgress";
    private ListView progresslistview;
    private int uid;
    private String orderid;
    private ProgressDialog mProgDoal;
    private ProgressAdapter adapter;

    private List<ProgressBean> mListProgress=new ArrayList<>();

    private List<ImageView> progressStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_progress);
        TextView mCustomView = new TextView(this);
        mCustomView.setGravity(Gravity.CENTER);
        mCustomView.setText("进度查看");
        mCustomView.setTextSize(18);
        final ActionBar aBar = getActionBar();
        aBar.setCustomView(mCustomView,
                new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int change = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM;
        aBar.setDisplayOptions(change);

        /*bundle.putInt("uid",uid);
        bundle.putString("orderid",orderid);*/

        Intent intent = getIntent();
        Bundle progress = intent.getBundleExtra("progress");
        uid = progress.getInt("uid");
        orderid = progress.getString("orderid");

        LogHelper.i(TAG,"-----"+ uid+"--"+orderid);


        progresslistview = (ListView) findViewById(R.id.progress_listview);


        //getDateTime(System.currentTimeMillis());
        progressStar=new ArrayList<>();

       //范问获取数据
        getProgressData(orderid);


    }

    ViewHolder holder;

    class  ProgressAdapter extends BaseAdapter{


        private long mStartTime=0;
        private long mEndTime=0;

        @Override
        public int getCount() {
            return mListProgress.size();
        }

        @Override
        public Object getItem(int position) {
            return mListProgress.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(CheckProgress.this,R.layout.item_check_progress,null);
                holder. mProgressImg = (ImageView) convertView.findViewById(R.id.mProgressImg);
                holder. mStartTime = (TextView) convertView.findViewById(R.id.mStartTime);
                holder. mEndTime = (TextView) convertView.findViewById(R.id.mEndTime);
                holder. Img01 = (ImageView) convertView.findViewById(R.id.Img01);
                holder.  Img02 = (ImageView) convertView.findViewById(R.id.Img02);
                holder.  Img03 = (ImageView) convertView.findViewById(R.id.Img03);
                holder.  Img04 = (ImageView) convertView.findViewById(R.id.Img04);
                holder.  Img05 = (ImageView) convertView.findViewById(R.id.Img05);
                holder.  Img06 = (ImageView) convertView.findViewById(R.id.Img06);
                holder.  Img07 = (ImageView) convertView.findViewById(R.id.Img07);
                holder.  Img08 = (ImageView) convertView.findViewById(R.id.Img08);

                holder.mPraiseHeadImg= (ImageView) convertView.findViewById(R.id.mPriaseHeadImg);
                holder.mPraiseName= (TextView) convertView.findViewById(R.id.mPriaseName);
                holder.mFlowNum= (TextView) convertView.findViewById(R.id.mFlowNum);
                holder.mPraisePhone= (TextView) convertView.findViewById(R.id.mPriasePhone);
                holder.mFinallyText= (TextView) convertView.findViewById(R.id.mFinallyText);


                holder.start0= (ImageView) convertView.findViewById(R.id.star_img1);
                holder.start1= (ImageView) convertView.findViewById(R.id.star_img2);
                holder.start2= (ImageView) convertView.findViewById(R.id.star_img3);
                holder.start3= (ImageView) convertView.findViewById(R.id.star_img4);
                holder.start4= (ImageView) convertView.findViewById(R.id.star_img5);

                holder.  mTextAppriase = (TextView) convertView.findViewById(R.id.mTextAppriase);
                holder.  mLinAppriase = (LinearLayout)convertView. findViewById(R.id.mLinAppriase);
                holder.  mProgressAppraise = (Button)convertView. findViewById(R.id.mProgressAppraise);
                holder.  mStyleName = (TextView)convertView. findViewById(R.id.mStyleName);

                convertView.setTag(holder);
            }else{
                holder= (ViewHolder) convertView.getTag();
            }
            progressStar.clear();
            progressStar.add(holder.start0);
            progressStar.add(holder.start1);
            progressStar.add(holder.start2);
            progressStar.add(holder.start3);
            progressStar.add(holder.start4);

            ProgressBean item=mListProgress.get(position);
            String endTime = item.getmEndTime();

            String praiseName = item.getmPraiseName();
            String praisePhone = item.getmPraisePhone();
            String praiseStatus = item.getmPraiseStatus();
            String headImg = item.getmHeadImg();

            LogHelper.i(TAG,"---------"+praiseName+"-"+praiseStatus+"---"+headImg+"--="+praisePhone);
            String startTime = item.getmStartTime();
            String styleName = item.getmStyleName();
            String remark = item.getmRemark();//评语
            int star = item.getmStar();//星数
            final String imgUrl1 = item.getmImgUrl1();
            final String imgUrl2 = item.getmImgUrl2();
            final String imgUrl3 = item.getmImgUrl3();
            final String imgUrl4 = item.getmImgUrl4();
            final String imgUrl5 = item.getmImgUrl5();
            final String imgUrl6 = item.getmImgUrl6();
            final String imgUrl7 = item.getmImgUrl7();
            final String imgUrl8 = item.getmImgUrl8();

            holder.mPraiseName.setText(praiseName);
            holder.mPraisePhone.setText(praisePhone);
            holder.mFlowNum.setText(item.getFlowId());
            // TODO
            item.setImgTag1(imgUrl1);
            holder.Img01.setTag(imgUrl1);
            LogHelper.i(TAG, "-------imgUrl1-----" + imgUrl1);
            //图片二
            item.setImgTag2(imgUrl2);
            holder.Img02.setTag(imgUrl2);
            LogHelper.i(TAG, "-------imgUrl2-----" + imgUrl2);
            //图片三
            item.setImgTag3(imgUrl3);
            holder.Img03.setTag(imgUrl3);
            LogHelper.i(TAG, "-------imgUrl3-----" + imgUrl3);
            //图片四
            item.setImgTag4(imgUrl4);
            holder.Img04.setTag(imgUrl4);
            LogHelper.i(TAG, "-------imgUrl4-----" + imgUrl4);
            //图片五
            item.setImgTag5(imgUrl5);
            holder.Img05.setTag(imgUrl5);
            LogHelper.i(TAG, "-------imgUrl5-----" + imgUrl5);
            //图片六
            item.setImgTag6(imgUrl6);
            holder.Img06.setTag(imgUrl6);
            LogHelper.i(TAG, "-------imgUrl6-----" + imgUrl6);
            //图片七
            item.setImgTag7(imgUrl7);
            holder.Img07.setTag(imgUrl7);
            LogHelper.i(TAG, "-------imgUrl7-----" + imgUrl7);
            //图片八
            item.setImgTag8(imgUrl8);
            holder.Img08.setTag(imgUrl8);
            LogHelper.i(TAG, "-------imgUrl8-----" + imgUrl8);


           if (!headImg.equals("null")){
               ImageRequest mRequesttou = null;
               BitmapUtils.loadImgageUrl(mRequesttou, headImg, holder.mPraiseHeadImg);
           }

            if (praiseStatus.equals("5")){
                holder.mProgressAppraise.setBackgroundResource(R.drawable.btn_background_circle);
                holder.mProgressAppraise.setClickable(true);
                holder.mProgressAppraise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String flowId = mListProgress.get(position).getFlowId();
                        Intent intent=new Intent(CheckProgress.this,AppraiseActivity.class);
                        intent.putExtra("whichItem", position);
                        intent.putExtra("orderId",orderid);
                        intent.putExtra("flowId",flowId);
                        intent.putExtra("uid",uid);
                        startActivityForResult(intent,10);
                    }
                });
            }else{
                holder.mProgressAppraise.setClickable(false);
                holder.mProgressAppraise.setBackgroundColor(Color.GRAY);
            }

            if (praiseStatus.equals("5")) {

                if (position==mListProgress.size()-1) {
                    holder.mFinallyText.setText("要先评价，才能看到下一步");
                }else{
                    holder.mFinallyText.setText("");
                }

                if (!imgUrl1.equals("null")) {
                    ImageRequest mRequest = null;
                    String imgTag1 = item.getImgTag1();
                    holder.Img01.setVisibility(View.VISIBLE);
                    BitmapUtils.loadImgageUrl(imgTag1, mRequest, imgUrl1, holder.Img01);
                    LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl1);

                    LogHelper.i(TAG, "-------onClick---imgUrl1-----" + imgUrl1);
                    if (!imgUrl2.equals("null")) {
                        holder.Img02.setVisibility(View.VISIBLE);
                        ImageRequest mRequest1 = null;
                        String imgTag2 = item.getImgTag2();
                        BitmapUtils.loadImgageUrl(imgTag2, mRequest1, imgUrl2, holder.Img02);
                        LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl2);
                        LogHelper.i(TAG, "-------onClick---imgUrl2-----" + imgUrl2);
                        if (!imgUrl3.equals("null")) {
                            holder.Img03.setVisibility(View.VISIBLE);
                            ImageRequest mRequest3 = null;
                            String imgTag3 = item.getImgTag3();
                            BitmapUtils.loadImgageUrl(imgTag3, mRequest3, imgUrl3, holder.Img03);
                            LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl3);


                            if (!imgUrl4.equals("null")) {
                                holder.Img04.setVisibility(View.VISIBLE);
                                ImageRequest mRequest4 = null;
                                String imgTag4 = item.getImgTag4();
                                BitmapUtils.loadImgageUrl(imgTag4, mRequest4, imgUrl4, holder.Img04);
                                LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl4);

                                if (!imgUrl5.equals("null")) {
                                    holder.Img05.setVisibility(View.VISIBLE);
                                    ImageRequest mRequest5 = null;
                                    String imgTag5 = item.getImgTag5();
                                    BitmapUtils.loadImgageUrl(imgTag5, mRequest5, imgUrl5, holder.Img05);
                                    LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl5);

                                    if (!imgUrl6.equals("null")) {
                                        holder.Img06.setVisibility(View.VISIBLE);
                                        ImageRequest mRequest6 = null;
                                        String imgTag6 = item.getImgTag6();
                                        BitmapUtils.loadImgageUrl(imgTag6, mRequest6, imgUrl6, holder.Img06);
                                        LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl6);


                                        if (!imgUrl7.equals("null")) {
                                            holder.Img07.setVisibility(View.VISIBLE);
                                            ImageRequest mRequest7 = null;
                                            String imgTag7 = item.getImgTag7();
                                            BitmapUtils.loadImgageUrl(imgTag7, mRequest7, imgUrl7, holder.Img07);
                                            LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl7);
                                            if (!imgUrl8.equals("null")) {
                                                holder.Img08.setVisibility(View.VISIBLE);
                                                ImageRequest mRequest8 = null;
                                                String imgTag8 = item.getImgTag8();
                                                BitmapUtils.loadImgageUrl(imgTag8, mRequest8, imgUrl8, holder.Img08);
                                                LogHelper.i(TAG, "-------position-----" + position + "--" + imgUrl8);
                                                holder.Img08.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("imgUrl", imgUrl8);
                                                        startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                                    }
                                                });
                                            } else {
                                                //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                                                holder.Img08.setVisibility(View.INVISIBLE);
                                            }

                                            holder.Img07.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("imgUrl", imgUrl7);
                                                    startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                                }
                                            });
                                        } else {
                                            //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                                            holder.Img07.setVisibility(View.INVISIBLE);
                                            holder.Img08.setVisibility(View.INVISIBLE);
                                        }

                                        holder.Img06.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("imgUrl", imgUrl6);
                                                startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                            }
                                        });
                                    } else {
                                        //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                                        holder.Img06.setVisibility(View.INVISIBLE);
                                        holder.Img07.setVisibility(View.INVISIBLE);
                                        holder.Img08.setVisibility(View.INVISIBLE);
                                    }

                                    holder.Img05.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("imgUrl", imgUrl5);
                                            startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                        }
                                    });
                                } else {
                                    //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                                    holder.Img05.setVisibility(View.INVISIBLE);
                                    holder.Img06.setVisibility(View.INVISIBLE);
                                    holder.Img07.setVisibility(View.INVISIBLE);
                                    holder.Img08.setVisibility(View.INVISIBLE);
                                }


                                holder.Img04.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("imgUrl", imgUrl4);
                                        startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                    }
                                });
                            } else {
                                //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                                holder.Img04.setVisibility(View.INVISIBLE);
                                holder.Img05.setVisibility(View.INVISIBLE);
                                holder.Img06.setVisibility(View.INVISIBLE);
                                holder.Img07.setVisibility(View.INVISIBLE);
                                holder.Img08.setVisibility(View.INVISIBLE);
                            }

                            holder.Img03.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("imgUrl", imgUrl3);
                                    startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                                }
                            });
                        } else {
                            //  holder.Img03.setImageResource(R.mipmap.ic_launcher);
                            holder.Img03.setVisibility(View.INVISIBLE);
                            holder.Img04.setVisibility(View.INVISIBLE);
                            holder.Img05.setVisibility(View.INVISIBLE);
                            holder.Img06.setVisibility(View.INVISIBLE);
                            holder.Img07.setVisibility(View.INVISIBLE);
                            holder.Img08.setVisibility(View.INVISIBLE);
                        }
                        holder.Img02.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("imgUrl", imgUrl2);
                                startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                            }
                        });
                    } else {
                        holder.Img02.setVisibility(View.INVISIBLE);
                        holder.Img03.setVisibility(View.INVISIBLE);
                        holder.Img04.setVisibility(View.INVISIBLE);
                        holder.Img05.setVisibility(View.INVISIBLE);
                        holder.Img06.setVisibility(View.INVISIBLE);
                        holder.Img07.setVisibility(View.INVISIBLE);
                        holder.Img08.setVisibility(View.INVISIBLE);
                    }
                    holder.Img01.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("imgUrl", imgUrl1);
                            startActivityNumber(CheckProgress.this, bundle, SpaceImageDetailActivity.class);
                        }
                    });
                } else {
                    holder.Img01.setVisibility(View.INVISIBLE);
                    holder.Img02.setVisibility(View.INVISIBLE);
                    holder.Img03.setVisibility(View.INVISIBLE);
                    holder.Img04.setVisibility(View.INVISIBLE);
                    holder.Img05.setVisibility(View.INVISIBLE);
                    holder.Img06.setVisibility(View.INVISIBLE);
                    holder.Img07.setVisibility(View.INVISIBLE);
                    holder.Img08.setVisibility(View.INVISIBLE);
                }
            }else{
                holder.Img01.setVisibility(View.INVISIBLE);
                holder.Img02.setVisibility(View.INVISIBLE);
                holder.Img03.setVisibility(View.INVISIBLE);
                holder.Img04.setVisibility(View.INVISIBLE);
                holder.Img05.setVisibility(View.INVISIBLE);
                holder.Img06.setVisibility(View.INVISIBLE);
                holder.Img07.setVisibility(View.INVISIBLE);
                holder.Img08.setVisibility(View.INVISIBLE);


                if (position==mListProgress.size()-1) {
                    holder.mFinallyText.setText("要先评价，才能看到下一步");
                }else{
                    holder.mFinallyText.setText("");
                }
            }

            //显示时间
            if (!startTime.equals("null")) {
                mStartTime = Long.parseLong(startTime);
                LogHelper.i(TAG,"-----"+mStartTime);
                holder.mStartTime.setText(getDateTime(mStartTime));
            }
            if (!endTime.equals("null")) {
                mEndTime = Long.parseLong(endTime);
                holder. mEndTime.setText(getDateTime(mEndTime));
            }

            holder. mStyleName.setText(styleName);
            if (remark.equals("null")){
                holder.mTextAppriase.setText("");
            }else{
                holder.mTextAppriase.setText(remark);
            }
                if (star > 0) {
                    LogHelper.i(TAG,"----star-"+star);
                    for (int i = 0; i < star; i++) {  //TODO
                        progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_full);
                        LogHelper.i(TAG,"----full-"+i);
                    }
                    if (star<progressStar.size()){
                        for (int i = star; i < progressStar.size(); i++) {  //TODO
                            progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_empty);
                            LogHelper.i(TAG,"----full-"+i);
                        }
                    }
                    holder.mProgressAppraise.setVisibility(View.GONE);
                }else{
                    int size = progressStar.size();
                    for (int i = 0; i < size; i++) {  //TODO
                        progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_empty);
                        LogHelper.i(TAG,"---empty--"+i);
                    }
                    holder.mProgressAppraise.setVisibility(View.VISIBLE);
                }
           // adapter.notifyDataSetChanged();
            if(TextUtils.isEmpty(remark)){
                holder.mTextAppriase.setText("");
                for (int i = 0; i < 5; i++) {  //TODO
                    holder.mProgressAppraise.setVisibility(View.VISIBLE);
                    progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_empty);
                    adapter.notifyDataSetChanged();
                }
            }

            if (praiseStatus.equals("5") && item.getFlowId().equals("20")){
                holder.mFinallyText.setText("恭喜办证成功完成");
            }

            return convertView;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==10){
            int ratingNum = data.getIntExtra("ratingNum", -1);
            String mChat = data.getStringExtra("chat");
            LogHelper.i(TAG,"------mChat----"+mChat+"---"+ratingNum+mChat);
            int whichItem = data.getIntExtra("whichItem", -1);
            if (whichItem!=-1) {
                mListProgress.get(whichItem).setmStar(ratingNum);
                mListProgress.get(whichItem).setmRemark(mChat);
                LogHelper.i(TAG,"------1323445----"+whichItem+"---"+ratingNum+mChat);
                for (int i=0;i<ratingNum;i++){  //TODO
                    progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_full);

                }
                //holder.mTextAppriase.setText(mChat);
            }
            LogHelper.i(TAG,"------1323445----"+whichItem+"---"+ratingNum+mChat);

           // App.getInstance().showToast("---Check"+ratingNum+mChat);
            adapter.notifyDataSetChanged();
            LogHelper.i(TAG,"------132----"+whichItem+"---"+ratingNum+mChat);
            mListProgress.clear();
            //范问获取数据
            getProgressData(orderid);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Bundle progress = getIntent().getBundleExtra("progress");
        uid = progress.getInt("uid");
        orderid = progress.getString("orderid");

        LogHelper.i(TAG,"----onRestart---"+ uid+"--"+orderid);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearButton(){

        for (int i=0;i<progressStar.size();i++){
            progressStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_empty);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        LogHelper.i(TAG,"-------"+mListProgress.size());
    }

    /**
     * 根据订单id获取进度列表 TODO
     */
    private void getProgressData(String orderId) {
        createProgressDialog("数据加载中...");
        VolleyHelpApi.getInstance().getProgressYeWu(orderId, new APIListener() {

            @Override
            public void onResult(Object result) {
                LogHelper.i(TAG, "-----注册公司---进度信息--" + result.toString());

               //[{"employeeId":4,"commentLevel":null,"file2":null,"file3":null,"progressStatus":1,"emphead":"http:\/\/www.xiaohoutai.com.cn:8888\/XHT\/empheadportraitController\/downloadEmpheadportrait?fileName=1480432416517bzzbz_e4_1480432413387.jpg","file0":null,"file1":null,"file6":null,"file7":null,"file4":null,"file5":null,"endTime":null,"employeeName":"蔡成安",
                // "startTime":"1481099401751","serviceId":22,"commentContent":null,"telephone":"13531829360","flowName":"资料交接","flowId":1,"orderId":4}]

                JSONObject jsonoj= (JSONObject) result;
                String code = jsonoj.optString("code");
                if (code.equals("0")){
                    App.getInstance().showToast("暂无数据");
                    dismissProgressDialog();

                }else {
                    LogHelper.i(TAG, "-----jsonoj----" + jsonoj.toString());
                    JSONArray jsonArray = jsonoj.optJSONArray("entity");
                    LogHelper.i(TAG, "-----jsonArray----" + jsonArray.toString());
                    int len = jsonArray.length();
                    for (int i = 0; i < len; i++) {
                        ProgressBean bean = new ProgressBean();
                        try {
                            JSONObject item = (JSONObject) jsonArray.get(i);

                            bean.setmStyleName(item.optString("flowName"));
                            LogHelper.i(TAG, "--------" + item.optString("startTime") + "---" + item.optString("endTime"));
                            String startTime = item.optString("startTime");
                            String endTime = item.optString("endTime");

                            String commentContent = item.optString("commentContent");

                            String telephone = item.optString("telephone");
                            String emphead = item.optString("emphead");
                            String progressStatus = item.optString("progressStatus");
                            String employeeName = item.optString("employeeName");


                            int flowId = item.optInt("flowId");
                            int commentLevel = item.optInt("commentLevel");
                            bean.setmStartTime(startTime);
                            bean.setmEndTime(endTime);
                            String file0 = item.optString("file0");
                            bean.setmImgUrl1(file0);

                            bean.setmPraiseName(employeeName);
                            bean.setmPraisePhone(telephone);
                            bean.setmHeadImg(emphead);
                            bean.setmPraiseStatus(progressStatus);

                     /*   LogHelper.i(TAG,"------startTime--"+startTime+"---endTime"+endTime);
                        LogHelper.i(TAG,"------file0--"+file0+"---");
                        LogHelper.i(TAG,"------commentContent--"+commentContent+"---commentLevel"+commentLevel+"---flowId"+flowId+"---");*/
                            bean.setmImgUrl2(item.optString("file1"));
                            bean.setmImgUrl3(item.optString("file2"));
                            bean.setmImgUrl4(item.optString("file3"));
                            bean.setmImgUrl5(item.optString("file4"));
                            bean.setmImgUrl6(item.optString("file5"));
                            bean.setmImgUrl7(item.optString("file6"));
                            bean.setmImgUrl8(item.optString("file7"));
                            bean.setmRemark(commentContent);
                            bean.setFlowId(flowId + "");
                            bean.setmStar(commentLevel);

                            mListProgress.add(bean);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new ProgressAdapter();
                    progresslistview.setAdapter(adapter);
                    dismissProgressDialog();
                }
            }
            @Override
            public void onError(Object e) {
                dismissProgressDialog();
                App.getInstance().showToast(e.toString());
            }
        });
    }

    /**
     * 转换时间
     *
     * @param time
     * @return
     */
    public String getDateTime(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        LogHelper.i(TAG,"---------"+sdf.format(date));

        return sdf.format(date);
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔天数
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(long date1,long date2)
    {
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Date date = format.parse(dateStr); // dateStr="2008-1-1 1:21:28"   dateStr;---》Data对象
        // date.getTime();  //获得毫秒值      Data对象---》毫秒值

        int days = (int) ((date2 - date1) / (1000*3600*24));
        return days;
    }

    /**
     * 创建对话框
     *
     * @param title
     */
    private void createProgressDialog(String title) {
        if (mProgDoal == null) {
            mProgDoal = new ProgressDialog(this);
        }
        mProgDoal.setTitle(title);
        mProgDoal.setIndeterminate(true);
        mProgDoal.setCancelable(false);
        mProgDoal.show();
    }
    /**
     * 隐藏对话框
     */
    private void dismissProgressDialog() {
        if (mProgDoal != null) {
            mProgDoal.dismiss();
            mProgDoal = null;
        }
    }

     static class ViewHolder{
          ImageView mProgressImg;
         TextView mStartTime;
          TextView mEndTime;
         ImageView Img01;
          ImageView Img02;
          ImageView Img03;
          ImageView Img04;
          ImageView Img05;
          ImageView Img06;
          ImageView Img07;
          ImageView Img08;

         ImageView mPraiseHeadImg;
         TextView mPraiseName;
         TextView mPraisePhone;
         TextView mFlowNum;
         TextView mFinallyText;

         ImageView start0;
         ImageView start1;
         ImageView start2;
         ImageView start3;
         ImageView start4;

          TableLayout mTabLayout;
          TextView mTextAppriase;//评语
          LinearLayout mLinAppriase;
          Button mProgressAppraise;
          TextView mStyleName;
    }


    /**
     * 携带数据进入一个界面
     */
    public static void startActivityNumber(Activity context, Bundle bundle, Class<?>cls){
        Intent intent=new Intent(context,cls);
        intent.putExtra("bundle",bundle);
        context.startActivity(intent);
    }

}
