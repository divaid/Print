package com.weixing.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.weixing.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wb-cuiweixing on 2016/12/26.
 */
public class LabelEditText extends EditText {
    public interface OnIconClickListener {
        void onIconClick();
    }

    public static final int TEXT_ALIGN_CENTER = 0x00000000;
    public static final int TEXT_ALIGN_LEFT = 0x00000001;
    public static final int TEXT_ALIGN_RIGHT = 0x00000010;
    public static final int TEXT_ALIGN_CENTER_VERTICAL = 0x00000100;
    public static final int TEXT_ALIGN_CENTER_HORIZONTAL = 0x00001000;
    public static final int TEXT_ALIGN_TOP = 0x00010000;
    public static final int TEXT_ALIGN_BOTTOM = 0x00100000;

    /**
     * 文字的颜色
     */
    private int mLabelColor;
    /**
     * 文字的大小
     */
    private int mLabelSize;
    /**
     * 文字的方位
     */
    private int mLabelAlign = TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL;

    /**
     * 标签与边框间隔
     */
    private int mLabelSpace;

    /**
     * 控件的宽度
     */
    private int mViewWidth;
    /**
     * 控件的高度
     */
    private int mViewHeight;

    /**
     * 文本中轴线X坐标
     */
    private float mLabelCenterX;
    /**
     * 文本baseline线Y坐标
     */
    private float mLabelBaselineY;

    /**
     * 控件画笔
     */
    private Paint mPaint;

    /**
     * 图标宽度
     */
    private int mIconWidth;

    private Paint.FontMetrics fm;

    private List<ClickArea> mRightClickAreas;

    private String mLabel = "输入库位：";
    private float mLabelWidth;

