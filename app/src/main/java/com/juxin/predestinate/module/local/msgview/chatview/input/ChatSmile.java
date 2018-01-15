package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.text.Spannable;

/**
 * 维护聊天表情的实现。
 */
public class ChatSmile {
    public static final EmojiPack emojiPack = new EmojiPack();
    private static final Spannable.Factory spannableFactory = Spannable.Factory.getInstance();

    static {
        emojiPack.init();
    }

    public static String getSimpleSmiledText(String text) {
        return emojiPack.repleaseSimpleSmiles(text);
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        return getSmiledText(context, text, -1);
    }

    public static Spannable getSmiledText(Context context, CharSequence text, int size) {
        Spannable spannable = spannableFactory.newSpannable(text == null ? "" : text);
        emojiPack.addSmiles(context, spannable, size);
        return spannable;
    }

    public static boolean containsKey(String key) {
        return emojiPack.containsKey(key);
    }

    public static int getSmileCount() {
        return emojiPack.getCount();
    }
}
