package com.evilreader.android.evilcontentcontroller;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class EvilreaderViewPager extends ViewPager {

	public static boolean enabled;

	public EvilreaderViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public EvilreaderViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		enabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	public void setPagingEnabled(boolean enabled) {
		EvilreaderViewPager.enabled = enabled;
	}
}