    public LabelEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        init(context);
    }

    // TODO: 2017/3/9 添加使用属性设置样式与标签
    private void initAttrs(Context context, AttributeSet attrs) {
//        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.labelAttrs);
//
//        mLabel = ta.getString(R.styleable.labelAttrs_labelText);
//        mLabelColor= ta.getColor(R.styleable.labelAttrs_labelColor, Color.GRAY);
//        mLabelSize= (int) ta.getDimension(R.styleable.labelAttrs_labelSize, 16);
//        mLabelAlign= ta.getInteger(R.styleable.labelAttrs_labelAlign, TEXT_ALIGN_LEFT|TEXT_ALIGN_CENTER_VERTICAL);
//        mLabelSpace = (int) ta.getDimension(R.styleable.labelAttrs_labelSpace, 0);
//        ta.recycle();

        mLabelSpace = DensityUtil.dip2px(context, 6);
        mLabelSize = DensityUtil.sp2px(context, 20);
    }

    private void init(Context context) {
        // TODO: 2017/3/9 设置实际字体样式
        mPaint = new Paint();
        mPaint.setTextSize(mLabelSize);
        mPaint.setColor(Color.parseColor("#00ff00"));
        mPaint.setAntiAlias(true);
        fm = mPaint.getFontMetrics();
        setSingleLine(false);
        mLabelBaselineY = mViewHeight / 2 - fm.descent + (fm.descent - fm.ascent) / 2;

        mRightClickAreas = new ArrayList<ClickArea>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!TextUtils.isEmpty(mLabel)) {
            canvas.drawText(mLabel, mLabelCenterX, mLabelBaselineY + 1.5f, mPaint);
        }

        drawRightIcon(canvas);
    }

    private void drawRightIcon(Canvas canvas) {
        if (mRightClickAreas != null && mRightClickAreas.size() > 0) {
            for (int i = 0; i < mRightClickAreas.size(); ++i) {
                ClickArea clickArea = mRightClickAreas.get(i);

                if (clickArea.mClickArea == null) {
                    mIconWidth = mViewHeight - 2 * mLabelSpace;

                    int[] clickAreas = new int[4];
                    clickAreas[3] = mViewHeight - mLabelSpace;
                    clickAreas[2] = mViewWidth - (mIconWidth + mLabelSpace) * i - mLabelSpace;
                    clickAreas[1] = mLabelSpace;
                    clickAreas[0] = mViewWidth - (mIconWidth + mLabelSpace) * (i + 1);
                    clickArea.mClickArea = clickAreas;
                }


                Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), clickArea.mIconRes);

                Rect srcRect = new Rect();
                srcRect.left = 0;
                srcRect.right = bitmap.getWidth();
                srcRect.top = 0;
                srcRect.bottom = bitmap.getHeight();
//                float radio = (float)(srcRect.bottom - srcRect.top)  / bitmap.getWidth();
                RectF dstRecF = new RectF();
                dstRecF.left = clickArea.mClickArea[0];
                dstRecF.right = clickArea.mClickArea[2];
                dstRecF.top = clickArea.mClickArea[1];
//                float dstHeight = (dstRecF.right - dstRecF.left) * radio;
                dstRecF.bottom = clickArea.mClickArea[3];
                canvas.drawBitmap(bitmap, srcRect, dstRecF, mPaint);
                bitmap.recycle();
            }
        }
    }

    /**
     * 动态设置右边label
     *
     * @param label
     */
    public void setLabelText(String label) {
        mLabel = label;
        mLabelWidth = mPaint.measureText(mLabel);
        setTextLocation();
        invalidate();
    }

    public String getLabelText() {
        return mLabel;
    }

    /**
     * 定位文本绘制的位置，
     */
    private void setTextLocation() {
        fm = mPaint.getFontMetrics();
        float textCenterVerticalBaselineY = mViewHeight / 2 - fm.descent + (fm.descent - fm.ascent) / 2;
        switch (mLabelAlign) {
            case TEXT_ALIGN_CENTER_HORIZONTAL | TEXT_ALIGN_CENTER_VERTICAL:
                mLabelCenterX = (float) mViewWidth + mLabelSpace;
                mLabelBaselineY = textCenterVerticalBaselineY;
                break;
            case TEXT_ALIGN_LEFT | TEXT_ALIGN_CENTER_VERTICAL:
                mLabelCenterX = mLabelSpace;
                mLabelBaselineY = textCenterVerticalBaselineY;

                setPadding(mLabelSpace + (int) mLabelWidth, 0, 0, 0);
                break;
            case TEXT_ALIGN_RIGHT | TEXT_ALIGN_CENTER_VERTICAL:
                mLabelCenterX = mViewWidth - mLabelWidth - mLabelSpace;
                mLabelBaselineY = textCenterVerticalBaselineY;

                setPadding(0, 0, mLabelSpace + (int) mLabelWidth, 0);
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_CENTER_HORIZONTAL:
                mLabelCenterX = mViewWidth / 2 - mLabelWidth / 2;
                mLabelBaselineY = mViewHeight - fm.bottom - mLabelSize / 2;

                setPadding(0, 0, 0, mLabelSpace + mLabelSize);
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_CENTER_HORIZONTAL:
                mLabelCenterX = mViewWidth / 2 - mLabelWidth / 2;
                mLabelBaselineY = -fm.ascent + mLabelSize / 2;

                setPadding(0, mLabelSpace + mLabelSize, 0, 0);
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_LEFT:// TODO: 2017/3/9 上下左右自定义间距
                mLabelCenterX = mLabelSpace;
                mLabelBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_LEFT:
                mLabelCenterX = mLabelSpace;
                mLabelBaselineY = mViewHeight - fm.bottom;
                break;
            case TEXT_ALIGN_TOP | TEXT_ALIGN_RIGHT:
                mLabelCenterX = mViewWidth - mLabelWidth - mLabelSpace;
                mLabelBaselineY = -fm.ascent;
                break;
            case TEXT_ALIGN_BOTTOM | TEXT_ALIGN_RIGHT:
                mLabelCenterX = mViewWidth - mLabelWidth - mLabelSpace;
                mLabelBaselineY = mViewHeight - fm.bottom;
                break;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mViewWidth = getWidth();
        mViewHeight = getHeight();

        //xml布局中设置了label
        if (!TextUtils.isEmpty(mLabel)) {
            mLabelWidth = mPaint.measureText(mLabel);
            setTextLocation();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    public void addLeftIcon(int res) {

    }

    public void addLeftIcon(int res, OnIconClickListener onIconClickListener) {

    }

    public void addRightIcon(int res) {
        addRightIcon(res, null);
    }

    public void addRightIcon(int res, OnIconClickListener onIconClickListener) {
        if (mRightClickAreas == null) {
            mRightClickAreas = new ArrayList<ClickArea>();
        }

        ClickArea clickArea = new ClickArea(res, onIconClickListener, null);
        // TODO: 2017/3/9 添加上下与左边图片添加及实现icon与label排序

        mRightClickAreas.add(clickArea);

        invalidate();

    }

    public void setLabelColor(int mTextColor) {
        this.mLabelColor = mTextColor;
    }

    public void setLabelSize(int mTextSize) {
        this.mLabelSize = mTextSize;
    }

    public void setLabelAlign(int mTextAlign) {
        this.mLabelAlign = mTextAlign;
    }

    public void setLabelSpace(int mLabelSpace) {
        this.mLabelSpace = mLabelSpace;
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public int getLabelSize() {
        return mLabelSize;
    }

    public int getLabelAlign() {
        return mLabelAlign;
    }

    public int getLabelSpace() {
        return mLabelSpace;
    }

    private class ClickArea {
        private OnIconClickListener mOnIconClickListener;
        private int[] mClickArea;
        private int mIconRes;

        ClickArea(int iconRes, OnIconClickListener onIconClickListener, int[] clickArea) {
            this.mIconRes = iconRes;
            this.mOnIconClickListener = onIconClickListener;
            this.mClickArea = clickArea;
        }

        private boolean isPointInArea(float x, float y) {
            return x >= mClickArea[0] && y >= mClickArea[1] && x <= mClickArea[2] && y <= mClickArea[3];
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && mRightClickAreas != null && mRightClickAreas.size() > 0) {
            for (ClickArea clickArea : mRightClickAreas) {
                if (clickArea.mOnIconClickListener != null && clickArea.isPointInArea(event.getX(0), event.getY(0))) {
                    clickArea.mOnIconClickListener.onIconClick();
                    return true;
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return super.dispatchTouchEvent(event);
    }
}
