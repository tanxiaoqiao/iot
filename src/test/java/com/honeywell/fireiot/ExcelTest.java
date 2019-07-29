package com.honeywell.fireiot;

import com.honeywell.fireiot.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 5:22 PM 5/22/2019
 */
public class ExcelTest {
    @Test
    public void test() {
        File file = new File("C:\\test\\删除设备.xlsx");
        System.out.println("cunzai="+file.exists());
        try {
            Workbook workbook = new XSSFWorkbook(file);
            System.out.println(workbook.getSheetAt(0).getLastRowNum());
            Sheet sheet = workbook.getSheetAt(0);

            List<String> list = IntStream.rangeClosed(1, sheet.getLastRowNum())
                    .mapToObj(sheet::getRow)
                    .map(cells -> ExcelUtil.readRowString(cells, 1))
                    .collect(Collectors.toList());
            list.forEach(s -> {

            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test1() {
        int a = 0xff00;
        System.out.println(a);
    }

}
