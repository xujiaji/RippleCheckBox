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
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * author: xujiaji
 * created on: 2018/9/20 11:05
 * description:
 */
public class RippleCheckBox extends View  {
    private static final String KEY_INSTANCE_STATE = "InstanceState";

    public enum Status {
        /**
         * 正常状态 圈
         */
        CIRCLE(0),
        /**
         * 勾
         */
        HOOK(1),
        /**
         * 叉
         */
        CROSS(2);

        public final int value;
        Status(int value) {
            this.value = value;
        }

        public static Status of(int value) {
            if (value == 1) {
                return HOOK;
            } else if (value == 2) {
                return CROSS;
            } else {
                return CIRCLE;
            }
        }
    }

    // 当前状态
    private Status mLastStatus = Status.CIRCLE;
    private Status mCurrentStatus = Status.CIRCLE;
    private Status mCircleClickedStatus = Status.HOOK;
    private Status mCircleLongClickedStatus = Status.CROSS;
    private Status mHookClickedStatus = Status.CIRCLE;
    private Status mHookLongClickedStatus = Status.CROSS;
    private Status mCrossClickedStatus = Status.CIRCLE;
    private Status mCrossLongClickedStatus = Status.HOOK;
    
    private boolean mEnableClick = true;
    private boolean mEnableLongClick = true;

    private Paint mCenterCirclePaint;
    private Paint mRipplePaint;
    private Paint mRightPaint;
    private Paint mDeletePaint;

    private Path mRightPath;
    private Path mRightPathDst;
    private Path mCenterCirclePath;
    private Path mDeleteTotalPath;
    private Path mDeleteOnePath;
    private Path mDeleteTwoPath;
    private Path mDeletePathDst;

    private PathMeasure mRightPathMeasure = new PathMeasure();
    private float mRightPathMeasureLen; // mRightPathMeasure's length
    private PathMeasure mCenterCirclePathMeasure = new PathMeasure();
    private PathMeasure mDeleteOnePathMeasure = new PathMeasure();
    private PathMeasure mDeleteTwoPathMeasure = new PathMeasure();
    private float mDeletePathMeasureLen; // mDeletePathMeasure's length

    private int mCenterCircleRadius; // 默认中间的圆的半径  The radius of the circle in the middle of the default
    private PointF mCenterPointF = new PointF(); // 中心点  center point
    private PointF mWH = new PointF(); // 宽高  width, height

    private float mRightAnimatorValue;
    private float mRippleAnimatorValue;
    private float mDeleteAnimatorValue;

    private ValueAnimator mRightCheckedAnimator;
    private ValueAnimator mRippleCheckedAnimator;
    private ValueAnimator mDeleteCheckedAnimator;

    private ValueAnimator mRightUnCheckedAnimator;
    private ValueAnimator mRippleUnCheckedAnimator;
    private ValueAnimator mDeleteUnCheckedAnimator;

    private int mDurationRight;
    private int mDurationRipple;
    private int mDurationDelete;

    private OnCheckedChangeListener mListener;

    // 默认未选择状态的中心圆上的三个点相连，绘制√     The three points on the center circle of the unselected state are connected by default, drawing √
    private int _360_right_degree_start  = 150; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的150°开始点        Select √ to use three points to form; in the default unselected state of the center circle 360 ° clockwise rotation of the 150 ° start point
    private int _360_right_degree_center = 100; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的100°为中间的点     Select √ to use three points to form; in the default unselected state, the center circle 360° clockwise rotation of 100° is the middle point
    private int _360_right_degree_end    = 330; // 选中√用三个点可构成；在默认未选择状态的中心圆360°顺时针旋转的330°为最后的点     Select √ to use three points to form; in the default unselected state, the center circle 360° clockwise rotation of 330° is the last point

    private int mRightCorner; // √ corner
    private int mDeleteCorner; // × corner

    private int mRippleMargin;

    private float mDeleteScale; // 删除占比

    private boolean enableDeleteMode; // 是否启动删除模式

    public RippleCheckBox(Context context) {
        this(context, null);
    }

