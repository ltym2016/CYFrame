package com.caiyu.lib_base.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatButton;

import com.caiyu.lib_base.R;


/**
 * @author luys
 * @describe
 * @date 2019/3/11
 * @email samluys@foxmail.com
 */
public class UIButton extends AppCompatButton {

    private final int TOP_LEFT = 1;
    private final int TOP_RIGHT = 2;
    private final int BOTTOM_RIGHT = 4;
    private final int BOTTOM_LEFT = 8;

    /**
     * shape模式 rectangle（矩形） oval（椭圆） line（线性） ring（环形）
     */
    private int mShapeMode;

    /**
     * 填充颜色
     */
    private int mSolidColor;

    /**
     * 边框颜色
     */
    private int mStrokeColor;

    /**
     * 按压颜色
     */
    private int mPressedColor;

    /**
     * 边框宽度
     */
    private int mStrokeWidth;

    /**
     * 圆角大小
     */
    private int mCornerRadius;

    /**
     * 圆角位置
     */
    private int mCornerPosition;

    /**
     * 是否开启点击动画效果
     */
    private boolean mActiveEnable = false;

    /**
     * 渐变起始色
     */
    private int mStartColor;

    /**
     * 渐变中间颜色
     */
    private int mCenterColor;

    /**
     * 渐变终了色
     */
    private int mEndColor;

    /**
     * 渐变方向 从上到下 从左到右
     */
    private int mOrientation = 0;

    /**
     * 普通的Shape样式
     */
    private GradientDrawable nomalGradientDrawble = new GradientDrawable();

    /**
     * 按压shape样式
     */
    private GradientDrawable pressedGradientDrawble = new GradientDrawable();

    /**
     * shape样式集合
     */
    private StateListDrawable stateListDrawable = new StateListDrawable();


    public UIButton(Context context) {
        this(context, null);
    }

