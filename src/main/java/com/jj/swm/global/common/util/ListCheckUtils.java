package com.jj.swm.global.common.util;

import java.util.List;

public class ListCheckUtils {

    public static boolean isListPresent(List<?> someList){return someList != null && !someList.isEmpty();}

    public static boolean isListNotNull(List<?> someList){return someList != null;}

    public static boolean isListNotEmpty(List<?> someList){return !someList.isEmpty();}
}
