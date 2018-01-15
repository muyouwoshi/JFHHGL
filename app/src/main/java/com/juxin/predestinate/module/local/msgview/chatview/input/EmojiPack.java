package com.juxin.predestinate.module.local.msgview.chatview.input;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import com.juxin.library.log.PLogger;
import com.juxin.library.utils.FileUtil;
import com.juxin.predestinate.module.logic.application.App;
import com.juxin.predestinate.module.logic.baseui.custom.VerticalImageSpan;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理表情包。
 */
public class EmojiPack {
    private String name = null;
    private String desc = null;
    private String key = null;
    private String prefix = null;

    private final List<EmojiItem> emoticons = new ArrayList<EmojiItem>();

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getKey() {
        return key;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * 表情个数。
     *
     * @return 个数。
     */
    public int getCount() {
        return emoticons.size();
    }

    /**
     * 获取表情的资源对关系。
     *
     * @return
     */
    public List<EmojiItem> getEmoji() {
        return emoticons;
    }

    /**
     * 初始化一个新包。
     *
     * @return false初始化失败。
     */
    public boolean init() {
        String jsonStr = FileUtil.readAssets(App.context, "emoji.json");

        if (TextUtils.isEmpty(jsonStr)) {
            return false;
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);

            name = jsonObject.optString("name");
            desc = jsonObject.optString("desc");
            prefix = jsonObject.optString("prefix");

            key = jsonObject.getString("key");
            jsonObject = jsonObject.optJSONObject("files");

            emoticons.clear();

            Iterator it = jsonObject.keys();

            while (it.hasNext()) {
                String tempKey = it.next().toString();

                emoticons.add(addPattern(tempKey, jsonObject.optString(tempKey)));
            }
        } catch (JSONException e) {
            PLogger.printThrowable(e);
            return false;
        }

        return true;
    }


    /**
     * 通过正则表达式匹配。
     *
     * @param smile
     */
    private EmojiItem addPattern(String smile, String desc) {
        EmojiItem emojiItem = new EmojiItem();

        emojiItem.key = "[" + desc + "]";
        emojiItem.desc = desc;
        emojiItem.pattern = Pattern.compile(Pattern.quote(emojiItem.key));
     //   emojiItem.resId = UIUtil.getResIdFromDrawable(prefix + smile);
        emojiItem.name = "face/" + prefix + smile + ".png";
        return emojiItem;
    }

    /**
     * replace existing spannable with smiles
     *
     * @param text
     * @return
     */
    public String repleaseSimpleSmiles(String text) {
        String temp = text;

        for (EmojiItem emoticon : emoticons) {
            Matcher matcher = emoticon.pattern.matcher(temp);

            if (matcher.find()) {
                temp = matcher.replaceAll("[表情]");
            }
        }

        return temp;
    }

    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @param size      -1 表示不限定高度
     * @return
     */
    public boolean addSmiles(Context context, Spannable spannable, int size) {
        boolean hasChanges = false;

        for (EmojiItem emoticon : emoticons) {
            Matcher matcher = emoticon.pattern.matcher(spannable);

            while (matcher.find()) {
                boolean set = true;

                for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                }

                if (set) {
                    hasChanges = true;
                    VerticalImageSpan imageSpan = null;
                    try {
                        Bitmap mBitmap = BitmapFactory.decodeStream(context.getAssets().open(emoticon.name));
                        if (-1 == size) {
                            imageSpan = new VerticalImageSpan(context, mBitmap);
                        } else {
                            Drawable drawable = new BitmapDrawable(mBitmap);
                            drawable.setBounds(0, 0, size, size);
                            imageSpan = new VerticalImageSpan(drawable);
                        }
                        spannable.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

        return hasChanges;
    }

    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public boolean addSmiles(Context context, Spannable spannable) {
        return addSmiles(context, spannable, -1);
    }

    /**
     * 是否包含对应的key。
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        boolean b = false;

        for (EmojiItem emoticon : emoticons) {
            Matcher matcher = emoticon.pattern.matcher(key);

            if (matcher.find()) {
                b = true;
                break;
            }
        }

        return b;
    }

    public static class EmojiItem {
        public String key = null;
        public String desc = null;
        public Pattern pattern = null;
        public String name = null;
    }
}
