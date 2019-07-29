package com.honeywell.fireiot.fire;

import com.alibaba.fastjson.JSON;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.FireDevice;
import com.honeywell.fireiot.entity.Location;
import com.honeywell.fireiot.fire.bo.*;
import com.honeywell.fireiot.fire.constant.ModbusDataType;
import com.honeywell.fireiot.fire.dto.FireEventDto;
import com.honeywell.fireiot.fire.entity.FireEvent;
import com.honeywell.fireiot.fire.entity.MTGateway;
import com.honeywell.fireiot.fire.entity.MTPoint;
import com.honeywell.fireiot.fire.service.FireEventService;
import com.honeywell.fireiot.fire.service.MTGatewayService;
import com.honeywell.fireiot.fire.service.MTPointService;
import com.honeywell.fireiot.fire.util.ModbusUtil;
import com.honeywell.fireiot.integrationService.WebSocketHandle;
import com.honeywell.fireiot.integrationService.rabbitmq.RabbitmqProps;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.FireDeviceService;
import com.honeywell.fireiot.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author ： YingZhang
 * @Description:
 * @Date : Create in 1:37 PM 9/26/2018
 */
@Component
@Slf4j
public class MTProtocolParse {

    private static final String GATEWAY_PROPS = "网络离线";

    private static final String EVENT_STATUS_ADD = "Add";

    private static final String EVENT_STATUS_DEL = "Del";

    @Autowired
    MTGatewayService mtGatewayService;

    @Autowired
    MTPointService mtPointService;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    RabbitmqProps rabbitmqProps;

    @Autowired
    BusinessDeviceService businessDeviceService;

    @Autowired
    FireDeviceService fireDeviceService;

    @Autowired
    LocationService locationService;

    @Autowired
    FireEventService fireEventService;

    @Autowired
    WebSocketHandle webSocket;

    @Value("${modbus.open}")
    Boolean modbusOpen;

    ExecutorService threadPool = null;

