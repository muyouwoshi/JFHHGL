package com.juxin.predestinate.module.logic.baseui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.juxin.library.log.PLogger;
import com.juxin.predestinate.R;
import com.juxin.predestinate.module.local.msgview.chatview.input.ChatSmile;
import com.juxin.predestinate.module.util.UIUtil;

/**
 * 表情
 */
public class EmojiTextView extends TextView {
    private static final int DEFAULT_EMOJI_SIZE = -1;

    private int emojiSize = -1;

    public EmojiTextView(Context context) {
        this(context, null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmojiTextView);
        emojiSize = typedArray.getDimensionPixelSize(R.styleable.EmojiTextView_emojiSize, DEFAULT_EMOJI_SIZE);
        typedArray.recycle();
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EmojiTextView, defStyle, 0);
        emojiSize = typedArray.getDimensionPixelSize(R.styleable.EmojiTextView_emojiSize, DEFAULT_EMOJI_SIZE);
        typedArray.recycle();
    }

    @Override
    public void setTextAppearance(Context context, int resid) {
        super.setTextAppearance(context, resid);
    }

    /**
     * 获取表情图片的大小。
     *
     * @return pixels
     */
    public int getEmojiSize() {
        return emojiSize;
    }

    /**
     * 设置表情图片的大小。
     *
     * @param emojiSize pixels。-1表示使用字体大小。
     */
    public void setEmojiSize(int emojiSize) {
        this.emojiSize = emojiSize;
    }

    /**
     * 设置表情图片的大小。
     *
     * @param emojiSize dp。-1表示使用字体大小。
     */
    public void setEmojiDPSize(int emojiSize) {
        if (emojiSize == -1) {
            this.emojiSize = emojiSize;
        } else {
            this.emojiSize = UIUtil.dp2px(emojiSize);
        }
    }

    public void setTextContent(String text) {
        Spannable span;
        if (emojiSize == -1) {
            span = ChatSmile.getSmiledText(getContext(), text, (int) getTextSize());
        } else {
            span = ChatSmile.getSmiledText(getContext(), text, emojiSize);
        }

        setText(span, BufferType.SPANNABLE);
    }
}
