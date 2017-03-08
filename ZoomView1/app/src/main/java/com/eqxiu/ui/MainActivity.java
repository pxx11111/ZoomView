package com.eqxiu.ui;

import com.eqxiu.view.ZoomTextView;
import com.eqxiu.zoomview.R;
import com.lidroid.xutils.http.client.multipart.HttpMultipartMode;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by pxxjesson on 17/3/7
 */
public class MainActivity extends Activity {
	
	private ZoomTextView mZoomTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RelativeLayout view = (RelativeLayout) findViewById(R.id.rl);

		
		ZoomTextView tv = new ZoomTextView(this);
		tv.setText(Html.fromHtml("<font size=\"4\"><b><font color=\"#23a3d3\">此demo要实现的效果是， 拖动自定义TextView的蓝色边框实现文字的自适应宽高</font></b></font>"));
		RelativeLayout.LayoutParams layoutParamsContent = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		tv.setLayoutParams(layoutParamsContent);
		view.addView(tv);
		tv.setZoomTextViewInterface(new ZoomTextView.ZoomTextViewInterface() {
            @Override
            public void setDrawBorder(ZoomTextView view) {
                if(mZoomTextView ==null){
                    view.setIsFocusableDrawRect(true);
                }else {
                    mZoomTextView.setIsFocusableDrawRect(false);
                    view.setIsFocusableDrawRect(true);
                }
                mZoomTextView = view;
            }

            @Override
            public void deleteView(ZoomTextView view) {
            }

        });
		
		//拖动其中一个TextView改变位置，再点击另一个TextVeiw的任何一个边框，那么问题来了，上一个TextVeiw 回到了原来的位置，问题的根源在 ZoomTextView.java 的252行 调用的函数引起的
		
		
		ZoomTextView tv1 = new ZoomTextView(this);
		tv1.setText(Html.fromHtml("<font color=\"#f4711f\"><font size=\"5\">问题描述：</font><font size=\"1\">拖动其中一个TextView改变位置，再点击另一个TextVeiw的任何一个边框滑动，那么问题来了，上一个TextVeiw 回到了原来的位置，问题的根源在 ZoomTextView.java 的252行 调用的函数引起的。(拖动蓝色边框不灵敏请试试其他边框)</font></font>"));
		RelativeLayout.LayoutParams layoutParamsContent1 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		tv1.setLayoutParams(layoutParamsContent1);
		view.addView(tv1);
		tv1.setZoomTextViewInterface(new ZoomTextView.ZoomTextViewInterface() {
            @Override
            public void setDrawBorder(ZoomTextView view) {
                if(mZoomTextView ==null){
                    view.setIsFocusableDrawRect(true);
                }else {
                    mZoomTextView.setIsFocusableDrawRect(false);
                    view.setIsFocusableDrawRect(true);
                }
                mZoomTextView = view;
            }

            @Override
            public void deleteView(ZoomTextView view) {
            }

        });


	}

}