    ExecutorService threadPoolToHandleEvent = new ThreadPoolExecutor(5, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024),  new ThreadPoolExecutor.AbortPolicy());;

    // 网络状态缓存
    Map<Long,Boolean> gatewayStatus = new HashMap<>();

    public void initFireSystem() throws Exception{
        log.info("init FireSystem,modbusopen =" + modbusOpen);
        if(!modbusOpen){
            threadPool = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),  new ThreadPoolExecutor.AbortPolicy());
            return ;
        }
        try {
            List<MTGateway> mtGateways = mtGatewayService.findAll();
            threadPool = new ThreadPoolExecutor(mtGateways.size(), mtGateways.size(),
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),  new ThreadPoolExecutor.AbortPolicy());
            for (MTGateway mtGateway : mtGateways) {
                log.info("gateway:"+mtGateway);
                // 获取网关设备
                FireDevice fireDevice = fireDeviceService.findFirstByDeviceId("Gateway"+mtGateway.getId());
                Long gatewayDeviceId = fireDevice.getId();

                threadPool.execute(() -> {
                    // 将设备 根据网关先分类
                    List<MTPoint> mtPoints = mtPointService.findByGatewayId(mtGateway.getId());
                    // 转换成modbusPoint
                    List<ModbusPoint> points = convert(mtPoints);
                    // 将point转换成小分区
                    List<PTParse> ptParseList = parseProtocol(points,mtGateway.getLimit());
                    log.info("网关"+mtGateway.getId()+"数量="+ptParseList.size());
                    // 启动网关
                    ModbusMaster modbusMaster = new ModbusMaster(mtGateway.getIp(), mtGateway.getPort());
                    modbusMaster.Connect();
                    // 检查网络问题并上报
                    if(modbusMaster.isConnected()){
                        // 网关初始化，状态为true
                        checkGateway(gatewayDeviceId,false);
                        log.info(" 网关启动成功：" + mtGateway.getId());
                    }else{
//                         启动失败推送消息
//                        WebSocketMessage websocketMessage = new WebSocketMessage();
//                        websocketMessage.setMessageType(1);
//                        WebMessage webMessage = new WebMessage();
//                        BeanUtils.copyProperties(webMessage,mtGateway);
//                        webMessage.setEventType("网络异常");
//                        webMessage.setGatewayId(mtGateway.getId());
//                        webMessage.setTimeStamp(System.currentTimeMillis());
//                        websocketMessage.setData(webMessage);
//                        webSocket.sendToAll(JSON.toJSONString(websocketMessage));
                        // 网关初始化，状态为false
                        checkGateway(gatewayDeviceId,true);
                        log.error(" 网关启动失败，网关Id = " + mtGateway.getId());
                    }

                    // 就每个小分区开始 请求数据
                    while (true) {
                        Long startTime = System.currentTimeMillis();
                        List<MTResponsePoint> mtResponsePoints = parseModbusTcp(modbusMaster, ptParseList, mtGateway.getPointSleepTime(), gatewayDeviceId);
                        if (mtResponsePoints == null) {
                            log.info("网关读取失败：" + mtGateway.getId());
                        } else {
//                            log.info("网关读取成功：" + mtGateway.getId());
                            handlerEvent(mtResponsePoints);
                        }
                        // 每次整个网关需要休眠时间
                        try {
                            Thread.sleep(mtGateway.getIntervalSleepTime());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Long endTime = System.currentTimeMillis();
                        Long consumeTime =endTime-startTime;
                        //消耗时间大于一分钟时
                        if(consumeTime>=60000){
                            log.error("网关("+mtGateway.getId()+")时间消耗："+ consumeTime);
                        }
                    }
                });

            }
        } catch (Exception e) {
            log.error("Fire system parse error:" + e.getMessage());
            throw new Exception(e);
        }
    }

    /**
     * 关闭所有网关线程
     */
    public void stop() {
        if (threadPool != null) {
            threadPool.shutdown();
            log.info("threadPool shutdown start");
        }
    }

    public void restart() throws Exception{
        // 暂停所有线程
        stop();
        while(!threadPool.isShutdown()){
            Thread.sleep(1000);
        }
        log.info("threadPool shutdown end,and is restarting!");
        // 重启解析
        initFireSystem();
    }
    /**
     * 事件处理（异步）
     * @param mtResponsePoints
     */
    public void handlerEvent(List<MTResponsePoint> mtResponsePoints) {
        if(mtResponsePoints == null || mtResponsePoints.size() == 0){
            return;
        }
        log.info("事件总数量（包含无效事件） = " + mtResponsePoints.size());
        //
        threadPoolToHandleEvent.execute(() -> {
            for (MTResponsePoint point : mtResponsePoints) {
                FireDevice fireDevice =fireDeviceService.findById(point.getFireDeviceId());

                boolean pointValue = false;
                if("true".equals(point.getValue())){
                    pointValue = true;
                }
                //状态有且是删除/状态无且是新增
                if((fireDevice.contains(point.getCName()) && !pointValue ) || (!fireDevice.contains(point.getCName()) && pointValue)){
                    FireEvent fireEvent = new FireEvent();
                    fireEvent.setEventType(point.getCName());
                    BusinessDevice businessDevice = businessDeviceService.findByNo(fireDevice.getBusinessDeviceNo());
                    fireEvent.setCreateDatetime(System.currentTimeMillis());
                    fireEvent.setDeviceLabel(businessDevice.getDeviceLabel());
                    fireEvent.setDeviceType(businessDevice.getDeviceType().getName());
                    fireEvent.setDeviceId(fireDevice.getBusinessDeviceNo());
                    fireEvent.setZone(fireDevice.getZone());
                    fireEvent.setPoint(fireDevice.getPoint());
                    Location location = locationService.getInfo(businessDevice.getLocationId());
                    fireEvent.setBuilding(location.getBuilding());
                    fireEvent.setFloor(location.getFloor());
                    if(pointValue){
                        fireEvent.setEventStatus(EVENT_STATUS_ADD);
                        fireDevice.addStatus(point.getCName());
                    }else{
                        fireEvent.setEventStatus(EVENT_STATUS_DEL);
                        fireDevice.removeStatus(point.getCName());
                    }
                    fireEvent.setStatus(0);
                    /// 保存事件
                    fireEventService.save(fireEvent);
                    /// 修改设备状态
                    fireDeviceService.save(fireDevice);
                    log.info("fireEvent = " + fireEvent);
                    FireEventDto fireEventDto = fireEventService.toDto(fireEvent);
                    fireEventDto.setCreateDatetime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    String json = JSON.toJSONString(fireEventDto);
                    /// 发送mqtt消息
                    amqpTemplate.send(rabbitmqProps.getFireExchange(),"",new Message(json.getBytes(),new MessageProperties()));
                    log.info("发送mqtt消息成功,eventId="+fireEventDto.getEventId());
                    /// websocket推送消息
//            webSocket.sendToAll(json);
                }
            }
        });
    }

    /**
     * 网络掉线检查
     * @param deviceId
     * @param status
     */
    public void checkGateway(Long deviceId,Boolean status){

        if(gatewayStatus.containsKey(deviceId)){
            // 状态不一致时
            if(!gatewayStatus.get(deviceId).equals(status)){
                gatewayStatus.put(deviceId,status);
                List<MTResponsePoint> mtResponsePoints = new ArrayList<>();
                MTResponsePoint mtResponsePoint = new MTResponsePoint();
                mtResponsePoint.setCName(GATEWAY_PROPS);
                mtResponsePoint.setFireDeviceId(deviceId);
                mtResponsePoint.setValue(status+"");
                mtResponsePoints.add(mtResponsePoint);
                handlerEvent(mtResponsePoints);
            }
        }else{
            // 初始化时
            gatewayStatus.put(deviceId,status);
            // 初始化时网络故障上传  故障正常也需要发送
            if(status.equals(true) || status.equals(false)){
                List<MTResponsePoint> mtResponsePoints = new ArrayList<>();
                MTResponsePoint mtResponsePoint = new MTResponsePoint();
                mtResponsePoint.setCName(GATEWAY_PROPS);
                mtResponsePoint.setFireDeviceId(deviceId);
                mtResponsePoint.setValue(status+"");
                mtResponsePoints.add(mtResponsePoint);
                handlerEvent(mtResponsePoints);
            }
        }
    }
