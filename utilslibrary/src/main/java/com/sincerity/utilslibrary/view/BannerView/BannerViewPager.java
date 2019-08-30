package com.sincerity.utilslibrary.view.BannerView;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import java.lang.reflect.Field;


/**
 * Created by Sincerity on 2019/8/29.
 * 描述：
 */
public class BannerViewPager extends ViewPager {
    private BannerAdapter mAdapter;
    private final int MSG_SCROLLER = 0X0011;
    private int mCurrentSecond = 2000;
    private static String TAG = "BannerView";
    /*自定义页面切换的速率*/
    private BannerScroller scroller;

    /**
     *
     * @param mCurrentSecond 设置滚动的间隔时间
     */
    public void setCurrentSecond(int mCurrentSecond) {
        this.mCurrentSecond = mCurrentSecond;
    }

    /**
     * Handler会引发内存泄漏
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setCurrentItem(getCurrentItem() + 1);
            startSColl();
        }
    };

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //改变ViewPager的切换速率 反射
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            /* 第一个参数是当前对象,第二个是一个插值器*/
            scroller = new BannerScroller(context, new AccelerateInterpolator());
            field.setAccessible(true);//强制改变私有属性
            /*第一个参数代表参数在哪个类第二个参数代表要设置的值*/
            field.set(this, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param mScrollerDuration 滚动的速率
     */
    public void setScrollerDuration(int mScrollerDuration) {
        scroller.setScrollerDuration(mScrollerDuration);
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        this.mAdapter = adapter;
        setAdapter(new BannerPagerAdapter());
    }

    /**
     * 自动滚动
     */
    public void startSColl() {
        mHandler.removeMessages(MSG_SCROLLER);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLLER, mCurrentSecond);

    }
    //创建适配器
    private class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //为了实现无限循环
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            //ViewPager里面对每个页面的管理是key-value形式的，
            // 也就是说每个page都有个对应的id（id是object类型），
            // 需要对page操作的时候都是通过id来完成的
            return view == o;
        }

        /**
         * 创建ItemView的方法
         *
         * @param container viewPager
         * @param position 当前的位置
         * @return
         */
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            //采用Adapter适配模式,为了完全让用户自定义   position的变化值为0-65566
            View ItemView = mAdapter.getView(position % mAdapter.getCount());
            container.addView(ItemView);
            return ItemView;
        }

        /**
         * 销毁ItemView的方法
         *
         * @param container ViewPager
         * @param position 当前位置
         * @param object ItemView
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            object = null;
        }
    }

    /**
     * 处理内存泄露
     * 当Activity销毁后会回调这个方法
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_SCROLLER);
        mHandler = null;
    }
}