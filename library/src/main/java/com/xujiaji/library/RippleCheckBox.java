package com.xujiaji.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Checkable;

/**
 * author: xujiaji
 * created on: 2018/9/20 11:05
 * description:
 */
public class RippleCheckBox extends View implements Checkable {
    private static final String KEY_INSTANCE_STATE = "InstanceState";

    private Paint mCenterCirclePaint;
    private Paint mRipplePaint;
    private Paint mRightPaint;

    private Path mRightPath;
    private Path mRightPathDst;
    private Path mCenterCirclePath;

    private PathMeasure mRightPathMeasure = new PathMeasure();
    private float mRightPathMeasureLen; // mRightPathMeasure's length
    private PathMeasure mCenterCirclePathMeasure = new PathMeasure();

    private int mCenterCircleRadius; // 默认中间的圆的半径  The radius of the circle in the middle of the default
    private PointF mCenterPointF = new PointF(); // 中心点  center point
    private PointF mWH = new PointF(); // 宽高  width, height
    private boolean isChecked;

    private float mRightAnimatorValue;
    private float mRippleAnimatorValue;

    private ValueAnimator mRightCheckedAnimator;
    private ValueAnimator mRippleCheckedAnimator;

    private ValueAnimator mRightUnCheckedAnimator;
    private ValueAnimator mRippleUnCheckedAnimator;

    private int mDurationRight;
    private int mDurationRipple;

    private OnCheckedChangeListener mListener;

    // 默认未选择状态的中心圆上的三个点相连，绘制√     The three points on the center circle of the unselected state are connected by default, drawing √
    private int _360_right_degree_start  = 150; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的150°开始点        Select √ to use three points to form; in the default unselected state of the center circle 360 ° clockwise rotation of the 150 ° start point
    private int _360_right_degree_center = 100; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的100°为中间的点     Select √ to use three points to form; in the default unselected state, the center circle 360° clockwise rotation of 100° is the middle point
    private int _360_right_degree_end    = 330; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的330°为最后的点     Select √ to use three points to form; in the default unselected state, the center circle 360° clockwise rotation of 330° is the last point

    private int mRightCorner; // √ corner

    private int mRippleMargin;

    public RippleCheckBox(Context context) {
        this(context, null);
    }