// ---------------------- 协议解析 --------------------------

    /**
     * 根据分区解析出消息
     * @param ptParseList
     * @return
     */
    private List<MTResponsePoint> parseModbusTcp(ModbusMaster modbusMaster,List<PTParse> ptParseList,int pointSleepTime,Long gatewayDeviceId) {
        List<MTResponsePoint> mtResponsePoints = new ArrayList<>();
        try {
            for (PTParse ptParse : ptParseList) {
                List<PTParsePoint> ptParsePoints = ptParse.getList();
                byte[] receiveMessage = null;
                // 读取modbus
                try {
                    receiveMessage = modbusMaster.readModbusTcp(ptParse.getFunctionCode(), (short) ptParse.getAddressStart(), (short) ptParse.getInterval(), ptParse.getSlaveId());
                    checkGateway(gatewayDeviceId,false);
                } catch (Exception e) {
                    log.error("receive message failed,error message:{}"+e.getMessage());
                    // 断开
                    modbusMaster.Close();
                    // 重连
                    modbusMaster.Connect();
                    checkGateway(gatewayDeviceId,true);
                    // 退出
                    throw new Exception(e);
                }

                for (PTParsePoint ptParsePoint : ptParsePoints) {
                    int length = ptParsePoint.getByteAmount();
                    byte[] bytes = new byte[length];
                    System.arraycopy(receiveMessage, ptParsePoint.getByteStart(), bytes, 0, length);
                    String newValue = ModbusUtil.convertToString(ptParsePoint.getDataType(), bytes, ptParsePoint.getBitNum());
                    String oldValue = ptParsePoint.getValue();
                    String type = ptParsePoint.getDataType();
                    if (newValue == null) {
                        log.error("解析错误");
                    } else {
                        ptParsePoint.setValue(newValue);
                        if(oldValue != null && type.equals(ModbusDataType.BIT) && oldValue.equals(newValue)){
                            // 不是第一次 boolean类型 未发生改变
                        }else {
                            // 值有变化  是boolean类型（或者数值型）
                            MTResponsePoint mtResponsePoint = new MTResponsePoint();
                            mtResponsePoint.setCName(ptParsePoint.getCname());
                            mtResponsePoint.setFireDeviceId(ptParsePoint.getFireDeviceId());
                            mtResponsePoint.setValue(ptParsePoint.getValue());
                            mtResponsePoints.add(mtResponsePoint);
                        }
                    }
                }
                try {
                    // 每次小分区 间隔时间
                    Thread.sleep(pointSleepTime);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("parse error:" + e.getMessage());
        }
        return mtResponsePoints;
    }


    /**
     * 解析出分区
     * @param mtPoints
     * @param limit 每个分区最大寄存器数量
     * @return
     */
    private List<PTParse> parseProtocol(List<ModbusPoint> mtPoints,int limit) {
        List<PTParse> list = new ArrayList<>();
        // 解析步骤
        // 1. 按照slaveId和functionCode分区  PartPointKey : 分区条件  PartPoint： 分区结果
        Map<PartPointKey,PartPoint> map = new HashMap<>();
        for (ModbusPoint modbusPoint : mtPoints) {
            PartPointKey partPointKey = new PartPointKey(modbusPoint.getSlaveId(),modbusPoint.getFunctionCode());
            if(map.containsKey(partPointKey)){
                PartPoint partPoint = map.get(partPointKey);
                partPoint.addPoint(modbusPoint);
            }else{
                PartPoint partPoint = new PartPoint(partPointKey);
                partPoint.addPoint(modbusPoint);
                map.put(partPointKey,partPoint);
            }
        }

        // 2 对每个大分区进行分区，分割成每次用来解析的小分区
        for (PartPoint partPoint : map.values()) {// 先排序
            List<ModbusPoint> modbusPointList = partPoint.getList();
            Collections.sort(modbusPointList, Comparator.comparingInt(ModbusPoint::getRealAddress));
            // 设置每个小分区的slaveId 和 functionCode
            PTParse ptParse = new PTParse(partPoint.getPartPointKey().getSlaveId(), partPoint.getPartPointKey().getFunctionCode());

            int addressStart = modbusPointList.get(0).getRealAddress();
            ptParse.setAddressStart(addressStart);
            int addressPre = addressStart;
            int addressNext = addressStart;

            for (ModbusPoint modbusPoint : modbusPointList) {// modbusPoint 转 ptParsePoint

                PTParsePoint ptParsePoint = new PTParsePoint(modbusPoint.getDataType(),modbusPoint.getFireDeviceId(),modbusPoint.getCName());
                ptParsePoint.setBitNum(modbusPoint.getBitNum());
                ptParsePoint.setByteAmount(modbusPoint.getByteCount());

                if (modbusPoint.getRealAddress() == addressPre || modbusPoint.getRealAddress() == addressNext) {
                    // 每次分区(每次读取寄存器的最大数量<=limit)
                    int interval = addressNext - addressStart + modbusPoint.getRegisterCount();
                    if( modbusPoint.getRealAddress() == addressNext && interval > limit){
                        // 新建分区
                        ptParse.setInterval(addressNext - addressStart);
                        list.add(ptParse);
                        ptParse = new PTParse(partPoint.getPartPointKey().getSlaveId(), partPoint.getPartPointKey().getFunctionCode());
                        ptParsePoint.setByteStart(0);
                        addressStart = modbusPoint.getRealAddress();
                        ptParse.setAddressStart(addressStart);
                        ptParse.addPoint(ptParsePoint);
                        addressPre = addressStart;
                        addressNext = addressPre + modbusPoint.getRegisterCount();
                    }else{
                        // address相同时或者相连时
                        ptParsePoint.setByteStart((modbusPoint.getRealAddress() - addressStart) * 2);
                        ptParse.addPoint(ptParsePoint);

                        addressPre = modbusPoint.getRealAddress();
                        addressNext = addressPre + modbusPoint.getRegisterCount();
                    }
                } else if (modbusPoint.getRealAddress() > addressNext) {
                    // 新建分区
                    ptParse.setInterval(addressNext - addressStart);
                    list.add(ptParse);

                    addressStart = modbusPoint.getRealAddress();
                    addressPre = addressStart;
                    addressNext = addressPre + modbusPoint.getRegisterCount();

                    ptParse = new PTParse(partPoint.getPartPointKey().getSlaveId(), partPoint.getPartPointKey().getFunctionCode());
                    ptParsePoint.setByteStart(0);
                    ptParse.setAddressStart(addressStart);
                    ptParse.addPoint(ptParsePoint);
                }
            }
            // 封底最后一个
            ptParse.setInterval(addressNext - addressStart);
            list.add(ptParse);
        }


        log.info("解析完成!");
        return list;
    }

    /**
     * 转换数据库里的Point为我们需要的 解析Point
     * @param mtPoints
     * @return
     */
    private List<ModbusPoint> convert(List<MTPoint> mtPoints) {
        List<ModbusPoint> modbusPoints = new ArrayList<>();
        for (MTPoint mtPoint : mtPoints) {
            ModbusPoint modbusPoint = new ModbusPoint();
            modbusPoint.setFireDeviceId(mtPoint.getFireDevice().getId());
            modbusPoint.setCName(mtPoint.getCName());
            modbusPoint.setBitNum(mtPoint.getKey());
            modbusPoint.setDataType(mtPoint.getDataType());
            modbusPoint.setFunctionCode(mtPoint.getFunctionCode());
            modbusPoint.setRealAddress(mtPoint.getAddress());
            modbusPoint.setSlaveId(mtPoint.getSlaveId());

            switch (modbusPoint.getDataType()){
                case ModbusDataType.BIT:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
                case ModbusDataType.FLOAT_ABCD:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                case ModbusDataType.FLOAT_CDAB:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                case ModbusDataType.INT8H:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
                case ModbusDataType.INT8L:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
                case ModbusDataType.INT16:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
                case ModbusDataType.INT_32_ABCD:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                case ModbusDataType.INT_32_CDAB:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                case ModbusDataType.UINT16:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
                case ModbusDataType.UINT_32_CDAB:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                case ModbusDataType.UINT32_ABCD:
                    modbusPoint.setByteCount(4);
                    modbusPoint.setRegisterCount(2);
                    break;
                default:
                    modbusPoint.setByteCount(2);
                    modbusPoint.setRegisterCount(1);
                    break;
            }

            modbusPoints.add(modbusPoint);
        }
        return modbusPoints;
    }

}
