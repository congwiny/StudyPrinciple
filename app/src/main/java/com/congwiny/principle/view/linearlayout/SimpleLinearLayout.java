package com.congwiny.principle.view.linearlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;

import com.congwiny.principle.R;

public class SimpleLinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mOrientation = HORIZONTAL;

    public SimpleLinearLayout(Context context) {
        super(context);
    }

    public SimpleLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public SimpleLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleLinearLayout);
        mOrientation = a.getInt(R.styleable.SimpleLinearLayout_orientation, HORIZONTAL);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOrientation == VERTICAL) {
            measureVertical(widthMeasureSpec, heightMeasureSpec);
        } else {
            measureHorizontal(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void measureVertical(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int count = getChildCount();

        int totalHeight = paddingTop + paddingBottom;
        int maxChildWidth = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidthSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                    lp.height);

            child.measure(childWidthSpec, childHeightSpec);

            int measuredW = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int measuredH = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            totalHeight += measuredH;
            maxChildWidth = Math.max(maxChildWidth, measuredW);
        }

        int width = resolveSize(maxChildWidth + paddingLeft + paddingRight, widthMeasureSpec);
        int height = resolveSize(totalHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private void measureHorizontal(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int count = getChildCount();

        int totalWidth = paddingLeft + paddingRight;
        int maxChildHeight = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidthSpec = getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                    lp.height);

            child.measure(childWidthSpec, childHeightSpec);

            int measuredW = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int measuredH = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            totalWidth += measuredW;
            maxChildHeight = Math.max(maxChildHeight, measuredH);
        }

        int width = resolveSize(totalWidth, widthMeasureSpec);
        int height = resolveSize(maxChildHeight + paddingTop + paddingBottom, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mOrientation == VERTICAL) {
            layoutVertical(left, top, right, bottom);
        } else {
            layoutHorizontal(left, top, right, bottom);
        }
    }

    // 1. 自定义 LayoutParams
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * 子 View 的对齐方式，借用系统 Gravity 常量
         */
        public int gravity = Gravity.NO_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.SimpleLinearLayout_Layout);
            gravity = a.getInt(R.styleable.SimpleLinearLayout_Layout_android_layout_gravity,
                    Gravity.NO_GRAVITY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    // 2. 覆盖生成 LayoutParams 的方法
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    // 3. 修改 onLayout，应用 gravity
    private void layoutVertical(int left, int top, int right, int bottom) {
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int width = right - left;
        int y = getPaddingTop();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            // 计算水平位置
            int childLeft;
            int horizontalGravity = lp.gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            switch (horizontalGravity) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = paddingLeft
                            + lp.leftMargin
                            + (width - paddingLeft - paddingRight
                            - lp.leftMargin - lp.rightMargin - childW) / 2;
                    break;
                case Gravity.RIGHT:
                    childLeft = width - paddingRight - lp.rightMargin - childW;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = paddingLeft + lp.leftMargin;
            }

            int cl = childLeft;
            int ct = y + lp.topMargin;
            int cr = cl + childW;
            int cb = ct + childH;
            child.layout(cl, ct, cr, cb);

            y = cb + lp.bottomMargin;
        }
    }

    private void layoutHorizontal(int left, int top, int right, int bottom) {
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        final int height = bottom - top;
        int x = getPaddingLeft();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int childW = child.getMeasuredWidth();
            int childH = child.getMeasuredHeight();

            // 计算垂直位置
            int childTop;
            int verticalGravity = lp.gravity & Gravity.VERTICAL_GRAVITY_MASK;
            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = paddingTop
                            + lp.topMargin
                            + (height - paddingTop - paddingBottom
                            - lp.topMargin - lp.bottomMargin - childH) / 2;
                    break;
                case Gravity.BOTTOM:
                    childTop = height - paddingBottom - lp.bottomMargin - childH;
                    break;
                case Gravity.TOP:
                default:
                    childTop = paddingTop + lp.topMargin;
            }

            int cl = x + lp.leftMargin;
            int ct = childTop;
            int cr = cl + childW;
            int cb = ct + childH;
            child.layout(cl, ct, cr, cb);

            x = cr + lp.rightMargin;
        }
    }
}
