package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description: csv文件解析
 * @Date:Created in 9:36 AM 12/24/2018
 */
public class CsvUtil {


    public static List<String> readCsvFile(MultipartFile file, ReadFileConditon readFileConditon) throws BusinessException, IOException {

        BufferedReader bReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));

        //1、对于第一行的判断
        if (checkCsvLineData(bReader.readLine(), readFileConditon.getColumnNum())) {
            throw  new BusinessException(ErrorEnum.IMPORT_DATA_CLUMMUN_ERR);
        }

        //2、不需要读取的行
        if (readFileConditon.getIgonreRowNum() > 0) {
            for (int i = 1; i < readFileConditon.getIgonreRowNum(); i++) {
                bReader.readLine();
            }
        }

        //3、 提取文件中的数据
        List<String> stringList = readCsvData(bReader,readFileConditon.getColumnNum());
        return  stringList;
    }


    /**
     * 判断列数是否合法
     * @param line
     * @param columnNum
     * @return
     */
    public static boolean checkCsvLineData(String line, int columnNum) {
        if (("" == line.trim ()) || (null == line)
                || (line.split (",").length < columnNum)) {
            return true;
        }

        return false;
    }


    public static List<String> readCsvData(BufferedReader bReader, int columnNum) throws IOException {
        List<String> stringList = new ArrayList<>();
        String line = "";
        while ((line = bReader.readLine()) != null) {

            String[] pills = line.split(",",columnNum);
            stringList.add(line);
        }
        return stringList;
    }
}
