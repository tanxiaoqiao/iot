### 水系统


#### 相关配置
维传Lora私有云Mqtt服务相关：
```properties
# 维传Lora私有云Mqtt服务地址
water.mqtt.url=tcp://192.168.1.xxx:1883
# Mqtt用户名密码
water.mqtt.username=wangw
water.mqtt.password=ww1030
water.mqtt.subscribe.clientId=water-${random.uuid}
# 设备消息Topic
water.mqtt.subscribe.topics=application/0000000000000001/node/+/rx
```
维传Lora Http Api 调用相关：
```properties
# 任务调度时间间隔，毫秒数
water.lora.api.query.interval=300000
# api地址
water.lora.api.query.address=http://192.168.1.147:8000/api
# 维传Lora私有云平台用户名密码
water.lora.api.query.username=admin
water.lora.api.query.password=admin
# 登陆接口
water.lora.api.query.loginURI=/v1/user/login
# SensorBox透传接口
water.lora.api.query.queryURI=/v1/nodes/{eui}/queue
# Lora网关状态查询接口
water.lora.api.query.gatewayStatusURI=/v1/gateway/{eui}
# 透传指令配置
water.lora.api.query.tasks=WATER_SYS_BATTERY@FE0106000C00018809:220,WATER_SYS_BATTERY@FE0103000100069408:220,WATER_SYS_BATTERY@FEF10300030003E13B:220,WATER_SYS_FLOWMETER@FE44514D0D0A:220
```
关于water.lora.api.query.tasks配置为需要主动轮询的设备,example: WATER_SYS_FLOWMETER@FE4451530D0A:220,WATER_SYS_FOO@FE1234:20  
WATER_SYS_FLOWMETER@FE4451530D0A:220为主动轮询设备类型为WATER_SYS_FLOWMETER
的设备，发送指令为FE4451530D0A，FPort为220，多个用逗号分隔。


#### 消息解析

##### 温湿度传感器
占用5个字节。　第1、2字节表示温度值(16位有符号整型)，高字节在前，实际值需除以100；第3、4字节表示湿度值(16位无符号整型）。高字节在前，实际值需除以100；第5个字节，最高位为0表示电压正常，为1表示电压低，低7位表示电压值，实际值需除以10，如0x21表示3.3V,电压正常，0x9F表示3.1V，电压低。温湿度节点上报的数据为base64编码的数据 CEgYsCQ= ，要全部解码并转换成16进制。转换后的五个字节分别是 08 48 18 b0 24，把前两个字节0848转成10进制是2120，除以100即为温度21.2度，18b0转成10进制是6320，除以100即为湿度63.2%，24为电压转成10进制是3.6V。

| 08 48 | 18 b0 | 24 |
| ----- | ------- | -- |
| 21.2  | 63.2 | 3.6 |

##### 液位传感器
占用3个字节。第1、2字节表示液位高度（16位无符号整型），高字节在前，单位cm；第3个字节，最高位为0表示电压正常，为1表示电压低，低7位表示电压值，实际值需除以10，如0x21表示3.3V，电压正常，0x9F表示3.1V，电压低。

##### 压力传感器
占用3个字节。第1、2字节表示液位高度（16位无符号整型），高字节在前，单位cm；第3个字节，最高位为0表示电压正常，为1表示电压低，低7位表示电压值，实际值需除以10，如0x21表示3.3V，电压正常，0x9F表示3.1V，电压低。  


##### 水浸传感器
占用3个字节。第1个字节（溢水检测），0：未被水浸，1：水浸；第2个字节（无水检测），0：未被水浸，1：水浸；第3个字节，最高位为0表示电压正常，为1表示电压低，低7位表示电压值，实际值需除以10，如0x21表示3.3V，电压正常，0x9F表示3.1V，电压低。

##### 电池检测传感器（SensorBox）
+ TA内阻测试指令：01 06 00 0C F0 F0 0D 8D 返回数据：03 03 01 06 00 0C F0 F0 0D 8D;  
+ TA读取指定： 01 03 00 01 00 06 94 08 返回数据： 30 03 01 03 0C 32 C4 01 07 00 00 00 00 00 00 00 00 B4 31，第6，7字节为单体电压（16位无符号整形），第8，9字节为温度（16位有符号整形，高位为符号），按照以下表格依次类推;  

| 03 03 01 03 0C | 32 C4 | 01 07 | 00 00 | 00 00 | 00 00 | 00 00 | B4 31 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| - | 单体电压 | 电池温度 | 单体内阻 | - | 温度告警 | 内阻告警 |  |

+ TC读取指令：02 03 00 03 00 03 F5 F8， 返回数据： 02 03 06 83 EE 01 00 C2 0D;

| 02 03 06 | 83 EE | 01 00 | C2 0D |
| :--- | :--- | :--- | :--- |
| 充放电电流 | 环境温度1 | 环境温度2 |

##### 流量计（SensorBox）
查询指定（每分钟瞬时流量）： FE44514D0D0A，回复 03 03 2b 30 2e 30 30 30 30 30 30 45 2b 30 30 6d 33 2f 73 0d 0a ，回复内容为ascii码。

| 0303 | 2b | 30 | 2e | 30 | 30 | 30 | 30 | 30 | 30 | 45 | 2b | 30 | 30 | 6d | 33 | 2f | 73 | 0d | 0a |
| ---- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- | -- |
|  | + | 0 | . | 0 | 0 | 0 | 0 | 0 | 0 | e | + | 0 | 0 | m | 3 | / | s | cr | lf |



