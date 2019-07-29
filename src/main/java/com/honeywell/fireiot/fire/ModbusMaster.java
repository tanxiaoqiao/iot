package com.honeywell.fireiot.fire;

import com.honeywell.fireiot.fire.util.ModbusUtil;
import lombok.extern.slf4j.Slf4j;
import net.wimpi.modbus.Modbus;
import net.wimpi.modbus.io.BytesInputStream;
import net.wimpi.modbus.io.BytesOutputStream;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusRequest;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.net.TCPMasterConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * @Author ： YingZhang
 * @Description: ModbusTcp协议解析客户端
 * @Date : Create in 10:32 AM 12/27/2018
 */
@Slf4j
public class ModbusMaster {

    private TCPMasterConnection con; //the connection
    private ModbusTCPTransaction trans; //the transaction


    private InetAddress addr;
    private int port = 502;
    private int repeat = 1; //a loop for repeating the transaction

    public ModbusMaster(String addr, int port) {
        this.port = port;
        try {
            this.addr = InetAddress.getByName(addr);
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        }
    }

    public void Connect() {
        try {
            con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();
            trans = new ModbusTCPTransaction(con);
        } catch (Exception e) {
            log.error("ModbusTcp connect failed,error message:" + e.getMessage());
        }
    }

    public boolean isConnected(){
        if(con == null){
            return false;
        }else{
            return con.isConnected();
        }
    }

    /**
     *
     * @param functionCode 功能码：01 读写状态   02 只读状态     03 读写数值     04只读数值
     * @param addr  寄存器起始位置
     * @param count 寄存器数量
     * @param slaveId
     * @return
     * @throws Exception
     */
    public byte[] readModbusTcp(int functionCode,short addr,short count,int slaveId) throws Exception{
        byte[] ResponseData = null;
        try {
            // 根据不同的功能码返回不同的对象
            ModbusRequest request = ModbusRequest.createModbusRequest(functionCode);
            byte[] b1 = null; byte[] b2= null;
            int responseDataLength = 0;
            int start = 0 ;
            if(functionCode == 1 || functionCode == 2){
                // 放入寄存器地址和读取数量(只能读取0~4095个寄存器，每次最多2000个)
                b1 = ByteBuffer.allocate(2).putShort((short)(addr*16)).array();
                b2 = ByteBuffer.allocate(2).putShort((short)(count*16)).array();
                // 返回1个总数量 + count*2 个byte，固要去掉第一个byte
                responseDataLength = count*2;
                start = 1;
            }else if(functionCode == 3 || functionCode == 4){
                // 放入寄存器地址和读取数量
                b1 = ByteBuffer.allocate(2).putShort(addr).array();
                b2 = ByteBuffer.allocate(2).putShort(count).array();
                // 返回1个总数量 + count*2 个byte，固要去掉第一个byte
                responseDataLength = count * 2;
                start = 1;
            } else {
                return null;
            }
            byte[] data = new byte[b1.length+b2.length];
            System.arraycopy(b1,0,data,0,b1.length);
            System.arraycopy(b2,0,data,b1.length,b2.length);
            request.readData(new BytesInputStream(data));
            // 放入分区ID，设备地址
            request.setUnitID(slaveId);

            trans.setRequest(request);
            trans.execute();
            // 返回数据（数据会多一个数据长度，所以要去掉第一个byte）
            ModbusResponse modbusResponse = trans.getResponse();
            BytesOutputStream dataOutput = new BytesOutputStream(responseDataLength);
            modbusResponse.writeData(dataOutput);
//            System.out.println("size =" +dataOutput.size());
            ResponseData = new byte[responseDataLength];
            System.arraycopy(dataOutput.getBuffer(),start,ResponseData,0,responseDataLength);
//            for(int i=0; i<ResponseData.length; i++)
//            {
//                System.out.print(ResponseData[i]);
//            }
        } catch (Exception e){
            throw new Exception(e);
        }
        return ResponseData;
    }

