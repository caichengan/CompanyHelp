package com.xht.android.companyhelp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xht.android.companyhelp.net.APIListener;
import com.xht.android.companyhelp.net.VolleyHelpApi;
import com.xht.android.companyhelp.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-10-20.
 *
 * an 评价与打分
 */
public class AppraiseActivity extends Activity implements View.OnClickListener {

    private   int ratingNum;
    private static final String TAG = "AppraiseActivity";
    private String orderid;//订单id
    private int uid;//用户id

    private ProgressDialog mProgDoal;//进度条
    private EditText mEditChat;
    private ImageButton starchat1;
    private ImageButton starchat2;
    private ImageButton starchat3;
    private ImageButton starchat4;
    private ImageButton starchat5;
    private Button mChatAppraise;
    private List<ImageButton> chatStar=new ArrayList<>();
    private String mChat;
    private int whichItem;
    private String flowId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appraise);

        TextView mCustomView = new TextView(this);
        mCustomView.setGravity(Gravity.CENTER);
        mCustomView.setText("任务池");
        mCustomView.setTextSize(18);
        final ActionBar aBar = getActionBar();
        aBar.setCustomView(mCustomView,
                new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int change = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM;
        aBar.setDisplayOptions(change);

        initialize();
        chatStar.add(starchat1);
        chatStar.add(starchat2);
        chatStar.add(starchat3);
        chatStar.add(starchat4);
        chatStar.add(starchat5);
        orderid = getIntent().getStringExtra("orderId");
        flowId = getIntent().getStringExtra("flowId");
        uid = getIntent().getIntExtra("uid", -1);
        whichItem = getIntent().getIntExtra("whichItem", -1);
        mChatAppraise.setOnClickListener(this);

        starchat1.setOnClickListener(this);
        starchat2.setOnClickListener(this);
        starchat3.setOnClickListener(this);
        starchat4.setOnClickListener(this);
        starchat5.setOnClickListener(this);

       // starchat2.setImageResource(R.drawable.list_icon_star_big_full);
       // starchat5.setBackgroundResource(R.drawable.list_icon_star_big_full);
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
    @Override
    public void onClick(View v) {
        clearButton();
        switch (v.getId()) {
            case R.id.mChatAppraise:
                sureButton();
                break;
            case R.id.star_chat1:
                ratingNum=1;
                //App.getInstance().showToast("----"+ratingNum);
                break;
            case R.id.star_chat2:
                ratingNum=2;
               // App.getInstance().showToast("----"+ratingNum);
                break;
            case R.id.star_chat3:
                ratingNum=3;
               // App.getInstance().showToast("----"+ratingNum);
                break;
            case R.id.star_chat4:
                ratingNum=4;
                //App.getInstance().showToast("----"+ratingNum);
                break;
            case R.id.star_chat5:
                ratingNum=5;
                //App.getInstance().showToast("----"+ratingNum);
                break;

        }
        //App.getInstance().showToast("----"+ratingNum);
        if (ratingNum>0){
            for (int i=0;i<ratingNum;i++){
                //chatStar.get(i).setImageResource(R.drawable.list_icon_star_big_full);
                chatStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_full);
            }
        }
    }

    private void clearButton(){

        for (int i=0;i<chatStar.size();i++){
            chatStar.get(i).setBackgroundResource(R.drawable.list_icon_star_big_empty);
        }

    }
    private void sureButton(){
        Intent intent = getIntent();
        mChat = mEditChat.getText().toString().trim();
        LogHelper.i(TAG, "----" + "-" + mChat);
        if (TextUtils.isEmpty(mChat)) {
            App.getInstance().showToast("请先输入评论。");
            return;
        }
        intent.putExtra("chat", mChat);
        intent.putExtra("whichItem", whichItem);
        if (ratingNum > 0) {
            intent.putExtra("ratingNum", ratingNum);
        } else {
            App.getInstance().showToast("请先点评。");
            return;
        }
        //App.getInstance().showToast("ratingNum:" + ratingNum + "--" + mChat);

       // setResult(10, intent);

        //提交评价
        postListChatData(intent);

    }

    //提交评价
    private void postListChatData(final Intent intent) {

        JSONObject obj=new JSONObject();
        try {//orderId、flowId、commentContent、commentLevel

            obj.put("flowId",flowId);
            obj.put("orderId",orderid);
            obj.put("commentLevel",ratingNum);
            obj.put("commentContent",mChat);
          LogHelper.i(TAG,"-----ratingNum:" + ratingNum + "--flowId" + flowId);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogHelper.i(TAG,"-----obj---:" + obj);
        createProgressDialog("正在提交评论...");
        VolleyHelpApi.getInstance().postChatAppriase(uid, obj, new APIListener() {
            @Override
            public void onResult(Object result) {

                //App.getInstance().showToast(result.toString());
                setResult(10, intent);
                dismissProgressDialog();
                finish();
            }
            @Override
            public void onError(Object e) {
                dismissProgressDialog();
                App.getInstance().showToast(e.toString());
            }

        });


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

    private void initialize() {

        mEditChat = (EditText) findViewById(R.id.mEditChat);
        starchat1 = (ImageButton) findViewById(R.id.star_chat1);
        starchat2 = (ImageButton) findViewById(R.id.star_chat2);
        starchat3 = (ImageButton) findViewById(R.id.star_chat3);
        starchat4 = (ImageButton) findViewById(R.id.star_chat4);
        starchat5 = (ImageButton) findViewById(R.id.star_chat5);
        mChatAppraise = (Button) findViewById(R.id.mChatAppraise);
    }
}
