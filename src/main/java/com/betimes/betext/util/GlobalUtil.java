package com.betimes.betext.util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GlobalUtil {
    public static Date getCurrentDateTime() {
        return new Date();
    }

    public static String getActiveStatus() {
        return "A";
    }

    public static String getInActiveStatus() {
        return "I";
    }

    public static String getCreateBy() {
        return "SYSTEM";
    }
}