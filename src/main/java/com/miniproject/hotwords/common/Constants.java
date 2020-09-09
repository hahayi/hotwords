package com.miniproject.hotwords.common;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Constants {
    public static final String DEFAULT_CRON = "12";//默认12小时执行一次
    public static final int DEFAULT_HOTWORDS_NUMBER = 10;//默认返回十个
    public static final String CRON_PARAM_KEY = "cron";//参数表里是cron
    public static final String HOTWORDS_NUMBER_KEY = "hot_words_number";
    private Constants(){}

    public static String[] values() {
        Field[] fields = Constants.class.getFields();
        String[] s = new String[fields.length];
        for(int i=0,n=fields.length; i<n; i++) {
            try {
                Field f = fields[i];
                s[i] = (String) f.get(null);
            } catch (Exception ex) {
                Logger.getLogger(Constants.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return s;
    }

    public static Set<String> asSet() {
        return new HashSet<String>(Arrays.asList(values()));
    }

}
