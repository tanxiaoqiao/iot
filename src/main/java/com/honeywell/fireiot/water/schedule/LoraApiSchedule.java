package com.honeywell.fireiot.water.schedule;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.honeywell.fireiot.entity.BusinessDevice;
import com.honeywell.fireiot.entity.DeviceType;
import com.honeywell.fireiot.entity.WaterDevice;
import com.honeywell.fireiot.service.BusinessDeviceService;
import com.honeywell.fireiot.service.DeviceTypeService;
import com.honeywell.fireiot.service.WaterDeviceService;
import com.honeywell.fireiot.water.constant.WaterDeviceStatusEnum;
import com.honeywell.fireiot.water.schedule.bean.LoraApiGatewayStatus;
import com.honeywell.fireiot.water.schedule.bean.LoraApiLoginResult;
import com.honeywell.fireiot.water.schedule.bean.LoraApiResult;
import com.honeywell.fireiot.water.schedule.property.LoraApiQueryProperties;
import com.honeywell.fireiot.water.sensor.constant.WaterSensorEnum;
import com.honeywell.fireiot.water.service.WaterDeviceStatusService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**executorInit
 * @author: xiaomingCao
 * @date: 2018/12/28
 */
@Component
@EnableConfigurationProperties(LoraApiQueryProperties.class)
@Configuration
@Slf4j
@EnableScheduling
public class LoraApiSchedule {

    private RestTemplate rest = new RestTemplate();

    private volatile String jwt;

    @Autowired
    private BusinessDeviceService businessDeviceService;

    @Autowired
    private DeviceTypeService deviceTypeService;

    @Autowired
    private WaterDeviceService waterDeviceService;

    @Autowired
    private LoraApiQueryProperties queryProperties;

    @Autowired
    private WaterDeviceStatusService waterDeviceStatusService;

    @Value("${water.open}")
    private Boolean waterOpen;

    private static final Long WATER_SYSTEM_TYPE = 2L;

    private static final String API_OK = "200";

    private ExecutorService executor;

    private static ThreadFactory namesThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("lora-water-device-pool-%d").build();

    private void executorInit(){


        executor = new ThreadPoolExecutor(
                queryProperties.getProcessor(),
                queryProperties.getExecuteThread(),
                120L,
                TimeUnit.MICROSECONDS,
                new ArrayBlockingQueue<>(1000)
                , namesThreadFactory);
    }


    /**
     * 网关状态查询任务
     */
    @Scheduled(fixedDelayString = "${water.lora.api.query.interval}")
    public void gatewayTask(){
        // 水系统开启/关闭
        if(!waterOpen){
            return ;
        }
        login();
        Optional<DeviceType> op = deviceTypeService
                .findByNameAndSystemTypeId(WaterSensorEnum.WATER_SYS_GATEWAY.name(), WATER_SYSTEM_TYPE);
        if(!op.isPresent()){
            return;
        }
        businessDeviceService.findByDeviceType(op.get())
                .stream()
                .map(BusinessDevice::getDeviceNo)
                .map(waterDeviceService::findByDeviceNo)
                .map(WaterDevice::getDeviceEUI)
                .forEach(this::queryGatewayStatus);
    }


    /**
     * sensor box 设备查询任务
     */
    @Scheduled(fixedDelayString = "${water.lora.api.query.interval}")
    public void sensorBoxTask(){
        // 水系统开启/关闭
        if(!waterOpen){
            return ;
        }

        if(Objects.isNull(executor)){
            executorInit();
        }
        login();
        parseTask()
                .stream()
                .forEach(t ->
                        getEuiList(t.deviceType)
                                .forEach(eui ->
                                        executor.execute(() -> {
                                            Stream.of(t.data)
                                                    .forEach(cmdTxt -> {
                                                        callCmdApi(eui, cmdTxt, t.getFport());
                                                        try {
                                                            Thread.sleep(queryProperties.getExecuteDelay());
                                                        } catch (InterruptedException e) {
                                                            log.error(e.getMessage(), e);
                                                        }
                                                    });

                                        })
                                )
                );
    }

    @Data
    private static class Task{
        private String deviceType;
        private String[] data;
        private int fport = 220;

    }

    private List<Task> parseTask(){
        if(StringUtils.isEmpty(queryProperties.getTasks())){
            return Collections.emptyList();
        }

        return Arrays.stream(queryProperties.getTasks().split(","))
                .map(s -> s.split("@"))
                .map(strs -> {
                    Task task = new Task();
                    task.setDeviceType(strs[0]);
                    if(strs.length == 1){
                        return task;
                    }
                    String[] arr = strs[1].split(":");
                    task.setData(arr[0].split("_"));
                    if(arr.length == 1){
                        return task;
                    }
                    task.setFport(Integer.parseInt(arr[1]));
                    return task;
                })
                .collect(toList());
    }


