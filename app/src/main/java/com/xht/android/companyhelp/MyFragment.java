package com.xht.android.companyhelp;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.message.tag.TagManager;
import com.xht.android.companyhelp.model.UserInfo;
import com.xht.android.companyhelp.provider.MyDatabaseManager;
import com.xht.android.companyhelp.util.AppInfoUtils;
import com.xht.android.companyhelp.util.LogHelper;

import java.io.File;

public class MyFragment extends Fragment implements OnClickListener {
	private static final String Tag = "MyFragment";
	public static final String BRO_ACT_S = "com.xht.android.companyhelp.bro_act_s";
	public static final String BRO_ACT_S2 = "com.xht.android.companyhelp.bro_act_s2";
	public static final String PHONENUM_KEY = "phone_key";
	public static final String UID_KEY = "userId_key";
	public static final String UNAME_KEY = "userName_key";
	private LinearLayout mLinearLayout1, mLinearLayout2, mLinearLayout3, mLinearLayout4,mLinearLayout5,mLinearLayout6,mLinearLayout7,mLinearLayout8;
	private ImageView mHeadImageView;
	private MainActivity mActivity;
	private UserInfo mUserInfo;
	private TextView mPhoneNumView;
	private TextView aName;
	private TextView mZhangHu;//账户管理
	private TextView mVersionDescTV;
	private static final String TAG = "MyFragment";

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int uId = intent.getIntExtra(UID_KEY, 0);
			long phoneNum = intent.getLongExtra(PHONENUM_KEY, 0);
			String uName = intent.getStringExtra(UNAME_KEY);
			LogHelper.i(Tag, "uName=" + uName);
			mUserInfo.setUid(uId);
			mUserInfo.setPhoneNum(phoneNum);
			if (uName != null) {
				mUserInfo.setUserName(uName);
			}
			refleshUI();
		}
	};

	private BroadcastReceiver mClearUserReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mUserInfo.setUid(0);
			mUserInfo.setPhoneNum(0);
			mUserInfo.setUserName(null);

			refleshUI2();
		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = (MainActivity) activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IntentFilter intentFilter = new IntentFilter(BRO_ACT_S);
		getActivity().registerReceiver(mReceiver, intentFilter);
		IntentFilter intentFilter2 = new IntentFilter(BRO_ACT_S2);
		getActivity().registerReceiver(mClearUserReceiver, intentFilter2);
		mUserInfo = ((MainActivity) mActivity).mUserInfo;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_my, container, false);
		mLinearLayout1 = (LinearLayout) view.findViewById(R.id.fragm_my_ll1);
		mLinearLayout2 = (LinearLayout) view.findViewById(R.id.fragm_my_ll2);
		mLinearLayout4 = (LinearLayout) view.findViewById(R.id.fragm_my_ll4);
		mLinearLayout3 = (LinearLayout) view.findViewById(R.id.fragm_my_ll3);
		mLinearLayout5 = (LinearLayout) view.findViewById(R.id.	fragm_my_ll5);
		//mLinearLayout6 = (LinearLayout) view.findViewById(R.id.	fragm_my_ll6);
		mLinearLayout7 = (LinearLayout) view.findViewById(R.id.	fragm_my_ll7);
		mLinearLayout8= (LinearLayout) view.findViewById(R.id.	fragm_my_ll8);
		mHeadImageView = (ImageView) view.findViewById(R.id.head_img);
		mPhoneNumView = (TextView) view.findViewById(R.id.aPhoneNum);
		aName = (TextView) view.findViewById(R.id.aName);
		mZhangHu= (TextView) view.findViewById(R.id.changhuAdmin);
		mVersionDescTV = (TextView) view.findViewById(R.id.versionDesc);
		mLinearLayout1.setOnClickListener(this);
		mLinearLayout2.setOnClickListener(this);
		mLinearLayout3.setOnClickListener(this);
		mLinearLayout4.setOnClickListener(this);
		mLinearLayout5.setOnClickListener(this);
		//mLinearLayout6.setOnClickListener(this);
		mLinearLayout7.setOnClickListener(this);
		mLinearLayout8.setOnClickListener(this);

		mVersionDescTV.setText("版本:"+ AppInfoUtils.getAppInfoName(getActivity()));
		return view;		
	}
	@Override
	public void onResume() {
		super.onResume();
		if (isUserLogin()) {
			refleshUI();
		}
	}
	
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		getActivity().unregisterReceiver(mClearUserReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fragm_my_ll1:
				if (isUserLogin()) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					DialogFragment newFragment = SwitchUserDialogFragment.newInstance(mUserInfo.getUid(), 0);
					newFragment.show(ft, "sw_u_dialog");
				} else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;
			case R.id.fragm_my_ll2:
				if (isUserLogin()) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					DialogFragment newFragment = CompNDialogFragment.newInstance(mUserInfo.getUid());
					newFragment.show(ft, "cn_dialog");
				} else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;
			case R.id.fragm_my_ll3://我的订单
				if (isUserLogin()) {
					Intent intent2=new Intent(getActivity(), MyOrderActivity.class);
					Bundle bundle2=new Bundle();
					bundle2.putInt("mUid",mUserInfo.getUid());
					intent2.putExtra("mBundle",bundle2);
					startActivity(intent2);
				} else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;
			case R.id.fragm_my_ll4://服务看板
				if (isUserLogin()) {
					Bundle bundle = new Bundle();
					bundle.putInt("uid", mUserInfo.getUid());
					mActivity.switchToActivity(ServerLookBoardActivity.class, bundle, 0, false, false);
				} else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;
			case R.id.fragm_my_ll5://账户管理
				if (isUserLogin()) {
					Intent intent = new Intent(getActivity(), ZhangHuMessage.class);
					Bundle bundle = new Bundle();
					bundle.putInt("mUid", mUserInfo.getUid());
					bundle.putLong("mPhone", mUserInfo.getPhoneNum());
					bundle.putString("mName", mUserInfo.getUserName());
					intent.putExtra("mBundle", bundle);
					startActivity(intent);
					//mActivity.switchToActivity(ZhangHuManager.class,bundle,0,false,false);
				}else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;

			case R.id.fragm_my_ll7:
				if (isUserLogin()) {
					Intent intent2 = new Intent(getActivity(), CompleteMessage.class);
					Bundle bundle2 = new Bundle();
					bundle2.putInt("mUid", mUserInfo.getUid());
					intent2.putExtra("mBundle", bundle2);
					startActivity(intent2);
				}else {
				mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
				return;
			}
				break;

			case R.id.fragm_my_ll8:

				/*File file=new File(Environment.getExternalStorageDirectory(),"houtai.txt");
				openFile(file);
*/

				if (isUserLogin()) {
					Intent intent3 = new Intent(getActivity(),MyTaxSureActivity.class);
					Bundle bundle3 = new Bundle();
					bundle3.putInt("mUid", mUserInfo.getUid());
					intent3.putExtra("mBundle", bundle3);
					startActivity(intent3);
				}else {
					mActivity.switchToActivity(LoginActivity.class, null, 0, false, false);
					return;
				}
				break;
		}
	}

	/**
	 * 根据文件位置名称打开文件
	 * @param file
	 */
	private void openFile(File file){

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		//获取文件file的MIME类型
		String type = getMIMEType(file);
		//设置intent的data和Type属性。
		intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
		//跳转
		startActivity(intent);

	}


	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * @param file
	 */
	private String getMIMEType(File file) {

		String type="*/*";
		String fName = file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
    /* 获取文件的后缀名*/
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for(int i=0;i<MIME_MapTable.length;i++){ //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}



	boolean isUserLogin() {
		if (mUserInfo.getUid() == 0) {
			Cursor cursor = mActivity.getContentResolver().query(MyDatabaseManager.MyDbColumns.CONTENT_URI, null, null, null, null);
			if (cursor == null || cursor.getCount() == 0) {
				
				return false;
			}
			cursor.moveToFirst();
			int uidIndex = cursor.getColumnIndex(MyDatabaseManager.MyDbColumns.UID);
			int userNameIndex = cursor.getColumnIndex(MyDatabaseManager.MyDbColumns.NAME);
			int phoneIndex = cursor.getColumnIndex(MyDatabaseManager.MyDbColumns.PHONE);
			mUserInfo.setUid(cursor.getInt(uidIndex));
			mUserInfo.setUserName(cursor.getString(userNameIndex));
			mUserInfo.setPhoneNum(cursor.getLong(phoneIndex));
		}
		LogHelper.i(Tag, "mUserInfo.getUid() == " + mUserInfo.getUid() + "mUserInfo.getPhoneNum() == " + mUserInfo.getPhoneNum());
		return true;
		
	}
	
	private void refleshUI() {
		mPhoneNumView.setText("" + mUserInfo.getPhoneNum());
		aName.setText(mUserInfo.getUserName());
		//设置用户id为标签
		addTag(mUserInfo.getUid());// 添加用户标签
	}

	// 添加用户推送筛选标签
	private void addTag(int uid) {
		new AddTagTask(uid+"").execute();
		LogHelper.i(TAG, "------------------uid---" + uid);
		//App.getmPushAgent().getTagManager().add(uid + "");
	}
	class AddTagTask extends AsyncTask<Void, Void, String> {
		String tagString;
		String[] tags;

		public AddTagTask(String tag) {
			// TODO Auto-generated constructor stub
			tagString = tag;
			tags = tagString.split(",");
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				TagManager.Result result = App.getmPushAgent().getTagManager().add(tags);
				LogHelper.d(TAG, result.toString());
				return result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// edTag.setText("");
			// updateInfo("Add Tag:\n" + result);
		}
	}
	/**
	 * 清空用户后
	 */
	private void refleshUI2() {
		mPhoneNumView.setText("");
		aName.setText("");
	}



	private final String[][] MIME_MapTable={
			//{后缀名，MIME类型}
			{".3gp",    "video/3gpp"},
			{".apk",    "application/vnd.android.package-archive"},
			{".asf",    "video/x-ms-asf"},
			{".avi",    "video/x-msvideo"},
			{".bin",    "application/octet-stream"},
			{".bmp",    "image/bmp"},
			{".c",  "text/plain"},
			{".class",  "application/octet-stream"},
			{".conf",   "text/plain"},
			{".cpp",    "text/plain"},
			{".doc",    "application/msword"},
			{".docx",   "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{".xls",    "application/vnd.ms-excel"},
			{".xlsx",   "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{".exe",    "application/octet-stream"},
			{".gif",    "image/gif"},
			{".gtar",   "application/x-gtar"},
			{".gz", "application/x-gzip"},
			{".h",  "text/plain"},
			{".htm",    "text/html"},
			{".html",   "text/html"},
			{".jar",    "application/java-archive"},
			{".java",   "text/plain"},
			{".jpeg",   "image/jpeg"},
			{".jpg",    "image/jpeg"},
			{".js", "application/x-javascript"},
			{".log",    "text/plain"},
			{".m3u",    "audio/x-mpegurl"},
			{".m4a",    "audio/mp4a-latm"},
			{".m4b",    "audio/mp4a-latm"},
			{".m4p",    "audio/mp4a-latm"},
			{".m4u",    "video/vnd.mpegurl"},
			{".m4v",    "video/x-m4v"},
			{".mov",    "video/quicktime"},
			{".mp2",    "audio/x-mpeg"},
			{".mp3",    "audio/x-mpeg"},
			{".mp4",    "video/mp4"},
			{".mpc",    "application/vnd.mpohun.certificate"},
			{".mpe",    "video/mpeg"},
			{".mpeg",   "video/mpeg"},
			{".mpg",    "video/mpeg"},
			{".mpg4",   "video/mp4"},
			{".mpga",   "audio/mpeg"},
			{".msg",    "application/vnd.ms-outlook"},
			{".ogg",    "audio/ogg"},
			{".pdf",    "application/pdf"},
			{".png",    "image/png"},
			{".pps",    "application/vnd.ms-powerpoint"},
			{".ppt",    "application/vnd.ms-powerpoint"},
			{".pptx",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{".prop",   "text/plain"},
			{".rc", "text/plain"},
			{".rmvb",   "audio/x-pn-realaudio"},
			{".rtf",    "application/rtf"},
			{".sh", "text/plain"},
			{".tar",    "application/x-tar"},
			{".tgz",    "application/x-compressed"},
			{".txt",    "text/plain"},
			{".wav",    "audio/x-wav"},
			{".wma",    "audio/x-ms-wma"},
			{".wmv",    "audio/x-ms-wmv"},
			{".wps",    "application/vnd.ms-works"},
			{".xml",    "text/plain"},
			{".z",  "application/x-compress"},
			{".zip",    "application/x-zip-compressed"},
			{"",        "*/*"}
	};

}
