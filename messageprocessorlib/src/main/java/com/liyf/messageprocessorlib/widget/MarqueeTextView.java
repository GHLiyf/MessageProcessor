package com.liyf.messageprocessorlib.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 *   MarqueeTextView使用说明
 *   android:ellipsize="marquee"
 *   android:gravity="center"
 *   android:marqueeRepeatLimit="marquee_forever"
 *   android:singleLine="true"
 */
public class MarqueeTextView extends android.support.v7.widget.AppCompatTextView {

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFocused() {
		return true;
	}

}