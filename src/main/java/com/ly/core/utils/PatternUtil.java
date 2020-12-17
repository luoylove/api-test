package com.ly.core.utils;

import com.google.common.collect.Maps;
import com.ly.core.exception.BizException;
import com.ly.core.matches.ValidateUtils;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @Author: luoy
 * @Date: 2019/9/19 14:48.
 */
public class PatternUtil {
    private static Pattern TAG_PATTERN = Pattern.compile("(?<=\\$\\{)(.+?)(?=\\})");
    private static Pattern METHOD_AGES = compile(".+?(\\(.+?\\))");

    /**
     * 返回匹配${}中的字符串
     * @param s
     * @return
     */
    public static List<String> getPatterns(String s) {
        List<String> patterns = Lists.newArrayList();
        Matcher m = TAG_PATTERN.matcher(s);
        while(m.find()){
            patterns.add(m.group());
        }
        return patterns;
    }

    public static String createKey(String key) {
        return "${" + key + "}";
    }

    /**
     * ${}通配符
     * @param arg
     * @return
     */
    public static String replace(String arg) {
        List<String> patterns = PatternUtil.getPatterns(arg);
        if (patterns == null || patterns.size() == 0) {
            return arg;
        }
        for(String str : patterns) {
            String[] data = str.split(",");
            if (data.length > 2) {
                throw new BizException("%s包含多个逗号,无法确认默认值", arg);
            }
            ContextDataStorage instance = ContextDataStorage.getInstance();
            String v = (String) instance.getAttr(data[0].trim());
            if (data.length < 2) {
                AssertUtils.notNull(v, PatternUtil.createKey(data[0]) + ":缓存内未找到值");
                arg = arg.replace(createKey(data[0].trim()), v);
            }

            if (data.length == 2) {
                arg = v == null ? arg.replace(createKey(str), data[1].trim()) : arg.replace(createKey(str), v);
            }
        }
        return arg;
    }

    /**
     * 反射方法名与参数提取
     * @param msg
     * @return
     */
    public static Map<String, String> extractHookByRegular(String msg){
        Map<String, String> rex = Maps.newHashMap();
        if(msg.indexOf("(") <= 0) {
            rex.put(ValidateUtils.METHOD_NAME_KEY, msg.trim());
            return rex;
        } else {
            String args = "";

            Matcher m = METHOD_AGES.matcher(msg);
            while(m.find()){
                args = m.group(1);
            }

            String methodName = msg.substring(0, msg.indexOf(args));

            rex.put(ValidateUtils.METHOD_ARGS_KEY, args.substring(1, args.length() - 1));
            rex.put(ValidateUtils.METHOD_NAME_KEY, methodName);
            return rex;
        }
    }
}
