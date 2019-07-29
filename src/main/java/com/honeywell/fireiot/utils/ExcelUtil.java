package com.honeywell.fireiot.utils;


import com.honeywell.fireiot.constant.ErrorEnum;
import com.honeywell.fireiot.constant.FileImportAction;
import com.honeywell.fireiot.exception.BusinessException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 4:37 PM 11/29/2018
 */
public class ExcelUtil {

    /**
     *
     * @param workbook
     * @param sheet 当前分页
     * @param titleList 题目
     * @param rowIndex 当前所在行
     * @return 下一行所在标志
     */
    public static int writeTitleToExcel(Workbook workbook, Sheet sheet, List<String> titleList, int rowIndex) {
        Row row = sheet.createRow(rowIndex);//当前行
        int listNum = titleList.size();
        int i = 0;
        //设置列宽
        for(i = 0; i < listNum; i++)
        {
            sheet.setColumnWidth(i, 12*256);
        }

        //设置居中加粗
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFont(font);

        for(i = 0; i < listNum; i++){
            Cell cell = row.createCell(i);
            cell.setCellValue(titleList.get(i));
            cell.setCellStyle(cellStyle);
        }
        rowIndex++;
        return  rowIndex;
    }


    public static int writeRowsToExcel(Workbook workbook, Sheet sheet, List<List<Object>> rows, int rowIndex) {

        for(List<Object> rowData: rows){
            Row row = sheet.createRow(rowIndex);
            int colunmIndex = 0;
            for(Object cellData: rowData){
                Cell cell = row.createCell(colunmIndex);
                if(cellData != null){
                    cell.setCellValue(cellData.toString());//默认转化为string 类型
                }else{
                    cell.setCellValue("");
                }
                colunmIndex++;
            }
            rowIndex++;
        }
        return rowIndex;
    }



    public static int exportDataToExcel(ExcelData data, OutputStream out) {
        Workbook workbook = getWorkbook(data.getFileName());
        if(null == workbook ) {
            return FileImportAction.ERR_FILE;
        }

        int rowIndex = 0;
        try {
            Sheet sheet = workbook.createSheet(data.getSheetName());
            rowIndex = writeTitleToExcel(workbook, sheet,data.getTitle(), rowIndex );
            rowIndex = writeRowsToExcel(workbook, sheet, data.getRows(), rowIndex);
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                workbook.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return  rowIndex;
    }


    /**
     * 生excel表格
     * @param data
     * @param fileDir
     * @return
     * @throws Exception
     */
    public static int generateExcel(ExcelData data, String fileDir) throws Exception {

        File file = new File(fileDir);
        if(!file.exists()){
            file.mkdir();
        }
        FileOutputStream out = new FileOutputStream(file + "/"+data.getFileName());
        return exportDataToExcel(data, out);
    }


    /**
     * 导出excel
     * @param response
     * @param data
     * @return
     * @throws Exception
     */
    public static int exportExcel(HttpServletResponse response, ExcelData data) throws Exception {

        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(data.getFileName(), "utf-8"));

        return exportDataToExcel(data, response.getOutputStream());
    }

    /**
     * 根据不同的后缀名获取workbook
     * @param fileName
     * @return
     */
    public static  Workbook getWorkbook( String fileName)  {
        Workbook workbook = null;
        if(fileName.endsWith(FileImportAction.SUFFIX_XLS)){
            workbook = new HSSFWorkbook();
        }else if(fileName.endsWith(FileImportAction.SUFFIX_XLSX)){
            workbook = new XSSFWorkbook();
        }else{
            return  null;
        }
        return workbook;
    }

    /**********************************************excel文件读取***************************************************/
    /**
     * 检查当前行是否合法（一行非空的列数必须大于等于规定的列数（columnNum））
     * @param sheet 当前分页
     * @param rowIndex 行id
     * @param columnNum 指定需要的列数
     * @return
     */
    public static  boolean checkExcelLineData(Sheet sheet,int rowIndex, int columnNum){
        Row row = sheet.getRow(rowIndex);
        if(row == null ){
            return  true;
        }
        int count = 0;
        for(Cell cell:row){ //非空单元个数
            count++;
        }
        if(count < columnNum ){
            return  true;
        }
        return  false;
    }


    public static boolean checkSheet(Workbook workbook, int sheetIndex) {
        if(null == workbook ||  null == workbook.getSheetAt(sheetIndex) ){
            return  true;
        }

        return  false;
    }


