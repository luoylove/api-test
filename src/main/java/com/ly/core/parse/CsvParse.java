package com.ly.core.parse;

import au.com.bytecode.opencsv.CSVReader;
import com.ly.core.utils.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoy on 2019/6/17.
 */
public class CsvParse {
    public static Object[][] getParseFromCsv(String filePath) {
        try {
            return getParseFromCsv(filePath, "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(
                    "No parameter values available for method: "+ e.getMessage() + "请检查csv文件是否存在，路径："
                            + filePath + "或格式是否正确");
        }
    }

    public static Object[][] getParseFromCsv(String filePath, String encode) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis, encode);
             CSVReader reader = new CSVReader(isr)){

            List<Object[]> result = new ArrayList<>();
            Object[] nextLine ;

            while ((nextLine = reader.readNext()) != null) {
                result.add(nextLine);
            }

            if (null != reader) {
                reader.close();
            }
            Object[][] objects = Utils.listToArray(result);
            if ((null == objects) || (objects.length == 0)) {
                throw new RuntimeException(
                        "No parameter values available for method: " + "请检查csv文件是否存在，路径：" + filePath + "或格式是否正确");
            }
            return objects;
        }
    }
}
