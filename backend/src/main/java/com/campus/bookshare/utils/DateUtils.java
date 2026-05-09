package com.campus.bookshare.utils;

import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }

    public static boolean isOverdue(Date expectedReturnTime, Date actualReturnTime) {
        if (expectedReturnTime == null || actualReturnTime == null) {
            return false;
        }
        return actualReturnTime.after(expectedReturnTime);
    }
}
