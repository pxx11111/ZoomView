package com.eqxiu.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eqxiu.ui.SizeAdjustingTextView;
import com.eqxiu.util.DensityUtil;
import com.eqxiu.zoomview.R;

/**
 * @author pxxjesson 文字缩放
 */
public class ZoomTextView extends SizeAdjustingTextView implements View.OnTouchListener {

    private Context mContext;
    private int lastX, lastY, lastLeft, lastRight, lastTop, lastBottom;
    private static final int TOP = 0x01;
    private static final int LEFT = 0x02;
    private static final int BOTTOM = 0x03;
    private static final int RIGHT = 0x04;
    private static final int LEFT_TOP = 0x05;
    private static final int RIGHT_TOP = 0x06;
    private static final int LEFT_BOTTOM = 0x07;
    private static final int RIGHT_BOTTOM = 0x08;
    private static final int CENTER = 0x09;

    /**
     * 屏幕宽高
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * 按下的位置
     */
    private int downLocation;

    /**
     * 缩放的最小宽度
     */
    private int minWidth;

    /**
     * 外层view宽度，用来控制边界
     */
    private int rootViewWidth;
    private int rootViewHeight;

    /**
     * 控制图片的宽/2
     */
    private int controlImageWidth;

    /**
     * 控制图片旋转的view
     */
    private Bitmap controlImage, deleteImage;

    /**
     * 缩放，旋转图标的宽和高
     */
    private int mDrawableWidth, mDrawableHeight;

    private ZoomTextViewInterface zoomTextViewInterface;

    /**
     * 是否显示边框
     */
    private boolean isFocusableDrawRect;
    /**
     * 距离屏幕距离
     */
    private float vpMargin = 0;
    /**
     * 边框颜色
     */
    private int colorBlate, colorAlpha;

    /**
     * 图片编辑菜单
     */
    private MenuPopupWindow popupWindow;
    private Paint paint = new Paint();


    public ZoomTextView(Context context) {
        super(context);

        mContext = context;
        init();
    }

