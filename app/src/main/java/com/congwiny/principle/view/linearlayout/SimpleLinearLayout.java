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
        int paddingLeft   = getPaddingLeft();
        int paddingRight  = getPaddingRight();
        int paddingTop    = getPaddingTop();
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
        int paddingLeft   = getPaddingLeft();
        int paddingRight  = getPaddingRight();
        int paddingTop    = getPaddingTop();
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

    private void layoutVertical(int left, int top, int right, int bottom) {
        int x = getPaddingLeft();
        int y = getPaddingLeft();
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int cl = x + lp.leftMargin;
            int ct = y + lp.topMargin;
            int cr = cl + child.getMeasuredWidth();
            int cb = ct + child.getMeasuredHeight();

            child.layout(cl, ct, cr, cb);
            y = cb + lp.bottomMargin;
        }
    }

    private void layoutHorizontal(int left, int top, int right, int bottom) {
        int x = getPaddingLeft();
        int y = getPaddingTop();
        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int cl = x + lp.leftMargin;
            int ct = y + lp.topMargin;
            int cr = cl + child.getMeasuredWidth();
            int cb = ct + child.getMeasuredHeight();

            child.layout(cl, ct, cr, cb);
            x = cr + lp.rightMargin;
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
