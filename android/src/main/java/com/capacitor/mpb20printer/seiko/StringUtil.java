package com.capacitor.mpb20printer.seiko;


// Utility of string class.
public class StringUtil {

    // Check if it is empty.
    public static boolean isEmpty(CharSequence chsq) {
        boolean result;
        if (chsq == null) {
            result = true;

        } else if (chsq.length() == 0) {
            result = true;

        } else {
            result = false;
        }

        return result;
    }

    // Check if it is empty.
    public static boolean isEmpty(String str) {
        boolean result;
        if (str == null) {
            result = true;

        } else if (str.length() == 0) {
            result = true;

        } else {
            result = false;
        }

        return result;
    }

    // String -> int
    public static int getInt(String str) {
        return getInt(str, 0);
    }

    // String -> int
    public static int getInt(String str, int defaultVal) {
        int value;
        try {
            value = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            value = defaultVal;
        }

        return value;
    }

}
