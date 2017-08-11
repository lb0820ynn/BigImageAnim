package com.bin.bigimageanim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by liubin on 2017/8/9.
 */

public class TranBitImageView extends ImageView implements Handler.Callback{
    public static final String TAG = "TranBitImageView";
    private Matrix mMatrix;
    private ViewGroup mParent;
    private float mImageWidth;
    private float mImageHeight;
    private int mViewWidth;
    private int mViewHeight;

    /**
     * 判断是否调用过onDraw方法
     */
    private boolean isOnDraw = false;
    private AnimatorSet animatorSet;


    public TranBitImageView(Context context) {
        super(context);
        init();
    }

    public TranBitImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TranBitImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        mMatrix = new Matrix();
        mParent = getParent() instanceof ViewGroup ? (ViewGroup) getParent() : null;
        super.setScaleType(ScaleType.MATRIX);

    }

    private void initImage() {
        float[] values = new float[]{1, 0, 0, 0, 1, 0, 0, 0, 1};
        mMatrix.setValues(values);
        getWidthAndHeight(mMatrix);

        if (mImageHeight == 0 || mImageWidth == 0 || mViewHeight == 0 || mViewWidth == 0) {
            return;
        }

        mMatrix.postTranslate(mViewWidth - mImageWidth, mViewHeight - mImageHeight);
        setImageMatrix(mMatrix);

        startAnim();

    }

    /**
     * 获取图片的宽高
     */
    private void getWidthAndHeight(Matrix matrix) {
        BitmapDrawable bd = (BitmapDrawable) getDrawable();
        if (bd == null) {
            return;
        }
        Bitmap bm = bd.getBitmap();
        if (bm == null) {
            return;
        }
        float[] values = new float[9];
        matrix.getValues(values);
        // 获取图片的宽和高，
        mImageWidth = bm.getWidth() * values[Matrix.MSCALE_X];
        mImageHeight = bm.getHeight() * values[Matrix.MSCALE_X];

        Log.i(TAG, "imageWidth == " + mImageWidth + " imageHeight== " + mImageHeight);
        // 获取view的宽高
        mViewWidth = getWidth();
        mViewHeight = getHeight();

        // 获得拖动参数
//        if (mImageWidth > mViewWidth) {
//            ableTranX = true;
//        } else {
//            ableTranX = false;
//        }
//        if (mImageHeight > mViewHeight) {
//            ableTranY = true;
//        } else {
//            ableTranY = false;
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isOnDraw) {
            init();
            initImage();
            isOnDraw = true;
        }
        super.onDraw(canvas);
    }


    public void startAnim() {

        PointF start = new PointF(0, 0);
        PointF end = new PointF(mImageWidth - mViewWidth, mImageHeight - mViewHeight);
        ValueAnimator animator1 = ValueAnimator.ofObject(new MyPointFEvaluator(), start, end);
        animator1.setDuration(16 * 1000);
        animator1.addUpdateListener(new MyAnimUpdateListener(this));

        PointF startX = new PointF(0, 0);
        PointF endX = new PointF(mViewWidth - mImageWidth, 0);
        ValueAnimator animatorX = ValueAnimator.ofObject(new MyPointFEvaluator(), startX, endX);
        animatorX.addUpdateListener(new MyAnimUpdateListener(this));
        animatorX.setDuration(12 * 1000);

        PointF startY = new PointF(0, 0);
        PointF endY = new PointF(0, mViewHeight - mImageHeight);
        ValueAnimator animatorY = ValueAnimator.ofObject(new MyPointFEvaluator(), startY, endY);
        animatorY.addUpdateListener(new MyAnimUpdateListener(this));
        animatorY.setDuration(10 * 1000);

        animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animator1, animatorX, animatorY);

        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mHandler.sendEmptyMessageDelayed(1, 200);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    Handler mHandler = new Handler(this);

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pauseAnim() {
        if (animatorSet != null) {
            animatorSet.pause();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resumeAnim() {
        if (animatorSet != null && animatorSet.isPaused()) {
            animatorSet.resume();
        }
    }

    public void stopAnim() {
        mHandler.removeCallbacksAndMessages(null);
        if (animatorSet != null) {
            animatorSet.cancel();
            animatorSet = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case 1:
                if (animatorSet != null) {
                    animatorSet.start();
                }
                break;
        }
        return false;
    }

    static class MyAnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        WeakReference<TranBitImageView> mReference;

        public MyAnimUpdateListener(TranBitImageView view) {
            mReference = new WeakReference<>(view);
        }

        PointF lastAnimValue;

        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (mReference != null) {
                TranBitImageView imageView = mReference.get();
                if (imageView == null) {
                    return;
                }

                PointF animatedValue = (PointF) valueAnimator.getAnimatedValue();

                if (lastAnimValue == null) {
                    lastAnimValue = animatedValue;
                }

                float x = animatedValue.x - lastAnimValue.x;
                float y = animatedValue.y - lastAnimValue.y;
                imageView.mMatrix.postTranslate(x, y);
                imageView.setImageMatrix(imageView.mMatrix);
                lastAnimValue = animatedValue;
                if (valueAnimator.getAnimatedFraction() == 1.0f) {
                    lastAnimValue = null;
                }
            }
        }
    }

    class MyPointFEvaluator implements TypeEvaluator<PointF> {


        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            PointF startPoint = startValue;
            PointF endPoint = endValue;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            PointF point = new PointF(x, y);
            return point;
        }
    }


}
