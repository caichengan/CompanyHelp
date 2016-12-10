package com.xht.android.companyhelp.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.xht.android.companyhelp.App;
import com.xht.android.companyhelp.R;

/**
 * Created by Administrator on 2016/11/15.
 */

public class BitmapUtils {

    private static final String TAG = "BitmapUtils";
    public static void loadImgageUrl(String imgTag, ImageRequest request, String imgFile, final ImageView mImgFile) {


        if (imgTag == imgFile) {
            request = new ImageRequest(imgFile, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap arg0) {
                    LogHelper.i(TAG,"-------tag--");
                    mImgFile.setImageBitmap(arg0);

                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    mImgFile.setImageResource(R.mipmap.ic_launcher);
                }
            });
            //存储到队列中
            App.getInstance().getRequestQueue().add(request);
        } else {
            mImgFile.setImageResource(R.mipmap.ic_launcher);
        }

    }
    public static void loadImgageUrl( ImageRequest request, String imgFile, final ImageView mImgFile) {


            request = new ImageRequest(imgFile, new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap arg0) {
                  /*  ScaleAnimation scaleAnimation=new ScaleAnimation(0.3f, 1.0f ,0.3f, 1.0f);
                    scaleAnimation.setRepeatMode(TRIM_MEMORY_COMPLETE);
                    scaleAnimation.setDuration(500);
                    mImgFile.startAnimation(scaleAnimation);*/
                    mImgFile.setImageBitmap(arg0);

                }
            }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                   mImgFile.setImageResource(R.mipmap.ic_launcher);
                }
            });
            //存储到队列中
            App.getInstance().getRequestQueue().add(request);
        }



}
