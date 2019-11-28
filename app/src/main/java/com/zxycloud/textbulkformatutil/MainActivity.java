package com.zxycloud.textbulkformatutil;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.zxycloud.textbulkformat.TextBulkFormatUtils;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SparseArray<TextView> textViewArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewArray.append(R.id.bulk_format_1, (TextView) findViewById(R.id.bulk_format_1));
        textViewArray.append(R.id.bulk_format_2, (TextView) findViewById(R.id.bulk_format_2));
        textViewArray.append(R.id.bulk_format_3, (TextView) findViewById(R.id.bulk_format_3));
        textViewArray.append(R.id.bulk_format_4, (TextView) findViewById(R.id.bulk_format_4));
        textViewArray.append(R.id.bulk_format_5, (TextView) findViewById(R.id.bulk_format_5));
        textViewArray.append(R.id.bulk_format_6, (TextView) findViewById(R.id.bulk_format_6));

        String[] strings = {"我", "我的", "我的详", "我的详情", "我的详情页"};
//        String[] strings = {"aaaaaaaaaaa", "计算出当前绘制出来的字符串有多宽", "使用ToolBar的setTitle方法设置标题时", "我的手机号码：12345678901", "i'm joker", "modify Microsoft Excel"};
        Map<String, SpannableString> map = TextBulkFormatUtils.formatStringLength(TextBulkFormatUtils.LENGTH_FORMAT_BOTH_ENDS, false, strings);



        textViewArray.get(R.id.bulk_format_1).setText(map.get(strings[0]));
        textViewArray.get(R.id.bulk_format_2).setText(map.get(strings[1]));
        textViewArray.get(R.id.bulk_format_3).setText(map.get(strings[2]));
        textViewArray.get(R.id.bulk_format_4).setText(map.get(strings[3]));
        textViewArray.get(R.id.bulk_format_5).setText(map.get(strings[4]));
//        textViewArray.get(R.id.bulk_format_6).setText(map.get(strings[5]));

//        textViewArray.get(R.id.bulk_format_6).setText(AlignedTextUtils.formatText("我的天啊"));
    }

    private String getName() {
        return getClass().getSimpleName();
    }
}

/**
 * 不同文字数目2端对齐工具类 (支持2-6个数字)
 *
 * @author yuhao
 * @time 2016年6月28日
 */
class AlignedTextUtils {

    private static int n = 0;// 原Str拥有的字符个数
    private static SpannableString spannableString;
    private static double multiple = 0;// 放大倍数

    /**
     * 对显示的字符串进行格式化 比如输入：出生年月 输出结果：出正生正年正月
     */
    public static String formatStr(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        n = str.length();
        if (n >= 6) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = n - 1; i > 0; i--) {
            sb.insert(i, "正");
        }
        return sb.toString();
    }

    /**
     * 对显示字符串进行格式化 比如输入：安正卓正机正器正人 输出结果：安 卓 机 器 人
     *
     * @param str
     * @return
     */
    public static SpannableString formatText(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        str = formatStr(str);
        if (str.length() <= 6) {
            return null;
        }
        spannableString = new SpannableString(str);
        switch (n) {
            case 2:
                multiple = 4;
                break;
            case 3:
                multiple = 1.5;
                break;
            case 4:
                multiple = 0.66666666666666666666666666666666667;
                break;
            case 5:
                multiple = 0.25;
                break;
            default:
                break;
        }
        for (int i = 1; i < str.length(); i = i + 2) {
            spannableString.setSpan(new RelativeSizeSpan((float) multiple), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), i, i + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}