    public RippleCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RippleCheckBox, defStyleAttr, 0);
        final int centerCircleRadius      = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbCenterCircleRadius,      RippleCheckBoxUtil.dp2px(context, 10));
        final int centerCircleStrokeWidth = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbCenterCircleStrokeWidth, RippleCheckBoxUtil.dp2px(context, 1));
        final int centerCircleColor       = t.getColor(               R.styleable.RippleCheckBox_rcbCenterCircleColor,       Color.GRAY);

        final int rightStrokeWidth        = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRightStrokeWidth,        RippleCheckBoxUtil.dp2px(context, 3));
        final int rightColor              = t.getColor(               R.styleable.RippleCheckBox_rcbRightColor,              Color.BLUE);
        final int rightDuration           = t.getInteger(             R.styleable.RippleCheckBox_rcbRightDuration,   400); // default right animal 400ms

        final int deleteStrokeWidth       = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbDeleteStrokeWidth,        RippleCheckBoxUtil.dp2px(context, 2));
        final int deleteColor             = t.getColor(               R.styleable.RippleCheckBox_rcbDeleteColor,              Color.BLUE);
        final int deleteDuration          = t.getInteger(             R.styleable.RippleCheckBox_rcbDeleteDuration,   600); // default delete animal 400ms
        final int deleteRightCorner       = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbDeleteCorner,             RippleCheckBoxUtil.dp2px(context, 2));
        final float deleteScale           = t.getFloat(               R.styleable.RippleCheckBox_rcbDeleteScale,                     1);
        final boolean deleteEnable        = t.getBoolean(             R.styleable.RippleCheckBox_rcbDeleteEnable,                     false);

        final int rippleStrokeWidth       = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRippleStrokeWidth,       RippleCheckBoxUtil.dp2px(context, 4));
        final int rippleColor             = t.getColor(               R.styleable.RippleCheckBox_rcbRippleColor,             Color.BLUE);
        final int rippleDuration          = t.getInteger(             R.styleable.RippleCheckBox_rcbRippleDuration,  200); // default ripple animal 200ms
        final int rippleMargin            = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRippleMargin,    0);

        final int rightStartDegree        = t.getInteger(             R.styleable.RippleCheckBox_rcbRightStartDegree,        _360_right_degree_start);
        final int rightCenterDegree       = t.getInteger(             R.styleable.RippleCheckBox_rcbRightCenterDegree,       _360_right_degree_center);
        final int rightEndDegree          = t.getInteger(             R.styleable.RippleCheckBox_rcbRightEndDegree,          _360_right_degree_end);
        final int rightRightCorner        = t.getDimensionPixelOffset(R.styleable.RippleCheckBox_rcbRightCorner,             RippleCheckBoxUtil.dp2px(context, 2));

        final int status                  = t.getInteger(             R.styleable.RippleCheckBox_rcbStatus,                  Status.CIRCLE.value);
        final int circleClickedStatus     = t.getInteger(             R.styleable.RippleCheckBox_rcbCircleClickedStatus,     Status.HOOK.value);
        final int circleLongClickedStatus = t.getInteger(             R.styleable.RippleCheckBox_rcbCircleLongClickedStatus, Status.CROSS.value);
        final int hookClickedStatus       = t.getInteger(             R.styleable.RippleCheckBox_rcbHookClickedStatus,       Status.CIRCLE.value);
        final int hookLongClickedStatus   = t.getInteger(             R.styleable.RippleCheckBox_rcbHookLongClickedStatus,   Status.CROSS.value);
        final int crossClickedStatus      = t.getInteger(             R.styleable.RippleCheckBox_rcbCrossClickedStatus,      Status.CIRCLE.value);
        final int crossLongClickedStatus  = t.getInteger(             R.styleable.RippleCheckBox_rcbCrossLongClickedStatus,  Status.HOOK.value);

        final boolean enableClick         = t.getBoolean(             R.styleable.RippleCheckBox_rcbEnableClick,             true);
        final boolean enableLongClick     = t.getBoolean(             R.styleable.RippleCheckBox_rcbEnableLongClick,         true);
        t.recycle();

        this.enableDeleteMode         = deleteEnable;
        this.mDeleteScale             = deleteScale;

        this.mCurrentStatus           = Status.of(status);
        this.mLastStatus              = this.mCurrentStatus;
        this.mCircleClickedStatus     = Status.of(circleClickedStatus);
        this.mCircleLongClickedStatus = Status.of(circleLongClickedStatus);
        this.mHookClickedStatus       = Status.of(hookClickedStatus);
        this.mHookLongClickedStatus   = Status.of(hookLongClickedStatus);
        this.mCrossClickedStatus      = Status.of(crossClickedStatus);
        this.mCrossLongClickedStatus  = Status.of(crossLongClickedStatus);

        this.mCenterCircleRadius      = centerCircleRadius;
        this.mDurationRight           = rightDuration;
        this.mDurationRipple          = rippleDuration;
        this.mDurationDelete          = deleteDuration;
        this.mRippleMargin            = rippleMargin;

        this._360_right_degree_start  = rightStartDegree;
        this._360_right_degree_center = rightCenterDegree;
        this._360_right_degree_end    = rightEndDegree;

        this.mEnableClick             = enableClick;
        this.mEnableLongClick         = enableLongClick;

        mRightPath         = new Path();
        mRightPathDst      = new Path();
        mCenterCirclePath  = new Path();
        mDeleteTotalPath   = new Path();
        mDeleteOnePath     = new Path();
        mDeleteTwoPath     = new Path();
        mDeletePathDst     = new Path();

        mCenterCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mRipplePaint       = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mRightPaint        = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        mDeletePaint       = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);

        mCenterCirclePaint.setStrokeWidth(centerCircleStrokeWidth);
        mCenterCirclePaint.setColor(centerCircleColor);
        mCenterCirclePaint.setStyle(Paint.Style.STROKE);

        mRipplePaint.setStrokeWidth(rippleStrokeWidth);
        mRipplePaint.setColor(rippleColor);
        mRipplePaint.setStyle(Paint.Style.STROKE);

        mRightPaint.setPathEffect(new CornerPathEffect(mRightCorner = rightRightCorner));
        mRightPaint.setStrokeWidth(rightStrokeWidth);
        mRightPaint.setColor(rightColor);
        mRightPaint.setStrokeCap(Paint.Cap.ROUND);
        mRightPaint.setStyle(Paint.Style.STROKE);

        mDeletePaint.setPathEffect(new CornerPathEffect(mDeleteCorner = deleteRightCorner));
        mDeletePaint.setStrokeWidth(deleteStrokeWidth);
        mDeletePaint.setColor(deleteColor);
        mDeletePaint.setStrokeCap(Paint.Cap.ROUND);
        mDeletePaint.setStyle(Paint.Style.STROKE);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableClick()) {
                    handleNextStatus(false);
                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isEnableLongClick()) {
                    handleNextStatus(true);
                }
                return true;
            }
        });
    }

    /**
     * 处理下次点击
     * @param isLongClicked 是否是长按
     */
    private void handleNextStatus(boolean isLongClicked) {
        mLastStatus = mCurrentStatus;
        switch (mCurrentStatus) {
            case CIRCLE:
                mCurrentStatus = isLongClicked ? mCircleLongClickedStatus : mCircleClickedStatus;
                break;
            case HOOK:
                mCurrentStatus = isLongClicked ? mHookLongClickedStatus : mHookClickedStatus;
                break;
            case CROSS:
                mCurrentStatus = isLongClicked ? mCrossLongClickedStatus : mCrossClickedStatus;
                break;
        }
        if (mListener != null) {
            mListener.onCheckedChanged(this, mCurrentStatus);
        }
        startAnim();
    }

    /**
     * 更新中心圆的路径
     * update center circle path
     */
    public void updateCenterCircle() {
        mCenterCirclePath.reset();
        RectF circleRectF = new RectF(mCenterPointF.x - mCenterCircleRadius, mCenterPointF.y - mCenterCircleRadius, mCenterPointF.x + mCenterCircleRadius, mCenterPointF.y + mCenterCircleRadius);
        mCenterCirclePath.addOval(
                circleRectF,
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

        final float size = circleRectF.width() * mDeleteScale;
        final float l = circleRectF.left + (circleRectF.width() - size) / 2;
        final float t = circleRectF.top + (circleRectF.height() - size) / 2;
        final float r = l + size;
        final float b = t + size;
        RectF deleteRectF = new RectF(
                l, t, r, b
        );
        mDeleteOnePath.reset();
        mDeleteOnePath.moveTo(deleteRectF.right, deleteRectF.top);
        mDeleteOnePath.lineTo(deleteRectF.left, deleteRectF.bottom);
        mDeleteTwoPath.reset();
        mDeleteTwoPath.moveTo(deleteRectF.left, deleteRectF.top);
        mDeleteTwoPath.lineTo(deleteRectF.right, deleteRectF.bottom);

        mDeleteOnePathMeasure.setPath(mDeleteOnePath, false);
        mDeleteTwoPathMeasure.setPath(mDeleteTwoPath, false);
        mDeletePathMeasureLen = mDeleteOnePathMeasure.getLength() ;

        mDeleteTotalPath.reset();
        mDeleteTotalPath.addPath(mDeleteOnePath);
        mDeleteTotalPath.addPath(mDeleteTwoPath);
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
                || (mRightUnCheckedAnimator != null && mRightUnCheckedAnimator.isRunning())) { // 勾勾动画
            mRightPathDst.reset();
            // 下面两句代码是因为，PathMeasure.getSegment在硬件加速的情况下绘制出来一些问题
            // The following two lines of code are because PathMeasure.getSegment draws some problems in the case of hardware acceleration
            mRightPathDst.moveTo(-2000, -2000);
            mRightPathDst.rLineTo(0, 0);
            mRightPathMeasure.getSegment(0, mRightPathMeasureLen * mRightAnimatorValue, mRightPathDst, true);
            canvas.drawPath(mRightPathDst, mRightPaint);
        } else if (
                (mRippleCheckedAnimator != null && mRippleCheckedAnimator.isRunning())
                || (mRippleUnCheckedAnimator != null && mRippleUnCheckedAnimator.isRunning())) { // 波纹动画
            float value = (1 - mRippleAnimatorValue / (mWH.x / 2));
            mRipplePaint.setAlpha((int) (255 * value));
            canvas.drawCircle(mCenterPointF.x, mCenterPointF.y, mRippleAnimatorValue, mRipplePaint);
            if (mLastStatus == Status.CIRCLE) { // 上一个状态是圈圈时，还是要着圈圈
                canvas.drawPath(mCenterCirclePath, mCenterCirclePaint);
            }
        } else if (
                (mDeleteCheckedAnimator != null && mDeleteCheckedAnimator.isRunning())
                || (mDeleteUnCheckedAnimator != null && mDeleteUnCheckedAnimator.isRunning())
        ) { // 叉叉动画
            mDeletePathDst.reset();
//            mDeletePathDst.moveTo(-2000, -2000);
//            mDeletePathDst.rLineTo(0, 0);

            if (mDeleteAnimatorValue <= 0.5F) {
                mDeleteOnePathMeasure.getSegment(0, mDeletePathMeasureLen * mDeleteAnimatorValue * 2, mDeletePathDst, true);
            } else {
                mDeleteTwoPathMeasure.getSegment(0, mDeletePathMeasureLen * (mDeleteAnimatorValue - 0.5F) * 2, mDeletePathDst, true);
                mDeletePathDst.addPath(mDeleteOnePath);
            }
            canvas.drawPath(mDeletePathDst, mDeletePaint);
        } else if (mCurrentStatus == Status.CROSS) { // 叉叉
            canvas.drawPath(mDeleteTotalPath, mDeletePaint);
        } else if (mCurrentStatus == Status.HOOK) { // 勾勾
            canvas.drawPath(mRightPath, mRightPaint);
        } else {
            canvas.drawPath(mCenterCirclePath, mCenterCirclePaint);
        }
    }

    private void handleDeleteCheckedAnimator() {
        if (mDeleteCheckedAnimator == null) {
            mDeleteCheckedAnimator = ValueAnimator.ofFloat(0f, 1.0F);
            mDeleteCheckedAnimator.setInterpolator(new DecelerateInterpolator());
            mDeleteCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDeleteAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        mDeleteCheckedAnimator.setDuration(mDurationDelete);
    }

    private void handleRightCheckedAnimator() {
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
    }

    private void handleRippleCheckedAnimator() {
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
        }
        mRippleCheckedAnimator.setDuration(mDurationRipple);
    }

    private void startAnim() {
        if (mRippleCheckedAnimator != null && mRippleCheckedAnimator.isRunning()) {
            mRippleCheckedAnimator.cancel();
        }

        if (mRightCheckedAnimator != null && mRightCheckedAnimator.isRunning()) {
            mRightCheckedAnimator.cancel();
        }

        if (mDeleteCheckedAnimator != null && mDeleteCheckedAnimator.isRunning()) {
            mDeleteCheckedAnimator.cancel();
        }

        if (mRippleUnCheckedAnimator != null && mRippleUnCheckedAnimator.isRunning()) {
            mRippleUnCheckedAnimator.cancel();
        }

        if (mRightUnCheckedAnimator != null && mRightUnCheckedAnimator.isRunning()) {
            mRightUnCheckedAnimator.cancel();
        }

        if (mDeleteUnCheckedAnimator != null && mDeleteUnCheckedAnimator.isRunning()) {
            mDeleteUnCheckedAnimator.cancel();
        }

        handleDeleteCheckedAnimator();

        handleRightCheckedAnimator();

        handleRippleCheckedAnimator();

        handleRippleUnCheckedAnimator();

        handleDeleteUnCheckedAnimator();

        handleRightUnCheckedAnimator();

        mRippleCheckedAnimator.removeAllListeners();
        mDeleteUnCheckedAnimator.removeAllListeners();
        mRightUnCheckedAnimator.removeAllListeners();

        if (mLastStatus == Status.CIRCLE) {
            mRippleCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mCurrentStatus == Status.CROSS) {
                        mDeleteCheckedAnimator.start();
                    } else {
                        mRightCheckedAnimator.start();
                    }
                }
            });
            mRippleCheckedAnimator.start();
        } else if (mLastStatus == Status.HOOK) {
            mRightUnCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mCurrentStatus == Status.CIRCLE) {
                        mRippleUnCheckedAnimator.start();
                    } else {
                        mDeleteCheckedAnimator.start();
                    }
                }
            });
            mRightUnCheckedAnimator.start();
        } else if (mLastStatus == Status.CROSS) {
            mDeleteUnCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (mCurrentStatus == Status.CIRCLE) {
                        mRippleUnCheckedAnimator.start();
                    } else {
                        mRightCheckedAnimator.start();
                    }
                }
            });

            mDeleteUnCheckedAnimator.start();
        }
    }


    private void handleRippleUnCheckedAnimator() {
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
    }

    private void handleRightUnCheckedAnimator() {
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
        }
        mRightUnCheckedAnimator.setDuration(mDurationRight);
    }

    private void handleDeleteUnCheckedAnimator() {
        if (mDeleteUnCheckedAnimator == null) {
            mDeleteUnCheckedAnimator = ValueAnimator.ofFloat(1.0f, 0f);
            mDeleteUnCheckedAnimator.setInterpolator(new AccelerateInterpolator());
            mDeleteUnCheckedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mDeleteAnimatorValue = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
        }
        mDeleteUnCheckedAnimator.setDuration(mDurationDelete);
    }

    public void setCurrentStatus(Status status) {
        setCurrentStatus(status, false);
    }

    public void setCurrentStatus(Status status, boolean animal) {
        mLastStatus = mCurrentStatus;
        mCurrentStatus = status;
        if (mListener != null) {
            mListener.onCheckedChanged(this, mCurrentStatus);
        }
        if (animal) {
            startAnim();
        } else {
            invalidate();
        }
    }

    public Status getCurrentStatus() {
        return mCurrentStatus;
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

    public void setCenterCircleColor(int color) {
        mCenterCirclePaint.setColor(color);
    }

    public float getRightStrokeWidth() {
        return mRightPaint.getStrokeWidth();
    }

    public void setRightStrokeWidth(float value) {
        if (value < 0 || value > mCenterCircleRadius) return;
        mRightPaint.setStrokeWidth(value);
    }

    public void setRightColor(int color) {
        mRightPaint.setColor(color);
    }

    public float getDeleteScale() {
        return mDeleteScale;
    }

    public void setDeleteScale(float deleteScale) {
        this.mDeleteScale = deleteScale;
    }

    public int getDeleteCorner() {
        return mDeleteCorner;
    }

    public void setDeleteCorner(int deleteCorner) {
        this.mDeleteCorner = deleteCorner;
    }

    public float getDeleteStrokeWidth() {
        return mDeletePaint.getStrokeWidth();
    }

    public void setDeleteStrokeWidth(float value) {
        if (value < 0 || value > mCenterCircleRadius) return;
        mDeletePaint.setStrokeWidth(value);
    }

    public void setDeleteColor(int color) {
        mDeletePaint.setColor(color);
    }

    public float getRippleStrokeWidth() {
        return mRipplePaint.getStrokeWidth();
    }

    public void setRippleStrokeWidth(float value) {
        if (value < 0 || value > mWH.x / 2) return;
        mRipplePaint.setStrokeWidth(value);
    }

    public void setRippleColor(int color) {
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

    public int getDeleteDuration() {
        return mDurationDelete;
    }

    /**
     * 单位 Unit
     * @param ms 单位 Unit：ms
     */
    public void setDeleteDuration(int ms) {
        if (ms < 0) return;
        mDurationDelete = ms;
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

    /**
     *
     * @param degree range: from 0, to 360
     */
    public void setRightStartDegree(int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_start = degree;
    }

    public int getRightCenterDegree() {
        return _360_right_degree_center;
    }

    /**
     *
     * @param degree range: from 0, to 360
     */
    public void setRightCenterDegree(int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_center = degree;
    }

    public int getRightEndDegree() {
        return _360_right_degree_end;
    }

    /**
     *
     * @param degree range: from 0, to 360
     */
    public void setRightEndDegree(int degree) {
        if (degree < 0 || degree > 360) return;
        _360_right_degree_end = degree;
    }

    public boolean isEnableDeleteMode() {
        return enableDeleteMode;
    }

    public void setEnableDeleteMode(boolean enableDeleteMode) {
        this.enableDeleteMode = enableDeleteMode;
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

    public boolean isEnableClick() {
        return mEnableClick;
    }

    public void setEnableClick(boolean enableClick) {
        this.mEnableClick = enableClick;
    }

    public boolean isEnableLongClick() {
        return mEnableLongClick;
    }

    public void setEnableLongClick(boolean enableLongClick) {
        this.mEnableLongClick = enableLongClick;
    }

    public Status getLastStatus() {
        return mLastStatus;
    }

    public Status getCircleClickedStatus() {
        return mCircleClickedStatus;
    }

    public void setCircleClickedStatus(Status circleClickedStatus) {
        this.mCircleClickedStatus = circleClickedStatus;
    }

    public Status getCircleLongClickedStatus() {
        return mCircleLongClickedStatus;
    }

    public void setCircleLongClickedStatus(Status circleLongClickedStatus) {
        this.mCircleLongClickedStatus = circleLongClickedStatus;
    }

    public Status getHookClickedStatus() {
        return mHookClickedStatus;
    }

    public void setHookClickedStatus(Status hookClickedStatus) {
        this.mHookClickedStatus = hookClickedStatus;
    }

    public Status getHookLongClickedStatus() {
        return mHookLongClickedStatus;
    }

    public void setHookLongClickedStatus(Status hookLongClickedStatus) {
        this.mHookLongClickedStatus = hookLongClickedStatus;
    }

    public Status getCrossClickedStatus() {
        return mCrossClickedStatus;
    }

    public void setCrossClickedStatus(Status crossClickedStatus) {
        this.mCrossClickedStatus = crossClickedStatus;
    }

    public Status getCrossLongClickedStatus() {
        return mCrossLongClickedStatus;
    }

    public void setCrossLongClickedStatus(Status crossLongClickedStatus) {
        this.mCrossLongClickedStatus = crossLongClickedStatus;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(KEY_INSTANCE_STATE, mCurrentStatus.value);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            int status = bundle.getInt(KEY_INSTANCE_STATE, Status.CIRCLE.value);
            setCurrentStatus(Status.of(status), false);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        this.mListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RippleCheckBox checkBox, Status status);
    }
}