    private void init() {
        this.setWillNotDraw(false);
        this.setClickable(true);
        setOnTouchListener(this);

        // 初始化获取屏幕宽高
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        rootViewWidth = screenWidth - DensityUtil.dip2px(mContext, vpMargin * 2);
        rootViewHeight = screenHeight - DensityUtil.dip2px(mContext, vpMargin * 2);

        controlImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.scale);
        deleteImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.delete_element);

        mDrawableWidth = controlImage.getWidth();
        mDrawableHeight = controlImage.getHeight();

        controlImageWidth = mDrawableWidth / 2;

        colorBlate = getResources().getColor(R.color.blate);
        colorAlpha = getResources().getColor(R.color.alpha);

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        minWidth = getWidth();
        setPadding(controlImageWidth, controlImageWidth, controlImageWidth, controlImageWidth);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("viewon", "onDraw");

        if (isFocusableDrawRect) {
            setBitmapBorder(canvas, colorBlate);
        } else {
            setBitmapBorder(canvas, colorAlpha);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("viewon", "onMeasure");
    }

    /**
     * 设置边框
     *
     * @param canvas
     * @param color
     */
    private void setBitmapBorder(Canvas canvas, int color) {
        paint.setColor(color);

        // 添加边框
        canvas.drawLine(controlImageWidth, controlImageWidth, getWidth() - controlImageWidth, controlImageWidth, paint);// 绘制上边框
        canvas.drawLine(controlImageWidth, getHeight() - controlImageWidth, getWidth() - controlImageWidth,
                getHeight() - controlImageWidth, paint);// 绘制下边框
        canvas.drawLine(controlImageWidth, controlImageWidth, controlImageWidth, getHeight() - controlImageWidth,
                paint);// 绘制左边框
        canvas.drawLine(getWidth() - controlImageWidth, controlImageWidth, getWidth() - controlImageWidth,
                getHeight() - controlImageWidth, paint);// 绘制右边框

        // 四个角添加图片
        canvas.drawBitmap(controlImage, 0, 0, paint);
        canvas.drawBitmap(deleteImage, getWidth() - mDrawableWidth, 0, paint);
        canvas.drawBitmap(controlImage, 0, getHeight() - mDrawableWidth, paint);
        canvas.drawBitmap(controlImage, getWidth() - mDrawableWidth, getHeight() - mDrawableHeight, paint);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 设置边框
                zoomTextViewInterface.setDrawBorder(this);

                lastLeft = v.getLeft();
                lastRight = v.getRight();
                lastTop = v.getTop();
                lastBottom = v.getBottom();
                lastY = (int) event.getRawY();
                lastX = (int) event.getRawX();
                downLocation = getDirection(v, (int) event.getX(), (int) event.getY());

                // 删除view
                if (downLocation == RIGHT_TOP) {
                    zoomTextViewInterface.deleteView(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // 处理拖动事件
                actionMove(v, event);

                break;
            case MotionEvent.ACTION_UP:
                downLocation = 0;
                break;
        }
        invalidate();
        return false;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     */
    protected void actionMove(View v, MotionEvent event) {
        int dx = (int) event.getRawX() - lastX;
        int dy = (int) event.getRawY() - lastY;
        System.out.println("========dowlocation" + downLocation);
        switch (downLocation) {
            case LEFT: // 左边缘
                left(dx);
                break;
            case RIGHT: // 右边缘
                right(dx);
                break;
            case BOTTOM: // 下边缘
                bottom(dy);
                break;
            case TOP: // 上边缘
                top(dy);
                break;
            case CENTER: // 点击中心-->>移动
                center(v, dx, dy);
                break;
            case LEFT_BOTTOM: // 左下
                left(dx);
                bottom(dy);
                break;
            case RIGHT_BOTTOM: // 右下
                right(dx);
                bottom(dy);
                break;
            case RIGHT_TOP: // 右上
                right(dx);
                top(dy);
                break;
            case LEFT_TOP:
                left(dx);
                top(dy);
                break;
        }


        // 触摸边框，实现文字自适应
        if (downLocation != CENTER) {

            v.layout(lastLeft, lastTop, lastRight, lastBottom);

            // 引起问题的代码，如果注释掉此行代码就没有问题，但是就不能实时的自适应宽高
            setContentWidthHeight();
        }


        lastX = (int) event.getRawX();
        lastY = (int) event.getRawY();
    }

    /**
     * 控制文字自适应宽高 如果注释掉此行代码就没有问题，但是就不能实时的自适应
     */
    private void setContentWidthHeight() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
        lp.leftMargin =getLeft();
        lp.topMargin = getTop();
        lp.width = getWidth();
        lp.height = getHeight();
        this.setLayoutParams(lp);
    }

    /**
     * 触摸点为中心->>移动 边界检查
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < -controlImageWidth) {
            left = -controlImageWidth;
            right = left + v.getWidth();
        }
        if (right > rootViewWidth + controlImageWidth) {
            right = rootViewWidth + controlImageWidth;
            left = right - v.getWidth();
        }
        if (top < -controlImageWidth) {
            top = -controlImageWidth;
            bottom = top + v.getHeight();
        }

        if (bottom > rootViewHeight - controlImageWidth) {
            bottom = rootViewHeight - controlImageWidth;
            top = bottom - v.getHeight();
        }

        v.layout(left, top, right, bottom);
        invalidate();
    }

    /**
     * 触摸点为上边缘
     *
     * @param dy
     */
    private void top(int dy) {
        lastTop += dy;
        if (lastTop < -controlImageWidth) {
            lastTop = -controlImageWidth;
        }
        if (lastBottom - lastTop - 2 * controlImageWidth < minWidth) {
            lastTop = lastBottom - 2 * controlImageWidth - minWidth;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param dy
     */
    private void bottom(int dy) {
        lastBottom += dy;
        if (lastBottom > screenHeight + controlImageWidth) {
            lastBottom = screenHeight + controlImageWidth;
        }
        if (lastBottom - lastTop - 2 * controlImageWidth < minWidth) {
            lastBottom = minWidth + lastTop + 2 * controlImageWidth;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param dx
     */
    private void right(int dx) {
        lastRight += dx;
        if (lastRight > screenWidth + controlImageWidth) {
            lastRight = screenWidth + controlImageWidth;
        }
        if (lastRight - lastLeft - 2 * controlImageWidth < minWidth) {
            lastRight = lastLeft + 2 * controlImageWidth + minWidth;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param dx
     */
    private void left(int dx) {
        lastLeft += dx;
        if (lastLeft < -controlImageWidth) {
            lastLeft = -controlImageWidth;
        }
        if (lastRight - lastLeft - 2 * controlImageWidth < minWidth) {
            lastLeft = lastRight - 2 * controlImageWidth - minWidth;
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();

        // 触摸范围
        int threshold = controlImageWidth * 2;
        System.out.println("=======x " + x + " y " + y + " threshold " + threshold + " left " + left + " right " + right + " bottom " + bottom + " top " + top);
        if (x < threshold && y < threshold) {
            return LEFT_TOP;
        }
        if (y < threshold && right - left - x < threshold) {
            return RIGHT_TOP;
        }
        if (x < threshold && bottom - top - y < threshold) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < threshold && bottom - top - y < threshold) {
            return RIGHT_BOTTOM;
        }
        if (x < threshold) {
            return LEFT;
        }
        if (y < threshold) {
            return TOP;
        }
        if (right - left - x < threshold) {
            return RIGHT;
        }
        if (bottom - top - y < threshold) {
            return BOTTOM;
        }
        return CENTER;
    }

    public void setIsFocusableDrawRect(boolean isFocusableDrawRect) {
        this.isFocusableDrawRect = isFocusableDrawRect;
        invalidate();
    }

    public boolean isFocusableDrawRect() {
        return isFocusableDrawRect;
    }

    /**
     * 显示菜单
     */
    private void showPopupWindow() {
        popupWindow = new MenuPopupWindow(mContext);
        int[] location = new int[2];
        this.getLocationOnScreen(location);
        int pwX = location[0] + popupWindow.getmView().getMeasuredWidth() / 2 + getLeft() / 2 + controlImageWidth * 2;
        int pwY = location[1] - popupWindow.getmView().getMeasuredHeight() / 2;
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, pwX, pwY);

    }

    /**
     * 控件加边框,删除 回调接口
     */
    public interface ZoomTextViewInterface {
        public abstract void setDrawBorder(ZoomTextView view);

        public abstract void deleteView(ZoomTextView view);

    }

    public ZoomTextViewInterface getZoomTextViewInterface() {
        return zoomTextViewInterface;
    }

    public void setZoomTextViewInterface(ZoomTextViewInterface zoomTextViewInterface) {
        this.zoomTextViewInterface = zoomTextViewInterface;
    }
}