package com.video.tamas.Utils;

/**
 * Created by Dipendra Sharma  on 11-05-2019.
 */

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public class DirectionalViewPager extends ViewPager {
    private static final String TAG = "DirectionalViewPager";
    private static final String XML_NS = "http://schemas.android.com/apk/res/android";
    private static final boolean DEBUG = false;
    private static final boolean USE_CACHE = false;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private final ArrayList<ItemInfo> mItems = new ArrayList();
    private PagerAdapter mAdapter;
    private int mOffscreenPageLimit = 1;
    private int mCurItem;
    private int mRestoredCurItem = -1;
    private Parcelable mRestoredAdapterState = null;
    private ClassLoader mRestoredClassLoader = null;
    private Scroller mScroller;
    private android.database.DataSetObserver mObserver;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mChildWidthMeasureSpec;
    private int mChildHeightMeasureSpec;
    private boolean mInLayout;
    private boolean mScrollingCacheEnabled;
    private boolean mPopulatePending;
    private boolean mScrolling;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mTouchSlop;
    private float mInitialMotion;
    private float mLastMotionX;
    private float mLastMotionY;
    private int mOrientation = 0;
    private int mActivePointerId = -1;
    private static final int INVALID_POINTER = -1;
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private OnPageChangeListener mOnPageChangeListener;
    private int mScrollState = 0;

    public DirectionalViewPager(Context context) {
        super(context);
        this.initViewPager();
    }

    public DirectionalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initViewPager();
        int orientation = attrs.getAttributeIntValue("http://schemas.android.com/apk/res/android", "orientation", -1);
        if (orientation != -1) {
            this.setOrientation(orientation);
        }

    }

    void initViewPager() {
        this.setWillNotDraw(false);
        this.mScroller = new Scroller(this.getContext());
        ViewConfiguration configuration = ViewConfiguration.get(this.getContext());
        this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    private void setScrollState(int newState) {
        if (this.mScrollState != newState) {
            this.mScrollState = newState;
            if (this.mOnPageChangeListener != null) {
                this.mOnPageChangeListener.onPageScrollStateChanged(newState);
            }

        }
    }

    public void setAdapter(PagerAdapter adapter) {
        if (this.mAdapter != null) {
            mAdapter.registerDataSetObserver(mObserver);
            //VerticalViewPagerCompat.setDataSetObserver(this.mAdapter, (android.support.v4.view.VerticalViewPagerCompat.DataSetObserver)null);
        }

        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new DataSetObserver();
            }

            //VerticalViewPagerCompat.setDataSetObserver(this.mAdapter, this.mObserver);
            this.mPopulatePending = false;
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                this.setCurrentItemInternal(this.mRestoredCurItem, false, true);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = null;
                this.mRestoredClassLoader = null;
            } else {
                this.populate();
            }
        }

    }

    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setCurrentItem(int item) {
        this.mPopulatePending = false;
        this.setCurrentItemInternal(item, true, false);
    }

    void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
        if (this.mAdapter != null && this.mAdapter.getCount() > 0) {
            if (!always && this.mCurItem == item && this.mItems.size() != 0) {
                this.setScrollingCacheEnabled(false);
            } else {
                if (item < 0) {
                    item = 0;
                } else if (item >= this.mAdapter.getCount()) {
                    item = this.mAdapter.getCount() - 1;
                }

                if (item > this.mCurItem + 1 || item < this.mCurItem - 1) {
                    for(int i = 0; i < this.mItems.size(); ++i) {
                        ((ItemInfo)this.mItems.get(i)).scrolling = true;
                    }
                }

                boolean dispatchSelected = this.mCurItem != item;
                this.mCurItem = item;
                this.populate();
                if (smoothScroll) {
                    if (this.mOrientation == 0) {
                        this.smoothScrollTo(this.getWidth() * item, 0);
                    } else {
                        this.smoothScrollTo(0, this.getHeight() * item);
                    }

                    if (dispatchSelected && this.mOnPageChangeListener != null) {
                        this.mOnPageChangeListener.onPageSelected(item);
                    }
                } else {
                    if (dispatchSelected && this.mOnPageChangeListener != null) {
                        this.mOnPageChangeListener.onPageSelected(item);
                    }

                    this.completeScroll();
                    if (this.mOrientation == 0) {
                        this.scrollTo(this.getWidth() * item, 0);
                    } else {
                        this.scrollTo(0, this.getHeight() * item);
                    }
                }

            }
        } else {
            this.setScrollingCacheEnabled(false);
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOnPageChangeListener = listener;
    }

    void smoothScrollTo(int x, int y) {
        if (this.getChildCount() == 0) {
            this.setScrollingCacheEnabled(false);
        } else {
            int sx = this.getScrollX();
            int sy = this.getScrollY();
            int dx = x - sx;
            int dy = y - sy;
            if (dx == 0 && dy == 0) {
                this.completeScroll();
            } else {
                this.setScrollingCacheEnabled(true);
                this.mScrolling = true;
                this.setScrollState(2);
                this.mScroller.startScroll(sx, sy, dx, dy);
                this.invalidate();
            }
        }
    }

    void addNewItem(int position, int index) {
        ItemInfo ii = new ItemInfo();
        ii.position = position;
        ii.object = this.mAdapter.instantiateItem(this, position);
        if (index < 0) {
            this.mItems.add(ii);
        } else {
            this.mItems.add(index, ii);
        }

    }

    void dataSetChanged() {
        boolean needPopulate = this.mItems.isEmpty() && this.mAdapter.getCount() > 0;
        int newCurrItem = -1;

        for(int i = 0; i < this.mItems.size(); ++i) {
            ItemInfo ii = (ItemInfo)this.mItems.get(i);
            int newPos = this.mAdapter.getItemPosition(ii.object);
            if (newPos != -1) {
                if (newPos == -2) {
                    this.mItems.remove(i);
                    --i;
                    this.mAdapter.destroyItem(this, ii.position, ii.object);
                    needPopulate = true;
                    if (this.mCurItem == ii.position) {
                        newCurrItem = Math.max(0, Math.min(this.mCurItem, this.mAdapter.getCount() - 1));
                    }
                } else if (ii.position != newPos) {
                    if (ii.position == this.mCurItem) {
                        newCurrItem = newPos;
                    }

                    ii.position = newPos;
                    needPopulate = true;
                }
            }
        }

        if (newCurrItem >= 0) {
            this.setCurrentItemInternal(newCurrItem, false, true);
            needPopulate = true;
        }

        if (needPopulate) {
            this.populate();
            this.requestLayout();
        }

    }

    void populate() {
        if (this.mAdapter != null) {
            if (!this.mPopulatePending) {
                if (this.getWindowToken() != null) {
                    this.mAdapter.startUpdate(this);
                    int startPos = this.mCurItem > 0 ? this.mCurItem - 1 : this.mCurItem;
                    int count = this.mAdapter.getCount();
                    int endPos = this.mCurItem < count - 1 ? this.mCurItem + 1 : count - 1;
                    int lastPos = -1;

                    for(int i = 0; i < this.mItems.size(); ++i) {
                        ItemInfo ii = (ItemInfo)this.mItems.get(i);
                        if ((ii.position < startPos || ii.position > endPos) && !ii.scrolling) {
                            this.mItems.remove(i);
                            --i;
                            this.mAdapter.destroyItem(this, ii.position, ii.object);
                        } else if (lastPos < endPos && ii.position > startPos) {
                            ++lastPos;
                            if (lastPos < startPos) {
                                lastPos = startPos;
                            }

                            while(lastPos <= endPos && lastPos < ii.position) {
                                this.addNewItem(lastPos, i);
                                ++lastPos;
                                ++i;
                            }
                        }

                        lastPos = ii.position;
                    }

                    lastPos = this.mItems.size() > 0 ? ((ItemInfo)this.mItems.get(this.mItems.size() - 1)).position : -1;
                    if (lastPos < endPos) {
                        ++lastPos;

                        for(lastPos = lastPos > startPos ? lastPos : startPos; lastPos <= endPos; ++lastPos) {
                            this.addNewItem(lastPos, -1);
                        }
                    }

                    this.mAdapter.finishUpdate(this);
                }
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.position = this.mCurItem;
        if (this.mAdapter!=null){
        ss.adapterState = this.mAdapter.saveState();}
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            SavedState ss = (SavedState)state;
            super.onRestoreInstanceState(ss.getSuperState());
            if (this.mAdapter != null) {
                this.mAdapter.restoreState(ss.adapterState, ss.loader);
                this.setCurrentItemInternal(ss.position, false, true);
            } else {
                this.mRestoredCurItem = ss.position;
                this.mRestoredAdapterState = ss.adapterState;
                this.mRestoredClassLoader = ss.loader;
            }

        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        switch(orientation) {
            case 0:
            case 1:
                if (orientation == this.mOrientation) {
                    return;
                }

                this.completeScroll();
                this.mInitialMotion = 0.0F;
                this.mLastMotionX = 0.0F;
                this.mLastMotionY = 0.0F;
                if (this.mVelocityTracker != null) {
                    this.mVelocityTracker.clear();
                }

                this.mOrientation = orientation;
                if (this.mOrientation == 0) {
                    this.scrollTo(this.mCurItem * this.getWidth(), 0);
                } else {
                    this.scrollTo(0, this.mCurItem * this.getHeight());
                }

                this.requestLayout();
                return;
            default:
                throw new IllegalArgumentException("Only HORIZONTAL and VERTICAL are valid orientations.");
        }
    }

    public void addView(View child, int index, LayoutParams params) {
        if (this.mInLayout) {
            this.addViewInLayout(child, index, params);
            child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
        } else {
            super.addView(child, index, params);
        }

    }

    ItemInfo infoForChild(View child) {
        for(int i = 0; i < this.mItems.size(); ++i) {
            ItemInfo ii = (ItemInfo)this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(child, ii.object)) {
                return ii;
            }
        }

        return null;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAdapter != null) {
            this.populate();
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        this.mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredWidth() - this.getPaddingLeft() - this.getPaddingRight(), 1073741824);
        this.mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom(), 1073741824);
        this.mInLayout = true;
        this.populate();
        this.mInLayout = false;
        int size = this.getChildCount();

        for(int i = 0; i < size; ++i) {
            View child = this.getChildAt(i);
            if (child.getVisibility() != 8) {
                child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
            }
        }

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int scrollPos;
        if (this.mOrientation == 0) {
            scrollPos = this.mCurItem * w;
            if (scrollPos != this.getScrollX()) {
                this.completeScroll();
                this.scrollTo(scrollPos, this.getScrollY());
            }
        } else {
            scrollPos = this.mCurItem * h;
            if (scrollPos != this.getScrollY()) {
                this.completeScroll();
                this.scrollTo(this.getScrollX(), scrollPos);
            }
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.mInLayout = true;
        this.populate();
        this.mInLayout = false;
        int count = this.getChildCount();
        int size = this.mOrientation == 0 ? r - l : b - t;

        for(int i = 0; i < count; ++i) {
            View child = this.getChildAt(i);
            ItemInfo ii;
            if (child.getVisibility() != 8 && (ii = this.infoForChild(child)) != null) {
                int off = size * ii.position;
                int childLeft = this.getPaddingLeft();
                int childTop = this.getPaddingTop();
                if (this.mOrientation == 0) {
                    childLeft += off;
                } else {
                    childTop += off;
                }

                child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
            }
        }

    }

    public void computeScroll() {
        if (!this.mScroller.isFinished() && this.mScroller.computeScrollOffset()) {
            int oldX = this.getScrollX();
            int oldY = this.getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                this.scrollTo(x, y);
            }

            if (this.mOnPageChangeListener != null) {
                int size;
                int value;
                if (this.mOrientation == 0) {
                    size = this.getWidth();
                    value = x;
                } else {
                    size = this.getHeight();
                    value = y;
                }

                int position = value / size;
                int offsetPixels = value % size;
                float offset = (float)offsetPixels / (float)size;
                this.mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
            }

            this.invalidate();
        } else {
            this.completeScroll();
        }
    }

    private void completeScroll() {
        boolean needPopulate;
        int i;
        if (needPopulate = this.mScrolling) {
            this.setScrollingCacheEnabled(false);
            this.mScroller.abortAnimation();
            i = this.getScrollX();
            int oldY = this.getScrollY();
            int x = this.mScroller.getCurrX();
            int y = this.mScroller.getCurrY();
            if (i != x || oldY != y) {
                this.scrollTo(x, y);
            }

            this.setScrollState(0);
        }

        this.mPopulatePending = false;
        this.mScrolling = false;

        for(i = 0; i < this.mItems.size(); ++i) {
            ItemInfo ii = (ItemInfo)this.mItems.get(i);
            if (ii.scrolling) {
                needPopulate = true;
                ii.scrolling = false;
            }
        }

        if (needPopulate) {
            this.populate();
        }

    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & 255;
        if (action != 3 && action != 1) {
            if (action != 0) {
                if (this.mIsBeingDragged) {
                    return true;
                }

                if (this.mIsUnableToDrag) {
                    return false;
                }
            }

            switch(action) {
                case 0:
                    if (this.mOrientation == 0) {
                        this.mLastMotionX = this.mInitialMotion = ev.getX();
                        this.mLastMotionY = ev.getY();
                    } else {
                        this.mLastMotionX = ev.getX();
                        this.mLastMotionY = this.mInitialMotion = ev.getY();
                    }

                    this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    if (this.mScrollState == 2) {
                        this.mIsBeingDragged = true;
                        this.mIsUnableToDrag = false;
                        this.setScrollState(1);
                    } else {
                        this.completeScroll();
                        this.mIsBeingDragged = false;
                        this.mIsUnableToDrag = false;
                    }
                    break;
                case 2:
                    int activePointerId = this.mActivePointerId;
                    if (activePointerId != -1 || VERSION.SDK_INT <= 4) {
                        int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                        float x = MotionEventCompat.getX(ev, pointerIndex);
                        float y = MotionEventCompat.getY(ev, pointerIndex);
                        float xDiff = Math.abs(x - this.mLastMotionX);
                        float yDiff = Math.abs(y - this.mLastMotionY);
                        float primaryDiff;
                        float secondaryDiff;
                        if (this.mOrientation == 0) {
                            primaryDiff = xDiff;
                            secondaryDiff = yDiff;
                        } else {
                            primaryDiff = yDiff;
                            secondaryDiff = xDiff;
                        }

                        if (primaryDiff > (float)this.mTouchSlop && primaryDiff > secondaryDiff) {
                            this.mIsBeingDragged = true;
                            this.setScrollState(1);
                            if (this.mOrientation == 0) {
                                this.mLastMotionX = x;
                            } else {
                                this.mLastMotionY = y;
                            }

                            this.setScrollingCacheEnabled(true);
                        } else if (secondaryDiff > (float)this.mTouchSlop) {
                            this.mIsUnableToDrag = true;
                        }
                    }
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
            }

            return this.mIsBeingDragged;
        } else {
            this.mIsBeingDragged = false;
            this.mIsUnableToDrag = false;
            this.mActivePointerId = -1;
            return false;
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && ev.getEdgeFlags() != 0) {
            return false;
        } else if (this.mAdapter != null && this.mAdapter.getCount() != 0) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }

            this.mVelocityTracker.addMovement(ev);
            int action = ev.getAction();
            int activePointerIndex;
            float y;
            int size;
            switch(action & 255) {
                case 0:
                    this.completeScroll();
                    if (this.mOrientation == 0) {
                        this.mLastMotionX = this.mInitialMotion = ev.getX();
                    } else {
                        this.mLastMotionY = this.mInitialMotion = ev.getY();
                    }

                    this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                    break;
                case 1:
                    if (this.mIsBeingDragged) {
                        VelocityTracker velocityTracker = this.mVelocityTracker;
                        velocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                        int initialVelocity;
                        if (this.mOrientation == 0) {
                            initialVelocity = (int) VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
                            y = this.mLastMotionX;
                            size = this.getWidth() / 3;
                        } else {
                            initialVelocity = (int) VelocityTrackerCompat.getYVelocity(velocityTracker, this.mActivePointerId);
                            y = this.mLastMotionY;
                            size = this.getHeight() / 3;
                        }

                        this.mPopulatePending = true;
                        if (Math.abs(initialVelocity) <= this.mMinimumVelocity && Math.abs(this.mInitialMotion - y) < (float)size) {
                            this.setCurrentItemInternal(this.mCurItem, true, true);
                        } else if (y > this.mInitialMotion) {
                            this.setCurrentItemInternal(this.mCurItem - 1, true, true);
                        } else {
                            this.setCurrentItemInternal(this.mCurItem + 1, true, true);
                        }

                        this.mActivePointerId = -1;
                        this.endDrag();
                    }
                    break;
                case 2:
                    float x;
                    float scroll;
                    float lowerBound;
                    float upperBound;
                    if (!this.mIsBeingDragged) {
                        activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                        x = MotionEventCompat.getX(ev, activePointerIndex);
                        y = MotionEventCompat.getY(ev, activePointerIndex);
                        float xDiff = Math.abs(x - this.mLastMotionX);
                        scroll = Math.abs(y - this.mLastMotionY);
                        if (this.mOrientation == 0) {
                            lowerBound = xDiff;
                            upperBound = scroll;
                        } else {
                            lowerBound = scroll;
                            upperBound = xDiff;
                        }

                        if (lowerBound > (float)this.mTouchSlop && lowerBound > upperBound) {
                            this.mIsBeingDragged = true;
                            if (this.mOrientation == 0) {
                                this.mLastMotionX = x;
                            } else {
                                this.mLastMotionY = y;
                            }

                            this.setScrollState(1);
                            this.setScrollingCacheEnabled(true);
                        }
                    }

                    if (this.mIsBeingDragged) {
                        activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                        x = MotionEventCompat.getX(ev, activePointerIndex);
                        y = MotionEventCompat.getY(ev, activePointerIndex);
                        if (this.mOrientation == 0) {
                            size = this.getWidth();
                            scroll = (float)this.getScrollX() + (this.mLastMotionX - x);
                            this.mLastMotionX = x;
                        } else {
                            size = this.getHeight();
                            scroll = (float)this.getScrollY() + (this.mLastMotionY - y);
                            this.mLastMotionY = y;
                        }

                        lowerBound = (float)Math.max(0, (this.mCurItem - 1) * size);
                        upperBound = (float)(Math.min(this.mCurItem + 1, this.mAdapter.getCount() - 1) * size);
                        if (scroll < lowerBound) {
                            scroll = lowerBound;
                        } else if (scroll > upperBound) {
                            scroll = upperBound;
                        }

                        if (this.mOrientation == 0) {
                            this.mLastMotionX += scroll - (float)((int)scroll);
                            this.scrollTo((int)scroll, this.getScrollY());
                        } else {
                            this.mLastMotionY += scroll - (float)((int)scroll);
                            this.scrollTo(this.getScrollX(), (int)scroll);
                        }

                        if (this.mOnPageChangeListener != null) {
                            int position = (int)scroll / size;
                            int positionOffsetPixels = (int)scroll % size;
                            float positionOffset = (float)positionOffsetPixels / (float)size;
                            this.mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        }
                    }
                    break;
                case 3:
                    if (this.mIsBeingDragged) {
                        this.setCurrentItemInternal(this.mCurItem, true, true);
                        this.mActivePointerId = -1;
                        this.endDrag();
                    }
                case 4:
                default:
                    break;
                case 5:
                    activePointerIndex = MotionEventCompat.getActionIndex(ev);
                    if (this.mOrientation == 0) {
                        this.mLastMotionX = MotionEventCompat.getX(ev, activePointerIndex);
                    } else {
                        this.mLastMotionY = MotionEventCompat.getY(ev, activePointerIndex);
                    }

                    this.mActivePointerId = MotionEventCompat.getPointerId(ev, activePointerIndex);
                    break;
                case 6:
                    this.onSecondaryPointerUp(ev);
                    activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
                    if (this.mOrientation == 0) {
                        this.mLastMotionX = MotionEventCompat.getX(ev, activePointerIndex);
                    } else {
                        this.mLastMotionY = MotionEventCompat.getY(ev, activePointerIndex);
                    }
            }

            return true;
        } else {
            return false;
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        int pointerIndex = MotionEventCompat.getActionIndex(ev);
        int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == this.mActivePointerId) {
            int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            if (this.mOrientation == 0) {
                this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            } else {
                this.mLastMotionY = MotionEventCompat.getY(ev, newPointerIndex);
            }

            this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }

    }

    private void endDrag() {
        this.mIsBeingDragged = false;
        this.mIsUnableToDrag = false;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }

    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (this.mScrollingCacheEnabled != enabled) {
            this.mScrollingCacheEnabled = enabled;
        }

    }

    private class DataSetObserver extends android.database.DataSetObserver {
        private DataSetObserver() {
        }

        public void onDataSetChanged() {
            this.onDataSetChanged();
        }
    }

    public static class SavedState extends BaseSavedState {
        int position;
        Parcelable adapterState;
        ClassLoader loader;
        public static final Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        });

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.position);
            out.writeParcelable(this.adapterState, flags);
        }

        public String toString() {
            return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in);
            if (loader == null) {
                loader = this.getClass().getClassLoader();
            }

            this.position = in.readInt();
            this.adapterState = in.readParcelable(loader);
            this.loader = loader;
        }
    }

    static class ItemInfo {
        Object object;
        int position;
        boolean scrolling;

        ItemInfo() {
        }
    }
    public void addOnPageChangeListener(@NonNull ViewPager.OnPageChangeListener listener) {
        Log.v("dip","inside super lisner");
        if (this.mOnPageChangeListener== null) {
        //this.mOnPageChangeListener= new ArrayList<ViewPager.OnPageChangeListener>();
        }

        this.mOnPageChangeListener=listener;
    }

       public void setOffscreenPageLimit(int limit) {
        if (limit < 1) {
            Log.w("ViewPager", "Requested offscreen page limit " + limit + " too small; defaulting to " + 1);
            limit = 1;
        }

        if (limit != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = limit;
            this.populate();
        }

    }


}
