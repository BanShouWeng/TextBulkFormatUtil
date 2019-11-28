package com.zxycloud.textbulkformat;

import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class CharacterJudge {
    /**
     * 是否是数字
     *
     * @param c 待验字符串
     * @return 是否为数字
     */
    public static boolean isNum(char c) {
        return Character.isDigit(c);
    }

    /**
     * 是否是大写字母
     *
     * @param c 待验字符串
     * @return 是否为大写字母
     */
    public static boolean isUpperCase(char c) {
        return Character.isUpperCase(c);
    }

    /**
     * 是否是小写字母
     *
     * @param c 待验字符串
     * @return 是否为小写字母
     */
    public static boolean isLowerCase(char c) {
        return Character.isLowerCase(c);
    }

    /**
     * 是否为英文字母
     *
     * @param c 待验字符串
     * @return 是否为英文字母
     */
    public static boolean isEnChar(char c) {
        return isLowerCase(c) || isUpperCase(c);
    }

    /**
     * 是否为符号
     *
     * @param c 待验字符串
     * @return 是否为符号
     */
    public static boolean isSymbol(char c) {
        if (isCnSymbol(c)) return true;
        if (isEnSymbol(c)) return true;

        if (0x2010 <= c && c <= 0x2017) return true;
        if (0x2020 <= c && c <= 0x2027) return true;
        if (0x2B00 <= c && c <= 0x2BFF) return true;
        if (0xFF03 <= c && c <= 0xFF06) return true;
        if (0xFF08 <= c && c <= 0xFF0B) return true;
        if (c == 0xFF0D || c == 0xFF0F) return true;
        if (0xFF1C <= c && c <= 0xFF1E) return true;
        if (c == 0xFF20 || c == 0xFF65) return true;
        if (0xFF3B <= c && c <= 0xFF40) return true;
        if (0xFF5B <= c && c <= 0xFF60) return true;
        if (c == 0xFF62 || c == 0xFF63) return true;
        if (c == 0x0020 || c == 0x3000) return true;
        return false;
    }

    /**
     * 是否为中文符号
     *
     * @param c 待验字符串
     * @return 是否为中文符号
     */
    public static boolean isCnSymbol(char c) {
        if (0x3004 <= c && c <= 0x301C) return true;
        if (0x3020 <= c && c <= 0x303F) return true;
        return false;
    }

    /**
     * 是否为英文符号
     *
     * @param c 待验字符串
     * @return 是否为英文符号
     */
    public static boolean isEnSymbol(char c) {

        if (c == 0x40) return true;
        if (c == 0x2D || c == 0x2F) return true;
        if (0x23 <= c && c <= 0x26) return true;
        if (0x28 <= c && c <= 0x2B) return true;
        if (0x3C <= c && c <= 0x3E) return true;
        if (0x5B <= c && c <= 0x60) return true;
        if (0x7B <= c && c <= 0x7E) return true;

        return false;
    }

    /**
     * 是否为标点符号
     *
     * @param c 待验字符串
     * @return 是否为标点符号
     */
    public static boolean isPunctuation(char c) {
        if (isCjkPunc(c)) return true;
        if (isEnPunc(c)) return true;

        if (0x2018 <= c && c <= 0x201F) return true;
        if (c == 0xFF01 || c == 0xFF02) return true;
        if (c == 0xFF07 || c == 0xFF0C) return true;
        if (c == 0xFF1A || c == 0xFF1B) return true;
        if (c == 0xFF1F || c == 0xFF61) return true;
        if (c == 0xFF0E) return true;
        if (c == 0xFF65) return true;

        return false;
    }

    /**
     * 是否为英文标点符号
     *
     * @param c 待验字符串
     * @return 是否为英文标点符号
     */
    public static boolean isEnPunc(char c) {
        if (0x21 <= c && c <= 0x22) return true;
        if (c == 0x27 || c == 0x2C) return true;
        if (c == 0x2E || c == 0x3A) return true;
        if (c == 0x3B || c == 0x3F) return true;

        return false;
    }

    /**
     * 是否为中文标点符号
     *
     * @param c 待验字符串
     * @return 是否为中文标点符号
     */
    public static boolean isCjkPunc(char c) {
        if (0x3001 <= c && c <= 0x3003) return true;
        if (0x301D <= c && c <= 0x301F) return true;

        return false;
    }

    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public synchronized static boolean isChineseChar(char c) {
        try {
            return getCharLength(String.valueOf(c)) > 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public synchronized static int getCharLength(String c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return c.getBytes(StandardCharsets.UTF_8).length;
        } else {
            try {
                return c.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return 1;
            }
        }
    }
}
