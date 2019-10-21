package com.halcyon.helpModel;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by attract on 3/12/15.
 */
public class TextViewFontAwesome extends TextView {
    public TextViewFontAwesome(Context context) {
        super(context);
        init();
    }

    public TextViewFontAwesome(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewFontAwesome(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome-webfont.ttf");
        setTypeface(tf);
    }
}
