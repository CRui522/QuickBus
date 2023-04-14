package com.quick.quickbus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlowLayout extends ViewGroup {

    private static final String TAG = FlowLayout.class.getSimpleName();

    private static final int LEFT = -1;
    private static final int CENTER = 0;
    private static final int RIGHT = 1;
    // 给每一行数据设置 显示位置
    private final int mGravity = LEFT;
    // 存储所有的子View
    protected List<List<View>> mAllViews = new ArrayList<List<View>>();
    // 记录每一行的最大高度
    protected List<Integer> mLineHeight = new ArrayList<Integer>();
    // 记录每一行的宽度
    protected List<Integer> mLineWidth = new ArrayList<Integer>();
    // 存储一行的子view
    private List<View> lineViews = new ArrayList<>();
    private int mMaxLine = Integer.MAX_VALUE;

    private boolean mIsSingleLine = false;

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;
        int lineNum = 0;

        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == cCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
                lineNum++;
                if (lineNum >= mMaxLine) {
                    break;
                }
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == cCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        // EXACTLY代表具体高度或者match_parent, 如果是EXACTLY,直接从MeasureSpec中取。否则通过计算取值
        int measuredWidth = modeWidth == MeasureSpec.EXACTLY
                ? sizeWidth : width + getPaddingLeft() + getPaddingRight();
        int measuredHeight = modeHeight == MeasureSpec.EXACTLY
                ? sizeHeight : height + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measuredWidth, measuredHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mIsSingleLine) {
            onSingleLineLayout(changed, l, t, r, b);
        } else {
            onNormalLayout(changed, l, t, r, b);
        }
    }

    private void onNormalLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        mLineWidth.clear();
        lineViews.clear();
        // 流式布局的宽度
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        // 先记录流式布局中的view的 宽、高等信息，一边后续执行布局（layout)时使用
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin
                    > width - getPaddingLeft() - getPaddingRight()) { // 条件满足，代表本行已经放满
                // 记录该行的高度，宽度，以及放置的View
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);
                mLineWidth.add(lineWidth);

                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                lineViews = new ArrayList<View>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);

        }
        // 记录最后一行的数据(高度、宽度、View)
        mLineHeight.add(lineHeight);
        mLineWidth.add(lineWidth);
        mAllViews.add(lineViews);

        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();
        if (lineNum > mMaxLine) {
            lineNum = mMaxLine;
        }
        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            int currentLineWidth = this.mLineWidth.get(i);
            // 设置每一行的显示位置（居左、居中、居右）
            switch (this.mGravity) {
                case LEFT:
                    left = getPaddingLeft();
                    break;
                case CENTER:
                    left = (width - currentLineWidth) / 2 + getPaddingLeft();
                    break;
                case RIGHT:
                    //  适配了rtl，需要补偿一个padding值
                    left = width - (currentLineWidth + getPaddingLeft()) - getPaddingRight();
                    //  适配了rtl，需要把lineViews里面的数组倒序排
                    Collections.reverse(lineViews);
                    break;
                default:
                    break;
            }

            // 为每一个item进行布局layout
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin
                        + lp.rightMargin;
            }
            top += lineHeight;
        }
    }

    private void onSingleLineLayout(boolean changed, int l, int t, int r, int b) {

        List<View> lineViews = new ArrayList<>();

        // 流式布局的宽度
        int parentWidth = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();

        int endIndex = 0;

        // 先记录流式布局中的view的 宽、高等信息，一边后续执行布局（layout)时使用
        for (int i = 0; i < cCount; i++) {

            View child = getChildAt(i);

            if (endIndex > 0 && i > endIndex) {
                child.setVisibility(View.GONE);
                continue;
            }

            if (child.getVisibility() == View.GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            int restWidth = parentWidth - getPaddingLeft() - getPaddingRight();
            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin <= restWidth) {
                lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            } else {
                lineWidth = parentWidth;
                endIndex = i;
            }
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }

        int left = getPaddingLeft();
        int top = getPaddingTop();

        // 为每一个item进行布局layout
        for (int j = 0; j < lineViews.size(); j++) {

            View child = lineViews.get(j);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int lc = left + lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();

            child.layout(lc, tc, rc, bc);

            left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * 调用addView时，如果不指定child View的LayoutParams，那么使用该函数默认生成的LayoutParams以供
     * child View使用
     *
     * @return
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void setMaxLine(int maxLine) {
        this.mMaxLine = maxLine;
    }

    public void setSingleLine(boolean isSingleLine) {
        mIsSingleLine = isSingleLine;
    }
}

