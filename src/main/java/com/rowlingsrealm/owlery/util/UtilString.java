package com.rowlingsrealm.owlery.util;

import com.google.common.base.Joiner;
import org.apache.commons.lang.ArrayUtils;

public class UtilString {

    public static String join(String[] args, int ignored, String separator) {

        if (ignored > -1) args = (String[]) ArrayUtils.remove(args, ignored);

        String combined = Joiner.on(separator).join(args);

        return combined.substring(0, combined.length());
    }
}