    /**
     * 寄存器写入 功能码6 写入对应是3
     * @param addr 寄存器地址
     * @param value 值
     * @param slaveId
     * @return 写入是否成功
     */
    public boolean writeRegister(short addr,short value,int slaveId) {
        try {
            // fuctionCode = 6
            ModbusRequest request = ModbusRequest.createModbusRequest(6);
            byte[] b1 = ByteBuffer.allocate(2).putShort(addr).array();
            byte[] b2= ByteBuffer.allocate(2).putShort(value).array();
            byte[] data = new byte[b1.length+b2.length];
            System.arraycopy(b1,0,data,0,b1.length);
            System.arraycopy(b2,0,data,b1.length,b2.length);
            request.readData(new BytesInputStream(data));
            // 放入分区ID，设备地址
            request.setUnitID(slaveId);

            trans.setRequest(request);
            trans.execute();
            // 返回数据（数据会多一个数据长度，所以要去掉第一个byte）
            ModbusResponse modbusResponse = trans.getResponse();
            BytesOutputStream dataOutput = new BytesOutputStream(4);
            modbusResponse.writeData(dataOutput);
            byte[] receiveData = new byte[2];
            System.arraycopy(dataOutput.getBuffer(),2,receiveData,0,2);

            if(value != ModbusUtil.byte2short(receiveData,false)){
                return false;
            }
        } catch (Exception e){
            log.error("send message failed,error message:{}"+e.getMessage());
            return false;
        }
        return true;
    }


    /**
     * 线圈写入 功能码5 写入对应是1
     * @param addr 线圈地址0~65535  对应寄存器取值0~4095
     * @param value true/false
     * @param slaveId
     * @return 写入是否成功
     */
    public boolean writeSingleCoil(short addr,boolean value,int slaveId) {
        try {
            // fuctionCode = 5
            ModbusRequest request = ModbusRequest.createModbusRequest(5);
            byte[] b1 = ByteBuffer.allocate(2).putShort(addr).array();
            byte[] b2 = null;
            if (value ) {
//                    b2[0]=(byte)0xFF;
//                    b2[1]=0x00;
                b2 = Modbus.COIL_ON_BYTES;
            } else {
//                    b2[0]=0x00;
//                    b2[1]=0x00;
                b2 = Modbus.COIL_OFF_BYTES;
            }
            byte[] data = new byte[b1.length+b2.length];
            System.arraycopy(b1,0,data,0,b1.length);
            System.arraycopy(b2,0,data,b1.length,b2.length);
            request.readData(new BytesInputStream(data));
            // 放入分区ID，设备地址
            request.setUnitID(slaveId);

            trans.setRequest(request);
            trans.execute();
            // 返回数据（数据会多一个数据长度，所以要去掉第一个byte）
            ModbusResponse modbusResponse = trans.getResponse();
            BytesOutputStream dataOutput = new BytesOutputStream(4);
            modbusResponse.writeData(dataOutput);
            byte[] receiveData = new byte[1];
            System.arraycopy(dataOutput.getBuffer(),2,receiveData,0,1);

            if(value && receiveData[0] != -1){
                return false;
            }
            if(!value && receiveData[0] != 0){
                return false;
            }
        } catch (Exception e){
            log.error("send message failed,error message:{}"+e.getMessage());
            return false;
        }
        return true;
    }

    public void Close(){
        if(con != null){
            con.close();
        }
    }

    public static void main(String[] args){
        try {
            ModbusMaster modbusMaster = new ModbusMaster("127.0.0.1",502);
            modbusMaster.Connect();
            // 读取
//            byte[] b = modbusMaster.sendMessageForWriteSingleCoil(5,(short)16,false,1);
//            byte[] b = modbusMaster.sendMessage(1,(short)16,(short) 16,1);
//
//            modbusMaster.readSingleCoil1(1,(short)0,(short) 32,1);
            // 返回 4 2 1 1 1

//            modbusMaster.readSingleCoil1(2,(short)16,(short) 32,1);
            // 返回 4 2 1 1 1

//            modbusMaster.readSingleCoil1(3,(short)1,(short) 1,1);
            //返回 2 0 1
//            modbusMaster.sendMessage(3,(short)1,(short) 2,1);
            // 返回0 1 8 1

//            modbusMaster.readSingleCoil1(6,(short)3,(short) 2049,1);
            //返回0 3 8 1    0 3 表示写入的寄存器号



//            modbusMaster.sendMessageForWriteSingleCoil(5,(short)15,true,1);
            // 返回 0 0 0 0  从0 ~



            // 测试03 和 06
//            modbusMaster.sendMessage(6,(short)3,(short) 1,1);
//            modbusMaster.sendMessage(3,(short)1,(short)3,1);

            // 测试 01 和 05
//            modbusMaster.sendMessage(5,(short)1,(short)1,1);

            modbusMaster.Close();

            // 写入
//            modbusMaster.sendMessage(6,(short)0,(short)10,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
