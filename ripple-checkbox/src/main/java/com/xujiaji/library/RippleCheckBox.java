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
    private boolean isChecked;
    private boolean isDelete;

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
    private OnDeleteChangeListener mDeleteListener;

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
        final boolean isChecked           = t.getBoolean(             R.styleable.RippleCheckBox_rcbChecked,         false);
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
        t.recycle();

        this.enableDeleteMode         = deleteEnable;
        this.mDeleteScale             = deleteScale;
        this.isChecked                = isChecked;
        this.mCenterCircleRadius      = centerCircleRadius;
        this.mDurationRight           = rightDuration;
        this.mDurationRipple          = rippleDuration;
        this.mDurationDelete          = deleteDuration;
        this.mRippleMargin            = rippleMargin;

        this._360_right_degree_start  = rightStartDegree;
        this._360_right_degree_center = rightCenterDegree;
        this._360_right_degree_end    = rightEndDegree;

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
                if (RippleCheckBox.this.isDelete) {
                    setDelete(false, true);
                } else {
                    setChecked(!RippleCheckBox.this.isChecked, true);
                }
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (RippleCheckBox.this.isChecked) {
                    setChecked(false, true);
                } else {
                    setDelete(!RippleCheckBox.this.isDelete, true);
                }
                return true;
            }
        });
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
        } else if (
                (mDeleteCheckedAnimator != null && mDeleteCheckedAnimator.isRunning())
                || (mDeleteUnCheckedAnimator != null && mDeleteUnCheckedAnimator.isRunning())
        ) {
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
        } else if (isDelete) {
            canvas.drawPath(mDeleteTotalPath, mDeletePaint);
        } else if (isChecked) {
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

    private void handleRippleCheckedAnimator(final boolean isDelete) {
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
        mRippleCheckedAnimator.removeAllListeners();
        mRippleCheckedAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (isDelete) {
                    mDeleteCheckedAnimator.start();
                } else {
                    mRightCheckedAnimator.start();
                }
            }
        });
        mRippleCheckedAnimator.setDuration(mDurationRipple);
    }

    private void startAnim(boolean isDelete) {
        if (mRippleCheckedAnimator != null && mRippleCheckedAnimator.isRunning()) {
            mRippleCheckedAnimator.cancel();
        }

        if (mRightCheckedAnimator != null && mRightCheckedAnimator.isRunning()) {
            mRightCheckedAnimator.cancel();
        }

        if (mDeleteCheckedAnimator != null && mDeleteCheckedAnimator.isRunning()) {
            mDeleteCheckedAnimator.cancel();
        }

        handleDeleteCheckedAnimator();

        handleRightCheckedAnimator();

        handleRippleCheckedAnimator(isDelete);

        mRippleCheckedAnimator.start();
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
            mRightUnCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRippleUnCheckedAnimator.start();
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
            mDeleteUnCheckedAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mRippleUnCheckedAnimator.start();
                }
            });
        }
        mDeleteUnCheckedAnimator.setDuration(mDurationDelete);
    }

    private void startUnAnim(boolean isDelete) {
        if (mRippleUnCheckedAnimator != null && mRippleUnCheckedAnimator.isRunning()) {
            mRippleUnCheckedAnimator.cancel();
        }

        if (mRightUnCheckedAnimator != null && mRightUnCheckedAnimator.isRunning()) {
            mRightUnCheckedAnimator.cancel();
        }

        if (mDeleteUnCheckedAnimator != null && mDeleteUnCheckedAnimator.isRunning()) {
            mDeleteUnCheckedAnimator.cancel();
        }

        handleRippleUnCheckedAnimator();

        if (isDelete) {
            handleDeleteUnCheckedAnimator();
            mDeleteUnCheckedAnimator.start();
        } else {
            handleRightUnCheckedAnimator();
            mRightUnCheckedAnimator.start();
        }
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
                startAnim(false);
            } else {
                startUnAnim(false);
            }

            if (mListener != null) {
                mListener.onCheckedChanged(this, isChecked);
            }
        } else {
            setChecked(checked);
        }
    }

    /**
     * change RippleCheckBox check status
     * @param isDelete RippleCheckBox的删除状态  RippleCheckBox's delete status
     * @param animal 是否开启动画效果  Whether to turn on animation effects
     */
    public void setDelete(boolean isDelete, boolean animal) {
        if (animal) {
            this.isDelete = isDelete;
            if (this.isDelete) {
                startAnim(true);
            } else {
                startUnAnim(true);
            }

            if (mDeleteListener != null) {
                mDeleteListener.onDeleteChanged(this, isDelete);
            }
        } else {
            setDelete(isDelete);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public void setDelete(boolean isDelete) {
        this.isDelete = isDelete;
        invalidate();
    }

    public boolean isDelete() {
        return isDelete;
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

    public int getDeleteCorner() {
        return mDeleteCorner;
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

    public void setOnDeleteChangeListener(OnDeleteChangeListener l) {
        this.mDeleteListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RippleCheckBox checkBox, boolean isChecked);
    }

    public interface OnDeleteChangeListener {
        void onDeleteChanged(RippleCheckBox checkBox, boolean isDelete);
    }
}