    private List<String> getEuiList(String deviceType){
        DeviceType type = deviceTypeService.findByNameAndSystemTypeId(deviceType, WATER_SYSTEM_TYPE)
                .orElse(null);
        if(Objects.isNull(type)){
            return Collections.emptyList();
        }
        List<BusinessDevice> bs = businessDeviceService.findByDeviceType(type);
        List<String> noList = bs.stream()
                .map(BusinessDevice::getDeviceNo)
                .collect(toList());
        return waterDeviceService.findByDeviceNoList(noList)
                .stream()
                .map(WaterDevice::getDeviceEUI)
                .collect(toList());
    }


    /**
     * 查询gateway状态
     *
     * @param eui
     * @return
     */
    private void queryGatewayStatus(String eui){
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(headers);
        ResponseEntity<LoraApiGatewayStatus> res = rest.exchange(
                queryProperties.getAddress() + queryProperties.getGatewayStatusURI(),
                HttpMethod.GET,
                httpEntity,
                LoraApiGatewayStatus.class,
                eui
        );

        if(!HttpStatus.OK.equals(res.getStatusCode())){
            log.error(queryProperties.getQueryURI() + "\t" + res.getStatusCodeValue());
            waterDeviceStatusService.updateStatus(eui, WaterDeviceStatusEnum.OFFLINE, System.currentTimeMillis());
        }

        if(Objects.isNull(res.getBody())){
            log.error(queryProperties.getQueryURI() + "\t" + "empty response body");
            waterDeviceStatusService.updateStatus(eui, WaterDeviceStatusEnum.OFFLINE, System.currentTimeMillis());
        }

        LoraApiGatewayStatus status = res.getBody();
        long timestamp = Long.parseLong(res.getBody().getTimestamp());
        long current = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        // todo: 排查网关离线问题，临时日志
        log.info("lora gateway status response: {}", JSON.toJSONString(status));
        log.info("lora gateway status response timestamp: {}, current: {}", timestamp, current);

        if(!Objects.equals(API_OK, status.getCode())){
            log.error(queryProperties.getQueryURI() + "\t" + status.getCode());
            waterDeviceStatusService.updateStatus(eui, WaterDeviceStatusEnum.OFFLINE, timestamp*1000);
        }



        if(timestamp > current){
            log.error("server time incorrect!!!!!! lora or fire iot");
        }

        long tenMinutesSeconds = 10 * 60;

        if( (current - timestamp) >= tenMinutesSeconds){
            waterDeviceStatusService.updateStatus(eui, WaterDeviceStatusEnum.OFFLINE, timestamp*1000);
        }else{
            waterDeviceStatusService.updateStatus(eui, WaterDeviceStatusEnum.ONLINE, timestamp*1000);
        }
    }


    /**
     * 调用透传API
     *
     * @param eui
     * @param data
     * @param fport
     */
    private void callCmdApi(String eui, String data, int fport){
        log.info("send cmd: eui={}, data={}", eui, data);
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "bearer " + jwt);
        Map<String,Object> requestBody = new HashMap<>(16);
        requestBody.put("confirmed", false);
        requestBody.put("fPort", fport);
        requestBody.put("deveui", eui);
        byte[] bytes = Hex.decode(data);
        byte[] encode = Base64.getEncoder().encode(bytes);
        requestBody.put("data", new String(encode));
        HttpEntity httpEntity = new HttpEntity(requestBody, headers);
        log.info("lora api response: {}", JSON.toJSONString(httpEntity.getBody()));
        ResponseEntity<LoraApiResult> res = rest.exchange(
                queryProperties.getAddress() + queryProperties.getQueryURI(),
                HttpMethod.POST,
                httpEntity,
                LoraApiResult.class,
                eui
        );

        if(!HttpStatus.OK.equals(res.getStatusCode())){
            log.error(queryProperties.getQueryURI() + "\t" + res.getStatusCodeValue());
        }

    }


    /**
     * 获取Lora api鉴权信息
     */
    private void login(){
        log.info("login lora api");
        Map<String, Object> requestBody = new HashMap<>(16);
        requestBody.put("username", queryProperties.getUsername());
        requestBody.put("password", queryProperties.getPassword());
        String url = queryProperties.getAddress() + queryProperties.getLoginURI();
        ResponseEntity<LoraApiLoginResult> res = rest
                .postForEntity(url, requestBody, LoraApiLoginResult.class);
        log.info("login response: {}", JSON.toJSONString(res.getBody()));

        if(!HttpStatus.OK.equals(res.getStatusCode())){
            log.error(res.getStatusCode().getReasonPhrase());
            return;
        }

        if(Objects.isNull(res.getBody())){
            log.error("lora api login failed, status ok response is missing");
            return;
        }

        if(Objects.isNull(res.getBody().getJwt())){
            log.error("lora api login failed, status ok jwt is missing");
            return;
        }

        this.jwt = res.getBody().getJwt();
    }



}
