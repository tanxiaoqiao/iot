package com.honeywell.fireiot.constant;

/**
 * @Author: zhenzhong.wang
 * @Date: 1/7/2019 10:25 PM
 */
public enum ErrorEnum {
    LACKPARAMETER(50001, "参数缺失"),
    DATASTATUSERROR(50002, "数据已失效，刷新重试"),
    PARAS_ERROR(50003,"参数格式错误"),
    UPLOAD_ERROR(50004,"上传失败"),
    SERVER_ERROR(500, "服务器异常"),
    LOGIN_FAIL(40000, "帐号或者密码错误"),
    NO_LOGIN(40001, "用户未登录"),
    ACCOUNT_EXIST(40002, "账户已存在"),
    PARAMS_ERROR(40003, "参数错误"),
    PASSWORD_ERROR(40004, "用户名或密码错误"),
    USER_LOCKED(40005, "用户被禁用"),
    MENU_ERROR(40006, "菜单未初始化"),
    ACCOUNT_NOT_EXIST(400007, "用户不存在"),
    NOT_LOGIN(400008, "没有登录"),
    PERMISSION_NOT_EMPTY(400009, "无权限访问"),
    BUILDING_NOT_EXIST(400010, "建筑不存在"),
    IMPORT_DATA_FAIL(400011, "导入数据失败"),
    AREA_EXIST(400012, "辖区已存在"),
    ROLE_EXIST(400013, "角色名称已存在"),
    ADMIN_CAN_NOT_DELETE(400014, "该角色不可删除"),
    ROLE_NOT_EXIST(400015, "角色不存在"),
    AREA_NOT_EXIST(400016, "辖区不存在"),
    PASSWORD_NOT_CHANGED(400017, "新密码和原密码重复"),
    URL_NOT_EXIST(400018, "路径不存在"),
    CATEGORY_EXIST(400020, "预案类别已存在"),
    CATEGORY_NOT_EXIST(400021, "预案类别不存在"),
    FILETITLE_EXIST(400022, "文件标题已存在"),
    FILE_NOT_EXIST(400023, "文件不存在"),
    PROCESS_END(400024, "流程已经结束"),
    TASK_NULL(400028, "任务不存在"),
    PERSON_NULL(400025, "员工不存在"),
    SECTION_NULL(400026, "楼层不存在"),
    NOT_FOUND(400027, "记录未找到"),
    NO_FIRE(400029, "没有报警记录"),
    TEAM_NULL(400030, "没有该组"),
    IMPORT_DATA_REPEAT(400031, "导入数据存在重复"),
    IMPORT_DATA_CLUMMUN_ERR(400032, "导入数据列失败"),
    PASSWORD_SAME(400033, "新旧密码重复"),
    USERNAME_EXIST(400034,"用户名已存在"),
    DEVICE_EXIST(400035, "设备已存在"),
    DEVICE_NOT_EXIST(400036,"设备不存在"),
    NO_PERMISSION(400037,"没有权限"),
    PARAMETER_ERROR(400038,"请求参数异常"),
    UPDATE_ERROR(400039, "更新错误"),
    FILE_IS_EMPTY(400042,"文件为空"),
    FILE_FORMAT_ERROR(400043,"文件格式错误"),
    TOEKN_INVALID(400040, "token无效"),
    MISS_REQUEST_PARAMTER(40041, "缺少请求参数"),
    PARSE_GATEWAY_ERROR(401001,"解析网关失败"),
    USERNAME_PASSWORD_NOT_NULL(400042, "用户名或密码不能为空"),
    DATE_OUT_RANGE(400043, "日期超出范围"),
    DATE_NOT_NULL(400044, "统计日期不能为空"),
    PASSWORD_UPDATE_ERROR(400045, "密码更新失败"),
    USER_DELETE_FAIL(400046, "用户删除失败"),
    USER_NOT_APPLICATION(400047, "用户无权限访问当前应用"),
    EID_INVALID(400048, "eid无效"),
    EID_EXIST(400049, "eid已存在"),
    OLDPASSWORD_ERROR(400050, "旧密码错误"),
    OLDPASSWORDANDNEW_ERROR(400051, "新密码和旧密码一致"),

    NO_WORKTEAM(50014, "工作组不存在"),


    //contract
    SOCKET_FAIL(30024, "socket连接失败"),
    CONTRACT_NOT_EXIST(30030, "合同不存在"),
    CONTRACTTYPE_EXIST(30031, "合同类别已存在"),
    CONTRACTTYPE_NOT_EXIST(30032, "合同类别不存在"),
    EXCHANGERATE_NOT_EXIST(30051, "汇率不存在"),
    // polling

    NO_FILE(60002, "没有文件"),
    NO_USER(60003, "用户不存在"),
    PROCESS_NAME_NOT_NULL(60004, "流程模板不存在"),
    //    MISS_REQUEST_PARAMTER(60005,"缺少请求参数"),
    DATA_NOT_EXIST(60007, "数据不存在"),
    NO_POLLING(60006, "巡检模板不存在"),
    NO_HOLIDAY(60007, "节假日不存在"),
    SPOT_NOT_EXIST(60008, "点位不存在"),
    MAINTENANXE_PARAMS_ERROR(60009,"结束月份应小于开始月份"),
    PARENT_FORM_NOT_FOUND(60010, "未找到父form"),
    ELEMENT_FORM_NOT_NULL(60011, "表单元素为空"),
    ELEMENT_FORM_NOT_FOUND(60012, "表单元素未发现"),

    FILE_PATTERN_ERROR(61001,"不支持该文件格式"),
    FILE_PARSE_ERROR(61002,"文件解析发生错误"),

    WORK_PERSON_NULL(60013, "派单人员不能为空"),
    POLLING_EXIST(600014, "巡检名称已存在"),
    PARAM_IS_NULL(600015, "必要请求参数点位ID为空"),
    NO_SPOT(600016, "点位为空"),

    NO_POTRAL(600017, "巡检记录为空"),
    REPEAT_MODEL(600018, "模板已存在"),
    NO_WORKORDER(600019, "工单不存在"),
    EXIST_HOLIDAY(60020, "节假日存在"),
    NO_EMP(60021, "员工不存在"),
    APPLIED(600022, "工单已审批"),

    NO_TASK(50014, "任务不存在"),
    INSPECT_CONTENT_NULL(50015,"巡检内容不存在"),

    DEVICE_TYPE_NOT_EXIST(20034, "设备类型不存在"),



    SAVE_LOCATION_MAP_FAILED(100010,"文件保存失败"),
    LOCATION_IS_NOT_EXIST(100011,"空间位置不存在"),

    CATALOG_NOT_EXIST(700001,"分类不存在"),

    //Material
    WAREHOUSE_NOT_EXIST(80001,"仓库不存在"),
    MATERIAL_CODE_IS_EXIST(80002,"物资编号已存在"),
    MATERIAL_NOT_EXIST(80003,"物资不存在"),
    MATERIAL_STOCK_IS_EXIST(80004,"该物资还有库存"),
    LOW_STOCK(80003,"库存不足"),
    STOCK_FAIL(80004,"库存操作失败"),

    LOG_NOT_EXIST(90001,"日志不存"),

    EVENT_NOTEXIST(90010,"事件不存在")
    ;


    private int code;

    private String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
