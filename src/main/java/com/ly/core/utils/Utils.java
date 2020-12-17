package com.ly.core.utils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @Author: luoy
 * @Date: 2019/8/2 11:13.
 */
public class Utils {

    private static final String FORMAT_YY_MM_DD = "yyyy-MM-dd";

    private static final String FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";

    private static final String FORMAT_MONTH_TIME = "MM月dd日 HH点mm分ss秒";

    public final static String YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HMS_FORMAT = "HH:mm:ss";

    private static final String DTF_FORMAT_DATE= "yyyy-MM-dd";

    private static final String DTF_FORMAT_MONTH= "yyyy-MM";

    private static final String DTF_FORMAT_YYYYMMDDHHMMSS= "yyyyMMddHHmmss";



    public static Object[][] listToArray(List<Object[]> result) {
        if ((null == result) || (result.isEmpty())) {
            return null;
        }
        int size = result.size();
        Object[][] objects = new Object[size][];
        for (int i = 0; i < size; i++) {
            objects[i] =  result.get(i);
        }
        return objects;
    }

    public static String  getClasspath() {
        try {
            File path = new File(ResourceUtils.getURL("classpath:").getPath());
            return path.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 随机生成length 位数字
     * @param length
     * @return
     */
    public  static String getRandomInt(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += random.nextInt(10);
        }

        return val;
    }

    /**
     * 生成日期格式字符串 格式：yyyyMMddHHmmss
     * @return
     */
    public static String getTime (String pattern) {
        SimpleDateFormat timePattern = new SimpleDateFormat(pattern);
        return timePattern.format(new Date());
    }

    /**
     * yyyy-MM-dd HH:mm:ss格式时间转时间戳（ms）
     *
     * @return 日期格式的字符串
     */
    public static String dateToMs (String date) {
        SimpleDateFormat timePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStemp = null;
        try {
            timeStemp = timePattern.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(timeStemp.getTime());
    }

    /**
     * yyyy-MM-dd HH:mm:ss格式时间转时间戳（s）
     *
     * @return 日期格式的字符串
     */
    public static String dateTos (String date) {
        SimpleDateFormat timePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStemp = null;
        try {
            timeStemp = timePattern.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(timeStemp.getTime() /1000);
    }

    /**
     * 当前时间间隔
     * @param field
     *             时间单位 Calendar常量
     * @param amount
     *             间隔
     * @return
     */
    public static String getNowIntervalForSql(int field, int amount) {
        SimpleDateFormat timePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, amount);
        return timePattern.format(calendar.getTime());
    }

    public static String getToday_forSql() {
        SimpleDateFormat timePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return timePattern.format(new Date());
    }

    public static String getLocalDataTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        return time.toString();
    }

    public static LocalDateTime stringToLocalDateTime(String date) {
        return LocalDateTime.parse(date);
    }



    /**
     * 写入文件
     * @param path
     * @param result
     * @param flag
     *     true 追加，false覆盖
     */
    public static void write(String path, String result, Boolean flag) {
        File file = new File(path);
        FileWriter fw;
        try {
            fw = new FileWriter(file,flag);
            fw.write(result);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取path位置文件内容
     * @param path
     * @return
     */
    public static String read(String path, String encode) {
        String result = null;

        try (FileInputStream file = new FileInputStream(path);
             BufferedReader reader = new BufferedReader(new InputStreamReader(file, encode))) {
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                strber.append(line + "\n");
            }

            result = strber.toString();
        } catch (IOException e) {
            e.getStackTrace();
        }

        return result;
    }

    /**
     * 读取path位置文件内容
     * @param path
     * @return
     */
    public static String read(String path) {
        return read(path, "UTF-8");
    }

    public static boolean isExistBrace(String input) {
        String regex = "\\{([^}]*)\\}";
        Pattern pattern = compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * 是否为json
     * @param json
     * @return
     */
    public final static boolean isJSONValid(String json) {
        try {
            JSONObject.parseObject(json);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String getPath() {
        return Utils.class.getResource("/").getPath();
    }

    /** 获取入参的tpye */
    public final static Class<?>[] getParamsType(Object[] args) {
        Class<?>[] paramsType = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramsType[i] = args[i].getClass();
        }
        return paramsType;
    }

}