    public RippleCheckBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleCheckBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RippleCheckBox, defStyleAttr, 0);
        final int centerCircleRadius      = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbCenterCircleRadius,      RippleCheckBoxUtil.dp2px(context, 10));
        final int centerCircleStrokeWidth = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbCenterCircleStrokeWidth, RippleCheckBoxUtil.dp2px(context, 1));
        final int centerCircleColor       = t.getColor(               R.styleable.RippleCheckBox_rcbCenterCircleColor,       Color.GRAY);

        final int rightStrokeWidth        = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRightStrokeWidth,        RippleCheckBoxUtil.dp2px(context, 3));
        final int rightColor              = t.getColor(               R.styleable.RippleCheckBox_rcbRightColor,              Color.BLUE);
        final int rightDuration           = t.getInteger(             R.styleable.RippleCheckBox_rcbRightDuration,   400); // default right animal 400ms

        final int rippleStrokeWidth       = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRippleStrokeWidth,       RippleCheckBoxUtil.dp2px(context, 4));
        final int rippleColor             = t.getColor(               R.styleable.RippleCheckBox_rcbRippleColor,             Color.BLUE);
        final int rippleDuration          = t.getInteger(             R.styleable.RippleCheckBox_rcbRippleDuration,  200); // default ripple animal 200ms
        final int rippleMargin            = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRippleMargin,            0);

        final int rightStartDegree        = t.getInteger(             R.styleable.RippleCheckBox_rcbRightStartDegree,        _360_right_degree_start);
        final int rightCenterDegree       = t.getInteger(             R.styleable.RippleCheckBox_rcbRightCenterDegree,       _360_right_degree_center);
        final int rightEndDegree          = t.getInteger(             R.styleable.RippleCheckBox_rcbRightEndDegree,          _360_right_degree_end);
        final int rightRightCorner        = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRightCorner,             RippleCheckBoxUtil.dp2px(context, 2));
        t.recycle();

        mCenterCircleRadius      = centerCircleRadius;
        mDurationRight           = rightDuration;
        mDurationRipple          = rippleDuration;
        mRippleMargin            = rippleMargin;

        _360_right_degree_start  = rightStartDegree;
        _360_right_degree_center = rightCenterDegree;
        _360_right_degree_end    = rightEndDegree;

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mCenterCirclePaint.setStrokeWidth(centerCircleStrokeWidth);
        mCenterCirclePaint.setColor(centerCircleColor);
        mCenterCirclePaint.setStyle(Paint.Style.STROKE);

        mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mRipplePaint.setStrokeWidth(rippleStrokeWidth);
        mRipplePaint.setColor(rippleColor);
        mRipplePaint.setStyle(Paint.Style.STROKE);

        mRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mRightPaint.setPathEffect(new CornerPathEffect(mRightCorner = rightRightCorner));
        mRightPaint.setStrokeWidth(rightStrokeWidth);
        mRightPaint.setColor(rightColor);
        mRightPaint.setStrokeCap(Paint.Cap.ROUND);
        mRightPaint.setStyle(Paint.Style.STROKE);

        mRightPath = new Path();
        mRightPathDst = new Path();
        mCenterCirclePath = new Path();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked(!isChecked, true);
            }
        });
    }

    /**
     * 更新中心圆的路径
     * update center circle path
     */
    public void updateCenterCircle() {
        mCenterCirclePath.reset();
        mCenterCirclePath.addOval(
                new RectF(mCenterPointF.x - mCenterCircleRadius, mCenterPointF.y - mCenterCircleRadius, mCenterPointF.x + mCenterCircleRadius, mCenterPointF.y + mCenterCircleRadius),
                Path.Direction.CW);
        mCenterCirclePathMeasure.setPath(mCenterCirclePath, true);

        float[]
                 start = new float[2],
                center = new float[2],
                   end = new float[2];

        final float len = mCenterCirclePathMeasure.getLength();

        mCenterCirclePathMeasure.getPosTan(len * _360_right_degree_start  / 360,  start, new float[2]);
        mCenterCirclePathMeasure.getPosTan(len * _360_right_degree_center / 360, center, new float[2]);
        mCenterCirclePathMeasure.getPosTan(len * _360_right_degree_end    / 360,    end, new float[2]);

        mRightPath.reset();
        mRightPath.moveTo(start[0], start[1]);
        mRightPath.lineTo(center[0], center[1]);
        mRightPath.lineTo(end[0], end[1]);
        mRightPathMeasure.setPath(mRightPath, false);
        mRightPathMeasureLen = mRightPathMeasure.getLength();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterPointF.set(w * 1F / 2, h * 1F / 2);
        mWH.set(w, h);

        updateCenterCircle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (
                (mRightCheckedAnimator != null && mRightCheckedAnimator.isRunning())
                || (mRightUnCheckedAnimator != null && mRightUnCheckedAnimator.isRunning())) {
            mRightPathDst.reset();
            // 下面两句代码是因为，PathMeasure.getSegment在硬件加速的情况下绘制出来一些问题
            // The following two lines of code are because PathMeasure.getSegment draws some problems in the case of hardware acceleration
            mRightPathDst.moveTo(-2000, -2000);
            mRightPathDst.rLineTo(0, 0);
            mRightPathMeasure.getSegment(0, mRightPathMeasureLen * mRightAnimatorValue, mRightPathDst, true);
            canvas.drawPath(mRightPathDst, mRightPaint);
        } else if (
                (mRippleCheckedAnimator != null && mRippleCheckedAnimator.isRunning())
                || (mRippleUnCheckedAnimator != null && mRippleUnCheckedAnimator.isRunning())) {
            float value = (1 - mRippleAnimatorValue / (mWH.x / 2));
            mRipplePaint.setAlpha((int) (255 * value));
            canvas.drawCircle(mCenterPointF.x, mCenterPointF.y, mRippleAnimatorValue, mRipplePaint);
            if (isChecked) {
                canvas.drawPath(mCenterCirclePath, mCenterCirclePaint);
            }
        } else if (isChecked) {
            canvas.drawPath(mRightPath, mRightPaint);
        } else {
            canvas.drawPath(mCenterCirclePath, mCenterCirclePaint);
        }
    }

    private void startCheckedAnim() {
        if (mRippleCheckedAnimator != null && mRippleCheckedAnimator.isRunning()) {
            mRippleCheckedAnimator.cancel();
        }

        if (mRightCheckedAnimator != null && mRightCheckedAnimator.isRunning()) {
            mRightCheckedAnimator.cancel();
        }

        if (mRightCheckedAnimator == null) {
            mRightCheckedAnimator = ValueAnimator.ofFloat(0f, 1.0f);
            mRightCheckedAnimator.setInterpolator(new AccelerateInterpolator());
            mRightCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRightAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        mRightCheckedAnimator.setDuration(mDurationRight);

        if (mRippleCheckedAnimator == null) {
            mRippleCheckedAnimator = ValueAnimator.ofFloat(mCenterCircleRadius, mWH.x / 2 - mRippleMargin);
            mRippleCheckedAnimator.setInterpolator(new AccelerateInterpolator());
            mRippleCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRippleAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mRippleCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRightCheckedAnimator.start();
                }
            });
        }
        mRippleCheckedAnimator.setDuration(mDurationRipple);

        mRippleCheckedAnimator.start();
    }

    private void startUnCheckedAnim() {
        if (mRippleUnCheckedAnimator != null && mRippleUnCheckedAnimator.isRunning()) {
            mRippleUnCheckedAnimator.cancel();
        }

        if (mRightUnCheckedAnimator != null && mRightUnCheckedAnimator.isRunning()) {
            mRightUnCheckedAnimator.cancel();
        }

        if (mRippleUnCheckedAnimator == null) {
            mRippleUnCheckedAnimator = ValueAnimator.ofFloat(mWH.x / 2 - mRippleMargin, mCenterCircleRadius);
            mRippleUnCheckedAnimator.setInterpolator(new AccelerateInterpolator());
            mRippleUnCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRippleAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        mRippleUnCheckedAnimator.setDuration(mDurationRipple);

        if (mRightUnCheckedAnimator == null) {
            mRightUnCheckedAnimator = ValueAnimator.ofFloat(1.0f, 0f);
            mRightUnCheckedAnimator.setInterpolator(new AccelerateInterpolator());
            mRightUnCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRightAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mRightUnCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRippleUnCheckedAnimator.start();
                }
            });

        }
        mRightUnCheckedAnimator.setDuration(mDurationRight);

        mRightUnCheckedAnimator.start();
    }

    /**
     * change RippleCheckBox check status
     * @param checked RippleCheckBox的选中状态  RippleCheckBox's check status
     * @param animal 是否开启动画效果  Whether to turn on animation effects
     */
    public void setChecked(boolean checked, boolean animal) {
        if (animal) {
            isChecked = checked;
            if (isChecked) {
                startCheckedAnim();
            } else {
                startUnCheckedAnim();
            }

            if (mListener != null) {
                mListener.onCheckedChanged(this, isChecked);
            }
        } else {
            setChecked(checked);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
        if (mListener != null) {
            mListener.onCheckedChanged(this, isChecked);
        }
        invalidate();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public int getCenterCircleRadius() {
        return mCenterCircleRadius;
    }

    public void setCenterCircleRadius(int radius) {
        if (radius < 0 || radius > mWH.x) return;
        this.mCenterCircleRadius = radius;
    }

    public float getCenterCircleStrokeWidth() {
        return mCenterCirclePaint.getStrokeWidth();
    }

    public void setCenterCircleStrokeWidth(float value) {
        if (value < 0 || value > mCenterCircleRadius) return;
        mCenterCirclePaint.setStrokeWidth(value);
    }

    public void setCenterCircleColor(@ColorInt int color) {
        mCenterCirclePaint.setColor(color);
    }

    public float getRightStrokeWidth() {
        return mRightPaint.getStrokeWidth();
    }

    public void setRightStrokeWidth(float value) {
        if (value < 0 || value > mCenterCircleRadius) return;
        mRightPaint.setStrokeWidth(value);
    }

    public void setRightColor(@ColorInt int color) {
        mRightPaint.setColor(color);
    }

    public float getRippleStrokeWidth() {
        return mRipplePaint.getStrokeWidth();
    }

    public void setRippleStrokeWidth(float value) {
        if (value < 0 || value > mWH.x / 2) return;
        mRipplePaint.setStrokeWidth(value);
    }

    public void setRippleColor(@ColorInt int color) {
        mRipplePaint.setColor(color);
    }

    public int getRippleDuration() {
        return mDurationRipple;
    }

    /**
     * 单位 Unit
     * @param ms 单位 Unit：ms
     */
    public void setRippleDuration(int ms) {
        if (ms < 0) return;
        mDurationRipple = ms;
    }

    public int getRightDuration() {
        return mDurationRight;
    }

    public void setRightDuration(int ms) {
        if (ms < 0) return;
        mDurationRight = ms;
    }

    public int getRightStartDegree() {
        return _360_right_degree_start;
    }

    public void setRightStartDegree(@IntRange(from = 0, to = 360) int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_start = degree;
    }

    public int getRightCenterDegree() {
        return _360_right_degree_center;
    }

    public void setRightCenterDegree(@IntRange(from = 0, to = 360) int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_center = degree;
    }

    public int getRightEndDegree() {
        return _360_right_degree_end;
    }

    public void setRightEndDegree(@IntRange(from = 0, to = 360) int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_end = degree;
    }

    public int getRightCorner() {
        return mRightCorner;
    }

    public void setRightCorner(int corner) {
        mRightPaint.setPathEffect(new CornerPathEffect(corner));
    }

    public int getRippleMargin() {
        return mRippleMargin;
    }

    public void setRippleMargin(int margin) {
        if (margin < 0 || margin > mWH.x / 2 - mCenterCircleRadius) return;

        mRippleMargin = margin;

        mRippleUnCheckedAnimator = null;
        mRippleCheckedAnimator = null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBoolean(KEY_INSTANCE_STATE, isChecked());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            boolean isChecked = bundle.getBoolean(KEY_INSTANCE_STATE);
            setChecked(isChecked);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RippleCheckBox checkBox, boolean isChecked);
    }
}
