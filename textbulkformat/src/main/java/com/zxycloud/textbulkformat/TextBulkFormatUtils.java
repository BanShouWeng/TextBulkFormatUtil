package com.zxycloud.textbulkformat;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.util.SparseArray;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextBulkFormatUtils {
    private static final String PLACE_HOLDER = "\u00A0";
    /**
     * 右对齐
     */
    public static final int LENGTH_FORMAT_RIGHT = 0x10;
    /**
     * 两端对齐
     */
    public static final int LENGTH_FORMAT_BOTH_ENDS = 0x11;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LENGTH_FORMAT_RIGHT, LENGTH_FORMAT_BOTH_ENDS})
    @interface LengthFormatType {
    }

    public synchronized static Map<String, SpannableString> formatStringLength(@LengthFormatType int type, String... strings) {
        return formatStringLength(type, false, strings);
    }

    public synchronized static Map<String, SpannableString> formatStringLength(@LengthFormatType int type, boolean isEnglishSplit, String... strings) {
        List<Float> lengths = new ArrayList<>();
        Map<String, SpannableString> stringMap = new HashMap<>();
        float maxLength = 0;
        float currentLength;
        for (String string : strings) {
            currentLength = getStringLength(string);
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
            lengths.add(currentLength);
        }
        if (maxLength > 0) {
            for (int i = 0; i < strings.length; i++) {
                String fillString = strings[i];
                stringMap.put(fillString, fillSpace((int) ((maxLength - lengths.get(i)) / getStringLength(PLACE_HOLDER)), fillString, type, isEnglishSplit));
            }
        }
        return stringMap;
    }

    /**
     * 格式化字符串，右对齐/两端对齐
     *
     * @param context   上下文
     * @param stringRes 字符串Res
     * @return 对齐后的字符串map
     */
    public synchronized static SparseArray<SpannableString> formatStringLength(Context context, @LengthFormatType int type, @StringRes int... stringRes) {
        return formatStringLength(context, type, false, stringRes);
    }

    public synchronized static SparseArray<SpannableString> formatStringLength(Context context, @LengthFormatType int type, boolean isEnglishSplit, @StringRes int... stringRes) {
        List<Float> lengths = new ArrayList<>();
        SparseArray<SpannableString> stringMap = new SparseArray<>();
        float maxLength = 0;

        String currentString;
        for (int string : stringRes) {
            currentString = getString(context, string);
//            int currentLength = getStringShowLength(currentString);
            float currentLength = getStringLength(currentString);
            if (currentLength > maxLength) {
                maxLength = currentLength;
            }
            lengths.add(currentLength);
        }
        if (maxLength > 0) {
            for (int i = 0; i < stringRes.length; i++) {
                int fillString = stringRes[i];
                stringMap.put(fillString, fillSpace((int) ((maxLength - lengths.get(i)) / getStringLength(PLACE_HOLDER)), getString(context, fillString), type, isEnglishSplit));
            }
        }
        return stringMap;
    }

    /**
     * 添加空格
     *
     * @param multiple 放大倍数
     * @param s        被填充字符串
     * @param type     类型
     * @return 填充后的结果
     */
    private synchronized static SpannableString fillSpace(float multiple, String s, int type, boolean isEnglishSplit) {
        if (isEmpty(s)) {
            return new SpannableString(s);
        }
        if (multiple > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            SpannableString spannableString;
            switch (type) {
                case LENGTH_FORMAT_BOTH_ENDS:
                    char[] chars = s.toCharArray();
                    // 如果英语单词中间也做拆分
                    if (isEnglishSplit) {
                        int charSize = chars.length;
                        if (charSize == 1) {
                            // 如果只有一位char的情况下，占位符拼接
                            StringBuilder builder = new StringBuilder();
                            builder.append(PLACE_HOLDER).append(chars[0]).append(PLACE_HOLDER);
                            spannableString = new SpannableString(builder);
                            // 调整单一char前后的占位符宽度
                            spannableString.setSpan(new ScaleXSpan(multiple / 2), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ScaleXSpan(multiple / 2), 2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            return spannableString;
                        } else {
                            // 占位符拼接
                            stringBuilder.append(chars[0]);
                            for (int i = 1; i < charSize; i++) {
                                stringBuilder.append(PLACE_HOLDER).append(chars[i]);
                            }
                            // 依据缺少的宽度，调整每一个占位符宽度，达到宽度相同
                            spannableString = new SpannableString(stringBuilder);
                            for (int i = 1; i < spannableString.length(); i = i + 2) {
                                // 放大每一个占位符
                                spannableString.setSpan(new ScaleXSpan(multiple / (charSize - 1)), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            }
                            return spannableString;
                        }
                    } else {
                        // 字符串列表，由于英文/数字不拆分，因此需将被填充字符串拆分为需补充占位符的部分（拆分项）
                        List<String> strings = new ArrayList<>();
                        // 字符串缓存，用于存储需拼接的英文/数字
                        StringBuilder stringTemp = new StringBuilder();
                        boolean isCharSpan = false;
                        for (char c : chars) {
                            /*
                             * 判断是否是大小写字母或数字，如果是，则在字符串缓存中拼接，若不是，则直接添加到列表中
                             * isUpperCase  大写字母匹配
                             * isLowerCase  小写字母匹配
                             * isDigit      数字匹配
                             * "'"          ' 是英文缩写常用字符，因此也与字符做相同处理
                             */
                            if (CharacterJudge.isEnChar(c)
                                    || CharacterJudge.isNum(c)
                                    || String.valueOf(c).equals("'")
                                    || String.valueOf(c).equals("_")) {
                                stringTemp.append(c);
                                isCharSpan = true;
                                continue;
                            }
                            isCharSpan = false;
                            /*
                             * 若有缓存中的字母/数字，则取缓存字符串；若没有则取对应的char
                             */
                            if (stringTemp.length() > 0) {
                                if (isSpaceChar(c)) {
                                    // 如果是空格，则只需要存储缓存的字符串，空格自动忽略
                                    strings.add(stringTemp.toString());
                                    // 清空缓存
                                    stringTemp.setLength(0);
                                } else if (CharacterJudge.isPunctuation(c)) {
                                    // 如果是标点符号，则需要在存储缓存的字符串后拼接该标点，其后再读取这个标点
                                    stringTemp.append(c);
                                    strings.add(stringTemp.toString());
                                    stringTemp.setLength(0);
                                } else {
                                    // 如果不满足上面判断，则添加缓存字符串后，再添加一条当前char，如：“项目deadline是今晚”，拆分后位{"项","目","deadline","是","今","晚"}
                                    strings.add(stringTemp.toString());
                                    stringTemp.setLength(0);
                                    strings.add(String.valueOf(c));
                                }
                            } else {
                                strings.add(String.valueOf(c));
                            }
                        }
                        if (isCharSpan) {
                            strings.add(stringTemp.toString());
                        }

                        int stringSize = strings.size();
                        if (stringSize == 1) {
                            // 只有一个字符串的情况下
                            StringBuilder builder = new StringBuilder();
                            builder.append(PLACE_HOLDER).append(strings.get(0)).append(PLACE_HOLDER);
                            spannableString = new SpannableString(builder);
                            // 获取第一个占位符位置，并调整占位符宽度
                            spannableString.setSpan(new ScaleXSpan(multiple / 2), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            // 获取第二个占位符位置，并调整占位符宽度
                            String indexGetTemp = builder.toString();
                            int placeHolderPosition = indexGetTemp.indexOf(strings.get(0)) + strings.get(0).length();
                            spannableString.setSpan(new ScaleXSpan(multiple / 2), placeHolderPosition, placeHolderPosition + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                            return spannableString;
                        } else {
                            // 占位符拼接
                            stringBuilder.append(strings.get(0));
                            for (int i = 1; i < stringSize; i++) {
                                stringBuilder.append(PLACE_HOLDER).append(strings.get(i));
                            }
                            // 依据缺少的宽度，调整每一个占位符宽度，达到宽度相同
                            spannableString = new SpannableString(stringBuilder);
                            // 由于SpannableString没有indexOf方法，因此创建一个缓存String获取占位符位置
                            String indexGetTemp = stringBuilder.toString();
                            int lastPlaceHolderIndex = 0;
                            // 由于占位符是添加到各字符串之间，因此最后一个字符串
                            for (int i = 0; i < stringSize - 1; i++) {
                                // 每次需要获取上一次得到的占位符后的位置
                                int currentPlaceHolderIndex = indexGetTemp.indexOf(strings.get(i), lastPlaceHolderIndex);
                                /* 由于indexOf获取的是第一位的位置，但占位符是在整个字符串后的位置，因此需要加字符串的长度。
                                 * 如："1234 ".indexOf("1234")为0，但空格所在的位置为0 + "1234".length()，也就是4
                                 */
                                currentPlaceHolderIndex += strings.get(i).length();
                                // 放大每一个占位符
                                spannableString.setSpan(new ScaleXSpan(multiple / (stringSize - 1)), currentPlaceHolderIndex, currentPlaceHolderIndex + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                lastPlaceHolderIndex = currentPlaceHolderIndex;
                            }
                            return spannableString;
                        }
                    }

                case LENGTH_FORMAT_RIGHT:
                    stringBuilder.append(PLACE_HOLDER);
                    stringBuilder.append(s);
                    spannableString = new SpannableString(stringBuilder);
                    spannableString.setSpan(new ScaleXSpan(multiple), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    return spannableString;
            }
            return new SpannableString(stringBuilder);
        }
        return new SpannableString(s);
    }

    /**
     * 校验一个字符是否是空格
     * \u00A0 ：不间断空格\u00A0,主要用在office中,让一个单词在结尾处不会换行显示,快捷键ctrl+shift+space;
     * \u0020 ：半角空格(英文符号)\u0020,代码中常用的;
     * \u3000 ：全角空格(中文符号)\u3000,中文文章中使用;
     *
     * @param c 被校验的字符
     * @return true代表是空格
     */
    public synchronized static boolean isSpaceChar(char c) {
        String[] spaces = {"\u00A0", "\u0020", "\u3000"};
        String s = String.valueOf(c);
        return spaces[0].equals(s)
                || spaces[1].equals(s)
                || spaces[2].equals(s);
    }

    public synchronized static float getStringLength(String s) {
        return new TextPaint().measureText(s);
    }

    /**
     * 获取stringId指向的文本
     *
     * @param context    上下文
     * @param resourceId 文本Id
     * @return 文本对应的字符串
     */
    public static String getString(Context context, int resourceId) {
        return context.getResources().getString(resourceId);
    }

    /**
     * 是否为空
     *
     * @param str 待判断字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
}
