package com.congwiny.principle.view.linearlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.View;

import com.congwiny.principle.R;

public class SimpleLinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    private int mOrientation = HORIZONTAL; // 默认水平布局

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
        int paddingLeft   = getPaddingLeft();
        int paddingRight  = getPaddingRight();
        int paddingTop    = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int count = getChildCount();
        int totalWidth  = paddingLeft + paddingRight;
        int totalHeight = paddingTop + paddingBottom;
        int maxChildWidth  = 0;
        int maxChildHeight = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidthSpec  = getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                    lp.width);
            int childHeightSpec = getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin,
                    lp.height);

            child.measure(childWidthSpec, childHeightSpec);

            int measuredW = child.getMeasuredWidth()  + lp.leftMargin + lp.rightMargin;
            int measuredH = child.getMeasuredHeight() + lp.topMargin  + lp.bottomMargin;

            if (mOrientation == HORIZONTAL) {
                totalWidth  += measuredW;
                maxChildHeight = Math.max(maxChildHeight, measuredH);
            } else {
                totalHeight += measuredH;
                maxChildWidth = Math.max(maxChildWidth, measuredW);
            }
        }

        int finalWidth;
        int finalHeight;
        if (mOrientation == HORIZONTAL) {
            finalWidth  = resolveSize(totalWidth, widthMeasureSpec);
            finalHeight = resolveSize(maxChildHeight + paddingTop + paddingBottom, heightMeasureSpec);
        } else {
            finalWidth  = resolveSize(maxChildWidth  + paddingLeft + paddingRight, widthMeasureSpec);
            finalHeight = resolveSize(totalHeight,                heightMeasureSpec);
        }

        setMeasuredDimension(finalWidth, finalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int paddingLeft = getPaddingLeft();
        int paddingTop  = getPaddingTop();
        int x = paddingLeft;
        int y = paddingTop;

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int l = x + lp.leftMargin;
            int t = y + lp.topMargin;
            int r = l + child.getMeasuredWidth();
            int b = t + child.getMeasuredHeight();

            child.layout(l, t, r, b);

            if (mOrientation == HORIZONTAL) {
                x = r + lp.rightMargin;
            } else {
                y = b + lp.bottomMargin;
            }
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}
