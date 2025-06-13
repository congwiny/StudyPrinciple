package com.congwiny.principle.view.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;

public class MyScrollView extends ViewGroup {
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private float mLastMotionY;
    private boolean mIsBeingDragged;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    public MyScrollView(Context context) {
        super(context);
        init(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new OverScroller(context);
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMinimumVelocity = vc.getScaledMinimumFlingVelocity();
        mMaximumVelocity = vc.getScaledMaximumFlingVelocity();
        setWillNotDraw(false);
    }

    // ---- 测量与布局 ----
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 1) {
            throw new IllegalStateException("MyScrollView can host only one direct child");
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (getChildCount() == 1) {
            View child = getChildAt(0);
            measureChild(child, widthMeasureSpec, MeasureSpec.UNSPECIFIED);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 1) {
            View child = getChildAt(0);
            // 布局在 (paddingLeft, paddingTop)
            int left = getPaddingLeft();
            int top = getPaddingTop();
            child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
        }
    }

    // ---- 触摸事件拦截 ----
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                mIsBeingDragged = !mScroller.isFinished();
                break;

            case MotionEvent.ACTION_MOVE:
                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) break;
                float y = ev.getY(pointerIndex);
                float dy = Math.abs(y - mLastMotionY);
                if (dy > mTouchSlop) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                    initOrResetVelocityTracker();
                    mVelocityTracker.addMovement(ev);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                recycleVelocityTracker();
                break;
        }

        return mIsBeingDragged;
    }

    // ---- 触摸事件处理 ----
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initOrResetVelocityTracker();
        mVelocityTracker.addMovement(ev);

        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                return true;

            case MotionEvent.ACTION_MOVE:
                int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) return false;
                float y = ev.getY(pointerIndex);
                float deltaY = mLastMotionY - y;
                mLastMotionY = y;
                scrollBy(0, (int) deltaY);
                clampScroll();
                break;

            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) mVelocityTracker.getYVelocity(mActivePointerId);
                if (Math.abs(initialVelocity) > mMinimumVelocity) {
                    fling(-initialVelocity);
                }
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                recycleVelocityTracker();
                break;

            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    private void initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    // ---- 惯性滑动 ----
    public void fling(int velocityY) {
        int maxY = getScrollRange();
        mScroller.fling(
                getScrollX(), getScrollY(),
                0, velocityY,
                0, 0,
                0, maxY
        );
        invalidate();
    }

    // ---- 平滑滚动驱动 ----
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    // ---- 边界限制 ----
    @Override
    public void scrollTo(int x, int y) {
        int newY = Math.max(0, Math.min(y, getScrollRange()));
        super.scrollTo(x, newY);
    }

    private void clampScroll() {
        int scrollY = getScrollY();
        if (scrollY < 0) {
            scrollTo(0, 0);
        } else if (scrollY > getScrollRange()) {
            scrollTo(0, getScrollRange());
        }
    }

    private int getScrollRange() {
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            int contentHeight = child.getMeasuredHeight();
            int viewportHeight = getHeight() - getPaddingTop() - getPaddingBottom();
            return Math.max(0, contentHeight - viewportHeight);
        }
        return 0;
    }
}
