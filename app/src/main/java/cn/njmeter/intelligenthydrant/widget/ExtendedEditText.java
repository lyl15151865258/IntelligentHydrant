package cn.njmeter.intelligenthydrant.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.ArrayList;

/**
 * 用于互相监听的EditText，避免陷入死循环
 * Created by LiYuliang on 2018/1/31.
 *
 * @author LiYuliang
 * @version 2018/1/31
 */

public class ExtendedEditText extends AppCompatEditText {

    private ArrayList<TextWatcher> mListeners = null;

    public ExtendedEditText(Context ctx) {
        super(ctx);
    }

    public ExtendedEditText(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public ExtendedEditText(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
    }

    @Override
    public void addTextChangedListener(TextWatcher watcher) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(watcher);
        super.addTextChangedListener(watcher);
    }

    @Override
    public void removeTextChangedListener(TextWatcher watcher) {
        if (mListeners != null) {
            int i = mListeners.indexOf(watcher);
            if (i >= 0) {
                mListeners.remove(i);
            }
        }
        super.removeTextChangedListener(watcher);
    }

    public void clearTextChangedListeners() {
        if (mListeners != null) {
            for (TextWatcher watcher : mListeners) {
                super.removeTextChangedListener(watcher);
            }
            mListeners.clear();
            mListeners = null;
        }
    }
}