    /**
     * 读取正文
     * @param sheet
     * @param ignoreRowIndex
     * @param columnNum
     * @return
     */
    public static List<String> readExcelData(Sheet sheet, int ignoreRowIndex, int columnNum) {
        HashSet<String> hashSet = new HashSet<>();
        int rowNum = sheet.getLastRowNum() + 1;
        List<String> stringList = new ArrayList<>();
        for(int i = ignoreRowIndex ; i < rowNum; i++ ) {
            Row row = sheet.getRow(i);
            //防止读到空行
            if( null == row   || (row.getFirstCellNum() < 0)){
                continue;
            }
            //读取每一行
            String str = readRowString(row, columnNum);
            stringList.add(str);
        }

        return  stringList;
    }

    /**
     * 获取当前行，不同项以“,”隔开
     * @param row
     * @param colunmNum
     * @return
     */
    public static  String  readRowString(Row row, int colunmNum){
        String str = new String();
        for(int j = 0; j < colunmNum; j++){
            //如果为空则退出
            Cell cell = row.getCell(j);
            if(isCellEmpty(cell)){
                str += " ";
                if( j < colunmNum - 1 ){
                    str+=",";
                }
                continue;
            }
           SetCellTypeToString(cell);
            //去掉换行字符
            str += cell.getStringCellValue().replace("\n", "");
            if( j < colunmNum - 1 ){
                str+=",";
            }
        }

        return str;
    }

    /**
     * 以数组形式获取
     * @param row
     * @param colunmNum
     * @return
     */
    public static  List<String>  readRowList(Row row, int colunmNum){
        List<String> objectList = new ArrayList<>();
        for(int j = 0; j < colunmNum; j++){
            Cell cell = row.getCell(j);
            //如果为空则退出
            if( isCellEmpty(cell) ){
              objectList.add("");
                continue;
            }
            SetCellTypeToString(cell);
            objectList.add(cell.getStringCellValue());
        }

        return objectList;
    }

    /**
     * 判断当前元素是否为空
     * @param cell
     * @return
     */
    public static boolean isCellEmpty(Cell cell){
        if( (null == cell) || (CellType.BLANK == cell.getCellType())  ){
            return  true;
        }
        return  false;
    }

    /**
     * 将单元转化为String 类型
     * @param cell
     */
    public static  void SetCellTypeToString(Cell cell){
        if(!cell.getCellType().equals(CellType.STRING)){
            //将excel的字符转化成string
            cell.setCellType(CellType.STRING);
        }
    }


    public  static Workbook getWorkbook(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        String name = fileName.substring(0, fileName.lastIndexOf("."));

        //判断版本
        try {
            if (fileName.endsWith(FileImportAction.SUFFIX_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith(FileImportAction.SUFFIX_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }else{
                workbook = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return  workbook;
    }

    public  static  List<String> readExcelFromWorkbook(Workbook workbook, ReadFileConditon readFileConditon) throws BusinessException {
        //1、文件分页是否存在
        if(checkSheet(workbook, readFileConditon.getSheetIndex())){
            throw  new BusinessException(ErrorEnum.PARAMS_ERROR) ;
        }

        //2、表格题目的判断（检查某一行（默认第一行）的列数是否 >= 指定读取的列数）
        Sheet sheet = workbook.getSheetAt(readFileConditon.getSheetIndex());
        if(checkExcelLineData(sheet,readFileConditon.getTitleRowIndex(),readFileConditon.getColumnNum())){
            throw  new BusinessException(ErrorEnum.IMPORT_DATA_CLUMMUN_ERR);
        }

        //3、将文件中所有数据取出
        List<String> stringList = readExcelData(sheet, readFileConditon.getIgonreRowNum(), readFileConditon.getColumnNum());

        if(workbook != null ){
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  stringList;
    }

    public  static  int checkExcel(Workbook workbook, ReadFileConditon readFileConditon){

        int result = FileImportAction.ERR_SUCCESS;
        //1、文件分页是否存在
        if(checkSheet(workbook, readFileConditon.getSheetIndex())){
            return FileImportAction.ERR_FILE;
        }

        //2、表格题目的判断
        Sheet sheet = workbook.getSheetAt(readFileConditon.getSheetIndex());
        if(checkExcelLineData(sheet,readFileConditon.getTitleRowIndex(),readFileConditon.getColumnNum())){
           result = FileImportAction.ERR_CLUMN;
        }

        return  result;
    }

    public  static List<String> readExcel(MultipartFile file, ReadFileConditon readFileConditon) throws BusinessException {
        Workbook workbook = getWorkbook(file);
        List<String> stringList = readExcelFromWorkbook(workbook, readFileConditon);
        return  stringList;

    }


}
