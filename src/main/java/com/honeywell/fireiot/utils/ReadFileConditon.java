package com.honeywell.fireiot.utils;

import com.honeywell.fireiot.constant.FileImportAction;
import lombok.Data;

/**
 * @Author: Kayla, Ye
 * @Description:读取文件的所需要的条件
 * @Date:Created in 3:26 PM 12/21/2018
 */
@Data
public class ReadFileConditon {

    private int igonreRowNum = 1;//可以跳过的行数

    private int columnNum = 1; //规定要读取的列数

    private int sheetIndex = 0; //分页id，默认为第一页

    private  String fileName;//预留可用生成的文件的名字

    private  int  operateType ;//可以根据实际情况进行扩展操作

    private  int titleRowIndex = 0;//题目所在行

    private int  tableType = 0;//数据类型
    public  ReadFileConditon(){

    }

    public  ReadFileConditon(int igonreRowNum, int columnNum, int sheetIndex, String fileName, int operateType,int titleRowIndex, int tableType){
        this.igonreRowNum = igonreRowNum;
        this.columnNum = columnNum;
        this.sheetIndex = sheetIndex;
        this.fileName = fileName;
        this.operateType = operateType;
        this.titleRowIndex = titleRowIndex;
        this.tableType = tableType;
    }

    public  static ReadFileConditon getFireDevice(){
        ReadFileConditon readFileConditon = new ReadFileConditon();
        readFileConditon.setOperateType(FileImportAction.GENERATE_JSON);
        readFileConditon.setColumnNum(FileImportAction.FIREDEVICE_CLUMNNUM);
        readFileConditon.setSheetIndex(FileImportAction.FIREDEVICE_SHEETINDEX);
        readFileConditon.setTableType(FileImportAction.FIREDEVICE_SINGLETON_DATA);
        return  readFileConditon;
    }

    public ReadFileConditon(int igonreRowNum, int columnNum) {
        this.igonreRowNum = igonreRowNum;
        this.columnNum = columnNum;
    }
}
