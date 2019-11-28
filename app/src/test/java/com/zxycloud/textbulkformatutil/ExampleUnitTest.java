package com.zxycloud.textbulkformatutil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void indexTest() {
        String a = "12341234123412341234";
        System.out.println(a.indexOf("1234") + "1234".length());
        System.out.println(a.indexOf("1234", 6));
    }
}