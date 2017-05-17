package com.chad.vuedemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private LinearLayout containerLeft;
    private ImageView imageViewLeft;
    private LinearLayout containerMiddle;
    private ImageView imageViewMiddle;

    private int mScreenWidth;
    private RelativeLayout layout;

    private GestureDetector gestureDetector;//手势识别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (RelativeLayout) findViewById(R.id.activity_main);
        containerLeft = (LinearLayout) findViewById(R.id.container_left);
        imageViewLeft = (ImageView) findViewById(R.id.image_left);
        containerMiddle = (LinearLayout) findViewById(R.id.container_middle);
        imageViewMiddle = (ImageView) findViewById(R.id.image_middle);

        mScreenWidth = getScreenWidth(this);
        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                final float FLING_MIN_DISTANCE = 100;//滑动的最小位移
                final float FLING_MIN_VELOCITY = 150;//滑动的最小速度

                //右滑
                if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                    //快速滑动或者慢速滑动但滑动距离超过屏幕一半
                    if ((Math.abs(velocityX) > FLING_MIN_VELOCITY || (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() > mScreenWidth / 2))) {
                        switchFilter(true);
                    }
                    //慢速滑动但滑动距离不到屏幕一半
                    else if (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() <= mScreenWidth / 2) {
                        switchFilter(false);
                    }
                }
                //右滑但是滑动距离不到FLING_MIN_DISTANCE
                else if (e2.getX() - e1.getX() > 0 && e2.getX() - e1.getX() < FLING_MIN_DISTANCE) {
                    switchFilter(false);
                }
                //左滑
                else if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
                    //快速滑动或者慢速滑动但滑动距离超过屏幕一半
                    if ((Math.abs(velocityX) > FLING_MIN_VELOCITY || (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() > mScreenWidth / 2))) {
                        switchFilter(false);
                    }
                    //慢速滑动但滑动距离不到屏幕一半
                    else if (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() <= mScreenWidth / 2) {
                        switchFilter(true);
                    }
                }
                //左滑但是滑动距离不到FLING_MIN_DISTANCE
                else if (e1.getX() - e2.getX() > 0 && e1.getX() - e2.getX() < FLING_MIN_DISTANCE) {
                    switchFilter(true);
                }

                return true;
            }
        });


        LinearLayout.LayoutParams layoutParamsTop = (LinearLayout.LayoutParams) imageViewLeft.getLayoutParams();
        layoutParamsTop.width = mScreenWidth;
        imageViewLeft.setLayoutParams(layoutParamsTop);

        LinearLayout.LayoutParams layoutParamsBottom = (LinearLayout.LayoutParams) imageViewMiddle.getLayoutParams();
        layoutParamsBottom.width = mScreenWidth;
        imageViewMiddle.setLayoutParams(layoutParamsBottom);
    }


    private float downPointWidth;
    private int startWidth;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) containerLeft.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downPointWidth = event.getRawX();
                startWidth = mLayoutParams.width;
                if (startWidth == -1)
                    startWidth = mScreenWidth;//起始位置时,container是match_parent,mLayoutParams.width = -1
                break;
            case MotionEvent.ACTION_MOVE:
                float movingPointWidth = event.getRawX();
                float distance = movingPointWidth - downPointWidth;
                float currentWidth = startWidth + distance;
                mLayoutParams.width = (int) currentWidth;
                containerLeft.setLayoutParams(mLayoutParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 上下两层filter,下层filter宽度是屏幕宽度,上层filter初始状态宽度为0,切换滤镜时让上层filter的宽度等于屏幕宽度,遮盖住下层filter
     *
     * @param isSwitch
     */
    private void switchFilter(boolean isSwitch) {
        final RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) containerLeft.getLayoutParams();
        ValueAnimator animator;
        if (isSwitch)
            animator = ValueAnimator.ofInt(mLayoutParams.width, mScreenWidth);
        else
            animator = ValueAnimator.ofInt(mLayoutParams.width, 0);
        containerLeft.setPivotX(0);
        animator.setDuration(300);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                mLayoutParams.width = value;
                containerLeft.setLayoutParams(mLayoutParams);
            }
        });
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