    public UIButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UIButton);
        mShapeMode = typedArray.getInt(R.styleable.UIButton_qf_shapeMode, 0);
        mSolidColor = typedArray.getColor(R.styleable.UIButton_qf_solid, 0xFFFFFFFF);
        mPressedColor = typedArray.getColor(R.styleable.UIButton_qf_pressedColor, 0xFFC3C3C3);
        mStrokeColor = typedArray.getColor(R.styleable.UIButton_qf_stroke, 0);
        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.UIButton_qf_strokeWidth, 0);
        mCornerRadius = typedArray.getDimensionPixelSize(R.styleable.UIButton_qf_cornerRadius, 0);
        mCornerPosition = typedArray.getInt(R.styleable.UIButton_qf_cornerPosition, -1);
        mActiveEnable = typedArray.getBoolean(R.styleable.UIButton_qf_activeEnable, false);
        mStartColor = typedArray.getColor(R.styleable.UIButton_qf_startColor, 0xFFFFFFFF);
        mEndColor = typedArray.getColor(R.styleable.UIButton_qf_endColor, 0xFFFFFFFF);
        mOrientation = typedArray.getColor(R.styleable.UIButton_qf_orientation, 0);
        mCenterColor = typedArray.getColor(R.styleable.UIButton_qf_centerColor, 0xFFFFFFFF);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        nomalGradientDrawble.setColor(mSolidColor);

        // 设置shape的模式
        setShapeMode();
        // 设置背景颜色
        setSolidColor();
        // 设置圆角半径 默认设置4个点击的半径
        setCornerRadius();
        // 设置边框颜色和宽度
        setStroke();

        // 点击波纹效果处理
        if (mActiveEnable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setBackground(new RippleDrawable(ColorStateList.valueOf(mPressedColor), nomalGradientDrawble, null));
            } else {
                pressedGradientDrawble.setColor(mPressedColor);
                if (mShapeMode == 0) {
                    pressedGradientDrawble.setShape(GradientDrawable.RECTANGLE);
                } else if (mShapeMode == 1) {
                    pressedGradientDrawble.setShape(GradientDrawable.OVAL);
                } else if (mShapeMode == 2) {
                    pressedGradientDrawble.setShape(GradientDrawable.LINE);
                } else if (mShapeMode == 3) {
                    pressedGradientDrawble.setShape(GradientDrawable.RING);
                }
                pressedGradientDrawble.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mCornerRadius, getResources().getDisplayMetrics()));
                pressedGradientDrawble.setStroke(mStrokeWidth, mStrokeColor);

                // 设置按下状态
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedGradientDrawble);
                // 设置正常状态
                stateListDrawable.addState(new int[]{}, nomalGradientDrawble);

                setBackground(stateListDrawable);
            }
        } else {
            setBackground(nomalGradientDrawble);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        setClickable(true);
    }

    /**
     * 设置边框颜色和宽度
     */
    private void setStroke() {
        if (mStrokeColor != 0) {
            nomalGradientDrawble.setStroke(mStrokeWidth, mStrokeColor);
        }
    }

    /**
     * 设置圆角半径 默认设置4个点击的半径
     */
    private void setCornerRadius() {
        if (mCornerPosition == -1) {
            nomalGradientDrawble.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                    mCornerRadius, getResources().getDisplayMetrics()));
        } else {
            // 根据圆角位置设置圆角半径
            nomalGradientDrawble.setCornerRadii(getCornerRadiusByPosition());
        }
    }

    public void setQfSolidColor (int colorId) {
        mSolidColor = colorId;
        setSolidColor();
    }

    /**
     * 设置背景颜色(包括渐变)
     */
    private void setSolidColor() {
        if (mStartColor != 0xFFFFFFFF && mEndColor != 0xFFFFFFFF) {
            // 渐变色
            int[] colors;
            if (mCenterColor != 0xFFFFFFFF) {
                colors = new int[]{mStartColor,mCenterColor, mEndColor};
            } else {
                colors = new int[]{mStartColor,mEndColor};
            }
            nomalGradientDrawble.setColors(colors);
            // 从上到下
            if (mOrientation == 0) {
                nomalGradientDrawble.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
            // 从左到右
            } else if (mOrientation == 1) {
                nomalGradientDrawble.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
            // 从右上角到左底 对角线
            } else if (mOrientation == 2) {
                nomalGradientDrawble.setOrientation(GradientDrawable.Orientation.TR_BL);
            // 从左上角到右底 对角线
            } else if (mOrientation == 3) {
                nomalGradientDrawble.setOrientation(GradientDrawable.Orientation.TL_BR);
            }
        } else {
            // 正常色
            nomalGradientDrawble.setColor(mSolidColor);
        }
    }

    /**
     * 设置shape的模式
     */
    private void setShapeMode() {
        if (mShapeMode == 0) {
            nomalGradientDrawble.setShape(GradientDrawable.RECTANGLE);
        } else if (mShapeMode == 1) {
            nomalGradientDrawble.setShape(GradientDrawable.OVAL);
        } else if (mShapeMode == 2) {
            nomalGradientDrawble.setShape(GradientDrawable.LINE);
        } else if (mShapeMode == 3) {
            nomalGradientDrawble.setShape(GradientDrawable.RING);
        }
    }

    /**
     * 根据圆角位置获取圆角半径
     */
    private float[] getCornerRadiusByPosition() {
        float[] result = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        if (containsFlag(mCornerPosition, TOP_LEFT)) {
            result[0] = mCornerRadius;
            result[1] = mCornerRadius;
        }
        if (containsFlag(mCornerPosition, TOP_RIGHT)) {
            result[2] = mCornerRadius;
            result[3] = mCornerRadius;
        }
        if (containsFlag(mCornerPosition, BOTTOM_RIGHT)) {
            result[4] = mCornerRadius;
            result[5] = mCornerRadius;
        }
        if (containsFlag(mCornerPosition, BOTTOM_LEFT)) {
            result[6] = mCornerRadius;
            result[7] = mCornerRadius;
        }
        return result;
    }

    /**
     * 是否包含对应flag
     * 按位或
     */
    private boolean containsFlag(int flagSet, int flag) {
        return (flagSet | flag) == flagSet;
    }
}
