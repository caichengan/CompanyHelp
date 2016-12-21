package com.xht.android.companyhelp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.xht.android.companyhelp.net.APIListener;
import com.xht.android.companyhelp.net.VolleyHelpApi;
import com.xht.android.companyhelp.provider.MyDatabaseManager;
import com.xht.android.companyhelp.util.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务里面的注册公司界面
 */
public class ZhuCeCompanyActivity extends Activity implements OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

	private static final String TAG = "ZhuCeCompanyActivity";
	private int mUId;
	private long mPhoneNum;
	private String mUserName;
	private ProgressDialog mProgDoal;
	private LinearLayout mLLJiZEx;    //代理记账展开按钮-显示记账周期
	private LinearLayout mLLNaSR;        //代理记账展开按钮-显示纳税人类别
	private EditText mET;    //公司名称
	private TextView mHeJiTV;
	private TextView mYouhuiTV;
	private EditText mPhone;
	private EditText mNameET;
	private RadioGroup mPeriod;
	private RadioGroup mNSR;	//纳税人类型
	private Switch switch2;		//代理记账的开关
	private EditText mETBei1;	//备选公司名称1
	private EditText mETBei2;	//备选公司名称2
	private EditText mETBei3;	//备选公司名称3


	private int mMoney = 0;    //合计
	private int mMoneyTotal = 0;    //总合计

	private int mAddTGMoney = 0;    //地址托管
	private int mMoneyYouHui = 0;    //若选择代理记账服务，可优惠多少钱
	private int mMoneyJiaJi=0;//加急的价钱
	private int mArea = 0;    //spinner注册区域所选的id
	private boolean mShiFouJiaJi;	//是否加急
	private boolean mAddTuoGuan;	//地址托管
	private boolean mDLJZFlag;	//地址托管
	private int dljz = 0;	//代理记账-0,1,2,3,4,5,6
	private int nsrFlag = 0;	//纳税人默认为小规模纳税人
	private int zhouqiFlag = 3;	//默认为一年
	private int[] dljz6 = new int[7];
	private String[] dljz6JsonKey = new String[] {
	"Q1", "HY1", "Y1", "Q", "HY", "Y"};//小规模纳税人季度、小规模纳税人半年、小规模纳税人一年、一般纳税人季度价格、一般纳税人半年、一般纳税人一年
	private int[] shishibuzou = new int[7];	//起始步骤价格
	private int mQiShiBuZouF;
	private boolean mShiFouFlag;	//是否加载价格完成
	private int mMoneyNornalTaxJiDu=0;
	private int mMoneyNornalTaxHalf=0;
	private int mMoneyNornalTaxYear=0;
	private int mMoneySmallScaleJiDu=0;
	private int mMoneySmallScaleHalf=0;
	private int mMoneySmallScaleYear=0;
	private int money;
	private Switch yorNJiaJi;
	private Switch switch1;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhucecompany);
		Bundle bundle = getIntent().getBundleExtra("uData");
		mUId = bundle.getInt("uid", 0);
		mPhoneNum = bundle.getLong("uphone");
		mUserName = bundle.getString("uname");
		TextView mCustomView = new TextView(this);
		mCustomView.setGravity(Gravity.CENTER);
		mCustomView.setText("下单预约-公司注册");
		mCustomView.setTextSize(18);
		final ActionBar aBar = getActionBar();
		aBar.setCustomView(mCustomView,
				new ActionBar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		int change = ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_CUSTOM;
		aBar.setDisplayOptions(change);
		final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);    //注册区域的spinner
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.areas, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(arrayAdapter);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				LogHelper.i("spinner1", spinner1.getSelectedItem().toString());
				mArea = position + 1;
				jiaZaiJaiGe();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		mET = (EditText) findViewById(R.id.cName_et);
		//是否加急
		yorNJiaJi = (Switch) findViewById(R.id.tSFJJBtn);
		yorNJiaJi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!mShiFouFlag){
					return;
				}
				mShiFouJiaJi = isChecked;
				if (isChecked) {
					mMoneyTotal = mMoneyTotal + mMoneyJiaJi;
				} else {
					mMoneyTotal = mMoneyTotal - mMoneyJiaJi;
				}
				reFleshMoneyHeji();
			}
		});
		//地址托管
		switch1 = (Switch) findViewById(R.id.toggleButton1);
		switch1.setOnCheckedChangeListener(this);

		//TODO
		//代理记账
		switch2 = (Switch) findViewById(R.id.toggleButton2);
		switch2.setOnCheckedChangeListener(this);
		mLLJiZEx = (LinearLayout) findViewById(R.id.jiZLL);
		mLLNaSR = (LinearLayout) findViewById(R.id.leiNSRLL);
		mPhone = (EditText) findViewById(R.id.book_phone);
		mNameET = (EditText) findViewById(R.id.book_name);
		mPeriod = (RadioGroup) findViewById(R.id.period_jz);//季度
		mPeriod.setOnCheckedChangeListener(this);
		mNSR = (RadioGroup) findViewById(R.id.naShuiP);//一般纳税人
		mNSR.setOnCheckedChangeListener(this);
		//请选择起始步骤
		Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
		ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(this, R.array.qishi_buzou, android.R.layout.simple_spinner_item);
		arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(arrayAdapter2);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
									   int position, long id) {
				if (!mShiFouFlag) return;
				int preM = shishibuzou[mQiShiBuZouF + 1];
				mQiShiBuZouF = position;
				mMoneyTotal = mMoneyTotal - preM + shishibuzou[mQiShiBuZouF + 1];
				reFleshMoneyHeji();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		mETBei1 = (EditText) findViewById(R.id.anthor_c1);
		mETBei2 = (EditText) findViewById(R.id.anthor_c2);
		mETBei3 = (EditText) findViewById(R.id.anthor_c3);
		mYouhuiTV = (TextView) findViewById(R.id.tip_word_youhui_book);
		//mYouhuiTV.setText(String.format(getResources().getString(R.string.bookjichang_youhui_s), mMoneyYouHui));
//		((TextView) findViewById(R.id.shu_add)).setText(String.format(getResources().getString(R.string.address_tuoguan_ss), mAddTGMoney));
		mHeJiTV = ((TextView) findViewById(R.id.shu_heji));
		//下单预约
		Button yuYueBook = (Button) findViewById(R.id.bookYuQue);
		yuYueBook.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mET.getText() == null || mET.getText().toString().equals("")) {
					App.getInstance().showToast(R.string.please_gongsiname);
					return;
				}
				if (mPhone.getText() == null || mPhone.getText().toString().equals("")) {
					App.getInstance().showToast(R.string.please_phone);
					return;
				}
				if (mNameET.getText() == null || mNameET.getText().toString().equals("")) {
					App.getInstance().showToast(R.string.please_name);
					return;
				}
				fengzhuangJsonofPost();
			}
		});
		reFleshMoneyHeji();
	}

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
	 * 隐藏mProgressDialog
	 */
	private void dismissProgressDialog()
	{
		if(mProgDoal != null)
		{
			mProgDoal.dismiss();
			mProgDoal = null;
		}
	}

	private void jiaZaiJaiGe() {
		createProgressDialog("价格初始化中...");

		yorNJiaJi.setChecked(false);
		switch1.setChecked(false);
		switch2.setChecked(false);

		mShiFouJiaJi=false;
		mAddTuoGuan=false;
		VolleyHelpApi.getInstance().getJifGeofYeFu(0, mArea, new APIListener() {
			@Override
			public void onResult(Object result) {
				JSONObject jO = ((JSONObject) result).optJSONObject("entity");
				LogHelper.i(TAG,"-----" +jO.toString());
				for (int i = 0; i < 6; i++) {
					dljz6[i + 1] = jO.optInt(dljz6JsonKey[i]);

					LogHelper.i(TAG,"----dljz6[i + 1]----"+dljz6[i + 1]);
				}
				//{"Y1":1,"free1":1,"yewu1":1,"st02":1,"HY":1,"st01":1,"st04":1,"Q":1,"Q1":1,"st03":1,"st06":1,"HY1":1,"st05":1,"fuwu1":48800,"Y":1}

				mMoney=mMoneyTotal = jO.optInt("fuwu1");//公司注册先加载的价钱
				mAddTGMoney = jO.optInt("yewu1");//地址托管的钱
				mMoneyYouHui = jO.optInt("free1");//优惠价格
				mMoneyJiaJi = jO.optInt("JiaJi");//加急价格

				//一般纳税人季度价格
				mMoneyNornalTaxJiDu = jO.optInt("Q");
				//一般纳税人半年
				mMoneyNornalTaxHalf = jO.optInt("HY");
				//一般纳税人一年
				mMoneyNornalTaxYear = jO.optInt("Y");

				//小规模纳税人季度
				mMoneySmallScaleJiDu = jO.optInt("Q1");
				//小规模纳税人半年
				mMoneySmallScaleHalf = jO.optInt("HY1");
				//小规模纳税人一年
				mMoneySmallScaleYear = jO.optInt("Y1");

				shishibuzou[1] = jO.optInt("st01");
				shishibuzou[2] = jO.optInt("st02");
				shishibuzou[3] = jO.optInt("st03");
				shishibuzou[4] = jO.optInt("st04");
				shishibuzou[5] = jO.optInt("st05");
				shishibuzou[6] = jO.optInt("st06");
				mMoney += shishibuzou[mQiShiBuZouF + 1];
				if (mShiFouJiaJi) mMoneyTotal += mMoneyJiaJi;
				if (mAddTuoGuan) mMoneyTotal += mAddTGMoney;


				/*if (switch2.isChecked()) {

					LogHelper.i(TAG,"----"+dljz6[dljz]);
					mMoney += dljz6[dljz];
					mMoney -= mMoneyYouHui;
				}else{
					LogHelper.i(TAG,"----"+dljz6[dljz]);
					mMoney += dljz6[dljz];
					mMoney -= mMoneyYouHui;
				}*/
				mShiFouFlag = true;
				dismissProgressDialog();
				reFleshMoneyHeji();
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
	 * 获取用户所填的资料，封装成json
	 */
	private void fengzhuangJsonofPost() {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("area", mArea);
			jsonObj.put("CompName", mET.getText().toString());
			jsonObj.put("jiaji", mShiFouJiaJi);
			jsonObj.put("dizhituoguan", mAddTuoGuan);
			jsonObj.put("pNumber", mPhone.getText().toString());
			jsonObj.put("pName", mNameET.getText().toString());
			jsonObj.put("qishibuzhou", mQiShiBuZouF + 1);
			jsonObj.put("Comp_Bei1", mETBei1.getText().toString());
			jsonObj.put("Comp_Bei2", mETBei2.getText().toString());
			jsonObj.put("Comp_Bei3", mETBei3.getText().toString());
			jsonObj.put("Money_HeJi", mMoneyTotal);
			jsonObj.put("dailijizhang", dljz);
			jsonObj.put("dailijizhang_money", dljz6[dljz]);
			jsonObj.put("userId", mUId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		createProgressDialog("订单提交中...");
		VolleyHelpApi.getInstance().postDingDanZhuCeCompany(mUId, jsonObj, new APIListener() {
			@Override
			public void onResult(Object result) {
				LogHelper.i("订单提交成功", "2016-08-03");
				//用户姓名写入数据库
				if (mUserName == null || mUserName.isEmpty()) {
					//用户姓名写入数据库
					ContentValues cv = new ContentValues();
					cv.put(MyDatabaseManager.MyDbColumns.NAME, mNameET.getText().toString());
					String where = MyDatabaseManager.MyDbColumns.UID + " = ?";
					String[] selectionArgs = {String.valueOf(mUId)};
					ZhuCeCompanyActivity.this.getContentResolver().update(MyDatabaseManager.MyDbColumns.CONTENT_URI, cv, where, selectionArgs);
					//数据有更新，更新一下内存的用户变量
					Intent intent = new Intent(MyFragment.BRO_ACT_S);
					intent.putExtra(MyFragment.UID_KEY, mUId);
					intent.putExtra(MyFragment.PHONENUM_KEY, Long.parseLong("" + mPhoneNum));
					intent.putExtra(MyFragment.UNAME_KEY, mNameET.getText().toString());
					sendBroadcast(intent);
				}
				JSONObject tempJO = ((JSONObject) result).optJSONObject("entity");
				dismissProgressDialog();
				Bundle bundle = new Bundle();
				bundle.putString("shangpin", "注册公司");
				bundle.putString("bookListId", tempJO.optString("registOrderId"));
				bundle.putFloat("pay_money", mMoney / 100.0f);
				Intent intent = new Intent(ZhuCeCompanyActivity.this, PayOptActivity.class);
				intent.putExtra("booklistdata", bundle);
				ZhuCeCompanyActivity.this.startActivity(intent);
				ZhuCeCompanyActivity.this.finish();
			}

			@Override
			public void onError(Object e) {
				dismissProgressDialog();
				App.getInstance().showToast(e.toString());
			}
		});
	}

	/**
	 * 更新合计的View
	 */
	private void reFleshMoneyHeji() {
		LogHelper.i("更新合计的View", "mMoney = " + mMoneyTotal);
		mHeJiTV.setText(String.format(getResources().getString(R.string.heji_yuan), mMoneyTotal / 100.0f));
		mYouhuiTV.setText(String.format(getResources().getString(R.string.bookjichang_youhui_s), mMoneyYouHui));
		mPhone.setText("" + mPhoneNum);
		if (mUserName != null) {
			mNameET.setText(mUserName);
			mNameET.setEnabled(false);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.toggleButton2:



				if (isChecked) {
					dljz = nsrFlag + zhouqiFlag;
					money = dljz6[dljz];
					mLLJiZEx.setVisibility(View.VISIBLE);
					mLLNaSR.setVisibility(View.VISIBLE);
					mMoneyTotal = mMoneyTotal + money - mMoneyYouHui;
					LogHelper.i(TAG,"---mMoneyTotal--"+mMoneyTotal);
					LogHelper.i(TAG,"---money--"+money);
					LogHelper.i(TAG,"---dljz--"+dljz);

				} else {
					//dljz = 0;
					mMoneyTotal = mMoneyTotal - money+mMoneyYouHui;
					LogHelper.i(TAG,"-----else---mMoneyTotal--"+mMoneyTotal);
					LogHelper.i(TAG,"----else--money--"+money);
					LogHelper.i(TAG,"----else--dljz6[dljz]--"+dljz);

					mLLJiZEx.setVisibility(View.GONE);
					mLLNaSR.setVisibility(View.GONE);

				}

				reFleshMoneyHeji();
				break;
			case R.id.toggleButton1:
				if (!mShiFouFlag) return;
				mAddTuoGuan = isChecked;
				if (isChecked) {
					mMoneyTotal += mAddTGMoney;
				} else {
					mMoneyTotal -= mAddTGMoney;
				}
				reFleshMoneyHeji();
				break;
			default:

				break;
		}
	}


	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mMoneyTotal = mMoneyTotal - money;
		switch (checkedId) {
			case R.id.radio1:
				zhouqiFlag = 1;//季度
				break;
			case R.id.radio2:
				zhouqiFlag = 2;//半年
				break;
			case R.id.radio3:
				zhouqiFlag = 3;//一年
				break;
			case R.id.radioR1:
				nsrFlag = 3;//一般纳税人
				break;
			case R.id.radioR2:
				nsrFlag = 0;//小规模纳税人
				break;
			default:
				break;
		}

		LogHelper.i(TAG,"---nsrFlag"+nsrFlag);
		LogHelper.i(TAG,"---zhouqiFlag"+zhouqiFlag);
		LogHelper.i(TAG,"---money"+money);

		dljz = nsrFlag + zhouqiFlag;
		money=dljz6[dljz];
		mMoneyTotal = mMoneyTotal + money;

		LogHelper.i(TAG,"---mMoneyTotal"+mMoneyTotal);
		reFleshMoneyHeji();
	}
}
