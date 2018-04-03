package com.martoph.mail.util;

import org.apache.commons.lang.ArrayUtils;

public class UtilString {

    public static String join(String[] args, int ignored, String separator) {

        if (ignored > -1)
            args = (String[]) ArrayUtils.remove(args, ignored);

        return String.join(separator, args);
    }
}
