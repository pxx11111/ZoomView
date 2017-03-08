package com.eqxiu.view;

import com.eqxiu.zoomview.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by jiangchunbo on 15/11/17.
 */
public class MenuPopupWindow extends PopupWindow {

    public View getmView() {
        return mView;
    }

    public void setmView(View mView) {
        this.mView = mView;
    }

    private View mView;

    public MenuPopupWindow(final Context context) {
        super(context);
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.zoom_menu, null);

        this.setContentView(mView);
        this.setWidth(300);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体的背景
//        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.lock_bg));
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        mView.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

    }

    private void myDismiss() {
        this.dismiss();
    }

}
