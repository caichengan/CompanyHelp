package com.xht.android.companyhelp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xht.android.companyhelp.net.APIListener;
import com.xht.android.companyhelp.net.VolleyHelpApi;
import com.xht.android.companyhelp.provider.MyDatabaseManager;
import com.xht.android.companyhelp.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016-8-30. 账户管理
 *
 * author: an
 */
public class ZhangHuManager extends Activity implements View.OnClickListener {
    private static final String TAG = "ZhangHuManager";
   // private String mName;
    private Button mButTiJiao;

  //  private String mPhone;
    private int mUId;//用户id
    private String[]mCompNames;//公司名字数组
    private int[] mCompIds;
    private int mSelectedCompId;//选中哪一个id
    private  int mPrice;
    private ProgressDialog mProgDoal;


    private EditText mEditName;
    private EditText mEditphone;

    private String mName;
    private String mPhone;

    private String getPhone=null;//TODO 还没初始化
    private String getNumber=null;
    private String getName=null;
    private int getUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getBundleExtra("mBundle");
        mUId = bundle.getInt("mUid",0);

        setContentView(R.layout.activity_zhanghuguanli);
        TextView mCustomView = new TextView(this);
        mCustomView.setGravity(Gravity.CENTER);
        mCustomView.setText("修改账户");
        mCustomView.setTextSize(18);
        final ActionBar aBar = getActionBar();
        aBar.setCustomView(mCustomView,
                new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        int change = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM;
        aBar.setDisplayOptions(change);

        mButTiJiao= (Button) findViewById(R.id.zh_submit);
        mEditName= (EditText) findViewById(R.id.ed_new_name);
        mEditphone= (EditText) findViewById(R.id.edd_new_phone);

        LogHelper.i(TAG,"-----"+mUId+"--");
        //获取账户信息
        getZhangHuXinXi(mUId);
        mButTiJiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取数据
                getMessageSubmit();
            }
        });
       // LogHelper.i(TAG,"------"+mUId+"-------"+mPhone);//
    }
    private void getMessageSubmit() {
        mName=mEditName.getText().toString();
        mPhone=mEditphone.getText().toString();

       /* if (TextUtils.isEmpty(mName) && TextUtils.isEmpty(mPhone) ){
            App.getInstance().showToast("请确认至少修改一项才可提交");
            return;
        }*/

        //封装数据提交修改信息
        postButtonSubmit();

    }
    private void postButtonSubmit() {
        JSONObject MessJson=new JSONObject();
        LogHelper.i(TAG,"-------------------");
        try {
            MessJson.put("contactId",getUid);
            if (!TextUtils.isEmpty(mName)) {
                MessJson.put("contactName",mName );
                LogHelper.i(TAG,"-------------name------"+mName);
            }else{
                App.getInstance().showToast("数据不可为空");
                return;
            }

            if (!TextUtils.isEmpty(mPhone)) {
                MessJson.put("telephone",mPhone );
                LogHelper.i(TAG,"-------------mPhone------"+mPhone);
            }else{
                App.getInstance().showToast("数据不可为空");
                return;
            }


            if (mName.equals(getName)&&mPhone.equals(getPhone)){
                App.getInstance().showToast("请确认至少修改一项才可提交");
                return;
            }



           /* if (mName.equals(getName)){
                App.getInstance().showToast("姓名不能与原名相同");
                return;
            }
            if (mPhone.equals(getPhone)){
                App.getInstance().showToast("手机号码不能相同");
                return;
            }

            if (!TextUtils.isEmpty(mPhone)) {
                MessJson.put("telephone",mPhone );
                LogHelper.i(TAG,"-------------mPhone------"+mPhone);
            }*/

            LogHelper.i(TAG,"----修改信息的所有---"+MessJson.toString());

        }catch(JSONException e) {
            e.printStackTrace();
        }

        //提交账户信息
        createProgressDialog("账户修改中...");
        VolleyHelpApi.getInstance().postXiuGaiZhangHu(mUId, MessJson, new APIListener() {
            @Override
            public void onResult(Object result) {
                LogHelper.i(TAG,"修改账户成功");

                //用户姓名写入数据库
                dismissProgressDialog();

                    LogHelper.i(TAG,"-------修改账户---");
                    //用户姓名写入数据库
                    ContentValues cv = new ContentValues();
                if (mName!=null) {
                    cv.put(MyDatabaseManager.MyDbColumns.NAME, mName);
                }
                if (mPhone!=null) {
                    cv.put(MyDatabaseManager.MyDbColumns.PHONE, mPhone);
                }
                    String where = MyDatabaseManager.MyDbColumns.UID + " = ?";
                    String[] selectionArgs = {String.valueOf(mUId)};
                    ZhangHuManager.this.getContentResolver().update(MyDatabaseManager.MyDbColumns.CONTENT_URI, cv, where, selectionArgs);
                    //数据有更新，更新一下内存的用户变量
                    Intent intent = new Intent(MyFragment.BRO_ACT_S);
                    intent.putExtra(MyFragment.UID_KEY, mUId);
                if (mPhone!=null) {
                    intent.putExtra(MyFragment.PHONENUM_KEY, Long.parseLong(mPhone));
                }
                if (mName!=null) {
                    intent.putExtra(MyFragment.UNAME_KEY, mName);
                }
                sendBroadcast(intent);
                App.getInstance().showToast("账户修改成功");
                ZhangHuManager.this.finish();
            }
            @Override
            public void onError(Object e) {
                dismissProgressDialog();
                App.getInstance().showToast(e.toString());
            }
        });
    }

    /**
     * 获取账户信息
     * @param uid
     */

    private void getZhangHuXinXi(int uid) {
            VolleyHelpApi.getInstance().getZhangHuGuanLiYeWu11(uid, new APIListener() {
                @Override
                public void onResult(Object result) {
                   // {"contactName":"蔡成安","loginStatus":"1","ordContactId":12,"password":"123456","registTime":"1471674887613","status":"1","telephone":"13531829360"}}
                    LogHelper.i(TAG,"---"+result.toString()+"--");
                    JSONObject mJSONJO;
                    try {
                        mJSONJO = ((JSONObject) result).getJSONObject("user");
                        getName= mJSONJO.optString("contactName");
                        getUid=mJSONJO.optInt("ordContactId");
                       getNumber=mJSONJO.optString("password");
                       getPhone=mJSONJO.optString("telephone");
                        LogHelper.i(TAG,"------------------"+getName+"--"+getPhone+"--"+getNumber+"--"+getUid);

                        mEditName.setText(getName);
                        mEditphone.setText(getPhone);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Object e) {
                    dismissProgressDialog();
                    App.getInstance().showToast(e.toString());
                    finish();
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

    }
}
