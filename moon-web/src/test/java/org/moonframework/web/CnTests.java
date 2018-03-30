package org.moonframework.web;

/**
 * @author quzile
 * @version 1.0
 * @since 2017/11/11
 */
public class CnTests {

    public static void main(String[] args) {
        String str = "王大锤+wefw";
        char[] array = str.toCharArray();
        int count = 0;
        for (char c : array) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]+")) {
                count++;
            }
        }
        System.out.println(count);
    }
}
