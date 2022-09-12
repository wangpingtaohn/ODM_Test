package com.quectel.agingtest;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        HashSet<String> s = new HashSet<>();
        s.add("1");
        s.add("2");
        System.out.println(Arrays.toString(s.toArray()));
        String s2 = s.iterator().next();
        s.remove(s2);
        System.out.println("s="+s2);
        System.out.println(Arrays.toString(s.toArray()));
    }


}