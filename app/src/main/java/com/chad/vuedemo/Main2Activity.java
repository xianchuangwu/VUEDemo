package com.chad.vuedemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import static com.chad.vuedemo.MainActivity.getScreenWidth;

public class Main2Activity extends AppCompatActivity {

    private LinearLayout containerLeft;
    private ImageView imageViewLeft;
    private LinearLayout containerMiddle;
    private ImageView imageViewMiddle;
    private ImageView imageViewRight;

    private int mScreenWidth;

    private GestureDetector gestureDetector;//手势识别
    private RelativeLayout.LayoutParams mContainerMiddleParams;
    private RelativeLayout.LayoutParams mContainerLeftParams;
    private int[] mFilterArray = {R.mipmap.filter_1, R.mipmap.filter_no, R.mipmap.filter_2};
    private int mMiddleFilterIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        containerLeft = (LinearLayout) findViewById(R.id.container_left);
        imageViewLeft = (ImageView) findViewById(R.id.image_left);
        containerMiddle = (LinearLayout) findViewById(R.id.container_middle);
        imageViewMiddle = (ImageView) findViewById(R.id.image_middle);
        imageViewRight = (ImageView) findViewById(R.id.image_right);

        mScreenWidth = getScreenWidth(this);

        //固定死子view的宽度,必须设置具体的值,如果设置match_parent,子view会跟着父view变动切一直充满父view(父view width变动时有一种翻页的效果)
        mContainerLeftParams = (RelativeLayout.LayoutParams) containerLeft.getLayoutParams();
        LinearLayout.LayoutParams layoutParamsTop = (LinearLayout.LayoutParams) imageViewLeft.getLayoutParams();
        layoutParamsTop.width = mScreenWidth;
        imageViewLeft.setLayoutParams(layoutParamsTop);

        mContainerMiddleParams = (RelativeLayout.LayoutParams) containerMiddle.getLayoutParams();
        LinearLayout.LayoutParams layoutParamsBottom = (LinearLayout.LayoutParams) imageViewMiddle.getLayoutParams();
        layoutParamsBottom.width = mScreenWidth;
        imageViewMiddle.setLayoutParams(layoutParamsBottom);

        LinearLayout.LayoutParams layoutParamsRight = (LinearLayout.LayoutParams) imageViewRight.getLayoutParams();
        layoutParamsRight.width = mScreenWidth;
        imageViewRight.setLayoutParams(layoutParamsRight);

        //设置三个container的起始默认滤镜及状态
        setContainerFilter();

        gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //抬手的时候切换到另一个初始状态,left width重新设定0,middle/right width重新设定match_parent,并且切换三个container的滤镜

                //右滑
                if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE) {
                    //快速滑动或者慢速滑动但滑动距离超过屏幕一半
                    if ((Math.abs(velocityX) > FLING_MIN_VELOCITY || (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() > mScreenWidth / 2))) {
                        switchFilterAnimation(false, true);

                    }
                    //慢速滑动但滑动距离不到屏幕一半
                    else if (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() <= mScreenWidth / 2) {
                        switchFilterAnimation(false, false);
                    }

                }
                //右滑但是滑动距离不到FLING_MIN_DISTANCE
//                else if (e2.getX() - e1.getX() > 0 && e2.getX() - e1.getX() < FLING_MIN_DISTANCE) {
//                }
                //左滑
                else if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE) {
                    //快速滑动或者慢速滑动但滑动距离超过屏幕一半
                    if ((Math.abs(velocityX) > FLING_MIN_VELOCITY || (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() > mScreenWidth / 2))) {
                        switchFilterAnimation(true, true);
                    }
                    //慢速滑动但滑动距离不到屏幕一半
                    else if (Math.abs(velocityX) <= FLING_MIN_VELOCITY && e2.getX() - e1.getX() <= mScreenWidth / 2) {
                        switchFilterAnimation(true, false);
                    }

                }
                //左滑但是滑动距离不到FLING_MIN_DISTANCE
//                else if (e1.getX() - e2.getX() > 0 && e1.getX() - e2.getX() < FLING_MIN_DISTANCE) {
//                }
                return true;
            }
        });
    }

    private final float FLING_MIN_DISTANCE = 10;//滑动的最小位移
    final float FLING_MIN_VELOCITY = 150;//滑动的最小速度
    private float touchPointDownX;
    private float containerLeftStartX;
    private float containerMiddleStartX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchPointDownX = event.getRawX();

                containerLeftStartX = mContainerLeftParams.width;
                if (containerLeftStartX == -1)
                    containerLeftStartX = mScreenWidth;//起始位置时,container是match_parent,mLayoutParams.width = -1
                containerMiddleStartX = mContainerMiddleParams.width;
                if (containerMiddleStartX == -1)
                    containerMiddleStartX = mScreenWidth;//起始位置时,container是match_parent,mLayoutParams.width = -1
                break;
            case MotionEvent.ACTION_MOVE:
                float touchPointMovingX = event.getRawX();
                float distance = touchPointMovingX - touchPointDownX;
                //右滑 containerLeft可见,并且剪裁containerLeft
                if (touchPointMovingX - touchPointDownX > FLING_MIN_DISTANCE) {
                    mContainerLeftParams.width = (int) (containerLeftStartX + distance);
                    containerLeft.setLayoutParams(mContainerLeftParams);
                }
                //左滑 containerLeft不可见,并且剪裁containerMiddle
                else if (touchPointDownX - touchPointMovingX > FLING_MIN_DISTANCE) {
                    mContainerMiddleParams.width = (int) (containerMiddleStartX + distance);
                    containerMiddle.setLayoutParams(mContainerMiddleParams);
                }
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * 每一次切换完滤镜,都要重新设置一下left middle的params
     */
    private void setContainerFilter() {
        imageViewLeft.setImageResource(mFilterArray[(mMiddleFilterIndex - 1) % mFilterArray.length]);
        imageViewMiddle.setImageResource(mFilterArray[mMiddleFilterIndex % mFilterArray.length]);
        imageViewRight.setImageResource(mFilterArray[(mMiddleFilterIndex + 1) % mFilterArray.length]);

        mContainerLeftParams.width = 0;
        containerLeft.setLayoutParams(mContainerLeftParams);

        mContainerMiddleParams.width = -1;
        containerMiddle.setLayoutParams(mContainerMiddleParams);
    }

    private void switchFilterAnimation(final boolean isLeftFling, final boolean isSwitchFilter) {
        ValueAnimator animator;
        if (isLeftFling) {
            if (isSwitchFilter)
                animator = ValueAnimator.ofInt(mContainerMiddleParams.width, 0);
            else animator = ValueAnimator.ofInt(mContainerMiddleParams.width, mScreenWidth);
        } else {
            if (isSwitchFilter)
                animator = ValueAnimator.ofInt(mContainerLeftParams.width, mScreenWidth);
            else animator = ValueAnimator.ofInt(mContainerLeftParams.width, 0);
        }
        containerLeft.setPivotX(0);
        animator.setDuration(300);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (isLeftFling) {
                    mContainerMiddleParams.width = value;
                    containerMiddle.setLayoutParams(mContainerMiddleParams);
                } else {
                    mContainerLeftParams.width = value;
                    containerLeft.setLayoutParams(mContainerLeftParams);
                }
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (isSwitchFilter) {
                    if (isLeftFling) {
                        mMiddleFilterIndex++;
                    } else {
                        mMiddleFilterIndex--;
                        if (mMiddleFilterIndex - 1 < 0) mMiddleFilterIndex = mMiddleFilterIndex + mFilterArray.length;
                    }
                    setContainerFilter();
                }
            }
        });
    }
}
